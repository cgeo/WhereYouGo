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

import menion.android.whereyougo.R;
import menion.android.whereyougo.gui.activity.MainActivity;
import menion.android.whereyougo.gui.activity.wherigo.DetailsActivity;
import menion.android.whereyougo.gui.utils.UtilsWherigo;
import menion.android.whereyougo.utils.Images;
import menion.android.whereyougo.utils.Utils;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.EventTable;
import cz.matejcik.openwig.Zone;
import cz.matejcik.openwig.formats.CartridgeFile;
import locus.api.android.objects.PackPoints;
import locus.api.objects.extra.GeoDataExtra;
import locus.api.objects.extra.Location;
import locus.api.objects.geoData.Point;
import locus.api.objects.geoData.Track;
import locus.api.objects.styles.GeoDataStyle;
import locus.api.objects.styles.LineStyle;

public class LocusMapDataProvider implements MapDataProvider {
    private static LocusMapDataProvider instance = null;
    private ArrayList<Track> tracks = null;
    private final PackPoints pack;

    private LocusMapDataProvider() {
        tracks = new ArrayList<>();
        pack = new PackPoints("WhereYouGo");
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
        addZones((Vector<Zone>) Engine.instance.cartridge.zones, DetailsActivity.et);
        if (DetailsActivity.et != null && !(DetailsActivity.et instanceof Zone))
            addOther(DetailsActivity.et, true);
    }

    public void addCartridges(Vector<CartridgeFile> cartridges) {
        if (cartridges == null)
            return;

        pack.setBitmap(Images.getImageB(R.drawable.icon_gc_wherigo, (int) Utils.getDpPixels(32.0f)));

        for (CartridgeFile cartridge : cartridges) {
            // do not show waypoints that are "Play anywhere" (with zero
            // coordinates)
            if (cartridge.latitude % 360.0 == 0 && cartridge.longitude % 360.0 == 0) {
                continue;
            }

            // construct waypoint
            Location loc = new Location(cartridge.latitude, cartridge.longitude);
            Point wpt = new Point(cartridge.name, loc);

            final GeoDataExtra gde = new GeoDataExtra();
            gde.addParameter(GeoDataExtra.PAR_DESCRIPTION, cartridge.description);
            gde.addParameter(GeoDataExtra.PAR_COMMENT, cartridge.author);
            if (cartridge.url != null && cartridge.url.length() > 0) {
                gde.addAttachment(GeoDataExtra.AttachType.URL, null, cartridge.url);
            }
            wpt.setExtraData(gde);

            pack.addPoint(wpt);
        }
    }

    public void addOther(EventTable et, boolean mark) {
        if (et == null || !et.isLocated() || !et.isVisible())
            return;

        Location loc = UtilsWherigo.extractLocation(et);
        pack.addPoint(new Point(et.name, loc));
    }

    public void addZone(Zone z, boolean mark) {
        if (z == null || !z.isLocated() || !z.isVisible())
            return;

        List<Location> locs = new ArrayList<>();
        for (int i = 0; i < z.points.length; i++) {
            Location loc = new Location(z.points[i].latitude, z.points[i].longitude);
            locs.add(loc);
        }
        if (locs.size() >= 3)
            locs.add(locs.get(0));

        LineStyle lineStyle = new LineStyle();
        lineStyle.setColoring(LineStyle.Coloring.SIMPLE);
        lineStyle.setColorBase(Color.MAGENTA);
        lineStyle.setWidth(2.0f);
        lineStyle.setUnits(LineStyle.Units.PIXELS);

        GeoDataStyle geoDataStyle = new GeoDataStyle();
        geoDataStyle.setLineStyle(lineStyle);

        Track track = new Track();
        track.setStyleHighlight(geoDataStyle);
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
    }

    public PackPoints getPoints() {
        return pack;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }
}
