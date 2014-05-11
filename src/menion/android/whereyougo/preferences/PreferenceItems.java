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

import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_GUIDING_WAYPOINT_SOUND;
import static menion.android.whereyougo.preferences.PreferenceValues.DEFAULT_LANGUAGE;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_GUIDING_WAYPOINT_SOUND;
import static menion.android.whereyougo.preferences.PreferenceValues.KEY_S_LANGUAGE;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_GUIDING_WAYPOINT_SOUND_BEEP_ON_DISTANCE;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND_URI;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_GUIDING_WAYPOINT_SOUND_INCREASE_CLOSER;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_AR;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_CZ;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_DA;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_DE;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_DEFAULT;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_EL;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_EN;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_ES;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_FI;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_FR;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_HU;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_IT;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_JA;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_KO;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_NL;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_PL;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_PT;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_PT_BR;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_RU;
import static menion.android.whereyougo.preferences.PreferenceValues.VALUE_LANGUAGE_SK;
import static menion.android.whereyougo.preferences.PreferenceValues.getPrefString;

import java.io.File;

import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.extension.activity.CustomPreferenceActivity;
import menion.android.whereyougo.maps.mapsforge.filepicker.FilePicker;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.FileSystem;
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
import android.text.InputType;

public class PreferenceItems {

  private static final String TAG = "SettingItems";

  private static final int REQUEST_GUIDING_WPT_SOUND = 0;

  private static final int REQUEST_ROOT = 1;

  private static ListPreference lastUsedPreference;

  public static void addPrefConfirmOnExit(CustomPreferenceActivity activity,
      PreferenceCategory category) {
    activity.addCheckBoxPreference(category, R.string.pref_confirm_on_exit,
        R.string.pref_confirm_on_exit_desc, PreferenceValues.KEY_B_CONFIRM_ON_EXIT,
        PreferenceValues.DEFAULT_CONFIRM_ON_EXIT);
  }

