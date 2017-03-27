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

import menion.android.whereyougo.gui.extension.activity.CustomMainActivity;
import menion.android.whereyougo.preferences.PreferenceValues;

/**
 * @author menion
 * @since 1.4.2010 2010
 */
public class VersionInfo {

    public static String getNews(int lastVersion, int actualVersion) {
        String newsInfo = "";

        if (lastVersion == 0) {
            newsInfo +=
                    "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /></head><body>";
            newsInfo +=
                    CustomMainActivity.loadAssetString(PreferenceValues.getLanguageCode() + "_first.html");
            newsInfo += "</body></html>";
        } else {
            newsInfo = CustomMainActivity.getNewsFromTo(lastVersion, actualVersion);
        }

        return newsInfo;
    }
}
