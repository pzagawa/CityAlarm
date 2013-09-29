package pl.pzagawa.cityalarm.alarm;

import pl.pzagawa.cityalarm.Utils;

public class SpeechTextBuilder
{
	private final String separator = ", ";
	
	private final StringBuilder text = new StringBuilder();
	
	public SpeechTextBuilder()
	{		
	}
	
	public void add(String value)
	{
		text.append(value);
		text.append(separator);
	}

	public boolean isText()
	{
		return (text.toString().trim().length() > 0);
	}

	public String getText()
	{
		final String speechText = text.toString();
		
		return Utils.removeEndingText(speechText, separator);
	}
	
}
