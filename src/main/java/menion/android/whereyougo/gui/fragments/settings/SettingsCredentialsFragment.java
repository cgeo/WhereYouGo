package menion.android.whereyougo.gui.fragments.settings;

import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import menion.android.whereyougo.R;
import menion.android.whereyougo.preferences.Preferences;

public class SettingsCredentialsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.whereyougo_preferences_credentials, rootKey);

        EditTextPreference gc_username = findPreference(Preferences.getKey(R.string.pref_KEY_S_GC_USERNAME));
        EditTextPreference gc_password = findPreference(Preferences.getKey(R.string.pref_KEY_S_GC_PASSWORD));

        if (gc_username != null) {
            gc_username.setOnPreferenceChangeListener((preference, o) -> {
                Preferences.GC_USERNAME = (String) o;
                return true;
            });
        }
        if (gc_password != null) {
            gc_password.setOnPreferenceChangeListener((preference, o) -> {
                Preferences.GC_PASSWORD = (String) o;
                return true;
            });
        }
    }
}
