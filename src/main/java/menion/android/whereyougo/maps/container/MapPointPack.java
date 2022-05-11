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

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MapPointPack implements Parcelable {

    public static final Parcelable.Creator<MapPointPack> CREATOR =
            new Parcelable.Creator<MapPointPack>() {
                public MapPointPack createFromParcel(Parcel p) {
                    return new MapPointPack(p);
                }

                public MapPointPack[] newArray(int size) {
                    return new MapPointPack[size];
                }
            };
    private boolean isPolygon;
    private int resource;
    private Bitmap icon = null;
    private ArrayList<MapPoint> points;

    public MapPointPack() {
        points = new ArrayList<>();
    }

    public MapPointPack(ArrayList<MapPoint> points, boolean isPolygon) {
        this.points = points;
        this.isPolygon = isPolygon;
    }

    public MapPointPack(ArrayList<MapPoint> points, boolean isPolygon, Bitmap icon) {
        this(points, isPolygon);
        this.icon = icon;
    }

    public MapPointPack(ArrayList<MapPoint> points, boolean isPolygon, int resource) {
        this(points, isPolygon);
        this.resource = resource;
    }

    public MapPointPack(boolean isPolygon) {
        this(new ArrayList<MapPoint>(), isPolygon);
    }

    public MapPointPack(boolean isPolygon, Bitmap icon) {
        this(new ArrayList<MapPoint>(), isPolygon, icon);
    }

    public MapPointPack(boolean isPolygon, int resource) {
        this(new ArrayList<MapPoint>(), isPolygon, resource);
    }

    public MapPointPack(Parcel p) {
        isPolygon = p.readInt() == 1;
        resource = p.readInt();
        points = p.readArrayList(getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public ArrayList<MapPoint> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<MapPoint> points) {
        this.points = points;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public boolean isPolygon() {
        return isPolygon;
    }

    public void setPolygon(boolean isPolygon) {
        this.isPolygon = isPolygon;
    }

    @Override
    public void writeToParcel(Parcel p, int arg1) {
        p.writeInt(isPolygon ? 1 : 0);
        p.writeInt(resource);
        p.writeList(points);
    }
}
