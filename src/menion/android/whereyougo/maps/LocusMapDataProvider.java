package menion.android.whereyougo.maps;

import java.util.ArrayList;
import java.util.Vector;

import locus.api.android.objects.PackWaypoints;
import locus.api.objects.extra.ExtraData;
import locus.api.objects.extra.ExtraStyle;
import locus.api.objects.extra.ExtraStyle.LineStyle.ColorStyle;
import locus.api.objects.extra.ExtraStyle.LineStyle.Units;
import locus.api.objects.extra.Location;
import locus.api.objects.extra.Track;
import locus.api.objects.extra.Waypoint;
import menion.android.whereyougo.Main;
import menion.android.whereyougo.gui.Details;
import menion.android.whereyougo.settings.SettingValues;
import menion.android.whereyougo.settings.Settings;
import android.graphics.Color;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.EventTable;
import cz.matejcik.openwig.Zone;
import cz.matejcik.openwig.formats.CartridgeFile;

public class LocusMapDataProvider implements MapDataProvider {
  private static LocusMapDataProvider instance = null;
  private ArrayList<Track> tracks = null;
  private PackWaypoints pack;

  private LocusMapDataProvider() {
    tracks = new ArrayList<Track>();
    pack = new PackWaypoints("WhereYouGo");
  }

  public static LocusMapDataProvider getInstance() {
    if (instance == null)
      instance = new LocusMapDataProvider();
    return instance;
  }

  public ArrayList<Track> getTracks() {
    return tracks;
  }

  public PackWaypoints getPoints() {
    return pack;
  }

  public void clear() {
    tracks.clear();
    pack.reset();
  }

  public void addCartridges(Vector<CartridgeFile> cartridges) {
    if (cartridges == null)
      return;
    // Bitmap b = Images.getImageB(R.drawable.wherigo, (int) Utils.getDpPixels(32.0f));
    // pack.setBitmap(b);
    for (CartridgeFile cartridge : cartridges) {
      // do not show waypoints that are "Play anywhere" (with zero
      // coordinates)
      if (cartridge.latitude % 360.0 == 0 && cartridge.longitude % 360.0 == 0) {
        continue;
      }

      // construct waypoint
      Location loc = new Location("WhereYouGo");
      loc.setLatitude(cartridge.latitude);
      loc.setLongitude(cartridge.longitude);
      Waypoint wpt = new Waypoint(cartridge.name, loc);
      wpt.addParameter(ExtraData.PAR_DESCRIPTION, cartridge.description);
      wpt.addUrl(cartridge.url);
      pack.addWaypoint(wpt);
    }
  }

  public void addAll() {
    Vector<CartridgeFile> v = new Vector<CartridgeFile>();
    v.add(Main.cartridgeFile);
    addCartridges(v);
    addZones((Vector<Zone>) Engine.instance.cartridge.zones, Details.et);
    if (Details.et != null && !(Details.et instanceof Zone))
      addOther(Details.et, true);
  }

  public void addZones(Vector<Zone> zones) {
    addZones(zones, null);
  }

  public void addZones(Vector<Zone> zones, EventTable mark) {
    if (zones == null)
      return;
    // show zones
    for (Zone z : zones) {
      addZone(z, z == mark);
    }
  }

  public void addZone(Zone z, boolean mark) {
    if (z == null || !z.isLocated() || !z.isVisible())
      return;

    ArrayList<Location> locs = new ArrayList<Location>();
    for (int i = 0; i < z.points.length; i++) {
      Location loc = new Location("");
      loc.setLatitude(z.points[i].latitude);
      loc.setLongitude(z.points[i].longitude);
      locs.add(loc);
    }
    if (locs.size() >= 3)
      locs.add(locs.get(0));

    Track track = new Track();
    ExtraStyle style = new ExtraStyle();
    style.setLineStyle(ColorStyle.SIMPLE, Color.MAGENTA, 2.0f, Units.PIXELS);
    track.styleNormal = style;
    track.setPoints(locs);
    track.setName(z.name);

    tracks.add(track);
  }

  public void addOther(EventTable et, boolean mark) {
    if (et == null || !et.isLocated() || !et.isVisible())
      return;

    Location loc = new Location("");
    if (et instanceof Zone
        && SettingValues.GUIDING_ZONE_NAVIGATION_POINT == Settings.VALUE_GUIDING_ZONE_POINT_NEAREST) {
      Zone z = ((Zone) et);
      loc.setLatitude(z.nearestPoint.latitude);
      loc.setLongitude(z.nearestPoint.longitude);
    } else {
      loc.setLatitude(et.position.latitude);
      loc.setLongitude(et.position.longitude);
    }
    pack.addWaypoint(new Waypoint(et.name, loc));
  }

  public static Waypoint locusMapWaypoint(EventTable et) {
    if (et == null || !et.isLocated() || !et.isVisible())
      return null;

    Location loc = new Location();
    if (et instanceof Zone) {
      Zone z = ((Zone) et);
      loc.setLatitude(z.nearestPoint.latitude);
      loc.setLongitude(z.nearestPoint.longitude);
    } else {
      loc.setLatitude(et.position.latitude);
      loc.setLongitude(et.position.longitude);
    }
    return new Waypoint(et.name, loc);
  }
}
