package pl.pzagawa.cityalarm.data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.pzagawa.cityalarm.CityAlarmApplication;
import pl.pzagawa.cityalarm.Utils;
import pl.pzagawa.cityalarm.location.ScannedItem;
import pl.pzagawa.cityalarm.settings.SettingsManager;

public class DataStats
{
	private final static int MAX_SAMPLES = 3;
	private final static int MIN_DISTANCE_METERS = 100;
	private final static int MAX_SPEED_INCREMENT = 2;
	
	private final static float ARRIVAL_TIME_CORRECTION = 1.2f;
	
	private final static DecimalFormat formatSpeed = new DecimalFormat("0km/h");
	
	private int metersPerSecond = 0;
	private float averageSpeedKM = 0;
	private boolean averageSpeedAvailable = false;
	
	public DataStats()
	{		
	}
	
	private List<ScannedItem> getScannedLocationsSamples(DataModel dm)
	{		
		final List<ScannedItem> result = new ArrayList<ScannedItem>();
			
		final SettingsManager settings = CityAlarmApplication.getSettingsManager();

		if (settings.valueScanning.isEnabled())
		{
			int samplesCounter = 0;
	
			final List<ScannedItem> scannedLocations = dm.dataScannedLocations.getLocations();
			
			for (ScannedItem locationItem : scannedLocations)
			{
				result.add(locationItem);
				samplesCounter++;
				
				if (samplesCounter > MAX_SAMPLES)
					break;
			}
		}

		if (settings.valueScanning.isEnabled() == false)
			result.clear();
		
		return result;
	}
	
	private List<Integer> getSpeedsMetersPerSecond(List<ScannedItem> list)
	{
		final Calendar calStartTime = CityAlarmApplication.getSettingsManager().valueScanning.getStartTime();

		final List<Integer> speedHistory = new ArrayList<Integer>();
		
		if (calStartTime == null)
			return speedHistory;	

		int speedSum = 0;
		int speedCount = 0;
		
		double lat = 0;
		double lon = 0;
		long prevTime = 0; 
		
		for(int index = 0; index < list.size(); index++)
		{
			final ScannedItem item = list.get(index);
			
			if (index > 0)
			{
				final double meters = item.distanceTo(lat, lon);

				if (meters <= MIN_DISTANCE_METERS)
				{
					speedHistory.add(0);
				}
				else
				{
					final int seconds = (int) ((prevTime - item.getTime()) / 1000);
		
					//calc speed
					if (seconds > 0)
					{
						int speed = (int) (meters / seconds);
						
						final int avgSpeed = (speedCount == 0) ? 0 : (speedSum / speedCount);
												
						//max speed guard and limiter
						if (avgSpeed > 0)
						{
							if (speed > (avgSpeed * MAX_SPEED_INCREMENT))
								speed = avgSpeed * MAX_SPEED_INCREMENT;
						}
						
						speedHistory.add(speed);
						
						speedSum += speed;
						speedCount++;			
					}
				}
			}
			
			lat = item.getLat();
			lon = item.getLon();
			prevTime = item.getTime();
		}

		return speedHistory;
	}
	
	private int getAverageSpeedMetersPerSecond(List<Integer> list)
	{
		int speed = 0;
		
		for (Integer item : list)
		{
			speed += item;			
		}
		
		if (list.size() > 0)
			return speed / list.size();
		
		return 0;		
	}

	private float metersPerSecondToKM(int speed)
	{
		return (float)(speed) * 3.6f;
	}
	
	public void update()
	{
		final DataModel dm = new DataModel();
		final SettingsManager settings = CityAlarmApplication.getSettingsManager();

		this.metersPerSecond = 0;
		this.averageSpeedKM = 0;
		this.averageSpeedAvailable = false;
		
		if (settings.valueGeoData.isReady())
		{
			if (settings.valueScanning.isEnabled())
			{
				//get average speed
				final List<ScannedItem> locations = getScannedLocationsSamples(dm);
				
				if (locations.size() >= 2)
				{
					//calc speed
					final List<Integer> speeds = getSpeedsMetersPerSecond(locations);
					
					this.metersPerSecond = getAverageSpeedMetersPerSecond(speeds);
					
					this.averageSpeedKM = metersPerSecondToKM(metersPerSecond);

					this.averageSpeedAvailable = true;														
				}
			}
		}		
	}

	public float getAverageSpeedKM()
	{
		return averageSpeedKM;
	}

	public String getAverageSpeedKmText()
	{
		if (averageSpeedAvailable)			
			return formatSpeed.format(averageSpeedKM);
		
		return "";
	}

	public boolean isAverageSpeedAvailable()
	{
		return averageSpeedAvailable;
	}

	public int getSecondsToArriveForDistance(double meters)
	{
		if (this.metersPerSecond == 0)
			return -1;
		
		return (int) (meters / (double)this.metersPerSecond);
	}
	
	public String getTextTimeToArriveForDistance(double meters)
	{
		int seconds = getSecondsToArriveForDistance(meters);

		if (seconds < 0)
			return "";
		
		seconds = (int) ((float)(seconds) * ARRIVAL_TIME_CORRECTION);

		final int hours = (int)((float)seconds / (float)3600.0f);
		final int minutes = (int)(((float)seconds / (float)60.0f) % 60);
				
		return Utils.getFutureFullTextTimeString(hours, minutes);
	}

}
