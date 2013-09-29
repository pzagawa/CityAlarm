package pl.pzagawa.cityalarm.ui.main;

import pl.pzagawa.cityalarm.AppEvent;
import pl.pzagawa.cityalarm.CityAlarmApplication;
import pl.pzagawa.cityalarm.MainActivity;
import pl.pzagawa.cityalarm.R;
import pl.pzagawa.cityalarm.settings.SettingsManager;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

public class MainActivityUI
{
	private final MainActivity activity;

	private final Handler handler = new Handler();	
	
	private final LinearLayout llayLocationsList;
	private final LinearLayout llayGeocodeDbUpdate;
	private final LinearLayout llayBottomShadow;
	private final RelativeLayout rlayBottomControls;
	
	private final ListView locationsList;

	private final ToggleButton toggleScanLocations;
	private final Button btnAddLocation;
	
	private String titleExtension; 
		
	public MainActivityUI(MainActivity activity)
	{
		this.activity = activity;

		this.llayLocationsList = (LinearLayout) activity.findViewById(R.id.llayLocationsList);
		this.llayGeocodeDbUpdate = (LinearLayout) activity.findViewById(R.id.llayGeocodeDbUpdate);
		this.llayBottomShadow = (LinearLayout) activity.findViewById(R.id.llayBottomShadow);
		this.rlayBottomControls = (RelativeLayout) activity.findViewById(R.id.llayBottomControls);
				
		this.locationsList = (ListView) activity.findViewById(R.id.locationsList);
		this.toggleScanLocations = (ToggleButton) activity.findViewById(R.id.toggleScanLocations);
		this.btnAddLocation = (Button) activity.findViewById(R.id.btnAddLocation);
	}
	
	private CompoundButton.OnCheckedChangeListener onToggleScanLocations = new CompoundButton.OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			if (isChecked)
			{
				CityAlarmApplication.getAppEventManager().callEvent(AppEvent.ENABLE_LOCATION_SCANNING);
			}
			else
			{
				CityAlarmApplication.getAppEventManager().callEvent(AppEvent.DISABLE_LOCATION_SCANNING);
			}
		}
	};

	private View.OnClickListener onAddLocation = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			activity.showAddLocation();
		}		
	};
	
	private void updateTitle(String extension)
	{
		String title = activity.getString(R.string.title_activity_main); 
		
		if (extension == null)
		{
			activity.setTitle(title);			
		}
		else
		{
			if (extension.trim().length() == 0)
			{
				activity.setTitle(title);
			}
			else
			{
				activity.setTitle(title + ": " + extension);				
			}
		}
	}
	
	public void showScanningIcon(boolean enable)
	{
		if (enable)
		{
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					updateTitle(activity.getString(R.string.label_scanning));
				}
			});
		}
		else
		{
			handler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					updateTitle(titleExtension);
				}
			}, 1000);
		}
	}

	public void update()
	{
		activity.getDataStats().update();
		
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				updateUI();
			}
		});
	}
	
	public void setScanningDetailsText(String text)
	{
		updateTitle(text);
	}
	
	private void updateList()
	{
		//save list scroll position
		final int index = locationsList.getFirstVisiblePosition();
		View v = locationsList.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();
		
		//set new list data
		final LocationListAdapter listAdapter = new LocationListAdapter(activity);
		
        locationsList.setAdapter(listAdapter);
        
        //scroll to last location item
		final SettingsManager settings = CityAlarmApplication.getSettingsManager();
		
		if (settings.valueScanning.isEnabled())
		{
			if (listAdapter.getData().geocodedLocation == null)
			{
				//restore list scroll position
		        locationsList.setSelectionFromTop(index, top);
			}
			else
			{
				//scrol list to active location
				final int activeLocationIndex = listAdapter.getData().getActiveLocationPosition();
				
				if (activeLocationIndex != -1)
					locationsList.setSelectionFromTop(activeLocationIndex, 0);
			}
		}
		else
		{
			//scrol list to first location
	        locationsList.setSelectionFromTop(0, 0);
		}
	}
	
	private void updateUI()
	{
		final SettingsManager settings = CityAlarmApplication.getSettingsManager();
		
		//reset event listeners before state initialization
		toggleScanLocations.setOnCheckedChangeListener(null);
				
		if (settings.valueGeoData.isReady())
		{
			final boolean isScanningEnabled = settings.valueScanning.isEnabled();
			
			llayLocationsList.setVisibility(View.VISIBLE);
			llayGeocodeDbUpdate.setVisibility(View.GONE);
			llayBottomShadow.setVisibility(View.VISIBLE);
			rlayBottomControls.setVisibility(View.VISIBLE);
			
			locationsList.setEnabled(true);
			toggleScanLocations.setEnabled(true);

			updateList();

			toggleScanLocations.setChecked(isScanningEnabled);
			
			titleExtension = activity.getDataStats().getAverageSpeedKmText();
			
			updateTitle(titleExtension);
		}
		else
		{
			llayLocationsList.setVisibility(View.GONE);
			llayGeocodeDbUpdate.setVisibility(View.VISIBLE);
			llayBottomShadow.setVisibility(View.GONE);
			rlayBottomControls.setVisibility(View.GONE);
		
			showScanningIcon(false);
			
			locationsList.setEnabled(false);
			toggleScanLocations.setEnabled(false);
		}
				
		//set event listeners AFTER state initialization
		toggleScanLocations.setOnCheckedChangeListener(onToggleScanLocations);

		btnAddLocation.setOnClickListener(onAddLocation);
	}

}
