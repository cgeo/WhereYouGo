package menion.android.whereyougo.maps.mapsforge.model;

import org.mapsforge.core.model.GeoPoint;

/**
 * Created by Michal on 30.11.13.
 */
public class MyGeoPoint extends GeoPoint {
  int id;

  public MyGeoPoint(double latitude, double longitude, int id) {
    super(latitude, longitude);
    this.id = id;
  }

  public int getId() {
    return id;
  }
}
