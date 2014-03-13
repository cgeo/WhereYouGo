package org.mapsforge.applications.android.advancedmapviewer.extension;

import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.MercatorProjection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * A {@code Marker} draws a {@link Drawable} at a given geographical position.
 */
public class CaptionMarker extends Marker {
  static Paint labelPaint;
  static Paint labelBgPaint;

  static {
    labelPaint = new Paint();
    labelPaint.setStyle(android.graphics.Paint.Style.STROKE);
    // labelPaint.setTextAlign(Align.CENTER);
    labelBgPaint = new Paint();
    labelBgPaint.setColor(Color.argb(192, 255, 255, 255));
    labelBgPaint.setStyle(android.graphics.Paint.Style.FILL);
    // labelBgPaint.setTextAlign(Align.CENTER);
  }

  String caption;

  /**
   * @param geoPoint the initial geographical coordinates of this marker (may be null).
   * @param drawable the initial {@code Drawable} of this marker (may be null).
   * @param caption the initial caption of this marker (may be null).
   */
  public CaptionMarker(GeoPoint geoPoint, Drawable drawable, String caption) {
    super(geoPoint, drawable);
    this.caption = caption;
  }

  @Override
  public synchronized boolean draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas,
      Point canvasPosition) {
    if (!super.draw(boundingBox, zoomLevel, canvas, canvasPosition))
      return false;
    if (caption == null)
      return true;

    GeoPoint geoPoint = getGeoPoint();
    Drawable drawable = getDrawable();

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

    Rect text = new Rect();
    labelPaint.getTextBounds(caption, 0, caption.length(), text);
    int x = (left + right) / 2 - text.width() / 2;
    int y = bottom;
    int margin = 2;
    Rect r =
        new Rect(x - margin, y - margin, x + text.width() + margin, y + text.height() + margin);
    canvas.drawRect(r, labelBgPaint);
    canvas.drawRect(r, labelPaint);
    canvas.drawText(caption, x, y + text.height(), labelPaint);

    return true;
  }

  /**
   * @return the caption of this marker (may be null).
   */
  public synchronized String getCaption() {
    return this.caption;
  }

  /**
   * @param caption the new caption of this marker (may be null).
   */
  public synchronized void setCaption(String caption) {
    this.caption = caption;
  }

}
