package pl.pzagawa.cityalarm.location;

import java.util.Calendar;

import pl.pzagawa.cityalarm.Utils;
import android.content.ContentValues;
import android.database.Cursor;

public abstract class GeoPosItem
{
	protected long id;
	
	protected double lat;
	protected double lon;
	
	protected int int_lat;
	protected int int_lon;

	protected int accuracy;	
	protected long time;
	
	//for sorting purposes only
	protected int distance = 0;

	public GeoPosItem(double lat, double lon, float accuracy)
	{
		this.id = -1;
		
		this.lat = lat;
		this.lon = lon;
		
		this.int_lat = LocationConverter.doubleToInt(lat);
		this.int_lon = LocationConverter.doubleToInt(lon);
		
		this.accuracy = (int) accuracy;
		this.time = Calendar.getInstance().getTimeInMillis();
	}
	
	public GeoPosItem(Cursor cr)
	{
		this.id = cr.getLong(cr.getColumnIndex("_id"));
		
		this.int_lat = cr.getInt(cr.getColumnIndex("latitude"));
		this.int_lon = cr.getInt(cr.getColumnIndex("longitude"));
		
		this.lat = LocationConverter.intToDouble(int_lat);
		this.lon = LocationConverter.intToDouble(int_lon);
		
		this.accuracy = cr.getInt(cr.getColumnIndex("accuracy"));
		this.time = cr.getLong(cr.getColumnIndex("time"));
	}
	
	public long getId()
	{
		return id;
	}
		
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(Long.toString(id));
		sb.append(";");
		sb.append(Integer.toString(int_lat));
		sb.append(";");
		sb.append(Integer.toString(int_lon));
		sb.append(";");
		sb.append(accuracy);
		sb.append(";");		
		sb.append(getDistance());
		sb.append(";");
		sb.append(getTimeString());
		sb.append(";");		

		return sb.toString();		
	}
		
	//returns distance in meters
    public double distanceTo(double lat2, double lng2)
    {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat);
        double dLng = Math.toRadians(lng2-lon);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
        	Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(lat2)) *
        	Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        int meterConversion = 1609;

        return (dist * meterConversion);
    }
    
	public double getLat()
	{
		return lat;
	}

	public double getLon()
	{
		return lon;
	}

	public int getIntLat()
	{
		return int_lat;
	}

	public int getIntLon()
	{
		return int_lon;
	}
	
	public int getAccuracy()
	{
		return accuracy;
	}
	
	public boolean coordsEqual(GeoPosItem item)
	{
		return (item.int_lat == int_lat && item.int_lon == int_lon); 
	}

	public boolean areCoordsSet()
	{
		return (int_lat != 0) && (int_lon != 0);
	}

	public int getDistance()
	{		
		return distance;
	}
	
	public void setDistance(int value)
	{
		this.distance = value;
	}
	
	public void setTime(long time)
	{
		this.time = time;
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

	public String getNiceTimeString()
	{	
		Calendar calTime = Calendar.getInstance();
		calTime.setTimeInMillis(time);

		return Utils.getNiceTimeString(calTime);
	}
	
	public int getTimeAsSecondsFromNow()
	{	
		Calendar calNow = Calendar.getInstance();

		Calendar calTime = Calendar.getInstance();
		calTime.setTimeInMillis(time);

		return (int) ((calNow.getTimeInMillis() - calTime.getTimeInMillis()) / 1000f);
	}

	public int getTimeAsSecondsFromTime(long startTime)
	{	
		if (startTime == 0)
			return 0;

		Calendar calStartTime = Calendar.getInstance();
		calStartTime.setTimeInMillis(startTime);

		Calendar calTime = Calendar.getInstance();
		calTime.setTimeInMillis(time);

		return (int) ((calTime.getTimeInMillis() - calStartTime.getTimeInMillis()) / 1000f);
	}	
	
	public ContentValues toContentValues()
	{
		ContentValues values = new ContentValues();

		values.put("latitude", int_lat);
		values.put("longitude", int_lon);
		values.put("accuracy", accuracy);
		values.put("time", time);
		
		return values;
	}

}
