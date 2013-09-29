package pl.pzagawa.cityalarm.settings;

import android.content.SharedPreferences;

public class SettingsValueSpeechNotify
	extends SettingsValue
{
	public SettingsValueSpeechNotify(SettingsManager settingsManager)
	{
		super(settingsManager);
	}

	public synchronized void set(boolean enabled)
	{
		try
		{
			SharedPreferences.Editor editor = getPrefs().edit();

			editor.putBoolean(VALUE_SPEECH_NOTIFY, enabled);
			
			editor.commit();
		}
		catch (Exception e)
		{
			//eat this
		}		
	}

	public synchronized boolean isEnabled()
	{
		return getPrefs().getBoolean(VALUE_SPEECH_NOTIFY, false);
	}
	
}
