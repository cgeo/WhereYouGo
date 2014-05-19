package menion.android.whereyougo.gui.dialog;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import menion.android.whereyougo.R;
import menion.android.whereyougo.geo.location.Location;
import menion.android.whereyougo.geo.location.LocationState;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.extension.DataInfo;
import menion.android.whereyougo.gui.extension.IconedListAdapter;
import menion.android.whereyougo.gui.extension.dialog.CustomDialogFragment;
import menion.android.whereyougo.gui.utils.UtilsGUI;
import menion.android.whereyougo.openwig.WUI;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Images;
import menion.android.whereyougo.utils.Logger;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import cz.matejcik.openwig.formats.CartridgeFile;

public class ChooseCartridgeDialog extends CustomDialogFragment {

  private static final String TAG = "DialogChooseCartridge";

  private Vector<CartridgeFile> cartridgeFiles;

  public ChooseCartridgeDialog() {
    super();
  }

  @Override
  public Dialog createDialog(Bundle savedInstanceState) {
    if (A.getMain() == null || cartridgeFiles == null) {
      return null;
    }
    try {
      // sort cartridges
      final Location actLoc = LocationState.getLocation();
      final Location loc1 = new Location(TAG);
      final Location loc2 = new Location(TAG);
      Collections.sort(cartridgeFiles, new Comparator<CartridgeFile>() {

        @Override
        public int compare(CartridgeFile object1, CartridgeFile object2) {
          loc1.setLatitude(object1.latitude);
          loc1.setLongitude(object1.longitude);
          loc2.setLatitude(object2.latitude);
          loc2.setLongitude(object2.longitude);
          return (int) (actLoc.distanceTo(loc1) - actLoc.distanceTo(loc2));
        }
      });

      // prepare list
      ArrayList<DataInfo> data = new ArrayList<DataInfo>();
      for (int i = 0; i < cartridgeFiles.size(); i++) {
        CartridgeFile file = cartridgeFiles.get(i);
        byte[] iconData = file.getFile(file.iconId);
        Bitmap icon;
        try {
          icon = BitmapFactory.decodeByteArray(iconData, 0, iconData.length);
        } catch (Exception e) {
          icon = Images.getImageB(R.drawable.icon_gc_wherigo);
        }

        DataInfo di =
            new DataInfo(file.name, file.type + ", " + file.author + ", " + file.version, icon);
        di.value01 = file.latitude;
        di.value02 = file.longitude;
        di.setDistAzi(actLoc);
        data.add(di);
      }

      // complete adapter
      IconedListAdapter adapter = new IconedListAdapter(A.getMain(), data, null);
      adapter.setTextView02Visible(View.VISIBLE, false);

      // create listView
      ListView lv = UtilsGUI.createListView(getActivity(), false, data);
      // set click listener
      lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          itemClicked(position);
        }
      });
      // set on long click listener for file deletion
      lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
          itemLongClicked(position);
          return true;
        }
      });
      // construct dialog
      return new AlertDialog.Builder(getActivity()).setTitle(R.string.choose_cartridge)
          .setIcon(R.drawable.ic_title_logo).setView(lv).setNeutralButton(R.string.close, null)
          .create();
    } catch (Exception e) {
      Logger.e(TAG, "createDialog()", e);
    }
    return null;
  }

  private void itemClicked(int position) {
    try {
      MainActivity.cartridgeFile = cartridgeFiles.get(position);
      MainActivity.selectedFile = MainActivity.cartridgeFile.filename;

      if (MainActivity.cartridgeFile.getSavegame().exists()) {
        UtilsGUI.showDialogQuestion(getActivity(), R.string.resume_previous_cartridge,
            new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int btn) {
                File file =
                    new File(MainActivity.getSelectedFile().substring(0,
                        MainActivity.getSelectedFile().length() - 3)
                        + "gwl");
                FileOutputStream fos = null;
                try {
                  if (!file.exists())
                    file.createNewFile();
                  fos = new FileOutputStream(file, true);
                } catch (Exception e) {
                  Logger.e(TAG, "onResume() - create empty saveGame file", e);
                }
                MainActivity.restoreCartridge(fos);
              }
            }, new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int btn) {
                MainActivity.wui.showScreen(WUI.SCREEN_CART_DETAIL, null);
                try {
                  MainActivity.getSaveFile().delete();
                } catch (Exception e) {
                  Logger.e(TAG, "onCreate() - deleteSyncFile", e);
                }
              }
            }, null);
      } else {
        MainActivity.wui.showScreen(WUI.SCREEN_CART_DETAIL, null);
      }
    } catch (Exception e) {
      Logger.e(TAG, "onCreate()", e);
    }
    dismiss();
  }
  
  private void itemLongClicked(int position) {
    try {
      CartridgeFile cartridgeFile = cartridgeFiles.get(position);
      final String filename = cartridgeFile.filename.substring(0,
          cartridgeFile.filename.length() - 3);

        UtilsGUI.showDialogQuestion(getActivity(), R.string.delete_cartridge,
            new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int btn) {
                new File(filename+"gwc").delete();
                new File(filename+"gwl").delete();
                new File(filename+"ows").delete();
                MainActivity.refreshCartridges();
              }
            }, null);
    } catch (Exception e) {
      Logger.e(TAG, "onCreate()", e);
    }
    dismiss();
  }

  public void setParams(Vector<CartridgeFile> cartridgeFiles) {
    this.cartridgeFiles = cartridgeFiles;
  }
}
