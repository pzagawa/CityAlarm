package pl.pzagawa.cityalarm.directions;

import android.content.ContentValues;
import android.database.Cursor;

public class DirectionItem
{
	private static final int TRESHOLD_MOVEMENT_METERS = 10;
	private static final int MAX_DIRECTION_RATIO = 1000;
	
	public final long id;
	public final long location_id;

	private int distance;
	private int direction_ratio;
	
	public DirectionItem(long location_id)
	{
		this.id = -1;
		this.location_id = location_id;		
		this.distance = 0;
		this.direction_ratio = 0;
	}

	public DirectionItem(Cursor cr)
	{
		this.id = cr.getLong(cr.getColumnIndex("_id"));
		this.location_id = cr.getLong(cr.getColumnIndex("location_id"));
		this.distance = cr.getInt(cr.getColumnIndex("distance"));
		this.direction_ratio = cr.getInt(cr.getColumnIndex("direction_ratio"));
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(id);
		sb.append(";");
		sb.append(location_id);
		sb.append(";");		
		sb.append(Integer.toString(distance));
		sb.append(";");		
		sb.append(Integer.toString(direction_ratio));

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

	public int getDistance()
	{
		return distance;
	}

	public int getDirection()
	{
		if (this.direction_ratio < 0)
			return LocationDirection.GETTING_CLOSER;
		
		if (this.direction_ratio > 0)
			return LocationDirection.MOVES_AWAY;

		return LocationDirection.IN_PLACE;
	}
	
	public boolean isDirectionMovingAway()
	{
		return (getDirection() == LocationDirection.MOVES_AWAY);
	}

	public boolean isDirectionGettingCloser()
	{
		return (getDirection() == LocationDirection.GETTING_CLOSER);
	}
	
	public void setDirectionMovingAway()
	{
		this.direction_ratio = MAX_DIRECTION_RATIO;
	}

	public void setDirectionGettingCloser()
	{
		this.direction_ratio = -MAX_DIRECTION_RATIO;
	}
	
	public void update(int currDistance)
	{
		final int deltaDistance = currDistance - getDistance();
		
		if (deltaDistance > +TRESHOLD_MOVEMENT_METERS)
		{
			this.direction_ratio++;
		}
		
		if (deltaDistance < -TRESHOLD_MOVEMENT_METERS)
		{
			this.direction_ratio--;
		}
		
		this.distance = currDistance;				
	}
	
	public int getDirectionRatio()
	{
		return this.direction_ratio;
	}
	
	public ContentValues toContentValues()
	{
		ContentValues values = new ContentValues();

		values.put("location_id", location_id);
		values.put("distance", distance);
		values.put("direction_ratio", direction_ratio);
		
		return values;
	}
	
}
