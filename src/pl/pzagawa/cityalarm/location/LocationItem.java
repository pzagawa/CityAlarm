package pl.pzagawa.cityalarm.location;

import java.util.List;

import pl.pzagawa.cityalarm.data.geocoder.NodeItem;
import android.content.ContentValues;
import android.database.Cursor;

public class LocationItem
	extends GeoPosItem
{
	private final int nodeType;
	private final String name;
	
	private boolean alarm_enabled;
	
	private String scanningUID = "";

	public LocationItem(NodeItem nodeItem)
	{
		super(0, 0, 0);
		
		this.id = -1;
		
		this.int_lat = nodeItem.getIntLat();
		this.int_lon = nodeItem.getIntLon();
		
		this.lat = LocationConverter.intToDouble(int_lat);
		this.lon = LocationConverter.intToDouble(int_lon);
		
		this.nodeType = nodeItem.getNodeType();		
		this.name = nodeItem.getName();		
		this.alarm_enabled = false;
		this.scanningUID = "";
	}
	
	public LocationItem(ScannedItem location, NodeItem nodeItem)
	{
		super(0, 0, 0);
		
		this.id = -1;
		
		this.int_lat = nodeItem.getIntLat();
		this.int_lon = nodeItem.getIntLon();
		
		this.lat = LocationConverter.intToDouble(int_lat);
		this.lon = LocationConverter.intToDouble(int_lon);
		
		this.accuracy = location.getAccuracy();
		this.time = location.getTime();
		
		this.nodeType = nodeItem.getNodeType();		
		this.name = nodeItem.getName();		
		this.alarm_enabled = false;
		this.scanningUID = "";
	}

	public LocationItem(ScannedItem location, LocationItem alarmItem, NodeItem nodeItem)
	{
		super(0, 0, 0);
		
		this.id = alarmItem.getId();
		
		this.int_lat = nodeItem.getIntLat();
		this.int_lon = nodeItem.getIntLon();
		
		this.lat = LocationConverter.intToDouble(int_lat);
		this.lon = LocationConverter.intToDouble(int_lon);
		
		this.accuracy = location.getAccuracy();
		this.time = location.getTime();
		
		this.nodeType = nodeItem.getNodeType();
		this.name = nodeItem.getName();
		this.alarm_enabled = alarmItem.isAlarmEnabled();
		this.scanningUID = "";
	}
	
	public LocationItem(Cursor cr)
	{
		super(cr);
		
		this.nodeType = cr.getInt(cr.getColumnIndex("type"));
		this.name = cr.getString(cr.getColumnIndex("name"));
		this.alarm_enabled = (cr.getInt(cr.getColumnIndex("alarm_enabled")) == 1);
		this.scanningUID = cr.getString(cr.getColumnIndex("scanningUID"));
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(super.toString());
		sb.append(getNodeTypeString());
		sb.append(";");
		sb.append(name);
		sb.append(";");
		sb.append(alarm_enabled ? "alarm_on" : "alarm_off");

		return sb.toString();		
	}
    
	public int getNodeType()
	{
		return nodeType;
	}
	
	public String getNodeTypeString()
	{
		String itemType = "_";
		
		switch (nodeType)
		{
		case NodeItem.TYPE_CITY:
			itemType = "c";
			break;
		case NodeItem.TYPE_TOWN:
			itemType = "t";
			break;
		case NodeItem.TYPE_VILLAGE:
			itemType = "v";
			break;
		case NodeItem.TYPE_LOCALITY:
			itemType = "l";
			break;
		case NodeItem.TYPE_SUBURB:
			itemType = "s";
			break;
		}
		
		return itemType;		
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean isAlarmEnabled()
	{
		return alarm_enabled;
	}
		
	public void setAlarmEnabled(boolean alarm_enabled)
	{
		this.alarm_enabled = alarm_enabled;
	}

	public ContentValues toContentValues()
	{
		ContentValues values = super.toContentValues();

		values.put("type", nodeType);
		values.put("name", name);
		values.put("alarm_enabled", alarm_enabled);
		values.put("scanningUID", scanningUID);
		
		return values;
	}

	public String getScanningUID()
	{
		return scanningUID;
	}

	public void setScanningUID(String scanningUID)
	{
		this.scanningUID = scanningUID;
	}

	public boolean scanningUidEqual(String uid)
	{
		return (getScanningUID().equalsIgnoreCase(uid)); 
	}

	public static LocationItem getItemById(List<LocationItem> list, long id)
	{
		for (LocationItem item : list)
			if (item.getId() == id)
				return item;
		
		return null;
	}	
	
}
