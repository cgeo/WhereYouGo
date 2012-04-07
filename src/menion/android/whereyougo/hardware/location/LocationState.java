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

package menion.android.whereyougo.hardware.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.extension.CustomDialog;
import menion.android.whereyougo.gui.extension.MainApplication;
import menion.android.whereyougo.gui.location.SatelliteScreen;
import menion.android.whereyougo.settings.SettingValues;
import menion.android.whereyougo.settings.Settings;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Const;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.Utils;
import menion.android.whereyougo.utils.geometry.Point2D;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author menion
 * @since 25.1.2010 2010
 */
public class LocationState {

	private static String TAG = "LocationState";
	
	public static final String SIMULATE_LOCATION = "SIMULATE_LOCATION";
	
	private static final String KEY_B_GPS_ENABLE_ASK_ON_ENABLE = "KEY_B_GPS_ENABLE_ASK_ON_ENABLE";
	
    private static final int GPS_ON = 0;
    private static final int GPS_OFF = 1;

    // gps connection "service"
    private static GpsConnection gpsConn;
    
    // last known location
    private static Location location;
    // actual gps mSource
    private static int mSource = GPS_OFF;
    
    // last GPS fix time
    private static long mLastGpsFixTime = 0L;
    // count of satellites
    private static Point2D.Int mSatsCount = new Point2D.Int();
    // is speed correction enabled
    private static boolean speedCorrection = false;
    
    // location listeners
    private static ArrayList<LocationEventListener> mListeners;
    // last current mSource
    private static int lastSource;

    public static void init(Context c) {
    	if (LocationState.location == null) {
    		LocationState.location = Settings.getLastKnownLocation(c);
    		mListeners = new ArrayList<LocationEventListener>();
    		lastSource = -1;
    	}
    }
    
	public static void setGpsOff(Context context) {
		setLocation(context, GPS_OFF, null, true, null);
	}

	public static void destroy(Context context) {
//Logger.d(TAG, "destroy(" + context + ")");
		setLocation(context, GPS_OFF, location, false, null);
		mListeners.clear();
		gpsConn = null;
		location = null;
	}
    
    public static void setGpsOn(Context context) {
    	if (mSource == GPS_ON)
    		setGpsOff(context);
    	setLocation(context, GPS_ON, null, true, null);
    }
    
