package pl.pzagawa.cityalarm.settings;

import android.content.SharedPreferences;

public class SettingsValueHints
	extends SettingsValue
{	
	public SettingsValueHints(SettingsManager settingsManager)
	{
		super(settingsManager);
	}

	private String getHintItemKey(int hintId)
	{
		return VALUE_HINT_ITEM + "_" + Integer.toString(hintId);
	}
	
	public synchronized void disableHintItemById(int hintId)
	{
		try
		{
			SharedPreferences.Editor editor = getPrefs().edit();

			editor.putBoolean(getHintItemKey(hintId), true);

			editor.commit();
		}
		catch (Exception e)
		{
			//eat this
		}		
	}

	public synchronized boolean isHintItemDisabled(int hintId)
	{
		return getPrefs().getBoolean(getHintItemKey(hintId), false);
	}
	
}
