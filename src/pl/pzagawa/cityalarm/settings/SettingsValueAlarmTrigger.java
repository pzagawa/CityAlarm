package pl.pzagawa.cityalarm.settings;

import android.content.SharedPreferences;

public class SettingsValueAlarmTrigger
	extends SettingsValue
{
	private static final int DEFAULT_ALARM_TRIGGER_DISTANCE_KM = 10;
	
	public SettingsValueAlarmTrigger(SettingsManager settingsManager)
	{
		super(settingsManager);
	}

	public synchronized void setDistanceKM(int value)
	{
		try
		{
			SharedPreferences.Editor editor = getPrefs().edit();

			editor.putInt(VALUE_ALARM_TRIGGER_DISTANCE, value);
			
			editor.commit();
		}
		catch (Exception e)
		{
			//eat this
		}		
	}

	public synchronized int getDistanceKM()
	{
		return getPrefs().getInt(VALUE_ALARM_TRIGGER_DISTANCE, DEFAULT_ALARM_TRIGGER_DISTANCE_KM);
	}
	
}
