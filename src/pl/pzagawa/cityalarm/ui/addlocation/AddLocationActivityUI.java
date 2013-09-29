package pl.pzagawa.cityalarm.ui.addlocation;

import pl.pzagawa.cityalarm.AddLocationActivity;
import pl.pzagawa.cityalarm.R;
import pl.pzagawa.cityalarm.location.ScannedItem;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class AddLocationActivityUI
{
	private final AddLocationActivity activity;

	private final Handler handler = new Handler();	
	
	private final ListView locationsList;	
	private final TextView textLocationsHeader;
	private final TextView textLocationsHeaderDetails;	
	private final EditText editLocationName;
	
	private boolean isLocationWait = false;
	
	private volatile ScannedItem location;
	private Object locationLock = new Object();
	
	public AddLocationActivityUI(AddLocationActivity activity)
	{
		this.activity = activity;
		
		this.locationsList = (ListView) activity.findViewById(R.id.locationsList);
		this.textLocationsHeader = (TextView) activity.findViewById(R.id.textLocationsHeader);
		this.textLocationsHeaderDetails = (TextView) activity.findViewById(R.id.textLocationsHeaderDetails);
		this.editLocationName = (EditText) activity.findViewById(R.id.editLocationName);
				
		editLocationName.addTextChangedListener(textWatcher);
	}
	
	private TextWatcher textWatcher = new TextWatcher()
	{
		public void afterTextChanged(Editable arg0)
		{
			handler.removeCallbacks(runUpdateList);
			handler.postDelayed(runUpdateList, 100);
		}
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
		{
		}
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
		{
		}				
	};
	
	private Runnable runUpdateList = new Runnable()
	{
		@Override
		public void run()
		{
			updateList();
		}
	};
	
	public void setLocation(ScannedItem location)
	{
		synchronized (locationLock)
		{
			this.location = location;
		}		
	}
	
	private ScannedItem getLocation()
	{
		synchronized (locationLock)
		{
			return location;
		}
	}
	
	public void setLocationWait(boolean enable)
	{
		isLocationWait = enable;
		
		updateHeaderText();
	}
	
	private void updateHeaderText()
	{
		textLocationsHeaderDetails.setText("");

		String textTitle = activity.getString(R.string.title_activity_add_location_label);
		
		if (isLocationWait)
			textTitle = activity.getString(R.string.location_wait_message);

		textLocationsHeader.setText(textTitle);
		
		if (locationsList.getAdapter() != null)
		{
			final int size = locationsList.getAdapter().getCount();
			
			if (size >  0)
				textLocationsHeaderDetails.setText(Integer.toString(size));
		}
	}
	
	private String getText()
	{
		return editLocationName.getText().toString().trim();
	}
	
	public void update()
	{
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				updateUI();
			}
		});
	}
		
	private void updateList()
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						final String textName = getText();

						final AddLocationListAdapter adapter = new AddLocationListAdapter(activity, textName, getLocation());

				        locationsList.setAdapter(adapter);

				        updateHeaderText();						
					}
				});				
			}			
		});

		thread.start();
	}

	private void updateUI()
	{
		updateList();
	}

}
