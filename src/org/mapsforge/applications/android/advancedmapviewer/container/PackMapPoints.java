package org.mapsforge.applications.android.advancedmapviewer.container;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class PackMapPoints implements Parcelable {

  boolean isPolygon;
  int resource;
  ArrayList<MapPoint> points;

  public PackMapPoints() {
    points = new ArrayList<MapPoint>();
  }

  public PackMapPoints(ArrayList<MapPoint> points, boolean isPolygon) {
    this.points = points;
    this.isPolygon = isPolygon;
  }

  public PackMapPoints(ArrayList<MapPoint> points, boolean isPolygon, int resource) {
    this.points = points;
    this.isPolygon = isPolygon;
    this.resource = resource;
  }


  public boolean isPolygon() {
    return isPolygon;
  }

  public void setPolygon(boolean isPolygon) {
    this.isPolygon = isPolygon;
  }

  public int getResource() {
    return resource;
  }

  public void setResource(int resource) {
    this.resource = resource;
  }

  public ArrayList<MapPoint> getPoints() {
    return points;
  }

  public void setPoints(ArrayList<MapPoint> points) {
    this.points = points;
  }

  @Override
  public int describeContents() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void writeToParcel(Parcel p, int arg1) {
    p.writeInt(isPolygon ? 1 : 0);
    p.writeInt(resource);
    p.writeList(points);
  }

  public static final Parcelable.Creator<PackMapPoints> CREATOR =
      new Parcelable.Creator<PackMapPoints>() {
        public PackMapPoints createFromParcel(Parcel p) {
          return new PackMapPoints(p);
        }

        public PackMapPoints[] newArray(int size) {
          return new PackMapPoints[size];
        }
      };

  public PackMapPoints(Parcel p) {
    isPolygon = p.readInt() == 1 ? true : false;
    resource = p.readInt();
    points = p.readArrayList(getClass().getClassLoader());
  }
}
