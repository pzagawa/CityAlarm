package pl.pzagawa.cityalarm;

import pl.pzagawa.cityalarm.alarm.TextToSpeechManager;
import pl.pzagawa.cityalarm.data.DataModel;
import pl.pzagawa.cityalarm.data.geocoder.GeocodeDatabaseUpdateService;
import pl.pzagawa.cityalarm.settings.SettingsManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;

public class CityAlarmApplication
	extends Application
{
	private static Context context;
	private static CityAlarmResources resources;	
	private static AppEventManager appEventManager;
	private static SettingsManager settingsManager;
	private static TextToSpeechManager ttsManager; 	
	
	@Override
	public void onCreate()
	{
		super.onCreate();

        context = getApplicationContext();
		resources = new CityAlarmResources(this);
		appEventManager = new AppEventManager(this);
		settingsManager = new SettingsManager(this);
		ttsManager = new TextToSpeechManager(this);

		runGeocodeDatabaseUpdate();
	}

	@Override
	public void onTerminate()
	{		
		ttsManager.close();

		super.onTerminate();
	}

	public static Context getContext()
	{
		return context;
	}
	
	public static LayoutInflater getLayoutInflater()
	{
		return LayoutInflater.from(context);
	}

	public static CityAlarmResources getAppResources()
	{
		return resources;
	}

	public static AppEventManager getAppEventManager()
	{
		return appEventManager;
	}
	
	public static SettingsManager getSettingsManager()
	{
		return settingsManager;
	}
	
	public static TextToSpeechManager getTTS()
	{
		return ttsManager;
	}
	
	public static void sendEvent(AppEvent appEvent)
	{
		final Intent intent = appEvent.toIntent(getContext());
		getContext().sendBroadcast(intent);
	}
	
	private void runGeocodeDatabaseUpdate()
	{
		final DataModel dm = new DataModel();
		
		boolean runService = false;
		
		try
		{
			if (dm.dataNodes.isDataReady())
			{
				sendEvent(AppEvent.GEOCODEDB_READY);
				return;
			}
			else
			{
				runService = true;
			}
		}
		finally
		{
			dm.close();
		}

		if (runService)
		{
	        final Intent geocodeDbUpdateIntent = new Intent(this, GeocodeDatabaseUpdateService.class);
	        this.startService(geocodeDbUpdateIntent);
		}		
	}	
	
}
