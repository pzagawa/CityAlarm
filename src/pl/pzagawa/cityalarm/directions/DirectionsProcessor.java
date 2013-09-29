package pl.pzagawa.cityalarm.directions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import pl.pzagawa.cityalarm.data.DataModel;
import pl.pzagawa.cityalarm.location.LocationItem;
import pl.pzagawa.cityalarm.location.ScannedItem;
import pl.pzagawa.cityalarm.settings.SettingsManager;
import android.database.sqlite.SQLiteDatabase;

public class DirectionsProcessor
{
	private List<LocationItem> locations = null;
	private Map<Long, Integer> mapDistances = null;
	private Map<Long, DirectionItem> directions = null;
	
	private long stopLocationId = 0;
	
	private DirectionsProcessor()
	{	
	}
		
	private void process(DataModel dm, ScannedItem scannedLocation, String scanningUID)
	{
		this.locations = dm.dataGeocodedLocations.getAll();
		
		//map locations id keys to locations distances from scanned location
		this.mapDistances = dm.getDistancesForLocations(locations, scannedLocation);

		//map locations id keys to directions
		this.directions = dm.dataDirections.getDirectionItemsForLocations(locations);
		
		//process locations
		final SQLiteDatabase db = dm.getWritableDatabase();

		try
		{
			db.beginTransaction();

			processLocations(dm, db, scanningUID);

			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
			db.close();
		}
		
		this.stopLocationId = calcStopLocationId(scannedLocation);
	}
	
	private void processLocations(DataModel dm, SQLiteDatabase db, String scanningUID)
	{
		for (LocationItem locationItem : locations)
		{
			final long locationId = locationItem.getId();
			
			final boolean isLocationVisited = (locationItem.scanningUidEqual(scanningUID));
			
			final int currDistance = getCurrentDistance(locationId);
									
			final DirectionItem directionItem = getDirectionItem(locationId);
			
			if (isLocationVisited)
			{
				if (directionItem.isDirectionMovingAway() == false)
				{
					directionItem.setDirectionMovingAway();
					updateDirectionItem(dm, db, locationId, directionItem);
				}
			}
			else
			{
				directionItem.update(currDistance);
				updateDirectionItem(dm, db, locationId, directionItem);
			}
		}
	}	

	private void updateDirectionItem(DataModel dm, SQLiteDatabase db, long locationId, DirectionItem directionItem)
	{
		if (isDirectionItemExists(locationId))
		{
			dm.dataDirections.updateItem(db, directionItem);
		}
		else
		{
			dm.dataDirections.insertItem(db, directionItem);				
		}		
	}
	
	private int getCurrentDistance(long locationId)
	{
		if (mapDistances.containsKey(locationId))
		{
			return mapDistances.get(locationId);
		}
		else
		{
			return 0;
		}
	}
	
	private boolean isDirectionItemExists(long locationId)
	{
		return directions.containsKey(locationId);
	}

	private int getLocationDirection(long locationId)
	{
		if (directions.containsKey(locationId))
		{
			return directions.get(locationId).getDirection();
		}
		
		return LocationDirection.IN_PLACE;
	}
	
	private DirectionItem getDirectionItem(long locationId)
	{
		if (directions.containsKey(locationId))
		{
			return directions.get(locationId);
		}
		else
		{
			return new DirectionItem(locationId);
		}
	}
		
	private List<LocationItem> getLocationsForDirection(int direction)
	{
		final List<LocationItem> list = new ArrayList<LocationItem>();

		for (LocationItem item : locations)
		{
			if (item.isAlarmEnabled())
			{
				final long locationId = item.getId();
				
				if (getLocationDirection(locationId) == direction)
					list.add(item);
			}
		}
		
		return list;
	}
	
    private Comparator<LocationItem> compareToLocationDESC = new Comparator<LocationItem>()
	{
		@Override
		public int compare(LocationItem item1, LocationItem item2)
		{
			final double distance1 = item1.getDistance();
			final double distance2 = item2.getDistance();

			if (distance1 < distance2)
				return 1;

			if (distance1 > distance2)
				return -1;

			return 0;
		}
	};	
	
	private long calcStopLocationId(ScannedItem scannedLocation)
	{				
		final List<LocationItem> list = getLocationsForDirection(LocationDirection.GETTING_CLOSER);

		if (list.size() > 0)
		{		
			//update each item distance for sorting comparator
			for (LocationItem item : list)
			{
				final double meters = scannedLocation.distanceTo(item.getLat(), item.getLon());
				item.setDistance((int) meters);
			}		
			
			Collections.sort(list, compareToLocationDESC);
			
			return list.get(0).getId();
		}
		
		return 0;
	}

	private long getStopLocationId()
	{
		return stopLocationId;
	}
	
	public static void process(DataModel dm, SettingsManager settings, ScannedItem scannedLocation)
	{
		final String scanningUID = settings.valueScanning.getUID();
		
		final DirectionsProcessor directions = new DirectionsProcessor();
		
		directions.process(dm, scannedLocation, scanningUID);
		
		if (directions.getStopLocationId() != 0)
		{			
			settings.valueScanning.setStopLocationId(directions.getStopLocationId());
		}
	}
	
}
