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

package menion.android.whereyougo.settings;

import java.util.Locale;

import menion.android.whereyougo.gui.extension.CustomPreferenceActivity;
import menion.android.whereyougo.gui.extension.MainApplication;
import menion.android.whereyougo.hardware.location.LocationState;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;

public class Settings {

	private static final String TAG = "Settings";

	/** last application version that run on machine */
	private static final String KEY_S_APPLICATION_VERSION_LAST = "KEY_S_APPLICATION_VERSION_LAST";
	
	// GLOBAL
	/** enable fullscreen mode on newly created activities */
	public static final String KEY_B_FULLSCREEN = "KEY_B_FULLSCREEN";
	public static final boolean DEFAULT_FULLSCREEN = false;
	/** screen highlight mode */
	public static final String KEY_S_HIGHLIGHT = "KEY_S_HIGHLIGHT";
	public static final int VALUE_HIGHLIGHT_OFF = 0;
	public static final int VALUE_HIGHLIGHT_ONLY_GPS = 1;
	public static final int VALUE_HIGHLIGHT_ALWAYS = 2;
	public static final String DEFAULT_HIGHLIGHT = String.valueOf(VALUE_HIGHLIGHT_OFF);

	// GENERAL
	/** default language */
	public static final String KEY_S_LANGUAGE = "KEY_S_LANGUAGE";
	public static final String VALUE_LANGUAGE_DEFAULT = "default";
	public static final String VALUE_LANGUAGE_AR = "ar";
	public static final String VALUE_LANGUAGE_CZ = "cs";
	public static final String VALUE_LANGUAGE_DA = "da";
	public static final String VALUE_LANGUAGE_DE = "de";
	public static final String VALUE_LANGUAGE_EL = "el";
	public static final String VALUE_LANGUAGE_EN = "en";
	public static final String VALUE_LANGUAGE_ES = "es";
	public static final String VALUE_LANGUAGE_FI = "fi";
	public static final String VALUE_LANGUAGE_FR = "fr";
	public static final String VALUE_LANGUAGE_HU = "hu";
	public static final String VALUE_LANGUAGE_IT = "it";
	public static final String VALUE_LANGUAGE_JA = "ja";
	public static final String VALUE_LANGUAGE_KO = "ko";
	public static final String VALUE_LANGUAGE_NL = "nl";
	public static final String VALUE_LANGUAGE_PL = "pl";
	public static final String VALUE_LANGUAGE_PT = "pt";
	public static final String VALUE_LANGUAGE_PT_BR = "pt_BR";
	public static final String VALUE_LANGUAGE_RU = "ru";
	public static final String VALUE_LANGUAGE_SK = "sk";
	public static final String DEFAULT_LANGUAGE = VALUE_LANGUAGE_DEFAULT;
	
	/** confirmation on exit */
	public static final String KEY_B_CONFIRM_ON_EXIT = "KEY_B_CONFIRM_ON_EXIT";
	public static final boolean DEFAULT_CONFIRM_ON_EXIT = true;
	/** last used index of coordinates format */
	public static final String KEY_I_GET_COORDINATES_LAST_INDEX = "KEY_I_GET_COORDINATES_LAST_INDEX";
	public static final int DEFAULT_GET_COORDINATES_LAST_INDEX = 0;
	
