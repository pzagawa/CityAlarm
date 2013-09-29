package pl.pzagawa.cityalarm.settings;

import android.content.SharedPreferences;

public abstract class SettingsValue
{
	protected static final String VALUE_SCANNING_ENABLED = "scanningEnabled";
	protected static final String VALUE_SCANNING_START_TIME = "scanningStartTime";
	protected static final String VALUE_SCANNING_UID = "scanningUID";
	protected static final String VALUE_SCANNING_START_LOCATION_ID = "scanningStartLocationId";
	protected static final String VALUE_SCANNING_STOP_LOCATION_ID = "scanningStopLocationId";
	
	protected static final String VALUE_GEOCODE_DATA_READY = "geocodeDataReady";
	
	protected static final String VALUE_ALARM_TRIGGER_DISTANCE = "alarmTriggerDistance";

	protected static final String VALUE_ADD_LOCATIONS_TO_LOG = "addLocationsToLog";

	protected static final String VALUE_PREFER_GPS = "preferGps";

	protected static final String VALUE_SPEECH_NOTIFY = "speechNotify";
	
	protected static final String VALUE_LICENSE_CHECK_RESULT = "lcheckResult";
	protected static final String VALUE_LICENSE_CHECK_COUNTER = "lcheckCounter";
	
	protected static final String VALUE_HINT_ITEM = "hintItem";	
	
	private final SettingsManager settingsManager;

	public SettingsValue(SettingsManager settingsManager)
	{
		this.settingsManager = settingsManager;
	}
		
	protected SharedPreferences getPrefs()
	{
		return settingsManager.getPrefs();
	}
	
}
