package menion.android.whereyougo.maps;

import java.util.ArrayList;
import java.util.Vector;

import menion.android.whereyougo.Main;
import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.Details;
import menion.android.whereyougo.settings.SettingValues;
import menion.android.whereyougo.settings.Settings;
import menion.android.whereyougo.utils.Images;

import org.mapsforge.applications.android.advancedmapviewer.container.MapPoint;
import org.mapsforge.applications.android.advancedmapviewer.container.MapPointPack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.EventTable;
import cz.matejcik.openwig.Zone;
import cz.matejcik.openwig.formats.CartridgeFile;

public class VectorMapDataProvider implements MapDataProvider {
  private static VectorMapDataProvider instance = null;
  private ArrayList<MapPointPack> items = null;

  private VectorMapDataProvider() {
    items = new ArrayList<MapPointPack>();
  }

  public static VectorMapDataProvider getInstance() {
    if (instance == null)
      instance = new VectorMapDataProvider();
    return instance;
  }

  public ArrayList<MapPointPack> getItems() {
    return items;
  }

  public void clear() {
    items.clear();
  }

  public void addCartridges(Vector<CartridgeFile> cartridges) {
    if (cartridges == null)
      return;
    MapPointPack pack = new MapPointPack(false, R.drawable.marker_wherigo);
    for (CartridgeFile cartridge : cartridges) {
      // do not show waypoints that are "Play anywhere" (with zero
      // coordinates)
      if (cartridge.latitude % 360.0 == 0 && cartridge.longitude % 360.0 == 0) {
        continue;
      }
      MapPoint pt = new MapPoint(cartridge.name, cartridge.latitude, cartridge.longitude);

      try {
        byte[] iconData = cartridge.getFile(cartridge.iconId);
        Bitmap icon = BitmapFactory.decodeByteArray(iconData, 0, iconData.length);
        MapPointPack iconPack = new MapPointPack(false, icon);
        iconPack.getPoints().add(pt);
        items.add(iconPack);
      } catch (Exception e) {
        pack.getPoints().add(pt);
      }
    }

    items.add(pack);
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

    MapPointPack border = new MapPointPack();
    border.setPolygon(true);
    for (int i = 0; i < z.points.length; i++) {
      border.getPoints().add(new MapPoint("", z.points[i].latitude, z.points[i].longitude));
    }
    if (border.getPoints().size() >= 3)
      border.getPoints().add(border.getPoints().get(0));
    items.add(border);

    MapPointPack pack = new MapPointPack();
    if (SettingValues.GUIDING_ZONE_NAVIGATION_POINT == Settings.VALUE_GUIDING_ZONE_POINT_NEAREST) {
      pack.getPoints().add(
          new MapPoint(z.name, z.nearestPoint.latitude, z.nearestPoint.longitude, mark));
    } else {
      pack.getPoints().add(new MapPoint(z.name, z.position.latitude, z.position.longitude, mark));
    }
    if (mark)
      pack.setResource(R.drawable.marker_green);
    else
      pack.setResource(R.drawable.marker_red);
    items.add(pack);
  }

  public void addOther(EventTable et, boolean mark) {
    if (et == null || !et.isLocated() || !et.isVisible())
      return;

    MapPointPack pack = new MapPointPack();
    pack.getPoints().add(new MapPoint(et.name, et.position.latitude, et.position.longitude, mark));
    if (mark)
      pack.setResource(R.drawable.marker_green);
    else
      pack.setResource(R.drawable.marker_red);
    items.add(pack);
  }
}
