package org.mapsforge.applications.android.advancedmapviewer.container;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class MapPointPack implements Parcelable {

	boolean isPolygon;
	int resource;
	Bitmap icon = null;
	ArrayList<MapPoint> points;

	public MapPointPack() {
		points = new ArrayList<MapPoint>();
	}

	public MapPointPack(ArrayList<MapPoint> points, boolean isPolygon) {
		this.points = points;
		this.isPolygon = isPolygon;
	}

	public MapPointPack(ArrayList<MapPoint> points, boolean isPolygon,
			int resource) {
		this.points = points;
		this.isPolygon = isPolygon;
		this.resource = resource;
	}

	public MapPointPack(ArrayList<MapPoint> points, boolean isPolygon,
			Bitmap icon) {
		this.points = points;
		this.isPolygon = isPolygon;
		this.icon = icon;
	}

	public MapPointPack(boolean isPolygon) {
		this();
		this.isPolygon = isPolygon;
	}

	public MapPointPack(boolean isPolygon, int resource) {
		this();
		this.isPolygon = isPolygon;
		this.resource = resource;
	}

	public MapPointPack(boolean isPolygon, Bitmap icon) {
		this();
		this.isPolygon = isPolygon;
		this.icon = icon;
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

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
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

	public static final Parcelable.Creator<MapPointPack> CREATOR = new Parcelable.Creator<MapPointPack>() {
		public MapPointPack createFromParcel(Parcel p) {
			return new MapPointPack(p);
		}

		public MapPointPack[] newArray(int size) {
			return new MapPointPack[size];
		}
	};

	public MapPointPack(Parcel p) {
		isPolygon = p.readInt() == 1 ? true : false;
		resource = p.readInt();
		points = p.readArrayList(getClass().getClassLoader());
	}
}