	// GPS & LOCATION
	/** if GPS should start automatically after application start */
	public static final String KEY_B_START_GPS_AUTOMATICALLY = "KEY_B_START_GPS_AUTOMATICALLY";
	public static final boolean DEFAULT_START_GPS_AUTOMATICALLY = true;
	/** last known latitude */
	protected static final String KEY_F_LAST_KNOWN_LOCATION_LATITUDE = "KEY_F_LAST_KNOWN_LOCATION_LATITUDE";
	protected static final float DEFAULT_LAST_KNOWN_LOCATION_LATITUDE = 50.07967f;
	/** last known longitude */
	protected static final String KEY_F_LAST_KNOWN_LOCATION_LONGITUDE = "KEY_F_LAST_KNOWN_LOCATION_LONGITUDE";
	protected static final float DEFAULT_LAST_KNOWN_LOCATION_LONGITUDE = 14.42980f;
	/** last known altitude */
	protected static final String KEY_F_LAST_KNOWN_LOCATION_ALTITUDE = "KEY_F_LAST_KNOWN_LOCATION_ALTITUDE";
	protected static final float DEFAULT_LAST_KNOWN_LOCATION_ALTITUDE = 0.0f;
	/** add manual correction to altitude */
	public static final String KEY_S_GPS_ALTITUDE_MANUAL_CORRECTION = "KEY_S_GPS_ALTITUDE_MANUAL_CORRECTION";
	public static final String DEFAULT_GPS_ALTITUDE_MANUAL_CORRECTION = String.valueOf(0.0);
	/** minimum time for notification */
	public static final String KEY_S_GPS_MIN_TIME_NOTIFICATION = "KEY_S_GPS_MIN_TIME_NOTIFICATION";
	public static final String DEFAULT_GPS_MIN_TIME_NOTIFICATION = "0";
	/** beep on first gps fix */
	public static final String KEY_B_GPS_BEEP_ON_GPS_FIX = "KEY_B_GPS_BEEP_ON_GPS_FIX";
	public static final boolean DEFAULT_GPS_BEEP_ON_GPS_FIX = true;
	/** disable GPS when not needed */
	public static final String KEY_B_GPS_DISABLE_WHEN_HIDE = "KEY_B_GPS_DISABLE_WHEN_HIDE";
	public static final boolean DEFAULT_GPS_DISABLE_WHEN_HIDE = true;
	
	// SENSORS
	/** is hardware orientation sensor enabled */
	public static final String KEY_B_HARDWARE_COMPASS_SENSOR = "KEY_B_HARDWARE_COMPASS_SENSOR";
	public static final boolean DEFAULT_HARDWARE_COMPASS_SENSOR = true;
	/** is hardware orientation sensor enabled */
	public static final String KEY_B_HARDWARE_COMPASS_AUTO_CHANGE = "KEY_B_HARDWARE_COMPASS_AUTO_CHANGE";
	public static final boolean DEFAULT_HARDWARE_COMPASS_AUTO_CHANGE = false;
	/** is hardware orientation sensor enabled */
	public static final String KEY_S_HARDWARE_COMPASS_AUTO_CHANGE_VALUE = "KEY_S_HARDWARE_COMPASS_AUTO_CHANGE_VALUE";
	public static final String DEFAULT_HARDWARE_COMPASS_AUTO_CHANGE_VALUE = "1";
	/** use true or magnetic bearing */
	public static final String KEY_B_SENSORS_BEARING_TRUE = "KEY_B_SENSORS_BEARING_TRUE";
	public static final boolean DEFAULT_SENSORS_BEARING_TRUE = true;
	/** orientation filter */
	public static final String KEY_S_SENSORS_ORIENT_FILTER = "KEY_S_SENSORS_ORIENT_FILTER";
	public static final int VALUE_SENSORS_ORIENT_FILTER_NO = 0;
	public static final int VALUE_SENSORS_ORIENT_FILTER_LIGHT = 1;
	public static final int VALUE_SENSORS_ORIENT_FILTER_MEDIUM = 2;
	public static final int VALUE_SENSORS_ORIENT_FILTER_HEAVY = 3;
	public static final String DEFAULT_SENSORS_ORIENT_FILTER = String.valueOf(VALUE_SENSORS_ORIENT_FILTER_MEDIUM);
	/** screen rotate modifiers */
	public static final String KEY_S_SENSOR_ORIENT_MODIF_PORTRAIT = "KEY_S_SENSOR_ORIENT_MODIF_PORTRAIT";
	public static final String DEFAULT_SENSOR_ORIENT_MODIF_PORTRAIT = Utils.isAndroid30OrMore() ? "270" : "0";
	public static final String KEY_S_SENSOR_ORIENT_MODIF_LANDSCAPE = "KEY_S_SENSOR_ORIENT_MODIF_LANDSCAPE";
	public static final String DEFAULT_SENSOR_ORIENT_MODIF_LANDSCAPE = Utils.isAndroid30OrMore() ? "0" : "90";

