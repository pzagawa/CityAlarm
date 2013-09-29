package pl.pzagawa.cityalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NotificationIntentReceiver
	extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		final Bundle bundle = intent.getExtras();
		
		if (bundle != null)
		{
			final long locationId = bundle.getLong("locationId");
			
			AppEventManager appEventManager = CityAlarmApplication.getAppEventManager();
						
			if (appEventManager != null)
			{
				try
				{
					AppEvent.CLEAR_NOTIFICATION.setData(locationId);
					
					appEventManager.callEvent(AppEvent.CLEAR_NOTIFICATION);
				}
				catch (Exception e)
				{
					//eat this
				}
			}
		}
	}

}
