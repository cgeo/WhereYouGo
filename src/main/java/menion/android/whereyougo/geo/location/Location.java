/*
 * Copyright 2012, Asamm Software, s. r. o.
 * 
 * This file is part of LocusAPI.
 * 
 * LocusAPI is free software: you can redistribute it and/or modify it under the terms of the Lesser
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * LocusAPI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Lesser
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with LocusAPI. If
 * not, see <http://www.gnu.org/licenses/lgpl.html/>.
 */

package menion.android.whereyougo.geo.location;

import java.lang.reflect.Field;

import locus.api.objects.Storable;

public class Location {


    private static final String NEW_LINE = System.getProperty("line.separator");
    // provider for location source
    String provider;
    // location time
    long time;
    // latitude of location in WGS coordinates
    double mLatitude;
    // longitude of location in WGS coordinates
    double mLongitude;
    // flag if altitude is set
    boolean mHasAltitude;
    // altitude value
    double mAltitude;
    boolean mHasSpeed;
    float mSpeed;
    boolean mHasBearing;
    float mBearing;
    boolean mHasAccuracy;
    float mAccuracy;

    /**
     * Empty constructor used for {@link Storable} <br />
     * Do not use directly!
     */
    public Location() {
        this("");
    }

    public Location(android.location.Location loc) {
        this(loc.getProvider());
        setLongitude(loc.getLongitude());
        setLatitude(loc.getLatitude());
        setTime(loc.getTime());
        if (loc.hasAccuracy()) {
            setAccuracy(loc.getAccuracy());
        }
        if (loc.hasAltitude()) {
            setAltitude(loc.getAltitude());
        }
        if (loc.hasBearing()) {
            setBearing(loc.getBearing());
        }
        if (loc.hasSpeed()) {
            setSpeed(loc.getSpeed());
        }
    }

    public Location(Location loc) {
        set(loc);
    }

    /**
     * Constructs a new Location.
     *
     * @param provider the name of the location provider that generated this location fix.
     */
    public Location(String provider) {
        super();
        setProvider(provider);
    }


    public Location(String provider, double lat, double lon) {
        super();
        setProvider(provider);
        this.mLatitude = lat;
        this.mLongitude = lon;
    }


    /**
     * Returns the approximate initial bearing in degrees East of true North when traveling along the
     * shortest path between this location and the given location. The shortest path is defined using
     * the WGS84 ellipsoid. Locations that are (nearly) antipodal may produce meaningless results.
     *
     * @param dest the destination location
     * @return the initial bearing in degrees
     */
    public float bearingTo(Location dest) {
        LocationCompute com = new LocationCompute(this);
        return com.bearingTo(dest);
    }

    /**
     * Compute bearing and distance values at once
     *
     * @param dest the destination location
     * @return array with float[0] - distance (in metres), float[1] - bearing (in degree)
     */
    public float[] distanceAndBearingTo(Location dest) {
        LocationCompute com = new LocationCompute(this);
        return new float[]{com.distanceTo(dest), com.bearingTo(dest)};
    }

    /**
     * Returns the approximate distance in meters between this location and the given location.
     * Distance is defined using the WGS84 ellipsoid.
     *
     * @param dest the destination location
     * @return the approximate distance in meters
     */
    public float distanceTo(Location dest) {
        LocationCompute com = new LocationCompute(this);
        return com.distanceTo(dest);
    }

    /**
     * Returns the accuracy of the fix in meters. If hasAccuracy() is false, 0.0 is returned.
     */
    public float getAccuracy() {
        if (mHasAccuracy) {
            return mAccuracy;
        }
        return 0.0f;
    }

    /**
     * Sets the accuracy of this fix. Following this call, hasAccuracy() will return true.
     */
    public void setAccuracy(float accuracy) {
        mAccuracy = accuracy;
        mHasAccuracy = true;
    }

    /**
     * Returns the altitude of this fix. If {@link #hasAltitude} is false, 0.0f is returned.
     */
    public double getAltitude() {
        return mAltitude;
    }

    /**
     * Sets the altitude of this fix. Following this call, hasAltitude() will return true.
     */
    public void setAltitude(double altitude) {
        this.mAltitude = altitude;
        this.mHasAltitude = true;
    }

    /**
     * Returns the direction of travel in degrees East of true North. If hasBearing() is false, 0.0 is
     * returned.
     */
    public float getBearing() {
        if (mHasBearing) {
            return mBearing;
        }
        return 0.0f;
    }

    /**************************************************/
  /* GETTER & SETTERS */
    /**************************************************/

    /**
     * Sets the bearing of this fix. Following this call, hasBearing() will return true.
     */
    public void setBearing(float bearing) {
        while (bearing < 0.0f) {
            bearing += 360.0f;
        }
        while (bearing >= 360.0f) {
            bearing -= 360.0f;
        }

        mBearing = bearing;
        mHasBearing = true;
    }

    /**
     * Returns the latitude of this fix.
     */
    public double getLatitude() {
        return mLatitude;
    }

