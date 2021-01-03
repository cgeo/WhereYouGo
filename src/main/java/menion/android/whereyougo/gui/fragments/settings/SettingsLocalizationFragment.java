package menion.android.whereyougo.gui.fragments.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;

import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.StringToken;
import menion.android.whereyougo.utils.Utils;

public class SettingsLocalizationFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.whereyougo_preferences_localization, rootKey);

        // ListPreference language = findPreference(Preferences.getKey(R.string.pref_KEY_S_LANGUAGE));
        ListPreference coordFormat = findPreference(Preferences.getKey(R.string.pref_KEY_S_UNITS_COO_LATLON));
        ListPreference lengthUnit = findPreference(Preferences.getKey(R.string.pref_KEY_S_UNITS_LENGTH));
        ListPreference altitudeUnit = findPreference(Preferences.getKey(R.string.pref_KEY_S_UNITS_ALTITUDE));
        ListPreference speedUnit = findPreference(Preferences.getKey(R.string.pref_KEY_S_UNITS_SPEED));
        ListPreference angleUnit = findPreference(Preferences.getKey(R.string.pref_KEY_S_UNITS_ANGLE));

        // Language has nothing to do when its changed.
        if (coordFormat != null) {
            coordFormat.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.FORMAT_COO_LATLON = Utils.parseInt(newValue);
                return true;
            });
        }
        if (lengthUnit != null) {
            lengthUnit.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.FORMAT_LENGTH = Utils.parseInt(newValue);
                return true;
            });
        }
        if (altitudeUnit != null) {
            altitudeUnit.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.FORMAT_ALTITUDE = Utils.parseInt(newValue);
                return true;
            });
        }
        if (speedUnit != null) {
            speedUnit.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.FORMAT_SPEED = Utils.parseInt(newValue);
                return true;
            });
        }
        if (angleUnit != null) {
            angleUnit.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.FORMAT_ANGLE = Utils.parseInt(newValue);
                return true;
            });
        }
    }
}
