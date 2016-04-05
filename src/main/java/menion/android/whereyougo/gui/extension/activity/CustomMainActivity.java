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

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Debug;
import android.os.StatFs;
import android.view.KeyEvent;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;

import menion.android.whereyougo.MainApplication;
import menion.android.whereyougo.R;
import menion.android.whereyougo.geo.location.LocationState;
import menion.android.whereyougo.gui.utils.UtilsGUI;
import menion.android.whereyougo.preferences.Locale;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.FileSystem;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.Utils;

public abstract class CustomMainActivity extends CustomActivity {

    public static final int FINISH_NONE = -1;
    public static final int FINISH_EXIT = 0;
    public static final int FINISH_EXIT_FORCE = 1;
    public static final int FINISH_RESTART = 2;
    public static final int FINISH_RESTART_FORCE = 3;
    public static final int FINISH_RESTART_FACTORY_RESET = 4;
    public static final int FINISH_REINSTALL = 5;
    public static final int CLOSE_DESTROY_APP_NO_DIALOG = 0;
    public static final int CLOSE_DESTROY_APP_DIALOG_NO_TEXT = 1;
    public static final int CLOSE_DESTROY_APP_DIALOG_ADDITIONAL_TEXT = 2;
    public static final int CLOSE_HIDE_APP = 3;
    private static final String TAG = "CustomMain";
    // create directories during startup
    protected static String[] DIRS = new String[]{FileSystem.CACHE};
    private static boolean callSecondInit;
    private static boolean callRegisterOnly;
    protected int finishType = FINISH_NONE;
    private boolean finish = false;

    public static String getNewsFromTo(int lastVersion, int actualVersion) {
        // Logger.d(TAG, "getNewsFromTo(" + lastVersion + ", " + actualVersion + "), file:" +
        // "news_" + (Const.isPro() ? "pro" : "free") + ".xml");
        String versionInfo =
                "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /></head><body>";
        String data = CustomMainActivity.loadAssetString("news.xml");
        if (data == null || data.length() == 0)
            data = CustomMainActivity.loadAssetString("news.xml");
        if (data != null && data.length() > 0) {
            XmlPullParser parser;
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                parser = factory.newPullParser();
                parser.setInput(new StringReader(data));

                int event;
                String tagName;

                boolean correct = false;
                while (true) {
                    event = parser.nextToken();
                    if (event == XmlPullParser.START_TAG) {
                        tagName = parser.getName();
                        if (tagName.equalsIgnoreCase("update")) {
                            String name = parser.getAttributeValue(null, "name");
                            int id = Utils.parseInt(parser.getAttributeValue(null, "id"));
                            if (id > lastVersion && id <= actualVersion) {
                                correct = true;
                                versionInfo += ("<h4>" + name + "</h4><ul>");
                            } else {
                                correct = false;
                            }
                        } else if (tagName.equalsIgnoreCase("li")) {
                            if (correct) {
                                versionInfo += ("<li>" + parser.nextText() + "</li>");
                            }
                        }
                    } else if (event == XmlPullParser.END_TAG) {
                        tagName = parser.getName();
                        if (tagName.equalsIgnoreCase("update")) {
                            if (correct) {
                                correct = false;
                                versionInfo += "</ul>";
                            }
                        } else if (tagName.equals("document")) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                Logger.e(TAG, "getNews()", e);
            }
        }

        versionInfo += "</body></html>";
        return versionInfo;
    }

    public static String loadAssetString(String name) {
        InputStream is = null;
        try {
            is = A.getMain().getAssets().open(name);
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer);
        } catch (Exception e) {
            Logger.e(TAG, "loadAssetString(" + name + ")", e);
            return "";
        } finally {
            Utils.closeStream(is);
        }
    }

    private void clearPackageFromMemory() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    ActivityManager aM =
                            (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);

