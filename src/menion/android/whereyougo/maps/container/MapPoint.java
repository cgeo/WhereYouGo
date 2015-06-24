/*
 * Copyright 2013, 2014 biylda <biylda@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */

package menion.android.whereyougo.maps.container;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class MapPoint implements Parcelable {
  String name;
  String description;
  double latitude;
  double longitude;
  boolean target;

  public static final Parcelable.Creator<MapPoint> CREATOR = new Parcelable.Creator<MapPoint>() {
    public MapPoint createFromParcel(Parcel p) {
      return new MapPoint(p);
    }

    public MapPoint[] newArray(int size) {
      return new MapPoint[size];
    }
  };

  /**
   * Empty constructor used for {@link Serializable} <br />
   * Do not use directly!
   */
  public MapPoint() {}

  public MapPoint(Parcel p) {
    name = p.readString();
    description = p.readString();
    latitude = p.readDouble();
    longitude = p.readDouble();
    target = p.readByte() > 0;
  }

  public MapPoint(String name, double latitude, double longitude) {
    this(name, null, latitude, longitude, false);
  }
  
  public MapPoint(String name, double latitude, double longitude, boolean target) {
    this(name, null, latitude, longitude, target);
  }
  
  public MapPoint(String name, String description, double latitude, double longitude) {
    this(name, description, latitude, longitude, false);
  }

  public MapPoint(String name, String description, double latitude, double longitude, boolean target) {
    this.name = name;
    this.description = description;
    this.latitude = latitude;
    this.longitude = longitude;
    this.target = target;
  }

  @Override
  public int describeContents() {
    // TODO Auto-generated method stub
    return 0;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }
  
  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  public boolean isTarget() {
    return target;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTarget(boolean target) {
    this.target = target;
  }

  @Override
  public void writeToParcel(Parcel p, int arg1) {
    p.writeString(name);
    p.writeString(description);
    p.writeDouble(latitude);
    p.writeDouble(longitude);
    p.writeByte((byte) (target ? 1 : 0));
  }

}
