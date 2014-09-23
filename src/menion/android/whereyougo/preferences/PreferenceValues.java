/*
 * This file is part of WhereYouGo.
 * 
 * WhereYouGo is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * WhereYouGo is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with WhereYouGo. If not,
 * see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2012 Menion <whereyougo@asamm.cz>
 */

package menion.android.whereyougo.preferences;

import java.util.Locale;

import menion.android.whereyougo.MainApplication;
import menion.android.whereyougo.R;
import menion.android.whereyougo.geo.location.Location;
import menion.android.whereyougo.geo.location.LocationState;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Logger;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.PowerManager;
import android.preference.PreferenceManager;


public class PreferenceValues {

  private static final String TAG = "PreferenceValues";
  
  // GLOBAL
  /** root directory */
  public static final String KEY_S_ROOT = "KEY_S_ROOT";
  public static final String DEFAULT_ROOT = "";
  /** map provider */
  public static final int VALUE_MAP_PROVIDER_VECTOR = 0;
  public static final int VALUE_MAP_PROVIDER_LOCUS = 1;

  /** screen highlight mode */
  public static final int VALUE_HIGHLIGHT_OFF = 0;
  public static final int VALUE_HIGHLIGHT_ONLY_GPS = 1;
  public static final int VALUE_HIGHLIGHT_ALWAYS = 2;

  /** font size */
  public static final int VALUE_FONT_SIZE_DEFAULT = 0;
  public static final int VALUE_FONT_SIZE_SMALL = 1;
  public static final int VALUE_FONT_SIZE_MEDIUM = 2;
  public static final int VALUE_FONT_SIZE_LARGE = 3;

  // LOGIN
  /** GC credentials */
  public static final String KEY_S_GC_USERNAME = "KEY_S_GC_USERNAME";
  public static final String DEFAULT_GC_USERNAME = "";
  public static final String KEY_S_GC_PASSWORD = "KEY_S_GC_PASSWORD";
  public static final String DEFAULT_GC_PASSWORD = "";

  // GENERAL
  /** default language */
  public static final String VALUE_LANGUAGE_CZ = "cz";
  public static final String VALUE_LANGUAGE_EN = "en";

  // GPS & LOCATION
  /** if GPS should start automatically after application start */
  public static final String KEY_B_START_GPS_AUTOMATICALLY = "KEY_B_START_GPS_AUTOMATICALLY";
  public static final boolean DEFAULT_START_GPS_AUTOMATICALLY = true;
 
  /** beep on first gps fix */
  public static final String KEY_B_GPS_BEEP_ON_GPS_FIX = "KEY_B_GPS_BEEP_ON_GPS_FIX";

  /** disable GPS when not needed */
  public static final String KEY_B_GPS_DISABLE_WHEN_HIDE = "KEY_B_GPS_DISABLE_WHEN_HIDE";
  public static final boolean DEFAULT_GPS_DISABLE_WHEN_HIDE = false;

  // SENSORS
  /** is hardware orientation sensor enabled */
  public static final String KEY_B_HARDWARE_COMPASS_SENSOR = "KEY_B_HARDWARE_COMPASS_SENSOR";
  public static final boolean DEFAULT_HARDWARE_COMPASS_SENSOR = true;

  public static final int VALUE_SENSORS_ORIENT_FILTER_NO = 0;
  public static final int VALUE_SENSORS_ORIENT_FILTER_LIGHT = 1;
  public static final int VALUE_SENSORS_ORIENT_FILTER_MEDIUM = 2;
  public static final int VALUE_SENSORS_ORIENT_FILTER_HEAVY = 3;

  // GUIDING
  public static final int VALUE_GUIDING_WAYPOINT_SOUND_INCREASE_CLOSER = 0;
  public static final int VALUE_GUIDING_WAYPOINT_SOUND_BEEP_ON_DISTANCE = 1;
  public static final int VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND = 2;
  public static final String VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND_URI = "";
 
  /** navigation point */
  public static final int VALUE_GUIDING_ZONE_POINT_CENTER = 0;
  public static final int VALUE_GUIDING_ZONE_POINT_NEAREST = 1;
  
  // UNITS PARAMETRES
  /** default latitude/longitude format */
  public static final int VALUE_UNITS_COO_LATLON_DEC = 0;
  public static final int VALUE_UNITS_COO_LATLON_MIN = 1;
  public static final int VALUE_UNITS_COO_LATLON_SEC = 2;
  /** default length format */
  public static final int VALUE_UNITS_LENGTH_ME = 0;
  public static final int VALUE_UNITS_LENGTH_IM = 1;
  public static final int VALUE_UNITS_LENGTH_NA = 2;
  /** default height format */
  public static final int VALUE_UNITS_ALTITUDE_METRES = 0;
  public static final int VALUE_UNITS_ALTITUDE_FEET = 1;
  /** default angle format */
  public static final int VALUE_UNITS_SPEED_KMH = 0;
  public static final int VALUE_UNITS_SPEED_MILH = 1;
  public static final int VALUE_UNITS_SPEED_KNOTS = 2;
  /** default angle format */
  public static final int VALUE_UNITS_ANGLE_DEGREE = 0;
  public static final int VALUE_UNITS_ANGLE_MIL = 1;
  
 
  /* LAST KNOW LOCATION */
  /** last known location */
 // public static Location lastKnownLocation;

