package pl.pzagawa.cityalarm.location.scanner;

import java.util.ArrayList;
import java.util.List;

import pl.pzagawa.cityalarm.CityAlarmApplication;
import pl.pzagawa.cityalarm.location.ScannedItem;
import pl.pzagawa.cityalarm.settings.SettingsManager;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

public abstract class LocationScanner
{
	private static final int RETRY_COUNT = 1;
	
	private final LocationManager locationManager;
	private final Handler handler = new Handler();		
	private final LocationScannerProviders providers;
	
	private int retryCounter = 0;
	private List<ScannedItem> scannedItems = new ArrayList<ScannedItem>();
	
	private boolean waitLong = false;
	
	public LocationScanner(Context context)
	{
    	this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    	
		final SettingsManager settings = CityAlarmApplication.getSettingsManager();
    	
    	this.providers = new LocationScannerProviders(locationManager, settings.valuePreferGps.getProvidersOrder());
	}

	public boolean isAnyProviderEnabled()
	{
		return providers.isAnyEnabled();
	}
	
	private LocationListener locationListener = new LocationListener()
	{
		@Override
		public void onLocationChanged(Location location)
		{
			scannedItems.add(new ScannedItem(location.getLatitude(), location.getLongitude(), location.getAccuracy()));
			
	    	handler.post(runStopReading);
		}
		
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}
		
		public void onProviderEnabled(String provider)
		{    	    	
		}
		
		public void onProviderDisabled(String provider)
		{
		}
	    
	};

	private Runnable runStartReading = new Runnable()
	{
		@Override
		public void run()
		{
			read();
		}		
	};
	
	private Runnable runStopReading = new Runnable()
	{
		@Override
		public void run()
		{
			stop();
		}		
	};
	
    public void start(boolean waitLong)
    {
    	this.waitLong = waitLong;
    	
    	this.retryCounter = 0;
    	
    	handler.post(runStartReading);
    }
    
    private void read()
    {
    	retryCounter++;
    	
    	scannedItems.clear();
    	
    	locationManager.requestLocationUpdates(providers.getCurrent(), 0, 0, locationListener);
    	
    	handler.postDelayed(runStopReading, providers.getCurrentTimeout(waitLong));
    	
    	begin();
    }
    
    private ScannedItem getTopItem()
    {
    	final int count = scannedItems.size();
    	
    	if (count > 0)
    		return scannedItems.get(count - 1);

    	return null;
    }
    
    public void stop()
    {
    	handler.removeCallbacks(runStartReading);
    	handler.removeCallbacks(runStopReading);
    	
    	locationManager.removeUpdates(locationListener);
    	
    	if (scannedItems.isEmpty())
    	{
    		if (retryCounter < RETRY_COUNT)
    		{
        		handler.post(runStartReading);
    		}
    		else
    		{
    			if (providers.isThereAnyLeft())
    			{
    				providers.setNext();

    				handler.post(runStartReading);
    			}
    			else
    			{
            		finish(null);    				
    			}
    		}
    	}
    	else
    	{
    		finish(getTopItem());    		
    	}
    }
    
    public abstract void begin();
    public abstract void finish(ScannedItem scannedItem);

}
