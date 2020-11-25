package menion.android.whereyougo.gui.fragments.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import menion.android.whereyougo.R;

public class SettingsLocalizationFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.whereyougo_preferences_localization, rootKey);
    }
}
