package pl.pzagawa.cityalarm.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.pzagawa.cityalarm.Utils;
import pl.pzagawa.cityalarm.directions.DirectionItem;
import pl.pzagawa.cityalarm.location.LocationItem;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataDirectionsManager
	extends DataItemsManager
{
	public DataDirectionsManager(DataModel dm)
	{
		super(dm);
	}
	
	@Override
	protected String getTableName()
	{
		return "directions_log";
	}
	
	@Override
	protected String getTableSchema()
	{
		return "create table " + getTableName() + " ( _id integer primary key, location_id integer, distance integer, direction_ratio integer )"; 
	}
	
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
	
	public void insertItem(final SQLiteDatabase db, DirectionItem item)
	{
		db.insert(getTableName(), null, item.toContentValues());
	}

	public void updateItem(final SQLiteDatabase db, DirectionItem item)
	{
		final String where = "_id = " + Long.toString(item.getId());
		
		db.update(getTableName(), item.toContentValues(), where, null);
	}	
	
	public List<DirectionItem> getDirectionItems()
	{
		final List<DirectionItem> list = new ArrayList<DirectionItem>();
		
		final String query = String.format(Locale.US, "select * from %s",
			getTableName());

		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				list.add(new DirectionItem(cr));
			}
		};

		return list;
	}
	
	private String makeLocationIdsList(List<LocationItem> list)
	{
		final StringBuilder sb = new StringBuilder();
		
		for(LocationItem item : list)
		{
			sb.append(item.getId());
			sb.append(",");
		}

		return Utils.removeEndingText(sb.toString(), ",");
	}
	
	public Map<Long, DirectionItem> getDirectionItemsForLocations(List<LocationItem> locations)
	{
		final Map<Long, DirectionItem> map = new HashMap<Long, DirectionItem>();
		
		if (locations.size() > 0)
		{		
			final String locationIds = makeLocationIdsList(locations);
		
			final String query = "select * from " + getTableName() + " where location_id in (" + locationIds + ")";
			
			new DataQuery(dm, query)
			{
				@Override
				public void onNextCursorPosition(Cursor cr)
				{
					final DirectionItem directionItem = new DirectionItem(cr);
					map.put(directionItem.getLocationId(), directionItem);
				}
			};		
		}

		return map;
	}	

	public DirectionItem getDirectionItem(long locationId)
	{
		final DirectionItem value[] = new DirectionItem[1];
		
		final String query = "select * from " + getTableName() + " where location_id=" + Long.toString(locationId);
		
		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				value[0] = new DirectionItem(cr);
			}
		};		

		return value[0];
	}	
	
}
