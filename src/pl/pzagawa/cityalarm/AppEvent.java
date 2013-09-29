package pl.pzagawa.cityalarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public enum AppEvent
{
	NEW_ROUTE(1),
	
	NEW_VISITED_LOCATION(2),
	NEW_SCANNED_LOCATION(3),
	
	LOCATION_SCANNER_START(4),
	LOCATION_SCANNER_STOP(5),
	
	ENABLE_LOCATION_SCANNING(6),
	DISABLE_LOCATION_SCANNING(7),
	
	TRIGGER_LOCATION(8),
	
	IMPORT_GEOCODEDB_START(9),
	IMPORT_GEOCODEDB_STOP(10),
	IMPORT_GEOCODEDB_ERROR(11),	
	GEOCODEDB_READY(12),
	
	CLEAR_NOTIFICATION(13);

	private final int id;
	private String message;
	private Object data;
	
	private AppEvent(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}
	
	public void setText(String value)
	{
		this.message = value;
	}
	
	public String getText()
	{
		return this.message;
	}

	public void setData(Object data)
	{
		this.data = data;
	}
	
	public Object getData()
	{
		return this.data;
	}
	
	public static AppEvent fromId(int id)
	{
		for (AppEvent appEvent : values())
		{
			if (appEvent.getId() == id)
				return appEvent;			
		}
		
		return null;
	}

	public static AppEvent fromIntent(Bundle bundle)
	{
		final int appEventId = bundle.getInt("AppEventId");

		return AppEvent.fromId(appEventId);
	}
	
	public Intent toIntent(Context context)	
	{
		final Intent intent = new Intent(context, ScannerServiceReceiver.class);

		intent.putExtra("AppEventId", getId());

		return intent;
	}

}
