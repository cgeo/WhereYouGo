package menion.android.whereyougo.gui.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import menion.android.whereyougo.gui.extension.activity.CustomMainActivity;
import menion.android.whereyougo.maps.mapsforge.filepicker.FilePicker;
import menion.android.whereyougo.maps.mapsforge.filefilter.FilterByFileExtension;
import menion.android.whereyougo.R;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Locale;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.FileSystem;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.ManagerNotify;
import menion.android.whereyougo.utils.Utils;


public class XmlSettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    
	private static final String TAG = "XmlSettingsActivity";
	
	public boolean needRestart;
	
	private static ListPreference lastUsedPreference; // TODO
	
	private static final int REQUEST_GUIDING_WPT_SOUND = 0;
	private static final int REQUEST_ROOT = 1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.settings);
        
        /* I don't really know why I cannot call CustomActivity.customOnCreate(this); */
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
        
        addPreferencesFromResource(R.xml.whereyougo_preferences);
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
		
		needRestart = false; // TODO where it handle?
		
	
        Preference x = findPreference( R.string.pref_KEY_S_ROOT );
        x.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				FilePicker.setFileDisplayFilter(new FilterByFileExtension(".gwc"));
				FilePicker.setFileSelectFilter(null);
				
				startActivityForResult(new Intent(XmlSettingsActivity.this, FilePicker.class), REQUEST_ROOT );
				return false;
			}		
        }); 
        
        String dir = PreferenceValues.getPrefString( R.string.pref_KEY_S_ROOT, R.string.pref_DEFAULT_ROOT );
        x.setSummary( "(" + dir + ") " + Locale.get( R.string.pref_root_desc ) ); // TODO make it better :-(        
        
		/* TODO - check this code */ 
	    if (!Utils.isAndroid201OrMore()) {
	    	Preference prefSensorFilter = findPreference( R.string.pref_KEY_S_SENSORS_ORIENT_FILTER );
	    	if ( prefSensorFilter != null ) {
	    		prefSensorFilter.setEnabled(false);
	    	}
	    }

	    /* 
	     * TODO workaround to fix old value for cz (until version 0.8.12, remove this block after one year!)
	     */
	    
	    
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	
		if ( key.equals("X") ) {
			// DO NOTHING
		} 
  		else if ( key.equals( PreferenceValues.KEY_S_FONT_SIZE ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			Preferences.APPEARANCE_FONT_SIZE = Utils.parseInt(newValue);
  		}		
		else if ( key.equals( PreferenceValues.KEY_B_FULLSCREEN ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
			Preferences.APPEARANCE_FULLSCREEN = Utils.parseBoolean(newValue);
		}
		else if ( key.equals( PreferenceValues.KEY_S_GPS_ALTITUDE_MANUAL_CORRECTION ) ) {
			String newValue = sharedPreferences.getString( key, null );
			Preferences.GPS_ALTITUDE_CORRECTION = Utils.parseDouble(newValue);
		}
		else if ( key.equals( PreferenceValues.KEY_B_GPS_BEEP_ON_GPS_FIX ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
			Preferences.GPS_BEEP_ON_GPS_FIX = Utils.parseBoolean(newValue);
		} 
		else if ( key.equals( PreferenceValues.KEY_B_GUIDING_COMPASS_SOUNDS ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
			Preferences.GUIDING_SOUNDS = Utils.parseBoolean(newValue); // TODO WHY? sollte nicht persitiert werden
		} 
		else if ( key.equals( PreferenceValues.KEY_B_GUIDING_GPS_REQUIRED ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
			Preferences.GUIDING_GPS_REQUIRED = Utils.parseBoolean(newValue);
		}
  		else if ( key.equals( PreferenceValues.KEY_S_GUIDING_WAYPOINT_SOUND ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			int result = Utils.parseInt(newValue);
            if (result != PreferenceValues.VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND) {
            	Preferences.GUIDING_WAYPOINT_SOUND = result; // todo why only store the value if not custom_sound 
            } else {
              // TODO lastUsedPreference = (ListPreference) pref;
              Intent intent = new Intent(Intent.ACTION_PICK);
              intent.setType("audio/*");
              if (!Utils.isIntentAvailable(intent)) {
                intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
              }
              this.startActivityForResult(intent, REQUEST_GUIDING_WPT_SOUND);
            }
  		}
  		else if ( key.equals( PreferenceValues.KEY_S_GUIDING_WAYPOINT_SOUND_DISTANCE ) ) {
  			String newValue = sharedPreferences.getString( key, null );
            int value = Utils.parseInt(newValue);
            if (value > 0) {
              Preferences.GUIDING_WAYPOINT_SOUND_DISTANCE = value;
            } else {
              ManagerNotify.toastShortMessage(R.string.invalid_value);
            }				
  		}
		else if ( key.equals( PreferenceValues.KEY_S_GUIDING_ZONE_POINT ) ) {
			String newValue = sharedPreferences.getString( key, null );
			Preferences.GUIDING_ZONE_NAVIGATION_POINT = Utils.parseInt(newValue);
		} 		
		else if ( key.equals( PreferenceValues.KEY_B_HARDWARE_COMPASS_AUTO_CHANGE ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
	        Preferences.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE = Utils.parseBoolean(newValue);
	        A.getRotator().manageSensors();
		}
		else if ( key.equals( PreferenceValues.KEY_S_HARDWARE_COMPASS_AUTO_CHANGE_VALUE ) ) {
			String newValue = sharedPreferences.getString( key, null );
	        int value = Utils.parseInt(newValue);
	        if (value > 0) {
	          Preferences.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE_VALUE = value;
	        } else {
	          ManagerNotify.toastShortMessage(R.string.invalid_value);
	        }			
		}
		else if ( key.equals( PreferenceValues.KEY_B_HARDWARE_COMPASS_SENSOR ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
	        Preferences.SENSOR_HARDWARE_COMPASS = Utils.parseBoolean(newValue);
	        A.getRotator().manageSensors();
		}
  		else if ( key.equals( PreferenceValues.KEY_S_HIGHLIGHT ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			Preferences.APPEARANCE_HIGHLIGHT = Utils.parseInt(newValue);
  			PreferenceValues.enableWakeLock();
  		}			
		else if ( key.equals( PreferenceValues.KEY_B_IMAGE_STRETCH ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
			Preferences.APPEARANCE_IMAGE_STRETCH = Utils.parseBoolean(newValue);
		}
		else if ( key.equals( PreferenceValues.KEY_S_LANGUAGE ) ) {
			needRestart = true;
		} 			
		else if ( key.equals( PreferenceValues.KEY_S_MAP_PROVIDER ) ) {
			String newValue = sharedPreferences.getString( key, null );
			Preferences.GLOBAL_MAP_PROVIDER = Utils.parseInt(newValue);		
		} 
		else if ( key.equals( PreferenceValues.KEY_B_SAVEGAME_AUTO ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
			Preferences.GLOBAL_SAVEGAME_AUTO = Utils.parseBoolean(newValue);
		}
		else if ( key.equals( PreferenceValues.KEY_B_SENSORS_BEARING_TRUE ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
	        Preferences.SENSOR_BEARING_TRUE = Utils.parseBoolean(newValue);
		}
		else if ( key.equals( PreferenceValues.KEY_S_SENSORS_ORIENT_FILTER ) ) {
			String newValue = sharedPreferences.getString( key, null );
			Preferences.SENSOR_ORIENT_FILTER = Utils.parseInt(newValue);
		}
		else if ( key.equals( PreferenceValues.KEY_B_STATUSBAR ) ) {
			boolean newValue = sharedPreferences.getBoolean( key, false );
			Preferences.APPEARANCE_STATUSBAR = Utils.parseBoolean(newValue);
		}
		/* TODO PreferenceValues.KEY_S_GPS_MIN_TIME_NOTIFICATION / Preferences.GPS_MIN_TIME (= 1 ?) */ 
  		else if ( key.equals( PreferenceValues.KEY_S_UNITS_ALTITUDE ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			Preferences.FORMAT_ALTITUDE = Utils.parseInt(newValue);
  		}	
  		else if ( key.equals( PreferenceValues.KEY_S_UNITS_ANGLE ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			Preferences.FORMAT_ANGLE = Utils.parseInt(newValue);
  		}
  		else if ( key.equals( PreferenceValues.KEY_S_UNITS_COO_LATLON ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			Preferences.FORMAT_COO_LATLON = Utils.parseInt(newValue);
  		}
  		else if ( key.equals( PreferenceValues.KEY_S_UNITS_LENGTH ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			Preferences.FORMAT_LENGTH = Utils.parseInt(newValue);
  		}	
  		else if ( key.equals( PreferenceValues.KEY_S_UNITS_SPEED ) ) {
  			String newValue = sharedPreferences.getString( key, null );
  			Preferences.FORMAT_SPEED = Utils.parseInt(newValue);
  		}			
		// TODO setTrans .getEditText().setTransformationMethod(new PasswordTransformationMethod());
    }	
     
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GUIDING_WPT_SOUND) {
            if (resultCode == Activity.RESULT_OK && data != null) {
              Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
              // Uri uri =
              // data.getData();//getStringExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI));
              if (uri != null) {
                Logger.d(TAG, "uri:" + uri.toString());
                PreferenceValues.setPrefString( R.string.pref_KEY_S_GUIDING_WAYPOINT_SOUND,
                    String.valueOf( PreferenceValues.VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND));
                PreferenceValues.setPrefString( R.string.pref_VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND_URI,
                    uri.toString());
                // TODO setPrefGuidingWptSound(activity, lastUsedPreference, VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND);
              }
            }
            lastUsedPreference = null;
          } else if (requestCode == REQUEST_ROOT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
              String filename = data.getStringExtra(FilePicker.SELECTED_FILE);
              if (filename != null) {
                File file = new File(filename);
                String dir = file.getParent();
                PreferenceValues.setPrefString( R.string.pref_KEY_S_ROOT, dir);
                Preferences.GLOBAL_ROOT = dir;
                Preference pref = findPreference( R.string.pref_KEY_S_ROOT );
                pref.setSummary( "(" + dir + ")" + Locale.get( R.string.pref_root_desc ) ); // TODO make it better :-(
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



