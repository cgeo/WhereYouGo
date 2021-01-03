package menion.android.whereyougo.gui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import menion.android.whereyougo.R;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.Utils;

import static menion.android.whereyougo.gui.extension.activity.CustomActivity.setLocale;


public class XmlSettingsActivity
    extends AppCompatActivity
    implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TAG = "XmlSettingsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        setTitle(R.string.settings);
        ((TextView) findViewById(R.id.title_text)).setText(R.string.settings);

        // Set language
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lang = sharedPreferences.getString(getString(R.string.pref_KEY_S_LANGUAGE), "");
        setLocale(this, lang);

        /* workaround: I don't really know why I cannot call CustomActivity.customOnCreate(this); - OMG! */
        switch (Preferences.APPEARANCE_FONT_SIZE) {
            case PreferenceValues.VALUE_FONT_SIZE_SMALL:
                this.setTheme(R.style.FontSizeSmall);
                break;
            case PreferenceValues.VALUE_FONT_SIZE_MEDIUM:
                this.setTheme(R.style.FontSizeMedium);
                break;
            case PreferenceValues.VALUE_FONT_SIZE_LARGE:
                this.setTheme(R.style.FontSizeLarge);
                break;
        }

        if (Preferences.APPEARANCE_FULLSCREEN) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        Log.e(TAG, getBaseContext().getResources().getConfiguration().toString());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == R.string.pref_KEY_S_GUIDING_WAYPOINT_SOUND) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (uri != null) {
                    Logger.d(TAG, "uri:" + uri.toString());
                    Preferences.setStringPreference(R.string.pref_KEY_S_GUIDING_WAYPOINT_SOUND,
                        PreferenceValues.VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND);
                    Preferences.setStringPreference(R.string.pref_KEY_S_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND_URI,
                        uri.toString());
                    Preferences.GUIDING_WAYPOINT_SOUND = Utils.parseInt(R.string.pref_VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND);
                    Preferences.GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND_URI = uri.toString();
                }
            }
        }
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
            getClassLoader(),
            pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.settings_fragment_root, fragment)
            .addToBackStack(null)
            .commit();

        return true;
    }
}



