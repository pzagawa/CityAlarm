package pl.pzagawa.cityalarm;

import pl.pzagawa.cityalarm.settings.SettingsManager;
import pl.pzagawa.cityalarm.ui.CommonActivity;
import pl.pzagawa.cityalarm.ui.hints.HintResource;
import pl.pzagawa.cityalarm.alarm.TextToSpeechManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity
	extends PreferenceActivity
	implements OnSharedPreferenceChangeListener, TextToSpeechManager.OnStatusListener
{
	private SettingsManager settings = null;
	
	private ListPreference pref_triggerAlarmDistance = null;
	private CheckBoxPreference pref_addLocations = null;
	private CheckBoxPreference pref_preferGps = null;
	private CheckBoxPreference pref_speechNotify = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);

		this.settings = CityAlarmApplication.getSettingsManager();
				
		this.pref_triggerAlarmDistance = (ListPreference) this.findPreference("key_TriggerAlarmDistance");
		this.pref_addLocations = (CheckBoxPreference) this.findPreference("key_AddLocations");
		this.pref_preferGps = (CheckBoxPreference) this.findPreference("key_PreferGps");
		this.pref_speechNotify = (CheckBoxPreference) this.findPreference("key_SpeechNotify");
		
		//set prefs ui events
		this.pref_speechNotify.setOnPreferenceChangeListener(onPrefSpeechNotifyChangeListener);

		updatePreferenceValues();
		
		updatePreferenceScreen();
	}
	
	@Override
	protected void onResume()
	{
		initializeSpeechEngine();

		super.onResume();
	}

	@Override
	protected void onStart()
	{
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
		
		super.onStart();
	}

	@Override
	protected void onStop()
	{
		storePreferences();

		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
		
		if (pref_preferGps.isChecked() && (isGpsEnabled() == false))
		{
			showHintMessage(R.array.hint_gps_disabled);
		}
		
		super.onStop();
	}

	private boolean showHintMessage(int hintResourceId)
	{
		return showHintMessage(hintResourceId, true);
	}
	
	private boolean showHintMessage(int hintResourceId, boolean allowDisable)
	{
		final Intent intent = HintResource.toIntent(this, hintResourceId, allowDisable);
		
		if (intent == null)
			return false;
		
		hideHintMessage();
		
		this.startActivityForResult(intent, CommonActivity.REQUEST_CODE_HINT);
		
		return true;
	}
	
	private void hideHintMessage()
	{		
		this.finishActivity(CommonActivity.REQUEST_CODE_HINT);
	}
	
	private boolean isGpsEnabled()
	{
    	final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	
    	if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null)
    	{
        	if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        	{
        		return true;        		
        	}
    	}

    	return false;
	}
	
	private void updatePreferenceValues()
	{
		this.pref_triggerAlarmDistance.setValue(Integer.toString(settings.valueAlarmTrigger.getDistanceKM()));		
		
		this.pref_addLocations.setChecked(settings.valueAddLocations.isEnabled());
		
		this.pref_preferGps.setChecked(settings.valuePreferGps.isEnabled());
		
		this.pref_speechNotify.setChecked(settings.valueSpeechNotify.isEnabled());
	}
	
	private void updatePreferenceScreen()
	{
		//update preference summary
		String text = this.getString(R.string.preference_trigger_alarm_distance_summary);
		text += " ";
		text += pref_triggerAlarmDistance.getValue();
		text += "km";
		
		pref_triggerAlarmDistance.setSummary(text);		
	}
	
	private void storePreferences()
	{
		//update preference value
		final int triggerAlarmDistanceValue = Integer.parseInt(pref_triggerAlarmDistance.getValue());

		settings.valueAlarmTrigger.setDistanceKM(triggerAlarmDistanceValue);
		
		//update preference value		
		settings.valueAddLocations.set(pref_addLocations.isChecked());
		
		//update preference value
		settings.valuePreferGps.set(pref_preferGps.isChecked());
		
		//update preference value		
		settings.valueSpeechNotify.set(pref_speechNotify.isChecked());
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		updatePreferenceScreen();
	}

	private Preference.OnPreferenceChangeListener onPrefSpeechNotifyChangeListener = new Preference.OnPreferenceChangeListener()
	{
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue)
		{
			if (CityAlarmApplication.getTTS().isAvailable() == false)
			{
				showHintMessage(R.array.hint_install_tts);

				//disable update pref value
				return false;
			}
			
			final boolean speechEnabled = (Boolean) newValue;
			
			if (speechEnabled)
			{
				CityAlarmApplication.getTTS().say(getString(R.string.preference_speech_notify_enabled));
			}
			
			//allow update pref value
			return true;
		}
	};
	
	private void initializeSpeechEngine()
	{
		//update settings
		pref_speechNotify.setEnabled(false);
		pref_speechNotify.setSummary(getString(R.string.preference_speech_notify_checking_summary));
		
		//check TTS status
		CityAlarmApplication.getTTS().checkIfAvailable(SettingsActivity.this);
	}
	
	@Override
	public void onSpeechEngineInitialized(boolean isAvailable, String defaultEngineName)
	{
		if (isAvailable)
		{
			//update summary text
			String summary = getString(R.string.preference_speech_notify_summary);			
			summary += " (" + defaultEngineName + ")";
			pref_speechNotify.setSummary(summary);			
		}
		else
		{
			//update summary text
			String summary = getString(R.string.preference_speech_notify_disabled_summary);			
			pref_speechNotify.setSummary(summary);
			
			//disable speech
			pref_speechNotify.setChecked(false);			
		}

		pref_speechNotify.setEnabled(true);
	}

}