	// GUIDING
	/** is guiding sounds enabled on compass screen */
	public static final String KEY_B_GUIDING_COMPASS_SOUNDS = "KEY_B_GUIDING_COMPASS_SOUNDS";
	public static final boolean DEFAULT_GUIDING_COMPASS_SOUNDS = false;
	/** disable gps when screen off during guiding */
	public static final String KEY_B_GUIDING_GPS_REQUIRED = "KEY_B_GUIDING_GPS_REQUIRED";
	public static final boolean DEFAULT_GUIDING_GPS_REQUIRED = true;
	/** waypoint sounds */
	public static final String KEY_S_GUIDING_WAYPOINT_SOUND = "KEY_S_GUIDING_WAYPOINT_SOUND";
	public static final int VALUE_GUIDING_WAYPOINT_SOUND_INCREASE_CLOSER = 0;
	public static final int VALUE_GUIDING_WAYPOINT_SOUND_BEEP_ON_DISTANCE = 1;
	public static final int VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND = 2;
	public static final String DEFAULT_GUIDING_WAYPOINT_SOUND = String.valueOf(VALUE_GUIDING_WAYPOINT_SOUND_BEEP_ON_DISTANCE);
	public static final String VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND_URI = "";
	/** waypoint sounds beep distance */
	public static final String KEY_S_GUIDING_WAYPOINT_SOUND_DISTANCE = "KEY_S_GUIDING_WAYPOINT_SOUND_DISTANCE";
	public static final String DEFAULT_GUIDING_WAYPOINT_SOUND_DISTANCE = "100";
	
	// UNITS PARAMETRES
	/** default latitude/longitude format */
	public static final String KEY_S_UNITS_COO_LATLON = "KEY_S_UNITS_COO_LATLON";
	public static final int VALUE_UNITS_COO_LATLON_DEC = 0;
	public static final int VALUE_UNITS_COO_LATLON_MIN = 1;
	public static final int VALUE_UNITS_COO_LATLON_SEC = 2;	
	public static final String DEFAULT_UNITS_COO_LATLON = String.valueOf(VALUE_UNITS_COO_LATLON_MIN);
	/** default length format */
	public static final String KEY_S_UNITS_LENGTH = "KEY_S_UNITS_LENGTH";
	public static final int VALUE_UNITS_LENGTH_ME = 0;
	public static final int VALUE_UNITS_LENGTH_IM = 1;
	public static final int VALUE_UNITS_LENGTH_NA = 2;
	public static final String DEFAULT_UNITS_LENGTH = String.valueOf(VALUE_UNITS_LENGTH_ME);
	/** default height format */
	public static final String KEY_S_UNITS_ALTITUDE = "KEY_S_UNITS_ALTITUDE";
	public static final int VALUE_UNITS_ALTITUDE_METRES = 0;
	public static final int VALUE_UNITS_ALTITUDE_FEET = 1;
	public static final String DEFAULT_UNITS_ALTITUDE = String.valueOf(VALUE_UNITS_ALTITUDE_METRES);
	/** default angle format */
	public static final String KEY_S_UNITS_SPEED = "KEY_S_UNITS_SPEED";
	public static final int VALUE_UNITS_SPEED_KMH = 0;
	public static final int VALUE_UNITS_SPEED_MILH = 1;
	public static final int VALUE_UNITS_SPEED_KNOTS = 2;
	public static final String DEFAULT_UNITS_SPEED = String.valueOf(VALUE_UNITS_SPEED_KMH);
	/** default angle format */
	public static final String KEY_S_UNITS_ANGLE = "KEY_S_UNITS_ANGLE";
	public static final int VALUE_UNITS_ANGLE_DEGREE = 0;
	public static final int VALUE_UNITS_ANGLE_MIL = 1;
	public static final String DEFAULT_UNITS_ANGLE = String.valueOf(VALUE_UNITS_ANGLE_DEGREE);

