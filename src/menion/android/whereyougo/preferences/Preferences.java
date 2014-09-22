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

import menion.android.whereyougo.R;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.Utils;
import android.content.Context;
import android.preference.PreferenceManager;

public class Preferences {

  private static final String TAG = "SettingValues";

  // global things
  /** altitude format */
  public static int FORMAT_ALTITUDE;
  /** angle format */
  public static int FORMAT_ANGLE;
  /** latitude/longitude format */
  public static int FORMAT_COO_LATLON;
  /** distance format */
  public static int FORMAT_LENGTH;
  /** speed format */
  public static int FORMAT_SPEED;

  /** root directory */
  public static String GLOBAL_ROOT;
  /** map provider option */
  public static int GLOBAL_MAP_PROVIDER;
  /** save game automatically option */
  public static boolean GLOBAL_SAVEGAME_AUTO;
  /** GC credentials */
  // public static String GLOBAL_GC_USERNAME;
  // public static String GLOBAL_GC_PASSWORD;
  /** is status icon enabled */
  public static boolean APPEARANCE_STATUSBAR;
  /** is fullscreen enabled */
  public static boolean APPEARANCE_FULLSCREEN;
  /** highlight option */
  public static int APPEARANCE_HIGHLIGHT;
  /** stretch images option */
  public static boolean APPEARANCE_IMAGE_STRETCH;
  /** large font */
  public static int APPEARANCE_FONT_SIZE;

  // GPS
  /** gps min time */
  public static int GPS_MIN_TIME;
  /** beep on gps fix */
  public static boolean GPS_BEEP_ON_GPS_FIX;
  /** altitude correction */
  public static double GPS_ALTITUDE_CORRECTION;

  // SENSORS
  /** use hardware compass */
  public static boolean SENSOR_HARDWARE_COMPASS;
  /** use hardware compass */
  public static boolean SENSOR_HARDWARE_COMPASS_AUTO_CHANGE;
  /** use hardware compass */
  public static int SENSOR_HARDWARE_COMPASS_AUTO_CHANGE_VALUE;
  /** use true bearing as orientation */
  public static boolean SENSOR_BEARING_TRUE;
  /** applied filter */
  public static int SENSOR_ORIENT_FILTER;

  // GUIDING
  /** disable gps when screen off */
  public static boolean GUIDING_GPS_REQUIRED;
  /** enable/disable guiding sounds */
  public static boolean GUIDING_SOUNDS;
  /** waypoint sound type */
  public static int GUIDING_WAYPOINT_SOUND;
  /** waypoint sound distance */
  public static int GUIDING_WAYPOINT_SOUND_DISTANCE;
  /** zone navigation point */
  public static int GUIDING_ZONE_NAVIGATION_POINT;
 
  public static boolean comparePreferenceKey( Context c, final String prefString, final int prefId ) {
  	return prefString.equals( c.getString( prefId ) );
  } 
  
  public static String getStringPreference( Context c, final int PreferenceId ) {
	String key = c.getString( PreferenceId );
	return PreferenceManager.getDefaultSharedPreferences(c).getString(key, "false" );	  
  } 

  public static double getDecimalPreference( Context c, final int PreferenceId ) {
	String key = c.getString( PreferenceId );
	return Utils.parseDouble( PreferenceManager.getDefaultSharedPreferences(c).getString(key, "0.0" ) );	  
  }   
  
  public static int getNumericalPreference( Context c, final int PreferenceId ) {
	String key = c.getString( PreferenceId );
	return Utils.parseInt( PreferenceManager.getDefaultSharedPreferences(c).getString(key, "0" ) );	  
  }  
  
  public static boolean getBooleanPreference( Context c, final int PreferenceId ) {
	String key = c.getString( PreferenceId );
	return Utils.parseBoolean( PreferenceManager.getDefaultSharedPreferences(c).getString(key, "" ) );	  
  }  
  
