/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package menion.android.whereyougo.maps.mapsforge.mapgenerator;

import android.util.AttributeSet;

import org.mapsforge.android.maps.mapgenerator.MapGenerator;
import org.mapsforge.android.maps.mapgenerator.blank.Blank;
import org.mapsforge.android.maps.mapgenerator.databaserenderer.DatabaseRenderer;
import org.mapsforge.android.maps.mapgenerator.tiledownloader.FormatURLTileDownloader;

/**
 * A factory for the internal MapGenerator implementations.
 */
public final class MapGeneratorFactory {
    private static final String MAP_GENERATOR_ATTRIBUTE_NAME = "mapGenerator";

    private MapGeneratorFactory() {
        throw new IllegalStateException();
    }

    /**
     * @param attributeSet A collection of attributes which includes the desired MapGenerator.
     * @return a new MapGenerator instance.
     */
    public static MapGenerator createMapGenerator(AttributeSet attributeSet) {
        String mapGeneratorName = attributeSet.getAttributeValue(null, MAP_GENERATOR_ATTRIBUTE_NAME);
        if (mapGeneratorName == null) {
            return new DatabaseRenderer();
        }

        MapGeneratorInternal mapGeneratorInternal = MapGeneratorInternal.valueOf(mapGeneratorName);
        return MapGeneratorFactory.createMapGenerator(mapGeneratorInternal);
    }

    /**
     * @param mapGeneratorInternal the internal MapGenerator implementation.
     * @return a new MapGenerator instance.
     */
    public static MapGenerator createMapGenerator(MapGeneratorInternal mapGeneratorInternal) {
        switch (mapGeneratorInternal) {
            case BLANK:
                return new Blank();
            case DATABASE_RENDERER:
                return new DatabaseRenderer();
            case OPENSTREETMAP:
                return new FormatURLTileDownloader(
                        18,
                        "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png",
                        "\u00a9 OpenStreetMap contributors, CC-BY-SA");
            case OPENSTREETMAP_DE:
                return new FormatURLTileDownloader(
                        18,
                        "https://a.tile.openstreetmap.de/{z}/{x}/{y}.png",
                        "\u00a9 OpenStreetMap contributors, CC-BY-SA");
            case OPENSTREETMAP_CyclOSM:
                return new FormatURLTileDownloader(
                        18,
                        "https://a.tile-cyclosm.openstreetmap.fr/cyclosm/{z}/{x}/{y}.png",
                        "Tiles \u00a9 CyclOSM, Openstreetmap France, data \u00a9 OpenStreetMap contributors, ODBL");
            case PUBLIC_TRANSPORT_OEPNV:
                return new FormatURLTileDownloader(
                        18,
                        "https://tile.memomaps.de/tilegen/{z}/{x}/{y}.png",
                        "\u00a9 OpenStreetMap contributors, CC-BY-SA");
            case ESRI_WORLD_STREET_MAP:
                return new FormatURLTileDownloader(
                        18,
                        "https://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}.png",
                        "\u00a9 Esri, DeLorme, NAVTEQ, USGS, Intermap, iPC, NRCAN, Esri Japan, METI, Esri China (Hong Kong), Esri (Thailand), TomTom, 2012");
            case ESRI_WORLD_IMAGERY:
                return new FormatURLTileDownloader(
                        18,
                        "https://services.arcgisonline.com/arcgis/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}.png",
                        "\u00a9 Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community");
            default:
                return new Blank();
        }

        // throw new IllegalArgumentException("unknown enum value: " + mapGeneratorInternal);
    }
}
