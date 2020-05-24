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

/**
 * Enumeration of all internal MapGenerator implementations.
 */
public enum MapGeneratorInternal {
    /**
     * Blank map.
     */
    BLANK,

    /**
     * Map tiles are rendered offline.
     */
    DATABASE_RENDERER,

    /**
     * Map tiles are downloaded from https://openstreetmap.org
     *
     * @see <a href="https://openstreetmap.org/">OpenStreetMap.org</a>
     */
    OPENSTREETMAP,

    /**
     * Map tiles are downloaded from https://openstreetmap.de
     *
     * @see <a href="https://openstreetmap.de/">OpenStreetMap.de</a>
     */
    OPENSTREETMAP_DE,

    /**
     * Map tiles are downloaded from the CyclOSM server.
     *
     * @see <a href="https://www.cyclosm.org/">CyclOSM</a>
     */
    OPENSTREETMAP_CyclOSM,

    /**
     * Map tiles are downloaded from the Public Transport (\u00d6PNV) server.
     *
     * @see <a href="https://\u00f6pnvkarte.de/">Public Transport (\u00d6PNV)</a>
     */
    PUBLIC_TRANSPORT_OEPNV,

    /**
     * Map tiles are downloaded from the Esri server.
     *
     * @see <a href="https://www.arcgisonline.com/">ArcGIS</a>
     */
    ESRI_WORLD_STREET_MAP,

    /**
     * Map tiles are downloaded from the Esri server.
     *
     * @see <a href="https://www.arcgisonline.com/">ArcGIS</a>
     */
    ESRI_WORLD_IMAGERY
}