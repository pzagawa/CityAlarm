package pl.pzagawa.cityalarm.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class DataQuery
{
	public DataQuery(DataModel dm, String query)
	{
		try
		{
			final SQLiteDatabase db = dm.getReadableDatabase();
			
			try
			{
				final Cursor cr = db.rawQuery(query, null);

				if (cr != null && cr.getCount() > 0)
				{
					cr.moveToFirst();
					
					while (!cr.isAfterLast())
					{
						onNextCursorPosition(cr);
						cr.moveToNext();
					}					
				}

				if (cr != null)
					cr.close();			
			}
			finally
			{
				db.close();
			}			
		}
		catch(Exception e)
		{
			return;
		}		
	}

	public abstract void onNextCursorPosition(final Cursor cr);

}
