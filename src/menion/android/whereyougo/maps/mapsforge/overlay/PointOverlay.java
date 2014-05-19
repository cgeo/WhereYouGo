package menion.android.whereyougo.maps.mapsforge.overlay;

import menion.android.whereyougo.maps.container.MapPoint;

import org.mapsforge.core.model.GeoPoint;

import android.graphics.drawable.Drawable;
import android.location.Location;

/**
 * Created by Michal on 30.11.13.
 */
public class PointOverlay extends LabelMarker {
  int id;
  MapPoint point;

  public PointOverlay(GeoPoint geoPoint, Drawable drawable, MapPoint point) {
    this(geoPoint, drawable, point, -1);
  }

  public PointOverlay(GeoPoint geoPoint, Drawable drawable, MapPoint point, int id) {
    super(geoPoint, drawable, point.getName(), point.getDescription() == null ? "" : point.getDescription());
    this.id = id;
    this.point = point;
  }

  /**
   * @param geoPoint the initial geographical coordinates of this marker (may be null).
   * @param drawable the initial {@code Drawable} of this marker (may be null).
   */
  public PointOverlay(int id, GeoPoint geoPoint, Drawable drawable) {
    super(geoPoint, drawable);
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public MapPoint getPoint() {
    return point;
  }
}
