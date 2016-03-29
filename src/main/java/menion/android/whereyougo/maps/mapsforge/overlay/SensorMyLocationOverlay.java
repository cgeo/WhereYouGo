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

import android.content.Context;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;
import android.view.WindowManager;

import org.mapsforge.android.maps.MapView;

public class SensorMyLocationOverlay extends MyLocationOverlay implements SensorEventListener {

    private static final int UPDATE_INTERVAL = 100; // ms
    private static final float UPDATE_AZIMUTH = 5f;

    final SensorManager sensorManager;
    final WindowManager windowManager;
    final RotationMarker marker;
    final MapView mapView;
    float currentCompassAzimuth;
    float lastCompassAzimuth;
    float lastGPSAzimuth;
    long lastCompassTimestamp;
    long lastGPSTimestamp;
    float filter = 0.15f;

    /**
     * Constructs a new {@code MyLocationOverlay} with the given drawable and the default circle
     * paints.
     *
     * @param context  a reference to the application context.
     * @param mapView  the {@code MapView} on which the location will be displayed.
     * @param drawable a drawable to display at the current location (might be null).
     */
    public SensorMyLocationOverlay(Context context, MapView mapView, RotationMarker marker) {
        super(context, mapView, marker);
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.marker = marker;
        this.mapView = mapView;
    }

    /**
     * Constructs a new {@code MyLocationOverlay} with the given drawable and circle paints.
     *
     * @param context      a reference to the application context.
     * @param mapView      the {@code MapView} on which the location will be displayed.
     * @param drawable     a drawable to display at the current location (might be null).
     * @param circleFill   the {@code Paint} used to fill the circle that represents the current
     *                     location (might be null).
     * @param circleStroke the {@code Paint} used to stroke the circle that represents the current
     *                     location (might be null).
     */
    public SensorMyLocationOverlay(Context context, MapView mapView, RotationMarker marker,
                                   Paint circleFill, Paint circleStroke) {
        super(context, mapView, marker, circleFill, circleStroke);
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.marker = marker;
        this.mapView = mapView;
    }

  /*
   * @Override public synchronized void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas)
   * { if (!isMyLocationEnabled()) { return; }
   * 
   * super.draw(boundingBox, zoomLevel, canvas); double canvasPixelLeft =
   * MercatorProjection.longitudeToPixelX( boundingBox.minLongitude, zoomLevel); double
   * canvasPixelTop = MercatorProjection.latitudeToPixelY( boundingBox.maxLatitude, zoomLevel);
   * Point canvasPosition = new Point(canvasPixelLeft, canvasPixelTop);
   * this.marker.draw(boundingBox, zoomLevel, canvas, canvasPosition); }
   */

    /**
     * Stops the receiving of location updates. Has no effect if location updates are already
     * disabled.
     */
    public synchronized void disableMyLocation() {
        if (isMyLocationEnabled()) {
            setSensor(false);
        }
        super.disableMyLocation();
    }

    public synchronized boolean enableMyLocation(boolean centerAtFirstFix) {
        if (super.enableMyLocation(centerAtFirstFix)) {
            setSensor(true);
            return true;
        }
        return false;
    }

    private float filterValue(float current, float last) {
        if (current < last - 180.0f) {
            last -= 360.0f;
        } else if (current > last + 180.0f) {
            last += 360.0f;
        }
        return filter * current + (1 - filter) * last;
    }

    protected int getRotationOffset() {
        switch (windowManager.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            default:
                return 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        super.onProviderDisabled(provider);
        if (!super.isMyLocationEnabled()) {
            setSensor(false);
        }
    }

  /*
   * @Override public void onLocationChanged(Location location) { boolean redraw = false;
   * synchronized(this){ if(location.getBearing() != 0.0f){ long timestamp = location.getTime();
   * float azimuth = (location.getBearing() + getRotationOffset() + 360) % 360; azimuth =
   * filterValue(azimuth, this.lastGPSTimestamp); this.marker.setRotation(azimuth);
   * if(Math.abs(timestamp-this.lastGPSTimestamp) >= UPDATE_INTERVAL &&
   * Math.abs(azimuth-this.lastGPSAzimuth) >= UPDATE_AZIMUTH){ this.lastGPSTimestamp = timestamp;
   * this.lastGPSAzimuth = azimuth; redraw = true; } } } super.onLocationChanged(location);
   * if(redraw) this.mapView.getOverlayController().redrawOverlays(); }
   */

    @Override
    public void onProviderEnabled(String provider) {
        super.onProviderEnabled(provider);
        if (!super.isMyLocationEnabled()) {
            setSensor(false);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            boolean redraw = false;
            synchronized (this) {
                long timestamp = event.timestamp / 1000000;
                float azimuth = (event.values[0] + getRotationOffset() + 360) % 360;
                azimuth = filterValue(azimuth, this.currentCompassAzimuth);
                this.currentCompassAzimuth = azimuth;
                this.marker.setRotation(azimuth);
                if (Math.abs(timestamp - this.lastCompassTimestamp) >= UPDATE_INTERVAL
                        && Math.abs(azimuth - this.lastCompassAzimuth) >= UPDATE_AZIMUTH) {
                    this.lastCompassTimestamp = timestamp;
                    this.lastCompassAzimuth = azimuth;
                    redraw = true;
                }
            }
            if (redraw)
                this.mapView.getOverlayController().redrawOverlays();
        }
    }

    private void setSensor(boolean state) {
        if (state) {
            setSensor(false);
            this.sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        } else {
            this.sensorManager.unregisterListener(this);
        }
    }

}
