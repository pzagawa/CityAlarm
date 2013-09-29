package pl.pzagawa.cityalarm.settings;

import android.content.SharedPreferences;

public class SettingsValueGeoData
	extends SettingsValue
{
	public SettingsValueGeoData(SettingsManager settingsManager)
	{
		super(settingsManager);
	}

	public synchronized void setReady()
	{
		try
		{
			SharedPreferences.Editor editor = getPrefs().edit();

			editor.putBoolean(VALUE_GEOCODE_DATA_READY, true);
			
			editor.commit();
		}
		catch (Exception e)
		{
			//eat this
		}		
	}

	public synchronized boolean isReady()
	{
		return getPrefs().getBoolean(VALUE_GEOCODE_DATA_READY, false);
	}
	
}
