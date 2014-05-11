package menion.android.whereyougo.maps.mapsforge.overlay;

/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.MercatorProjection;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * A {@code Marker} draws a {@link Drawable} at a given geographical position.
 */
public class RotationMarker extends Marker {
  private static boolean intersect(Canvas canvas, float left, float top, float right, float bottom) {
    return right >= 0 && left <= canvas.getWidth() && bottom >= 0 && top <= canvas.getHeight();
  }

  float rotation;

  /**
   * @param geoPoint the initial geographical coordinates of this marker (may be null).
   * @param drawable the initial {@code Drawable} of this marker (may be null).
   */
  public RotationMarker(GeoPoint geoPoint, Drawable drawable) {
    super(geoPoint, drawable);
  }

  @Override
  public synchronized boolean draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas,
      Point canvasPosition) {
    GeoPoint geoPoint = this.getGeoPoint();
    Drawable drawable = this.getDrawable();
    if (geoPoint == null || drawable == null) {
      return false;
    }

    double latitude = geoPoint.latitude;
    double longitude = geoPoint.longitude;
    int pixelX =
        (int) (MercatorProjection.longitudeToPixelX(longitude, zoomLevel) - canvasPosition.x);
    int pixelY =
        (int) (MercatorProjection.latitudeToPixelY(latitude, zoomLevel) - canvasPosition.y);

    Rect drawableBounds = drawable.copyBounds();
    int left = pixelX + drawableBounds.left;
    int top = pixelY + drawableBounds.top;
    int right = pixelX + drawableBounds.right;
    int bottom = pixelY + drawableBounds.bottom;

    if (!intersect(canvas, left, top, right, bottom)) {
      return false;
    }

    int saveCount = canvas.save();
    canvas.rotate(rotation, (float) pixelX, (float) pixelY);
    drawable.setBounds(left, top, right, bottom);
    drawable.draw(canvas);
    drawable.setBounds(drawableBounds);
    canvas.restoreToCount(saveCount);
    return true;
  }

  public float getRotation() {
    return rotation;
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

}
