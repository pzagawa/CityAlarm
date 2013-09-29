package pl.pzagawa.cityalarm.ui.main;

import pl.pzagawa.cityalarm.CityAlarmApplication;
import pl.pzagawa.cityalarm.CityAlarmResources;
import pl.pzagawa.cityalarm.CityAlarmResources.ListItemColors;
import pl.pzagawa.cityalarm.R;
import pl.pzagawa.cityalarm.Utils;
import pl.pzagawa.cityalarm.data.DataStats;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class LocationListItemView
{
	private final LocationListData data;
	private final View view;

	private final ImageView iconStopLocation;
	private final ImageView iconLocationStatus;
    
    private final TextView textLocationTitle;
    private final TextView textLocationDistance;
    private final TextView textLocationTime;
    private final ToggleButton btnItemAlarm;
	    
    private LocationListItemView(LocationListData data, View view)
    {
    	this.data = data;
    	this.view = view;
    	
    	this.iconStopLocation = (ImageView) view.findViewById(R.id.iconStopLocation);
    	this.iconLocationStatus = (ImageView) view.findViewById(R.id.iconLocationStatus);
    	
        this.textLocationTitle = (TextView) view.findViewById(R.id.textLocationTitle);
        this.textLocationDistance = (TextView) view.findViewById(R.id.textLocationDistance);
        this.textLocationTime = (TextView) view.findViewById(R.id.textLocationTime);
        this.btnItemAlarm = (ToggleButton) view.findViewById(R.id.btnItemAlarm);
        
        //store reusable object in view's tag
        view.setTag(this);
    }
    
    public View getView()
    {
    	return view;
    }
    
    public static LocationListItemView get(LocationListData data, LayoutInflater inflater, View convertView, ViewGroup parent)
    {
    	LocationListItemView itemView;
    	
        if (convertView == null)
        {
        	View view = (LinearLayout)inflater.inflate(R.layout.location_list_item, parent, false);        	
        	itemView = new LocationListItemView(data, view);
        }
        else
        {
            //restore reusable object from given view's tag
        	itemView = (LocationListItemView) convertView.getTag();        	
        }
        
        return itemView;
    }
    
    private void updateTextColors(LocationListData.ItemData itemData)
    {
        ListItemColors itemColors = CityAlarmApplication.getAppResources().listItemColorsNotVisited;
        
        if (itemData.locationVisited || (itemData.alarmItemsCount > 0))
        {
            itemColors = CityAlarmApplication.getAppResources().listItemColors;
        }

    	textLocationTitle.setTextColor(itemColors.title);
    	textLocationDistance.setTextColor(itemColors.distance);
    	textLocationTime.setTextColor(itemColors.time);    	
    }
    
    private void updateStopIcon(LocationListData.ItemData itemData)
    {
    	final CityAlarmResources appRes = CityAlarmApplication.getAppResources();
    	
    	if (itemData.isStopLocation)
    	{
    		iconStopLocation.setImageDrawable(appRes.iconStopLocation);
    	}
    	else
    	{
    		iconStopLocation.setImageDrawable(appRes.iconStopLocationInactive);
    	}
    }
    
    private void updateStatusIcon(LocationListData.ItemData itemData)
    {
    	final CityAlarmResources appRes = CityAlarmApplication.getAppResources();
    	
    	iconLocationStatus.setImageDrawable(appRes.iconLocationStateNotVisited);
    	
        final long lastLocationId = (data.geocodedLocation == null) ? 0 : data.geocodedLocation.getId(); 

		if (itemData.locationVisited)
		{
			if (lastLocationId == itemData.locationItem.getId())
			{
				if (data.scanningEnabled)
				{
			    	iconLocationStatus.setImageDrawable(appRes.iconLocationStateCurrent);
				}
				else
				{
			    	iconLocationStatus.setImageDrawable(appRes.iconLocationStateVisited);
				}
			}
			else
			{
		    	iconLocationStatus.setImageDrawable(appRes.iconLocationStateVisited);
			}
		}
		else
		{
			if (itemData.alarmItemsCount == 0)
			{
		    	iconLocationStatus.setImageDrawable(appRes.iconLocationStateNotVisited);
			}
			else
			{
				if (itemData.alarmItemsCount == 1)
			    	iconLocationStatus.setImageDrawable(appRes.iconLocationStateAlarm);

				if (itemData.alarmItemsCount == 2)
			    	iconLocationStatus.setImageDrawable(appRes.iconLocationStateAlarm2);
				
				if (itemData.alarmItemsCount >= 3)
			    	iconLocationStatus.setImageDrawable(appRes.iconLocationStateAlarm3);
			}
		}
    }
    
    private void updateTextName(LocationListData.ItemData itemData)
    {
    	final String name = itemData.locationItem.getName();    	
        textLocationTitle.setText(name);
    }
    
    private void updateTextDetails(LocationListData.ItemData itemData, DataStats dataStats)
    {        
        if (data.scanningEnabled)
        {
        	//set distance
            textLocationDistance.setVisibility(View.VISIBLE);
            
        	if (itemData.isDistanceAvailable)
        	{
                String textDistance = Utils.distanceToString(itemData.distance);
                textLocationDistance.setText(textDistance);
        	}
        	else
        	{
                textLocationDistance.setText(CityAlarmApplication.getAppResources().textLocationWaiting);
        	}

        	//set time        	
            String textTime = "";
    		
    		if (itemData.locationVisited)
    		{
                textTime = itemData.locationItem.getNiceTimeString();
    		}
    		else
    		{    			    			
    			textTime = dataStats.getTextTimeToArriveForDistance(itemData.distance);
    		}

    		textLocationTime.setText(textTime);
        }
        else
        {
        	//set distance
            textLocationDistance.setVisibility(View.GONE);
        	
            String textTime = "";

            //set time
    		if (itemData.locationVisited)
    		{
	            textTime = itemData.locationItem.getNiceTimeString();            
    		}
    		else
    		{
    			textTime = CityAlarmApplication.getAppResources().textLocationNew;
    		}

    		textLocationTime.setText(textTime);    			
        }
    }
    
    private void updateButton(LocationListData.ItemData itemData)
    {
        btnItemAlarm.setOnCheckedChangeListener(null);
        btnItemAlarm.setChecked(itemData.locationItem.isAlarmEnabled());
		btnItemAlarm.setOnCheckedChangeListener(onToggleAlarm);
        btnItemAlarm.setTag(itemData.locationItem.getId());    	
    }

	private CompoundButton.OnCheckedChangeListener onToggleAlarm = new CompoundButton.OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			final ToggleButton btnItemAlarm = (ToggleButton) buttonView;

			//handling object reference by id, because list can be reloaded 
			final Long locationItemId = (Long) btnItemAlarm.getTag();

			data.enableLocationItemAlarm(locationItemId, isChecked);
		}
	};

	public void update(int position, DataStats dataStats)
	{
		final LocationListData.ItemData itemData = data.getItemData(position);

		updateStopIcon(itemData);
		updateStatusIcon(itemData);
		updateTextColors(itemData);
		updateTextName(itemData);
		updateTextDetails(itemData, dataStats);
		updateButton(itemData);		
	}
    
}
