package menion.android.whereyougo.gui.fragments.settings;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.Utils;

public class SettingsLocalizationFragment extends PreferenceFragmentCompat {

    private static String TAG = "SettingsLocalizationFragment";

    private boolean restartRequired = false;
    private String currentLanguage;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.whereyougo_preferences_localization, rootKey);

        prepareLanguage();
        prepareCoordFormat();
        prepareLengthUnit();
        prepareAltitudeUnit();
        prepareSpeedUnit();
        prepareAngleUnit();
    }

    private void prepareLanguage() {
        ListPreference language = findPreference(Preferences.getKey(R.string.pref_KEY_S_LANGUAGE));
        if (language != null) {
            currentLanguage = language.getValue();
            language.setSummaryProvider(preference -> {
                SharedPreferences preferences = preference.getSharedPreferences();
                String currentValue = preferences.getString(preference.getKey(), "");
                if (currentValue != null && currentValue.equals("default")) {
                    return getString(R.string.pref_language_desc);
                }
                return getString(R.string.pref_language_summary, currentValue);
            });
            language.setOnPreferenceChangeListener((preference, o) -> {
                String newLanguage = (String) o;
                restartRequired = !(newLanguage.equals(currentLanguage));
                return true;
            });
        }
    }

    private void prepareCoordFormat() {
        ListPreference coordFormat = findPreference(Preferences.getKey(R.string.pref_KEY_S_UNITS_COO_LATLON));
        if (coordFormat != null) {
            coordFormat.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.FORMAT_COO_LATLON = Utils.parseInt(newValue);
                return true;
            });
            coordFormat.setSummaryProvider(preference -> {
                SharedPreferences preferences = preference.getSharedPreferences();
                String current_value = preferences.getString(preference.getKey(), "");
                switch (current_value != null ? current_value : "") {
                    case "0":
                        return getString(R.string.pref_units_coo_latlon_summary, getString(R.string.pref_units_coo_latlon_dec));
                    case "1":
                        return getString(R.string.pref_units_coo_latlon_summary, getString(R.string.pref_units_coo_latlon_min));
                    case "2":
                        return getString(R.string.pref_units_coo_latlon_summary, getString(R.string.pref_units_coo_latlon_sec));
                    default:
                        return getString(R.string.pref_units_coo_latlon_desc);
                }
            });
        }
    }

    private void prepareLengthUnit() {
        ListPreference lengthUnit = findPreference(Preferences.getKey(R.string.pref_KEY_S_UNITS_LENGTH));
        if (lengthUnit != null) {
            lengthUnit.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.FORMAT_LENGTH = Utils.parseInt(newValue);
                return true;
            });
            lengthUnit.setSummaryProvider(preference -> {
                SharedPreferences preferences = preference.getSharedPreferences();
                String current_value = preferences.getString(preference.getKey(), "");
                switch (current_value != null ? current_value : "") {
                    case "0":
                        return getString(R.string.pref_units_summary, getString(R.string.pref_units_length_me_m));
                    case "1":
                        return getString(R.string.pref_units_summary, getString(R.string.pref_units_length_me_mkm));
                    case "2":
                        return getString(R.string.pref_units_summary, getString(R.string.pref_units_length_im_f));
                    case "3":
                        return getString(R.string.pref_units_summary, getString(R.string.pref_units_length_im_fm));
                    case "4":
                        return getString(R.string.pref_units_summary, getString(R.string.pref_units_length_im_y));
                    case "5":
                        return getString(R.string.pref_units_summary, getString(R.string.pref_units_length_im_ym));
                    case "6":
                        return getString(R.string.pref_units_summary, getString(R.string.pref_units_length_na_nmi));
                    default:
                        return getString(R.string.pref_units_length_desc);
                }
            });
        }
    }

    private void prepareAltitudeUnit() {
        ListPreference altitudeUnit = findPreference(Preferences.getKey(R.string.pref_KEY_S_UNITS_ALTITUDE));
        if (altitudeUnit != null) {
            altitudeUnit.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.FORMAT_ALTITUDE = Utils.parseInt(newValue);
                return true;
            });
            altitudeUnit.setSummaryProvider(preference -> {
                SharedPreferences preferences = preference.getSharedPreferences();
                String current_value = preferences.getString(preference.getKey(), "");
                switch (current_value != null ? current_value : "") {
                    case "0":
                        return getString(R.string.pref_units_summary, getString(R.string.metres));
                    case "1":
                        return getString(R.string.pref_units_summary, getString(R.string.feet));
                    default:
                        return getString(R.string.pref_units_altitude_desc);
                }
            });
        }
    }

    private void prepareSpeedUnit() {
        ListPreference speedUnit = findPreference(Preferences.getKey(R.string.pref_KEY_S_UNITS_SPEED));
        if (speedUnit != null) {
            speedUnit.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.FORMAT_SPEED = Utils.parseInt(newValue);
                return true;
            });
            speedUnit.setSummaryProvider(preference -> {
                SharedPreferences preferences = preference.getSharedPreferences();
                String current_value = preferences.getString(preference.getKey(), "");
                Resources res = getResources(); //assuming in an activity for example, otherwise you can provide a context.
                String[] speedUnitLabels = res.getStringArray(R.array.pref_units_speed_entries);
                switch (current_value != null ? current_value : "") {
                    case "0":
                        return getString(R.string.pref_units_summary, speedUnitLabels[0]);
                    case "1":
                        return getString(R.string.pref_units_summary, speedUnitLabels[1]);
                    case "2":
                        return getString(R.string.pref_units_summary, speedUnitLabels[2]);
                    case "3":
                        return getString(R.string.pref_units_summary, speedUnitLabels[3]);
                    default:
                        return getString(R.string.pref_units_speed_desc);
                }
            });
        }
    }

    private void prepareAngleUnit() {
        ListPreference angleUnit = findPreference(Preferences.getKey(R.string.pref_KEY_S_UNITS_ANGLE));
        if (angleUnit != null) {
            angleUnit.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.FORMAT_ANGLE = Utils.parseInt(newValue);
                return true;
            });
            angleUnit.setSummaryProvider(preference -> {
                SharedPreferences preferences = preference.getSharedPreferences();
                String current_value = preferences.getString(preference.getKey(), "");
                switch (current_value != null ? current_value : "") {
                    case "0":
                        return getString(R.string.pref_units_summary, getString(R.string.pref_units_angle_degree));
                    case "1":
                        return getString(R.string.pref_units_summary, getString(R.string.pref_units_angle_angular_mil));
                    case "2":
                        return getString(R.string.pref_units_summary, getString(R.string.pref_units_angle_russian_mil));
                    case "3":
                        return getString(R.string.pref_units_summary, getString(R.string.pref_units_angle_us_artillery_mil));
                    default:
                        return getString(R.string.pref_units_length_desc);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            if (restartRequired) {
                A.getMain().showDialogFinish(MainActivity.FINISH_RESTART);
            }
        } catch (Exception e) {
            Logger.e(TAG, "onDestroy()", e);
        }
    }
}
