package pl.pzagawa.cityalarm.data.geocoder;

import pl.pzagawa.cityalarm.location.LocationConverter;
import android.content.ContentValues;
import android.database.Cursor;

public class NodeItem
{
	public static final int TYPE_CITY = 1; //count: 40
	public static final int TYPE_TOWN = 2; //count: 899
	public static final int TYPE_VILLAGE = 3; //count: 46269
	public static final int TYPE_LOCALITY = 4; //count: 1434
	public static final int TYPE_SUBURB = 5; //count: 1383

	private int nodeType = -1;
	private int int_lat = 0; 
	private int int_lon = 0;
	private String name = "";
	
	private int distance = 0;

	public NodeItem()
	{		
	}

	public NodeItem(Cursor cr)
	{	
		this.nodeType = cr.getInt(cr.getColumnIndex("type"));
		this.int_lat = cr.getInt(cr.getColumnIndex("latitude"));
		this.int_lon = cr.getInt(cr.getColumnIndex("longitude"));
		this.name = cr.getString(cr.getColumnIndex("name"));
	}
	
	public void parse(String[] items)
	{		
		//get type
		final String itemType = items[0];
		
		nodeType = -1;
		
		if (itemType.equals("c"))
			nodeType = TYPE_CITY;
		if (itemType.equals("t"))
			nodeType = TYPE_TOWN;
		if (itemType.equals("v"))
			nodeType = TYPE_VILLAGE;
		if (itemType.equals("l"))
			nodeType = TYPE_LOCALITY;
		if (itemType.equals("s"))
			nodeType = TYPE_SUBURB;
		
		//get latitude
		String strLat = items[1];
		
		if (strLat.length() != 9)
			throw new IllegalArgumentException("Parsing coords / lat size incorrect: " + strLat);
		
		this.int_lat = Integer.parseInt(strLat);
		
		//get longitude
		String strLon = items[2];

		if (strLon.length() != 9)
			throw new IllegalArgumentException("Parsing coords / lon size incorrect: " + strLon);
		
		this.int_lon = Integer.parseInt(strLon);
		
		//get name
		this.name = items[3];		
	}

	public int getNodeType()
	{
		return nodeType;
	}

	public double getLat()
	{
		return LocationConverter.intToDouble(int_lat);
	}

	public double getLon()
	{
		return LocationConverter.intToDouble(int_lon);
	}
	
	public int getIntLat()
	{
		return int_lat;
	}

	public int getIntLon()
	{
		return int_lon;
	}

	public String getName()
	{
		return name;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		String itemType = "_";

		switch (nodeType)
		{
		case TYPE_CITY:
			itemType = "c";
			break;
		case TYPE_TOWN:
			itemType = "t";
			break;
		case TYPE_VILLAGE:
			itemType = "v";
			break;
		case TYPE_LOCALITY:
			itemType = "l";
			break;
		case TYPE_SUBURB:
			itemType = "s";
			break;
		}
				
		sb.append(itemType);
		sb.append(";");
		sb.append(Integer.toString(int_lat));
		sb.append(";");
		sb.append(Integer.toString(int_lon));
		sb.append(";");
		sb.append(name);
		sb.append(";");
		sb.append(distance);
		
		return sb.toString();
	}
	
	public ContentValues toContentValues()
	{
		ContentValues values = new ContentValues();

		values.put("type", nodeType);
		values.put("latitude", int_lat);
		values.put("longitude", int_lon);
		values.put("name", name);		
		
		return values;
	}

	public int getDistance()
	{		
		return distance;
	}
	
	public void setDistance(int value)
	{
		this.distance = value;
	}

}
