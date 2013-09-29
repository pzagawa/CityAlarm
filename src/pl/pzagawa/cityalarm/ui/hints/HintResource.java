package pl.pzagawa.cityalarm.ui.hints;

import pl.pzagawa.cityalarm.CityAlarmApplication;
import pl.pzagawa.cityalarm.HintMessageActivity;
import pl.pzagawa.cityalarm.Utils;
import pl.pzagawa.cityalarm.settings.SettingsManager;
import android.content.Context;
import android.content.Intent;

public class HintResource
{
	private static final int INDEX_ID = 0;
	private static final int INDEX_HEADER = 1;
	private static final int INDEX_CONTENT = 2;
	
	private final Context context;
	private final String[] hintData;
	private final boolean allowDisable;

	public HintResource(Context context, int resourceId, boolean allowDisable)
	{
		this.context = context;
        this.hintData = context.getResources().getStringArray(resourceId);
        this.allowDisable = allowDisable;
	}
	
	public boolean allowDisable()
	{
		return allowDisable;		
	}

	public int getHintId()
	{
		return Integer.parseInt(hintData[INDEX_ID]);
	}
		
	public String getHeaderText()
    {
    	return hintData[INDEX_HEADER];
    }

	private String replaceVariables(String text)
    {
    	String s = text;

    	s = s.replace("%version%", Utils.getAppVersionText(context));

    	return s;
    }

	public String getContentText()
    {
    	final StringBuilder sb = new StringBuilder();
    	
    	for (int index = INDEX_CONTENT; index < hintData.length; index++)
    	{
    		sb.append(hintData[index]);
    		
    		if (index < (hintData.length - 1))
    		{    		
	    		sb.append("\n");
	    		sb.append("\n");
    		}
    	}
    	
    	return replaceVariables(sb.toString());
    }
	
	public static Intent toIntent(Context context, int hintResourceId, boolean allowDisable)
	{		
		final HintResource hintResource = new HintResource(context, hintResourceId, allowDisable);

		if (allowDisable)
		{
    		final SettingsManager settings = CityAlarmApplication.getSettingsManager();
    		
    		if (settings.valueHints.isHintItemDisabled(hintResource.getHintId()))
    		{
				return null;    			
    		}
		}
		
		final Intent intent = new Intent(context, HintMessageActivity.class);

		intent.putExtra("hintResourceId", hintResourceId);
		intent.putExtra("allowDisable", allowDisable);

		return intent;
	}

	public static HintResource fromIntent(Context context, Intent intent)
	{
	    final int hintResourceId = intent.getIntExtra("hintResourceId", 0);
	    final boolean allowDisable = intent.getBooleanExtra("allowDisable", true);

	    return new HintResource(context, hintResourceId, allowDisable);
	}
	
}
