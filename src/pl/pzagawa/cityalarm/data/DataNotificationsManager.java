package pl.pzagawa.cityalarm.data;

import java.util.ArrayList;
import java.util.List;

import pl.pzagawa.cityalarm.alarm.NotificationItem;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataNotificationsManager
	extends DataItemsManager
{
	public DataNotificationsManager(DataModel dm)
	{
		super(dm);
	}
	
	@Override
	protected String getTableName()
	{
		return "notifications_log";
	}
	
	@Override
	protected String getTableSchema()
	{
		return "create table " + getTableName() + " ( _id integer primary key, location_id integer, time integer )"; 
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
	
	public void insertItem(NotificationItem item)
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

	public void updateItem(NotificationItem item)
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
	
	public int getItemsCount(long location_id)
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

	public List<NotificationItem> getItems(long location_id)
	{
		final List<NotificationItem> list = new ArrayList<NotificationItem>();

		final String query = "select * from " + getTableName() + " where location_id=" + Long.toString(location_id);
		
		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				list.add(new NotificationItem(cr));
			}	
		};

		return list;
	}
	
}
