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

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Vector;

import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.EventTable;
import cz.matejcik.openwig.Zone;
import cz.matejcik.openwig.formats.CartridgeFile;
import locus.api.android.objects.PackWaypoints;
import locus.api.objects.extra.ExtraData;
import locus.api.objects.extra.ExtraStyle;
import locus.api.objects.extra.ExtraStyle.LineStyle.ColorStyle;
import locus.api.objects.extra.ExtraStyle.LineStyle.Units;
import locus.api.objects.extra.Location;
import locus.api.objects.extra.Track;
import locus.api.objects.extra.Waypoint;
import menion.android.whereyougo.MainApplication;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.activity.wherigo.DetailsActivity;
import menion.android.whereyougo.gui.utils.UtilsWherigo;
import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;

public class LocusMapDataProvider implements MapDataProvider {
    private static LocusMapDataProvider instance = null;
    private ArrayList<Track> tracks = null;
    private final PackWaypoints pack;

    private LocusMapDataProvider() {
        tracks = new ArrayList<>();
        pack = new PackWaypoints("WhereYouGo");
    }

    public static LocusMapDataProvider getInstance() {
        if (instance == null)
            instance = new LocusMapDataProvider();
        return instance;
    }

    public void addAll() {
        Vector<CartridgeFile> v = new Vector<>();
        v.add(MainActivity.cartridgeFile);
        addCartridges(v);
        addZones((Vector<Zone>) MainApplication.getInstance().getEngine().cartridge.zones, DetailsActivity.et);
        if (DetailsActivity.et != null && !(DetailsActivity.et instanceof Zone))
            addOther(DetailsActivity.et, true);
    }

    public void addCartridges(Vector<CartridgeFile> cartridges) {
        if (cartridges == null)
            return;
        // Bitmap b = Images.getImageB(R.drawable.wherigo, (int) Utils.getDpPixels(32.0f));
        // pack.setBitmap(b);
        for (CartridgeFile cartridge : cartridges) {
            // do not show waypoints that are "Play anywhere" (with zero
            // coordinates)
            if (cartridge.latitude % 360.0 == 0 && cartridge.longitude % 360.0 == 0) {
                continue;
            }

            // construct waypoint
            Location loc = new Location("WhereYouGo");
            loc.setLatitude(cartridge.latitude);
            loc.setLongitude(cartridge.longitude);
            Waypoint wpt = new Waypoint(cartridge.name, loc);
            wpt.addParameter(ExtraData.PAR_DESCRIPTION, cartridge.description);
            wpt.addUrl(cartridge.url);
            pack.addWaypoint(wpt);
        }
    }

    public void addOther(EventTable et, boolean mark) {
        if (et == null || !et.isLocated() || !et.isVisible())
            return;

        Location loc = UtilsWherigo.extractLocation(et);
        pack.addWaypoint(new Waypoint(et.name, loc));
    }

    public void addZone(Zone z, boolean mark) {
        if (z == null || !z.isLocated() || !z.isVisible())
            return;

        ArrayList<Location> locs = new ArrayList<>();
        for (int i = 0; i < z.points.length; i++) {
            Location loc = new Location("");
            loc.setLatitude(z.points[i].latitude);
            loc.setLongitude(z.points[i].longitude);
            locs.add(loc);
        }
        if (locs.size() >= 3)
            locs.add(locs.get(0));

        Track track = new Track();
        ExtraStyle style = new ExtraStyle();
        style.setLineStyle(ColorStyle.SIMPLE, Color.MAGENTA, 2.0f, Units.PIXELS);
        track.styleNormal = style;
        track.setPoints(locs);
        track.setName(z.name);

        tracks.add(track);
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
        tracks.clear();
        pack.reset();
    }

    public PackWaypoints getPoints() {
        return pack;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }
}
