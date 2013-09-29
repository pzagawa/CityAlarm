package pl.pzagawa.cityalarm.ui.main;

import java.util.List;

import pl.pzagawa.cityalarm.CityAlarmApplication;
import pl.pzagawa.cityalarm.data.DataModel;
import pl.pzagawa.cityalarm.data.LocationListProcessor;
import pl.pzagawa.cityalarm.directions.DirectionItem;
import pl.pzagawa.cityalarm.directions.LocationDirection;
import pl.pzagawa.cityalarm.location.GeoPosItem;
import pl.pzagawa.cityalarm.location.LocationItem;
import pl.pzagawa.cityalarm.settings.SettingsManager;

public class LocationListData
{	
	public static class ItemData
	{
		LocationItem locationItem;
		boolean locationVisited;
	    int alarmItemsCount;
	    boolean isDistanceAvailable;
	    int distance;
	    int direction;
	    boolean isStopLocation;
	};
	
	private final DataModel dm;
	private final SettingsManager settings;
	
	private final GeoPosItem scannedLocation;
	private final LocationListProcessor listProcessor;
			
	public final String scanningUID;
	public final boolean scanningEnabled;
	
	public final GeoPosItem geocodedLocation;
	
	private final List<LocationItem> list;
	
	private final ItemData itemData = new ItemData();
	
	public LocationListData()
	{
	    this.dm = new DataModel();
		this.settings = CityAlarmApplication.getSettingsManager();
	    
	    this.scannedLocation = dm.dataScannedLocations.getLastScannedLocation();

		this.scanningUID = settings.valueScanning.getUID();
		this.scanningEnabled = settings.valueScanning.isEnabled();

	    this.geocodedLocation = dm.dataGeocodedLocations.getLastGeocodedLocation(scanningUID);
	    
		this.listProcessor = new LocationListProcessor(dm, settings, scannedLocation);

	    this.list = listProcessor.getList();
	}
		
	public ItemData getItemData(int index)
	{
		final LocationItem location = list.get(index);
		
		final long locationId = location.getId();
		
		final Integer distance = listProcessor.getDistance(locationId);
		
		final DirectionItem directionItem = listProcessor.getDirection(locationId);

		//set item data		
		itemData.locationItem = location;

		itemData.locationVisited = (location.scanningUidEqual(scanningUID));

		itemData.alarmItemsCount = listProcessor.getAlarmsCount(locationId);

		itemData.isDistanceAvailable = (distance != null);

		itemData.distance = (distance == null) ? 0 : distance;

		itemData.direction = (directionItem == null) ? LocationDirection.IN_PLACE : directionItem.getDirection();
		
		itemData.isStopLocation = (settings.valueScanning.getStopLocationId() == locationId);

	    return itemData;
	}
	
	public int getListSize()
	{
		return list.size();
	}
	
	public LocationItem getLocationItem(int position)
	{
		return list.get(position);
	}

	public LocationItem getLocationItemById(long id)
	{
		return LocationItem.getItemById(list, id); 
	}

	public void enableLocationItemAlarm(long id, boolean enabled)
	{
		final LocationItem item = getLocationItemById(id); 

		item.setAlarmEnabled(enabled);

		dm.dataGeocodedLocations.updateItem(item);
	}

	public int getLastLocationPosition()
	{
		if (geocodedLocation != null)
		{
			for (int index = 0; index < getListSize(); index++)
			{
				final LocationItem locationItem = getLocationItem(index);
				
				if (locationItem.getId() == geocodedLocation.getId())
					return index;
			}
		}
		
		return -1;
	}

	public int getAlarmLocationPosition()
	{
		for (int index = 0; index < getListSize(); index++)
		{			
			final LocationItem locationItem = getLocationItem(index);
			
			final int alarmItemsCount = listProcessor.getAlarmsCount(locationItem.getId()); 

			final boolean isLocationVisited = locationItem.scanningUidEqual(scanningUID);
			
			if (alarmItemsCount > 0)
			{
				if (isLocationVisited)
					break;
				
				return index;
			}
		}
		
		return -1;
	}
	
	public int getActiveLocationPosition()
	{
		//check if there is location with alarm
		final int alarmLocationIndex = getAlarmLocationPosition();
		
		if (alarmLocationIndex == -1)
		{
			//if not, get last visited location
			return getLastLocationPosition();
		}
		
		return alarmLocationIndex;
	}
	
}
