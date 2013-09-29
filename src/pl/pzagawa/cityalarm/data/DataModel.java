package pl.pzagawa.cityalarm.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.pzagawa.cityalarm.CityAlarmApplication;
import pl.pzagawa.cityalarm.location.GeoPosItem;
import pl.pzagawa.cityalarm.location.LocationItem;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataModel
	extends SQLiteOpenHelper
{
	private final static String DB_NAME = "data.sqlite";
	
	public final DataNodesManager dataNodes;
	public final DataScannedLocationsManager dataScannedLocations;
	public final DataGeocodedLocationsManager dataGeocodedLocations;
	public final DataAlarmsManager dataAlarms;
	public final DataNotificationsManager dataNotifications;
	public final DataDirectionsManager dataDirections;
	
	public DataModel()
	{
		super(CityAlarmApplication.getContext(), DB_NAME, null, 1);

		this.dataNodes = new DataNodesManager(this);
		this.dataScannedLocations = new DataScannedLocationsManager(this);
		this.dataGeocodedLocations = new DataGeocodedLocationsManager(this);
		this.dataAlarms = new DataAlarmsManager(this);
		this.dataNotifications = new DataNotificationsManager(this);
		this.dataDirections = new DataDirectionsManager(this);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2)
	{
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		dataNodes.createTable(db);
		dataScannedLocations.createTable(db);
		dataGeocodedLocations.createTable(db);
		dataAlarms.createTable(db);
		dataNotifications.createTable(db);
		dataDirections.createTable(db);
	}

	public Integer getSingleIntegerValue(String query)
	{
		final Integer value[] = new Integer[1];
		
		new DataQuery(this, query)
		{
			@Override
			public void onNextCursorPosition(Cursor cr)
			{
				value[0] = cr.getInt(0);
			}	
		};
		
		return value[0];
	}
	
	//get distances from one, current location, map items by location_id
	public Map<Long, Integer> getDistancesForLocations(List<LocationItem> list, GeoPosItem location)
	{
		final Map<Long, Integer> map = new HashMap<Long, Integer>();
	
		if (location != null)
		{
			final double lat = location.getLat();
			final double lon = location.getLon();
			
			for(LocationItem item : list)
			{
				final double meters = item.distanceTo(lat, lon);
		        map.put(item.getId(), (int) meters);
			}
		}

		return map;
	}

	public void cleanupTables()
	{
		final SQLiteDatabase db = getWritableDatabase();
		
		try
		{
			db.beginTransaction();
			
			dataGeocodedLocations.truncate(db);
			dataScannedLocations.truncate(db);
			dataAlarms.truncate(db);
			dataNotifications.truncate(db);
			dataDirections.truncate(db);

			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
			db.close();
		}
	}	

}
