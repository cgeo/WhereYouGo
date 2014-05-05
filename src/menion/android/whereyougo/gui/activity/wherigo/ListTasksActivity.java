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

import java.util.Vector;

import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.openwig.WUI;
import menion.android.whereyougo.utils.Images;
import android.graphics.Bitmap;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.Task;

public class ListTasksActivity extends ListVariousActivity {

  private static Bitmap[] stateIcons;

  static {
    stateIcons = new Bitmap[3];
    stateIcons[Task.PENDING] = Images.getImageB(R.drawable.task_pending);
    stateIcons[Task.DONE] = Images.getImageB(R.drawable.task_done);
    stateIcons[Task.FAILED] = Images.getImageB(R.drawable.task_failed);
  }

  @Override
  protected void callStuff(Object what) {
    Task z = (Task) what;
    if (z.hasEvent("OnClick")) {
      Engine.callEvent(z, "OnClick", null);
    } else {
      MainActivity.wui.showScreen(WUI.DETAILSCREEN, z);
    }
    ListTasksActivity.this.finish();
  }

  protected Bitmap getStuffIcon(Object what) {
    return stateIcons[((Task) what).state()];
  }

  @Override
  protected String getStuffName(Object what) {
    return ((Task) what).name;
  }

  @Override
  protected Vector<Object> getValidStuff() {
    Vector<Object> newtasks = new Vector<Object>();
    for (int i = 0; i < Engine.instance.cartridge.tasks.size(); i++) {
      Task t = (Task) Engine.instance.cartridge.tasks.get(i);
      if (t.isVisible())
        newtasks.add(t);
    }
    return newtasks;
  }

  @Override
  protected boolean stillValid() {
    return true;
  }
}