    /**
     * Sets the latitude of this fix.
     */
    public Location setLatitude(double latitude) {
        this.mLatitude = latitude;
        return this;
    }

    /**
     * Returns the longitude of this fix.
     */
    public double getLongitude() {
        return mLongitude;
    }

    /**************************************************/
  /* BASIC EXTRA DATA */
    /**************************************************/

    // SPEED

    /**
     * Sets the longitude of this fix.
     */
    public Location setLongitude(double longitude) {
        this.mLongitude = longitude;
        return this;
    }

    /**
     * Returns the name of the provider that generated this fix, or null if it is not associated with
     * a provider.
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Sets the name of the provider that generated this fix.
     */
    public Location setProvider(String provider) {
        if (provider == null) {
            this.provider = "";
        } else {
            this.provider = provider;
        }
        return this;
    }

    /**
     * Returns the speed of the device over ground in meters/second. If hasSpeed() is false, 0.0f is
     * returned.
     */
    public float getSpeed() {
        if (mHasSpeed) {
            return mSpeed;
        }
        return 0.0f;
    }

    // BEARING

    /**
     * Sets the speed of this fix, in meters/second. Following this call, hasSpeed() will return true.
     */
    public void setSpeed(float speed) {
        mSpeed = speed;
        mHasSpeed = true;
    }

    /**
     * Returns the UTC time of this fix, in milliseconds since January 1, 1970.
     */
    public long getTime() {
        return time;
    }

    /**
     * Sets the UTC time of this fix, in milliseconds since January 1, 1970.
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Returns true if the provider is able to report accuracy information, false otherwise. The
     * default implementation returns false.
     */
    public boolean hasAccuracy() {
        return mHasAccuracy;
    }

    // ACCURACY

    /**
     * Returns true if this fix contains altitude information, false otherwise.
     */
    public boolean hasAltitude() {
        return mHasAltitude;
    }

    /**
     * Returns true if the provider is able to report bearing information, false otherwise. The
     * default implementation returns false.
     */
    public boolean hasBearing() {
        return mHasBearing;
    }

    /**
     * Returns true if this fix contains speed information, false otherwise. The default
     * implementation returns false.
     */
    public boolean hasSpeed() {
        return mHasSpeed;
    }

    /**
     * Returns the approximate distance in meters between this longitude and the next longitude at
     * this position (latitude) Distance is defined using the WGS84 ellipsoid.
     *
     * @return the approximate distance in meters
     */
    public float longitudeLineDistance() {
        Location next = new Location(this);
        next.setLongitude(this.getLongitude() + 1);
        return this.distanceTo(next);
    }


    /**************************************************/
  /* UTILS PART */
    /**************************************************/

    /**
     * Clears the accuracy of this fix. Following this call, hasAccuracy() will return false.
     */
    public void removeAccuracy() {
        mAccuracy = 0.0f;
        mHasAccuracy = false;
    }

    /**
     * Clears the altitude of this fix. Following this call, hasAltitude() will return false.
     */
    public void removeAltitude() {
        this.mAltitude = 0.0f;
        this.mHasAltitude = false;
    }

    /**
     * Clears the bearing of this fix. Following this call, hasBearing() will return false.
     */
    public void removeBearing() {
        mBearing = 0.0f;
        mHasBearing = false;
    }

    /**
     * Clears the speed of this fix. Following this call, hasSpeed() will return false.
     */
    public void removeSpeed() {
        mSpeed = 0.0f;
        mHasSpeed = false;
    }

    /**
     * Sets the contents of the location to the values from the given location.
     */
    public void set(Location l) {
        provider = l.provider;
        time = l.time;
        mLatitude = l.mLatitude;
        mLongitude = l.mLongitude;
        mHasAltitude = l.mHasAltitude;
        mAltitude = l.mAltitude;

        mHasSpeed = l.mHasSpeed;
        mSpeed = l.mSpeed;

        mHasBearing = l.mHasBearing;
        mBearing = l.mBearing;

        mHasAccuracy = l.mHasAccuracy;
        mAccuracy = l.mAccuracy;

    }

    /**************************************************/
  /* UTILS */

    /**************************************************/

    @Override
    public String toString() {
        return toString(this, "");
    }

    private String toString(Object obj, String prefix) {
        // add base
        StringBuilder result = new StringBuilder();
        result.append(prefix);
        if (obj == null) {
            result.append(" empty object!");
            return result.toString();
        }

        // handle existing object
        result.append(obj.getClass().getName()).append(" {").append(NEW_LINE);

        // determine fields declared in this class only (no fields of superclass)
        Field[] fields = obj.getClass().getDeclaredFields();

        // print field names paired with their values
        for (Field field : fields) {
            result.append(prefix).append("    ");
            try {
                result.append(field.getName());
                result.append(": ");
                // set accessible for private fields
                field.setAccessible(true);
                // requires access to private field:
                result.append(field.get(obj));
            } catch (Exception ex) {
                System.out.println(ex);
            }
            result.append(NEW_LINE);
        }
        result.append(prefix).append("}");
        return result.toString();
    }
}
