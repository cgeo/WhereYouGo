/*
  * This file is part of WhereYouGo.
  *
  * WhereYouGo is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * WhereYouGo is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with WhereYouGo.  If not, see <http://www.gnu.org/licenses/>.
  *
  * Copyright (C) 2012 Menion <whereyougo@asamm.cz>
  */ 

package menion.android.whereyougo.hardware.sensors;

import java.util.ArrayList;
import java.util.Vector;

import menion.android.whereyougo.hardware.location.LocationEventListener;
import menion.android.whereyougo.hardware.location.LocationState;
import menion.android.whereyougo.hardware.location.SatellitePosition;
import menion.android.whereyougo.settings.SettingValues;
import menion.android.whereyougo.settings.Settings;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Const;
import menion.android.whereyougo.utils.Logger;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;

/**
 * @author menion
 * @since 25.1.2010 2010
 */
public class Orientation implements SensorEventListener, LocationEventListener {

	private static final String TAG = "Orientation";
	
	private Vector<OrientationListener> listeners;

	private SensorManager sensorManager;

	private float mLastAziGps;
	private float mLastAziSensor;
	
	private float mLastPitch;
	private float mLastRoll;
	
	private static float orient;
	private static float pitch;
	private static float roll;

	private static float aboveOrBelow = 0.0f;
	
	public Orientation() {
		this.listeners = new Vector<OrientationListener>();
	}
	
	public void addListener(OrientationListener listener) {
		if (!listeners.contains(listener)) {
			this.listeners.add(listener);
Logger.i(TAG, "addListener(" + listener + "), listeners.size():" + listeners.size());
			manageSensors();
		}
	}
	
	public void removeListener(OrientationListener listener) {
		if (listeners.contains(listener)) {
			this.listeners.remove(listener);
Logger.i(TAG, "removeListener(" + listener + "), listeners.size():" + listeners.size());
			manageSensors();
		}
	}
	
	public void removeAllListeners() {
		listeners.clear();
		manageSensors();
	}
	
	public void manageSensors() {
		// stop sensor manager
		if (sensorManager != null) {
			mLastAziGps = 0.0f;
			mLastAziSensor = 0.0f;
			sendOrientation(0.0f, 0.0f);
			sensorManager.unregisterListener(this);
			sensorManager = null;
			// remove location listener
			LocationState.removeLocationChangeListener(this);
		}
		
		// start new manager
		if (listeners.size() > 0) {
			if (sensorManager == null) {
				sensorManager = (SensorManager) A.getMain().getSystemService(Context.SENSOR_SERVICE);
				sensorManager.registerListener(this,
						sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
						SensorManager.SENSOR_DELAY_GAME);
				sensorManager.registerListener(this,
						sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
						SensorManager.SENSOR_DELAY_GAME);
			}

			// get azimuth from GPS when enabled in settings or by auto-change
			if (!SettingValues.SENSOR_HARDWARE_COMPASS ||
					SettingValues.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE) {
				// register location listener
				LocationState.addLocationChangeListener(this);
				// set zero bearing, if previously was seted by sensor
				mLastAziGps = 0.0f;
			}
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	
	public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                break;
            case Sensor.TYPE_ACCELEROMETER:
            	float filter = getFilter();
            	aboveOrBelow =
                    (float) ((event.values[SensorManager.DATA_Z] * filter) + 
                    (aboveOrBelow * (1.0 - filter)));
                break;
            case Sensor.TYPE_ORIENTATION:
            	float valueOr = event.values[SensorManager.DATA_X];
//Logger.d(TAG, "sensorOrientation:" + valueOr + ", " + event.values[SensorManager.DATA_Y] + ", " + event.values[SensorManager.DATA_Z] + ", " + getDeclination());
            	// fix to true bearing
            	if (SettingValues.SENSOR_BEARING_TRUE) {
            		valueOr += getDeclination();
            	}
            	orient = filterValue(valueOr, orient);
            	pitch = filterValue(event.values[SensorManager.DATA_Y], pitch);
            	
            	roll = filterValue(event.values[SensorManager.DATA_Z], roll);
                float rollDef;
                if (aboveOrBelow < 0) {
                	if (roll < 0) {
                		rollDef = -180 - roll;
                	} else {
                		rollDef = 180 - roll;
                	}
                } else {
                	rollDef = roll;
                }
                this.mLastAziSensor = orient;
                // do some orientation change by settings
                if (Const.SCREEN_WIDTH > Const.SCREEN_HEIGHT)
                	mLastAziSensor += SettingValues.SENSOR_ORIENT_MODIF_LANDSCAPE;
                else
                	mLastAziSensor += SettingValues.SENSOR_ORIENT_MODIF_PORTRAIT;
                sendOrientation(pitch, rollDef);
                break;
        }
	}

