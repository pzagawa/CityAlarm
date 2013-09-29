package pl.pzagawa.cityalarm.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.pzagawa.cityalarm.Utils;
import pl.pzagawa.cityalarm.alarm.AlarmItem;
import pl.pzagawa.cityalarm.location.LocationItem;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataAlarmsManager
	extends DataItemsManager
{
	public DataAlarmsManager(DataModel dm)
	{
		super(dm);
	}
	
	@Override
	protected String getTableName()
	{
		return "locations_alarms";
	}
	
	@Override
	protected String getTableSchema()
	{
		return "create table " + getTableName() + " ( _id integer primary key, location_id integer, time integer )"; 
	}

	@Override
	public void truncate()
	{
		final SQLiteDatabase db = dm.getWritableDatabase();
		
		try
		{
			truncate(db);
		}
		finally
		{
			db.close();
		}
	}
	
	public void truncate(SQLiteDatabase db)
	{
		db.execSQL("delete from " + getTableName());		
	}
	
	public void insertItem(AlarmItem item)
	{
		final SQLiteDatabase db = dm.getWritableDatabase();
		
		try
		{
			db.insert(getTableName(), null, item.toContentValues());
		}
		finally
		{
			db.close();
		}		
	}

	private String makeLocationIdsList(List<LocationItem> list)
	{
		final StringBuilder sb = new StringBuilder();
		
		for(LocationItem item : list)
		{
			if (item.isAlarmEnabled())
			{
				sb.append(item.getId());
				sb.append(",");
			}
		}

		return Utils.removeEndingText(sb.toString(), ",");
	}
	
	//maps alarm count by location_id
	public Map<Long, Integer> getAlarmCountForLocations(List<LocationItem> locations)
	{
		final Map<Long, Integer> map = new HashMap<Long, Integer>();
		
		if (locations.size() > 0)
		{		
			final String locationIds = makeLocationIdsList(locations);
		
			final String query = "select * from " + getTableName() + " where location_id in (" + locationIds + ")";
			
			new DataQuery(dm, query)
			{
				@Override
				public void onNextCursorPosition(Cursor cr)
				{
					final AlarmItem alarmItem = new AlarmItem(cr);
					
					final long locationId = alarmItem.getLocationId();
				
					if (map.containsKey(locationId))
					{
						final int alarmsCount = map.get(locationId);
						map.put(locationId, alarmsCount + 1);
					}
					else
					{
						map.put(locationId, 1);
					}
				}
			};		
		}

		return map;
	}	

	public int getAlarmCount(long location_id)
	{
		final int value[] = new int[1];
				
		final String query = "select * from " + getTableName() + " where location_id=" + Long.toString(location_id);
		
		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				value[0]++;
			}	
		};

		return value[0];
	}	
	
}
