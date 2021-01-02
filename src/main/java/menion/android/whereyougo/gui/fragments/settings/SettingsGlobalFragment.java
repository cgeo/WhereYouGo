package menion.android.whereyougo.gui.fragments.settings;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.io.File;

import ar.com.daidalos.afiledialog.FileChooserDialog;
import menion.android.whereyougo.MainApplication;
import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.utils.UtilsGUI;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.ManagerNotify;
import menion.android.whereyougo.utils.Utils;

public class SettingsGlobalFragment extends PreferenceFragmentCompat {
    private static final String TAG = SettingsGlobalFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.whereyougo_preferences_global, rootKey);

        Activity settingsContext = getActivity();
        Preference fileroot = findPreference(getString(R.string.pref_KEY_S_ROOT));
        ListPreference mapProvider = findPreference(Preferences.getKey(R.string.pref_KEY_S_MAP_PROVIDER));
        CheckBoxPreference autosave = findPreference(Preferences.getKey(R.string.pref_KEY_B_SAVEGAME_AUTO));
        EditTextPreference savegameSlots = findPreference(Preferences.getKey(R.string.pref_KEY_S_SAVEGAME_SLOTS));
        CheckBoxPreference doubelTapExit = findPreference(Preferences.getKey(R.string.pref_KEY_B_DOUBLE_CLICK));
        CheckBoxPreference runScreenOff = findPreference(Preferences.getKey(R.string.pref_KEY_B_RUN_SCREEN_OFF));

        if (fileroot != null) {
            fileroot.setOnPreferenceClickListener(preference -> {
                // This might crash the app but since we are using our legacy filepicker, I don't have any better idea
                // as to accept the risk and hope for the best.
                UtilsGUI.dialogDoItem(settingsContext, getText(R.string.pref_root), R.drawable.var_empty, getText(R.string.pref_root_desc),
                    getString(R.string.cancel), null,
                    getString(R.string.folder_select), (dialog, which) -> {
                        FileChooserDialog selectDialog = new FileChooserDialog(settingsContext);
                        selectDialog.loadFolder(Preferences.GLOBAL_ROOT);
                        selectDialog.setFolderMode(true);
                        selectDialog.setCanCreateFiles(false);
                        selectDialog.setShowCancelButton(true);
                        selectDialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
                            public void onFileSelected(Dialog source, File folder) {
                                source.dismiss();
                                if (((MainApplication) A.getApp()).setRoot(folder.getAbsolutePath())) {
                                    MainActivity.refreshCartridges();
                                }
                            }

                            public void onFileSelected(Dialog source, File folder, String name) {
                                String newFolder = folder.getAbsolutePath() + "/" + name;
                                new File(newFolder).mkdir();
                                ((FileChooserDialog) source).loadFolder(newFolder);
                            }
                        });
                        selectDialog.show();
                    },
                    getString(R.string.folder_default), (dialog, which) -> {
                        if (((MainApplication) A.getApp()).setRoot(null)) {
                            MainActivity.refreshCartridges();
                        }
                    });
                return false;
            });
        }
        if (mapProvider != null) {
            mapProvider.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                Preferences.GLOBAL_MAP_PROVIDER = Utils.parseInt(newValue);
                return true;
            });
        }
        if (autosave != null) {
            autosave.setOnPreferenceChangeListener((preference, o) -> {
                boolean newValue = (boolean) o;
                Preferences.GLOBAL_SAVEGAME_AUTO = Utils.parseBoolean(newValue);
                return true;
            });
        }
        if (savegameSlots != null) {
            savegameSlots.setOnPreferenceChangeListener((preference, o) -> {
                String newValue = (String) o;
                int value = Utils.parseInt(newValue);
                if (value >= 0) {
                    Preferences.GLOBAL_SAVEGAME_SLOTS = value;
                } else {
                    ManagerNotify.toastShortMessage(R.string.invalid_value);
                }
                return true;
            });
        }
        if (doubelTapExit != null) {
            doubelTapExit.setOnPreferenceChangeListener((preference, o) -> {
                boolean newValue = (boolean) o;
                Preferences.GLOBAL_DOUBLE_CLICK = Utils.parseBoolean(newValue);
                return true;
            });
        }
        if (runScreenOff != null) {
            runScreenOff.setOnPreferenceChangeListener((preference, o) -> {
                boolean newValue = (boolean) o;
                Preferences.GLOBAL_RUN_SCREEN_OFF = Utils.parseBoolean(newValue);
                CheckBoxPreference status_bar = findPreference(Preferences.getKey(R.string.pref_KEY_B_STATUSBAR));
                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P && newValue) {
                    status_bar.setEnabled(false);
                }
                PreferenceValues.enableWakeLock();
                return true;
            });

        }
    }
}
