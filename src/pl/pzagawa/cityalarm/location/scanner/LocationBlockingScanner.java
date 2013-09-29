package pl.pzagawa.cityalarm.location.scanner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import pl.pzagawa.cityalarm.location.ScannedItem;

import android.content.Context;

public class LocationBlockingScanner
	extends LocationScanner
{
	private CountDownLatch locationScannerLock;
	private ScannedItem scannedItem = null;
	
	public LocationBlockingScanner(Context context)
	{
		super(context);
	}

	@Override
	public void begin()
	{
		scannedItem = null;
	}

	@Override
	public void finish(ScannedItem scannedItem)
	{
		if (locationScannerLock.getCount() == 0)
			return;
		
		this.scannedItem = scannedItem;
		
		locationScannerLock.countDown();
	}

	public boolean startAndWaitForResult(boolean waitLong)
	{
		this.locationScannerLock = new CountDownLatch(1);
		
        if (isAnyProviderEnabled())
        {
        	start(waitLong);
    		
    		try
			{
        		//wait for location scanner result
    			locationScannerLock.await(2, TimeUnit.MINUTES);
    			
    			return true;
			}
    		catch (InterruptedException e)
			{
    			return false;
			}
        }
        else
        {
        	//location service is turned off
			return false;
        }
	}
	
	public ScannedItem getLocation()
	{	
		return scannedItem;
	}

}
