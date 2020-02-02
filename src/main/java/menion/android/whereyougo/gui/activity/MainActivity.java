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

package menion.android.whereyougo.gui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;

import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.formats.CartridgeFile;
import locus.api.objects.extra.Location;
import locus.api.objects.extra.Waypoint;
import menion.android.whereyougo.MainApplication;
import menion.android.whereyougo.R;
import menion.android.whereyougo.VersionInfo;
import menion.android.whereyougo.geo.location.LocationState;
import menion.android.whereyougo.gui.dialog.AboutDialog;
import menion.android.whereyougo.gui.dialog.ChooseCartridgeDialog;
import menion.android.whereyougo.gui.dialog.ChooseSavegameDialog;
import menion.android.whereyougo.gui.extension.activity.CustomActivity;
import menion.android.whereyougo.gui.extension.activity.CustomMainActivity;
import menion.android.whereyougo.gui.utils.UtilsGUI;
import menion.android.whereyougo.maps.utils.MapDataProvider;
import menion.android.whereyougo.maps.utils.MapHelper;
import menion.android.whereyougo.network.activity.DownloadCartridgeActivity;
import menion.android.whereyougo.openwig.WLocationService;
import menion.android.whereyougo.openwig.WSaveFile;
import menion.android.whereyougo.openwig.WSeekableFile;
import menion.android.whereyougo.openwig.WUI;
import menion.android.whereyougo.preferences.Locale;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Const;
import menion.android.whereyougo.utils.FileSystem;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.ManagerNotify;
import menion.android.whereyougo.utils.NotificationService;

public class MainActivity extends CustomMainActivity {

    private static final String TAG = "Main";

    public static final WUI wui = new WUI();
    private static final WLocationService wLocationService = new WLocationService();
    public static CartridgeFile cartridgeFile;
    public static String selectedFile;
    private static Vector<CartridgeFile> cartridgeFiles;

