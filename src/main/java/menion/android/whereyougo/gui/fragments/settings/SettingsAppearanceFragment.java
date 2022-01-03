package menion.android.whereyougo.gui.fragments.settings;

import android.content.SharedPreferences;
import android.os.Build;
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
        prepareAlwaysOn();
        prepareFontSize();
        CheckBoxPreference imageStretch = findPreference(Preferences.getKey(R.string.pref_KEY_B_IMAGE_STRETCH));

        if (statusbarIcon != null) {
            if (Preferences.GLOBAL_RUN_SCREEN_OFF && Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                statusbarIcon.setEnabled(false);
            }
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
        if (imageStretch != null) {
            imageStretch.setOnPreferenceChangeListener((preference, o) -> {
                boolean newValue = (boolean) o;
                Preferences.APPEARANCE_IMAGE_STRETCH = Utils.parseBoolean(newValue);
                return true;
            });
        }
    }

    private void prepareAlwaysOn() {
        ListPreference alwaysOn = findPreference(Preferences.getKey(R.string.pref_KEY_S_HIGHLIGHT));
        if (alwaysOn != null) {
            alwaysOn.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.APPEARANCE_HIGHLIGHT = Utils.parseInt(newValue);
                PreferenceValues.enableWakeLock();
                return true;
            });
            alwaysOn.setSummaryProvider(preference -> {
                SharedPreferences preferences = preference.getSharedPreferences();
                String current_value = preferences.getString(preference.getKey(), "");
                switch (current_value != null ? current_value : "") {
                    case "0":
                        return getString(R.string.pref_highlight_summary, getString(R.string.pref_highlight_off_text));
                    case "1":
                        return getString(R.string.pref_highlight_summary, getString(R.string.pref_highlight_only_gps_text));
                    case "2":
                        return getString(R.string.pref_highlight_summary, getString(R.string.pref_highlight));
                    default:
                        return getString(R.string.pref_highlight_desc);
                }
            });
        }
    }

    private void prepareFontSize() {
        ListPreference fontSize = findPreference(Preferences.getKey(R.string.pref_KEY_S_FONT_SIZE));
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
            fontSize.setSummaryProvider(preference -> {
                SharedPreferences preferences = preference.getSharedPreferences();
                String current_value = preferences.getString(preference.getKey(), "");
                switch (current_value != null ? current_value : "") {
                    case "0":
                        return getString(R.string.pref_font_size_summary, getString(R.string.pref_font_size_default));
                    case "1":
                        return getString(R.string.pref_font_size_summary, getString(R.string.pref_font_size_small));
                    case "2":
                        return getString(R.string.pref_font_size_summary, getString(R.string.pref_font_size_medium));
                    case "3":
                        return getString(R.string.pref_font_size_summary, getString(R.string.pref_font_size_large));
                    default:
                        return getString(R.string.pref_font_size_desc);
                }
            });
        }
    }
}
