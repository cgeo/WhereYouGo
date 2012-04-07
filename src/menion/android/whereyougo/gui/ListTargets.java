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

import java.util.Vector;

import menion.android.whereyougo.Main;
import menion.android.whereyougo.R;
import menion.android.whereyougo.WUI;
import menion.android.whereyougo.gui.extension.CustomDialog;
import menion.android.whereyougo.gui.extension.UtilsGUI;
import se.krka.kahlua.vm.LuaTable;
import android.view.View;
import cz.matejcik.openwig.Action;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.Thing;

public class ListTargets extends ListVarious {

	private static String title;
	private static Action action;
	private static Thing thing;

	private static Vector<Object> validStuff;
	
	public static void reset(String title, Action what, Thing actor) {
		ListTargets.title = title;
		ListTargets.action = what;
		ListTargets.thing = actor;
		makeValidStuff();
	}
	
	private static void makeValidStuff() {
		LuaTable current = Engine.instance.cartridge.currentThings();
		int size = current.len() + Engine.instance.player.inventory.len();
		validStuff = new Vector<Object>();
		Object key = null;
		while ((key = current.next(key)) != null)
			validStuff.addElement(current.rawget(key));
		while ((key = Engine.instance.player.inventory.next(key)) != null)
			validStuff.addElement(Engine.instance.player.inventory.rawget(key));
		
		for (int i = 0; i < validStuff.size(); i++) {
			Thing t = (Thing)validStuff.elementAt(i);
			if (! t.isVisible() || ! action.isTarget(t)) {
				validStuff.removeElementAt(i--);
			}
		}
	}
	
	public void refresh() {
		if (validStuff.isEmpty()) {
			UtilsGUI.showDialogInfo(this, R.string.no_target,
					new CustomDialog.OnClickListener() {
						@Override
						public boolean onClick(CustomDialog dialog, View v, int btn) {
							ListTargets.this.finish();
							return true;
						}
					});
		} else {
			super.refresh();
		}
	}
	
	@Override
	protected void callStuff(Object what) {
		Main.wui.showScreen(WUI.DETAILSCREEN, Details.et); // XXX def null
		String eventName = "On" + action.getName();
		Engine.callEvent(action.getActor(), eventName, (Thing) what);
	}

	@Override
	protected String getStuffName(Object what) {
		return ((Thing)what).name;
	}

	@Override
	protected Vector<Object> getValidStuff() {
		return validStuff;
	}

	@Override
	protected boolean stillValid() {
		return thing.visibleToPlayer();
	}

}
