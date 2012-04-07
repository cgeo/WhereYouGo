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

import java.util.Timer;
import java.util.TimerTask;

import menion.android.whereyougo.R;
import menion.android.whereyougo.geoData.Waypoint;
import menion.android.whereyougo.hardware.location.LocationState;
import menion.android.whereyougo.settings.SettingValues;
import menion.android.whereyougo.settings.Settings;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Logger;
import menion.android.whereyougo.utils.audio.AudioClip;
import android.location.Location;
import android.net.Uri;

/**
 * @author menion
 * @since 25.1.2010 2010
 */
public class WaypointGuide implements Guide {

	private static final String TAG = "WaypointGuide";
	
	private Waypoint wpt;
    private int id;

    private float azimuth;
    private float distance;
    
	/** last sound sonar call */
	private long lastSonarCall;
	/** audio sound for beeping */
	private AudioClip audioBeep;

	private boolean mAlreadyBeeped;
	
    /**
     * Creates new waypoint navigator
     * @param target
     * @param name
     */
    public WaypointGuide(Waypoint wpt) {
    	this.wpt = wpt;
    	mAlreadyBeeped = false;
    	lastSonarCall = 0;
        audioBeep = new AudioClip(A.getApp(), R.raw.sound_beep_01);
    }

	@Override
	public Waypoint getActualTarget() {
		return wpt;
	}

	@Override
    public String getTargetName() {
        return wpt.getName();
    }

	@Override
    public float getAzimuthToTaget() {
        return azimuth;
    }
    
	@Override
    public float getDistanceToTarget() {
        return distance;
    }
    
	@Override
	public long getTimeToTarget() {
       	if (LocationState.getLocation().getSpeed() > 1.0) {
       		return (long) ((getDistanceToTarget() / LocationState.getLocation().getSpeed()) * 1000);
       	} else {
       		return 0;
       	}
	}

    public int getId() {
        return id;
    }

	public void actualizeState(Location actualLocation) {
		azimuth = actualLocation.bearingTo(wpt.getLocation());
		distance = actualLocation.distanceTo(wpt.getLocation());
	}

	@Override
	public void manageDistanceSoundsBeeping(double distance) {
		try {
			switch (SettingValues.GUIDING_WAYPOINT_SOUND) {
			case Settings.VALUE_GUIDING_WAYPOINT_SOUND_BEEP_ON_DISTANCE:
				if (distance < SettingValues.GUIDING_WAYPOINT_SOUND_DISTANCE &&
						!mAlreadyBeeped) {
					audioBeep.play();
					mAlreadyBeeped = true;
				}
				break;
			case Settings.VALUE_GUIDING_WAYPOINT_SOUND_INCREASE_CLOSER:
				long currentTime = System.currentTimeMillis();
				float sonarTimeout = getSonarTimeout(distance);
				if ((currentTime - lastSonarCall) > sonarTimeout) { // (currentTime - lastSonarCall) > soundSonarDuration && 
					lastSonarCall = currentTime;
					audioBeep.play();
				}
				break;
			case Settings.VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND:
				if (distance < SettingValues.GUIDING_WAYPOINT_SOUND_DISTANCE &&
						!mAlreadyBeeped) {
					String uri = Settings.getPrefString(Settings.VALUE_GUIDING_WAYPOINT_SOUND_CUSTOM_SOUND_URI, "");
					if (uri.length() > 0) {
						final AudioClip audioClip = new AudioClip(A.getApp(), Uri.parse(uri));
						audioClip.play();
						new Timer().schedule(new TimerTask() {
							@Override
							public void run() {
								AudioClip.destroyAudio(audioClip);
							}
						}, 5000);
					}
					mAlreadyBeeped = true;
				}
				break;
			}
		} catch (Exception e) {
			Logger.e(TAG, "manageDistanceSounds(" + distance + "), e:" + e.toString());
		}
	}
	
	private long getSonarTimeout(double distance) {
		if (distance < SettingValues.GUIDING_WAYPOINT_SOUND_DISTANCE) {
			return (long) (distance * 1000 / 33);
		} else {
			return Long.MAX_VALUE;
		}
	}
}
