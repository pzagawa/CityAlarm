package pl.pzagawa.cityalarm;

import pl.pzagawa.cityalarm.settings.SettingsManager;
import pl.pzagawa.cityalarm.ui.CommonActivity;
import pl.pzagawa.cityalarm.ui.hints.HintResource;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class HintMessageActivity
	extends CommonActivity
{
	private TextView textHintHeader;
	private TextView textHintContent;
	private CheckBox checkDisableHint;

	private HintResource hintResource;
	
	private boolean isHintDisabled = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hint_message);
        
    	this.textHintHeader = (TextView) findViewById(R.id.textHintHeader);
    	this.textHintContent = (TextView) findViewById(R.id.textHintContent);
    	this.checkDisableHint = (CheckBox) findViewById(R.id.checkDisableHint);

    	//get hint data from resource
    	this.hintResource = HintResource.fromIntent(this, this.getIntent());
    	
    	if (hintResource.getHintId() == 0)
        {
        	finish();
        	return;
        }
    	
   		checkDisableHint.setEnabled(hintResource.allowDisable());
    	
        //setup events
    	this.checkDisableHint.setOnCheckedChangeListener(onDisableHint);
    }
    
    private CompoundButton.OnCheckedChangeListener onDisableHint = new CompoundButton.OnCheckedChangeListener()
    {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			isHintDisabled = isChecked;
		}    	
    };
    
    private void updateHintStatus()
    {
    	if (hintResource.allowDisable())
    	{
	    	if (isHintDisabled == true)
	    	{
	    		final SettingsManager settings = CityAlarmApplication.getSettingsManager();
	    		
	    		settings.valueHints.disableHintItemById(hintResource.getHintId());
	    	}
    	}
    }        

    @Override
	protected void onStop()
	{
    	updateHintStatus();
    	
		super.onStop();
	}
    
	@Override
	protected void onResume()
	{
		//update text
		textHintHeader.setText(hintResource.getHeaderText());
		textHintContent.setText(hintResource.getContentText());
		
		super.onResume();
	}
	
}
