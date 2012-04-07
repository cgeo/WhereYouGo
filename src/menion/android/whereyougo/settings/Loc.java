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

import menion.android.whereyougo.utils.A;

public class Loc {

	public static final String get(int string) {
		if (A.getApp() != null) {
			return A.getApp().getString(string);
		} else {
			return "";
		}
	}
	
	public static final String get(int string, String replace) {
		if (A.getApp() != null) {
			return A.getApp().getString(string, replace);
		} else {
			return "";
		}
	}
	
	public static final String get(int string, String replace01, String replace02) {
		if (A.getApp() != null) {
			return A.getApp().getString(string, replace01, replace02);
		} else {
			return "";
		}
	}
}
