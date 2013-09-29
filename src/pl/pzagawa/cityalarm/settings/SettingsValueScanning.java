package pl.pzagawa.cityalarm.settings;

import java.util.Calendar;
import java.util.UUID;

import android.content.SharedPreferences;

public class SettingsValueScanning
	extends SettingsValue
{
	private static final int LOCATION_NULL_VALUE = 0;

	public SettingsValueScanning(SettingsManager settingsManager)
	{
		super(settingsManager);
	}

	public synchronized void setEnabled(boolean enabled)
	{
		try
		{
			SharedPreferences.Editor editor = getPrefs().edit();

			editor.putBoolean(VALUE_SCANNING_ENABLED, enabled);
			
			if (enabled)
			{
				editor.putLong(VALUE_SCANNING_START_TIME, Calendar.getInstance().getTimeInMillis());
				editor.putString(VALUE_SCANNING_UID, UUID.randomUUID().toString());
				editor.putLong(VALUE_SCANNING_START_LOCATION_ID, LOCATION_NULL_VALUE);
				editor.putLong(VALUE_SCANNING_STOP_LOCATION_ID, LOCATION_NULL_VALUE);
			}
			
			editor.commit();
		}
		catch (Exception e)
		{
			//eat this
		}		
	}

	public synchronized boolean isEnabled()
	{
		return getPrefs().getBoolean(VALUE_SCANNING_ENABLED, false);
	}
	
	public synchronized Calendar getStartTime()
	{
		final long scanningStartTimeValue = getPrefs().getLong(VALUE_SCANNING_START_TIME, 0);
		
		if (scanningStartTimeValue == 0)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(scanningStartTimeValue);

		return cal;
	}

	public synchronized int getSecondsFromStart()
	{
		final long scanningStartTimeValue = getPrefs().getLong(VALUE_SCANNING_START_TIME, 0);
		
		if (scanningStartTimeValue == 0)
			return 0;

		Calendar scanningStartTime = Calendar.getInstance();
		scanningStartTime.setTimeInMillis(scanningStartTimeValue);
			
		Calendar calNow = Calendar.getInstance();
	
		return (int) ((calNow.getTimeInMillis() - scanningStartTime.getTimeInMillis()) / 1000f);
	}

	public synchronized String getUID()
	{
		return getPrefs().getString(VALUE_SCANNING_UID, UUID.randomUUID().toString());
	}

	//VALUE_SCANNING_START_LOCATION_ID
	public synchronized void setStartLocationId(long id)
	{
		try
		{
			SharedPreferences.Editor editor = getPrefs().edit();

			editor.putLong(VALUE_SCANNING_START_LOCATION_ID, id);
			
			editor.commit();
		}
		catch (Exception e)
		{
			//eat this
		}		
	}
	
	public synchronized long getStartLocationId()
	{
		return getPrefs().getLong(VALUE_SCANNING_START_LOCATION_ID, LOCATION_NULL_VALUE);
	}
	
	public synchronized void resetStartLocation()
	{
		try
		{
			SharedPreferences.Editor editor = getPrefs().edit();

			editor.putLong(VALUE_SCANNING_START_LOCATION_ID, LOCATION_NULL_VALUE);
			
			editor.commit();
		}
		catch (Exception e)
		{
			//eat this
		}		
	}
	
	public synchronized boolean isStartLocationSet()
	{
		return (getStartLocationId() != LOCATION_NULL_VALUE);
	}
	
	//VALUE_SCANNING_STOP_LOCATION_ID	
	public synchronized void setStopLocationId(long id)
	{
		try
		{
			SharedPreferences.Editor editor = getPrefs().edit();

			editor.putLong(VALUE_SCANNING_STOP_LOCATION_ID, id);
			
			editor.commit();
		}
		catch (Exception e)
		{
			//eat this
		}		
	}

	public synchronized long getStopLocationId()
	{
		return getPrefs().getLong(VALUE_SCANNING_STOP_LOCATION_ID, LOCATION_NULL_VALUE);
	}

	public synchronized void resetStopLocation()
	{
		try
		{
			SharedPreferences.Editor editor = getPrefs().edit();

			editor.putLong(VALUE_SCANNING_STOP_LOCATION_ID, LOCATION_NULL_VALUE);
			
			editor.commit();
		}
		catch (Exception e)
		{
			//eat this
		}		
	}
	
	public synchronized boolean isStopLocationSet()
	{
		return (getStopLocationId() != LOCATION_NULL_VALUE);
	}
	
}