    private static void setLocation(final Context context, int source, Location location, boolean writeToSettings, String addData) {
//Logger.w(TAG, "setLocation(" + context + ", " + source + ", " + (location != null ? location.getProvider() : "null") +
//		", " + writeToSettings + ", " + addData + "), actual:" + LocationState.mSource);
    	if (LocationState.mSource == source && 
    			((location == null) || (LocationState.location.getLatitude() == location.getLatitude() &&
    			LocationState.location.getLongitude() == location.getLongitude()))) {
//Logger.w(TAG, "setLocation(), do nothing ...");    		
    		return;
    	}
    		

    	// save to start GPS with new start
    	if (writeToSettings && context != null)
    		Settings.setPrefBoolean(context, Settings.KEY_B_START_GPS_AUTOMATICALLY, source == GPS_ON);
   		
        if (source == GPS_ON && context != null) {
        	LocationState.mSource = GPS_ON;
        	if (gpsConn != null) {
        		gpsConn.destroy();
        		gpsConn = null;
        	}
        	
    		boolean gpsNotEnabled = false;
			// test if GPS allowed
   			String provider = android.provider.Settings.Secure.getString(
   					context.getContentResolver(),
   					android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
//Logger.d(TAG, " - provider allowed:'" + provider + "'");
		    if (provider != null && provider.contains("network") || provider.contains("gps")) {
		    	//activity.startService(new Intent(activity, GpsConnectionService.class));
		    	gpsConn = new GpsConnection(context);
		    } else {
		    	if (Settings.getPrefBoolean(context, KEY_B_GPS_ENABLE_ASK_ON_ENABLE, true)) {
		    		LinearLayout ll = new LinearLayout(context);
		    		ll.setOrientation(LinearLayout.VERTICAL);
		    		
		        	int pad = (int) Utils.getDpPixels(5.0f);
		        	TextView tv = new TextView(context);
		        	tv.setPadding(pad, pad, pad, pad);
		            tv.setText(R.string.gps_not_enabled_show_system_settings);
		            tv.setTextColor(Color.BLACK);
		            ll.addView(tv);
		            
    	    		final CheckBox chb = new CheckBox(context);
    	    		chb.setText(R.string.dont_ask);
    	    		chb.setTextColor(Color.BLACK);
    	    		ll.addView(chb);
    	    		
    	    		new CustomDialog.Builder(context, true).
    	    		setTitle(R.string.question, R.drawable.ic_question_default).
    	    		setTitleExtraCancel().
    				setContentView(ll, false).
    				setPositiveButton(R.string.yes, new CustomDialog.OnClickListener() {
						public boolean onClick(CustomDialog dialog, View v, int which) {
							if (chb.isChecked()) {
								Settings.setPrefBoolean(context, KEY_B_GPS_ENABLE_ASK_ON_ENABLE, false);
							}
							Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							context.startActivity(intent);
							return true;
						}
					}).
    				setNegativeButton(R.string.no, new CustomDialog.OnClickListener() {
						public boolean onClick(CustomDialog dialog, View v, int which) {
							if (chb.isChecked()) {
								Settings.setPrefBoolean(context, KEY_B_GPS_ENABLE_ASK_ON_ENABLE, false);
							}
							return true;
						}
					}).
    	    		show();    		    		
		    	}
	    		gpsNotEnabled = true;
		    }
    		
    		if (gpsNotEnabled) {
    			if (context instanceof SatelliteScreen) {
    				((SatelliteScreen) context).notifyGpsDisable();	
    			}
    			setLocation(context, GPS_OFF, null, true, null);
    		}
        } else {
       		LocationState.mSource = GPS_OFF;
        	onGpsStatusChanged(GpsStatus.GPS_EVENT_STOPPED, null);
   			
       		if (gpsConn != null) {
       			gpsConn.destroy();
       			gpsConn = null;
           	}
        }
        
        // notify about changes
        onLocationChanged(LocationState.location);
    }

	public static void setLocationDirectly(Location location) {
		if (!Const.STATE_RELEASE && !LocationState.isActuallyHardwareGpsOn()) { // XXX
			location.setSpeed(20.0f);
			if (LocationState.location != null)
				location.setBearing(LocationState.location.bearingTo(location));
		}

		onLocationChanged(location);
    }
    
    public static Location getLocation() {
    	if (location == null)
    		return new Location(TAG);
    	return new Location(location);
    }
    
    public static boolean isActuallyHardwareGpsOn() {
    	return mSource == GPS_ON;
    }
    public static boolean isActualLocationHardwareGps() {
    	return mSource == GPS_ON && location.getProvider().equals(LocationManager.GPS_PROVIDER);
    }
    public static boolean isActualLocationHardwareNetwork() {
    	return mSource == GPS_ON && location.getProvider().equals(LocationManager.NETWORK_PROVIDER);
    }
    
    /*
     * Returns the last known location of the device using its GPS and network location providers.
     * May be null if location access is disabled, or if the location providers don't exist.
     */
    public static Location getLastKnownLocation(Activity activity) {
        LocationManager lm 
                = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        Location gpsLocation = null;
        try {
            gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            Logger.w(TAG, "Failed to retrieve location: access appears to be disabled.");
        } catch (IllegalArgumentException e) {
        	Logger.w(TAG, "Failed to retrieve location: device has no GPS provider.");
        }
        
        Location networkLocation = null;
        try {
            networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {
        	Logger.w(TAG, "Failed to retrieve location: access appears to be disabled.");
        } catch (IllegalArgumentException e) {
        	Logger.w(TAG, "Failed to retrieve location: device has no network provider.");
        }
        
        if (gpsLocation != null && networkLocation != null) {
            if (gpsLocation.getTime() > networkLocation.getTime())
            	return gpsLocation;
            else
            	return networkLocation;
        } else if (gpsLocation != null)
        	return gpsLocation;
        else if (networkLocation != null)
        	return networkLocation;
        else
        	return null;
    }
    
    /** Registers a listener for GPS events*/
    public static synchronized void addLocationChangeListener(LocationEventListener listener) {
    	if (listener == null || mListeners == null)
    		return;
    	
    	if (!mListeners.contains(listener)) {
    		mListeners.add(listener);
    		
    		// sort listeners
    		if (mListeners.size() > 0)
    			Collections.sort(mListeners, new Comparator<LocationEventListener>() {
    				public int compare(LocationEventListener object1,
    						LocationEventListener object2) {
    					return object1.getPriority() - object2.getPriority();
    				}
    			});
    		
    		onScreenOn(true);
    	}
    }
    
    /** Unregisters a listener. */
    public static synchronized void removeLocationChangeListener(LocationEventListener listener) {
    	if (mListeners.size() == 0)
    		return;
    	
        if (listener != null && mListeners.contains(listener)) {
        	mListeners.remove(listener);
Logger.i(TAG, "removeLocationChangeListener(" + listener + "), actualSize:" + mListeners.size());
        }
    }
    
    public static void onActivityPauseInstant(Context context) {
		try {
			boolean screenOff = (A.getApp() != null && ((MainApplication) A.getApp()).isScreenOff());
			boolean disableWhenHide = context == null ? false : Settings.getPrefBoolean(context,
					Settings.KEY_B_GPS_DISABLE_WHEN_HIDE, Settings.DEFAULT_GPS_DISABLE_WHEN_HIDE);

			// also disable wake-lock here
			if (!Settings.existCurrentActivity() || screenOff) {
				Settings.disableWakeLock();
			}
			
			// do not change gps state when ...
			if (!disableWhenHide)
				return;
			
			// disable GPS if not needed and hidden
			if (mListeners.size() == 0 && !Settings.existCurrentActivity()) {
				lastSource = mSource;
				setLocation(context, GPS_OFF, null, true, null);
			// disable gps when screen off or no activity visible (widget only)
			} else if (screenOff || !Settings.existCurrentActivity()) {
				if (!isGpsRequired()) {
					lastSource = mSource;
					setLocation(context, GPS_OFF, null, true, null);	
				} else {
					lastSource = -1;
				}
			} else {
				lastSource = -1;
			}
		} catch (Exception e) {
			Logger.e(TAG, "onActivityPauseInstant()", e);
		}
    }
    
    public static boolean isGpsRequired() {
    	if (mListeners == null)
    		return false;
    	
		boolean required = false;
		for (int  i = 0; i < mListeners.size(); i++) {
//Logger.d(TAG, "isGpsRequired() - list:" + mListeners.get(i) + ", req:" + mListeners.get(i).isRequired());
			if (mListeners.get(i).isRequired()) {
				required = true;
				break;
			}
		}
		
		return required;
    }
    
    public static void onScreenOn(boolean force) {
//Logger.i(TAG, "onScreenOn(), activity:" + Settings.getCurrentActivity() + ", exist:" + Settings.existCurrentActivity() + ", " + isGpsRequired());
    	if (lastSource != -1 && mListeners != null && mListeners.size() > 0 &&
   				(Settings.existCurrentActivity() || force)) {
   			setLocation(Settings.getCurrentActivity(), lastSource, null, true, null);
   			lastSource = -1;
   		}
    }
    
    public static long getLastFixTime() {
    	return mLastGpsFixTime;
    }
    
    protected static void onLocationChanged(Location location) {
		try {
			// check if location is valid
			if (LocationState.location == null)
				return;
			
			// if first location from Network, and new from GPS but with worst precision, do not set
			if (LocationState.location.getProvider().equals(LocationManager.NETWORK_PROVIDER) &&
					location.getProvider().equals(LocationManager.GPS_PROVIDER) &&
					(LocationState.location.getAccuracy() * 3) < location.getAccuracy()) {
				return;
			}
			
			// check incorrect speed (if speed bigger then 100, and after 2sec increase more then 50%, set old speed
			if (!speedCorrection && (location.getTime() - LocationState.location.getTime()) < 5000 &&
					location.getSpeed() > 100.0f && location.getSpeed() / LocationState.location.getSpeed() > 2) {
				location.setSpeed(LocationState.location.getSpeed());
				speedCorrection = true;
			} else {
				speedCorrection = false;
			}
			
			// set last gps fix
			if (LocationState.location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
				mLastGpsFixTime = System.currentTimeMillis();//LocationState.location.getTime();
			}

			// check incorrect azimuth changes when almost zero speed
			if (location.getSpeed() < 0.5f) {
				if (Math.abs(location.getBearing() - LocationState.location.getBearing()) > 25.0) {
					location.setBearing(LocationState.location.getBearing());
				}
			}

			if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
				// set altitude correction
				location.setAltitude(location.getAltitude() + SettingValues.GPS_ALTITUDE_CORRECTION);
			}

			// finally set new location
			LocationState.location = location;
			
			for (int i = 0; i < mListeners.size(); i++) {
				LocationEventListener list = mListeners.get(i);
				list.onLocationChanged(location);
			}
		} catch (Exception e) {
			Logger.e(TAG, "onLocationChanged(" + location + ")", e);
		}
	}

    protected static void onProviderDisabled(String provider) {
//Logger.w(TAG, "onProviderDisabled(" + provider + ")");
	}

    protected static void onProviderEnabled(String provider) {
//Logger.w(TAG, "onProviderEnabled(" + provider + ")");
	}

    protected static void onStatusChanged(String provider, int status, Bundle extras) {
//Logger.w(TAG, "onStatusChanged(" + provider + ", " + status + ", " + extras + ")");
        for (int i = 0; i < mListeners.size(); i++) {
        	mListeners.get(i).onStatusChanged(provider, status, extras);
        }
        
		// status 1 - provider disabled
		// status 2 - provider enabled
		// if GPS provider is disabled, set location only as network
		if (provider.equals(LocationManager.GPS_PROVIDER) && status == 1) {
			location.setProvider(LocationManager.NETWORK_PROVIDER);
			onLocationChanged(location);
		}
	}
	
    protected static void onGpsStatusChanged(int event, GpsStatus gpsStatus) {
    	if (mListeners == null || mListeners.size() == 0)
    		return;
    	
		if (event == GpsStatus.GPS_EVENT_STARTED ||
				event == GpsStatus.GPS_EVENT_STOPPED) {
	        for (int i = 0; i < mListeners.size(); i++) {
	        	mListeners.get(i).onStatusChanged(LocationManager.GPS_PROVIDER,
	        			event == GpsStatus.GPS_EVENT_STARTED ? 2 : 1, null);
	        }
		} else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
			ArrayList<SatellitePosition> pos = null;
			if (gpsStatus != null) {
				pos = new ArrayList<SatellitePosition>();
				Iterator<GpsSatellite> enuSat = gpsStatus.getSatellites().iterator();
				// clear sats count
				mSatsCount.x = 0;
				mSatsCount.y = 0;
				while (enuSat.hasNext()) {
					GpsSatellite sat = enuSat.next();
					//pos.add(enuPos.nextElement());
					SatellitePosition satPos = new SatellitePosition();
					satPos.azimuth = sat.getAzimuth();
					satPos.elevation = sat.getElevation();
					satPos.prn = sat.getPrn();
					satPos.snr = (int) sat.getSnr();
					satPos.fixed = sat.usedInFix();
					if (satPos.fixed)
						mSatsCount.x++;
					mSatsCount.y++;
					pos.add(satPos);
				}
			}
			postGpsSatelliteChange(pos);
		}
	}
	
