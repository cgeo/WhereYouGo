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

import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_FULLSCREEN;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_GPS_ALTITUDE_MANUAL_CORRECTION;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_GPS_BEEP_ON_GPS_FIX;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_GPS_MIN_TIME_NOTIFICATION;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_GUIDING_COMPASS_SOUNDS;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_GUIDING_GPS_REQUIRED;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_GUIDING_WAYPOINT_SOUND;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_GUIDING_WAYPOINT_SOUND_DISTANCE;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_GUIDING_ZONE_POINT;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_HARDWARE_COMPASS_AUTO_CHANGE;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_HARDWARE_COMPASS_AUTO_CHANGE_VALUE;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_HARDWARE_COMPASS_SENSOR;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_HIGHLIGHT;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_IMAGE_STRETCH;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_FONT_SIZE;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_MAP_PROVIDER;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_ROOT;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_SAVEGAME_AUTO;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_GC_USERNAME;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_GC_PASSWORD;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_SENSORS_BEARING_TRUE;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_SENSORS_ORIENT_FILTER;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_STATUSBAR;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_UNITS_ALTITUDE;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_UNITS_ANGLE;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_UNITS_COO_LATLON;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_UNITS_LENGTH;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_UNITS_SPEED;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_B_FULLSCREEN;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_B_GPS_BEEP_ON_GPS_FIX;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_B_GUIDING_COMPASS_SOUNDS;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_B_GUIDING_GPS_REQUIRED;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_B_HARDWARE_COMPASS_AUTO_CHANGE;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_B_HARDWARE_COMPASS_SENSOR;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_B_IMAGE_STRETCH;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_FONT_SIZE;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_B_SAVEGAME_AUTO;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_GC_USERNAME;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_GC_PASSWORD;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_B_SENSORS_BEARING_TRUE;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_B_STATUSBAR;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_GPS_ALTITUDE_MANUAL_CORRECTION;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_GPS_MIN_TIME_NOTIFICATION;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_GUIDING_WAYPOINT_SOUND;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_GUIDING_WAYPOINT_SOUND_DISTANCE;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_GUIDING_ZONE_POINT;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_HARDWARE_COMPASS_AUTO_CHANGE_VALUE;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_HIGHLIGHT;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_MAP_PROVIDER;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_ROOT;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_SENSORS_ORIENT_FILTER;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_UNITS_ALTITUDE;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_UNITS_ANGLE;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_UNITS_COO_LATLON;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_UNITS_LENGTH;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_UNITS_SPEED;
import static menion.android.whereyougo.preferences.PreferenceValues.getPrefBoolean;
import static menion.android.whereyougo.preferences.PreferenceValues.getPrefString;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.Utils;
import android.content.Context;

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

  public static void init(Context c) {
    Logger.d(TAG, "init(" + c + ")");
    GLOBAL_ROOT = getPrefString(c, KEY_S_ROOT, DEFAULT_ROOT);
    GLOBAL_MAP_PROVIDER =
        Utils.parseInt(getPrefString(c, KEY_S_MAP_PROVIDER, DEFAULT_MAP_PROVIDER));
    GLOBAL_SAVEGAME_AUTO = getPrefBoolean(c, KEY_B_SAVEGAME_AUTO, DEFAULT_SAVEGAME_AUTO);
    // GLOBAL_GC_USERNAME = getPrefString(c, KEY_S_GC_USERNAME, DEFAULT_GC_USERNAME);
    // GLOBAL_GC_PASSWORD = getPrefString(c, KEY_S_GC_PASSWORD, DEFAULT_GC_PASSWORD);
    APPEARANCE_STATUSBAR = getPrefBoolean(c, KEY_B_STATUSBAR, DEFAULT_STATUSBAR);
    APPEARANCE_FULLSCREEN = getPrefBoolean(c, KEY_B_FULLSCREEN, DEFAULT_FULLSCREEN);
    APPEARANCE_HIGHLIGHT = Utils.parseInt(getPrefString(c, KEY_S_HIGHLIGHT, DEFAULT_HIGHLIGHT));
    APPEARANCE_IMAGE_STRETCH = getPrefBoolean(c, KEY_B_IMAGE_STRETCH, DEFAULT_IMAGE_STRETCH);
    APPEARANCE_FONT_SIZE = Utils.parseInt(getPrefString(c, KEY_S_FONT_SIZE, DEFAULT_FONT_SIZE));

    FORMAT_ALTITUDE =
        Utils.parseInt(getPrefString(c, KEY_S_UNITS_ALTITUDE, DEFAULT_UNITS_ALTITUDE));
    FORMAT_ANGLE = Utils.parseInt(getPrefString(c, KEY_S_UNITS_ANGLE, DEFAULT_UNITS_ANGLE));
    FORMAT_COO_LATLON =
        Utils.parseInt(getPrefString(c, KEY_S_UNITS_COO_LATLON, DEFAULT_UNITS_COO_LATLON));
    FORMAT_LENGTH = Utils.parseInt(getPrefString(c, KEY_S_UNITS_LENGTH, DEFAULT_UNITS_LENGTH));
    FORMAT_SPEED = Utils.parseInt(getPrefString(c, KEY_S_UNITS_SPEED, DEFAULT_UNITS_SPEED));

    GPS_MIN_TIME =
        Utils.parseInt(getPrefString(c, KEY_S_GPS_MIN_TIME_NOTIFICATION,
            DEFAULT_GPS_MIN_TIME_NOTIFICATION));
    GPS_BEEP_ON_GPS_FIX = getPrefBoolean(c, KEY_B_GPS_BEEP_ON_GPS_FIX, DEFAULT_GPS_BEEP_ON_GPS_FIX);
    GPS_ALTITUDE_CORRECTION =
        Utils.parseDouble(getPrefString(c, KEY_S_GPS_ALTITUDE_MANUAL_CORRECTION,
            DEFAULT_GPS_ALTITUDE_MANUAL_CORRECTION));

    SENSOR_HARDWARE_COMPASS =
        getPrefBoolean(c, KEY_B_HARDWARE_COMPASS_SENSOR, DEFAULT_HARDWARE_COMPASS_SENSOR);
    SENSOR_HARDWARE_COMPASS_AUTO_CHANGE =
        getPrefBoolean(c, KEY_B_HARDWARE_COMPASS_AUTO_CHANGE, DEFAULT_HARDWARE_COMPASS_AUTO_CHANGE);
    SENSOR_HARDWARE_COMPASS_AUTO_CHANGE_VALUE =
        Utils.parseInt(getPrefString(c, KEY_S_HARDWARE_COMPASS_AUTO_CHANGE_VALUE,
            DEFAULT_HARDWARE_COMPASS_AUTO_CHANGE_VALUE));
    SENSOR_BEARING_TRUE =
        getPrefBoolean(c, KEY_B_SENSORS_BEARING_TRUE, DEFAULT_SENSORS_BEARING_TRUE);
    SENSOR_ORIENT_FILTER =
        Utils
            .parseInt(getPrefString(c, KEY_S_SENSORS_ORIENT_FILTER, DEFAULT_SENSORS_ORIENT_FILTER));

    GUIDING_GPS_REQUIRED =
        getPrefBoolean(c, KEY_B_GUIDING_GPS_REQUIRED, DEFAULT_GUIDING_GPS_REQUIRED);
    GUIDING_SOUNDS =
        getPrefBoolean(c, KEY_B_GUIDING_COMPASS_SOUNDS, DEFAULT_GUIDING_COMPASS_SOUNDS);
    GUIDING_WAYPOINT_SOUND =
        Utils.parseInt(getPrefString(c, KEY_S_GUIDING_WAYPOINT_SOUND,
            DEFAULT_GUIDING_WAYPOINT_SOUND));
    GUIDING_WAYPOINT_SOUND_DISTANCE =
        Utils.parseInt(getPrefString(c, KEY_S_GUIDING_WAYPOINT_SOUND_DISTANCE,
            DEFAULT_GUIDING_WAYPOINT_SOUND_DISTANCE));
    GUIDING_ZONE_NAVIGATION_POINT =
        Utils.parseInt(getPrefString(c, KEY_S_GUIDING_ZONE_POINT, DEFAULT_GUIDING_ZONE_POINT));
  }
}
