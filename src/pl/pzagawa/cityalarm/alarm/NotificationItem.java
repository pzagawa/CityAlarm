package pl.pzagawa.cityalarm.alarm;

import java.util.Calendar;

import pl.pzagawa.cityalarm.Utils;
import android.content.ContentValues;
import android.database.Cursor;

public class NotificationItem
{
	public final long id;
	public final long location_id;

	private long time;
	
	public NotificationItem(long location_id)
	{
		this.id = -1;
		this.location_id = location_id;
		this.time = Calendar.getInstance().getTimeInMillis();		
	}

	public NotificationItem(Cursor cr)
	{
		this.id = cr.getLong(cr.getColumnIndex("_id"));
		this.location_id = cr.getLong(cr.getColumnIndex("location_id"));
		this.time = cr.getLong(cr.getColumnIndex("time"));
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(id);
		sb.append(";");
		sb.append(location_id);
		sb.append(";");		
		sb.append(getTimeString());

		return sb.toString();		
	}
    
	public long getId()
	{
		return id;
	}

	public long getLocationId()
	{
		return location_id;
	}
		
	public long getTime()
	{
		return time;
	}
	
	public String getTimeString()
	{	
		Calendar calTime = Calendar.getInstance();
		calTime.setTimeInMillis(time);

		return Utils.getTimeString(calTime);
	}
	
	public ContentValues toContentValues()
	{
		ContentValues values = new ContentValues();

		values.put("location_id", location_id);
		values.put("time", time);
		
		return values;
	}
	
}
