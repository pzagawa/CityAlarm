package pl.pzagawa.cityalarm.location;

public class LocationConverter
{
	public final static double VALUE_WIDTH = 10000000f;	
	
	public static double intToDouble(int value)
	{
		if (value == 0)
			return 0;
		
		return (double)value / VALUE_WIDTH;
	}
	
	public static int doubleToInt(double value)
	{
		if (value == 0)
			return 0;
		
		return (int) (value * VALUE_WIDTH);		
	}
		
}
