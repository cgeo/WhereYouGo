package menion.android.whereyougo.gui.fragments.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import menion.android.whereyougo.R;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.ManagerNotify;
import menion.android.whereyougo.utils.Utils;

public class SettingsCompassFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.whereyougo_preferences_compass, rootKey);

        CheckBoxPreference compassUseHardware = findPreference(Preferences.getKey(R.string.pref_KEY_B_SENSOR_HARDWARE_COMPASS));
        CheckBoxPreference compassAutoChange = findPreference(Preferences.getKey(R.string.pref_KEY_B_HARDWARE_COMPASS_AUTO_CHANGE));
        prepareCompassAutoChangeValue();
        CheckBoxPreference bearingUseTrue = findPreference(Preferences.getKey(R.string.pref_KEY_B_SENSORS_BEARING_TRUE));
        prepareOrientationFilter();

        if (compassUseHardware != null) {
            compassUseHardware.setOnPreferenceChangeListener((preference, o) -> {
                boolean newValue = (boolean) o;
                Preferences.SENSOR_HARDWARE_COMPASS = Utils.parseBoolean(newValue);
                A.getRotator().manageSensors();
                return true;
            });
        }
        if (compassAutoChange != null) {
            compassAutoChange.setOnPreferenceChangeListener((preference, o) -> {
                boolean newValue = (boolean) o;
                Preferences.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE = Utils.parseBoolean(newValue);
                A.getRotator().manageSensors();
                return true;
            });
        }
        if (bearingUseTrue != null) {
            bearingUseTrue.setOnPreferenceChangeListener((preference, o) -> {
                boolean newValue = (boolean) o;
                Preferences.SENSOR_BEARING_TRUE = Utils.parseBoolean(newValue);
                return true;
            });
        }
    }

    private void prepareCompassAutoChangeValue() {
        EditTextPreference compassAutoChangeValue = findPreference(Preferences.getKey(R.string.pref_KEY_S_HARDWARE_COMPASS_AUTO_CHANGE_VALUE));
        if (compassAutoChangeValue != null) {
            compassAutoChangeValue.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                int value = Utils.parseInt(newValue);
                if (value > 0) {
                    Preferences.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE_VALUE = value;
                } else {
                    ManagerNotify.toastShortMessage(R.string.invalid_value);
                }
                return true;
            });
            compassAutoChangeValue.setSummaryProvider(preference -> {
                SharedPreferences preferences = preference.getSharedPreferences();
                String currentValue = preferences.getString(preference.getKey(), "");
                if (!currentValue.equals("")) {
                    return getString(R.string.pref_sensors_compass_auto_change_value_summary, currentValue);
                }
                return getString(R.string.pref_sensors_compass_auto_change_value_desc);
            });
        }
    }

    private void prepareOrientationFilter() {
        ListPreference orientationFilter = findPreference(Preferences.getKey(R.string.pref_KEY_S_SENSORS_ORIENT_FILTER));
        if (orientationFilter != null) {
            orientationFilter.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.SENSOR_ORIENT_FILTER = Utils.parseInt(newValue);
                return true;
            });
            orientationFilter.setSummaryProvider(preference -> {
                SharedPreferences preferences = preference.getSharedPreferences();
                String current_value = preferences.getString(preference.getKey(), "");
                switch (current_value != null ? current_value : "") {
                    case "0":
                        return getString(R.string.pref_sensors_orient_filter_summary, getString(R.string.pref_sensors_orient_filter_no_filter));
                    case "1":
                        return getString(R.string.pref_sensors_orient_filter_summary, getString(R.string.pref_sensors_orient_filter_ligth));
                    case "2":
                        return getString(R.string.pref_sensors_orient_filter_summary, getString(R.string.pref_sensors_orient_filter_medium));
                    case "3":
                        return getString(R.string.pref_sensors_orient_filter_summary, getString(R.string.pref_sensors_orient_filter_heavy));
                    default:
                        return getString(R.string.pref_sensors_orient_filter_desc);
                }
            });
        }
    }
}
