/*
  * This file is part of WhereYouGo.
  *
  * WhereYouGo is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * WhereYouGo is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with WhereYouGo.  If not, see <http://www.gnu.org/licenses/>.
  *
  * Copyright (C) 2012 Menion <whereyougo@asamm.cz>
  */ 

package menion.android.whereyougo.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;

import menion.android.whereyougo.settings.SettingValues;
import menion.android.whereyougo.settings.Settings;

public class UtilsFormat {
    
	private static final String TAG = "UtilsFormat";
	
	// degree sign
	public static String degree = "°";
	// angle mi value
    private static double angleInMi = (2 * Math.PI * 1000.0 / 360.0);

	/**
     * Format distance in metres.
     * @param dist Distance in metres.
     * @return Formated distance in appropriate units.
     */
    public static String formatAltitude(double altitude, boolean addUnits) {
    	double value = formatAltitudeValue(altitude);
    	String res =  formatDouble(value, 0);
    	if (addUnits)
    		return res + formatAltitudeUnits();
    	else 
    		return res;
    }
    
    public static double formatAltitudeValue(double altitude) {
    	if (SettingValues.FORMAT_ALTITUDE == Settings.VALUE_UNITS_ALTITUDE_FEET) {
            return altitude * 3.2808;
        } else {
            return altitude;
        }
    }
    
    public static String formatAltitudeUnits() {
    	if (SettingValues.FORMAT_ALTITUDE == Settings.VALUE_UNITS_ALTITUDE_FEET) {
            return "ft";
        } else {
            return "m";
        }
    }
    
    public static final double MILE_METERS = 1609.344;
    
    /**
     * Format distance in metres.
     * @param dist Distance in metres.
     * @return Formated distance in appropriate units.
     */
    public static String formatDistance(double dist, boolean withoutUnits) {
    	String value = null;
    	if (SettingValues.FORMAT_LENGTH == Settings.VALUE_UNITS_LENGTH_IM) {
    		double feet = dist * 3.2808;
    		if (feet > 1000.0) {
    			double mi = dist / 1609.344;
                if (mi > 100) {
                    value = formatDouble(mi, 0);
                } else if (mi > 1) {
                    value = formatDouble(mi, 1);
                } else {
                	value = formatDouble(mi, 2);
                }
    		} else {
    			if (feet < 10)
    				value = formatDouble(feet, 1); // to ft
    			else
    				value = formatDouble(feet, 0); // to ft
    		}
        } else if (SettingValues.FORMAT_LENGTH == Settings.VALUE_UNITS_LENGTH_NA) {
            if (dist > 1852.0) {
                double nmi = dist / 1852.0;
                if (nmi > 100) {
                    value = formatDouble(nmi, 0);
                } else {
                    value = formatDouble(nmi, 1);
                }
            } else {
                value = formatDouble(dist, 0);
            }
        } else { // metric
            if (dist > 1000.0) {
                double km = dist / 1000.0;
                if (km > 100) {
                    value = formatDouble(km, 0);
                } else {
                    value = formatDouble(km, 1);
                }
            } else {
            	if (dist < 10)
            		value = formatDouble(dist, 1);
            	else
            		value = formatDouble(dist, 0);
            }
        }
    	
    	if (withoutUnits)
    		return value;
    	else
    		return value + formatDistanceUnits(dist);
    }
    
    public static double formatDistanceValue(double dist) {
    	if (SettingValues.FORMAT_LENGTH == Settings.VALUE_UNITS_LENGTH_IM) {
    		double feet = dist * 3.2808;
    		if (feet > 1000.0) {
    			return dist / 1609.344;
    		} else {
            	return feet;
    		}
        } else if (SettingValues.FORMAT_LENGTH == Settings.VALUE_UNITS_LENGTH_NA) {
            if (dist > 1852.0) {
                return dist / 1852.0;
            } else {
                return dist;
            }
        } else { // metric
            if (dist > 1000.0) {
                return dist / 1000.0;
            } else {
                return dist;
            }
        }
    }
    
