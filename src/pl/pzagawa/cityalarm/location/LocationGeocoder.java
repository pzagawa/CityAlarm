package pl.pzagawa.cityalarm.location;

import java.util.List;

import pl.pzagawa.cityalarm.data.DataModel;
import pl.pzagawa.cityalarm.data.geocoder.NodeItem;

public class LocationGeocoder
{
	private static final int KILOMETER = 1000;

	private static final int DISTANCE_METERS_FAR = 5 * KILOMETER;
	private static final int DISTANCE_METERS_MID = 4 * KILOMETER;
	private static final int DISTANCE_METERS_NEAR = 3 * KILOMETER;	
	private static final int DISTANCE_METERS_HERE = 1 * KILOMETER;	
	
	private LocationGeocoder()
	{
	}

	private static NodeItem getGeoNodeBySize(List<NodeItem> geoNodes, int distanceMeters, int nodeType)
	{
		for (NodeItem nodeItem : geoNodes)
		{
			if (nodeItem.getDistance() <= distanceMeters)
			{
				if (nodeItem.getNodeType() == nodeType)
					return nodeItem;				
			}
		}
		
		return null;
	}
	
	private static NodeItem getBestMatchGeoNode(List<NodeItem> geoNodes)
	{
		NodeItem result = null;
		
		if (geoNodes.size() > 0)
		{	
			result = getGeoNodeBySize(geoNodes, DISTANCE_METERS_FAR, NodeItem.TYPE_CITY);
			
			if (result == null)
			{
				result = getGeoNodeBySize(geoNodes, DISTANCE_METERS_MID, NodeItem.TYPE_TOWN);
				
				if (result == null)
				{
					result = getGeoNodeBySize(geoNodes, DISTANCE_METERS_NEAR, NodeItem.TYPE_VILLAGE);
					
					if (result == null)
					{
						result = getGeoNodeBySize(geoNodes, DISTANCE_METERS_HERE, NodeItem.TYPE_SUBURB);
						
						if (result == null)
						{
							result = getGeoNodeBySize(geoNodes, DISTANCE_METERS_HERE, NodeItem.TYPE_LOCALITY);
						}
					}					
				}
			}
		}
		
		return result;
	}

	public static LocationItem coordsToAddress(DataModel dm, ScannedItem location)
	{
		//get 5KM distance radius geo node locations
		final double distanceRadiusKM = 5;

		final List<NodeItem> geoNodes = dm.dataNodes.getItems(location, distanceRadiusKM);
	
		//nodes are sorted by distance
		final NodeItem nodeItem = getBestMatchGeoNode(geoNodes);
		
		if (nodeItem == null)
			return null;
			
		return new LocationItem(location, nodeItem);
	}	
	
}
