package pl.pzagawa.cityalarm.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.pzagawa.cityalarm.location.ScannedItem;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataScannedLocationsManager
	extends DataItemsManager
{
	public DataScannedLocationsManager(DataModel dm)
	{
		super(dm);
	}

	@Override
	protected String getTableName()
	{
		return "scanned_log";
	}

	@Override
	protected String getTableSchema()
	{
		return "create table " + getTableName() + " ( _id integer primary key, " +
			"latitude integer, longitude integer, accuracy integer, time integer )";
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

	public void insertItem(ScannedItem item)
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

	public void updateItem(ScannedItem item)
	{
		final String where = "_id = " + Long.toString(item.getId());
		
		final SQLiteDatabase db = dm.getWritableDatabase();
		
		try
		{
			db.update(getTableName(), item.toContentValues(), where, null);
		}
		finally
		{
			db.close();
		}		
	}
	
	public ScannedItem getLastScannedLocation()
	{
		final ScannedItem value[] = new ScannedItem[1];

		final String query = String.format(Locale.US, "select * from %s order by time desc limit 1",
			getTableName());
				
		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				value[0] = new ScannedItem(cr);
			}	
		};

		return value[0];
	}
	
	public List<ScannedItem> getLocations()
	{
		final List<ScannedItem> list = new ArrayList<ScannedItem>();
		
		final String query = String.format(Locale.US, "select * from %s order by time desc",
			getTableName());

		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				list.add(new ScannedItem(cr));
			}
		};

		return list;
	}
	
	public ScannedItem getLocation(int lat, int lon)
	{
		final ScannedItem value[] = new ScannedItem[1];
		
		final String query = String.format(Locale.US, "select * from %s where latitude=%d and longitude=%d",
			getTableName(), lat, lon);

		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				value[0] = new ScannedItem(cr);
			}	
		};

		return value[0];
	}
	
}
