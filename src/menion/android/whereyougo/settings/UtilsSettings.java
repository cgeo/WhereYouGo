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

package menion.android.whereyougo.settings;

import menion.android.whereyougo.gui.SettingScreens;
import menion.android.whereyougo.gui.extension.CustomPreferenceActivity;
import android.app.Activity;
import android.content.Intent;
import android.preference.Preference;

public class UtilsSettings {

	public static void setDependecies(CustomPreferenceActivity act) {
		setDependency(act, Settings.KEY_S_GUIDING_WAYPOINT_SOUND,
				Settings.KEY_B_GUIDING_COMPASS_SOUNDS);
		setDependency(act, Settings.KEY_S_GUIDING_WAYPOINT_SOUND_DISTANCE,
				Settings.KEY_B_GUIDING_COMPASS_SOUNDS);
		
		setDependency(act, Settings.KEY_B_GUIDING_GPS_REQUIRED, 
				Settings.KEY_B_GPS_DISABLE_WHEN_HIDE);
		
		setDependency(act, Settings.KEY_S_HARDWARE_COMPASS_AUTO_CHANGE_VALUE, 
				Settings.KEY_B_HARDWARE_COMPASS_AUTO_CHANGE);
	}
	
	private static void setDependency(CustomPreferenceActivity act, String prefKey, String parentKey) {
        Preference pref = act.getPreferenceManager().findPreference(prefKey);
        if (pref != null)
            pref.setDependency(parentKey);
	}
	
	public static void showSettings(Activity activity) {
		activity.startActivity(new Intent(activity, SettingScreens.class));			
	}
}
