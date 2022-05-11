/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2013, 2014 biylda <biylda@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package menion.android.whereyougo.maps.mapsforge.overlay;

import menion.android.whereyougo.maps.container.MapPoint;

import android.graphics.drawable.Drawable;

import org.mapsforge.core.model.GeoPoint;

public class PointOverlay extends LabelMarker {
    int id;
    MapPoint point;

    public PointOverlay(GeoPoint geoPoint, Drawable drawable, MapPoint point) {
        this(geoPoint, drawable, point, -1);
    }

    public PointOverlay(GeoPoint geoPoint, Drawable drawable, MapPoint point, int id) {
        super(geoPoint, drawable, point.getName(), point.getDescription() == null ? "" : point.getDescription());
        this.id = id;
        this.point = point;
    }

    /**
     * @param geoPoint the initial geographical coordinates of this marker (may be null).
     * @param drawable the initial {@code Drawable} of this marker (may be null).
     */
    public PointOverlay(int id, GeoPoint geoPoint, Drawable drawable) {
        super(geoPoint, drawable);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public MapPoint getPoint() {
        return point;
    }
}