                    Thread.sleep(1250);
                    aM.killBackgroundProcesses(getPackageName());
                } catch (Exception e) {
                    Logger.e(TAG, "clearPackageFromMemory()", e);
                }
            }
        }).start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Logger.d(TAG, "dispatchKeyEvent(" + event.getAction() + ", " + event.getKeyCode() + ")");
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            switch (getCloseValue()) {
                case CLOSE_DESTROY_APP_NO_DIALOG:
                    finish = true;
                    CustomMainActivity.this.finish();
                    return true;
                case CLOSE_DESTROY_APP_DIALOG_NO_TEXT:
                    showDialogFinish(FINISH_EXIT);
                    return true;
                case CLOSE_DESTROY_APP_DIALOG_ADDITIONAL_TEXT:
                    showDialogFinish(FINISH_EXIT);
                    return true;
                case CLOSE_HIDE_APP:
                    // no action
                    break;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    /**
     * Method that create layout for actual activity. This is called everytime, onCreate method is
     * called
     */
    protected abstract void eventCreateLayout();

    /**
     * This is called only when application really need to be destroyed, so in this method is
     * suggested to clear all variables
     */
    protected abstract void eventDestroyApp();

    /**
     * This is called only once after application start. It's called in onCreate method before layout
     * is placed
     */
    protected abstract void eventFirstInit();

    /**
     * This is called everytime except first run. It's called in onResume method
     */
    protected abstract void eventRegisterOnly();

    /**
     * This is called only once after application start. It's called in onResume method
     */
    protected abstract void eventSecondInit();

    public void finishForceSilent() {
        finish = true;
        CustomMainActivity.this.finish();
    }

    protected abstract String getCloseAdditionalText();

    protected abstract int getCloseValue();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        A.registerMain(this);

        callSecondInit = false;
        callRegisterOnly = false;
        if (A.getApp() == null) { // first app run
            // Logger.w(TAG, "onCreate() - init new");
            A.registerApp((MainApplication) getApplication());

            // not test some things
            if (testFileSystem() && testFreeSpace()) {
                // set last known location
                if (Utils.isPermissionAllowed(Manifest.permission.ACCESS_FINE_LOCATION)
                        && Preferences.getBooleanPreference(R.string.pref_KEY_B_START_GPS_AUTOMATICALLY)) {
                    LocationState.setGpsOn(CustomMainActivity.this);
                } else {
                    LocationState.setGpsOff(CustomMainActivity.this);
                }

                eventFirstInit();
                setScreenBasic(this);
                eventCreateLayout();
                callSecondInit = true;
            } else {
                // do nothing, just close APP
            }
        } else {
            // Logger.w(TAG, "onCreate() - only register");
            setScreenBasic(this);
            eventCreateLayout();
            callRegisterOnly = true;
        }
    }

    @Override
    public void onDestroy() {
        if (finish) {
            // stop debug if any forgotten
            Debug.stopMethodTracing();
            // remember value before A.getApp() exist
            boolean clearPackageAllowed =
                    Utils.isPermissionAllowed(Manifest.permission.KILL_BACKGROUND_PROCESSES);

            // call individual app close
            eventDestroyApp();

            // disable highlight
            PreferenceValues.disableWakeLock();
            // save last known location
            PreferenceValues.setLastKnownLocation();
            // disable GPS modul
            LocationState.destroy(CustomMainActivity.this);

            // destroy static references
            A.destroy();
            // call native destroy
            super.onDestroy();

            // remove app from memory
            if (clearPackageAllowed) {
                clearPackageFromMemory(); // XXX not work on 2.2 and higher!!!
            }
        } else {
            super.onDestroy();
        }
    }

    public void onResumeExtra() {
        if (callSecondInit) {
            callSecondInit = false;
            eventSecondInit();
        }
        if (callRegisterOnly) {
            callRegisterOnly = false;
            eventRegisterOnly();
        }
    }

    public void showDialogFinish(final int typeOfFinish) {
        // Logger.d(TAG, "showFinishDialog(" + typeOfFinish + ")");
        if (typeOfFinish == FINISH_NONE)
            return;

        this.finishType = typeOfFinish;

        runOnUiThread(new Runnable() {
            public void run() {
                String title = Locale.getString(R.string.question);
                String message = "";
                boolean cancelable =
                        !(finishType == FINISH_RESTART_FORCE || finishType == FINISH_RESTART_FACTORY_RESET
                                || finishType == FINISH_REINSTALL || finishType == FINISH_EXIT_FORCE);
                switch (finishType) {
                    case FINISH_EXIT:
                        message = Locale.getString(R.string.do_you_really_want_to_exit);
                        break;
                    case FINISH_EXIT_FORCE:
                        title = Locale.getString(R.string.info);
                        message = Locale.getString(R.string.you_have_to_exit_app_force);
                        break;
                    case FINISH_RESTART:
                        message = Locale.getString(R.string.you_have_to_restart_app_recommended);
                        break;
                    case FINISH_RESTART_FORCE:
                        title = Locale.getString(R.string.info);
                        message = Locale.getString(R.string.you_have_to_restart_app_force);
                        break;
                    case FINISH_RESTART_FACTORY_RESET:
                        title = Locale.getString(R.string.info);
                        message = Locale.getString(R.string.you_have_to_restart_app_force);
                        break;
                    case FINISH_REINSTALL:
                        title = Locale.getString(R.string.info);
                        message = Locale.getString(R.string.new_version_will_be_installed);
                        break;
                }

                AlertDialog.Builder b = new AlertDialog.Builder(CustomMainActivity.this);
                b.setCancelable(cancelable);
                b.setTitle(title);
                b.setIcon(R.drawable.ic_question_alt);
                b.setMessage(message);
                b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (finishType == FINISH_EXIT || finishType == FINISH_EXIT_FORCE) {
                            finish = true;
                            CustomMainActivity.this.finish();
                        } else if (finishType == FINISH_RESTART || finishType == FINISH_RESTART_FORCE
                                || finishType == FINISH_RESTART_FACTORY_RESET) {
                            // Setup one-short alarm to restart my application in 3 seconds - TODO need use
                            // another context
                            // AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            // Intent intent = new Intent(APP_INTENT_MAIN);
                            // PendingIntent pi = PendingIntent.getBroadcast(CustomMain.this, 0, intent,
                            // PendingIntent.FLAG_ONE_SHOT);
                            // alarmMgr.set(AlarmManager.ELAPSED_REALTIME, System.currentTimeMillis() + 3000, pi);
                            finish = true;
                            CustomMainActivity.this.finish();
                        } else if (finishType == FINISH_REINSTALL) {
                            // Intent intent = new Intent();
                            // intent.setAction(android.content.Intent.ACTION_VIEW);
                            // intent.setDataAndType(Uri.fromFile(new File(FileSystem.ROOT + "smartmaps.apk")),
                            // "application/vnd.android.package-archive");
                            //
                            // startActivity(intent);
                            showDialogFinish(FINISH_EXIT_FORCE);
                        }
                    }
                });
                if (cancelable) {
                    b.setNegativeButton(R.string.cancel, null);
                }
                b.show();
            }
        });
    }

    private boolean testFileSystem() {
        if (DIRS == null || DIRS.length == 0)
            return true;

        if (FileSystem.createRoot(MainApplication.APP_NAME)) {
            // Logger.w(TAG, "FileSystem succesfully created!");
        } else {
            // Logger.w(TAG, "FileSystem cannot be created!");
            UtilsGUI.showDialogError(CustomMainActivity.this, R.string.filesystem_cannot_create_root,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showDialogFinish(FINISH_EXIT_FORCE);
                        }
                    });
            return false;
        }

        // fileSystem created successfully
        for (int i = 0; i < DIRS.length; i++) {
            (new File(DIRS[i])).mkdirs();
        }
        return true;
    }

    private boolean testFreeSpace() {
        if (DIRS == null || DIRS.length == 0)
            return true;

        // check disk space (at least 5MB)
        StatFs stat = new StatFs(FileSystem.ROOT);
        long bytesFree = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        long megFree = bytesFree / 1048576;
        // Logger.d(TAG, "megAvailable:" + megAvail + ", free:" + megFree);
        if (megFree > 0 && megFree < 5) {
            UtilsGUI.showDialogError(CustomMainActivity.this,
                    getString(R.string.not_enough_disk_space_x, FileSystem.ROOT, megFree + "MB"),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showDialogFinish(FINISH_EXIT_FORCE);
                        }
                    });
            return false;
        }
        return true;
    }
}