    public static String formatDistanceUnits(double dist) {
    	if (SettingValues.FORMAT_LENGTH == Settings.VALUE_UNITS_LENGTH_IM) {
    		double feet = dist * 3.2808;
    		if (feet > 1000.0) {
    			return "mi";    			
            } else {
                return "ft";
            }
        } else if (SettingValues.FORMAT_LENGTH == Settings.VALUE_UNITS_LENGTH_NA) {
            if (dist > 1852.0) {
                return "nmi";
            } else {
                return "m";
            }
        } else { // metric
            if (dist > 1000.0) {
                return "km";
            } else {
                return "m";
            }
        }    	
    }
    
    /**
     * Format speed to correct format.
     * @param Speed Speed in m/s.
     * @return Formated speed in appropriate units.
     */
    public static String formatSpeed(double speed, boolean withoutUnits) {
        speed = formatSpeedValue(speed);
        String result = formatDouble(speed, speed > 100 ? 0 : 1);
        
        if (withoutUnits)
        	return result;
        else
        	return result + getSpeedUnits();
    }
    
    public static double formatSpeedValue(double speed) {
        if (SettingValues.FORMAT_SPEED == Settings.VALUE_UNITS_SPEED_MILH) {
        	speed *= 2.237;
        } else if (SettingValues.FORMAT_SPEED == Settings.VALUE_UNITS_SPEED_KNOTS) {
        	speed *= (3.6 / 1.852);
        } else { // metric UNITS_LENGTH_METRIC
        	speed *= 3.6;
        }
        return speed;
    }
    
    public static String getSpeedUnits() {
        if (SettingValues.FORMAT_SPEED == Settings.VALUE_UNITS_SPEED_MILH) {
        	return "mi/h";
        } else if (SettingValues.FORMAT_SPEED == Settings.VALUE_UNITS_SPEED_KNOTS) {
        	return "nmi/h";
        } else { // metric UNITS_LENGTH_METRIC
        	return "km/h";        	
        }
    }
    
    public static String formatAngle(double angle) {
    	try {
    		// fix angle values
    		if (angle < 0)
    			angle += 360.0;
    		if (angle > 360.0)
    			angle -= 360.0f;
    		
    		if (SettingValues.FORMAT_ANGLE == Settings.VALUE_UNITS_ANGLE_DEGREE) {
    			return formatDouble(angle, 0) + "�";
    		} else if (SettingValues.FORMAT_ANGLE == Settings.VALUE_UNITS_ANGLE_MIL) {
    			return formatDouble(angle * angleInMi, 0);
    		}
    	} catch (Exception e) {
    		Logger.e(TAG, "formatAngle(" + angle + ")", e);
    	}
    	return "";
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
    	StringBuffer out = new StringBuffer();
    	out.append(formatLatitude(lat));
    	out.append(twoLines ? "<br />" : " | ");
    	out.append(formatLongitude(lon));
    	return out.toString();
    }
    
    private static void formatCooLatLon(StringBuffer out, double value, int minLen) {
		try {
	        if (SettingValues.FORMAT_COO_LATLON == Settings.VALUE_UNITS_COO_LATLON_DEC) {
	        	out.append(formatDouble(value, Const.PRECISION, minLen)).append(degree);
	        } else if (SettingValues.FORMAT_COO_LATLON == Settings.VALUE_UNITS_COO_LATLON_MIN) {
	        	double deg = Math.floor(value);
	            double min = (value - deg) * 60;
	            out.append(formatDouble(deg, 0, 2)).append(degree).append(formatDouble(min, Const.PRECISION - 2, 2)).append("'");        	
	        } else if (SettingValues.FORMAT_COO_LATLON == Settings.VALUE_UNITS_COO_LATLON_SEC) {
	        	double deg = Math.floor(value);
	            double min = Math.floor((value - deg) * 60.0);
	            double sec = (value - deg - min / 60.0) * 3600.0;
	            out.append(formatDouble(deg, 0, 2)).append(degree).append(formatDouble(min, 0, 2)).
	            		append("'").append(formatDouble(sec, Const.PRECISION - 2)).append("''");
	        }
		} catch (Exception e) {
			Logger.e(TAG, "formatCoordinates(" + out.toString() + ", " + value + ", " + minLen + "), e:" + e.toString());
		}
	}
    
