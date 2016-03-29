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

package menion.android.whereyougo.maps.mapsforge.model;

import org.mapsforge.core.model.GeoPoint;

@SuppressWarnings("serial")
public class MyGeoPoint extends GeoPoint {
    int id;

    public MyGeoPoint(double latitude, double longitude, int id) {
        super(latitude, longitude);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
