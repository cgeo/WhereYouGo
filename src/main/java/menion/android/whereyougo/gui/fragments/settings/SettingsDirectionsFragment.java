package menion.android.whereyougo.gui.fragments.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;

import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.activity.XmlSettingsActivity;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.ManagerNotify;
import menion.android.whereyougo.utils.Utils;

public class SettingsDirectionsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.whereyougo_preferences_directions, rootKey);

        CheckBoxPreference guidingCompassSounds = findPreference(Preferences.getKey(R.string.pref_KEY_B_GUIDING_COMPASS_SOUNDS));
        prepareNotificationSoundType();
        prepareSoundDistance();
        prepareGuidingZonePoint();

        if (guidingCompassSounds != null) {
            guidingCompassSounds.setOnPreferenceChangeListener((preference, o) -> {
                boolean newValue = (boolean) o;
                Preferences.GUIDING_SOUNDS = Utils.parseBoolean(newValue);
                return true;
            });
        }
    }

    private void prepareNotificationSoundType() {
        ListPreference notificationSoundType = findPreference(Preferences.getKey(R.string.pref_KEY_S_GUIDING_WAYPOINT_SOUND));
        if (notificationSoundType != null) {
            notificationSoundType.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                int result = Utils.parseInt(newValue);
                if (result == PreferenceValues.VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("audio/*");
                    if (!Utils.isIntentAvailable(intent)) {
                        intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    }
                    startActivityForResult(intent, XmlSettingsActivity.REQUEST_RINGTONE_NOTIFICATION_SOUND);
                } else {
                    Preferences.GUIDING_WAYPOINT_SOUND = result;
                }
                return true;
            });
            notificationSoundType.setSummaryProvider(preference -> {
                SharedPreferences preferences = preference.getSharedPreferences();
                String current_value = preferences.getString(preference.getKey(), "");
                switch (current_value != null ? current_value : "") {
                    case "0":
                        return getString(R.string.pref_guiding_sound_type_summary, getString(R.string.pref_guiding_waypoint_sound_increasing));
                    case "1":
                        return getString(R.string.pref_guiding_sound_type_summary, getString(R.string.pref_guiding_waypoint_sound_beep_on_distance));
                    case "2":
                        return getString(R.string.pref_guiding_sound_type_summary, getString(R.string.pref_guiding_waypoint_sound_custom_on_distance));
                    default:
                        return getString(R.string.pref_guiding_sound_type_waypoint_desc);
                }
            });
        }
    }

    private void prepareSoundDistance() {
        EditTextPreference soundDistance = findPreference(Preferences.getKey(R.string.pref_KEY_S_GUIDING_WAYPOINT_SOUND_DISTANCE));
        if (soundDistance != null) {
            soundDistance.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                int value = Utils.parseInt(newValue);
                if (value > 0) {
                    Preferences.GUIDING_WAYPOINT_SOUND_DISTANCE = value;
                } else {
                    ManagerNotify.toastShortMessage(R.string.invalid_value);
                }
                return true;
            });
            soundDistance.setSummaryProvider(preference -> {
                SharedPreferences preferences = preference.getSharedPreferences();
                String currentValue = preferences.getString(preference.getKey(), "");
                return getString(R.string.pref_guiding_sound_distance_waypoint_summary, currentValue);
            });
        }
    }

    private void prepareGuidingZonePoint() {
        ListPreference guidingZonePoint = findPreference(Preferences.getKey(R.string.pref_KEY_S_GUIDING_ZONE_POINT));
        if (guidingZonePoint != null) {
            guidingZonePoint.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.GUIDING_ZONE_NAVIGATION_POINT = Utils.parseInt(newValue);
                return true;
            });
            guidingZonePoint.setSummaryProvider(preference -> {
                SharedPreferences preferences = preference.getSharedPreferences();
                String current_value = preferences.getString(preference.getKey(), "");
                switch (current_value != null ? current_value : "") {
                    case "0":
                        return getString(R.string.pref_guiding_zone_point_summary, getString(R.string.pref_guiding_zone_point_center));
                    case "1":
                        return getString(R.string.pref_guiding_zone_point_summary, getString(R.string.pref_guiding_zone_point_nearest));
                    default:
                        return getString(R.string.pref_guiding_zone_point_desc);
                }
            });
        }
    }
}
