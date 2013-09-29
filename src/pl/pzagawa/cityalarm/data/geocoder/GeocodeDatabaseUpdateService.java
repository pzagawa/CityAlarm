package pl.pzagawa.cityalarm.data.geocoder;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import pl.pzagawa.cityalarm.AppEvent;
import pl.pzagawa.cityalarm.CityAlarmApplication;
import pl.pzagawa.cityalarm.MainActivity;
import pl.pzagawa.cityalarm.R;
import pl.pzagawa.cityalarm.data.DataModel;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

public class GeocodeDatabaseUpdateService
	extends IntentService
{
	private static final int ONGOING_NOTIFICATION = 1;
	private static final int READ_BUFFER_SIZE = 16 * 1024; 
	
	private String fileName = "";
	private Notification notification = null;

	private NodeItem nodeItem = new NodeItem();	
	
	public GeocodeDatabaseUpdateService()
	{
		super("GeocodeDatabaseUpdateService");
	}

	private void sendEvent(AppEvent appEvent)
	{
		final Intent intent = appEvent.toIntent(this);
		sendBroadcast(intent);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		this.fileName = GeocodeDatabaseFile.DATA_FILENAME;

		sendEvent(AppEvent.IMPORT_GEOCODEDB_START);

	    final int result = super.onStartCommand(intent, flags, startId);
		
		//start service as foreground
		this.notification = new Notification(R.drawable.ic_launcher, getText(R.string.geocode_db_update), System.currentTimeMillis());
		
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);		
		notification.setLatestEventInfo(this, getText(R.string.title_activity_main), getText(R.string.geocode_db_update), pendingIntent);

		startForeground(ONGOING_NOTIFICATION, notification);

		return result;
	}

	@Override
	public void onDestroy()
	{
		sendEvent(AppEvent.IMPORT_GEOCODEDB_STOP);
		
		stopForeground(true);

		super.onDestroy();
	}
	
	@Override
	protected void onHandleIntent(Intent intent)
	{		
		final DataModel dm = new DataModel();
		final SQLiteDatabase db = dm.getWritableDatabase();

		String errorMessage = null;
		DataInputStream dis = null;
		
    	try
		{
    		db.beginTransaction();
    		
    		dis = new DataInputStream(getAssets().open(fileName));    		

    		BufferedReader br = new BufferedReader(new InputStreamReader(dis), READ_BUFFER_SIZE);

			String line;
	  
			while ((line = br.readLine()) != null)
			{
				final String[] items = line.split(";");
				
				nodeItem.parse(items);
				
				dm.dataNodes.insertItem(db, nodeItem);
			}

			dm.dataNodes.createTableIndexes(db);
			
			db.setTransactionSuccessful();

			CityAlarmApplication.getSettingsManager().valueGeoData.setReady();
		}
		catch (Exception e)
		{
			errorMessage = e.getMessage();
		}
		finally
		{
			db.endTransaction();
			db.close();

			if (dis != null)
			{
				try
				{
					dis.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			if (errorMessage == null)
			{
				sendEvent(AppEvent.GEOCODEDB_READY);
			}
			else
			{
				AppEvent.IMPORT_GEOCODEDB_ERROR.setText(errorMessage);
				sendEvent(AppEvent.IMPORT_GEOCODEDB_ERROR);	
			}			
		}
	}

}