    protected static void onGpsStatusChanged(Hashtable<Integer, SatellitePosition> sats) {
//Logger.w(TAG, "onGpsStatusChanged(" + sats + ")");
		ArrayList<SatellitePosition> pos = null;
		if (sats != null) {
			pos = new ArrayList<SatellitePosition>();
			Enumeration<SatellitePosition> enuPos = sats.elements();
			mSatsCount.x = 0;
			mSatsCount.y = 0;
			while (enuPos.hasMoreElements()) {
				SatellitePosition sat = enuPos.nextElement();
				pos.add(sat);
				if (sat.fixed)
					mSatsCount.x++;
				mSatsCount.y++;
			}
		}
		
		postGpsSatelliteChange(pos);
	}
	
	public static Point2D.Int getSatCount() {
		return mSatsCount;
	}
	
	private static void postGpsSatelliteChange(final ArrayList<SatellitePosition> pos) {
		if (Settings.getCurrentActivity() == null)
			return;
		
		Settings.getCurrentActivity().runOnUiThread(new Runnable() {
			public void run() {
				for (int i = 0; i < mListeners.size(); i++) {
					mListeners.get(i).onGpsStatusChanged(GpsStatus.GPS_EVENT_SATELLITE_STATUS, pos);
				}					
			}
		});
	}
}