	private void sendOrientation(float pitch, float roll) {
		float usedOrient;
		if (!SettingValues.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE || 
				LocationState.getLocation().getSpeed() < SettingValues.SENSOR_HARDWARE_COMPASS_AUTO_CHANGE_VALUE) {
			if (!SettingValues.SENSOR_HARDWARE_COMPASS)
				usedOrient = mLastAziGps;
			else
				usedOrient = mLastAziSensor;
		} else {
			usedOrient = mLastAziGps;
		}
		
		this.mLastPitch = pitch;
		this.mLastRoll = roll;
		
    	for (OrientationListener listener : listeners) {
    		listener.onOrientationChanged(usedOrient, mLastPitch, mLastRoll);
    	}
	}
	
	public void onStatusChanged(String provider, int state, Bundle extras) {}

	public void onGpsStatusChanged(int event, ArrayList<SatellitePosition> sats) {}

	public int getPriority() {
		return LocationEventListener.PRIORITY_MEDIUM;
	}

	public void onLocationChanged(Location location) {
//Logger.d(TAG, "onLocationChanged(), bear:" + location.hasBearing() + ", " + location.getBearing());
		if (location.getBearing() != 0.0f) {
			this.mLastAziGps = location.getBearing();
			sendOrientation(mLastPitch, mLastRoll);
		}
	}
	
	@Override
	public boolean isRequired() {
		return false;
	}
	
	@Override
	public String getName() {
		return TAG;
	}
	
	private float filterValue(float valueActual, float valueLast) {
		if (valueActual < valueLast - 180.0f) {
			valueLast -= 360.0f;
		} else if (valueActual > valueLast + 180.0f) {
			valueLast += 360.0f;
		}
		
//Logger.d(TAG, "filterValue(" + valueActual + ", " + valueLast + ")");
		float filter = getFilter();
		return (float) ((valueActual * filter) + 
                (valueLast * (1.0 - filter)));
	}
	
	private float getFilter() {
		switch (SettingValues.SENSOR_ORIENT_FILTER) {
		case Settings.VALUE_SENSORS_ORIENT_FILTER_LIGHT:
			return 0.20f;
		case Settings.VALUE_SENSORS_ORIENT_FILTER_MEDIUM:
			return 0.06f;
		case Settings.VALUE_SENSORS_ORIENT_FILTER_HEAVY:
			return 0.03f;
		}
		return 1.0f;
	}
	
	private static GeomagneticField gmf;
	private static long lastCompute;
	public static float getDeclination() {
		long actualTime = System.currentTimeMillis();
		if (gmf == null || actualTime - lastCompute > 300000) { // once per five minutes
			
			Location loc = LocationState.getLocation();
			// compute this only if needed
			gmf = new GeomagneticField(
					(float) loc.getLatitude(),
					(float) loc.getLongitude(),
					(float) loc.getAltitude(),
					actualTime);
			lastCompute = actualTime;
Logger.w(TAG, "getDeclination() - dec:" + gmf.getDeclination());
		}
		
		return gmf.getDeclination();
	}
}
