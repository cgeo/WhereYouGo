package menion.android.whereyougo.gui.activity;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import menion.android.whereyougo.gui.dialog.AboutDialog;
import menion.android.whereyougo.gui.extension.activity.CustomMainActivity;
import menion.android.whereyougo.gui.utils.UtilsGUI;
import menion.android.whereyougo.maps.mapsforge.filepicker.FilePicker;
import menion.android.whereyougo.maps.mapsforge.filefilter.FilterByFileExtension;
import menion.android.whereyougo.MainApplication;
import menion.android.whereyougo.R;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.preferences.PreviewPreference;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Locale;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.FileSystem;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.ManagerNotify;
import menion.android.whereyougo.utils.Utils;


public class XmlSettingsActivity extends PreferenceActivity 
	implements SharedPreferences.OnSharedPreferenceChangeListener,
	Preference.OnPreferenceClickListener	{
    
	private static final String TAG = "XmlSettingsActivity";
	
	public boolean needRestart;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.settings);
        
		needRestart = false; 
		
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
        
		/*
		 * 
		 */		
        addPreferencesFromResource(R.xml.whereyougo_preferences);
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
		
		/*
		 * Remove internal preferences
		 */
		Preference somePreference = findPreference( R.string.pref_KEY_X_HIDDEN_PREFERENCES );
		PreferenceScreen preferenceScreen = getPreferenceScreen();
		preferenceScreen.removePreference(somePreference);		
		
		/*
		 * Register OnClick handler
		 */		
        Preference preferenceRoot = findPreference( R.string.pref_KEY_S_ROOT );
        preferenceRoot.setOnPreferenceClickListener( this ); 
		
        Preference preferenceAbout = findPreference( R.string.pref_KEY_X_ABOUT );
        preferenceAbout.setOnPreferenceClickListener( this ); 
		
		/*
		 * Workaround: Update/set value preview 
		 */
        // String dir = Preferences.getStringPreference( R.string.pref_KEY_S_ROOT );
        // x.setSummary( "(" + dir + ") " + Locale.get( R.string.pref_root_desc ) ); // TODO make it better :-(        
        
		/* TODO - check this code */ 
	    if (!Utils.isAndroid201OrMore()) {
	    	Preference prefSensorFilter = findPreference( R.string.pref_KEY_S_SENSORS_ORIENT_FILTER );
	    	if ( prefSensorFilter != null ) {
	    		prefSensorFilter.setEnabled(false);
	    	}
	    }
	    
	    
    }
	
	@Override
	public void onDestroy() {
		try {
			super.onDestroy();
			if (needRestart) {
				A.getMain().showDialogFinish(CustomMainActivity.FINISH_RESTART);
			}
		} catch (Exception e) {
			Logger.e(getLocalClassName(), "onDestroy()", e);
		}		
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		boolean status = false;
		String key = preference.getKey();
		
		if ( key.equals( "" ) ) {
			// DO NOTHING
		} else if ( key.equals( getString( R.string.pref_KEY_S_ROOT ) ) ) {
			// call file picker
			FilePicker.setFileDisplayFilter(new FilterByFileExtension(".gwc"));
			FilePicker.setFileSelectFilter(null);
			startActivityForResult(new Intent(XmlSettingsActivity.this, FilePicker.class), R.string.pref_KEY_S_ROOT );
			return false;		
		} else if ( key.equals( getString( R.string.pref_KEY_X_ABOUT ) ) ) {
			/*
	          AlertDialog.Builder b = new AlertDialog.Builder(this);
	          b.setCancelable(false);
	          b.setTitle(MainApplication.APP_NAME);
	          b.setIcon(R.drawable.icon);
	          b.setView(UtilsGUI.getFilledWebView(A.getMain(), "XXX"));
	          b.setNeutralButton(R.string.yes, new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	              // stage01Completed = true;
	              // PreferenceValues.setApplicationVersionLast(actualVersion);
	            }
	          });
	          b.show();
			*/

		} else {
			return status;
		}
		return status;
	}
	
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	
		if ( key.equals("X") ) {
			// DO NOTHING
		} 
  		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_FONT_SIZE ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			Preferences.APPEARANCE_FONT_SIZE = Utils.parseInt(newValue);
  		}		
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_B_FULLSCREEN ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
			Preferences.APPEARANCE_FULLSCREEN = Utils.parseBoolean(newValue);
		}
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_GPS_ALTITUDE_MANUAL_CORRECTION ) ) {
			String newValue = sharedPreferences.getString( key, null );
			Preferences.GPS_ALTITUDE_CORRECTION = Utils.parseDouble(newValue);
		}
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_B_GPS_BEEP_ON_GPS_FIX ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
			Preferences.GPS_BEEP_ON_GPS_FIX = Utils.parseBoolean(newValue);
		} 
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_B_GUIDING_COMPASS_SOUNDS ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
			Preferences.GUIDING_SOUNDS = Utils.parseBoolean(newValue);
		} 
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_B_GUIDING_GPS_REQUIRED ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
			Preferences.GUIDING_GPS_REQUIRED = Utils.parseBoolean(newValue);
		}
  		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_GUIDING_WAYPOINT_SOUND ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			int result = Utils.parseInt(newValue);
            if (result != PreferenceValues.VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND) {
            	Preferences.GUIDING_WAYPOINT_SOUND = result; 
            } else {
              Intent intent = new Intent(Intent.ACTION_PICK);
              intent.setType("audio/*");
              if (!Utils.isIntentAvailable(intent)) {
                intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
              }
              this.startActivityForResult(intent, R.string.pref_KEY_S_GUIDING_WAYPOINT_SOUND);
            }
  		}
  		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_GUIDING_WAYPOINT_SOUND_DISTANCE ) ) {
  			String newValue = sharedPreferences.getString( key, null );
            int value = Utils.parseInt(newValue);
            if (value > 0) {
              Preferences.GUIDING_WAYPOINT_SOUND_DISTANCE = value;
            } else {
              ManagerNotify.toastShortMessage(R.string.invalid_value);
            }				
  		}
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_GUIDING_ZONE_POINT ) ) {
			String newValue = sharedPreferences.getString( key, null );
			Preferences.GUIDING_ZONE_NAVIGATION_POINT = Utils.parseInt(newValue);
		} 		
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_B_HARDWARE_COMPASS_AUTO_CHANGE ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
	        Preferences.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE = Utils.parseBoolean(newValue);
	        A.getRotator().manageSensors();
		}
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_HARDWARE_COMPASS_AUTO_CHANGE_VALUE ) ) {
			String newValue = sharedPreferences.getString( key, null );
	        int value = Utils.parseInt(newValue);
	        if (value > 0) {
	          Preferences.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE_VALUE = value;
	        } else {
	          ManagerNotify.toastShortMessage(R.string.invalid_value);
	        }			
		}
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_B_HARDWARE_COMPASS_SENSOR ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
	        Preferences.SENSOR_HARDWARE_COMPASS = Utils.parseBoolean(newValue);
	        A.getRotator().manageSensors();
		}
  		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_HIGHLIGHT ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			Preferences.APPEARANCE_HIGHLIGHT = Utils.parseInt(newValue);
  			PreferenceValues.enableWakeLock();
  		}			
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_B_IMAGE_STRETCH ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
			Preferences.APPEARANCE_IMAGE_STRETCH = Utils.parseBoolean(newValue);
		}
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_LANGUAGE ) ) {
			needRestart = true;
		} 			
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_MAP_PROVIDER ) ) {
			String newValue = sharedPreferences.getString( key, null );
			Preferences.GLOBAL_MAP_PROVIDER = Utils.parseInt(newValue);		
		} 
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_B_SAVEGAME_AUTO ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
			Preferences.GLOBAL_SAVEGAME_AUTO = Utils.parseBoolean(newValue);
		}
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_B_SENSORS_BEARING_TRUE ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
	        Preferences.SENSOR_BEARING_TRUE = Utils.parseBoolean(newValue);
		}
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_SENSORS_ORIENT_FILTER ) ) {
			String newValue = sharedPreferences.getString( key, null );
			Preferences.SENSOR_ORIENT_FILTER = Utils.parseInt(newValue);
		}
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_B_STATUSBAR ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
			Preferences.APPEARANCE_STATUSBAR = Utils.parseBoolean(newValue);
		}
		/* TODO - Preferences.GPS_MIN_TIME is used but there is no settings option - default value?
		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_GPS_MIN_TIME_NOTIFICATION ) ) {
			Preferences.GPS_MIN_TIME = 
		} */
  		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_UNITS_ALTITUDE ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			Preferences.FORMAT_ALTITUDE = Utils.parseInt(newValue);
  		}	
  		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_UNITS_ANGLE ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			Preferences.FORMAT_ANGLE = Utils.parseInt(newValue);
  		}
  		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_UNITS_COO_LATLON ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			Preferences.FORMAT_COO_LATLON = Utils.parseInt(newValue);
  		}
  		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_UNITS_LENGTH ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			Preferences.FORMAT_LENGTH = Utils.parseInt(newValue);
  		}	
  		else if ( Preferences.comparePreferenceKey( key, R.string.pref_KEY_S_UNITS_SPEED ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			Preferences.FORMAT_SPEED = Utils.parseInt(newValue);
  		}			
    }	
        
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == R.string.pref_KEY_S_GUIDING_WAYPOINT_SOUND) {
            if (resultCode == Activity.RESULT_OK && data != null) {
              Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
              if (uri != null) {
                Logger.d(TAG, "uri:" + uri.toString());
                Preferences.setStringPreference( R.string.pref_KEY_S_GUIDING_WAYPOINT_SOUND,
                		PreferenceValues.VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND );
                Preferences.setStringPreference( R.string.pref_VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND_URI,
                    uri.toString());
                Preferences.GUIDING_WAYPOINT_SOUND = Utils.parseInt( R.string.pref_VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND );
              }
            }
          } else if (requestCode == R.string.pref_KEY_S_ROOT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
              String filename = data.getStringExtra(FilePicker.SELECTED_FILE);
              if (filename != null) {
                File file = new File(filename);
                String dir = file.getParent();
				
				// PreviewPreference preferenceRoot = (PreviewPreference)findPreference( R.string.pref_KEY_S_ROOT );
				// preferenceRoot.setValue(dir);
				
                Preferences.GLOBAL_ROOT = dir;
                FileSystem.setRootDirectory(null, dir);
                MainActivity.refreshCartridges();
              }
            }
          }
    }   
    
    private static String getKey(final int prefKeyId) {
    	return A.getApp().getString(prefKeyId);
    }
       
    private Preference findPreference(final int keyId ) {
    	return findPreference(getKey(keyId));
    }    
}



