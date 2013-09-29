package pl.pzagawa.cityalarm.alarm;

import java.util.ArrayList;
import java.util.List;

import pl.pzagawa.cityalarm.data.DataModel;
import pl.pzagawa.cityalarm.location.LocationItem;
import pl.pzagawa.cityalarm.location.ScannedItem;
import pl.pzagawa.cityalarm.settings.SettingsManager;

public class LocationTrigger
{
	private LocationTrigger()
	{
	}

	public static List<LocationItem> getLocations(DataModel dm, SettingsManager settings, ScannedItem location)
	{
		final String scanningUID = settings.valueScanning.getUID();
		final double distanceMeters = settings.valueAlarmTrigger.getDistanceKM() * 1000f;
		
		final List<LocationItem> alarmLocations = dm.dataGeocodedLocations.getAlarmEnabledNotVisited(location, scanningUID);				
		
		final List<LocationItem> list = new ArrayList<LocationItem>();  

		for(LocationItem alarmLocation : alarmLocations)
		{
			if (alarmLocation.getDistance() < distanceMeters)
			{
				list.add(alarmLocation);
			}
		}
		
		return list;
	}
	
	public static void logAlarm(DataModel dm, LocationItem location)
	{
		final long locationId = location.getId();
		
		final AlarmItem alarmItem = new AlarmItem(locationId);

		dm.dataAlarms.insertItem(alarmItem);
	}
	
}
