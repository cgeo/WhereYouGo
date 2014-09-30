package menion.android.whereyougo.maps.utils;

import cz.matejcik.openwig.EventTable;
import cz.matejcik.openwig.Zone;
import locus.api.android.ActionDisplayPoints;
import locus.api.android.ActionDisplayTracks;
import locus.api.android.ActionTools;
import locus.api.android.ActionDisplay.ExtraAction;
import locus.api.android.utils.LocusUtils;
import locus.api.android.utils.RequiredVersionMissingException;
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
