package pl.pzagawa.cityalarm.ui;

import pl.pzagawa.cityalarm.AboutActivity;
import pl.pzagawa.cityalarm.AddLocationActivity;
import pl.pzagawa.cityalarm.R;
import pl.pzagawa.cityalarm.SettingsActivity;
import pl.pzagawa.cityalarm.ui.hints.HintResource;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class CommonActivity
	extends Activity
{
	public final static int REQUEST_CODE_HINT = 100;
	
	protected final static int MESSAGE_DIALOG = 1;
	
	protected ProgressDialog progressDialog = null;	
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
	
	public void showSettings()
	{
		startActivity(new Intent(this, SettingsActivity.class));		
	}

	public void showAbout()
	{
		startActivity(new Intent(this, AboutActivity.class));
	}

	public void showAddLocation()
	{
		startActivity(new Intent(this, AddLocationActivity.class));
		
		showHintMessage(R.array.hint_add_location);
	}

	public boolean showHintMessage(int hintResourceId)
	{
		return showHintMessage(hintResourceId, true);
	}

	public boolean showHintMessageLocked(int hintResourceId)
	{
		return showHintMessage(hintResourceId, false);
	}
	
	private boolean showHintMessage(int hintResourceId, boolean allowDisable)
	{
		final Intent intent = HintResource.toIntent(this, hintResourceId, allowDisable);
		
		if (intent == null)
			return false;
		
		hideHintMessage();
		
		this.startActivityForResult(intent, REQUEST_CODE_HINT);
		
		return true;
	}
	
	public void hideHintMessage()
	{		
		this.finishActivity(REQUEST_CODE_HINT);
	}

	public void showError(String title, String message)
	{
		Bundle bundle = new Bundle();

		bundle.putString("title", title);
		bundle.putString("message", message);

		showDialog(MESSAGE_DIALOG, bundle);
	}
	
	@Override
	public Dialog onCreateDialog(int id)
	{
		if (id == MESSAGE_DIALOG)
		{
		    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

		    LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		    View layout = inflater.inflate(R.layout.custom_alert_dialog, (ViewGroup)findViewById(R.id.layoutCustomAlertDialog));

		    dialogBuilder.setView(layout);
		    dialogBuilder.setTitle("Title");
		    
		    return dialogBuilder.create();
		}

		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args)
	{
		if (id == MESSAGE_DIALOG)
		{
		    dialog.setTitle(args.getString("title"));

		    TextView text = (TextView)dialog.findViewById(R.id.textCustomAlertDialogText);
		    TextView footerText = (TextView)dialog.findViewById(R.id.textCustomAlertDialogFooterText);
		    
	    	text.setText(args.getString("message"));

		    footerText.setVisibility(View.GONE);
		    
		    if (args.containsKey("footer"))
		    {
			    footerText.setText(args.getString("footer"));		    	
			    footerText.setVisibility(View.VISIBLE);
		    }
		    
		    return;
		}
		
		super.onPrepareDialog(id, dialog, args);
	}

	protected void initializeProgressDialog(String title, String message, int maxProgressValue)
	{
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle(getString(R.string.please_wait));
		progressDialog.setMessage(message);
		progressDialog.setProgress(0);
		progressDialog.setMax(maxProgressValue);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	protected void initializeProgressDialog(String message)
	{
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle(getString(R.string.please_wait));
		progressDialog.setMessage(message);
		progressDialog.setIndeterminate(true);		
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
}
