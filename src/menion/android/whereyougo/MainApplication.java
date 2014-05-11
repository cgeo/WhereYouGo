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

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import menion.android.whereyougo.geo.location.LocationState;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.ExceptionHandler;
import menion.android.whereyougo.utils.FileSystem;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.StringToken;
import menion.android.whereyougo.utils.Utils;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import cz.matejcik.openwig.Engine;

public class MainApplication extends Application {

  public interface OnAppVisibilityChange {

    public void onAppMinimized();

    public void onAppRestored();

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

  private static final String TAG = "MainApplication";
  // application name
  public static String APP_NAME = "WhereYouGo";

  public static void appRestored() {
    onAppRestored();
    if (onAppVisibilityChange != null)
      onAppVisibilityChange.onAppRestored();
  }

  private Locale locale = null;

  // screen ON/OFF receiver
  private ScreenReceiver mScreenReceiver;

  private boolean mScreenOff = false;

  private static Timer mTimer;

  private static OnAppVisibilityChange onAppVisibilityChange;

  public static void onActivityPause() {
    // Logger.i(TAG, "onActivityPause()");
    if (mTimer != null) {
      mTimer.cancel();
    }

    mTimer = new Timer();
    mTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (!PreferenceValues.existCurrentActivity())
          onAppMinimized();
        LocationState.onActivityPauseInstant(PreferenceValues.getCurrentActivity());
        mTimer = null;
      }
    }, 2000);
  }

  private static void onAppMinimized() {
    Logger.w(TAG, "onAppMinimized()");
    if (onAppVisibilityChange != null)
      onAppVisibilityChange.onAppMinimized();
  }

  private static void onAppRestored() {
    Logger.w(TAG, "onAppRestored()");
  }

  public static void registerVisibilityHandler(OnAppVisibilityChange handler) {
    MainApplication.onAppVisibilityChange = handler;
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
    onAppVisibilityChange = null;
  }

  private void initCore() {
    // register screen on/off receiver
    IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
    filter.addAction(Intent.ACTION_SCREEN_OFF);
    mScreenReceiver = new ScreenReceiver();
    registerReceiver(mScreenReceiver, filter);

    // set basic settings values
    Preferences.init(this);
    // initialize root directory
    if (Preferences.GLOBAL_ROOT.equals(PreferenceValues.DEFAULT_ROOT)
        || !FileSystem.setRootDirectory(null, Preferences.GLOBAL_ROOT))
      FileSystem.createRoot(APP_NAME);
    // set location state
    LocationState.init(this);
    // initialize DPI
    Utils.getDpPixels(this, 1.0f);

    // set DeviceID for OpenWig
    try {
      PackageManager pm = getPackageManager();
      PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
      String name =
          String.format("%s, app:%s", pm.getApplicationLabel(pi.applicationInfo), pi.versionName);
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

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "onCreate()");
    Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
    Configuration config = getBaseContext().getResources().getConfiguration();
    String lang =
        settings
            .getString(PreferenceValues.KEY_S_LANGUAGE, PreferenceValues.VALUE_LANGUAGE_DEFAULT);
    // Logger.d(TAG, "lang:" + lang + ", system:" + config.locale.getLanguage());
    if (!lang.equals(PreferenceValues.VALUE_LANGUAGE_DEFAULT)
        && !config.locale.getLanguage().equals(lang)) {
      ArrayList<String> loc = StringToken.parse(lang, "_");
      if (loc.size() == 1) {
        locale = new Locale(lang);
      } else {
        locale = new Locale(loc.get(0), loc.get(1));
      }
      Locale.setDefault(locale);
      config.locale = locale;
      getBaseContext().getResources().updateConfiguration(config,
          getBaseContext().getResources().getDisplayMetrics());
    }

    // initialize core
    initCore();
  }

  public void onLowMemory() {
    super.onLowMemory();
    Log.d(TAG, "onLowMemory()");
  }

  public void onTerminate() {
    super.onTerminate();
    Log.d(TAG, "onTerminate()");
  }

  @Override
  public void onTrimMemory(int level) {
    // TODO Auto-generated method stub
    super.onTrimMemory(level);
    Logger.i(TAG, String.format("onTrimMemory(%d)", level));
    if (Preferences.GLOBAL_SAVEGAME_AUTO
        && level == android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN
        && MainActivity.selectedFile != null && Engine.instance != null) {
      Engine.requestSync();
      Toast.makeText(this, R.string.save_game_auto, Toast.LENGTH_SHORT).show();
    }
  }
}
