/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package menion.android.whereyougo.maps.mapsforge.preferences;

import menion.android.whereyougo.R;
import menion.android.whereyougo.maps.mapsforge.filepicker.FilePicker;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Activity to edit the application preferences.
 */
public class EditPreferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.mapsforge_preferences);

        Preference resetCurrentDirectoryPrivate = findPreference("resetCurrentDirectoryPrivate");
        if (resetCurrentDirectoryPrivate != null) {
            resetCurrentDirectoryPrivate.setOnPreferenceClickListener(preference -> resetCurrentDirectory(true));
        }

        Preference resetCurrentDirectoryRoot = findPreference("resetCurrentDirectoryRoot");
        if (resetCurrentDirectoryRoot != null) {
            resetCurrentDirectoryRoot.setOnPreferenceClickListener(preference -> resetCurrentDirectory(false));
        }

    }

    private boolean resetCurrentDirectory(final boolean toPrivateFolder) {
        new AlertDialog.Builder(this)
            .setTitle(R.string.reset_map_settings_confirmation_title)
            .setMessage(R.string.reset_map_settings_confirmation_message)
            .setPositiveButton(android.R.string.ok, ((dialogInterface, i) -> {
                SharedPreferences.Editor editor = getSharedPreferences(FilePicker.PREFERENCES_FILE, MODE_PRIVATE).edit();
                editor.putString(FilePicker.CURRENT_DIRECTORY, toPrivateFolder ? getExternalFilesDir(null).getAbsolutePath() : "/");
                editor.commit();

                SharedPreferences.Editor editor2 = getSharedPreferences("MapActivity", MODE_PRIVATE).edit();
                editor2.remove("mapFile");
                editor2.commit();

                Toast.makeText(this, R.string.reset_current_directory_info, Toast.LENGTH_SHORT).show();
            }))
            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {})
            .show();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check if the full screen mode should be activated
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("fullscreen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }
}
