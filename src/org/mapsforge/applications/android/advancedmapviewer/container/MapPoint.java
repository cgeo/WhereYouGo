package org.mapsforge.applications.android.advancedmapviewer.container;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class MapPoint implements Parcelable {
  String name;
  double latitude;
  double longitude;
  boolean target;

  /**
   * Empty constructor used for {@link Serializable} <br />
   * Do not use directly!
   */
  public MapPoint() {}

  public MapPoint(String name, double latitude, double longitude) {
    this(name, latitude, longitude, false);
  }

  public MapPoint(String name, double latitude, double longitude, boolean target) {
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.target = target;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public boolean isTarget() {
    return target;
  }

  public void setTarget(boolean target) {
    this.target = target;
  }

  @Override
  public int describeContents() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void writeToParcel(Parcel p, int arg1) {
    // TODO Auto-generated method stub
    p.writeString(name);
    p.writeDouble(latitude);
    p.writeDouble(longitude);
    p.writeByte((byte) (target ? 1 : 0));
  }

  public static final Parcelable.Creator<MapPoint> CREATOR = new Parcelable.Creator<MapPoint>() {
    public MapPoint createFromParcel(Parcel p) {
      return new MapPoint(p);
    }

    public MapPoint[] newArray(int size) {
      return new MapPoint[size];
    }
  };

  public MapPoint(Parcel p) {
    name = p.readString();
    latitude = p.readDouble();
    longitude = p.readDouble();
    target = p.readByte() > 0;
  }

}
