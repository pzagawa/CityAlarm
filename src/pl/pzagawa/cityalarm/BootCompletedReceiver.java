package pl.pzagawa.cityalarm;

import pl.pzagawa.cityalarm.settings.SettingsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver
	extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		final SettingsManager settings = CityAlarmApplication.getSettingsManager();

		//reset scanning mode to OFF after boot, because scanning service timer is also cleared after boot
		settings.valueScanning.setEnabled(false);
	}

}
