package pl.pzagawa.cityalarm;

import java.util.Calendar;
import java.util.Observable;

import pl.pzagawa.cityalarm.alarm.LocationNotification;
import pl.pzagawa.cityalarm.alarm.NotificationItem;
import pl.pzagawa.cityalarm.data.DataModel;
import pl.pzagawa.cityalarm.settings.SettingsManager;
import pl.pzagawa.cityalarm.ui.ToastManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class AppEventManager
	extends Observable
{
	private final Context context;
	private final Handler dispatchEventHandler = new Handler();
	
	public AppEventManager(Context context)	
	{
		this.context = context;
	}
		
	private PendingIntent createScannerPendingIntent()
	{
        final Intent scannerIntent = new Intent(context, ScannerService.class);

        PendingIntent scannerPendingIntent = PendingIntent.getService(context, 0, scannerIntent, 0);
		
        return scannerPendingIntent;
	}

	private long getNowTimeInMillis()
	{
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        
        return cal.getTimeInMillis();		
	}
	
	private long getRepeatIntervalInMilis()
	{
        return AppConfig.SCAN_INTERVAL_MILISECONDS;
	}
		
	private void enableLocationScanning(DataModel dm, SettingsManager settings)
	{
		try
		{
			dm.cleanupTables();
		}
		catch (Exception e)
		{
			//cancel processing in case of db error
			return;
		}

		settings.valueScanning.resetStartLocation();
		settings.valueScanning.resetStopLocation();		
        
		final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final PendingIntent scannerIntent = createScannerPendingIntent();
        
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getNowTimeInMillis(), getRepeatIntervalInMilis(), scannerIntent);
        
        settings.valueScanning.setEnabled(true);

        ToastManager.show(context.getString(R.string.toast_scanning_title), context.getString(R.string.toast_scanning_enabled), ToastManager.Theme.DARK_GREEN);
	}

	private void disableLocationScanning(DataModel dm, SettingsManager settings)
	{
		final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final PendingIntent scannerIntent = createScannerPendingIntent();
        
		alarmManager.cancel(scannerIntent);

        settings.valueScanning.setEnabled(false);

		LocationNotification.cancelAll();
        
		ToastManager.show(context.getString(R.string.toast_scanning_title), context.getString(R.string.toast_scanning_disabled), ToastManager.Theme.DARK_BLUE);
	}
	
	private void disableFutureNotifications(DataModel dm, long locationId)
	{
		for (int index = 0; index < LocationNotification.MAX_NOTIFICATIONS; index++)
		{
			final NotificationItem notificationItem = new NotificationItem(locationId);
	
			dm.dataNotifications.insertItem(notificationItem);
		}
	}
	
	public void callEvent(AppEvent event)
	{
		final DataModel dm = new DataModel();
		final SettingsManager settings = CityAlarmApplication.getSettingsManager();

		if (event.equals(AppEvent.ENABLE_LOCATION_SCANNING))
		{	
			enableLocationScanning(dm, settings);
		}

		if (event.equals(AppEvent.DISABLE_LOCATION_SCANNING))
		{
			disableLocationScanning(dm, settings);
		}
		
		if (event.equals(AppEvent.CLEAR_NOTIFICATION))
		{
			final long locationId = (Long) event.getData();

			disableFutureNotifications(dm, locationId);
		}
		
		if (event.equals(AppEvent.IMPORT_GEOCODEDB_STOP))
		{
			if (settings.valueGeoData.isReady())
			{
				ToastManager.show(context.getString(R.string.toast_geodb_update_title), context.getString(R.string.toast_geodb_update_content), ToastManager.Theme.ORANGE);
			}
		}
		
		dispatchEvent(event);
	}

	private void dispatchEvent(final AppEvent event)
	{
		dispatchEventHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					AppEventManager.this.setChanged();
					AppEventManager.this.notifyObservers(event);
				}
				catch (Exception e)
				{
					//eat this					
				}
			}
		});		
	}
	
}
