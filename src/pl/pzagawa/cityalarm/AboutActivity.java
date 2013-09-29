package pl.pzagawa.cityalarm;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity
	extends Activity
{
	private TextView aboutTextLabel;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_about);
        
        aboutTextLabel = (TextView)this.findViewById(R.id.labelAbout);
    }

	@Override
	protected void onStart()
	{
		super.onStart();		
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();

		//setup content
		final String line1 = getString(R.string.app_name) + "\n" + Utils.getAppVersionText(this);
		
		aboutTextLabel.setText(line1);
	}
	
}
