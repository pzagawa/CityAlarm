package pl.pzagawa.cityalarm.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import pl.pzagawa.cityalarm.data.geocoder.GeocodeDatabaseFile;
import pl.pzagawa.cityalarm.data.geocoder.NodeItem;
import pl.pzagawa.cityalarm.location.BoundingBox;
import pl.pzagawa.cityalarm.location.ScannedItem;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataNodesManager
	extends DataItemsManager
{
	public DataNodesManager(DataModel dm)
	{
		super(dm);
	}
	
	@Override
	protected String getTableName()
	{
		return "nodes";
	}

	@Override
	protected String getTableSchema()
	{
		return "create table " + getTableName() + " ( _id integer primary key, " +
			"type integer, latitude integer, longitude integer, name text )";
	}
		
	@Override
	public void truncate()
	{
	}
	
	public void insertItem(SQLiteDatabase db, NodeItem item)
	{
		db.insert(getTableName(), null, item.toContentValues());
	}
	
	public boolean isDataReady()
	{
		final int size = dm.getSingleIntegerValue("select count(*) as size from nodes");
		
		return (size >= GeocodeDatabaseFile.TOTAL_ITEMS);
	}

	public void createTableIndexes(SQLiteDatabase db)
	{
		db.execSQL("create index if not exists coords_lat_index on nodes (latitude)");		
		db.execSQL("create index if not exists coords_lon_index on nodes (longitude)");
	}
	
	public List<NodeItem> getItems(ScannedItem location, double distance)
	{
		final ArrayList<NodeItem> list = new ArrayList<NodeItem>();
		
		final BoundingBox bbox = new BoundingBox(location, distance);
		
		//select NodeItem.TYPE_CITY, TYPE_TOWN, TYPE_VILLAGE
		final String query = "select * from nodes where " + bbox.getSearchCondition();
				
		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				list.add(new NodeItem(cr));
			}
		};

		sortByDistance(list, location);

		return list;
	}
	
    private Comparator<NodeItem> compareToLocation = new Comparator<NodeItem>()
	{
		@Override
		public int compare(NodeItem item1, NodeItem item2)
		{
			final double distance1 = item1.getDistance();
			final double distance2 = item2.getDistance();

			if (distance1 > distance2)
				return 1;

			if (distance1 < distance2)
				return -1;

			return 0;
		}
	};
    
	private void sortByDistance(List<NodeItem> items, ScannedItem location)
	{
		if (location == null)
			return;
		
		//update each node distance for sorting comparator
		for (NodeItem item : items)
		{
			final double meters = location.distanceTo(item.getLat(), item.getLon());
			item.setDistance((int) meters);
		}
		
		Collections.sort(items, compareToLocation);
	}
	
	public List<NodeItem> getItemsByName(String textName, ScannedItem location)
	{
		final ArrayList<NodeItem> list = new ArrayList<NodeItem>();
	
		if (textName.length() == 0)
			return list;
		
		final String query = String.format(Locale.US, "select * from %s where name like '%s%%'",
			getTableName(), textName);
		
		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				list.add(new NodeItem(cr));
			}
		};

		sortByDistance(list, location);
		
		return list;
	}
	
}
