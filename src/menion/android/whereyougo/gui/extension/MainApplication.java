/*
  * This file is part of WhereYouGo.
  *
  * WhereYouGo is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * WhereYouGo is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with WhereYouGo.  If not, see <http://www.gnu.org/licenses/>.
  *
  * Copyright (C) 2012 Menion <whereyougo@asamm.cz>
  */ 

package menion.android.whereyougo.gui.extension;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import menion.android.whereyougo.Main;
import menion.android.whereyougo.hardware.location.LocationState;
import menion.android.whereyougo.settings.SettingValues;
import menion.android.whereyougo.settings.Settings;
import menion.android.whereyougo.utils.FileSystem;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.StringToken;
import menion.android.whereyougo.utils.Utils;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

public class MainApplication extends Application {

	private static final String TAG = "MainApplication";
	
    private Locale locale = null;
	// screen ON/OFF receiver
	private ScreenReceiver mScreenReceiver;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (locale != null) {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            getBaseContext().getResources().updateConfiguration(
            		newConfig,
            		getBaseContext().getResources().getDisplayMetrics());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration config = getBaseContext().getResources().getConfiguration();
        String lang = settings.getString(Settings.KEY_S_LANGUAGE,
        		Settings.VALUE_LANGUAGE_DEFAULT);
//Logger.d(TAG, "lang:" + lang + ", system:" + config.locale.getLanguage());
        if (!lang.equals(Settings.VALUE_LANGUAGE_DEFAULT) &&
        		!config.locale.getLanguage().equals(lang)) {
        	ArrayList<String> loc = StringToken.parse(lang, "_");
        	if (loc.size() == 1) {
        		locale = new Locale(lang);	
        	} else {
        		locale = new Locale(loc.get(0), loc.get(1));
        	}
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
            		getBaseContext().getResources().getDisplayMetrics());
        }
        
        // initialize core
        initCore(Main.APP_NAME);
    }

    public void onLowMemory() {
    	super.onLowMemory();
    	Log.d(TAG, "onLowMemory()");
    }
    
	public void onTerminate() {
		super.onTerminate();
		Log.d(TAG, "onTerminate()");
    }

	public boolean isScreenOff() {
		return mScreenOff;
	}
	
	private boolean mScreenOff = false;
	
	private class ScreenReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//Logger.v(TAG, "ACTION_SCREEN_OFF");
				mScreenOff = true;
	        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
//Logger.v(TAG, "ACTION_SCREEN_ON");
				LocationState.onScreenOn(false);
				mScreenOff = false;
	        }
	    }
	}
	
	/**
	 * I appName is 'null', function is called from JNI, so do basic initialization.
	 * Otherwise function is called from MainActivity, so set root directory
	 * @param context
	 * @param appName
	 */
	private void initCore(String appName) {
		// register screen on/off receiver
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		mScreenReceiver = new ScreenReceiver();
		registerReceiver(mScreenReceiver, filter);
		
    	// init root directory
		FileSystem.createRoot(appName);
		// set basic settings values
		SettingValues.init(this);
		// set location state
		LocationState.init(this);
    	// initialize DPI
    	Utils.getDpPixels(this, 1.0f);
	}
	
    private static Timer mTimer;
    
    public static void onActivityPause() {
//Logger.i(TAG, "onActivityPause()");
    	if (mTimer != null) {
    		mTimer.cancel();
    	}
    	
    	mTimer = new Timer();
    	mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (!Settings.existCurrentActivity())
					onAppMinimized();
				LocationState.onActivityPauseInstant(Settings.getCurrentActivity());
				mTimer = null;
			}
		}, 2000);
    }
    
	public void destroy() {
		try {
			unregisterReceiver(mScreenReceiver);
		} catch (Exception e) {
			Logger.w(TAG, "destroy(), e:" + e);
		}
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		onAppVisibilityChange = null;
	}
    
    private static void onAppMinimized() {
    	if (onAppVisibilityChange != null)
    		onAppVisibilityChange.onAppMinimized();
    }
    
    public static void appRestored() {
    	onAppRestored();
    	if (onAppVisibilityChange != null)
    		onAppVisibilityChange.onAppRestored();
    }
    
    private static void onAppRestored() {
    	Logger.w(TAG, "onAppRestored()");
    }
    
    private static OnAppVisibilityChange onAppVisibilityChange;
    
    public static void registerVisibilityHandler(OnAppVisibilityChange handler) {
    	MainApplication.onAppVisibilityChange = handler;
    }
    
    public interface OnAppVisibilityChange {
    	
    	public void onAppMinimized();
    	
    	public void onAppRestored();
    	
    }
}
