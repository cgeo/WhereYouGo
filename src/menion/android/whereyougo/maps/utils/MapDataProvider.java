package menion.android.whereyougo.maps.utils;

import java.util.Vector;

import cz.matejcik.openwig.EventTable;
import cz.matejcik.openwig.Zone;
import cz.matejcik.openwig.formats.CartridgeFile;

public interface MapDataProvider {

  void addAll();

  void addCartridges(Vector<CartridgeFile> cartridges);

  void addOther(EventTable et, boolean mark);

  void addZone(Zone z, boolean mark);

  void addZones(Vector<Zone> zones);

  void addZones(Vector<Zone> zones, EventTable mark);

  void clear();
}
