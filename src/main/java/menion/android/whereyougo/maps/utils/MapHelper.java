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

import cz.matejcik.openwig.EventTable;
import cz.matejcik.openwig.Zone;
import locus.api.android.ActionDisplayPoints;
import locus.api.android.ActionDisplayTracks;
import locus.api.android.ActionTools;
import locus.api.android.ActionDisplay.ExtraAction;
import locus.api.android.utils.LocusUtils;
import locus.api.android.utils.exceptions.RequiredVersionMissingException;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;
import menion.android.whereyougo.utils.Logger;
import android.app.Activity;
import android.content.Intent;

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
    boolean center = navigate;

    Intent intent =
        new Intent(activity, menion.android.whereyougo.maps.mapsforge.MapsforgeActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    intent.putExtra("center", center);
    intent.putExtra("navigate", navigate);
    activity.startActivity(intent);
  }

  public static void locusMap(Activity activity, EventTable et) {
    LocusMapDataProvider mdp = LocusMapDataProvider.getInstance();
    try {
      if(!mdp.getPoints().getWaypoints().isEmpty())
        ActionDisplayPoints.sendPack(activity, mdp.getPoints(), ExtraAction.NONE);
      if(!mdp.getTracks().isEmpty())
        ActionDisplayTracks.sendTracks(activity, mdp.getTracks(), ExtraAction.CENTER);
      if (et != null && et.isLocated()) {
        locus.api.objects.extra.Location loc =
            new locus.api.objects.extra.Location(activity.toString());
        if (et instanceof Zone) {
          Zone z = ((Zone) et);
          loc.setLatitude(z.nearestPoint.latitude);
          loc.setLongitude(z.nearestPoint.longitude);
        } else {
          loc.setLatitude(et.position.latitude);
          loc.setLongitude(et.position.longitude);
        }
        locus.api.objects.extra.Waypoint wpt = new locus.api.objects.extra.Waypoint(et.name, loc);
        ActionTools.actionStartGuiding(activity, wpt);
      }
    } catch (RequiredVersionMissingException e) {
      Logger.e(activity.toString(), "MapHelper.showMap() - missing locus version", e);
      LocusUtils.callInstallLocus(activity);
    } catch (Exception e) {
      Logger.e(activity.toString(), "MapHelper.showMap() - unknown locus problem", e);
    }
  }
}
