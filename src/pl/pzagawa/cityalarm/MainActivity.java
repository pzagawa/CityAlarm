package pl.pzagawa.cityalarm;

import java.util.Observable;
import java.util.Observer;

import pl.pzagawa.cityalarm.data.DataStats;
import pl.pzagawa.cityalarm.ui.CommonActivity;
import pl.pzagawa.cityalarm.ui.main.MainActivityUI;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity
	extends CommonActivity
	implements Observer	
{
	private MainActivityUI uiController = null;
	private DataStats dataStats;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //setup view
        setContentView(R.layout.activity_main);
        
        this.uiController = new MainActivityUI(this);
		this.dataStats = new DataStats();

		showHintsOnStartup();
    }

	private void showHintsOnStartup()
    {
		//welcome hints
		if (showHintMessage(R.array.hint_welcome) == false)
		{
			if (Utils.isAppVersionBeta(this))
			{
			}
		}
    }

    public DataStats getDataStats()
    {
    	return dataStats;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	final MenuInflater inflater = getMenuInflater();
    	
    	inflater.inflate(R.menu.activity_main, menu);

    	final MenuItem itemSettings = menu.findItem(R.id.menu_settings);    	
    	final MenuItem itemAbout = menu.findItem(R.id.menu_about);
    	
    	itemSettings.setIcon(R.drawable.ic_menu_manage);
    	itemAbout.setIcon(R.drawable.ic_menu_info_details);
    	
    	itemSettings.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    	itemAbout.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
    	if (item.getItemId() == R.id.menu_settings)
    	{
    		showSettings();
    	}
    	
    	if (item.getItemId() == R.id.menu_about)
    	{
    		showAbout();
    	}
    	
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		//remove events target
        CityAlarmApplication.getAppEventManager().deleteObserver(this);
	}
    	
	@Override
	protected void onResume()
	{
		super.onResume();
				
		//set events target
        CityAlarmApplication.getAppEventManager().addObserver(this);

		uiController.update();
	}

	public void onNewAppEvent(AppEvent event)
	{
		if (event.equals(AppEvent.NEW_ROUTE))
		{
			uiController.update();
		}

		if (event.equals(AppEvent.NEW_SCANNED_LOCATION))
		{
			uiController.update();
		}
		
		if (event.equals(AppEvent.NEW_VISITED_LOCATION))
		{
			uiController.update();
		}

		if (event.equals(AppEvent.LOCATION_SCANNER_START))
		{
			uiController.showScanningIcon(true);
		}

		if (event.equals(AppEvent.LOCATION_SCANNER_STOP))
		{
			uiController.showScanningIcon(false);	
		}

		if (event.equals(AppEvent.ENABLE_LOCATION_SCANNING))
		{
			uiController.update();
			
			showHintMessage(R.array.hint_scanning);
		}

		if (event.equals(AppEvent.DISABLE_LOCATION_SCANNING))
		{
			uiController.showScanningIcon(false);

			uiController.update();
		}
		
		if (event.equals(AppEvent.TRIGGER_LOCATION))
		{
			uiController.update();
		}

		if (event.equals(AppEvent.GEOCODEDB_READY))
		{
			uiController.update();			
		}
	}

	@Override
	public void update(Observable source, Object data)
	{
		if (source == CityAlarmApplication.getAppEventManager())
		{
			onNewAppEvent((AppEvent) data);
		}
	}
	
}
