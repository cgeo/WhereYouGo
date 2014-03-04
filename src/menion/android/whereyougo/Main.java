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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;

import locus.api.android.ActionDisplay.ExtraAction;
import locus.api.android.ActionDisplayPoints;
import locus.api.android.objects.PackWaypoints;
import locus.api.objects.extra.ExtraData;
import locus.api.objects.extra.Location;
import locus.api.objects.extra.Waypoint;
import menion.android.whereyougo.gui.dialogs.DialogChooseCartridge;
import menion.android.whereyougo.gui.dialogs.DialogMain;
import menion.android.whereyougo.gui.extension.CustomMain;
import menion.android.whereyougo.gui.extension.MainApplication;
import menion.android.whereyougo.gui.extension.UtilsGUI;
import menion.android.whereyougo.gui.location.SatelliteScreen;
import menion.android.whereyougo.guiding.GuidingScreen;
import menion.android.whereyougo.settings.Loc;
import menion.android.whereyougo.settings.UtilsSettings;
import menion.android.whereyougo.utils.Const;
import menion.android.whereyougo.utils.FileSystem;
import menion.android.whereyougo.utils.Images;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.ManagerNotify;
import menion.android.whereyougo.utils.Utils;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.formats.CartridgeFile;

public class Main extends CustomMain {

  private static final String TAG = "Main";

  public static WUI wui = new WUI();
  public static WLocationService wLocationService = new WLocationService();
  public static CartridgeFile cartridgeFile;

  private static Vector<CartridgeFile> cartridgeFiles;

  public static String selectedFile;

  public static void setSelectedFile(String filepath) {
    Main.selectedFile = filepath;
  }

  public static String getSelectedFile() {
    return selectedFile;
  }

  @Override
  protected void eventFirstInit() {
    // call after start actions here
    MainAfterStart.afterStartAction();
  }

  @Override
  protected void eventSecondInit() {}

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
            Intent intent02 = new Intent(Main.this, SatelliteScreen.class);
            startActivity(intent02);
            break;
          case R.id.button_settings:
            UtilsSettings.showSettings(Main.this);
            break;
          case R.id.button_map:
            clickMap();
            break;
          case R.id.button_logo:
            getSupportFragmentManager().beginTransaction().add(new DialogMain(), "DIALOG_TAG_MAIN")
                .commitAllowingStateLoss();
            break;
        }
      }
    };

    UtilsGUI.setButtons(this, new int[] {R.id.button_start, R.id.button_map, R.id.button_gps,
        R.id.button_settings, R.id.button_logo}, mOnClickListener, null);
  }

  private void clickStart() {
    // check cartridges
    if (!isAnyCartridgeAvailable()) {
      return;
    }

    DialogChooseCartridge dialog = new DialogChooseCartridge();
    dialog.setParams(cartridgeFiles);
    getSupportFragmentManager().beginTransaction().add(dialog, "DIALOG_TAG_CHOOSE_CARTRIDGE")
        .commitAllowingStateLoss();
  }

  private void clickMap() {
    // check cartridges
    if (!isAnyCartridgeAvailable()) {
      return;
    }

    try {
      // complete waypoints data
      PackWaypoints pack = new PackWaypoints("WhereYouGo");
      Bitmap b = Images.getImageB(R.drawable.ic_title_logo, (int) Utils.getDpPixels(24.0f));
      pack.setBitmap(b);
      for (CartridgeFile cartridge : cartridgeFiles) {
        // do not show waypoints that are "Play anywhere" (with zero
        // coordinates)
        if (cartridge.latitude % 360.0 == 0 && cartridge.longitude % 360.0 == 0) {
          continue;
        }

        // construct waypoint
        Location loc = new Location(TAG);
        loc.setLatitude(cartridge.latitude);
        loc.setLongitude(cartridge.longitude);
        Waypoint wpt = new Waypoint(cartridge.name, loc);
        wpt.addParameter(ExtraData.PAR_DESCRIPTION, cartridge.description);
        wpt.addUrl(cartridge.url);
        pack.addWaypoint(wpt);
      }

      ActionDisplayPoints.sendPack(this, pack, ExtraAction.NONE);
    } catch (Exception e) {
      Logger.e(TAG, "clickMap()", e);
    }
  }

  private boolean isAnyCartridgeAvailable() {
    if (cartridgeFiles == null || cartridgeFiles.size() == 0) {
      UtilsGUI.showDialogInfo(
          Main.this,
          getString(R.string.no_wherigo_cartridge_available, FileSystem.ROOT,
              MainApplication.APP_NAME));
      return false;
    } else {
      return true;
    }
  }

  @Override
  protected void eventDestroyApp() {
    NotificationManager mNotificationManager =
        (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.cancelAll();
  }

  @Override
  public void onResume() {
    super.onResume();
    refreshCartridges();
  }

  public static void loadCartridge(OutputStream log) {
    try {
      WUI.startProgressDialog();
      Engine.newInstance(cartridgeFile, log, wui, wLocationService).start();
    } catch (Throwable t) {
    }
  }

  public static void restoreCartridge(OutputStream log) {
    try {
      WUI.startProgressDialog();
      Engine.newInstance(cartridgeFile, log, wui, wLocationService).restore();
    } catch (Throwable t) {
    }
  }

  public static File getSaveFile() throws IOException {
    try {
      File file = new File(selectedFile.substring(0, selectedFile.length() - 3) + "ows");
      return file;
    } catch (SecurityException e) {
      Logger.e(TAG, "getSyncFile()", e);
      return null;
    }
  }

  public static void setBitmapToImageView(Bitmap i, ImageView iv) {
    Logger.w(TAG, "setBitmapToImageView(), " + i.getWidth() + " x " + i.getHeight());
    float width = i.getWidth() - 10;
    float height = (Const.SCREEN_WIDTH / width) * i.getHeight();

    if ((height / Const.SCREEN_HEIGHT) > 0.60f) {
      height = 0.60f * Const.SCREEN_HEIGHT;
      width = (height / i.getHeight()) * i.getWidth();
    }
    iv.setMinimumWidth((int) width);
    iv.setMinimumHeight((int) height);
    iv.setImageBitmap(i);
  }

  private void refreshCartridges() {
    Logger.w(TAG, "refreshCartridges(), " + (Main.selectedFile == null));
    if (Main.selectedFile != null)
      return;

    // load cartridge files
    File[] files = FileSystem.getFiles(FileSystem.ROOT, "gwc");
    cartridgeFiles = new Vector<CartridgeFile>();

    // add cartridges to map
    ArrayList<Waypoint> wpts = new ArrayList<Waypoint>();

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
          ManagerNotify.toastShortMessage(Loc.get(R.string.invalid_cartridge, file.getName()));
        }
      }
    }

    if (wpts.size() > 0) {
      // TODO add items on map
    }
  }

  /**
   * Call activity that guide onto point.
   * 
   * @param activity
   * @return true if internal activity was called. False if external by intent.
   */
  public static boolean callGudingScreen(Activity activity) {
    Intent intent = new Intent(activity, GuidingScreen.class);
    activity.startActivity(intent);
    return true;
  }

  @Override
  protected int getCloseValue() {
    return CLOSE_DESTROY_APP_NO_DIALOG;
  }

  @Override
  protected String getCloseAdditionalText() {
    return null;
  }

  @Override
  protected void eventRegisterOnly() {}
}
