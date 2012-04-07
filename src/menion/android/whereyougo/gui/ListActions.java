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
import menion.android.whereyougo.WUI;
import cz.matejcik.openwig.Action;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.Thing;

public class ListActions extends ListVarious {

	private static Thing thing;
	
	public static void reset(Thing what) {
		ListActions.thing = what;
	}
	
	@Override
	protected void callStuff(Object what) {
		Action z = (Action)what;
		callAction(z);
		ListActions.this.finish();
	}
	
	public static void callAction(Action z) {
		String eventName = "On" + z.getName();

		if (z.hasParameter()) {
			if (z.getActor() == thing) {
				ListTargets.reset(thing.name + ": " + z.text, z, thing);
				Main.wui.showScreen(WUI.SCREEN_TARGETS, null);
			} else {
				Main.wui.showScreen(WUI.DETAILSCREEN, Details.et);
				Engine.callEvent(z.getActor(), eventName, thing);
			}
		} else {
			Main.wui.showScreen(WUI.DETAILSCREEN, Details.et);
			Engine.callEvent(thing, eventName, null);
		}
	}

	@Override
	protected String getStuffName(Object what) {
		Action a = (Action) what;
		if (a.getActor() == thing)
			return a.text;
		else return (a.getActor().name + ": " + a.text);
	}

	@Override
	protected Vector<Object> getValidStuff() {
		return getValidActions(thing);
	}
	
	public static Vector<Object> getValidActions(Thing thing) {
		Vector<Object> newActions = new Vector<Object>();
		for (int i = 0; i < thing.actions.size(); i++)
			newActions.add(thing.actions.get(i));
		
		for (int i = 0; i < newActions.size(); i++) {
			Action a = (Action) newActions.elementAt(i);
			if (!a.isEnabled() || !a.getActor().visibleToPlayer()) {
				newActions.removeElementAt(i--);
				continue;
			}
		}
		return newActions;
	}
	
	@Override
	protected boolean stillValid() {
		if (!thing.visibleToPlayer())
			return false;
		return thing.visibleActions() > 0;
	}

}
