package pl.pzagawa.cityalarm.ui.addlocation;

import java.util.List;

import pl.pzagawa.cityalarm.AddLocationActivity;
import pl.pzagawa.cityalarm.AppEvent;
import pl.pzagawa.cityalarm.CityAlarmApplication;
import pl.pzagawa.cityalarm.R;
import pl.pzagawa.cityalarm.Utils;
import pl.pzagawa.cityalarm.data.DataModel;
import pl.pzagawa.cityalarm.data.geocoder.NodeItem;
import pl.pzagawa.cityalarm.location.LocationItem;
import pl.pzagawa.cityalarm.location.ScannedItem;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

public class AddLocationListAdapter
	implements ListAdapter
{	
	private final AddLocationActivity activity;
	private final LayoutInflater inflater;
	private final DataModel dm;
	private final List<NodeItem> list;
	private final ScannedItem location;

	public AddLocationListAdapter(AddLocationActivity activity, String textName, ScannedItem location)
	{
		this.activity = activity;
	    this.inflater = activity.getLayoutInflater();
	    this.dm = new DataModel();
	    this.list = dm.dataNodes.getItemsByName(textName, location);
	    this.location = location;
	}
	
	public int getCount()
	{
		return list.size();
	}

	public Object getItem(int position)
	{
		return list.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public int getItemViewType(int position)
	{
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		//get views
		LinearLayout view;
        
        if (convertView == null)
        {
            view = (LinearLayout)inflater.inflate(R.layout.add_location_list_item, parent, false);
        } else {
        	view = (LinearLayout)convertView;
        }

        final TextView textLocationTitle = (TextView) view.findViewById(R.id.textLocationTitle);
        final TextView textLocationDetails = (TextView) view.findViewById(R.id.textLocationDetails);
        final Button btnAddLocation = (Button) view.findViewById(R.id.btnAddLocation);
        
        //get values
		final NodeItem nodeItem = list.get(position);

        textLocationTitle.setText(nodeItem.getName());
		
		if (location == null)
		{
	        textLocationDetails.setVisibility(View.GONE);
		}
		else
		{
			final double distance = location.distanceTo(nodeItem.getLat(), nodeItem.getLon());
	        String textDistance = Utils.distanceToString(distance);
	        textLocationDetails.setText(textDistance);
	        textLocationDetails.setVisibility(View.VISIBLE);
		}
		
        //update button
        btnAddLocation.setOnClickListener(onAddLocationListener);		
		btnAddLocation.setTag(nodeItem);		

        return view;
	}

	private View.OnClickListener onAddLocationListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View buttonView)
		{
			final Button btnAddLocation = (Button) buttonView;
			final NodeItem nodeItem = (NodeItem) btnAddLocation.getTag();

			final LocationItem item = new LocationItem(nodeItem);

			item.setAlarmEnabled(true);
			
			dm.dataGeocodedLocations.insertItem(item);

			CityAlarmApplication.getAppEventManager().callEvent(AppEvent.NEW_SCANNED_LOCATION);
			
			activity.finish();
		}		
	};
	
	public int getViewTypeCount()
	{
		return 1;
	}

	public boolean hasStableIds()
	{
		return true;
	}

	public boolean isEmpty()
	{
		return (list.size() == 0);
	}

	public void registerDataSetObserver(DataSetObserver observer)
	{
	}

	public void unregisterDataSetObserver(DataSetObserver observer)
	{
	}

	public boolean areAllItemsEnabled()
	{
		return true;
	}

	public boolean isEnabled(int arg0)
	{
		return true;
	}

}