  public static void addPrefFullscreen(CustomPreferenceActivity activity,
      PreferenceCategory category) {
    activity.addCheckBoxPreference(category, R.string.pref_fullscreen,
        R.string.pref_fullscreen_desc, PreferenceValues.KEY_B_FULLSCREEN,
        PreferenceValues.DEFAULT_FULLSCREEN, new Preference.OnPreferenceChangeListener() {
          @Override
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            Preferences.GLOBAL_FULLSCREEN = Utils.parseBoolean(newValue);
            return true;
          }
        });
  }

  /**************************/
  /* GPS */
  /**************************/

  public static void addPrefGpsAltitudeManualCorrection(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    EditTextPreference pref =
        activity.addEditTextPreference(category, R.string.pref_gps_altitude_manual_correction,
            R.string.pref_gps_altitude_manual_correction_desc,
            PreferenceValues.KEY_S_GPS_ALTITUDE_MANUAL_CORRECTION,
            PreferenceValues.DEFAULT_GPS_ALTITUDE_MANUAL_CORRECTION, InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED,
            new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference pref, Object newValue) {
                Preferences.GPS_ALTITUDE_CORRECTION = Utils.parseDouble(newValue);
                setEditTextPreference(activity, (EditTextPreference) pref,
                    UtilsFormat.formatDouble(Preferences.GPS_ALTITUDE_CORRECTION, 2) + "m",
                    R.string.pref_gps_altitude_manual_correction_desc);
                return true;
              }
            });
    setEditTextPreference(activity, (EditTextPreference) pref,
        UtilsFormat.formatDouble(Preferences.GPS_ALTITUDE_CORRECTION, 2) + "m",
        R.string.pref_gps_altitude_manual_correction_desc);
  }

  public static void addPrefGpsBeepOnGpsFix(CustomPreferenceActivity activity,
      PreferenceCategory category) {
    activity.addCheckBoxPreference(category, R.string.pref_gps_beep_on_gps_fix,
        R.string.pref_gps_beep_on_gps_fix_desc, PreferenceValues.KEY_B_GPS_BEEP_ON_GPS_FIX,
        PreferenceValues.DEFAULT_GPS_BEEP_ON_GPS_FIX, new Preference.OnPreferenceChangeListener() {
          @Override
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            Preferences.GPS_BEEP_ON_GPS_FIX = Utils.parseBoolean(newValue);
            return true;
          }
        });
  }

  public static void addPrefGpsDisable(CustomPreferenceActivity activity,
      PreferenceCategory category) {
    activity.addCheckBoxPreference(category, R.string.pref_gps_disable,
        R.string.pref_gps_disable_desc, PreferenceValues.KEY_B_GPS_DISABLE_WHEN_HIDE,
        PreferenceValues.DEFAULT_GPS_DISABLE_WHEN_HIDE);
  }

  public static void addPrefGpsMinTime(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    EditTextPreference pref =
        activity.addEditTextPreference(category, R.string.pref_gps_min_time,
            R.string.pref_gps_min_time_desc, PreferenceValues.KEY_S_GPS_MIN_TIME_NOTIFICATION,
            PreferenceValues.DEFAULT_GPS_MIN_TIME_NOTIFICATION, InputType.TYPE_CLASS_NUMBER,
            new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference pref, Object newValue) {
                int value = Utils.parseInt(newValue);
                if (value >= 0) {
                  Preferences.GPS_MIN_TIME = value;
                  activity.needGpsRestart = true;
                  setEditTextPreference(activity, (EditTextPreference) pref,
                      Preferences.GPS_MIN_TIME + "s", R.string.pref_gps_min_time_desc);
                  return true;
                } else {
                  ManagerNotify.toastShortMessage(R.string.invalid_value);
                  return false;
                }
              }
            });
    setEditTextPreference(activity, (EditTextPreference) pref, Preferences.GPS_MIN_TIME + "s",
        R.string.pref_gps_min_time_desc);
  }

  /********************************/
  /* GUIDING */
  /********************************/

  public static void addPrefGuidingCompassSounds(CustomPreferenceActivity activity,
      PreferenceCategory category) {
    activity.addCheckBoxPreference(category, R.string.pref_guiding_compass_sounds,
        R.string.pref_guiding_compass_sounds_desc, PreferenceValues.KEY_B_GUIDING_COMPASS_SOUNDS,
        PreferenceValues.DEFAULT_GUIDING_COMPASS_SOUNDS,
        new Preference.OnPreferenceChangeListener() {
          @Override
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            setPrefGuidingCompassSounds(false, Utils.parseBoolean(newValue));
            return true;
          }
        });
  }

  public static void addPrefGuidingGpsRequired(CustomPreferenceActivity activity,
      PreferenceCategory category) {
    activity.addCheckBoxPreference(category, R.string.pref_guiding,
        R.string.pref_guiding_gps_required_desc, PreferenceValues.KEY_B_GUIDING_GPS_REQUIRED,
        PreferenceValues.DEFAULT_GUIDING_GPS_REQUIRED, new Preference.OnPreferenceChangeListener() {
          @Override
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            Preferences.GUIDING_GPS_REQUIRED = Utils.parseBoolean(newValue);
            return true;
          }
        });
  }

  public static void addPrefGuidingWptSound(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    CharSequence[] entries =
        new CharSequence[] {activity.getString(R.string.pref_guiding_waypoint_sound_increasing),
            activity.getString(R.string.pref_guiding_waypoint_sound_beep_on_distance),
            activity.getString(R.string.pref_guiding_waypoint_sound_custom_on_distance)};
    CharSequence[] entryValues =
        new CharSequence[] {String.valueOf(VALUE_GUIDING_WAYPOINT_SOUND_INCREASE_CLOSER),
            String.valueOf(VALUE_GUIDING_WAYPOINT_SOUND_BEEP_ON_DISTANCE),
            String.valueOf(VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND)};
    ListPreference pref =
        activity.addListPreference(category, R.string.pref_guiding_sound_type,
            R.string.pref_guiding_sound_type_waypoint_desc, KEY_S_GUIDING_WAYPOINT_SOUND,
            DEFAULT_GUIDING_WAYPOINT_SOUND, entries, entryValues,
            new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference pref, Object newValue) {
                int result = Utils.parseInt(newValue);
                if (result != VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND) {
                  setPrefGuidingWptSound(activity, (ListPreference) pref, Utils.parseInt(newValue));
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
    setPrefGuidingWptSound(activity, pref, Preferences.GUIDING_WAYPOINT_SOUND);
  }

  public static void addPrefGuidingWptSoundDistance(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    EditTextPreference pref =
        activity.addEditTextPreference(category, R.string.pref_guiding_sound_distance,
            R.string.pref_guiding_sound_distance_waypoint_desc,
            PreferenceValues.KEY_S_GUIDING_WAYPOINT_SOUND_DISTANCE,
            PreferenceValues.DEFAULT_GUIDING_WAYPOINT_SOUND_DISTANCE, InputType.TYPE_CLASS_NUMBER,
            new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference pref, Object newValue) {
                int value = Utils.parseInt(newValue);
                if (value > 0) {
                  Preferences.GUIDING_WAYPOINT_SOUND_DISTANCE = value;
                  setEditTextPreference(activity, (EditTextPreference) pref,
                      Preferences.GUIDING_WAYPOINT_SOUND_DISTANCE + "m",
                      R.string.pref_guiding_sound_distance_waypoint_desc);
                  return true;
                } else {
                  ManagerNotify.toastShortMessage(R.string.invalid_value);
                  return false;
                }
              }
            });
    setEditTextPreference(activity, pref, Preferences.GUIDING_WAYPOINT_SOUND_DISTANCE + "m",
        R.string.pref_guiding_sound_distance_waypoint_desc);
  }

  public static void addPrefGuidingZonePoint(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    CharSequence[] entries =
        new CharSequence[] {Locale.get(R.string.pref_guiding_zone_point_center),
            Locale.get(R.string.pref_guiding_zone_point_nearest)};
    CharSequence[] entryValues =
        new CharSequence[] {String.valueOf(PreferenceValues.VALUE_GUIDING_ZONE_POINT_CENTER),
            String.valueOf(PreferenceValues.VALUE_GUIDING_ZONE_POINT_NEAREST)};
    ListPreference pref =
        activity.addListPreference(category, R.string.pref_guiding_zone_point,
            R.string.pref_guiding_zone_point_desc, PreferenceValues.KEY_S_GUIDING_ZONE_POINT,
            PreferenceValues.DEFAULT_GUIDING_ZONE_POINT, entries, entryValues,
            new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference pref, Object newValue) {
                Preferences.GUIDING_ZONE_NAVIGATION_POINT = Utils.parseInt(newValue);
                setListPreference(activity, (ListPreference) pref,
                    Preferences.GUIDING_ZONE_NAVIGATION_POINT,
                    R.string.pref_guiding_zone_point_desc);
                return true;
              }
            });
    setListPreference(activity, (ListPreference) pref, Preferences.GUIDING_ZONE_NAVIGATION_POINT,
        R.string.pref_guiding_zone_point_desc);
  }

  public static void addPrefHighlight(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    CharSequence[] entries =
        new CharSequence[] {Locale.get(R.string.pref_highlight_off_text),
            Locale.get(R.string.pref_highlight_only_gps_text),
            Locale.get(R.string.pref_highlight_always_text)};
    CharSequence[] entryValues =
        new CharSequence[] {String.valueOf(PreferenceValues.VALUE_HIGHLIGHT_OFF),
            String.valueOf(PreferenceValues.VALUE_HIGHLIGHT_ONLY_GPS),
            String.valueOf(PreferenceValues.VALUE_HIGHLIGHT_ALWAYS)};
    ListPreference pref =
        activity.addListPreference(category, R.string.pref_highlight, R.string.pref_highlight_desc,
            PreferenceValues.KEY_S_HIGHLIGHT, PreferenceValues.DEFAULT_HIGHLIGHT, entries,
            entryValues, new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference pref, Object newValue) {
                Preferences.GLOBAL_HIGHLIGHT = Utils.parseInt(newValue);
                setListPreference(activity, (ListPreference) pref, Preferences.GLOBAL_HIGHLIGHT,
                    R.string.pref_highlight_desc);
                PreferenceValues.enableWakeLock();
                return true;
              }
            });
    setListPreference(activity, (ListPreference) pref, Preferences.GLOBAL_HIGHLIGHT,
        R.string.pref_highlight_desc);
  }

  /********************************/
  /* UNITS */
  /********************************/

  public static void addPrefLocal(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    CharSequence[] entries =
        new CharSequence[] {getLanguageText(VALUE_LANGUAGE_DEFAULT),
            getLanguageText(VALUE_LANGUAGE_AR), getLanguageText(VALUE_LANGUAGE_CZ),
            getLanguageText(VALUE_LANGUAGE_DA), getLanguageText(VALUE_LANGUAGE_DE),
            getLanguageText(VALUE_LANGUAGE_EL), getLanguageText(VALUE_LANGUAGE_EN),
            getLanguageText(VALUE_LANGUAGE_ES), getLanguageText(VALUE_LANGUAGE_FI),
            getLanguageText(VALUE_LANGUAGE_FR), getLanguageText(VALUE_LANGUAGE_HU),
            getLanguageText(VALUE_LANGUAGE_IT), getLanguageText(VALUE_LANGUAGE_JA),
            getLanguageText(VALUE_LANGUAGE_KO), getLanguageText(VALUE_LANGUAGE_NL),
            getLanguageText(VALUE_LANGUAGE_PL), getLanguageText(VALUE_LANGUAGE_PT),
            getLanguageText(VALUE_LANGUAGE_PT_BR), getLanguageText(VALUE_LANGUAGE_RU),
            getLanguageText(VALUE_LANGUAGE_SK)};
    CharSequence[] entryValues =
        new CharSequence[] {VALUE_LANGUAGE_DEFAULT, VALUE_LANGUAGE_AR, VALUE_LANGUAGE_CZ,
            VALUE_LANGUAGE_DA, VALUE_LANGUAGE_DE, VALUE_LANGUAGE_EL, VALUE_LANGUAGE_EN,
            VALUE_LANGUAGE_ES, VALUE_LANGUAGE_FI, VALUE_LANGUAGE_FR, VALUE_LANGUAGE_HU,
            VALUE_LANGUAGE_IT, VALUE_LANGUAGE_JA, VALUE_LANGUAGE_KO, VALUE_LANGUAGE_NL,
            VALUE_LANGUAGE_PL, VALUE_LANGUAGE_PT, VALUE_LANGUAGE_PT_BR, VALUE_LANGUAGE_RU,
            VALUE_LANGUAGE_SK};
    ListPreference pref =
        activity.addListPreference(category, R.string.pref_language, R.string.pref_language_desc,
            KEY_S_LANGUAGE, DEFAULT_LANGUAGE, entries, entryValues,
            new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference pref, Object newValue) {
                setPreferenceText(activity, pref, getLanguageText(String.valueOf(newValue)),
                    R.string.pref_language_desc);
                activity.needRestart = true;
                return true;
              }
            });
    setPreferenceText(
        activity,
        pref,
        getLanguageText(getPrefString(PreferenceValues.KEY_S_LANGUAGE,
            PreferenceValues.DEFAULT_LANGUAGE)), R.string.pref_language_desc);
  }

  public static void addPrefMapProvider(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    CharSequence[] entries =
        new CharSequence[] {Locale.get(R.string.pref_map_provider_vector),
            Locale.get(R.string.pref_map_provider_locus)};
    CharSequence[] entryValues =
        new CharSequence[] {String.valueOf(PreferenceValues.VALUE_MAP_PROVIDER_VECTOR),
            String.valueOf(PreferenceValues.VALUE_MAP_PROVIDER_LOCUS)};
    ListPreference pref =
        activity.addListPreference(category, R.string.pref_map_provider,
            R.string.pref_map_provider_desc, PreferenceValues.KEY_S_MAP_PROVIDER,
            PreferenceValues.DEFAULT_MAP_PROVIDER, entries, entryValues,
            new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference pref, Object newValue) {
                Preferences.GLOBAL_MAP_PROVIDER = Utils.parseInt(newValue);
                setListPreference(activity, (ListPreference) pref, Preferences.GLOBAL_MAP_PROVIDER,
                    R.string.pref_map_provider_desc);
                return true;
              }
            });
    setListPreference(activity, (ListPreference) pref, Preferences.GLOBAL_MAP_PROVIDER,
        R.string.pref_map_provider_desc);
  }

  /*****************************/
  /* GLOBAL */
  /*****************************/

  // GLOBAL

  public static void addPrefRoot(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    Preference pref =
        activity.addFilePreference(category, R.string.pref_root, R.string.pref_root_desc,
            PreferenceValues.KEY_S_ROOT, PreferenceValues.DEFAULT_ROOT, ".gwc", REQUEST_ROOT);
    setPreferenceText(activity, pref, Preferences.GLOBAL_ROOT, R.string.pref_root_desc);
  }

  public static void addPrefSavegameAuto(CustomPreferenceActivity activity,
      PreferenceCategory category) {
    activity.addCheckBoxPreference(category, R.string.pref_save_game_auto,
        R.string.pref_save_game_auto_desc, PreferenceValues.KEY_B_SAVEGAME_AUTO,
        PreferenceValues.DEFAULT_SAVEGAME_AUTO, new Preference.OnPreferenceChangeListener() {
          @Override
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            Preferences.GLOBAL_SAVEGAME_AUTO = Utils.parseBoolean(newValue);
            return true;
          }
        });
  }

  public static void addPrefSensorsBearingTrue(CustomPreferenceActivity activity,
      PreferenceCategory category) {
    activity.addCheckBoxPreference(category, R.string.pref_bearing_true,
        R.string.pref_bearing_true_desc, PreferenceValues.KEY_B_SENSORS_BEARING_TRUE,
        PreferenceValues.DEFAULT_SENSORS_BEARING_TRUE, new Preference.OnPreferenceChangeListener() {
          @Override
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            Preferences.SENSOR_BEARING_TRUE = Utils.parseBoolean(newValue);
            return true;
          }
        });
  }

  public static void addPrefSensorsCompassAutoChange(CustomPreferenceActivity activity,
      PreferenceCategory category) {
    activity.addCheckBoxPreference(category, R.string.pref_sensors_compass_auto_change,
        R.string.pref_sensors_compass_auto_change_desc,
        PreferenceValues.KEY_B_HARDWARE_COMPASS_AUTO_CHANGE,
        PreferenceValues.DEFAULT_HARDWARE_COMPASS_AUTO_CHANGE,
        new Preference.OnPreferenceChangeListener() {
          @Override
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            Preferences.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE = Utils.parseBoolean(newValue);
            A.getRotator().manageSensors();
            return true;
          }
        });
  }

  public static void addPrefSensorsCompassAutoChangeValue(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    EditTextPreference pref =
        activity.addEditTextPreference(category, R.string.pref_sensors_compass_auto_change_value,
            R.string.pref_sensors_compass_auto_change_value_desc,
            PreferenceValues.KEY_S_HARDWARE_COMPASS_AUTO_CHANGE_VALUE,
            PreferenceValues.DEFAULT_HARDWARE_COMPASS_AUTO_CHANGE_VALUE,
            InputType.TYPE_CLASS_NUMBER, new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference pref, Object newValue) {
                int value = Utils.parseInt(newValue);
                if (value > 0) {
                  Preferences.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE_VALUE = value;
                  setEditTextPreference(activity, (EditTextPreference) pref,
                      Preferences.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE_VALUE + "m/s",
                      R.string.pref_sensors_compass_auto_change_value_desc);
                  return true;
                } else {
                  ManagerNotify.toastShortMessage(R.string.invalid_value);
                  return false;
                }
              }
            });
    setEditTextPreference(activity, pref, Preferences.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE_VALUE
        + "m/s", R.string.pref_sensors_compass_auto_change_value_desc);
  }

  /***************************/
  /* SENSORS */
  /***************************/

  public static void addPrefSensorsCompassHardware(CustomPreferenceActivity activity,
      PreferenceCategory category) {
    activity.addCheckBoxPreference(category, R.string.pref_sensors_compass_hardware,
        R.string.pref_sensors_compass_hardware_desc,
        PreferenceValues.KEY_B_HARDWARE_COMPASS_SENSOR,
        PreferenceValues.DEFAULT_HARDWARE_COMPASS_SENSOR,
        new Preference.OnPreferenceChangeListener() {
          @Override
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            Preferences.SENSOR_HARDWARE_COMPASS = Utils.parseBoolean(newValue);
            A.getRotator().manageSensors();
            return true;
          }
        });
  }

  public static void addPrefSensorsOrienFilter(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    if (!Utils.isAndroid201OrMore())
      return;
    CharSequence[] entries =
        new CharSequence[] {Locale.get(R.string.pref_sensors_orient_filter_no_filter),
            Locale.get(R.string.pref_sensors_orient_filter_ligth),
            Locale.get(R.string.pref_sensors_orient_filter_medium),
            Locale.get(R.string.pref_sensors_orient_filter_heavy)};
    CharSequence[] entryValues =
        new CharSequence[] {String.valueOf(PreferenceValues.VALUE_SENSORS_ORIENT_FILTER_NO),
            String.valueOf(PreferenceValues.VALUE_SENSORS_ORIENT_FILTER_LIGHT),
            String.valueOf(PreferenceValues.VALUE_SENSORS_ORIENT_FILTER_MEDIUM),
            String.valueOf(PreferenceValues.VALUE_SENSORS_ORIENT_FILTER_HEAVY)};
    ListPreference pref =
        activity.addListPreference(category, R.string.pref_sensors_orient_filter,
            R.string.pref_sensors_orient_filter_desc, PreferenceValues.KEY_S_SENSORS_ORIENT_FILTER,
            PreferenceValues.DEFAULT_SENSORS_ORIENT_FILTER, entries, entryValues,
            new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference pref, Object newValue) {
                Preferences.SENSOR_ORIENT_FILTER = Utils.parseInt(newValue);
                setListPreference(activity, (ListPreference) pref,
                    Preferences.SENSOR_ORIENT_FILTER, R.string.pref_sensors_orient_filter_desc);
                return true;
              }
            });
    setListPreference(activity, (ListPreference) pref, Preferences.SENSOR_ORIENT_FILTER,
        R.string.pref_sensors_orient_filter_desc);
  }

  public static void addPrefStatusbar(CustomPreferenceActivity activity, PreferenceCategory category) {
    activity.addCheckBoxPreference(category, R.string.pref_statusbar, R.string.pref_statusbar_desc,
        PreferenceValues.KEY_B_STATUSBAR, PreferenceValues.DEFAULT_STATUSBAR,
        new Preference.OnPreferenceChangeListener() {
          @Override
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            Preferences.GLOBAL_STATUSBAR = Utils.parseBoolean(newValue);
            return true;
          }
        });
  }

  public static void addPrefUnitsAltitude(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    CharSequence[] entries =
        new CharSequence[] {Locale.get(R.string.metres), Locale.get(R.string.feet)};
    CharSequence[] entryValues =
        new CharSequence[] {String.valueOf(PreferenceValues.VALUE_UNITS_ALTITUDE_METRES),
            String.valueOf(PreferenceValues.VALUE_UNITS_ALTITUDE_FEET)};
    ListPreference pref =
        activity.addListPreference(category, R.string.pref_units_altitude,
            R.string.pref_units_altitude_desc, PreferenceValues.KEY_S_UNITS_ALTITUDE,
            PreferenceValues.DEFAULT_UNITS_ALTITUDE, entries, entryValues,
            new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference pref, Object newValue) {
                Preferences.FORMAT_ALTITUDE = Utils.parseInt(newValue);
                setListPreference(activity, (ListPreference) pref, Preferences.FORMAT_ALTITUDE,
                    R.string.pref_units_altitude_desc);
                return true;
              }
            });
    setListPreference(activity, (ListPreference) pref, Preferences.FORMAT_ALTITUDE,
        R.string.pref_units_altitude_desc);
  }

  public static void addPrefUnitsAngle(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    CharSequence[] entries =
        new CharSequence[] {activity.getString(R.string.pref_units_angle_degree),
            activity.getString(R.string.pref_units_angle_mil)};
    CharSequence[] entryValues =
        new CharSequence[] {String.valueOf(PreferenceValues.VALUE_UNITS_ANGLE_DEGREE),
            String.valueOf(PreferenceValues.VALUE_UNITS_ANGLE_MIL)};
    ListPreference pref =
        activity.addListPreference(category, R.string.pref_units_angle,
            R.string.pref_units_angle_desc, PreferenceValues.KEY_S_UNITS_ANGLE,
            PreferenceValues.DEFAULT_UNITS_ANGLE, entries, entryValues,
            new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference pref, Object newValue) {
                Preferences.FORMAT_ANGLE = Utils.parseInt(newValue);
                setListPreference(activity, (ListPreference) pref, Preferences.FORMAT_ANGLE,
                    R.string.pref_units_angle_desc);
                return true;
              }
            });
    setListPreference(activity, (ListPreference) pref, Preferences.FORMAT_ANGLE,
        R.string.pref_units_angle_desc);
  }

  public static void addPrefUnitsCooLatLon(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    CharSequence[] entries =
        new CharSequence[] {Locale.get(R.string.pref_units_coo_latlon_dec),
            Locale.get(R.string.pref_units_coo_latlon_min),
            Locale.get(R.string.pref_units_coo_latlon_sec)};
    CharSequence[] entryValues =
        new CharSequence[] {String.valueOf(PreferenceValues.VALUE_UNITS_COO_LATLON_DEC),
            String.valueOf(PreferenceValues.VALUE_UNITS_COO_LATLON_MIN),
            String.valueOf(PreferenceValues.VALUE_UNITS_COO_LATLON_SEC)};
    ListPreference pref =
        activity.addListPreference(category, R.string.pref_units_coo_latlon,
            R.string.pref_units_coo_latlon_desc, PreferenceValues.KEY_S_UNITS_COO_LATLON,
            PreferenceValues.DEFAULT_UNITS_COO_LATLON, entries, entryValues,
            new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference pref, Object newValue) {
                Preferences.FORMAT_COO_LATLON = Utils.parseInt(newValue);
                setListPreference(activity, (ListPreference) pref, Preferences.FORMAT_COO_LATLON,
                    R.string.pref_units_coo_latlon_desc);
                return true;
              }
            });
    setListPreference(activity, (ListPreference) pref, Preferences.FORMAT_COO_LATLON,
        R.string.pref_units_coo_latlon_desc);
  }

  public static void addPrefUnitsLength(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    CharSequence[] entries =
        new CharSequence[] {Locale.get(R.string.pref_units_length_me),
            Locale.get(R.string.pref_units_length_im), Locale.get(R.string.pref_units_length_na)};
    CharSequence[] entryValues =
        new CharSequence[] {String.valueOf(PreferenceValues.VALUE_UNITS_LENGTH_ME),
            String.valueOf(PreferenceValues.VALUE_UNITS_LENGTH_IM),
            String.valueOf(PreferenceValues.VALUE_UNITS_LENGTH_NA)};
    ListPreference pref =
        activity.addListPreference(category, R.string.pref_units_length,
            R.string.pref_units_length_desc, PreferenceValues.KEY_S_UNITS_LENGTH,
            PreferenceValues.DEFAULT_UNITS_LENGTH, entries, entryValues,
            new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference pref, Object newValue) {
                Preferences.FORMAT_LENGTH = Utils.parseInt(newValue);
                setListPreference(activity, (ListPreference) pref, Preferences.FORMAT_LENGTH,
                    R.string.pref_units_length_desc);
                return true;
              }
            });
    setListPreference(activity, (ListPreference) pref, Preferences.FORMAT_LENGTH,
        R.string.pref_units_length_desc);
  }

  public static void addPrefUnitsSpeed(final CustomPreferenceActivity activity,
      PreferenceCategory category) {
    CharSequence[] entries = new CharSequence[] {"km/h", "miles/h", "knots"};
    CharSequence[] entryValues =
        new CharSequence[] {String.valueOf(PreferenceValues.VALUE_UNITS_SPEED_KMH),
            String.valueOf(PreferenceValues.VALUE_UNITS_SPEED_MILH),
            String.valueOf(PreferenceValues.VALUE_UNITS_SPEED_KNOTS)};
    ListPreference pref =
        activity.addListPreference(category, R.string.pref_units_speed,
            R.string.pref_units_speed_desc, PreferenceValues.KEY_S_UNITS_SPEED,
            PreferenceValues.DEFAULT_UNITS_SPEED, entries, entryValues,
            new Preference.OnPreferenceChangeListener() {
              @Override
              public boolean onPreferenceChange(Preference pref, Object newValue) {
                Preferences.FORMAT_SPEED = Utils.parseInt(newValue);
                setListPreference(activity, (ListPreference) pref, Preferences.FORMAT_SPEED,
                    R.string.pref_units_speed_desc);
                return true;
              }
            });
    setListPreference(activity, (ListPreference) pref, Preferences.FORMAT_SPEED,
        R.string.pref_units_speed_desc);
  }

  private static String getFormatedText(boolean enabled, String value, String desc) {
    StringBuffer buff = new StringBuffer();
    buff.append("(");
    buff.append(value);
    buff.append(") ");
    buff.append(desc);
    return buff.toString();
  }

  private static String getLanguageText(String value) {
    if (value.equals(VALUE_LANGUAGE_DEFAULT)) {
      return Locale.get(R.string.pref_language_default);
    } else if (value.equals(VALUE_LANGUAGE_AR)) {
      return Locale.get(R.string.pref_language_ar);
    } else if (value.equals(VALUE_LANGUAGE_CZ)) {
      return Locale.get(R.string.pref_language_cs);
    } else if (value.equals(VALUE_LANGUAGE_DA)) {
      return Locale.get(R.string.pref_language_da);
    } else if (value.equals(VALUE_LANGUAGE_DE)) {
      return Locale.get(R.string.pref_language_de);
    } else if (value.equals(VALUE_LANGUAGE_EL)) {
      return Locale.get(R.string.pref_language_el);
    } else if (value.equals(VALUE_LANGUAGE_EN)) {
      return Locale.get(R.string.pref_language_en);
    } else if (value.equals(VALUE_LANGUAGE_ES)) {
      return Locale.get(R.string.pref_language_es);
    } else if (value.equals(VALUE_LANGUAGE_FI)) {
      return Locale.get(R.string.pref_language_fi);
    } else if (value.equals(VALUE_LANGUAGE_FR)) {
      return Locale.get(R.string.pref_language_fr);
    } else if (value.equals(VALUE_LANGUAGE_HU)) {
      return Locale.get(R.string.pref_language_hu);
    } else if (value.equals(VALUE_LANGUAGE_IT)) {
      return Locale.get(R.string.pref_language_it);
    } else if (value.equals(VALUE_LANGUAGE_JA)) {
      return Locale.get(R.string.pref_language_ja);
    } else if (value.equals(VALUE_LANGUAGE_KO)) {
      return Locale.get(R.string.pref_language_ko);
    } else if (value.equals(VALUE_LANGUAGE_NL)) {
      return Locale.get(R.string.pref_language_nl);
    } else if (value.equals(VALUE_LANGUAGE_PL)) {
      return Locale.get(R.string.pref_language_pl);
    } else if (value.equals(VALUE_LANGUAGE_PT)) {
      return Locale.get(R.string.pref_language_pt);
    } else if (value.equals(VALUE_LANGUAGE_PT_BR)) {
      return Locale.get(R.string.pref_language_pt_br);
    } else if (value.equals(VALUE_LANGUAGE_RU)) {
      return Locale.get(R.string.pref_language_ru);
    } else if (value.equals(VALUE_LANGUAGE_SK)) {
      return Locale.get(R.string.pref_language_sk);
    } else {
      return "";
    }
  }

  public static void handleResponse(CustomPreferenceActivity activity, int requestCode,
      int resultCode, Intent data) {
    if (requestCode == REQUEST_GUIDING_WPT_SOUND) {
      if (resultCode == Activity.RESULT_OK && data != null) {
        Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
        // Uri uri =
        // data.getData();//getStringExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI));
        if (uri != null) {
          Logger.d(TAG, "uri:" + uri.toString());
          PreferenceValues.setPrefString(KEY_S_GUIDING_WAYPOINT_SOUND,
              String.valueOf(VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND));
          PreferenceValues.setPrefString(VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND_URI,
              uri.toString());
          setPrefGuidingWptSound(activity, lastUsedPreference,
              VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND);
        }
      }
      lastUsedPreference = null;
    } else if (requestCode == REQUEST_ROOT) {
      if (resultCode == Activity.RESULT_OK && data != null) {
        String filename = data.getStringExtra(FilePicker.SELECTED_FILE);
        if (filename != null) {
          File file = new File(filename);
          String dir = file.getParent();
          PreferenceValues.setPrefString(PreferenceValues.KEY_S_ROOT, dir);
          Preferences.GLOBAL_ROOT = dir;
          Preference pref = activity.findPreference(PreferenceValues.KEY_S_ROOT);
          setPreferenceText(activity, pref, dir, R.string.pref_root_desc);
          FileSystem.setRootDirectory(null, dir);
          MainActivity.refreshCartridges();
        }
      }
    }
  }

  private static void setEditTextPreference(Activity activity, EditTextPreference pref,
      String value, int desc) {
    pref.setSummary(getFormatedText(pref.isEnabled(), value, activity.getString(desc)));
  }

  public static void setListPreference(Activity activity, ListPreference pref, int index, int desc) {
    setListPreference(activity, pref, index, activity.getString(desc));
  }

  public static void setListPreference(Activity activity, ListPreference pref, int index,
      String desc) {
    pref.setSummary(getFormatedText(pref.isEnabled(), pref.getEntries()[index].toString(), desc));
  }

  private static void setPreferenceText(Activity activity, Preference pref, String value, int desc) {
    pref.setSummary(getFormatedText(pref.isEnabled(), value, activity.getString(desc)));
  }

  public static void setPrefGuidingCompassSounds(boolean saveToPref, boolean value) {
    if (saveToPref) {
      PreferenceValues.setPrefBoolean(PreferenceValues.KEY_B_GUIDING_COMPASS_SOUNDS, value);
    }
    Preferences.GUIDING_SOUNDS = value;
  }

  private static void setPrefGuidingWptSound(CustomPreferenceActivity activity,
      ListPreference pref, int value) {
    Preferences.GUIDING_WAYPOINT_SOUND = value;
    setListPreference(activity, pref, value, R.string.pref_guiding_sound_type_waypoint_desc);
  }
}
