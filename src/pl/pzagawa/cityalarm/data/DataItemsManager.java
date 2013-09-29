package pl.pzagawa.cityalarm.data;

import android.database.sqlite.SQLiteDatabase;

public abstract class DataItemsManager
{
	protected final DataModel dm;
	
	public DataItemsManager(DataModel dm)
	{
		this.dm = dm;		
	}
	
	protected abstract String getTableName();
	protected abstract String getTableSchema();
	
	public void createTable(SQLiteDatabase db)
	{
		db.execSQL(getTableSchema());		
	}

	public abstract void truncate();
	
	protected String valutToSql(String value)
	{
		if (value == null)
			return "null";
		
		return "'" + value + "'";
	}

}
