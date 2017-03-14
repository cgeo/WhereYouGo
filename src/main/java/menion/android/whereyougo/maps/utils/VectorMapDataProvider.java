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

package menion.android.whereyougo.maps.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.EventTable;
import cz.matejcik.openwig.Zone;
import cz.matejcik.openwig.formats.CartridgeFile;
import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.activity.wherigo.DetailsActivity;
import menion.android.whereyougo.maps.container.MapPoint;
import menion.android.whereyougo.maps.container.MapPointPack;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;

public class VectorMapDataProvider implements MapDataProvider {
    private static VectorMapDataProvider instance = null;
    private ArrayList<MapPointPack> items = null;

    private VectorMapDataProvider() {
        items = new ArrayList<>();
    }

    public static VectorMapDataProvider getInstance() {
        if (instance == null)
            instance = new VectorMapDataProvider();
        return instance;
    }

    public void addAll() {
        if (MainActivity.cartridgeFile == null || Engine.instance == null
                || Engine.instance.cartridge == null || Engine.instance.cartridge.zones == null)
            return;
        clear();
        Vector<CartridgeFile> v = new Vector<>();
        v.add(MainActivity.cartridgeFile);
        addCartridges(v);
        addZones((Vector<Zone>) Engine.instance.cartridge.zones, DetailsActivity.et);
        if (DetailsActivity.et != null && !(DetailsActivity.et instanceof Zone))
            addOther(DetailsActivity.et, true);
    }

    public void addCartridges(Vector<CartridgeFile> cartridges) {
        if (cartridges == null)
            return;
        MapPointPack pack = new MapPointPack(false, R.drawable.marker_wherigo);
        for (CartridgeFile cartridge : cartridges) {
            // do not show waypoints that are "Play anywhere" (with zero
            // coordinates)
            if (cartridge.latitude % 360.0 == 0 && cartridge.longitude % 360.0 == 0) {
                continue;
            }
            MapPoint pt =
                    new MapPoint(cartridge.name, cartridge.description, cartridge.latitude,
                            cartridge.longitude);

            pt.setData(new File(cartridge.filename).getName());

            try {
                byte[] iconData = cartridge.getFile(cartridge.iconId);
                Bitmap icon = BitmapFactory.decodeByteArray(iconData, 0, iconData.length);
                MapPointPack iconPack = new MapPointPack(false, icon);
                iconPack.getPoints().add(pt);
                items.add(iconPack);
            } catch (Exception e) {
                pack.getPoints().add(pt);
            }
        }

        items.add(pack);
    }

    public void addOther(EventTable et, boolean mark) {
        if (et == null || !et.isLocated() || !et.isVisible())
            return;

        MapPointPack pack = new MapPointPack();
        pack.getPoints().add(new MapPoint(et.name, et.position.latitude, et.position.longitude, mark));
        if (mark)
            pack.setResource(R.drawable.marker_green);
        else
            pack.setResource(R.drawable.marker_red);
        items.add(pack);
    }

    public void addZone(Zone z, boolean mark) {
        if (z == null || !z.isLocated() || !z.isVisible())
            return;

        MapPointPack border = new MapPointPack();
        border.setPolygon(true);
        for (int i = 0; i < z.points.length; i++) {
            border.getPoints().add(new MapPoint("", z.points[i].latitude, z.points[i].longitude));
        }
        if (border.getPoints().size() >= 3)
            border.getPoints().add(border.getPoints().get(0));
        items.add(border);

        MapPointPack pack = new MapPointPack();
        if (Preferences.GUIDING_ZONE_NAVIGATION_POINT == PreferenceValues.VALUE_GUIDING_ZONE_POINT_NEAREST
                || z.position == null) {
            pack.getPoints().add(
                    new MapPoint(z.name, z.description, z.nearestPoint.latitude, z.nearestPoint.longitude,
                            mark));
        } else {
            pack.getPoints().add(
                    new MapPoint(z.name, z.description, z.position.latitude, z.position.longitude, mark));
        }
        if (mark)
            pack.setResource(R.drawable.marker_green);
        else
            pack.setResource(R.drawable.marker_red);
        items.add(pack);
    }

    public void addZones(Vector<Zone> zones) {
        addZones(zones, null);
    }

    public void addZones(Vector<Zone> zones, EventTable mark) {
        if (zones == null)
            return;
        // show zones
        for (Zone z : zones) {
            addZone(z, z == mark);
        }
    }

    public void clear() {
        items.clear();
    }

    public ArrayList<MapPointPack> getItems() {
        return items;
    }
}
