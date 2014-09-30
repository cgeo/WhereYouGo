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

package menion.android.whereyougo.gui.activity.wherigo;

import java.util.ArrayList;
import java.util.Vector;

import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.IRefreshable;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.activity.SatelliteActivity;
import menion.android.whereyougo.gui.extension.DataInfo;
import menion.android.whereyougo.gui.extension.IconedListAdapter;
import menion.android.whereyougo.gui.extension.activity.CustomActivity;
import menion.android.whereyougo.gui.extension.dialog.CustomDialog;
import menion.android.whereyougo.gui.utils.UtilsGUI;
import menion.android.whereyougo.maps.utils.MapDataProvider;
import menion.android.whereyougo.maps.utils.MapHelper;
import menion.android.whereyougo.openwig.WUI;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.FileSystem;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.ManagerNotify;
import menion.android.whereyougo.utils.Utils;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.Player;
import cz.matejcik.openwig.Task;
import cz.matejcik.openwig.Thing;
import cz.matejcik.openwig.Zone;

public class MainMenuActivity extends CustomActivity implements IRefreshable {

  private class SaveGameOnExit extends AsyncTask<Void, Void, Void> {

    private ProgressDialog dialog;

    @Override
    protected Void doInBackground(Void... params) {
      // let thread sleep for a while to be sure that cartridge is saved!
      try {
        while (WUI.saving) {
          Thread.sleep(100);
        }
      } catch (InterruptedException e) {
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      try {
        if (dialog != null) {
          dialog.cancel();
          dialog = null;
        }
      } catch (Exception e) {
        Logger.w(TAG, "onPostExecute(), e:" + e.toString());
      }
      Engine.kill();
      MainMenuActivity.this.finish();
    }

    @Override
    protected void onPreExecute() {
      dialog = ProgressDialog.show(MainMenuActivity.this, null, getString(R.string.working));
    }

  }

  private static final String TAG = "CartridgeMainMenu";

  private AdapterView.OnItemClickListener listClick;

  private String getVisibleCartridgeThingsDescription() {
    String description = null;
    @SuppressWarnings("unchecked")
    Vector<Zone> zones = Engine.instance.cartridge.zones;
    for (int i = 0; i < zones.size(); i++) {
      Zone z = (Zone) zones.elementAt(i);
      String des = getVisibleThingsDescription(z);
      if (des != null) {
        if (description == null)
          description = "";
        else
          description += ", ";

        description += des;
      }
    }
    return description;
  }

  private String getVisiblePlayerThingsDescription() {
    Player p = Engine.instance.player;
    String description = null;
    Object key = null;
    while ((key = p.inventory.next(key)) != null) {
      Object o = p.inventory.rawget(key);
      if (o instanceof Thing && ((Thing) o).isVisible()) {
        if (description == null)
          description = "";
        else
          description += ", ";

        description += ((Thing) o).name;
      }
    }
    return description;
  }

  public int getVisibleTasksCount() {
    int count = 0;
    for (int i = 0; i < Engine.instance.cartridge.tasks.size(); i++) {
      Task a = (Task) Engine.instance.cartridge.tasks.elementAt(i);
      if (a.isVisible())
        count++;
    }
    return count;
  }

  public String getVisibleTasksDescription() {
    String description = null;
    for (int i = 0; i < Engine.instance.cartridge.tasks.size(); i++) {
      Task a = (Task) Engine.instance.cartridge.tasks.elementAt(i);
      if (a.isVisible()) {
        if (description == null)
          description = "";
        else
          description += ", ";
        description += a.name;
      }
    }
    return description;
  }

  private String getVisibleThingsDescription(Zone z) {
    String description = null;
    if (!z.showThings())
      return null;
    Object key = null;
    while ((key = z.inventory.next(key)) != null) {
      Object o = z.inventory.rawget(key);
      if (o instanceof Player)
        continue;
      if (!(o instanceof Thing))
        continue;
      if (((Thing) o).isVisible()) {
        if (description == null)
          description = "";
        else
          description += ", ";

        description += ((Thing) o).name;
      }
    }
    return description;
  }

  /***********************************/
  /* SPECIAL ITEMS FUNCTIONS */
  /***********************************/

  // private Vector<Zone> getVisibleZones() {
  // Vector<Zone> zones = Engine.instance.cartridge.zones;
  // Vector<Zone> visible = new Vector<Zone>();
  // for (int i = 0; i < zones.size(); i++) {
  // Zone z = (Zone) zones.get(i);
  // if (z.isVisible())
  // visible.add(z);
  // }
  // return visible;
  // }