  /* Note: Default values are defined in xml/<preferences>.xml and loaded at programm start */
  public static void init(Context c) {
    Logger.d(TAG, "init(" + c + ")");
    
    GLOBAL_ROOT = getStringPreference( c, R.string.pref_KEY_S_ROOT );
    GLOBAL_MAP_PROVIDER = getNumericalPreference( c, R.string.pref_KEY_S_MAP_PROVIDER );
    GLOBAL_SAVEGAME_AUTO = getBooleanPreference( c, R.string.pref_KEY_B_SAVEGAME_AUTO );
    
    APPEARANCE_STATUSBAR = getBooleanPreference( c, R.string.pref_KEY_B_STATUSBAR );
    APPEARANCE_FULLSCREEN = getBooleanPreference( c, R.string.pref_KEY_B_FULLSCREEN );
    APPEARANCE_HIGHLIGHT = getNumericalPreference(c, R.string.pref_KEY_S_HIGHLIGHT );
    APPEARANCE_IMAGE_STRETCH = getBooleanPreference(c, R.string.pref_KEY_B_IMAGE_STRETCH );
    APPEARANCE_FONT_SIZE = getNumericalPreference(c, R.string.pref_KEY_S_FONT_SIZE );

    FORMAT_ALTITUDE = getNumericalPreference( c, R.string.pref_KEY_S_UNITS_ALTITUDE );
    FORMAT_ANGLE = getNumericalPreference(c, R.string.pref_KEY_S_UNITS_ANGLE );
    FORMAT_COO_LATLON =getNumericalPreference( c, R.string.pref_KEY_S_UNITS_COO_LATLON );
    FORMAT_LENGTH = getNumericalPreference(c, R.string.pref_KEY_S_UNITS_LENGTH );
    FORMAT_SPEED = getNumericalPreference(c, R.string.pref_KEY_S_UNITS_SPEED );

    GPS_MIN_TIME = getNumericalPreference(c, R.string.pref_KEY_S_GPS_MIN_TIME_NOTIFICATION ); // TODO default value not defined in preferences.xml
    GPS_BEEP_ON_GPS_FIX = getBooleanPreference( c, R.string.pref_KEY_B_GPS_BEEP_ON_GPS_FIX );
    GPS_ALTITUDE_CORRECTION = getDecimalPreference( c, R.string.pref_KEY_S_GPS_ALTITUDE_MANUAL_CORRECTION );

    SENSOR_HARDWARE_COMPASS = getBooleanPreference( c, R.string.pref_KEY_B_HARDWARE_COMPASS_SENSOR );
    SENSOR_HARDWARE_COMPASS_AUTO_CHANGE = getBooleanPreference( c, R.string.pref_KEY_B_HARDWARE_COMPASS_AUTO_CHANGE );
    SENSOR_HARDWARE_COMPASS_AUTO_CHANGE_VALUE = getNumericalPreference( c, R.string.pref_KEY_S_HARDWARE_COMPASS_AUTO_CHANGE_VALUE );
    SENSOR_BEARING_TRUE = getBooleanPreference( c, R.string.pref_KEY_B_SENSORS_BEARING_TRUE );
    SENSOR_ORIENT_FILTER = getNumericalPreference( c, R.string.pref_KEY_S_SENSORS_ORIENT_FILTER );

    GUIDING_GPS_REQUIRED = getBooleanPreference(c, R.string.pref_KEY_B_GUIDING_GPS_REQUIRED );
    GUIDING_SOUNDS = getBooleanPreference(c, R.string.pref_KEY_B_GUIDING_COMPASS_SOUNDS );
    GUIDING_WAYPOINT_SOUND = getNumericalPreference (c, R.string.pref_KEY_S_GUIDING_WAYPOINT_SOUND );
    GUIDING_WAYPOINT_SOUND_DISTANCE = getNumericalPreference(c, R.string.pref_KEY_S_GUIDING_WAYPOINT_SOUND_DISTANCE );
    GUIDING_ZONE_NAVIGATION_POINT = getNumericalPreference (c, R.string.pref_KEY_S_GUIDING_ZONE_POINT );
  }
}
