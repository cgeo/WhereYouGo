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

package menion.android.whereyougo.preferences;

import menion.android.whereyougo.MainApplication;

public class Locale {

    public static String getString(int string) {
        if (MainApplication.getContext() != null) {
            return MainApplication.getContext().getString(string);
        } else {
            return "";
        }
    }

    public static String getString(int string, Object... formatArgs) {
        if (MainApplication.getContext() != null) {
            return MainApplication.getContext().getString(string, formatArgs);
        } else {
            return "";
        }
    }
}