	public static boolean getPrefBoolean(Context context, String key, boolean def) {
//		Logger.v(TAG, "getPrefBoolean(" + key + ", " + def + ")");
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, def);
	}
	public static void setPrefBoolean(Context context, String key, boolean value) {
//		Logger.v(TAG, "setPrefBoolean(" + key + ", " + value + ")");
		PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).commit();
	} 
	public static int getPrefInt(Context context, String key, int def) {
//		Logger.v(TAG, "getPrefInt(" + key + ", " + def + ")");
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, def);
	}
	public static void setPrefInt(Context context, String key, int value) {
//		Logger.v(TAG, "setPrefInt(" + key + ", " + value + ")");
		PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).commit();
	}
	public static float getPrefFloat(Context context, String key, float def) {
//		Logger.v(TAG, "getPrefFloat(" + key + ", " + def + ")");
		return PreferenceManager.getDefaultSharedPreferences(context).getFloat(key, def);
	}
	public static void setPrefFloat(Context context, String key, float value) {
//		Logger.v(TAG, "setPrefFloat(" + key + ", " + value + ")");
		PreferenceManager.getDefaultSharedPreferences(context).edit().putFloat(key, value).commit();
	}
	public static String getPrefString(Context context, String key, String def) {
//		Logger.v(TAG, "getPrefString(" + key + ", " + def + ")");
		return PreferenceManager.getDefaultSharedPreferences(context).getString(key, def);
	}
	public static void setPrefString(Context context, String key, String value) {
//		Logger.v(TAG, "setPrefString(" + key + ", " + value + ")");
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).commit();
	}
	
	public static boolean getPrefBoolean(String key, boolean def) {
		if (A.getApp() == null) {
			return def;
		}
		return PreferenceManager.getDefaultSharedPreferences(A.getApp()).getBoolean(key, def);
	}
	public static void setPrefBoolean(String key, boolean value) {
		if (A.getApp() == null) {
			return;
		}
		PreferenceManager.getDefaultSharedPreferences(A.getApp()).edit().putBoolean(key, value).commit();
	} 
	public static int getPrefInt(String key, int def) {
		if (A.getApp() == null) {
			return def;
		}
		return PreferenceManager.getDefaultSharedPreferences(A.getApp()).getInt(key, def);
	}
	public static void setPrefInt(String key, int value) {
		if (A.getApp() == null) {
			return;
		}
		PreferenceManager.getDefaultSharedPreferences(A.getApp()).edit().putInt(key, value).commit();
	}
	public static float getPrefFloat(String key, float def) {
		if (A.getApp() == null) {
			return def;
		}
		return PreferenceManager.getDefaultSharedPreferences(A.getApp()).getFloat(key, def);
	}
	public static void setPrefFloat(String key, float value) {
		if (A.getApp() == null) {
			return;
		}
		PreferenceManager.getDefaultSharedPreferences(A.getApp()).edit().putFloat(key, value).commit();
	}
	public static String getPrefString(String key, String def) {
		if (A.getApp() == null) {
			return def;
		}
		return PreferenceManager.getDefaultSharedPreferences(A.getApp()).getString(key, def);
	}
	public static void setPrefString(String key, String value) {
		if (A.getApp() == null) {
			return;
		}
		PreferenceManager.getDefaultSharedPreferences(A.getApp()).edit().putString(key, value).commit();
	}
	
	/* APPLICATION VERSION */
	public static int getApplicationVersionLast() {
		return PreferenceManager.getDefaultSharedPreferences(A.getApp()).
		getInt(KEY_S_APPLICATION_VERSION_LAST, 0);
	}
	public static int getApplicationVersionActual() {
		try {
			return A.getApp().getPackageManager().getPackageInfo(A.getApp().getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			Logger.e(TAG, "getApplicationVersionActual()", e);
			return 0;
		}
	}
	public static void setApplicationVersionLast(int lastVersion) {
		PreferenceManager.getDefaultSharedPreferences(A.getApp()).edit().
		putInt(KEY_S_APPLICATION_VERSION_LAST, lastVersion).
		commit();
	}
	public static String getApplicationVersionActualName() {
		try {
			return A.getApp().getPackageManager().getPackageInfo(A.getApp().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			Logger.e(TAG, "getApplicationVersionActual()", e);
			return "";
		}
	}

	/* LANGUAGE */
	private static String loca = null;
    public static String getLanguageCode() {
    	if (loca == null) {
    		//String lang = getPrefString(KEY_S_LANGUAGE, Locale.getDefault().getLanguage());
    		String lang = Locale.getDefault().getLanguage();
Logger.w(TAG, "getLanguageCode() - " + lang);
    		if (lang == null)
    			return VALUE_LANGUAGE_EN;
    		if (lang.equals(VALUE_LANGUAGE_CZ)) {
    			loca = VALUE_LANGUAGE_CZ;
    		} else {
    			loca = VALUE_LANGUAGE_EN;
    		}
    	}
    	return loca;
    }
	/* LAST KNOW LOCATION */
    /** last known location */
    public static Location lastKnownLocation;
    public static Location getLastKnownLocation(Context c) {
    	if (lastKnownLocation == null) {
    		lastKnownLocation = new Location(TAG);
    		lastKnownLocation.setLatitude(getPrefFloat(c, KEY_F_LAST_KNOWN_LOCATION_LATITUDE,
    				DEFAULT_LAST_KNOWN_LOCATION_LATITUDE));
    		lastKnownLocation.setLongitude(getPrefFloat(c, KEY_F_LAST_KNOWN_LOCATION_LONGITUDE,
    				DEFAULT_LAST_KNOWN_LOCATION_LONGITUDE));
    		lastKnownLocation.setAltitude(getPrefFloat(c, KEY_F_LAST_KNOWN_LOCATION_ALTITUDE,
    				DEFAULT_LAST_KNOWN_LOCATION_ALTITUDE));
    	}
    	return lastKnownLocation;
    }
	public static void setLastKnownLocation() {
		try {
			PreferenceManager.getDefaultSharedPreferences(A.getApp()).edit().
			putFloat(KEY_F_LAST_KNOWN_LOCATION_LATITUDE, 
					(float) LocationState.getLocation().getLatitude()).
			putFloat(KEY_F_LAST_KNOWN_LOCATION_LONGITUDE,
					(float) LocationState.getLocation().getLongitude()).
			putFloat(KEY_F_LAST_KNOWN_LOCATION_ALTITUDE,
					(float) LocationState.getLocation().getAltitude()).
			commit();
		} catch (Exception e) {
			Logger.e(TAG, "setLastKnownLocation()", e);
		}
	}

	// setted from onResume();
    private static Activity currentActivity;
    public static boolean existCurrentActivity() {
    	return currentActivity != null;
    }
    
    public static Activity getCurrentActivity() {
    	return currentActivity == null ? A.getMain() : currentActivity;
    }
    
    public static void setCurrentActivity(Activity activity) {
    	if (Settings.currentActivity == null && activity != null)
    		MainApplication.appRestored();
    	Settings.currentActivity = activity;
    }
    
    public static boolean setScreenBasic(Activity activity) {
    	try {
//Logger.w(TAG, "setFullscreen(" + activity.getLocalClassName() + ")");
			// hide title
    		if (!(activity instanceof CustomPreferenceActivity)) {
   				activity.requestWindowFeature(Window.FEATURE_NO_TITLE);	
    		}
    		return true;
    	} catch (Exception e) {
    		Logger.e(TAG, "setFullScreen(" + activity + ")", e);
    	}
    	return false;
    }
    
    public static void setScreenFullscreen(Activity activity) {
    	try {
    		if (!(activity instanceof CustomPreferenceActivity)) {
				// set fullScreen
        		if (SettingValues.GLOBAL_FULLSCREEN) {
    				activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    						WindowManager.LayoutParams.FLAG_FULLSCREEN);
    			} else {
    				activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    			}
    		}
    	} catch (Exception e) {
    		Logger.e(TAG, "setFullScreen(" + activity + ")", e);
    	}
    }
    
    private static PowerManager.WakeLock wl;

    public static void enableWakeLock() {
    	try {
	    	boolean disable = false;
	    	if (SettingValues.GLOBAL_HIGHLIGHT == VALUE_HIGHLIGHT_OFF) {
	    		disable = true;
	    	} else if (SettingValues.GLOBAL_HIGHLIGHT == VALUE_HIGHLIGHT_ONLY_GPS) {
	    		if (!LocationState.isActuallyHardwareGpsOn()) {
	    			disable = true;
	    		}
	    	}
Logger.w(TAG, "enableWakeLock(), dis:" + disable + ", wl:" + wl);
	    	if (disable && wl != null) {
	    		disableWakeLock();
	    	} else if (!disable && wl == null) {
	   			PowerManager pm = (PowerManager) A.getApp().getSystemService(Context.POWER_SERVICE);
	   			wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
	   			wl.acquire();
	    	}
//Logger.w(TAG, "enableWakeLock(), res:" + wl);
    	} catch (Exception e) {
    		Logger.e(TAG, "enableWakeLock(), e:" + e.toString());
    	}
    }
    
    public static void disableWakeLock() {
Logger.w(TAG, "disableWakeLock(), wl:" + wl);
    	if (wl != null) {
    		wl.release();
    		wl = null;
    	}
    }
}
