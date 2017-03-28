/*
 * This file is part of WhereYouGo.
 * 
 * WhereYouGo is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * WhereYouGo is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with WhereYouGo. If not,
 * see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2012 Menion <whereyougo@asamm.cz>
 */

package menion.android.whereyougo;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cz.matejcik.openwig.Engine;
import menion.android.whereyougo.geo.location.LocationState;
import menion.android.whereyougo.gui.SaveGame;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.ExceptionHandler;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.ManagerNotify;
import menion.android.whereyougo.utils.StringToken;
import menion.android.whereyougo.utils.Utils;

public class MainApplication extends Application {

    // application name
    public static final String APP_NAME = "WhereYouGo";
    private static final String TAG = "MainApplication";
    private static MainApplication instance;
    private static Timer mTimer;
    private File cartridgesDir;
    private File filesDir;
    private File cacheDir;
    private Locale locale = null;
    // screen ON/OFF receiver
    private ScreenReceiver mScreenReceiver;
    private boolean mScreenOff = false;

    public static MainApplication getInstance() {
        return instance;
    }

    public static void onActivityPause() {
        // Logger.i(TAG, "onActivityPause()");
        if (mTimer != null) {
            mTimer.cancel();
        }

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                LocationState.onActivityPauseInstant(PreferenceValues.getCurrentActivity());
                mTimer = null;
            }
        }, 2000);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        instance = this;
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

    /* LEGACY SUPPORT - less v0.8.14
     * Converts preference - comes from a former version (less 0.8.14)
     * which are not stored as string into string.
     */
        try {
            // legacySupport4PreferencesFloat( R.string.pref_KEY_S_GPS_ALTITUDE_MANUAL_CORRECTION );
            legacySupport4PreferencesFloat(R.string.pref_KEY_F_LAST_KNOWN_LOCATION_LATITUDE);
            legacySupport4PreferencesFloat(R.string.pref_KEY_F_LAST_KNOWN_LOCATION_LONGITUDE);
            legacySupport4PreferencesFloat(R.string.pref_KEY_F_LAST_KNOWN_LOCATION_ALTITUDE);
            legacySupport4PreferencesInt(R.string.pref_KEY_S_APPLICATION_VERSION_LAST);
        } catch (Exception e) {
            Log.e(TAG, "onCreate() - PANIC! Wipe out preferences", e);
            PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
        }
    /* LEGACY SUPPORT -- END */

        // set basic settings values
        PreferenceManager.setDefaultValues(this, R.xml.whereyougo_preferences, false);
        Preferences.init(this);

        // get language
        Configuration config = getBaseContext().getResources().getConfiguration();
        String lang = Preferences.getStringPreference(R.string.pref_KEY_S_LANGUAGE);

        // set language
        if (!lang.equals(getString(R.string.pref_language_default_value))
                && !config.locale.getLanguage().equals(lang)) {
            ArrayList<String> loc = StringToken.parse(lang, "_");
            if (loc.size() == 1) {
                locale = new Locale(lang);
            } else {
                locale = new Locale(loc.get(0), loc.get(1));
            }
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

        // initialize core
        initCore();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Logger.d(TAG, String.format("onTrimMemory(%d)", level));
        try {
            if (level == android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN
                    && Preferences.GLOBAL_SAVEGAME_AUTO
                    && MainActivity.selectedFile != null && Engine.instance != null) {
                final Activity activity = PreferenceValues.getCurrentActivity();
                if (activity != null) {
                    if (MainActivity.wui != null) {
                        MainActivity.wui.setOnSavingFinished(new Runnable() {
                            @Override
                            public void run() {
                                ManagerNotify.toastShortMessage(activity, getString(R.string.save_game_auto));
                                MainActivity.wui.setOnSavingFinished(null);
                            }
                        });
                    }
                    new SaveGame(activity).execute();
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, String.format("onTrimMemory(%d): savegame failed", level));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (locale != null) {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            getBaseContext().getResources().updateConfiguration(newConfig,
                    getBaseContext().getResources().getDisplayMetrics());
        }
    }

    public void destroy() {
        try {
            unregisterReceiver(mScreenReceiver);
        } catch (Exception e) {
            Logger.w(TAG, "destroy(), e:" + e);
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public boolean setCartridgeDir(File dir) {
        cartridgesDir = filesDir = dir;
        if (dir != null && dir.isDirectory() && dir.canWrite()) {
            cacheDir = new File(dir.getAbsolutePath() + File.separator + "cache");
            cacheDir.mkdir();
        }

        if (cartridgesDir == null || !cartridgesDir.canRead()) {
            try {
                cartridgesDir = getExternalFilesDir(null);
            } catch (Exception e1) {
            }
        }
        if (cartridgesDir == null || !cartridgesDir.canRead()) {
            try {
                cartridgesDir = getFilesDir();
            } catch (Exception e1) {
            }
        }

        if (filesDir == null || !filesDir.canWrite()) {
            try {
                filesDir = getExternalFilesDir(null);
            } catch (Exception e1) {
            }
        }
        if (filesDir == null || !filesDir.canWrite()) {
            try {
                filesDir = getFilesDir();
            } catch (Exception e1) {
            }
        }

        if (cacheDir == null || !cacheDir.canWrite()) {
            try {
                cacheDir = getExternalCacheDir();
            } catch (Exception e1) {
            }
        }
        if (cacheDir == null || !cacheDir.canWrite()) {
            try {
                cacheDir = getCacheDir();
            } catch (Exception e1) {
            }
        }

        return cartridgesDir != null && cartridgesDir.canRead()
                && filesDir != null && filesDir.canWrite()
                && cacheDir != null && cacheDir.canWrite();
    }

    public File getCartridgesDir() {
        return cartridgesDir;
    }

    public File getFilesDir() {
        return filesDir;
    }

    public File getCacheDir() {
        return cartridgesDir;
    }

    private void initCore() {
        // register screen on/off receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mScreenReceiver = new ScreenReceiver();
        registerReceiver(mScreenReceiver, filter);

        setCartridgeDir(new File(Preferences.GLOBAL_ROOT));

        // set location state
        LocationState.init(this);
        // initialize DPI
        Utils.getDpPixels(this, 1.0f);

        // set DeviceID for OpenWig
        try {
            String name = String.format("%s, app:%s", getAppName(), getAppVersion());
            String platform = String.format("Android %s", android.os.Build.VERSION.RELEASE);
            cz.matejcik.openwig.WherigoLib.env.put(cz.matejcik.openwig.WherigoLib.DEVICE_ID, name);
            cz.matejcik.openwig.WherigoLib.env.put(cz.matejcik.openwig.WherigoLib.PLATFORM, platform);
        } catch (Exception e) {
            // not really important
        }
    }

    public boolean isScreenOff() {
        return mScreenOff;
    }

    /* LEGACY SUPPORT - less v0.8.14
     * Converts preference - comes from a former version (less 0.8.14)
     * which are not stored as string into string.
     */
    private void legacySupport4PreferencesFloat(int prefId) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String key = getString(prefId);

        try {
            sharedPref.getString(key, "");
        } catch (Exception e) {
            try {
                Log.d(TAG, "legacySupport4PreferencesFloat() - LEGACY SUPPORT: convert float to string");
                Float value = sharedPref.getFloat(key, 0.0f);
                sharedPref.edit().remove(key).commit();
                sharedPref.edit().putString(key, String.valueOf(value)).commit();
            } catch (Exception ee) {
                Log.e(TAG, "legacySupport4PreferencesFloat() - panic remove", ee);
                sharedPref.edit().remove(key).commit();
            }
        }
    }

    private void legacySupport4PreferencesInt(int prefId) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String key = getString(prefId);

        try {
            sharedPref.getString(key, "");
        } catch (Exception e) {
            try {
                Log.d(TAG, "legacySupport4PreferencesInt() - LEGACY SUPPORT: convert int to string");
                int value = sharedPref.getInt(key, 0);
                sharedPref.edit().remove(key).commit();
                sharedPref.edit().putString(key, String.valueOf(value)).commit();
            } catch (Exception ee) {
                Log.e(TAG, "legacySupportFloat2Int() - panic remove", ee);
                sharedPref.edit().remove(key).commit();
            }
        }
    }


    public String getAppName() {
        try {
            return getPackageManager().getApplicationLabel(getApplicationInfo()).toString();
        } catch (Exception e) {
            return "WhereYouGo";
        }
    }

    public String getAppVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            return BuildConfig.VERSION_NAME;
        }
    }

    private class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // Logger.v(TAG, "ACTION_SCREEN_OFF");
                mScreenOff = true;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                // Logger.v(TAG, "ACTION_SCREEN_ON");
                LocationState.onScreenOn(false);
                mScreenOff = false;
            }
        }
    }
}