    /** updated function for time formating as in stop watch */
    public static String formatTime(boolean full, long tripTime) {
    	return formatTime(full, tripTime, true);
    }
    
    /** updated function for time formating as in stop watch */
    public static String formatTime(boolean full, long tripTime, boolean withUnits) {
        long hours = tripTime / 3600000;
        long mins = (tripTime - (hours * 3600000)) / 60000;
        double sec = (tripTime - (hours * 3600000) - mins * 60000) / 1000.0;
        if (full) {
        	if (withUnits) {
        		return hours + "h:" +
        				formatDouble(mins, 0, 2) + "m:" +
        				formatDouble(sec, 0, 2) + "s";	
        	} else {
        		return formatDouble(hours, 0, 2) + ":" + 
        				formatDouble(mins, 0, 2) + ":" +
        				formatDouble(sec, 0, 2);
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
            		return formatDouble(hours, 0, 2) + ":" + 
            				formatDouble(mins, 0, 2) + ":" +
            				formatDouble(sec, 0, 2);
            	}
            }        	
        }
    }
    
    private static Date mDate;
    public static String formatDate(long time) {
    	if (mDate == null)
    		mDate = new Date();
    	mDate.setTime(time);
    	return mDate.getHours() + ":" + formatDouble(mDate.getMinutes(), 0, 2) + ":" +
    	formatDouble(mDate.getSeconds(), 0, 2);
    }
    
    /*****************************/
    /*    FORMAT DOUBLE PART     */
    /*****************************/
    
	public static String formatDouble(double value, int precision) {
		return formatDouble(value, precision, 1);
    }
	
	public static String formatDouble(double value, int precision, int minlen) {
		if (minlen < 0)
			minlen = 0;
		else if (minlen > formats.length - 1)
			minlen = formats.length - 1;
		if (precision < 0)
			precision = 0;
		else if (precision > formats[0].length - 1)
			precision = formats[0].length - 1;
		return formats[minlen][precision].format(value);
    }
	
    private static DecimalFormat[][] formats;
    static {
    	formats = new DecimalFormat[][] {
    			{
    				new DecimalFormat("#"),
    	        	new DecimalFormat("#.0"),
    	        	new DecimalFormat("#.00"),
    	        	new DecimalFormat("#.000"),
    	        	new DecimalFormat("#.0000"),
    	        	new DecimalFormat("#.00000"),
    				new DecimalFormat("#.000000")
    			}, {
    				new DecimalFormat("#0"),
    	        	new DecimalFormat("#0.0"),
    	        	new DecimalFormat("#0.00"),
    	        	new DecimalFormat("#0.000"),
    	        	new DecimalFormat("#0.0000"),
    	        	new DecimalFormat("#0.00000")    				,
    	        	new DecimalFormat("#0.000000")
    			}, {
    				new DecimalFormat("#00"),
    	        	new DecimalFormat("#00.0"),
    	        	new DecimalFormat("#00.00"),
    	        	new DecimalFormat("#00.000"),
    	        	new DecimalFormat("#00.0000"),
    	        	new DecimalFormat("#00.00000"),
    	        	new DecimalFormat("#00.000000")
    			}, {
    				new DecimalFormat("#000"),
    				new DecimalFormat("#000.0"),
    				new DecimalFormat("#000.00"),
    				new DecimalFormat("#000.000"),
    				new DecimalFormat("#000.0000"),
    				new DecimalFormat("#000.00000"),
    				new DecimalFormat("#000.000000")
    			}, {
    				new DecimalFormat("#0000"),
    				new DecimalFormat("#0000.0"),
    				new DecimalFormat("#0000.00"),
    				new DecimalFormat("#0000.000"),
    				new DecimalFormat("#0000.0000"),
    				new DecimalFormat("#0000.00000"),
    				new DecimalFormat("#0000.000000")
    			}
        	};
    	for (int i = 0; i < formats.length; i++) {
    		for (int j = 0; j < formats[i].length; j++) {
    			formats[i][j].setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
    		}
    	}
    }
    
    public static String addZeros(String text, int count) {
    	if (text == null || text.length() > count)
    		return text;
    	String res = new String(text);
    	for (int i = res.length(); i < count; i++) {
    		res = "0" + res;
    	}
    	return res;
    }
}
