/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2013, 2014 biylda <biylda@gmail.com>
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

package menion.android.whereyougo.maps.mapsforge.overlay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;

import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.Overlay;
import org.mapsforge.android.maps.overlay.PolygonalChain;
import org.mapsforge.android.maps.overlay.Polyline;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.MercatorProjection;

import java.util.ArrayList;
import java.util.List;


public class NavigationOverlay implements Overlay {

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

    private static Point getPoint(GeoPoint geoPoint, Point canvasPosition, byte zoomLevel) {
        int pixelX =
                (int) (MercatorProjection.longitudeToPixelX(geoPoint.longitude, zoomLevel) - canvasPosition.x);
        int pixelY =
                (int) (MercatorProjection.latitudeToPixelY(geoPoint.latitude, zoomLevel) - canvasPosition.y);
        return new Point(pixelX, pixelY);
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

    public synchronized void setTarget(GeoPoint target) {
        this.target = target;
    }

    public synchronized boolean checkItemHit(GeoPoint geoPoint, MapView mapView) {
        return false;
    }

    public void onTap(GeoPoint p) {
    }

}
