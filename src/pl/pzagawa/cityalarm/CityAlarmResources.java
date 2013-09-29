package pl.pzagawa.cityalarm;

import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class CityAlarmResources
{
	public static class ListItemColors
	{
		public int title;
		public int distance;
		public int time;
	};
	
	private final Context context;	
	
	/*
	 * dateStrings[0] = TODAY_TEXT
	 * dateStrings[1] = YESTERDAY_TEXT
	 */	
	public final String[] dateText = new String[2];
	
	public final ListItemColors listItemColors = new ListItemColors();
	public final ListItemColors listItemColorsNotVisited = new ListItemColors();
	
	public final String textLocationWaiting;
	public final String textLocationNew;
	
	public final Locale locale;
	
	public final Drawable iconLocationStateNotVisited;
	public final Drawable iconLocationStateVisited;
	public final Drawable iconLocationStateCurrent;
	public final Drawable iconLocationStateAlarm;
	public final Drawable iconLocationStateAlarm2;
	public final Drawable iconLocationStateAlarm3;

	public final Drawable iconStopLocation;
	public final Drawable iconStopLocationInactive;
	
	public CityAlarmResources(Context context)
	{
		this.context = context;
		
		final Resources res = context.getResources(); 

		//date strings
		this.dateText[0] = res.getString(R.string.date_str_today);
		this.dateText[1] = res.getString(R.string.date_str_yesterday);

		//list item colors
		this.listItemColors.title= res.getColor(R.color.list_location_item_title_color); 
		this.listItemColors.distance = res.getColor(R.color.list_location_item_distance_color); 
		this.listItemColors.time = res.getColor(R.color.list_location_item_time_color);

		this.listItemColorsNotVisited.title= res.getColor(R.color.list_location_item_title_color_not_visited); 
		this.listItemColorsNotVisited.distance = res.getColor(R.color.list_location_item_distance_color_not_visited); 
		this.listItemColorsNotVisited.time = res.getColor(R.color.list_location_item_time_color_not_visited); 

		//strings
		this.textLocationWaiting = res.getString(R.string.location_list_item_waiting);
		this.textLocationNew = res.getString(R.string.location_list_item_new);
		
		//other
		this.locale = new Locale("pl", "PL");
		
		//location state icons
		this.iconLocationStateNotVisited = res.getDrawable(R.drawable.location_state_not_visited);
		this.iconLocationStateVisited = res.getDrawable(R.drawable.location_state_visited);
		this.iconLocationStateCurrent = res.getDrawable(R.drawable.location_state_current);
		this.iconLocationStateAlarm = res.getDrawable(R.drawable.location_state_alarm);
		this.iconLocationStateAlarm2 = res.getDrawable(R.drawable.location_state_alarm_2);
		this.iconLocationStateAlarm3 = res.getDrawable(R.drawable.location_state_alarm_3);
		
		//stop location icons
		this.iconStopLocation = res.getDrawable(R.drawable.stop_location);
		this.iconStopLocationInactive = res.getDrawable(R.drawable.stop_location_inactive);
	}

	private Uri getRawResource(int resId)
	{
		return Uri.parse("android.resource://" + context.getPackageName() + "/" + Integer.toString(resId));				
	}
	
	public Uri getSoundAlarm()
	{
		final boolean speechEnabled = CityAlarmApplication.getSettingsManager().valueSpeechNotify.isEnabled();
		
		if (speechEnabled)
		{
			return getRawResource(R.raw.alarm_short);
		}
		else
		{
			return getRawResource(R.raw.alarm_long);			
		}
	}

	public long getSoundAlarmDurationMillis()
	{
		return 1 * 1000;
	}

}
