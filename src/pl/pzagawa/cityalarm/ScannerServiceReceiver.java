package pl.pzagawa.cityalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ScannerServiceReceiver
	extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		final Bundle bundle = intent.getExtras();
		
		if (bundle != null)
		{
			final AppEvent event = AppEvent.fromIntent(bundle);
			
			if (event != null)
			{				
				AppEventManager appEventManager = CityAlarmApplication.getAppEventManager();

				if (appEventManager != null)
				{
					try
					{
						appEventManager.callEvent(event);
					}
					catch (Exception e)
					{
						//eat this
					}
				}
			}
		}
	}

}
