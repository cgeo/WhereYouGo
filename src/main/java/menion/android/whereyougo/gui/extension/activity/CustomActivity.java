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

package menion.android.whereyougo.gui.extension.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import java.util.Locale;

import menion.android.whereyougo.MainApplication;
import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Const;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.NotificationService;

public class CustomActivity extends FragmentActivity {
    private static final String TAG = CustomActivity.class.getSimpleName();

    protected void customOnCreate(Activity activity) {
        Logger.v(TAG, "customOnCreate(), id:" + activity.hashCode());

        // Set language
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lang = sharedPreferences.getString(getString(R.string.pref_KEY_S_LANGUAGE), "");
        Logger.v(TAG, "customOnCreate(), lang: " + lang);
        setLocale(this, lang);

        // set main activity parameters
        if (!(activity instanceof MainActivity)) {
            // Settings.setLanguage(this);
            setScreenBasic(activity);
        }

        // set screen size
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Const.SCREEN_WIDTH = metrics.widthPixels;
        Const.SCREEN_HEIGHT = metrics.heightPixels;

        switch (Preferences.APPEARANCE_FONT_SIZE) {
            case PreferenceValues.VALUE_FONT_SIZE_SMALL:
                activity.setTheme(R.style.FontSizeSmall);
                break;
            case PreferenceValues.VALUE_FONT_SIZE_MEDIUM:
                activity.setTheme(R.style.FontSizeMedium);
                break;
            case PreferenceValues.VALUE_FONT_SIZE_LARGE:
                activity.setTheme(R.style.FontSizeLarge);
                break;
        }
    }

    protected static void setScreenBasic(Activity activity) {
        try {
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        } catch (Exception e) {
            // TODO Logger.e(TAG, "setFullScreen(" + activity + ")", e);
        }
    }

    protected static void customOnPause(Activity activity) {
        Logger.v(TAG, "customOnPause(), id:" + activity.hashCode());
        // activity is not in foreground
        if (PreferenceValues.getCurrentActivity() == activity) {
            PreferenceValues.setCurrentActivity(null);
        }
        // disable location
        MainApplication.onActivityPause();
    }

    protected static void customOnResume(Activity activity) {
        Logger.v(TAG, "customOnResume(), id:" + activity.hashCode());
        // set current activity
        PreferenceValues.setCurrentActivity(activity);
        // enable permanent screen on
        PreferenceValues.enableWakeLock();
    }

    protected static void customOnStart(Activity activity) {
        Logger.v(TAG, "customOnStart(), id:" + activity.hashCode());
        setStatusbar(activity);
        setScreenFullscreen(activity);
    }

    public static void setStatusbar(Activity activity) {
        try {
            Intent intent = new Intent(activity, NotificationService.class);
            intent.putExtra(NotificationService.TITEL, A.getAppName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && Preferences.GLOBAL_RUN_SCREEN_OFF) {
                    intent.setAction(NotificationService.START_NOTIFICATION_SERVICE_FOREGROUND);
                    activity.startService(intent);
                } else {
                    if (!Preferences.GPS_DISABLE_WHEN_HIDE || (Preferences.GPS_DISABLE_WHEN_HIDE && Preferences.GUIDING_GPS_REQUIRED)) {
                        intent.setAction(NotificationService.START_NOTIFICATION_SERVICE_FOREGROUND);
                        activity.startService(intent);
                    } else {
                        if (Preferences.APPEARANCE_STATUSBAR) {
                            intent.setAction(NotificationService.START_NOTIFICATION_SERVICE);
                            activity.startService(intent);
                        } else {
                            intent.setAction(NotificationService.STOP_NOTIFICATION_SERVICE);
                            activity.startService(intent);
                        }
                    }
                }
            } else {
                if (Preferences.APPEARANCE_STATUSBAR) {
                    intent.setAction(NotificationService.START_NOTIFICATION_SERVICE);
                    activity.startService(intent);
                } else {
                    intent.setAction(NotificationService.STOP_NOTIFICATION_SERVICE);
                    activity.startService(intent);
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, "setStatusbar(" + activity + ")", e);
        }
    }

    public static void setScreenFullscreen(Activity activity) {
        try {
            if (Preferences.APPEARANCE_FULLSCREEN) {
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        } catch (Exception e) {
            Logger.e(TAG, "setFullScreen(" + activity + ")", e);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    public int getParentViewId() {
        return -1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.v(TAG, "onCreate(), id:" + hashCode());
        try {
            super.onCreate(savedInstanceState);
            customOnCreate(this);
        } catch (Exception e) {
            Logger.e(TAG, "onCreate()", e);
        }
    }

    @Override
    public void onDestroy() {
        Logger.v(TAG, "onDestroy(), id:" + hashCode());
        try {
            super.onDestroy();

            if (getParentViewId() != -1) {
                unbindDrawables(findViewById(getParentViewId()));
                System.gc();
            }
        } catch (Exception e) {
            Logger.e(TAG, "onDestroy()", e);
        }
    }

    @Override
    protected void onPause() {
        Logger.v(TAG, "onPause(), id:" + hashCode());
        try {
            super.onPause();
            customOnPause(this);
        } catch (Exception e) {
            Logger.e(TAG, "onPause()", e);
        }
    }

    @Override
    protected void onResume() {
        Logger.v(TAG, "onResume(), id:" + hashCode());
        try {
            super.onResume();
            customOnResume(this);
            // set values again, this fix problem when activity is started after
            // activity in e.g. fixed portrait mode
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            Const.SCREEN_WIDTH = metrics.widthPixels;
            Const.SCREEN_HEIGHT = metrics.heightPixels;
        } catch (Exception e) {
            Logger.e(TAG, "onResume()", e);
        }
    }

    @Override
    public void onStart() {
        Logger.v(TAG, "onStart(), id:" + hashCode());
        try {
            super.onStart();
            customOnStart(this);
        } catch (Exception e) {
            Logger.e(TAG, "onStart()", e);
        }
    }

    private void unbindDrawables(View view) {
        if (view == null)
            return;
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    public static void setLocale(Activity activity, String languageCode) {
        String lang = languageCode;
        if (languageCode.equals("default")) {
            lang = Locale.getDefault().getLanguage();
        }
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        config.locale = locale;
        activity.getApplicationContext().createConfigurationContext(config);
    }
}
