package pl.pzagawa.cityalarm.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager
{
	private static final String SETTINGS_NAME = "CityAlarmSettings";
	
	private final SharedPreferences prefs;

	public final SettingsValueScanning valueScanning;
	public final SettingsValueGeoData valueGeoData;
	public final SettingsValueAlarmTrigger valueAlarmTrigger;
	public final SettingsValueAddLocations valueAddLocations;
	public final SettingsValuePreferGps valuePreferGps;
	public final SettingsValueSpeechNotify valueSpeechNotify;
	public final SettingsValueHints valueHints;

	public SettingsManager(Context context)
	{
		this.prefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
		
		this.valueScanning = new SettingsValueScanning(this);
		this.valueGeoData = new SettingsValueGeoData(this);
		this.valueAlarmTrigger = new SettingsValueAlarmTrigger(this);
		this.valueAddLocations = new SettingsValueAddLocations(this);
		this.valuePreferGps = new SettingsValuePreferGps(this);
		this.valueSpeechNotify = new SettingsValueSpeechNotify(this);
		this.valueHints = new SettingsValueHints(this);
	}

	public SharedPreferences getPrefs()
	{
		return prefs;
	}
	
	public boolean isAddingLocationEnabled()
	{
		if (valueAddLocations.isEnabled())
		{
			return true;			
		}
		else
		{
			if (valueScanning.isStartLocationSet())
			{
				//starting location set, can't add any more locations
				return false;
			}
			else
			{
				//starting location must be added right at scanning start 
				return true;
			}
		}
	}	

}
