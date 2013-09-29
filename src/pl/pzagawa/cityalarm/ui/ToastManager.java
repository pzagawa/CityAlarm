package pl.pzagawa.cityalarm.ui;

import pl.pzagawa.cityalarm.CityAlarmApplication;
import pl.pzagawa.cityalarm.R;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ToastManager
{
	public enum Theme
	{
		DARK_BLUE(0x880099cc),
		DARK_GREEN(0x8833b5e5),
		ORANGE(0xaaff8800);

		public final int color;
		
		private Theme(int color)
		{
			this.color = color;			
		}
	}
	
	private static Handler handler = new Handler();
	
	private static void run(String textTitle, String textContent, Theme theme)
	{
		final Context context = CityAlarmApplication.getContext();

		final LayoutInflater inflater = CityAlarmApplication.getLayoutInflater();

		final View layout = inflater.inflate(R.layout.custom_toast, null);

		final LinearLayout llayBackground = (LinearLayout) layout.findViewById(R.id.llayBackground);		
		llayBackground.setBackgroundColor(theme.color);

		final TextView textToastTitle = (TextView) layout.findViewById(R.id.textToastTitle);
		textToastTitle.setText(textTitle);
		
		final TextView textToastContent = (TextView) layout.findViewById(R.id.textToastContent);
		textToastContent.setText(textContent);

		Toast toast = new Toast(context);
		
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);

		toast.show();		
	}

	public static void show(final String textTitle, final String textContent, final Theme theme)
	{
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				ToastManager.run(textTitle, textContent, theme);
			}
		});
	}

}
