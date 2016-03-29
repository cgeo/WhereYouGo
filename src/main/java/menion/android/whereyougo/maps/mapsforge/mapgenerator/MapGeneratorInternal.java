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
	 * Map tiles are downloaded from http://openstreetmap.org
	 * 
	 * @see <a href="http://openstreetmap.org/">OpenStreetMap.org</a>
	 */
	OPENSTREETMAP,

	/**
	 * Map tiles are downloaded from http://openstreetmap.de
	 * 
	 * @see <a href="http://openstreetmap.de/">OpenStreetMap.de</a>
	 */
	OPENSTREETMAP_DE,

	/**
	 * Map tiles are downloaded from the OpenCycleMap server.
	 * 
	 * @see <a href="http://opencyclemap.org/">OpenCycleMap</a>
	 */
	OPENCYCLEMAP_CYCLE,

	/**
	 * Map tiles are downloaded from the OpenCycleMap server.
	 * 
	 * @see <a href="http://opencyclemap.org/">OpenCycleMap</a>
	 */
	OPENCYCLEMAP_TRANSPORT,

	/**
	 * Map tiles are downloaded from the OpenCycleMap server.
	 * 
	 * @see <a href="http://opencyclemap.org/">OpenCycleMap</a>
	 */
	OPENMAPSURFER_ROADS,

	/**
	 * Map tiles are downloaded from the Mapquest server.
	 * 
	 * @see <a href="http://www.mapquest.com/">Mapquest</a>
	 */
	MAPQUEST,

	/**
	 * Map tiles are downloaded from the Mapquest server.
	 * 
	 * @see <a href="http://www.mapquest.com/">Mapquest</a>
	 */
	MAPQUEST_AERIAL,

	/**
	 * Map tiles are downloaded from the Thunderforest server.
	 * 
	 * @see <a href="http://www.thunderforest.com/">Thunderforest</a>
	 */
	THUNDERFOREST_OPENCYCLEMAP,

	/**
	 * Map tiles are downloaded from the Thunderforest server.
	 * 
	 * @see <a href="http://www.thunderforest.com/">Thunderforest</a>
	 */
	THUNDERFOREST_TRANSPORT,

	/**
	 * Map tiles are downloaded from the Thunderforest server.
	 * 
	 * @see <a href="http://www.thunderforest.com/">Thunderforest</a>
	 */
	THUNDERFOREST_LANDSCAPE,

	/**
	 * Map tiles are downloaded from the Thunderforest server.
	 * 
	 * @see <a href="http://www.thunderforest.com/">Thunderforest</a>
	 */
	THUNDERFOREST_OUTDOORS,

	/**
	 * Map tiles are downloaded from the Esri server.
	 * 
	 * @see <a href="http://www.arcgisonline.com/">ArcGIS</a>
	 */
	ESRI_WORLD_STREET_MAP,

	/**
	 * Map tiles are downloaded from the Esri server.
	 * 
	 * @see <a href="http://www.arcgisonline.com/">ArcGIS</a>
	 */
	ESRI_WORLD_IMAGERY;
}