  // setted from onResume();
  private static Activity currentActivity;

  private static PowerManager.WakeLock wl;

  public static void disableWakeLock() {
    Logger.w(TAG, "disableWakeLock(), wl:" + wl);
    if (wl != null) {
      wl.release();
      wl = null;
    }
  }

  public static void enableWakeLock() {
    try {
      boolean disable = false;
      if (Preferences.APPEARANCE_HIGHLIGHT == VALUE_HIGHLIGHT_OFF) {
        disable = true;
      } else if (Preferences.APPEARANCE_HIGHLIGHT == VALUE_HIGHLIGHT_ONLY_GPS) {
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
      // Logger.w(TAG, "enableWakeLock(), res:" + wl);
    } catch (Exception e) {
      Logger.e(TAG, "enableWakeLock(), e:" + e.toString());
    }
  }

  public static boolean existCurrentActivity() {
    return currentActivity != null;
  }


  public static int getApplicationVersionActual() {
    try {
      return A.getApp().getPackageManager().getPackageInfo(A.getApp().getPackageName(), 0).versionCode;
    } catch (NameNotFoundException e) {
      Logger.e(TAG, "getApplicationVersionActual()", e);
      return 0;
    }
  }

  public static int getApplicationVersionLast() {
    try {
      return Preferences.getNumericalPreference( R.string.pref_KEY_S_APPLICATION_VERSION_LAST );
    } catch( ClassCastException e ) { 
      // workaround for old settings, which stores this key as integer
      Logger.e(TAG, "getNumericalPreference( R.string.pref_KEY_S_APPLICATION_VERSION_LAST ) return 0", e);
      return 0;
    }
  }

  public static Activity getCurrentActivity() {
    return currentActivity == null ? A.getMain() : currentActivity;
  }

  public static String getLanguageCode() {
      String lang = Locale.getDefault().getLanguage();
      Logger.w(TAG, "getLanguageCode() - " + lang);
      if (lang == null)
        return VALUE_LANGUAGE_EN;
      if (lang.equals(VALUE_LANGUAGE_CZ)) {
        return VALUE_LANGUAGE_CZ;
      } else {
        return VALUE_LANGUAGE_EN;
      }
  }

  public static Location getLastKnownLocation(Context c) {  
      Location lastKnownLocation = new Location(TAG);
      lastKnownLocation.setLatitude( Preferences.getDecimalPreference( R.string.pref_KEY_F_LAST_KNOWN_LOCATION_LATITUDE ) );
      lastKnownLocation.setLongitude(Preferences.getDecimalPreference( R.string.pref_KEY_F_LAST_KNOWN_LOCATION_LONGITUDE ) );
      lastKnownLocation.setAltitude( Preferences.getDecimalPreference( R.string.pref_KEY_F_LAST_KNOWN_LOCATION_ALTITUDE ) );
      return lastKnownLocation;
  }
 
  /* still used in SatelliteActivity.java, LocationState.java (2x) */
  @Deprecated 
  public static boolean getPrefBoolean(Context context, String key, boolean def) {
    // Logger.v(TAG, "getPrefBoolean(" + key + ", " + def + ")");
    return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, def);
  } 
 
  /* still used in Guide.java */
  @Deprecated  
  public static String getPrefString(Context context, String key, String def) {
    // Logger.v(TAG, "getPrefString(" + key + ", " + def + ")");
    return PreferenceManager.getDefaultSharedPreferences(context).getString(key, def);
  }

  /* still used in DownloadActivity.java (2x) */
  @Deprecated  
  public static String getPrefString(String key, String def) {
    if (A.getApp() == null) {
      return def;
    }
    return PreferenceManager.getDefaultSharedPreferences(A.getApp()).getString(key, def);
  }
  
  public static void setApplicationVersionLast(int lastVersion) {
	  Preferences.setStringPreference( R.string.pref_KEY_S_APPLICATION_VERSION_LAST, lastVersion );
  } 

  public static void setCurrentActivity(Activity activity) {
    if (PreferenceValues.currentActivity == null && activity != null)
      MainApplication.appRestored();
    PreferenceValues.currentActivity = activity;
  }

  public static void setLastKnownLocation() {
    try {
    	Preferences.setStringPreference( R.string.pref_KEY_F_LAST_KNOWN_LOCATION_LATITUDE,
    			(double) LocationState.getLocation().getLatitude() );
    	Preferences.setStringPreference( R.string.pref_KEY_F_LAST_KNOWN_LOCATION_LONGITUDE,
    			(double) LocationState.getLocation().getLongitude() );
    	Preferences.setStringPreference( R.string.pref_KEY_F_LAST_KNOWN_LOCATION_ALTITUDE,
    			(double) LocationState.getLocation().getAltitude() );    	
    } catch (Exception e) {
      Logger.e(TAG, "setLastKnownLocation()", e);
    }
  }

  /* still used in SatelliteActivity, LocationState (3x) */
  @Deprecated 
  public static void setPrefBoolean(Context context, String key, boolean value) {
    // Logger.v(TAG, "setPrefBoolean(" + key + ", " + value + ")");
    PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).commit();
  }

}
