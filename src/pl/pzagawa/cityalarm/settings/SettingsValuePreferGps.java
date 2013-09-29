package pl.pzagawa.cityalarm.settings;

import pl.pzagawa.cityalarm.location.scanner.LocationScannerProviders;
import android.content.SharedPreferences;

public class SettingsValuePreferGps
	extends SettingsValue
{
	public SettingsValuePreferGps(SettingsManager settingsManager)
	{
		super(settingsManager);
	}

	public synchronized void set(boolean enabled)
	{
		try
		{
			SharedPreferences.Editor editor = getPrefs().edit();

			editor.putBoolean(VALUE_PREFER_GPS, enabled);
			
			editor.commit();
		}
		catch (Exception e)
		{
			//eat this
		}		
	}

	public synchronized boolean isEnabled()
	{
		return getPrefs().getBoolean(VALUE_PREFER_GPS, false);
	}

	public synchronized LocationScannerProviders.Order getProvidersOrder()
	{
		if (getPrefs().getBoolean(VALUE_PREFER_GPS, false))
		{
			return LocationScannerProviders.Order.GPS_THEN_NETWORK;
		}
		else
		{
			return LocationScannerProviders.Order.NETWORK_THEN_GPS;			
		}		
	}
	
}
