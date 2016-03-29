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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import menion.android.whereyougo.MainApplication;
import menion.android.whereyougo.R;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.Const;
import menion.android.whereyougo.utils.Logger;

public class CustomActivity extends FragmentActivity {

    protected Handler handler;

    protected static void customOnCreate(Activity activity) {
        // Logger.v(activity.getLocalClassName(), "customOnCreate(), id:" +
        // activity.hashCode());
        // set main activity parameters
        if (!(activity instanceof CustomMainActivity)) {
            // Settings.setLanguage(this);
            setScreenBasic(activity);
        }

        // set screen size
        Const.SCREEN_WIDTH = activity.getWindowManager().getDefaultDisplay().getWidth();
        Const.SCREEN_HEIGHT = activity.getWindowManager().getDefaultDisplay().getHeight();

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

    protected static boolean setScreenBasic(Activity activity) {
        try {
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
            return true;
        } catch (Exception e) {
            // TODO Logger.e(TAG, "setFullScreen(" + activity + ")", e);
        }
        return false;
    }

    protected static void customOnPause(Activity activity) {
        // Logger.v(activity.getLocalClassName(), "customOnPause(), id:" +
        // activity.hashCode());
        // activity is not in foreground
        if (PreferenceValues.getCurrentActivity() == activity) {
            PreferenceValues.setCurrentActivity(null);
        }
        // disable location
        MainApplication.onActivityPause();
    }

    protected static void customOnResume(Activity activity) {
        // Logger.v(activity.getLocalClassName(), "customOnResume(), id:" +
        // activity.hashCode());
        // set current activity
        PreferenceValues.setCurrentActivity(activity);
        // enable permanent screen on
        PreferenceValues.enableWakeLock();
    }

    protected static void customOnStart(Activity activity) {
        // Logger.v(activity.getLocalClassName(), "customOnStart(), id:" +
        // activity.hashCode());
        setStatusbar(activity);
        setScreenFullscreen(activity);
    }

    public static void setStatusbar(Activity activity) {
        try {
            NotificationManager mNotificationManager =
                    (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            // set statusbar
            if (Preferences.APPEARANCE_STATUSBAR) {
                Context context = activity.getApplicationContext();
                Intent intent =
                        new Intent(context, menion.android.whereyougo.gui.activity.MainActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setAction(Intent.ACTION_MAIN);
                PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
                final int sdkVersion = Integer.parseInt(android.os.Build.VERSION.SDK);
                Notification notif = null;
                if (sdkVersion < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    notif =
                            new Notification(R.drawable.ic_title_logo, "WhereYouGo", System.currentTimeMillis());
                    notif.setLatestEventInfo(activity, "WhereYouGo", "", pIntent);
                } else {
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(activity).setContentTitle("WhereYouGo")
                                    .setSmallIcon(R.drawable.ic_title_logo).setContentIntent(pIntent);
                    notif = builder.build();
                }
                notif.flags = Notification.FLAG_ONGOING_EVENT;
                mNotificationManager.notify(0, notif);
            } else {
                mNotificationManager.cancel(0);
            }
        } catch (Exception e) {
            // Logger.e(TAG, "setStatusbar(" + activity + ")", e);
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
            // Logger.e(TAG, "setFullScreen(" + activity + ")", e);
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
        Logger.v(getLocalClassName(), "onCreate(), id:" + hashCode());
        try {
            super.onCreate(savedInstanceState);
            // create handler
            handler = new Handler();
            customOnCreate(this);
        } catch (Exception e) {
            Logger.e(getLocalClassName(), "onCreate()", e);
        }
    }

    @Override
    public void onDestroy() {
        Logger.v(getLocalClassName(), "onDestroy(), id:" + hashCode());
        try {
            super.onDestroy();

            if (getParentViewId() != -1) {
                unbindDrawables(findViewById(getParentViewId()));
                System.gc();
            }
        } catch (Exception e) {
            Logger.e(getLocalClassName(), "onDestroy()", e);
        }
    }

    @Override
    protected void onPause() {
        Logger.v(getLocalClassName(), "onPause(), id:" + hashCode());
        try {
            super.onPause();
            customOnPause(this);
        } catch (Exception e) {
            Logger.e(getLocalClassName(), "onPause()", e);
        }
    }

    @Override
    protected void onResume() {
        Logger.v(getLocalClassName(), "onResume(), id:" + hashCode());
        try {
            super.onResume();
            customOnResume(this);
            // set values again, this fix problem when activity is started after
            // activity in e.g. fixed portrait mode
            Const.SCREEN_WIDTH = getWindowManager().getDefaultDisplay().getWidth();
            Const.SCREEN_HEIGHT = getWindowManager().getDefaultDisplay().getHeight();
        } catch (Exception e) {
            Logger.e(getLocalClassName(), "onResume()", e);
        }
    }

    @Override
    public void onStart() {
        Logger.v(getLocalClassName(), "onStart(), id:" + hashCode());
        try {
            super.onStart();
            customOnStart(this);
        } catch (Exception e) {
            Logger.e(getLocalClassName(), "onStart()", e);
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
}
