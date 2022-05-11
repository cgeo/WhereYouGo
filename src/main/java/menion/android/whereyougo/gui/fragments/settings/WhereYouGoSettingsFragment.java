package menion.android.whereyougo.gui.fragments.settings;

import menion.android.whereyougo.R;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class WhereYouGoSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.whereyougo_preferences, rootKey);
    }
}
