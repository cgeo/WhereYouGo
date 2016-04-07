/*
 * This file is part of WhereYouGo.
 * 
 * WhereYouGo is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * WhereYouGo is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with WhereYouGo. If not,
 * see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2012 Menion <whereyougo@asamm.cz>
 */

package menion.android.whereyougo.utils;

import android.location.Location;

import org.mapsforge.core.model.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;

import menion.android.whereyougo.preferences.PreferenceValues;
import menion.android.whereyougo.preferences.Preferences;

public class UtilsFormat {

    private static final String TAG = "UtilsFormat";
    // degree sign
    public static String degree = "\u00b0";
    private static Date mDate;
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String formatAltitude(double altitude, boolean addUnits) {
        return locus.api.android.utils.UtilsFormat.formatAltitude(Preferences.FORMAT_ALTITUDE, altitude, addUnits);
    }

    public static String formatAngle(double angle) {
        return locus.api.android.utils.UtilsFormat.formatAngle(Preferences.FORMAT_ANGLE, (float) ((angle % 360) + 360) % 360, false, 0);
    }

    public static String formatSpeed(double speed, boolean withoutUnits) {
        return locus.api.android.utils.UtilsFormat.formatSpeed(Preferences.FORMAT_SPEED, speed, withoutUnits);
    }

    public static String formatDistance(double dist, boolean withoutUnits) {
        return locus.api.android.utils.UtilsFormat.formatDistance(Preferences.FORMAT_LENGTH, dist, withoutUnits);
    }

    public static String formatDouble(double value, int precision) {
        return locus.api.android.utils.UtilsFormat.formatDouble(value, precision);
    }

    public static String formatDouble(double value, int precision, int minlen) {
        return locus.api.android.utils.UtilsFormat.formatDouble(value, precision, minlen);
    }

    public static String addZeros(String text, int count) {
        if (text == null || text.length() > count)
            return text;
        String res = text;
        for (int i = res.length(); i < count; i++) {
            res = "0" + res;
        }
        return res;
    }

    public static String formatLatitude(double latitude) {
        StringBuffer out = new StringBuffer();
        if (latitude < 0) {
            out.append("S ");
        } else {
            out.append("N ");
        }
        latitude = Math.abs(latitude);

        formatCooLatLon(out, latitude, 2);
        return out.toString();
    }

    public static String formatLongitude(double longitude) {
        StringBuffer out = new StringBuffer();

        if (longitude < 0) {
            out.append("W ");
        } else {
            out.append("E ");
        }
        longitude = Math.abs(longitude);

        formatCooLatLon(out, longitude, 3);
        return out.toString();
    }

    public static String formatCooByType(double lat, double lon, boolean twoLines) {
        StringBuilder out = new StringBuilder();
        out.append(formatLatitude(lat));
        out.append(twoLines ? "<br />" : " | ");
        out.append(formatLongitude(lon));
        return out.toString();
    }

    private static void formatCooLatLon(StringBuffer out, double value, int minLen) {
        try {
            if (Preferences.FORMAT_COO_LATLON == PreferenceValues.VALUE_UNITS_COO_LATLON_DEC) {
                out.append(formatDouble(value, Const.PRECISION, minLen)).append(degree);
            } else if (Preferences.FORMAT_COO_LATLON == PreferenceValues.VALUE_UNITS_COO_LATLON_MIN) {
                double deg = Math.floor(value);
                double min = (value - deg) * 60;
                out.append(formatDouble(deg, 0, 2)).append(degree)
                        .append(formatDouble(min, Const.PRECISION - 2, 2)).append("'");
            } else if (Preferences.FORMAT_COO_LATLON == PreferenceValues.VALUE_UNITS_COO_LATLON_SEC) {
                double deg = Math.floor(value);
                double min = Math.floor((value - deg) * 60.0);
                double sec = (value - deg - min / 60.0) * 3600.0;
                out.append(formatDouble(deg, 0, 2)).append(degree).append(formatDouble(min, 0, 2))
                        .append("'").append(formatDouble(sec, Const.PRECISION - 2)).append("''");
            }
        } catch (Exception e) {
            Logger.e(TAG, "formatCoordinates(" + out.toString() + ", " + value + ", " + minLen + "), e:"
                    + e.toString());
        }
    }

    public static String formatGeoPoint(GeoPoint geoPoint) {
        return formatCooByType(geoPoint.latitude, geoPoint.longitude, false);
    }

    public static String formatGeoPointDefault(GeoPoint geoPoint) {
        String strLatitude = Location.convert(geoPoint.latitude, Location.FORMAT_MINUTES).replace(':', '\u00b0');
        String strLongitude = Location.convert(geoPoint.longitude, Location.FORMAT_MINUTES).replace(':', '\u00b0');
        return String.format("N %s E %s", strLatitude, strLongitude);
    }

    public static String formatTime(long time) {
        if (mDate == null)
            mDate = new Date();
        mDate.setTime(time);
        return timeFormat.format(mDate);
    }

    public static String formatDate(long time) {
        if (mDate == null)
            mDate = new Date();
        mDate.setTime(time);
        return dateFormat.format(mDate);
    }

    public static String formatDatetime(long time) {
        if (mDate == null)
            mDate = new Date();
        mDate.setTime(time);
        return datetimeFormat.format(mDate);
    }

    public static String formatTime(boolean full, long tripTime) {
        return formatTime(full, tripTime, true);
    }

    /**
     * updated function for time formating as in stop watch
     */
    public static String formatTime(boolean full, long tripTime, boolean withUnits) {
        long hours = tripTime / 3600000;
        long mins = (tripTime - (hours * 3600000)) / 60000;
        double sec = (tripTime - (hours * 3600000) - mins * 60000) / 1000.0;
        if (full) {
            if (withUnits) {
                return hours + "h:" + formatDouble(mins, 0, 2) + "m:" + formatDouble(sec, 0, 2) + "s";
            } else {
                return formatDouble(hours, 0, 2) + ":" + formatDouble(mins, 0, 2) + ":"
                        + formatDouble(sec, 0, 2);
            }
        } else {
            if (hours == 0) {
                if (mins == 0) {
                    if (withUnits)
                        return formatDouble(sec, 0) + "s";
                    else
                        return formatDouble(sec, 0, 2);
                } else {
                    if (withUnits)
                        return mins + "m:" + formatDouble(sec, 0) + "s";
                    else
                        return formatDouble(mins, 0, 2) + ":" + formatDouble(sec, 0, 2);
                }
            } else {
                if (withUnits) {
                    return hours + "h:" + mins + "m";
                } else {
                    return formatDouble(hours, 0, 2) + ":" + formatDouble(mins, 0, 2) + ":"
                            + formatDouble(sec, 0, 2);
                }
            }
        }
    }
}
