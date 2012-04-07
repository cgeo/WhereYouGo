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
import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.extension.CustomPreferenceActivity;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.ManagerNotify;
import menion.android.whereyougo.utils.Utils;
import menion.android.whereyougo.utils.UtilsFormat;
import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;

public class SettingItems {
	
	private static final String TAG = "SettingItems";
	
	/*****************************/
	/*           GLOBAL          */
	/*****************************/
	
	// GLOBAL
	
	public static void addPrefFullscreen(CustomPreferenceActivity activity, 
			PreferenceCategory category) {
		activity.addCheckBoxPreference(category,
				R.string.pref_fullscreen,
				R.string.pref_fullscreen_desc,
				Settings.KEY_B_FULLSCREEN,
				Settings.DEFAULT_FULLSCREEN,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						setPrefFullscreen(false, Utils.parseBoolean(newValue));
						return true;
					}
				});
	}
	
	public static void setPrefFullscreen(boolean saveToPref, boolean value) {
		if (saveToPref) {
			Settings.setPrefBoolean(Settings.KEY_B_FULLSCREEN, value);
		}
		SettingValues.GLOBAL_FULLSCREEN = value;
	}
	
	public static void addPrefConfirmOnExit(CustomPreferenceActivity activity, 
			PreferenceCategory category) {
		activity.addCheckBoxPreference(category,
				R.string.pref_confirm_on_exit,
				R.string.pref_confirm_on_exit_desc,
				Settings.KEY_B_CONFIRM_ON_EXIT,
				Settings.DEFAULT_CONFIRM_ON_EXIT);
	}
	
	public static void addPrefHighlight(final CustomPreferenceActivity activity, 
			PreferenceCategory category) {
        CharSequence[] entries = new CharSequence[] {
        		Loc.get(R.string.pref_highlight_off_text),
        		Loc.get(R.string.pref_highlight_only_gps_text),
        		Loc.get(R.string.pref_highlight_always_text)};
        CharSequence[] entryValues = new CharSequence[] {
        		String.valueOf(Settings.VALUE_HIGHLIGHT_OFF),
        		String.valueOf(Settings.VALUE_HIGHLIGHT_ONLY_GPS),
        		String.valueOf(Settings.VALUE_HIGHLIGHT_ALWAYS)};
		ListPreference pref = activity.addListPreference(category,
				R.string.pref_highlight,
				R.string.pref_highlight_desc,
				Settings.KEY_S_HIGHLIGHT,
				Settings.DEFAULT_HIGHLIGHT,
				entries, entryValues,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref, Object newValue) {
						SettingValues.GLOBAL_HIGHLIGHT = Utils.parseInt(newValue);
						setListPreference(activity, (ListPreference) pref,
								SettingValues.GLOBAL_HIGHLIGHT, R.string.pref_highlight_desc);
						Settings.enableWakeLock();
						return true;
					}
				});
		setListPreference(activity, (ListPreference) pref,
				SettingValues.GLOBAL_HIGHLIGHT, R.string.pref_highlight_desc);
	}
	
	/**************************/
	/*           GPS          */
	/**************************/
	
	public static void addPrefGpsAltitudeManualCorrection(final CustomPreferenceActivity activity, 
			PreferenceCategory category) {
		EditTextPreference pref = activity.addEditTextPreference(category,
				R.string.pref_gps_altitude_manual_correction,
				R.string.pref_gps_altitude_manual_correction_desc,
				Settings.KEY_S_GPS_ALTITUDE_MANUAL_CORRECTION,
				Settings.DEFAULT_GPS_ALTITUDE_MANUAL_CORRECTION,
				InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL |
				InputType.TYPE_NUMBER_FLAG_SIGNED,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref, Object newValue) {
						SettingValues.GPS_ALTITUDE_CORRECTION = Utils.parseDouble(newValue);
						setEditTextPreference(activity, (EditTextPreference) pref,
								UtilsFormat.formatDouble(SettingValues.GPS_ALTITUDE_CORRECTION, 2) + "m",
								R.string.pref_gps_altitude_manual_correction_desc);
						return true;
					}
				});
		setEditTextPreference(activity, (EditTextPreference) pref,
				UtilsFormat.formatDouble(SettingValues.GPS_ALTITUDE_CORRECTION, 2) + "m",
				R.string.pref_gps_altitude_manual_correction_desc);
	}

	public static void addPrefGpsMinTime(final CustomPreferenceActivity activity, 
			PreferenceCategory category) {
		EditTextPreference pref = activity.addEditTextPreference(category,
				R.string.pref_gps_min_time,
				R.string.pref_gps_min_time_desc,
				Settings.KEY_S_GPS_MIN_TIME_NOTIFICATION,
				Settings.DEFAULT_GPS_MIN_TIME_NOTIFICATION,
				InputType.TYPE_CLASS_NUMBER,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref, Object newValue) {
						int value = Utils.parseInt(newValue);
						if (value >= 0) {
							SettingValues.GPS_MIN_TIME = value;
							activity.needGpsRestart = true;
							setEditTextPreference(activity, (EditTextPreference) pref,
									SettingValues.GPS_MIN_TIME + "s",
									R.string.pref_gps_min_time_desc);
							return true;
						} else {
							ManagerNotify.toastShortMessage(R.string.invalid_value);
							return false;
						}
					}
				});
		setEditTextPreference(activity, (EditTextPreference) pref,
				SettingValues.GPS_MIN_TIME + "s",
				R.string.pref_gps_min_time_desc);
	}
	
	public static void addPrefGpsBeepOnGpsFix(CustomPreferenceActivity activity, 
			PreferenceCategory category) {
		activity.addCheckBoxPreference(category,
				R.string.pref_gps_beep_on_gps_fix,
				R.string.pref_gps_beep_on_gps_fix_desc,
				Settings.KEY_B_GPS_BEEP_ON_GPS_FIX,
				Settings.DEFAULT_GPS_BEEP_ON_GPS_FIX,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						SettingValues.GPS_BEEP_ON_GPS_FIX = Utils.parseBoolean(newValue);
						return true;
					}
				});
	}
	
	public static void addPrefGpsDisable(CustomPreferenceActivity activity, 
			PreferenceCategory category) {
		activity.addCheckBoxPreference(category,
				R.string.pref_gps_disable,
				R.string.pref_gps_disable_desc,
				Settings.KEY_B_GPS_DISABLE_WHEN_HIDE,
				Settings.DEFAULT_GPS_DISABLE_WHEN_HIDE);
	}
	
	/***************************/
	/*         SENSORS         */
	/***************************/
	
	public static void addPrefSensorsCompassHardware(CustomPreferenceActivity activity, 
			PreferenceCategory category) {
		activity.addCheckBoxPreference(category,
				R.string.pref_sensors_compass_hardware,
				R.string.pref_sensors_compass_hardware_desc,
				Settings.KEY_B_HARDWARE_COMPASS_SENSOR,
				Settings.DEFAULT_HARDWARE_COMPASS_SENSOR,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						SettingValues.SENSOR_HARDWARE_COMPASS = Utils.parseBoolean(newValue);
						A.getRotator().manageSensors();
						return true;
					}
				});
	}
	
	public static void addPrefSensorsCompassAutoChange(CustomPreferenceActivity activity, 
			PreferenceCategory category) {
		activity.addCheckBoxPreference(category,
				R.string.pref_sensors_compass_auto_change,
				R.string.pref_sensors_compass_auto_change_desc,
				Settings.KEY_B_HARDWARE_COMPASS_AUTO_CHANGE,
				Settings.DEFAULT_HARDWARE_COMPASS_AUTO_CHANGE,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						SettingValues.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE = Utils.parseBoolean(newValue);
						A.getRotator().manageSensors();
						return true;
					}
				});
	}
	
	public static void addPrefSensorsCompassAutoChangeValue(final CustomPreferenceActivity activity, 
			PreferenceCategory category) {
		EditTextPreference pref = activity.addEditTextPreference(category,
				R.string.pref_sensors_compass_auto_change_value,
				R.string.pref_sensors_compass_auto_change_value_desc,
				Settings.KEY_S_HARDWARE_COMPASS_AUTO_CHANGE_VALUE,
				Settings.DEFAULT_HARDWARE_COMPASS_AUTO_CHANGE_VALUE,
				InputType.TYPE_CLASS_NUMBER,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref, Object newValue) {
						int value = Utils.parseInt(newValue);
						if (value > 0) {
							SettingValues.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE_VALUE = value;
							setEditTextPreference(activity, (EditTextPreference) pref,
									SettingValues.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE_VALUE + "m/s",
									R.string.pref_sensors_compass_auto_change_value_desc);
							return true;
						} else {
							ManagerNotify.toastShortMessage(R.string.invalid_value);
							return false;
						}
					}
				});
		setEditTextPreference(activity, pref,
				SettingValues.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE_VALUE + "m/s",
				R.string.pref_sensors_compass_auto_change_value_desc);
	}
	
	public static void addPrefSensorsBearingTrue(CustomPreferenceActivity activity, 
			PreferenceCategory category) {
		activity.addCheckBoxPreference(category,
				R.string.pref_bearing_true,
				R.string.pref_bearing_true_desc,
				Settings.KEY_B_SENSORS_BEARING_TRUE,
				Settings.DEFAULT_SENSORS_BEARING_TRUE,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						SettingValues.SENSOR_BEARING_TRUE = Utils.parseBoolean(newValue);
						return true;
					}
				});
	}
	
	public static void addPrefSensorsOrienFilter(final CustomPreferenceActivity activity, 
			PreferenceCategory category) {
		if (!Utils.isAndroid201OrMore())
			return;
        CharSequence[] entries = new CharSequence[] {
        		Loc.get(R.string.pref_sensors_orient_filter_no_filter),
        		Loc.get(R.string.pref_sensors_orient_filter_ligth),
        		Loc.get(R.string.pref_sensors_orient_filter_medium),
        		Loc.get(R.string.pref_sensors_orient_filter_heavy)};
        CharSequence[] entryValues = new CharSequence[] {
        		String.valueOf(Settings.VALUE_SENSORS_ORIENT_FILTER_NO),
        		String.valueOf(Settings.VALUE_SENSORS_ORIENT_FILTER_LIGHT),
        		String.valueOf(Settings.VALUE_SENSORS_ORIENT_FILTER_MEDIUM),
        		String.valueOf(Settings.VALUE_SENSORS_ORIENT_FILTER_HEAVY)};
		ListPreference pref = activity.addListPreference(category,
				R.string.pref_sensors_orient_filter,
				R.string.pref_sensors_orient_filter_desc,
				Settings.KEY_S_SENSORS_ORIENT_FILTER,
				Settings.DEFAULT_SENSORS_ORIENT_FILTER,
				entries, entryValues,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref, Object newValue) {
						SettingValues.SENSOR_ORIENT_FILTER = Utils.parseInt(newValue);
						setListPreference(activity, (ListPreference) pref,
								SettingValues.SENSOR_ORIENT_FILTER, R.string.pref_sensors_orient_filter_desc);
						return true;
					}
				});
		setListPreference(activity, (ListPreference) pref,
				SettingValues.SENSOR_ORIENT_FILTER, R.string.pref_sensors_orient_filter_desc);
	}
	
	/********************************/
	/*            GUIDING           */
	/********************************/
	
	public static void addPrefGuidingCompassSounds(CustomPreferenceActivity activity, 
			PreferenceCategory category) {
		activity.addCheckBoxPreference(category,
				R.string.pref_guiding_compass_sounds,
				R.string.pref_guiding_compass_sounds_desc,
				Settings.KEY_B_GUIDING_COMPASS_SOUNDS,
				Settings.DEFAULT_GUIDING_COMPASS_SOUNDS,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						setPrefGuidingCompassSounds(false, Utils.parseBoolean(newValue));
						return true;
					}
				});
	}
	
	public static void setPrefGuidingCompassSounds(boolean saveToPref, boolean value) {
		if (saveToPref) {
			Settings.setPrefBoolean(Settings.KEY_B_GUIDING_COMPASS_SOUNDS, value);
		}
		SettingValues.GUIDING_SOUNDS = value;
	}
	
	public static void addPrefGuidingGpsRequired(CustomPreferenceActivity activity, 
			PreferenceCategory category) {
		activity.addCheckBoxPreference(category,
				R.string.pref_guiding,
				R.string.pref_guiding_gps_required_desc,
				Settings.KEY_B_GUIDING_GPS_REQUIRED,
				Settings.DEFAULT_GUIDING_GPS_REQUIRED,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						SettingValues.GUIDING_GPS_REQUIRED = Utils.parseBoolean(newValue);
						return true;
					}
				});
	}
	
	public static void addPrefGuidingWptSound(final CustomPreferenceActivity activity, 
			PreferenceCategory category) {
        CharSequence[] entries = new CharSequence[] {
        		activity.getString(R.string.pref_guiding_waypoint_sound_increasing),
        		activity.getString(R.string.pref_guiding_waypoint_sound_beep_on_distance),
        		activity.getString(R.string.pref_guiding_waypoint_sound_custom_on_distance)};
        CharSequence[] entryValues = new CharSequence[] {
        		String.valueOf(VALUE_GUIDING_WAYPOINT_SOUND_INCREASE_CLOSER),
        		String.valueOf(VALUE_GUIDING_WAYPOINT_SOUND_BEEP_ON_DISTANCE),
        		String.valueOf(VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND)};
		ListPreference pref = activity.addListPreference(category,
				R.string.pref_guiding_sound_type,
				R.string.pref_guiding_sound_type_waypoint_desc,
				KEY_S_GUIDING_WAYPOINT_SOUND,
				DEFAULT_GUIDING_WAYPOINT_SOUND,
				entries, entryValues,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref, Object newValue) {
						int result = Utils.parseInt(newValue);
						if (result != VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND) {
							setPrefGuidingWptSound(activity, (ListPreference) pref,
									Utils.parseInt(newValue));
							return true;
						} else {
							lastUsedPreference = (ListPreference) pref;
							Intent intent = new Intent(Intent.ACTION_PICK);
							intent.setType("audio/*");
							if (!Utils.isIntentAvailable(intent)) {
								intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
							}
							activity.startActivityForResult(intent, REQUEST_GUIDING_WPT_SOUND);
							return false;
						}
					}
				});
		setPrefGuidingWptSound(activity, pref,
				SettingValues.GUIDING_WAYPOINT_SOUND);
	}
	
	private static void setPrefGuidingWptSound(CustomPreferenceActivity activity,
			ListPreference pref, int value) {
		SettingValues.GUIDING_WAYPOINT_SOUND = value;
		setListPreference(activity, pref, value,
        		R.string.pref_guiding_sound_type_waypoint_desc);
	}
	
	public static void addPrefGuidingWptSoundDistance(final CustomPreferenceActivity activity, 
			PreferenceCategory category) {
		EditTextPreference pref = activity.addEditTextPreference(category,
				R.string.pref_guiding_sound_distance,
				R.string.pref_guiding_sound_distance_waypoint_desc,
				Settings.KEY_S_GUIDING_WAYPOINT_SOUND_DISTANCE,
				Settings.DEFAULT_GUIDING_WAYPOINT_SOUND_DISTANCE,
				InputType.TYPE_CLASS_NUMBER,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref, Object newValue) {
						int value = Utils.parseInt(newValue);
						if (value > 0) {
							SettingValues.GUIDING_WAYPOINT_SOUND_DISTANCE = value;
							setEditTextPreference(activity, (EditTextPreference) pref,
									SettingValues.GUIDING_WAYPOINT_SOUND_DISTANCE + "m",
									R.string.pref_guiding_sound_distance_waypoint_desc);
							return true;
						} else {
							ManagerNotify.toastShortMessage(R.string.invalid_value);
							return false;
						}
					}
				});
		setEditTextPreference(activity, pref,
				SettingValues.GUIDING_WAYPOINT_SOUND_DISTANCE + "m",
				R.string.pref_guiding_sound_distance_waypoint_desc);
	}
	
	/********************************/
	/*             UNITS            */
	/********************************/
	
	public static void addPrefLocal(final CustomPreferenceActivity activity, 
			PreferenceCategory category) {
        CharSequence[] entries = new CharSequence[] {
        		getLanguageText(VALUE_LANGUAGE_DEFAULT),
        		getLanguageText(VALUE_LANGUAGE_AR),
        		getLanguageText(VALUE_LANGUAGE_CZ),
        		getLanguageText(VALUE_LANGUAGE_DA),
        		getLanguageText(VALUE_LANGUAGE_DE),
        		getLanguageText(VALUE_LANGUAGE_EL),
        		getLanguageText(VALUE_LANGUAGE_EN),
        		getLanguageText(VALUE_LANGUAGE_ES),
        		getLanguageText(VALUE_LANGUAGE_FI),
        		getLanguageText(VALUE_LANGUAGE_FR),
        		getLanguageText(VALUE_LANGUAGE_HU),
        		getLanguageText(VALUE_LANGUAGE_IT),
        		getLanguageText(VALUE_LANGUAGE_JA),
        		getLanguageText(VALUE_LANGUAGE_KO),
        		getLanguageText(VALUE_LANGUAGE_NL),
        		getLanguageText(VALUE_LANGUAGE_PL),
        		getLanguageText(VALUE_LANGUAGE_PT),
        		getLanguageText(VALUE_LANGUAGE_PT_BR),
        		getLanguageText(VALUE_LANGUAGE_RU),
        		getLanguageText(VALUE_LANGUAGE_SK)};
        CharSequence[] entryValues = new CharSequence[] {
        		VALUE_LANGUAGE_DEFAULT,
        		VALUE_LANGUAGE_AR,
        		VALUE_LANGUAGE_CZ,
        		VALUE_LANGUAGE_DA,
        		VALUE_LANGUAGE_DE,
        		VALUE_LANGUAGE_EL,
        		VALUE_LANGUAGE_EN,
        		VALUE_LANGUAGE_ES,
        		VALUE_LANGUAGE_FI,
        		VALUE_LANGUAGE_FR,
        		VALUE_LANGUAGE_HU,
        		VALUE_LANGUAGE_IT,
        		VALUE_LANGUAGE_JA,
        		VALUE_LANGUAGE_KO,
        		VALUE_LANGUAGE_NL,
        		VALUE_LANGUAGE_PL,
        		VALUE_LANGUAGE_PT,
        		VALUE_LANGUAGE_PT_BR,
        		VALUE_LANGUAGE_RU,
        		VALUE_LANGUAGE_SK};
		ListPreference pref = activity.addListPreference(category,
				R.string.pref_language,
				R.string.pref_language_desc,
				KEY_S_LANGUAGE,
				DEFAULT_LANGUAGE,
				entries, entryValues,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref, Object newValue) {
						setPreferenceText(activity, pref,
								getLanguageText(String.valueOf(newValue)),
								R.string.pref_language_desc);
						activity.needRestart = true;
						return true;
					}
				});
		setPreferenceText(activity, pref,
				getLanguageText(getPrefString(Settings.KEY_S_LANGUAGE, Settings.DEFAULT_LANGUAGE)),
				R.string.pref_language_desc);
	}
	
	private static String getLanguageText(String value) {
		if (value.equals(VALUE_LANGUAGE_DEFAULT)) {
			return Loc.get(R.string.pref_language_default);
		} else if (value.equals(VALUE_LANGUAGE_AR)) {
			return Loc.get(R.string.pref_language_ar);
		} else if (value.equals(VALUE_LANGUAGE_CZ)) {
			return Loc.get(R.string.pref_language_cs);
		} else if (value.equals(VALUE_LANGUAGE_DA)) {
			return Loc.get(R.string.pref_language_da);
		} else if (value.equals(VALUE_LANGUAGE_DE)) {
			return Loc.get(R.string.pref_language_de);
		} else if (value.equals(VALUE_LANGUAGE_EL)) {
			return Loc.get(R.string.pref_language_el);
		} else if (value.equals(VALUE_LANGUAGE_EN)) {
			return Loc.get(R.string.pref_language_en);
		} else if (value.equals(VALUE_LANGUAGE_ES)) {
			return Loc.get(R.string.pref_language_es);
		} else if (value.equals(VALUE_LANGUAGE_FI)) {
			return Loc.get(R.string.pref_language_fi);
		} else if (value.equals(VALUE_LANGUAGE_FR)) {
			return Loc.get(R.string.pref_language_fr);
		} else if (value.equals(VALUE_LANGUAGE_HU)) {
			return Loc.get(R.string.pref_language_hu);
		} else if (value.equals(VALUE_LANGUAGE_IT)) {
			return Loc.get(R.string.pref_language_it);
		} else if (value.equals(VALUE_LANGUAGE_JA)) {
			return Loc.get(R.string.pref_language_ja);
		} else if (value.equals(VALUE_LANGUAGE_KO)) {
			return Loc.get(R.string.pref_language_ko);
		} else if (value.equals(VALUE_LANGUAGE_NL)) {
			return Loc.get(R.string.pref_language_nl);
		} else if (value.equals(VALUE_LANGUAGE_PL)) {
			return Loc.get(R.string.pref_language_pl);
		} else if (value.equals(VALUE_LANGUAGE_PT)) {
			return Loc.get(R.string.pref_language_pt);
		} else if (value.equals(VALUE_LANGUAGE_PT_BR)) {
			return Loc.get(R.string.pref_language_pt_br);
		} else if (value.equals(VALUE_LANGUAGE_RU)) {
			return Loc.get(R.string.pref_language_ru);
		} else if (value.equals(VALUE_LANGUAGE_SK)) {
			return Loc.get(R.string.pref_language_sk);
		} else {
			return "";
		}
	}
	
	public static void addPrefUnitsAngle(final CustomPreferenceActivity activity, 
			PreferenceCategory category) {
        CharSequence[] entries = new CharSequence[] {
        		activity.getString(R.string.pref_units_angle_degree),
        		activity.getString(R.string.pref_units_angle_mil)};
        CharSequence[] entryValues = new CharSequence[] {
        		String.valueOf(Settings.VALUE_UNITS_ANGLE_DEGREE),
        		String.valueOf(Settings.VALUE_UNITS_ANGLE_MIL)};
		ListPreference pref = activity.addListPreference(category,
				R.string.pref_units_angle,
				R.string.pref_units_angle_desc,
				Settings.KEY_S_UNITS_ANGLE,
				Settings.DEFAULT_UNITS_ANGLE,
				entries, entryValues,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref, Object newValue) {
						SettingValues.FORMAT_ANGLE = Utils.parseInt(newValue);
						setListPreference(activity, (ListPreference) pref,
								SettingValues.FORMAT_ANGLE, R.string.pref_units_angle_desc);
						return true;
					}
				});
		setListPreference(activity, (ListPreference) pref,
				SettingValues.FORMAT_ANGLE, R.string.pref_units_angle_desc);
	}
	
	public static void addPrefUnitsCooLatLon(final CustomPreferenceActivity activity, 
			PreferenceCategory category) {
        CharSequence[] entries = new CharSequence[] {
        		Loc.get(R.string.pref_units_coo_latlon_dec),
        		Loc.get(R.string.pref_units_coo_latlon_min),
        		Loc.get(R.string.pref_units_coo_latlon_sec)};
        CharSequence[] entryValues = new CharSequence[] {
        		String.valueOf(Settings.VALUE_UNITS_COO_LATLON_DEC),
        		String.valueOf(Settings.VALUE_UNITS_COO_LATLON_MIN),
        		String.valueOf(Settings.VALUE_UNITS_COO_LATLON_SEC)};
		ListPreference pref = activity.addListPreference(category,
				R.string.pref_units_coo_latlon,
				R.string.pref_units_coo_latlon_desc,
				Settings.KEY_S_UNITS_COO_LATLON,
				Settings.DEFAULT_UNITS_COO_LATLON,
				entries, entryValues,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref, Object newValue) {
						SettingValues.FORMAT_COO_LATLON = Utils.parseInt(newValue);
						setListPreference(activity, (ListPreference) pref,
								SettingValues.FORMAT_COO_LATLON, R.string.pref_units_coo_latlon_desc);
						return true;
					}
				});
		setListPreference(activity, (ListPreference) pref,
				SettingValues.FORMAT_COO_LATLON, R.string.pref_units_coo_latlon_desc);
	}
	
	public static void addPrefUnitsLength(final CustomPreferenceActivity activity, 
			PreferenceCategory category) {
        CharSequence[] entries = new CharSequence[] {
        		Loc.get(R.string.pref_units_length_me),
        		Loc.get(R.string.pref_units_length_im),
        		Loc.get(R.string.pref_units_length_na)};
        CharSequence[] entryValues = new CharSequence[] {
        		String.valueOf(Settings.VALUE_UNITS_LENGTH_ME),
        		String.valueOf(Settings.VALUE_UNITS_LENGTH_IM),
        		String.valueOf(Settings.VALUE_UNITS_LENGTH_NA)};
		ListPreference pref = activity.addListPreference(category,
				R.string.pref_units_length,
				R.string.pref_units_length_desc,
				Settings.KEY_S_UNITS_LENGTH,
				Settings.DEFAULT_UNITS_LENGTH,
				entries, entryValues,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref, Object newValue) {
						SettingValues.FORMAT_LENGTH = Utils.parseInt(newValue);
						setListPreference(activity, (ListPreference) pref,
								SettingValues.FORMAT_LENGTH, R.string.pref_units_length_desc);
						return true;
					}
				});
		setListPreference(activity, (ListPreference) pref,
				SettingValues.FORMAT_LENGTH, R.string.pref_units_length_desc);
	}
	
	public static void addPrefUnitsAltitude(final CustomPreferenceActivity activity, 
			PreferenceCategory category) {
        CharSequence[] entries = new CharSequence[] {
        		Loc.get(R.string.metres),
        		Loc.get(R.string.feet)};
        CharSequence[] entryValues = new CharSequence[] {
        		String.valueOf(Settings.VALUE_UNITS_ALTITUDE_METRES),
        		String.valueOf(Settings.VALUE_UNITS_ALTITUDE_FEET)};
		ListPreference pref = activity.addListPreference(category,
				R.string.pref_units_altitude,
				R.string.pref_units_altitude_desc,
				Settings.KEY_S_UNITS_ALTITUDE,
				Settings.DEFAULT_UNITS_ALTITUDE,
				entries, entryValues,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref, Object newValue) {
						SettingValues.FORMAT_ALTITUDE = Utils.parseInt(newValue);
						setListPreference(activity, (ListPreference) pref,
								SettingValues.FORMAT_ALTITUDE, R.string.pref_units_altitude_desc);
						return true;
					}
				});
		setListPreference(activity, (ListPreference) pref,
				SettingValues.FORMAT_ALTITUDE, R.string.pref_units_altitude_desc);
	}
	
	public static void addPrefUnitsSpeed(final CustomPreferenceActivity activity, 
			PreferenceCategory category) {
        CharSequence[] entries = new CharSequence[] {
        		"km/h",
        		"miles/h",
        		"knots"};
        CharSequence[] entryValues = new CharSequence[] {
        		String.valueOf(Settings.VALUE_UNITS_SPEED_KMH),
        		String.valueOf(Settings.VALUE_UNITS_SPEED_MILH),
        		String.valueOf(Settings.VALUE_UNITS_SPEED_KNOTS)};
		ListPreference pref = activity.addListPreference(category,
				R.string.pref_units_speed,
				R.string.pref_units_speed_desc,
				Settings.KEY_S_UNITS_SPEED,
				Settings.DEFAULT_UNITS_SPEED,
				entries, entryValues,
				new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref, Object newValue) {
						SettingValues.FORMAT_SPEED = Utils.parseInt(newValue);
						setListPreference(activity, (ListPreference) pref,
								SettingValues.FORMAT_SPEED, R.string.pref_units_speed_desc);
						return true;
					}
				});
		setListPreference(activity, (ListPreference) pref,
				SettingValues.FORMAT_SPEED, R.string.pref_units_speed_desc);
	}
	
	private static void setPreferenceText(Activity activity, Preference pref,
			String value, int desc) {
		pref.setSummary(getFormatedText(
				pref.isEnabled(), value, activity.getString(desc)));
	}
	
	public static void setListPreference(Activity activity, ListPreference pref,
			int index, int desc) {
		setListPreference(activity, pref, index, activity.getString(desc));
	}
	
	public static void setListPreference(Activity activity, ListPreference pref,
			int index, String desc) {
		pref.setSummary(getFormatedText(
				pref.isEnabled(), pref.getEntries()[index].toString(), desc));
	}
	
	private static void setEditTextPreference(Activity activity, EditTextPreference pref,
			String value, int desc) {
		pref.setSummary(getFormatedText(
				pref.isEnabled(), value, activity.getString(desc)));
	}
	
	public static final String HIGHLIGHTED_COLOR = "#ff8706";//"#ffb018";
	
	private static Spanned getFormatedText(boolean enabled, String value, String desc) {
//Logger.d(TAG, "getFormatedText(" + enabled + ", " + value + ", " + desc + ")");
		StringBuffer buff = new StringBuffer("<b><font color=\"");
		//buff.append(enabled ? "#ffb018" : "#ffb018");
		buff.append(HIGHLIGHTED_COLOR);
		buff.append("\">(");
		buff.append(value);
		buff.append(")</font></b> ");
		buff.append(desc);
		return Html.fromHtml(buff.toString());
	}
	
	private static final int REQUEST_GUIDING_WPT_SOUND = 0; 
	
	private static ListPreference lastUsedPreference;
	
	public static void handleResponse(CustomPreferenceActivity activity,
			int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_GUIDING_WPT_SOUND) {
			if (resultCode == Activity.RESULT_OK && data != null) {
				Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				//Uri uri = data.getData();//getStringExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI));
				if (uri != null) {
					Logger.d(TAG, "uri:" + uri.toString());
					Settings.setPrefString(KEY_S_GUIDING_WAYPOINT_SOUND,
							String.valueOf(VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND));
					Settings.setPrefString(VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND_URI,
							uri.toString());
					setPrefGuidingWptSound(activity, lastUsedPreference,
							VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND);
				}
			}
			lastUsedPreference = null;
		}
	}
}