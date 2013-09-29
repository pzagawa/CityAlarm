package pl.pzagawa.cityalarm;

public class AppConfig
{
	//timeout for getting location via gps
	private static final int MS_TIMEOUT_GPS = 20 * 1000;
	private static final int MS_TIMEOUT_GPS_LONG = 90 * 1000;
	
	//timeout for getting location via network
	private static final int MS_TIMEOUT_NET = 10 * 1000;
	private static final int MS_TIMEOUT_NET_LONG = 20 * 1000;
	
	//time between two following scans
	public static final int SCAN_INTERVAL_MILISECONDS = 30 * 1000;
	
	public static long getTimeoutGps(boolean waitLong)
	{
		return (waitLong ? AppConfig.MS_TIMEOUT_GPS_LONG : AppConfig.MS_TIMEOUT_GPS);			
	}

	public static long getTimeoutNet(boolean waitLong)
	{
		return (waitLong ? AppConfig.MS_TIMEOUT_NET_LONG : AppConfig.MS_TIMEOUT_NET);
	}
	
}
