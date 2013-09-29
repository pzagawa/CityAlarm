package pl.pzagawa.cityalarm.settings;

import android.content.SharedPreferences;

public class SettingsValueAddLocations
	extends SettingsValue
{
	public SettingsValueAddLocations(SettingsManager settingsManager)
	{
		super(settingsManager);
	}

	public synchronized void set(boolean enabled)
	{
		try
		{
			SharedPreferences.Editor editor = getPrefs().edit();

			editor.putBoolean(VALUE_ADD_LOCATIONS_TO_LOG, enabled);
			
			editor.commit();
		}
		catch (Exception e)
		{
			//eat this
		}		
	}

	public synchronized boolean isEnabled()
	{
		return getPrefs().getBoolean(VALUE_ADD_LOCATIONS_TO_LOG, true);
	}
	
}
