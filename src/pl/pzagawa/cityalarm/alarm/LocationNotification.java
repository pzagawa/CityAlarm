package pl.pzagawa.cityalarm.alarm;

import pl.pzagawa.cityalarm.CityAlarmApplication;
import pl.pzagawa.cityalarm.MainActivity;
import pl.pzagawa.cityalarm.NotificationIntentReceiver;
import pl.pzagawa.cityalarm.R;
import pl.pzagawa.cityalarm.data.DataModel;
import pl.pzagawa.cityalarm.location.LocationItem;
import pl.pzagawa.cityalarm.settings.SettingsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class LocationNotification
{
	public static final int MIN_NOTIFICATIONS = 1;
	public static final int MAX_NOTIFICATIONS = 3;
	
	private static final int LED_LIGHT = 0xffffff00;
	
	private static final long[] VIBRATION_PATTERN = { 0, 50, 100, 50, 100, 50, 100, 500 };

	private final Context context;
	private final NotificationManager notificationManager;
		
	public LocationNotification()
	{
		this.context = CityAlarmApplication.getContext();
		this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);		
	}
	
	private PendingIntent getActivityIntent()
	{
		final Intent intent = new Intent(context, MainActivity.class);

		final int requestCode = 0;

		return PendingIntent.getActivity(context, requestCode, intent, 0);
	}
	
	private PendingIntent getDeleteIntent(long locationId)
	{
		final Intent intent = new Intent(context, NotificationIntentReceiver.class);
		
		intent.putExtra("locationId", locationId);

		final int requestCode = (int) locationId;

		return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);		
	}
	
	public void notify(LocationItem location)
	{
		final long locationId = location.getId();
		
		final String notifyTitle = context.getString(R.string.notification_alarm_title);
		final String notifyText = context.getString(R.string.notification_alarm_text) + " " +  location.getName();

		Notification.Builder builder = new Notification.Builder(context);

		builder.setSmallIcon(R.drawable.notify_alarm);
		builder.setContentTitle(notifyTitle);
		builder.setContentText(notifyText);
				
		builder.setLights(LED_LIGHT, 10, 300);
		builder.setVibrate(VIBRATION_PATTERN);

		builder.setContentIntent(getActivityIntent());
		builder.setDeleteIntent(getDeleteIntent(locationId));

		builder.setSound(CityAlarmApplication.getAppResources().getSoundAlarm());
		
		Notification notification = builder.getNotification();
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify((int)locationId, notification);		
	}

	public void log(DataModel dm, LocationItem location)
	{
		final NotificationItem notificationItem = new NotificationItem(location.getId());

		dm.dataNotifications.insertItem(notificationItem);
	}
	
	public boolean isMaxNotificationsCount(DataModel dm, LocationItem location)
	{		
		final SettingsManager settings = CityAlarmApplication.getSettingsManager();
		
		int notificationsCountRequired = MIN_NOTIFICATIONS;

		if (settings.valueScanning.isStopLocationSet())
		{
			if (location.getId() == settings.valueScanning.getStopLocationId())
			{
				notificationsCountRequired = MAX_NOTIFICATIONS;
			}
		}

		if (dm.dataNotifications.getItemsCount(location.getId()) >= notificationsCountRequired)
			return true;

		return false;
	}

	public static void cancelAll()
	{
		final Context context = CityAlarmApplication.getContext();
		final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.cancelAll();
	}

	public static void cancel(long locationId)
	{
		final Context context = CityAlarmApplication.getContext();
		final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.cancel((int) locationId);
	}
	
}
