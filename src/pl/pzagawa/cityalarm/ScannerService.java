package pl.pzagawa.cityalarm;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import pl.pzagawa.cityalarm.alarm.LocationNotification;
import pl.pzagawa.cityalarm.alarm.LocationTrigger;
import pl.pzagawa.cityalarm.alarm.SpeechTextBuilder;
import pl.pzagawa.cityalarm.data.DataModel;
import pl.pzagawa.cityalarm.directions.DirectionsProcessor;
import pl.pzagawa.cityalarm.location.LocationGeocoder;
import pl.pzagawa.cityalarm.location.LocationItem;
import pl.pzagawa.cityalarm.location.ScannedItem;
import pl.pzagawa.cityalarm.location.scanner.LocationBlockingScanner;
import pl.pzagawa.cityalarm.settings.SettingsManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;

public class ScannerService
	extends IntentService
{
	private AtomicBoolean isInProgress = new AtomicBoolean(false);
	
	private LocationBlockingScanner locationBlockingScanner = null;	
			
	public ScannerService()
	{
		super("LocationScannerService");
		
		setIntentRedelivery(true);
	}

	private void sendEvent(AppEvent appEvent)
	{
		final Intent intent = appEvent.toIntent(this);
		sendBroadcast(intent);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if (isInProgress.get())
		{
			return super.onStartCommand(intent, flags, startId);
		}
		
		//reinitialize
		this.locationBlockingScanner = new LocationBlockingScanner(this);
						
		sendEvent(AppEvent.LOCATION_SCANNER_START);
		
	    return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy()
	{
		sendEvent(AppEvent.LOCATION_SCANNER_STOP);
		
		super.onDestroy();
	}
	
	private void saveFirstGeocodedLocation(DataModel dm, SettingsManager settings, String scanningUID)
	{
		if (settings.valueScanning.isStartLocationSet())
			return;
		
		final LocationItem location = dm.dataGeocodedLocations.getLastGeocodedLocation(scanningUID);
		
		if (location != null)
		{
			settings.valueScanning.setStartLocationId(location.getId());
		}
	}
	
	private void processTriggeredAlarms(DataModel dm, SettingsManager settings, List<LocationItem> triggerLocations)
	{
		final LocationNotification locationNotification = new LocationNotification();
		
		final long startLocationId = settings.valueScanning.getStartLocationId();
		
		final SpeechTextBuilder speechText = new SpeechTextBuilder();
		
		for (LocationItem triggerLocation : triggerLocations)
		{
			LocationTrigger.logAlarm(dm, triggerLocation);
			
			//process notification					
			if (startLocationId != triggerLocation.getId())
			{						
				if (locationNotification.isMaxNotificationsCount(dm, triggerLocation) == false)
				{
					//log notification
					locationNotification.log(dm, triggerLocation);
					
					locationNotification.notify(triggerLocation);
				
					speechText.add(triggerLocation.getName());
				}
			}
		}

		if (speechText.isText())
		{
			speakNotification(speechText.getText());
		}
	}
	
	private void speakNotification(String text)
	{
		if (CityAlarmApplication.getSettingsManager().valueSpeechNotify.isEnabled())
		{
			final String notifyText = getString(R.string.notification_alarm_text) + " " +  text;			

			final long delayMillis = CityAlarmApplication.getAppResources().getSoundAlarmDurationMillis();
			
			//ignores InterruptedException
			SystemClock.sleep(delayMillis);

			CityAlarmApplication.getTTS().say(notifyText);
		}		
	}	
		
	private void disableScanningWhenAllVisited(DataModel dm, SettingsManager settings, String scanningUID)
	{
		if (settings.valueScanning.isStopLocationSet())
		{
			if (settings.valueScanning.getStopLocationId() != settings.valueScanning.getStartLocationId())
			{					
				if (dm.dataGeocodedLocations.isAllAlarmEnabledVisited(scanningUID))
				{
					sendEvent(AppEvent.DISABLE_LOCATION_SCANNING);
				}
			}
		}		
	}
	
	@Override
	protected void onHandleIntent(Intent intent)
	{		
		final SettingsManager settings = CityAlarmApplication.getSettingsManager();
		
		if (!settings.valueScanning.isEnabled())
			return;
		
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				
		final String scanningUID = settings.valueScanning.getUID();
		
		final PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationQueryService WakeLock");
		
		if (settings.valueSpeechNotify.isEnabled())
		{
			CityAlarmApplication.getTTS().initialize();
		}
		
		try
		{
			wakeLock.acquire();
			
			isInProgress.set(true);
						
			final boolean waitLong = (settings.valueScanning.isStartLocationSet() == false);
			
			//get the current location coordinates
			locationBlockingScanner.startAndWaitForResult(waitLong);

			final ScannedItem scannedLocation = locationBlockingScanner.getLocation();			

			if (scannedLocation != null)
			{
				final DataModel dm = new DataModel();

				//log scanned location
				dm.dataScannedLocations.insertItem(scannedLocation);

				AppEvent eventNewLocation = AppEvent.NEW_SCANNED_LOCATION;

				//geocode scanned location to address
				final LocationItem geocodedLocation = LocationGeocoder.coordsToAddress(dm, scannedLocation);

				dm.dataGeocodedLocations.resetVisitedLocationId();
				
				if (geocodedLocation != null)
				{
					dm.dataGeocodedLocations.logLocation(settings, scanningUID, geocodedLocation);

					saveFirstGeocodedLocation(dm, settings, scanningUID);

					eventNewLocation = AppEvent.NEW_VISITED_LOCATION;
				}

				//get lastly visited location id
				final long visitedLocationId = dm.dataGeocodedLocations.getVisitedLocationId();
				
				//analyze directions and save stop location
				DirectionsProcessor.process(dm, settings, scannedLocation);
				
				//send new location event
				sendEvent(eventNewLocation);
												
				//get trigger locations								
				final List<LocationItem> triggerLocations = LocationTrigger.getLocations(dm, settings, scannedLocation);

				processTriggeredAlarms(dm, settings, triggerLocations);
				
				//send trigger location event
				if (triggerLocations.size() > 0)
				{
					sendEvent(AppEvent.TRIGGER_LOCATION);										
				}
				
				//remove notification for visited location
				if (visitedLocationId != 0)
				{
					LocationNotification.cancel(visitedLocationId);
				}

				disableScanningWhenAllVisited(dm, settings, scanningUID);				
			}
		}
		finally
		{
			wakeLock.release();
			
			isInProgress.set(false);
		}
	}
	
}
