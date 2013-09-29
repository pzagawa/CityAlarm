package pl.pzagawa.cityalarm;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;

public class Utils
{
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US);
	private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

	private final static DecimalFormat distanceFormat = new DecimalFormat("0");

	private final static String durationTimeFormat = "%d:%02d";

	public static String secondsToString(int seconds)
	{	
		final int hours = (int)((float)seconds / (float)3600.0f);
		final int minutes = (int)(((float)seconds / (float)60.0f) % 60);

		return String.format(Locale.US, durationTimeFormat, hours, minutes);
	}	
	
	public static String distanceToString(double distanceMeters)
	{		
		return distanceFormat.format(distanceMeters / 1000) + "KM";
	}
	
	public static String getTimeString(Calendar cal)
	{	
		return dateFormat.format(cal.getTime());
	}
	
	public static String getTimeOnlyString(Calendar cal)
	{	
		return timeFormat.format(cal.getTime());
	}
	
	private static String getQuantityString(int array_res_id, int value)
	{
		final Resources res = CityAlarmApplication.getContext().getResources();
		
		final String[] vec = res.getStringArray(array_res_id);
		
		if (value == 0)
			return vec[0];
		
		if (value == 1)
			return vec[1];

		if (value > 1 && value < 5)
			return vec[2];

		if (value >= 5)
			return vec[3];
		
		return vec[4];
	}
	
	public static String getFutureFullTextTimeString(int hours, int minutes)
	{		
		StringBuilder sb = new StringBuilder();
		
		if (hours > 0)
		{
			final String textHours = getQuantityString(R.array.time_hours_text, hours);

			if (hours != 1)
			{
				sb.append(hours);
				sb.append(" ");
			}
			
			sb.append(textHours);
			sb.append(" ");						
		}

		if (minutes > 0)
		{
			final String textMinutes = getQuantityString(R.array.time_minutes_text, minutes);
	
			if (minutes != 1)
			{
				sb.append(minutes);
				sb.append(" ");
			}
			
			sb.append(textMinutes);
		}

		return sb.toString();
	}
	
	/*
	 * dateStrings[0] = TODAY_TEXT
	 * dateStrings[1] = YESTERDAY_TEXT
	 */
	public static String getNiceTimeString(Calendar cal)
	{	
		final String[] dateText = CityAlarmApplication.getAppResources().dateText;		

		Calendar calNow = Calendar.getInstance();
		
		Calendar calTime = Calendar.getInstance();
		calTime.setTimeInMillis(cal.getTimeInMillis());

		//check for today and yesterday
		boolean isToday = false;
		boolean isYesterday = false;
		
		if (calNow.get(Calendar.YEAR) == calTime.get(Calendar.YEAR))
		{
			if (calNow.get(Calendar.DAY_OF_YEAR) == calTime.get(Calendar.DAY_OF_YEAR))
				isToday = true;			
		}
		
		calNow.add(Calendar.DAY_OF_YEAR, -1);

		if (calNow.get(Calendar.YEAR) == calTime.get(Calendar.YEAR))
		{
			if (calNow.get(Calendar.DAY_OF_YEAR) == calTime.get(Calendar.DAY_OF_YEAR))
				isYesterday = true;			
		}
				
		String text = Utils.getTimeString(calTime);
		
		if (isToday)
		{
			text = dateText[0] + ", " + Utils.getTimeOnlyString(calTime);
		}
		
		if (isYesterday)
		{
			text = dateText[1] + ", " + Utils.getTimeOnlyString(calTime);
		}
		
		return text;
	}
		
	public static String getAppVersionName(Context context)
    {
	    final PackageManager packageManager = context.getPackageManager();
	    if (packageManager != null)
	    {
            try
            {                               
            	PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);            	
                if (packageInfo != null)
                	return packageInfo.versionName;                
            }
            catch (NameNotFoundException e)
            {
            }               
	    }
	    return "";
    }

	public static boolean isAppVersionBeta(Context context)
	{
		return (getAppVersionName(context).contains("BETA"));
	}
	
	public static String getAppVersionText(Context context)
	{
		return context.getString(R.string.about_text_version) + " " + getAppVersionName(context);
	}

	public static int getAppVersionCode(Context context)
    {
	    final PackageManager packageManager = context.getPackageManager();
	    if (packageManager != null)
	    {
            try
            {                               
            	PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                if (packageInfo != null)
                	return packageInfo.versionCode;
                
            }
            catch (NameNotFoundException e)
            {
            }               
	    }
	    return 0;
    }       
	
	public static void openWebsite(Activity parent, String url)
	{
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		parent.startActivity(i);										
	}

	public static String formatTimeAsText(int seconds)
	{
		if (seconds < 0)
			return "";
		
		final int hours = (int)((float)seconds / (float)3600.0f);
		final int minutes = (int)(((float)seconds / (float)60.0f) % 60);

		if (hours == 0 && minutes == 0)
			return String.format(Locale.US, "%1$ds", seconds);

		if (hours == 0)
			return String.format(Locale.US, "%1$dm %2$ds", minutes, seconds % 60);
	
		return String.format(Locale.US, "%1$dh %2$dm %3$ds", hours, minutes, seconds % 60);
	}

	public static String getMD5(String value)
		throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("MD5");
		 
		byte[] messageDigest = md.digest(value.getBytes());
		 
		BigInteger number = new BigInteger(1, messageDigest);
		 
		return number.toString(16);
	}

	public static String removeEndingText(String text, String ending)
	{
		if (text.length() > 0)
		{		
			if (text.endsWith(ending))
			{
				return text.substring(0, text.length() - ending.length());
			}
		}
		
		return text;
	}
	
}
