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

package menion.android.whereyougo.guiding;

import java.util.ArrayList;

import menion.android.whereyougo.geoData.Waypoint;
import menion.android.whereyougo.hardware.location.LocationEventListener;
import menion.android.whereyougo.hardware.location.LocationState;
import menion.android.whereyougo.hardware.location.SatellitePosition;
import menion.android.whereyougo.settings.SettingValues;
import menion.android.whereyougo.utils.Logger;
import android.location.Location;
import android.os.Bundle;

/**
 * @author menion
 * @since 25.1.2010 2010
 */
public class GuidingContent implements LocationEventListener {

	private static String TAG = "NavigationContent";
	
	/** actual navigator */
    private Guide mGuide;
	
	/** name of target */
	private String mTargetName;
	/** azimuth to actual target */
	private float mAzimuthToTarget;
	/** distance to target */
	private float mDistanceToTarget;
	
	
	/** actual array of listeners */
	private ArrayList<GuidingListener> listeners;
	
    public GuidingContent() {
        listeners = new ArrayList<GuidingListener>();
    }
    
    public void addGuidingListener(GuidingListener listener) {
    	this.listeners.add(listener);
    	// actualize data and send event to new listener
    	onLocationChanged(LocationState.getLocation());
    }
    
    public void removeGuidingListener(GuidingListener listener) {
    	this.listeners.remove(listener);
    }
    
    public void guideStart(Waypoint wpt) {
    	guideStart(new WaypointGuide(wpt));
    }

    public void guideStart(Guide guide) {
    	this.mGuide = guide;
    	
        // set location listener
        LocationState.addLocationChangeListener(this);
        // call one onLocationChange, to update actual values imediately
        onLocationChanged(LocationState.getLocation());
//Logger.d(TAG, "X");
    	Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					while (mGuide != null) {
						if (SettingValues.GUIDING_SOUNDS) {
							mGuide.manageDistanceSoundsBeeping(mDistanceToTarget);
						}
						Thread.sleep(100);
					}
				} catch (Exception e) {
					Logger.e(TAG, "guideStart(" + mGuide + ")", e);
				}
			}
		});
    	thread.start();
    	
    	for (GuidingListener list : listeners) {
			list.guideStart();
		}
    }
    
    public void guideStop() {
    	this.mGuide = null;

    	LocationState.removeLocationChangeListener(this);
    	onLocationChanged(LocationState.getLocation());
    	for (GuidingListener list : listeners) {
			list.guideStop();
		}
    }

    public boolean isGuiding() {
    	return getTargetWaypoint() != null;
    }
    
    public Guide getGuide() {
    	return mGuide;
    }

    public Waypoint getTargetWaypoint() {
    	if (mGuide == null)
    		return null;
    	else
    		return mGuide.getActualTarget();
    }
    
	public void onLocationChanged(Location location) {
//Logger.d(TAG, "onLocationChanged(" + location + ")");
		if (mGuide != null && location != null) {
			mGuide.actualizeState(location);
			
			mTargetName = mGuide.getTargetName();
			mAzimuthToTarget = mGuide.getAzimuthToTaget();
			mDistanceToTarget = mGuide.getDistanceToTarget();
		} else {
			mTargetName = null;
			mAzimuthToTarget = 0.0f;
			mDistanceToTarget = 0.0f;
		}
		
		for (GuidingListener list : listeners) {
			list.receiveGuideEvent(mGuide, mTargetName, mAzimuthToTarget, mDistanceToTarget);
		}
	}
	
	public void onGpsStatusChanged(int event, ArrayList<SatellitePosition> sats) {
	}

	public void onStatusChanged(String provider, int state, Bundle extra) {
	}
	
	public int getPriority() {
		return LocationEventListener.PRIORITY_HIGH;
	}
	
	@Override
	public boolean isRequired() {
		return SettingValues.GUIDING_GPS_REQUIRED;
	}
	
	@Override
	public String getName() {
		return TAG;
	}
	
	protected void trackGuideCallRecalculate() {
		for (GuidingListener list : listeners) {
			list.trackGuideCallRecalculate();
		}
	}
}
