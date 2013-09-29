package pl.pzagawa.cityalarm.ui.main;

import pl.pzagawa.cityalarm.MainActivity;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class LocationListAdapter
	implements ListAdapter
{	
	private final MainActivity activity;
	private final LayoutInflater inflater;
	private final LocationListData data;
	
	public LocationListAdapter(MainActivity activity)
	{
		this.activity = activity;
	    this.inflater = activity.getLayoutInflater();
	    this.data = new LocationListData();
	}
	
	public LocationListData getData()
	{
		return data;
	}
		
	public int getCount()
	{
		return data.getListSize();
	}

	public Object getItem(int position)
	{
		return data.getLocationItem(position);
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
        final LocationListItemView itemView = LocationListItemView.get(data, inflater, convertView, parent);
        
        itemView.update(position, activity.getDataStats());
        
        return itemView.getView();
	}
	
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
		return (data.getListSize() == 0);
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
