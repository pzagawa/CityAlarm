package pl.pzagawa.cityalarm;

import pl.pzagawa.cityalarm.location.ScannedItem;
import pl.pzagawa.cityalarm.location.scanner.LocationScanner;
import pl.pzagawa.cityalarm.ui.CommonActivity;
import pl.pzagawa.cityalarm.ui.addlocation.AddLocationActivityUI;
import android.os.Bundle;

public class AddLocationActivity
	extends CommonActivity
{
	private AddLocationActivityUI uiController = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_location);

        this.uiController = new AddLocationActivityUI(this);
		
        waitForLocation();
    }
    
	@Override
	protected void onResume()
	{
		uiController.update();		
		
		super.onResume();
	}
	
	private void waitForLocation()
	{
		uiController.setLocationWait(true);
		
		final LocationScanner locationScanner = new LocationScanner(this)
		{
			@Override
			public void begin()
			{
			}
	
			@Override
			public void finish(ScannedItem scannedItem)
			{
				uiController.setLocation(scannedItem);
				uiController.setLocationWait(false);
				uiController.update();				
			}		
		};

		locationScanner.start(false);
	}
	
}
