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

package menion.android.whereyougo.gui.location;

import java.util.ArrayList;

import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.extension.CustomActivity;
import menion.android.whereyougo.hardware.location.LocationEventListener;
import menion.android.whereyougo.hardware.location.LocationState;
import menion.android.whereyougo.hardware.location.SatellitePosition;
import menion.android.whereyougo.hardware.sensors.Orientation;
import menion.android.whereyougo.settings.SettingValues;
import menion.android.whereyougo.settings.Settings;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.ManagerNotify;
import menion.android.whereyougo.utils.Utils;
import menion.android.whereyougo.utils.UtilsFormat;
import menion.android.whereyougo.utils.geometry.Point2D;
import android.location.Location;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * @author menion
 * @since 25.1.2010 2010
 */
public class SatelliteScreen extends CustomActivity implements LocationEventListener {

	private static final String TAG = "SatelliteScreen";
	
	private Satellite2DView satelliteView;
	
	private ToggleButton buttonGps;
	
	protected static ArrayList<SatellitePosition> satellites = new ArrayList<SatellitePosition>();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.satellite_screen_activity);
        
        createLayout();
    }
    
    private void createLayout() {
        LinearLayout llSkyplot = (LinearLayout) findViewById(R.id.linear_layout_skyplot);
        llSkyplot.removeAllViews();
        
        // return and add view to first linearLayout
        satelliteView = new Satellite2DView(SatelliteScreen.this);
        llSkyplot.addView(satelliteView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        // and final bottom buttons
        buttonGps = (ToggleButton) findViewById(R.id.btn_gps_on_off);
        buttonGps.setChecked(LocationState.isActuallyHardwareGpsOn());
        buttonGps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked) {
					LocationState.setGpsOff(SatelliteScreen.this);
					
					// disable satellites on screen
					satellites.clear();
					satelliteView.invalidate();
				} else {
					LocationState.setGpsOn(SatelliteScreen.this);
				}
				
				onGpsStatusChanged(0, null);
				Settings.enableWakeLock();
			}
		});
        
        ToggleButton buttonCompass = (ToggleButton) findViewById(R.id.btn_compass_on_off);
        buttonCompass.setChecked(Settings.getPrefBoolean(this, Settings.KEY_B_HARDWARE_COMPASS_SENSOR,
        		Settings.DEFAULT_HARDWARE_COMPASS_SENSOR));
        buttonCompass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ManagerNotify.toastLongMessage(R.string.pref_sensors_compass_hardware_desc);
				Settings.setPrefBoolean(SatelliteScreen.this, Settings.KEY_B_HARDWARE_COMPASS_SENSOR, isChecked);
				SettingValues.SENSOR_HARDWARE_COMPASS = Utils.parseBoolean(isChecked);
				A.getRotator().manageSensors();
			}
		});
    }
    
    public void notifyGpsDisable() {
   		buttonGps.setChecked(false);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    	onLocationChanged(LocationState.getLocation());
    	onGpsStatusChanged(0, null);
    }

    public void onStart() {
    	super.onStart();
    	LocationState.addLocationChangeListener(this);
    }
    
    public void onStop() {
    	super.onStop();
    	LocationState.removeLocationChangeListener(this);
    }

	public void onLocationChanged(final Location location) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((TextView) findViewById(R.id.text_view_latitude))
						.setText(UtilsFormat.formatLatitude(location.getLatitude()));
				((TextView) findViewById(R.id.text_view_longitude))
						.setText(UtilsFormat.formatLongitude(location.getLongitude()));
				((TextView) findViewById(R.id.text_view_altitude))
						.setText(UtilsFormat.formatAltitude(location.getAltitude(), true));
				((TextView) findViewById(R.id.text_view_accuracy))
						.setText(UtilsFormat.formatDistance(location.getAccuracy(), false));
				((TextView) findViewById(R.id.text_view_speed))
						.setText(UtilsFormat.formatSpeed(location.getSpeed(), false));
				((TextView) findViewById(R.id.text_view_declination))
						.setText(UtilsFormat.formatAngle(Orientation.getDeclination()));
				long lastFix = LocationState.getLastFixTime();
				if (lastFix > 0) {
					((TextView) findViewById(R.id.text_view_time_gps)).setText(UtilsFormat.formatDate(lastFix));
				} else {
					((TextView) findViewById(R.id.text_view_time_gps)).setText("~");
				}
			}
		});

				
	}

	private Point2D.Int setSatellites(ArrayList<SatellitePosition> sats) {
		synchronized (satellites) {
			Point2D.Int satCount = new Point2D.Int();
			satellites.clear();
			if (sats != null && satellites != null) {
				for (int i = 0; i < sats.size(); i++) {
					SatellitePosition sat = sats.get(i);
					if (sat.isFixed())
						satCount.x++;
					satCount.y++;
					satellites.add(sat);
				}
			}
			return satCount;
		}
	}
	
	public void onGpsStatusChanged(int event, ArrayList<SatellitePosition> gpsStatus) {
		try {
			Point2D.Int num = setSatellites(gpsStatus);
			satelliteView.invalidate();
			((TextView) findViewById(R.id.text_view_satellites)).
			setText(num.x + " | " + num.y);
		} catch (Exception e) {
			Logger.e(TAG, "onGpsStatusChanged(" + event + ", " + gpsStatus + "), e:" + e.toString());
		}
	}

	public void onStatusChanged(String provider, int state, Bundle extra) {
	}
	
	public int getPriority() {
		return LocationEventListener.PRIORITY_MEDIUM;
	}
	
	@Override
	public boolean isRequired() {
		return false;
	}
	
	@Override
	public String getName() {
		return TAG;
	}
}
