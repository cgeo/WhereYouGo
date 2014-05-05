package org.mapsforge.applications.android.advancedmapviewer.extension;

import java.util.ArrayList;
import java.util.List;

import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.Overlay;
import org.mapsforge.android.maps.overlay.PolygonalChain;
import org.mapsforge.android.maps.overlay.Polyline;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.MercatorProjection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;


public class NavigationOverlay implements Overlay {

  private static Point getPoint(GeoPoint geoPoint, Point canvasPosition, byte zoomLevel) {
    int pixelX =
        (int) (MercatorProjection.longitudeToPixelX(geoPoint.longitude, zoomLevel) - canvasPosition.x);
    int pixelY =
        (int) (MercatorProjection.latitudeToPixelY(geoPoint.latitude, zoomLevel) - canvasPosition.y);
    return new Point(pixelX, pixelY);
  }

  final MyLocationOverlay myLocationOverlay;
  GeoPoint target;

  private Polyline line;

  public NavigationOverlay(MyLocationOverlay myLocationOverlay) {
    this.myLocationOverlay = myLocationOverlay;

    Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    paintStroke.setStyle(Paint.Style.STROKE);
    paintStroke.setColor(Color.RED);
    paintStroke.setStrokeWidth(2);
    line = new Polyline(null, paintStroke);
  }

  @Override
  public int compareTo(Overlay arg0) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public synchronized void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas) {
    // TODO Auto-generated method stub
    if (target == null || !myLocationOverlay.isMyLocationEnabled()
        || myLocationOverlay.getLastLocation() == null)
      return;
    double canvasPixelLeft =
        MercatorProjection.longitudeToPixelX(boundingBox.minLongitude, zoomLevel);
    double canvasPixelTop = MercatorProjection.latitudeToPixelY(boundingBox.maxLatitude, zoomLevel);
    Point canvasPosition = new Point(canvasPixelLeft, canvasPixelTop);

    Location startLocation = myLocationOverlay.getLastLocation();
    GeoPoint start = new GeoPoint(startLocation.getLatitude(), startLocation.getLongitude());

    List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
    geoPoints.add(start);
    geoPoints.add(target);
    line.setPolygonalChain(new PolygonalChain(geoPoints));
    line.draw(boundingBox, zoomLevel, canvas, canvasPosition);

    /*
     * double canvasPixelLeft = MercatorProjection.longitudeToPixelX( boundingBox.minLongitude,
     * zoomLevel); double canvasPixelTop = MercatorProjection.latitudeToPixelY(
     * boundingBox.maxLatitude, zoomLevel); Point canvasPosition = new Point(canvasPixelLeft,
     * canvasPixelTop); Point a = getPoint(start, canvasPosition, zoomLevel); Point b =
     * getPoint(target, canvasPosition, zoomLevel); canvas.drawLine(a.x, a.y, b.x, b.y, paint);
     */
  }

  public synchronized GeoPoint getTarget() {
    return target;
  }

  public synchronized boolean checkItemHit(GeoPoint geoPoint, MapView mapView) {
    return false;
  }

  public void onTap(GeoPoint p) {}

  public synchronized void setTarget(GeoPoint target) {
    this.target = target;
  }

}
