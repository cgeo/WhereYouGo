package menion.android.whereyougo.gui.fragments.settings;

import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import menion.android.whereyougo.R;
import menion.android.whereyougo.network.LoginTask;
import menion.android.whereyougo.preferences.Preferences;

public class SettingsCredentialsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.whereyougo_preferences_credentials, rootKey);

        EditTextPreference gc_username = findPreference(Preferences.getKey(R.string.pref_KEY_S_GC_USERNAME));
        EditTextPreference gc_password = findPreference(Preferences.getKey(R.string.pref_KEY_S_GC_PASSWORD));
        Preference checkLogin = findPreference(Preferences.getKey(R.string.pref_KEY_S_GC_CHECK));

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
        if (checkLogin != null) {
            checkLogin.setOnPreferenceClickListener(preference -> {
                // TODO: Check for non empty username and password before
                LoginTask loginTask = new LoginTask(Preferences.GC_USERNAME, Preferences.GC_PASSWORD);
                loginTask.execute();
                return true;
            });
        }
    }
}
