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

        ListPreference language = findPreference(Preferences.getKey(R.string.pref_KEY_S_LANGUAGE));
        ListPreference coordFormat = findPreference(Preferences.getKey(R.string.pref_KEY_S_UNITS_COO_LATLON));
        ListPreference lengthUnit = findPreference(Preferences.getKey(R.string.pref_KEY_S_UNITS_LENGTH));
        ListPreference altitudeUnit = findPreference(Preferences.getKey(R.string.pref_KEY_S_UNITS_ALTITUDE));
        ListPreference speedUnit = findPreference(Preferences.getKey(R.string.pref_KEY_S_UNITS_SPEED));
        ListPreference angleUnit = findPreference(Preferences.getKey(R.string.pref_KEY_S_UNITS_ANGLE));

        if (language != null) {
            language.setOnPreferenceChangeListener((preference, o) -> {
                String lang = (String) o;
                ArrayList<String> loc = StringToken.parse(lang, "_");
                Configuration config = getActivity().getBaseContext().getResources().getConfiguration();
                java.util.Locale locale;
                if ("default".equals(lang)) {
                    locale = java.util.Locale.getDefault();
                } else if (loc.size() == 1) {
                    locale = new java.util.Locale(lang);
                } else if (loc.size() == 2) {
                    locale = new java.util.Locale(loc.get(0), loc.get(1));
                } else {
                    locale = config.locale;
                }
                if (!config.locale.getLanguage().equals(locale.getLanguage())) {
                    config.setLocale(locale);
                    Resources activityResources = getActivity().getBaseContext().getResources();
                    activityResources.updateConfiguration(config, activityResources.getDisplayMetrics());
                    //restartApp();
                }
                return true;
            });
        }
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

    private void restartApp() {
        Context settingsContext = getActivity();
        Context applicationContext = settingsContext.getApplicationContext();
        Intent mStartActivity = new Intent(settingsContext, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(
            applicationContext, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }
}
