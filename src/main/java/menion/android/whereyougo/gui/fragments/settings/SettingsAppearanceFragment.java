package menion.android.whereyougo.gui.fragments.settings;

import android.os.Bundle;

import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import menion.android.whereyougo.R;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.Utils;

public class SettingsAppearanceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.whereyougo_preferences_appearance, rootKey);

        CheckBoxPreference statusbarIcon = findPreference(Preferences.getKey(R.string.pref_KEY_B_STATUSBAR));
        CheckBoxPreference applicationInFullscreen = findPreference(Preferences.getKey(R.string.pref_KEY_B_FULLSCREEN));
        ListPreference alwaysOn = findPreference(Preferences.getKey(R.string.pref_KEY_S_HIGHLIGHT));
        ListPreference fontSize = findPreference(Preferences.getKey(R.string.pref_KEY_S_FONT_SIZE));
        CheckBoxPreference imageStretch = findPreference(Preferences.getKey(R.string.pref_KEY_B_IMAGE_STRETCH));

        if (statusbarIcon != null) {
            statusbarIcon.setOnPreferenceChangeListener((preference, o) -> {
                boolean newValue = (boolean) o;
                Preferences.APPEARANCE_STATUSBAR = Utils.parseBoolean(newValue);
                return true;
            });
        }
        if (applicationInFullscreen != null) {
            applicationInFullscreen.setOnPreferenceChangeListener((preference, o) -> {
                boolean newValue = (boolean) o;
                Preferences.APPEARANCE_FULLSCREEN = Utils.parseBoolean(newValue);
                // Restart Activity to run fullscreen
                // TODO: Relaunch specific Settings Fragment
                getActivity().finish();
                startActivity(getActivity().getIntent());
                return true;
            });
        }
        if (alwaysOn != null) {
            alwaysOn.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.APPEARANCE_HIGHLIGHT = Utils.parseInt(newValue);
                PreferenceValues.enableWakeLock();
                return true;
            });
        }
        if (fontSize != null) {
            fontSize.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.APPEARANCE_FONT_SIZE = Utils.parseInt(newValue);
                // Restart Activity to change Theme
                // TODO: Relaunch specific Settings Fragment
                getActivity().finish();
                startActivity(getActivity().getIntent());
                return true;
            });
        }
        if (imageStretch != null) {
            imageStretch.setOnPreferenceChangeListener((preference, o) -> {
                boolean newValue = (boolean) o;
                Preferences.APPEARANCE_IMAGE_STRETCH = Utils.parseBoolean(newValue);
                return true;
            });
        }

    }
}
