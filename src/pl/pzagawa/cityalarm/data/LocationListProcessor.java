package pl.pzagawa.cityalarm.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.pzagawa.cityalarm.directions.DirectionItem;
import pl.pzagawa.cityalarm.location.GeoPosItem;
import pl.pzagawa.cityalarm.location.LocationItem;
import pl.pzagawa.cityalarm.settings.SettingsManager;

public class LocationListProcessor
{
	private final DataModel dm;
	
	private final List<LocationItem> allItems;
	
	private final Map<Long, Integer> mapAlarmsCount;
	private final Map<Long, Integer> mapDistances;
	private final Map<Long, DirectionItem> mapDirections;

	private final String scanningUID;
	private final boolean scanningEnabled;
	
	private final List<LocationItem> list;
	
	public LocationListProcessor(DataModel dm, SettingsManager settings, GeoPosItem scannedLocation)
	{
	    this.dm = dm;

		this.scanningUID = settings.valueScanning.getUID();
		this.scanningEnabled = settings.valueScanning.isEnabled();

	    this.allItems = dm.dataGeocodedLocations.getSortedByTime();
	    
		this.mapAlarmsCount = dm.dataAlarms.getAlarmCountForLocations(allItems);
		this.mapDistances = dm.getDistancesForLocations(allItems, scannedLocation);
		this.mapDirections = dm.dataDirections.getDirectionItemsForLocations(allItems);
	
		this.list = createList(scannedLocation);
	}

	public int getAlarmsCount(long locationId)
	{
		return mapAlarmsCount.containsKey(locationId) ? mapAlarmsCount.get(locationId) : 0;
	}

	public Integer getDistance(long locationId)
	{
		return mapDistances.containsKey(locationId) ? mapDistances.get(locationId) : null;
	}
	
	public DirectionItem getDirection(long locationId)
	{
		return mapDirections.containsKey(locationId) ? mapDirections.get(locationId) : null;		
	}

	public List<LocationItem> getList()
	{
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
	
	private void sortByDistanceDESC(GeoPosItem location, List<LocationItem> items)
	{
		//update each item distance for sorting comparator
		for (LocationItem item : items)
		{
			final double meters = location.distanceTo(item.getLat(), item.getLon());
			item.setDistance((int) meters);
		}
		
		Collections.sort(items, compareToLocationDESC);
	}	

	private List<LocationItem> createList(GeoPosItem scannedLocation)
	{
	    if (scanningEnabled && (scannedLocation != null))
	    {
		    return getSortedByDistance(scannedLocation, scanningUID);
	    }
	    
	    return dm.dataGeocodedLocations.getSortedByTime();
	}

	private boolean isAlarmLocationMovingAway(long locationId)
	{
		if (getAlarmsCount(locationId) >= 3)
		{
			final DirectionItem directionItem = getDirection(locationId);
			
			if (directionItem != null && directionItem.isDirectionMovingAway())
			{
				return true;
			}
		}
		
		return false;
	}

	public List<LocationItem> getSortedByDistance(GeoPosItem scannedLocation, String scanningUID)
	{
		final Set<Long> uniqueItemsIds = new HashSet<Long>(); 
		
		final List<LocationItem> result = new ArrayList<LocationItem>();
		
		//not visited
		final List<LocationItem> locationsNotVisited = dm.dataGeocodedLocations.getNotVisited(scanningUID);		

		//visited
		final List<LocationItem> locationsVisited = dm.dataGeocodedLocations.getVisited(scanningUID);

		//add top locations
		sortByDistanceDESC(scannedLocation, locationsNotVisited);
		
		for (LocationItem item : locationsNotVisited)
		{
			final long locationId = item.getId();
		
			if (isAlarmLocationMovingAway(locationId))
			{
				locationsVisited.add(item);
				continue;
			}
			
			result.add(item);			
			uniqueItemsIds.add(locationId);
		}		

		//add bottom locations
		sortByDistanceASC(scannedLocation, locationsVisited);
		
		for (LocationItem item : locationsVisited)
		{
			final long locationId = item.getId();
			
			if (uniqueItemsIds.contains(locationId) == false)
			{
				result.add(item);
			}
		}		
		
		return result;
	}
	
}