    static {
        wui.setOnSavingStarted(new Runnable() {
            @Override
            public void run() {
                try {
                    FileSystem.backupFile(MainActivity.getSaveFile());
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     * Call activity that guide onto point.
     *
     * @param activity
     * @return true if internal activity was called. False if external by intent.
     */
    public static boolean callGudingScreen(Activity activity) {
        Intent intent = new Intent(activity, GuidingActivity.class);
        activity.startActivity(intent);
        return true;
    }

    public static File getSaveFile() throws IOException {
        try {
            return new File(selectedFile.substring(0, selectedFile.length() - 3) + "ows");
        } catch (SecurityException e) {
            Logger.e(TAG, "getSyncFile()", e);
            return null;
        }
    }

    public static File getLogFile() throws IOException {
        try {
            return new File(selectedFile.substring(0, selectedFile.length() - 3) + "owl");
        } catch (SecurityException e) {
            Logger.e(TAG, "getSyncFile()", e);
            return null;
        }
    }

    public static String getSelectedFile() {
        return selectedFile;
    }

    public static void setSelectedFile(String filepath) {
        MainActivity.selectedFile = filepath;
    }

    private static void loadCartridge(OutputStream log) {
        try {
            WUI.startProgressDialog();
            Engine.newInstance(cartridgeFile, log, wui, wLocationService).start();
        } catch (Throwable t) {
        }
    }

    private static void restoreCartridge(OutputStream log) {
        try {
            WUI.startProgressDialog();
            Engine.newInstance(cartridgeFile, log, wui, wLocationService).restore();
        } catch (Throwable t) {
        }
    }

    public static void startSelectedCartridge(boolean restore) {
        try {
            File file = getLogFile();
            FileOutputStream fos = null;
            try {
                if (!file.exists())
                    file.createNewFile();
                fos = new FileOutputStream(file, true);
            } catch (Exception e) {
                Logger.e(TAG, "onResume() - create empty saveGame file", e);
            }
            if (restore)
                MainActivity.restoreCartridge(fos);
            else
                MainActivity.loadCartridge(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void refreshCartridges() {
        Logger.w(TAG, "refreshCartridges(), " + (MainActivity.selectedFile == null));

        // load cartridge files
        File[] files = FileSystem.getFiles(FileSystem.ROOT, "gwc");
        cartridgeFiles = new Vector<>();

        // add cartridges to map
        ArrayList<Waypoint> wpts = new ArrayList<>();

        File actualFile = null;
        if (files != null) {
            for (File file : files) {
                try {
                    actualFile = file;
                    CartridgeFile cart = CartridgeFile.read(new WSeekableFile(file), new WSaveFile(file));
                    if (cart != null) {
                        cart.filename = file.getAbsolutePath();

                        Location loc = new Location(TAG);
                        loc.setLatitude(cart.latitude);
                        loc.setLongitude(cart.longitude);
                        Waypoint waypoint = new Waypoint(cart.name, loc);

                        cartridgeFiles.add(cart);
                        wpts.add(waypoint);
                    }
                } catch (Exception e) {
                    Logger.w(TAG, "refreshCartridge(), file:" + actualFile + ", e:" + e.toString());
                    ManagerNotify.toastShortMessage(Locale.getString(R.string.invalid_cartridge, actualFile.getName()));
                    // file.delete();
                }
            }
        }

        if (wpts.size() > 0) {
            // TODO add items on map
        }
    }

    public static void openCartridge(final CartridgeFile cartridgeFile) {
        final CustomActivity activity = A.getMain();
        if (activity == null) {
            return;
        }
        try {
            MainActivity.cartridgeFile = cartridgeFile;
            MainActivity.selectedFile = MainActivity.cartridgeFile.filename;
            File saveFile = MainActivity.getSaveFile();
            ChooseSavegameDialog chooseSavegameDialog = ChooseSavegameDialog.newInstance(saveFile);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(chooseSavegameDialog, "DIALOG_TAG_CHOOSE_SAVE_FILE")
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            Logger.e(TAG, "onCreate()", e);
        }
    }

    private void clickMap() {
        MapDataProvider mdp = MapHelper.getMapDataProvider();
        mdp.clear();
        mdp.addCartridges(cartridgeFiles);
        MainActivity.wui.showScreen(WUI.SCREEN_MAP, null);
    }

    private void clickStart() {
        // check cartridges
        if (!isAnyCartridgeAvailable()) {
            return;
        }

        ChooseCartridgeDialog dialog = new ChooseCartridgeDialog();
        dialog.setParams(cartridgeFiles);
        getSupportFragmentManager()
                .beginTransaction()
                .add(dialog, "DIALOG_TAG_CHOOSE_CARTRIDGE")
                .commitAllowingStateLoss();
    }

    @Override
    protected void eventCreateLayout() {
        setContentView(R.layout.layout_main);

        // set title
        ((TextView) findViewById(R.id.title_text)).setText(MainApplication.APP_NAME);

        // define buttons
        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_start:
                        clickStart();
                        break;
                    case R.id.button_gps:
                        MainActivity.this.startActivity(new Intent(MainActivity.this, SatelliteActivity.class));
                        break;
                    case R.id.button_settings:
                        MainActivity.this.startActivity(new Intent(MainActivity.this, XmlSettingsActivity.class));
                        break;
                    case R.id.button_map:
                        clickMap();
                        break;
                    case R.id.button_logo:
                        getSupportFragmentManager().beginTransaction()
                                .add(new AboutDialog(), "DIALOG_TAG_MAIN").commitAllowingStateLoss();
                        break;
                }
            }
        };

        UtilsGUI.setButtons(this, new int[]{R.id.button_start, R.id.button_map, R.id.button_gps,
                R.id.button_settings, R.id.button_logo}, mOnClickListener, null);
    }

    @Override
    protected void eventDestroyApp() {
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }

    @Override
    protected void eventFirstInit() {
        // call after start actions here
        VersionInfo.afterStartAction();
    }

    @Override
    protected void eventRegisterOnly() {
    }

    @Override
    protected void eventSecondInit() {
    }

    @Override
    protected String getCloseAdditionalText() {
        return null;
    }

    @Override
    protected int getCloseValue() {
        return CLOSE_DESTROY_APP_NO_DIALOG;
    }

    private boolean isAnyCartridgeAvailable() {
        if (cartridgeFiles == null || cartridgeFiles.size() == 0) {
            UtilsGUI.showDialogInfo(
                    MainActivity.this,
                    getString(R.string.no_wherigo_cartridge_available, FileSystem.ROOT,
                            MainApplication.APP_NAME));
            return false;
        } else {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        final String[] permissions = new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 0);
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        testFileSystem();
        if (Preferences.GPS || Preferences.GPS_START_AUTOMATICALLY) {
            LocationState.setGpsOn(this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Intent intent = new Intent(getIntent());
            intent.setClass(this, DownloadCartridgeActivity.class);
            startActivity(intent);
            finish();
        } else if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
            try {
                Uri uri = Uri.parse(getIntent().getStringExtra(Intent.EXTRA_TEXT));
                if (uri.getQueryParameter("CGUID") == null)
                    throw new Exception("Invalid URL");
                Intent intent = new Intent(this, DownloadCartridgeActivity.class);
                intent.setData(uri);
                startActivity(intent);
            } catch (Exception e) {
                ManagerNotify.toastShortMessage(this, getString(R.string.invalid_url));
            }
            finish();
        } else {
            String cguid = getIntent() == null ? null : getIntent().getStringExtra("cguid");
            if (cguid != null) {
                File file = FileSystem.findFile(cguid);
                if (file != null) {
                    openCartridge(file);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCartridges();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_geocaching:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://geocaching.com/")));
                return true;
            case R.id.menu_wherigo:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://wherigo.com/")));
                return true;
            case R.id.menu_github:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/biylda/WhereYouGo")));
            default:
                return false;
        }
    }

    private void openCartridge(File file) {
        try {
            CartridgeFile cart = null;
            try {
                cart = CartridgeFile.read(new WSeekableFile(file), new WSaveFile(file));
                if (cart != null) {
                    cart.filename = file.getAbsolutePath();
                } else {
                    return;
                }
            } catch (Exception e) {
                Logger.w(TAG, "openCartridge(), file:" + file + ", e:" + e.toString());
                ManagerNotify.toastShortMessage(Locale.getString(R.string.invalid_cartridge, file.getName()));
                // file.delete();
            }
            openCartridge(cart);
        } catch (Exception e) {
            Logger.e(TAG, "onCreate()", e);
        }
    }

    @Override
    public void onDestroy() {
        // Stop notification
        Intent intent = new Intent(MainActivity.this, NotificationService.class);
        intent.setAction(NotificationService.STOP_NOTIFICATION_SERVICE);
        startService(intent);
        super.onDestroy();
    }
}
