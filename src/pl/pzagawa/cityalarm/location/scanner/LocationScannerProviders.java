package pl.pzagawa.cityalarm.location.scanner;

import java.util.ArrayList;
import java.util.List;

import pl.pzagawa.cityalarm.AppConfig;

import android.location.LocationManager;

public class LocationScannerProviders
{
	public enum Order
	{
		NETWORK_THEN_GPS,
		GPS_THEN_NETWORK,
	};
	
	private final LocationManager locationManager;
	private final Order order;
	
	private final List<String> providers = new ArrayList<String>(); 

	private String currentProvider = "";
	
	public LocationScannerProviders(LocationManager locationManager, Order order)
	{
    	this.locationManager = locationManager;
    	this.order = order;

    	collectAvailable();
    	
    	setNext();
	}
	
	public String getCurrent()
	{
		return currentProvider;
	}

	public long getCurrentTimeout(boolean waitLong)
	{
		if (isCurrentGps())
		{
			return AppConfig.getTimeoutGps(waitLong);
		}
		else
		{
			return AppConfig.getTimeoutNet(waitLong);
		}
	}
	
	private void addProvider(String providerName)
	{
    	if (locationManager.getProvider(providerName) != null)
    	{
        	if (locationManager.isProviderEnabled(providerName))
        	{
        		providers.add(providerName);
        	}
    	}		
	}
	
	private void collectAvailable()
	{
		if (order.equals(Order.NETWORK_THEN_GPS))
		{
			addProvider(LocationManager.NETWORK_PROVIDER);
			addProvider(LocationManager.GPS_PROVIDER);
			return;
		}

		if (order.equals(Order.GPS_THEN_NETWORK))
		{
			addProvider(LocationManager.GPS_PROVIDER);			
			addProvider(LocationManager.NETWORK_PROVIDER);
			return;
		}
	}
	
	public void setNext()
	{
    	currentProvider = providers.remove(0);
	}
	
	public boolean isCurrentGps()
	{
		return (currentProvider.equalsIgnoreCase(LocationManager.GPS_PROVIDER));
	}
	
	public boolean isThereAnyLeft()
	{
		return (providers.size() > 0);
	}
	
	public boolean isAnyEnabled()
	{
    	return (isNetworkProviderEnabled() || isGpsProviderEnabled());
	}
	
	private boolean isNetworkProviderEnabled()
	{
    	return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	private boolean isGpsProviderEnabled()
	{
    	return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
}
