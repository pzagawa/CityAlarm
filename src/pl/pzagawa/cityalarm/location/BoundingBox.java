package pl.pzagawa.cityalarm.location;

public class BoundingBox
{
	//1deg in km, for Poland, earth radius on latitude 50-54 deg
	private final static double lat_1deg = 111.2; 	 
	private final static double lon_1deg = 63;
	
	public final double min_lat;
	public final double max_lat;

	public final double min_lon;
	public final double max_lon;
	
	public BoundingBox(ScannedItem location, double distance)
	{
		this.min_lat = addLatDistance(location.getLat(), -distance);
		this.max_lat = addLatDistance(location.getLat(), distance);
		this.min_lon = addLonDistance(location.getLon(), -distance);
		this.max_lon = addLonDistance(location.getLon(), distance);
	}

	private double lat_offset(double distance)
	{
    	return (distance / lat_1deg);
	}

	private double lon_offset(double distance)
	{
    	return (distance / lon_1deg);
	}
	
	private double addLatDistance(double lat, double distance)
    {
    	return lat + lat_offset(distance);
    }

	private double addLonDistance(double lon, double distance)
    {
    	return lon + lon_offset(distance);    	
    }	
	
	public int getIntMinLat()
	{		
		return LocationConverter.doubleToInt(min_lat);
	}

	public int getIntMaxLat()
	{
		return LocationConverter.doubleToInt(max_lat);
	}

	public int getIntMinLon()
	{
		return LocationConverter.doubleToInt(min_lon);
	}

	public int getIntMaxLon()
	{
		return LocationConverter.doubleToInt(max_lon);
	}

	public String getSearchCondition()
	{
		final StringBuilder sb = new StringBuilder(); 
		
		sb.append("(latitude >= ");
		sb.append(getIntMinLat());
		sb.append(" and latitude <= ");
		sb.append(getIntMaxLat());
		sb.append(") and (longitude >= ");
		sb.append(getIntMinLon());
		sb.append(" and longitude <= ");
		sb.append(getIntMaxLon());
		sb.append(")");
		
		return sb.toString();
	}
	
}
