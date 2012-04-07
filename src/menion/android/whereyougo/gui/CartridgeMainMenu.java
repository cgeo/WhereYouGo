/*
  * This file is part of WhereYouGo.
  *
  * WhereYouGo is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * WhereYouGo is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with WhereYouGo.  If not, see <http://www.gnu.org/licenses/>.
  *
  * Copyright (C) 2012 Menion <whereyougo@asamm.cz>
  */ 

package menion.android.whereyougo.gui;

import java.util.ArrayList;
import java.util.Vector;

import menion.android.whereyougo.Main;
import menion.android.whereyougo.R;
import menion.android.whereyougo.WUI;
import menion.android.whereyougo.gui.extension.CustomActivity;
import menion.android.whereyougo.gui.extension.CustomDialog;
import menion.android.whereyougo.gui.extension.DataInfo;
import menion.android.whereyougo.gui.extension.IconedListAdapter;
import menion.android.whereyougo.gui.location.SatelliteScreen;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.Utils;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.Player;
import cz.matejcik.openwig.Task;
import cz.matejcik.openwig.Thing;
import cz.matejcik.openwig.Zone;

public class CartridgeMainMenu extends CustomActivity implements Refreshable {

	private static final String TAG = "CartridgeMainMenu";
	
	private AdapterView.OnItemClickListener listClick;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.custom_dialog);

		listClick = new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Logger.d(TAG, "onItemClick:" + position);
				switch (position) {
				case 0:
					if (Engine.instance.cartridge.visibleZones() >= 1) {
						Main.wui.showScreen(WUI.LOCATIONSCREEN, null);
					}
					break;
				case 1:
					if (Engine.instance.cartridge.visibleThings() >= 1) {
						Main.wui.showScreen(WUI.ITEMSCREEN, null);
					}
					break;
				case 2:
					if (Engine.instance.player.visibleThings() >= 1) {
						Main.wui.showScreen(WUI.INVENTORYSCREEN, null);
					}
					break;
				case 3:
					if (getVisibleTasksCount() > 0) {
						Main.wui.showScreen(WUI.TASKSCREEN, null);
					}
					break;
				};
			}
		};
		
		CustomDialog.setTitle(this, Engine.instance.cartridge.name,
				null, CustomDialog.NO_IMAGE, null);
		CustomDialog.setBottom(this,
				getString(R.string.gps), new CustomDialog.OnClickListener() {
					@Override
					public boolean onClick(CustomDialog dialog, View v, int btn) {
						Intent intent = new Intent(CartridgeMainMenu.this, SatelliteScreen.class);
						startActivity(intent);
						return true;
					}
				}, null, null, null, null);
	}
	
	public void onResume() {
		super.onResume();
		refresh();
	}
	
	public void refresh() {
		runOnUiThread(new Runnable() {
			public void run() {
				ArrayList<DataInfo> data = new ArrayList<DataInfo>();
				DataInfo diLocations = new DataInfo(getString(R.string.locations) + " (" +
						Engine.instance.cartridge.visibleZones() + ")",
						getVisibleZonesDescription(), R.drawable.icon_locations);
				data.add(diLocations);
				
				DataInfo diYouSee = new DataInfo(getString(R.string.you_see) + " (" +
						Engine.instance.cartridge.visibleThings() + ")",
						getVisibleCartridgeThingsDescription(), R.drawable.icon_search);
				data.add(diYouSee);
				
				DataInfo diInventory = new DataInfo(getString(R.string.inventory) + " (" +
						Engine.instance.player.visibleThings() + ")",
						getVisiblePlayerThingsDescription(), R.drawable.icon_inventory);
				data.add(diInventory);
				
				DataInfo diTasks = new DataInfo(getString(R.string.tasks) + " (" +
						Engine.instance.cartridge.visibleTasks() + ")",
						getVisibleTasksDescription(), R.drawable.icon_tasks);
				data.add(diTasks);
				
				ListView lv = new ListView(CartridgeMainMenu.this);
				IconedListAdapter adapter = new IconedListAdapter(CartridgeMainMenu.this, data, lv);
				adapter.setMinHeight((int) Utils.getDpPixels(70));
				adapter.setTextView02Visible(View.VISIBLE, true);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(listClick);
				CustomDialog.setContent(CartridgeMainMenu.this, lv, 0, true, false);
			}
		});		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
Logger.d(TAG, "onKeyDown(" + keyCode + ", " + event + ")");
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			new CustomDialog.Builder(CartridgeMainMenu.this, true).
			setTitle(R.string.question, R.drawable.ic_question).
			setTitleExtraCancel().
			setMessage(R.string.save_game_before_exit).
			setPositiveButton(R.string.yes, new CustomDialog.OnClickListener() {
				public boolean onClick(CustomDialog dialog, View v, int btn) {
					Engine.requestSync();
					Main.selectedFile = null;
					new SaveGameOnExit().execute();
					return true;
				}
			}).
			setNeutralButtonCancel(R.string.cancel).
			setNegativeButton(R.string.no, new CustomDialog.OnClickListener() {
				public boolean onClick(CustomDialog dialog, View v, int btn) {
					Engine.kill();
					Main.selectedFile = null;
					CartridgeMainMenu.this.finish();
					return true;
				}
			}).show();
			return true;
		} else if (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
			return true;
		} else {
	    	return super.onKeyDown(keyCode, event);
		}
	}
	
	private class SaveGameOnExit extends AsyncTask<Void, Void, Void> {

		private ProgressDialog dialog;
		
		@Override
		protected void onPreExecute () {
			dialog = ProgressDialog.show(CartridgeMainMenu.this, null, getString(R.string.working));
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// let thread sleep for a while to be sure that cartridge is saved!
			try {
				while (WUI.saving) {
					Thread.sleep(100);
				}
			} catch (InterruptedException e) {}
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
			CartridgeMainMenu.this.finish();
		}
		
	}
	
	/***********************************/
	/*     SPECIAL ITEMS FUNCTIONS     */
	/***********************************/
	
//	private Vector<Zone> getVisibleZones() {
//		Vector<Zone> zones = Engine.instance.cartridge.zones;
//		Vector<Zone> visible = new Vector<Zone>();
//		for (int i = 0; i < zones.size(); i++) {
//			Zone z = (Zone) zones.get(i);
//			if (z.isVisible())
//				visible.add(z);
//		}
//		return visible;
//	}
	
	private String getVisibleZonesDescription() {
		String description = null;
		@SuppressWarnings("unchecked")
		Vector<Zone> zones = Engine.instance.cartridge.zones;
		for (int i = 0; i < zones.size(); i++) {
			Zone z = (Zone)zones.get(i);
			if (z.isVisible()) {
				if (description == null)
					description = "";
				else
					description += ", ";
				
				description += z.name;
			}
		}
		return description;
	}
	
	private String getVisibleCartridgeThingsDescription() {
		String description = null;
		@SuppressWarnings("unchecked")
		Vector<Zone> zones = Engine.instance.cartridge.zones;
		for (int i = 0; i < zones.size(); i++) {
			Zone z = (Zone)zones.elementAt(i);
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
			Task a = (Task)Engine.instance.cartridge.tasks.elementAt(i);
			if (a.isVisible()) count++;
		}
		return count;
	}
	
	public String getVisibleTasksDescription() {
		String description = null;
		for (int i = 0; i < Engine.instance.cartridge.tasks.size(); i++) {
			Task a = (Task)Engine.instance.cartridge.tasks.elementAt(i);
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
}