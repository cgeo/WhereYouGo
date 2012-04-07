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

import static menion.android.whereyougo.settings.Settings.*;
import menion.android.whereyougo.utils.Utils;
import android.content.Context;

public class SettingValues {

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
	
	/** is fullscreen enabled */
	public static boolean GLOBAL_FULLSCREEN;
	/** highlight option */
	public static int GLOBAL_HIGHLIGHT;
	
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
	/** map rotate modifier portrait */
	public static int SENSOR_ORIENT_MODIF_PORTRAIT;
	/** map rotate modifier landscape */
	public static int SENSOR_ORIENT_MODIF_LANDSCAPE;
	
    // GUIDING
    /** disable gps when screen off */
    public static boolean GUIDING_GPS_REQUIRED;
    /** enable/disable guiding sounds */
    public static boolean GUIDING_SOUNDS;
    /** waypoint sound type */
    public static int GUIDING_WAYPOINT_SOUND;
    /** waypoint sound distance */
    public static int GUIDING_WAYPOINT_SOUND_DISTANCE;
    
	public static void init(Context c) {
		GLOBAL_FULLSCREEN = getPrefBoolean(c, KEY_B_FULLSCREEN,
				DEFAULT_FULLSCREEN);
		GLOBAL_HIGHLIGHT = Utils.parseInt(getPrefString(c, KEY_S_HIGHLIGHT,
				DEFAULT_HIGHLIGHT));
		
		FORMAT_ALTITUDE = Utils.parseInt(getPrefString(c, KEY_S_UNITS_ALTITUDE, 
				DEFAULT_UNITS_ALTITUDE));
		FORMAT_ANGLE = Utils.parseInt(getPrefString(c, KEY_S_UNITS_ANGLE, 
				DEFAULT_UNITS_ANGLE));
		FORMAT_COO_LATLON = Utils.parseInt(getPrefString(c, KEY_S_UNITS_COO_LATLON, 
				DEFAULT_UNITS_COO_LATLON));
		FORMAT_LENGTH = Utils.parseInt(getPrefString(c, KEY_S_UNITS_LENGTH, 
				DEFAULT_UNITS_LENGTH));
		FORMAT_SPEED = Utils.parseInt(getPrefString(c, KEY_S_UNITS_SPEED, 
				DEFAULT_UNITS_SPEED));
		
		GPS_MIN_TIME = Utils.parseInt(getPrefString(c, KEY_S_GPS_MIN_TIME_NOTIFICATION, 
				DEFAULT_GPS_MIN_TIME_NOTIFICATION));
		GPS_BEEP_ON_GPS_FIX = getPrefBoolean(c, KEY_B_GPS_BEEP_ON_GPS_FIX,
				DEFAULT_GPS_BEEP_ON_GPS_FIX);
		GPS_ALTITUDE_CORRECTION = Utils.parseDouble(getPrefString(c, 
				KEY_S_GPS_ALTITUDE_MANUAL_CORRECTION, DEFAULT_GPS_ALTITUDE_MANUAL_CORRECTION));
		
		SENSOR_HARDWARE_COMPASS = getPrefBoolean(c, KEY_B_HARDWARE_COMPASS_SENSOR,
				DEFAULT_HARDWARE_COMPASS_SENSOR);
		SENSOR_HARDWARE_COMPASS_AUTO_CHANGE = getPrefBoolean(c, KEY_B_HARDWARE_COMPASS_AUTO_CHANGE,
				DEFAULT_HARDWARE_COMPASS_AUTO_CHANGE);
		SENSOR_HARDWARE_COMPASS_AUTO_CHANGE_VALUE = Utils.parseInt(getPrefString(c, KEY_S_HARDWARE_COMPASS_AUTO_CHANGE_VALUE,
				DEFAULT_HARDWARE_COMPASS_AUTO_CHANGE_VALUE));
		SENSOR_BEARING_TRUE = getPrefBoolean(c, KEY_B_SENSORS_BEARING_TRUE,
				DEFAULT_SENSORS_BEARING_TRUE);
		SENSOR_ORIENT_FILTER = Utils.parseInt(getPrefString(c, KEY_S_SENSORS_ORIENT_FILTER, 
				DEFAULT_SENSORS_ORIENT_FILTER));
		SENSOR_ORIENT_MODIF_PORTRAIT = Utils.parseInt(getPrefString(c, KEY_S_SENSOR_ORIENT_MODIF_PORTRAIT,
				DEFAULT_SENSOR_ORIENT_MODIF_PORTRAIT));
		SENSOR_ORIENT_MODIF_LANDSCAPE = Utils.parseInt(getPrefString(c, KEY_S_SENSOR_ORIENT_MODIF_LANDSCAPE,
				DEFAULT_SENSOR_ORIENT_MODIF_LANDSCAPE));

    	GUIDING_GPS_REQUIRED = getPrefBoolean(c, KEY_B_GUIDING_GPS_REQUIRED,
				DEFAULT_GUIDING_GPS_REQUIRED);
    	GUIDING_SOUNDS = getPrefBoolean(c, KEY_B_GUIDING_COMPASS_SOUNDS,
				DEFAULT_GUIDING_COMPASS_SOUNDS);
    	GUIDING_WAYPOINT_SOUND = Utils.parseInt(getPrefString(c, KEY_S_GUIDING_WAYPOINT_SOUND,
				DEFAULT_GUIDING_WAYPOINT_SOUND));
    	GUIDING_WAYPOINT_SOUND_DISTANCE = Utils.parseInt(getPrefString(c, KEY_S_GUIDING_WAYPOINT_SOUND_DISTANCE,
				DEFAULT_GUIDING_WAYPOINT_SOUND_DISTANCE));
	}
}
