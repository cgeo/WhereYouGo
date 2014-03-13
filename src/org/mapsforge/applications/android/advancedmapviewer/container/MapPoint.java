package org.mapsforge.applications.android.advancedmapviewer.container;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class MapPoint implements Parcelable {
  String name;
  double latitude;
  double longitude;

  /**
   * Empty constructor used for {@link Serializable} <br />
   * Do not use directly!
   */
  public MapPoint() {}

  public MapPoint(String name, double latitude, double longitude) {
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
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
  }

}
