/*
 * Copyright 2013, 2014 biylda <biylda@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */

package menion.android.whereyougo.maps.utils;

import android.app.Activity;
import android.content.Intent;

import cz.matejcik.openwig.EventTable;
import locus.api.android.ActionDisplayVarious.ExtraAction;
import locus.api.android.ActionDisplayPoints;
import locus.api.android.ActionDisplayTracks;
import locus.api.android.ActionBasics;
import locus.api.android.utils.LocusUtils;
import locus.api.android.utils.exceptions.RequiredVersionMissingException;
import locus.api.objects.extra.Location;
import locus.api.objects.geoData.Point;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.utils.UtilsWherigo;
import menion.android.whereyougo.maps.mapsforge.MapsforgeActivity;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.Logger;

public class MapHelper {
    public static MapDataProvider getMapDataProvider() {
        switch (Preferences.GLOBAL_MAP_PROVIDER) {
            case PreferenceValues.VALUE_MAP_PROVIDER_VECTOR:
                return VectorMapDataProvider.getInstance();
            case PreferenceValues.VALUE_MAP_PROVIDER_LOCUS:
                return LocusMapDataProvider.getInstance();
            default:
                return VectorMapDataProvider.getInstance();
        }
    }

    public static void showMap(Activity activity) {
        showMap(activity, null);
    }

    public static void showMap(Activity activity, EventTable waypoint) {
        switch (Preferences.GLOBAL_MAP_PROVIDER) {
            case PreferenceValues.VALUE_MAP_PROVIDER_VECTOR:
                vectorMap(activity, waypoint);
                break;
            case PreferenceValues.VALUE_MAP_PROVIDER_LOCUS:
                locusMap(activity, waypoint);
                break;
        }
    }

    public static void vectorMap(Activity activity, EventTable et) {
        boolean navigate = et != null && et.isLocated();

        Intent intent = new Intent(activity, MapsforgeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(MapsforgeActivity.BUNDLE_CENTER, navigate);
        intent.putExtra(MapsforgeActivity.BUNDLE_NAVIGATE, navigate);
        intent.putExtra(MapsforgeActivity.BUNDLE_ALLOW_START_CARTRIDGE, activity instanceof MainActivity);
        activity.startActivity(intent);
    }

    public static void locusMap(Activity activity, EventTable et) {
        LocusMapDataProvider mdp = LocusMapDataProvider.getInstance();
        try {
            ActionDisplayPoints.INSTANCE.sendPack(activity, mdp.getPoints(), ExtraAction.NONE);
            ActionDisplayTracks.INSTANCE.sendTracks(activity, mdp.getTracks(), ExtraAction.CENTER);
            if (et != null && et.isLocated()) {
                Location loc = UtilsWherigo.extractLocation(et);
                Point wpt = new Point(et.name, loc);
                ActionBasics.INSTANCE.actionStartGuiding(activity, wpt);
            }
        } catch (RequiredVersionMissingException e) {
            Logger.e(activity.toString(), "MapHelper.showMap() - missing locus version", e);
            LocusUtils.INSTANCE.callInstallLocus(activity);
        } catch (Exception e) {
            Logger.e(activity.toString(), "MapHelper.showMap() - unknown locus problem", e);
        }
    }
}
