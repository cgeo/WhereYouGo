package menion.android.whereyougo.maps;

import java.util.Vector;

import cz.matejcik.openwig.EventTable;
import cz.matejcik.openwig.Zone;
import cz.matejcik.openwig.formats.CartridgeFile;

public interface MapDataProvider {

  void clear();

  void addCartridges(Vector<CartridgeFile> cartridges);

  void addAll();

  void addZones(Vector<Zone> zones);

  void addZones(Vector<Zone> zones, EventTable mark);

  void addZone(Zone z, boolean mark);

  void addOther(EventTable et, boolean mark);
}
