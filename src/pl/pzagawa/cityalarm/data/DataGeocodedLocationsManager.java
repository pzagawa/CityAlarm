package pl.pzagawa.cityalarm.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import pl.pzagawa.cityalarm.location.GeoPosItem;
import pl.pzagawa.cityalarm.location.LocationItem;
import pl.pzagawa.cityalarm.settings.SettingsManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataGeocodedLocationsManager
	extends DataItemsManager
{
	private long visitedLocationId = 0;
	
	public DataGeocodedLocationsManager(DataModel dm)
	{
		super(dm);
	}

	@Override
	protected String getTableName()
	{
		return "locations_log";
	}

	@Override
	protected String getTableSchema()
	{
		return "create table " + getTableName() + " ( _id integer primary key, " +
			"latitude integer, longitude integer, accuracy integer, time integer, type integer, name text, alarm_enabled integer, scanningUID text )";
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
		db.execSQL("delete from " + getTableName() + " where alarm_enabled=0");
	}
	
	public void insertItem(LocationItem item)
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

	public void updateItem(LocationItem item)
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
	
	public LocationItem getLastGeocodedLocation(String scanningUID)
	{
		final LocationItem value[] = new LocationItem[1];
		
		final String query = String.format(Locale.US, "select * from %s where scanningUID=%s order by time desc limit 1",
			getTableName(), valutToSql(scanningUID));

		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				value[0] = new LocationItem(cr);
			}	
		};

		return value[0];
	}
	
	public List<LocationItem> getSortedByTime()
	{
		final List<LocationItem> list = new ArrayList<LocationItem>();
		
		final String query = String.format(Locale.US, "select * from %s order by time desc",
			getTableName());

		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				list.add(new LocationItem(cr));
			}
		};

		return list;
	}	
	
	public List<LocationItem> getVisited(String scanningUID)
	{
		final List<LocationItem> list = new ArrayList<LocationItem>();
		
		final String query = String.format(Locale.US, "select * from %s where scanningUID=%s",
			getTableName(), valutToSql(scanningUID));

		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				list.add(new LocationItem(cr));
			}
		};

		return list;
	}	

	public List<LocationItem> getNotVisited(String scanningUID)
	{
		final List<LocationItem> list = new ArrayList<LocationItem>();
		
		final String query = String.format(Locale.US, "select * from %s where scanningUID!=%s",
			getTableName(), valutToSql(scanningUID));

		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				list.add(new LocationItem(cr));
			}
		};

		return list;
	}	
	
    private Comparator<LocationItem> compareToLocationASC = new Comparator<LocationItem>()
	{
		@Override
		public int compare(LocationItem item1, LocationItem item2)
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
    
	private void sortByDistanceASC(GeoPosItem location, List<LocationItem> items)
	{
		//update each item distance for sorting comparator
		for (LocationItem item : items)
		{
			final double meters = location.distanceTo(item.getLat(), item.getLon());
			item.setDistance((int) meters);
		}
		
		Collections.sort(items, compareToLocationASC);
	}	
	
	public LocationItem getLocation(int lat, int lon)
	{
		final LocationItem value[] = new LocationItem[1];
		
		final String query = String.format(Locale.US, "select * from %s where latitude=%d and longitude=%d",
			getTableName(), lat, lon);

		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				value[0] = new LocationItem(cr);
			}	
		};

		return value[0];
	}
		
	public LocationItem getLocationItemById(long id)
	{
		final LocationItem value[] = new LocationItem[1];

		final String query = String.format(Locale.US, "select * from %s where _id=%d",
			getTableName(), id);

		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				value[0] = new LocationItem(cr);
			}
		};

		return value[0];
	}

	public List<LocationItem> getAlarmEnabled()
	{
		final List<LocationItem> list = new ArrayList<LocationItem>();

		final String query = String.format(Locale.US, "select * from %s where alarm_enabled=1",
			getTableName());
		
		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				list.add(new LocationItem(cr));
			}
		};

		return list;
	}	

	public List<LocationItem> getAll()
	{
		final List<LocationItem> list = new ArrayList<LocationItem>();

		final String query = String.format(Locale.US, "select * from %s",
			getTableName());
		
		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				list.add(new LocationItem(cr));
			}
		};

		return list;
	}	

	public List<LocationItem> getAlarmEnabledNotVisited(String scanningUID)
	{
		final List<LocationItem> list = new ArrayList<LocationItem>();
		
		final String query = String.format(Locale.US, "select * from %s where alarm_enabled=1 and scanningUID!=%s",
			getTableName(), valutToSql(scanningUID));

		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				list.add(new LocationItem(cr));
			}
		};
		
		return list;		
	}		

	public boolean isAllAlarmEnabledVisited(String scanningUID)
	{
		final int value[] = new int[2];
		
		final String query = String.format(Locale.US, "select count(*) as size from %s where alarm_enabled=1 and scanningUID!=%s",
			getTableName(), valutToSql(scanningUID));

		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				value[0] = cr.getInt(0);
				value[1] = 999;
			}
		};
		
		if (value[1] == 999)
			if (value[0] == 0)
				return true;
		
		return false;
	}
	
	public List<LocationItem> getAlarmEnabledNotVisited(GeoPosItem location, String scanningUID)
	{
		final List<LocationItem> list = new ArrayList<LocationItem>();
		
		final String query = String.format(Locale.US, "select * from %s where alarm_enabled=1 and scanningUID!=%s",
			getTableName(), valutToSql(scanningUID));

		new DataQuery(dm, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				list.add(new LocationItem(cr));
			}
		};

		sortByDistanceASC(location, list);
		
		return list;		
	}		
	
	private void insertOrUpdateLocation(String scanningUID, LocationItem location)
	{
		final LocationItem storedLocation = getLocation(location.getIntLat(), location.getIntLon());

		if (storedLocation == null)
		{
			//location not visited yet, log
			location.setScanningUID(scanningUID);

			insertItem(location);
		}
		else
		{
			//location already visited, update only for new scanning
			if (storedLocation.scanningUidEqual(scanningUID) == false)
			{
				storedLocation.setScanningUID(scanningUID);
				storedLocation.setTime(location.getTime());
				
				updateItem(storedLocation);
				
				onLocationVisit(storedLocation.getId());
			}
		}
	}

	private void updateLocation(String scanningUID, LocationItem location)
	{
		final LocationItem storedLocation = getLocation(location.getIntLat(), location.getIntLon());

		if (storedLocation != null)
		{
			//update for new scanning
			if (storedLocation.scanningUidEqual(scanningUID) == false)
			{
				storedLocation.setScanningUID(scanningUID);
				storedLocation.setTime(location.getTime());
				
				updateItem(storedLocation);
				
				onLocationVisit(storedLocation.getId());				
			}
		}
	}
		
	public void logLocation(SettingsManager settings, String scanningUID, LocationItem geocodedLocation)
	{
		resetVisitedLocationId();
		
		if (settings.isAddingLocationEnabled())
		{
			//insert/update each geocoded location
			insertOrUpdateLocation(scanningUID, geocodedLocation);
		}
		else
		{
			if (settings.valueScanning.isStartLocationSet())
			{
				//update each new visited location
				updateLocation(scanningUID, geocodedLocation);				
			}
			else
			{
				//insert/update only first geocoded location
				insertOrUpdateLocation(scanningUID, geocodedLocation);					
			}
		}
	}
	
	private void onLocationVisit(long locationId)
	{
		this.visitedLocationId = locationId;
	}
	
	public void resetVisitedLocationId()
	{
		this.visitedLocationId = 0;
	}
	
	public long getVisitedLocationId()
	{
		return this.visitedLocationId;
	}

	public void setLocationVisited(SettingsManager settings, LocationItem location)
	{
		final LocationItem storedLocation = getLocation(location.getIntLat(), location.getIntLon());

		if (storedLocation != null)
		{
			final String scanningUID = settings.valueScanning.getUID();			
			
			//update for new scanning
			if (storedLocation.scanningUidEqual(scanningUID) == false)
			{
				storedLocation.setScanningUID(scanningUID);
				storedLocation.setTime(location.getTime());
				
				updateItem(storedLocation);
			}
		}
	}
	
}