  private String getVisibleZonesDescription() {
    String description = null;
    @SuppressWarnings("unchecked")
    Vector<Zone> zones = Engine.instance.cartridge.zones;
    for (int i = 0; i < zones.size(); i++) {
      Zone z = (Zone) zones.get(i);
      if (z.isVisible()) {
        if (description == null)
          description = "";
        else
          description += ", ";

        description += z.name;
        if (z.contains(Engine.instance.player))
          description += " (INSIDE)";
      }
    }
    return description;
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (A.getMain() == null || Engine.instance == null) {
      finish();
      return;
    }
    setContentView(R.layout.custom_dialog);

    listClick = new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Logger.d(TAG, "onItemClick:" + position);
        switch (position) {
          case 0:
            if (Engine.instance.cartridge.visibleZones() >= 1) {
              MainActivity.wui.showScreen(WUI.LOCATIONSCREEN, null);
            }
            break;
          case 1:
            if (Engine.instance.cartridge.visibleThings() >= 1) {
              MainActivity.wui.showScreen(WUI.ITEMSCREEN, null);
            }
            break;
          case 2:
            if (Engine.instance.player.visibleThings() >= 1) {
              MainActivity.wui.showScreen(WUI.INVENTORYSCREEN, null);
            }
            break;
          case 3:
            if (getVisibleTasksCount() > 0) {
              MainActivity.wui.showScreen(WUI.TASKSCREEN, null);
            }
            break;
        };
      }
    };

    CustomDialog.setTitle(this, Engine.instance.cartridge.name, null, CustomDialog.NO_IMAGE, null);
    CustomDialog.setBottom(this, getString(R.string.gps), new CustomDialog.OnClickListener() {
      @Override
      public boolean onClick(CustomDialog dialog, View v, int btn) {
        Intent intent = new Intent(MainMenuActivity.this, SatelliteActivity.class);
        startActivity(intent);
        return true;
      }
    }, getString(R.string.map), new CustomDialog.OnClickListener() {
      @Override
      public boolean onClick(CustomDialog dialog, View v, int btn) {
        MapDataProvider mdp = MapHelper.getMapDataProvider();
        mdp.clear();
        mdp.addAll();
        MainActivity.wui.showScreen(WUI.SCREEN_MAP, null);
        return true;
      }
    }, getString(R.string.save_game), new CustomDialog.OnClickListener() {
      @Override
      public boolean onClick(CustomDialog dialog, View v, int btn) {
        // backup
        try {
          FileSystem.backupFile(MainActivity.getSaveFile());
        } catch (Exception e) {
        }
        
        Engine.requestSync();
        Toast.makeText(MainMenuActivity.this, R.string.save_game_ok, Toast.LENGTH_SHORT).show();
        return true;
      }
    });
  }

  private long lastPressedTime = 0;
  private static int DOUBLE_PRESS_HK_BACK_PERIOD = 666;
  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    Logger.d(TAG, "onKeyDown(" + keyCode + ", " + event + ")");
    if ( event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
      if ( event.getDownTime() - lastPressedTime < DOUBLE_PRESS_HK_BACK_PERIOD) {
        /* exit game */
        UtilsGUI.showDialogQuestion(this, R.string.save_game_before_exit,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // backup
                try {
                  FileSystem.backupFile(MainActivity.getSaveFile());
                } catch (Exception e) {
                }

                Engine.requestSync();
                MainActivity.selectedFile = null;
                DetailsActivity.et = null;
                new SaveGameOnExit().execute();
            }
    	  }, new DialogInterface.OnClickListener() {
            @Override
              public void onClick(DialogInterface dialog, int which) {
                Engine.kill();
                MainActivity.selectedFile = null;
                DetailsActivity.et = null;
                MainMenuActivity.this.finish();
              }
          }, null);

          return true;    		
      } else {
        /* back is tapped once */
        lastPressedTime = event.getDownTime();
        ManagerNotify.toastShortMessage(R.string.msg_exit_game);
        return true;
      }
    } else if (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
        return true;
    } else {
        return super.onKeyDown(keyCode, event);
    }    
  }

  public void onResume() {
    super.onResume();
    refresh();
  }

  public void refresh() {
    runOnUiThread(new Runnable() {
      public void run() {
        if (A.getMain() == null || Engine.instance == null || Engine.instance.cartridge == null) {
          return;
        }
        
        ArrayList<DataInfo> data = new ArrayList<DataInfo>();
        DataInfo diLocations =
            new DataInfo(getString(R.string.locations) + " ("
                + Engine.instance.cartridge.visibleZones() + ")", getVisibleZonesDescription(),
                R.drawable.icon_locations);
        data.add(diLocations);

        DataInfo diYouSee =
            new DataInfo(getString(R.string.you_see) + " ("
                + Engine.instance.cartridge.visibleThings() + ")",
                getVisibleCartridgeThingsDescription(), R.drawable.icon_search);
        data.add(diYouSee);

        DataInfo diInventory =
            new DataInfo(getString(R.string.inventory) + " ("
                + Engine.instance.player.visibleThings() + ")",
                getVisiblePlayerThingsDescription(), R.drawable.icon_inventory);
        data.add(diInventory);

        DataInfo diTasks =
            new DataInfo(getString(R.string.tasks) + " ("
                + Engine.instance.cartridge.visibleTasks() + ")", getVisibleTasksDescription(),
                R.drawable.icon_tasks);
        data.add(diTasks);

        ListView lv = new ListView(MainMenuActivity.this);
        IconedListAdapter adapter = new IconedListAdapter(MainMenuActivity.this, data, lv);
        adapter.setMinHeight((int) Utils.getDpPixels(70));
        adapter.setTextView02Visible(View.VISIBLE, true);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(listClick);
        CustomDialog.setContent(MainMenuActivity.this, lv, 0, true, false);
      }
    });
  }
}
