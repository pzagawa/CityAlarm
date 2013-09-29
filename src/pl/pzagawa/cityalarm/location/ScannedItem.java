package pl.pzagawa.cityalarm.location;

import android.content.ContentValues;
import android.database.Cursor;

public class ScannedItem
	extends GeoPosItem
{
	public ScannedItem(double lat, double lon, float accuracy)
	{
		super(lat, lon, accuracy);

	}
	
	public ScannedItem(Cursor cr)
	{
		super(cr);		
	}

	public ContentValues toContentValues()
	{
		ContentValues values = super.toContentValues();
		
		return values;
	}
	
}
