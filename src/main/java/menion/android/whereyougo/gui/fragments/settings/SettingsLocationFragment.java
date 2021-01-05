package menion.android.whereyougo.gui.fragments.settings;

import android.os.Build;
import android.os.Bundle;

import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import menion.android.whereyougo.R;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.Utils;

public class SettingsLocationFragment extends PreferenceFragmentCompat {
    private static final String TAG = SettingsGlobalFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.whereyougo_preferences_location, rootKey);

        // TODO - Preferences.GPS_MIN_TIME is used but there is no settings option - default value?

        CheckBoxPreference allowGps = findPreference(Preferences.getKey(R.string.pref_KEY_B_GPS));
        EditTextPreference adjustAltitude = findPreference(Preferences.getKey(R.string.pref_KEY_S_GPS_ALTITUDE_MANUAL_CORRECTION));
        CheckBoxPreference beepOnGpsFix = findPreference(Preferences.getKey(R.string.pref_KEY_B_GPS_BEEP_ON_GPS_FIX));
        prepareDisableWhenHidden();
        prepareGuidingGpsRequired();

        if (allowGps != null) {
            allowGps.setOnPreferenceChangeListener((preference, o) -> {
                boolean newValue = (boolean) o;
                Preferences.GPS_START_AUTOMATICALLY = Utils.parseBoolean(newValue);
                return true;
            });
        }
        if (adjustAltitude != null) {
            adjustAltitude.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.GPS_ALTITUDE_CORRECTION = Utils.parseDouble(newValue);
                return true;
            });
        }
        if (beepOnGpsFix != null) {
            beepOnGpsFix.setOnPreferenceChangeListener((preference, o) -> {
                boolean newValue = (boolean) o;
                Preferences.GPS_BEEP_ON_GPS_FIX = Utils.parseBoolean(newValue);
                return true;
            });
        }
    }

    private void prepareDisableWhenHidden() {
        CheckBoxPreference disableWhenHidden = findPreference(Preferences.getKey(R.string.pref_KEY_B_GPS_DISABLE_WHEN_HIDE));
        if (disableWhenHidden != null) {
            disableWhenHidden.setOnPreferenceChangeListener((preference, o) -> {
                boolean newValue = (boolean) o;
                Preferences.GPS_DISABLE_WHEN_HIDE = Utils.parseBoolean(newValue);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    CheckBoxPreference status_bar = findPreference(Preferences.getKey(R.string.pref_KEY_B_STATUSBAR));
                    CheckBoxPreference gps_guideing = findPreference(Preferences.getKey(R.string.pref_KEY_B_GUIDING_GPS_REQUIRED));
                    CheckBoxPreference screen_off = findPreference(Preferences.getKey(R.string.pref_KEY_B_RUN_SCREEN_OFF));
                    if (status_bar != null && gps_guideing != null && screen_off != null) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P
                            && screen_off.isChecked()) {
                            status_bar.setEnabled(false);
                        } else {
                            if (newValue) {
                                status_bar.setEnabled(!gps_guideing.isChecked());
                            } else {
                                status_bar.setEnabled(false);
                            }
                        }
                    } else {
                        Logger.e(TAG, "One of the required settings was missing. (KEY_B_STATUSBAR," +
                            " KEY_B_GUIDING_GPS_REQUIRED, KEY_B_RUN_SCREEN_OFF)");
                    }
                }
                return true;
            });
        }
    }

    private void prepareGuidingGpsRequired() {
        CheckBoxPreference guidingGpsRequired = findPreference(Preferences.getKey(R.string.pref_KEY_B_GUIDING_GPS_REQUIRED));
        if (guidingGpsRequired != null) {
            guidingGpsRequired.setOnPreferenceChangeListener((preference, o) -> {
                boolean newValue = (boolean) o;
                Preferences.GUIDING_GPS_REQUIRED = Utils.parseBoolean(newValue);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    CheckBoxPreference status_bar = findPreference(Preferences.getKey(R.string.pref_KEY_B_STATUSBAR));
                    CheckBoxPreference screen_off = findPreference(Preferences.getKey(R.string.pref_KEY_B_RUN_SCREEN_OFF));
                    if (status_bar != null && screen_off != null) {
                        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P && screen_off.isChecked()) {
                            status_bar.setEnabled(false);
                        } else {
                            status_bar.setEnabled(!newValue);
                        }
                    } else {
                        Logger.e(TAG, "One of the required settings was missing. (KEY_B_STATUSBAR," +
                            " KEY_B_RUN_SCREEN_OFF)");
                    }
                }
                return true;
            });
        }
    }
}
