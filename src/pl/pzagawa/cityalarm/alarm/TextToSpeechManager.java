package pl.pzagawa.cityalarm.alarm;

import java.util.HashMap;
import java.util.Locale;

import pl.pzagawa.cityalarm.CityAlarmApplication;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;

public class TextToSpeechManager
	implements TextToSpeech.OnInitListener
{
	public interface OnStatusListener
	{
		void onSpeechEngineInitialized(boolean isAvailable, String defaultEngineName);		
	}
	
	private final Context context;
	
	private TextToSpeech tts = null;
	
	private volatile boolean isAvailable = false;
	private volatile boolean initialized = false;
	private Object initializedMutex = new Object();
	
	private String defaultEngineName = "";
	
	private OnStatusListener onStatusListener = null;
	
	public TextToSpeechManager(Context context)
	{
		this.context = context;
	}

	public synchronized void initialize()
	{
		close();
		
		this.isAvailable = false;
		this.initialized = false;		
		this.defaultEngineName = "";		
		
		this.tts = new TextToSpeech(context, this);
	}
		
	private Runnable runOnInitialized = new Runnable()
	{
		@Override
		public void run()
		{
			if (onStatusListener != null)
			{
				onStatusListener.onSpeechEngineInitialized(isAvailable, getEngineName());

				//call only once
				onStatusListener = null;
			}
		}
	};

	private void waitForInitialization()
	{
		final Handler handler = new Handler();

		final Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				boolean locked = true;
				
				while (locked)
				{
					synchronized (initializedMutex)
					{
						if (initialized)
						{
							locked = false;

							handler.post(runOnInitialized);
						}
					}

					SystemClock.sleep(50);					
				}
			}
		});

		thread.start();
	}

	public void checkIfAvailable(OnStatusListener onStatusListener)
	{
		this.onStatusListener = onStatusListener;
		
		initialize();

		waitForInitialization();
	}
	
	public void close()
	{
		if (tts != null)
		{
			tts.stop();
			tts.shutdown();
			tts = null;
		}
	}
	
	@Override
	public void onInit(int status)
	{
		boolean success = false;
		
		if (tts == null)
			return;
		
        if (status == TextToSpeech.SUCCESS)
        {
        	final Locale locale = CityAlarmApplication.getAppResources().locale;
        	
        	final int result = tts.setLanguage(locale);

        	success = true;
        	
        	if (result == TextToSpeech.LANG_MISSING_DATA)
        	{
            	success = false;
        	}

        	if (result == TextToSpeech.LANG_NOT_SUPPORTED)
        	{
            	success = false;
        	}

           	isAvailable = success;
        	
        	if (isAvailable)
        	{
        		defaultEngineName = tts.getDefaultEngine();
        	}
        }
        
        if (status == TextToSpeech.ERROR)
        {
           	isAvailable = false;   
        }

		synchronized (initializedMutex)
		{
			initialized = true;
		}
	}
	
	public synchronized void say(String textToSpeak)
	{
		if (tts == null)
			return;
		
		if (isAvailable)
		{
			final HashMap<String, String> params = new HashMap<String, String>();
	
			params.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));
	
			tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params);
		}
	}
	
	public boolean isAvailable()
	{
		return this.isAvailable;
	}
	
	public String getEngineName()
	{
		if (defaultEngineName.contains("svox"))
			return "SVOX";

		if (defaultEngineName.contains("google.android.tts"))
			return "Android TTS";

		if (defaultEngineName.contains("com.ivona.tts"))
			return "IVONA";
		
		return defaultEngineName;
	}

}
