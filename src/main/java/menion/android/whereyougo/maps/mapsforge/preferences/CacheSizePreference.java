/*
 * Copyright 2010, 2011, 2012 mapsforge.org
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
package menion.android.whereyougo.maps.mapsforge.preferences;

import menion.android.whereyougo.R;
import menion.android.whereyougo.maps.mapsforge.MapsforgeActivity;

import android.content.Context;
import android.util.AttributeSet;

import org.mapsforge.core.model.Tile;

/**
 * Preferences class for adjusting the cache size.
 */
public class CacheSizePreference extends SeekBarPreference {
    private static final double ONE_MEGABYTE = 1000000d;
    private static final int TILE_SIZE_IN_BYTES = Tile.TILE_SIZE * Tile.TILE_SIZE * 2;

    /**
     * Construct a new cache size preference seek bar.
     *
     * @param context the context activity.
     * @param attrs   A set of attributes (currently ignored).
     */
    public CacheSizePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        // define the text message
        this.messageText = getContext().getString(R.string.preferences_tileCache_size_desc);

        // define the current and maximum value of the seek bar
        this.seekBarCurrentValue =
                this.preferencesDefault.getInt(this.getKey(),
                        MapsforgeActivity.FILE_SYSTEM_CACHE_SIZE_DEFAULT);
        this.max = MapsforgeActivity.FILE_SYSTEM_CACHE_SIZE_MAX;
    }

    @Override
    String getCurrentValueText(int progress) {
        String format = getContext().getString(R.string.preferences_tileCache_size_value);
        Double value = TILE_SIZE_IN_BYTES * progress / ONE_MEGABYTE;
        return String.format(format, value);
    }
}
