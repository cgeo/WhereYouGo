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

package menion.android.whereyougo.openwig;

import cz.matejcik.openwig.platform.LocationService;
import menion.android.whereyougo.MainApplication;
import menion.android.whereyougo.utils.Logger;

public class WLocationService implements LocationService {

    private static final String TAG = "WLocationService";

    public void connect() {
        Logger.w(TAG, "connect()");
    }

    public void disconnect() {
        Logger.w(TAG, "disconnect()");
    }

    public double getAltitude() {
        return MainApplication.getInstance().getLocationState().getLocation().getAltitude();
    }

    public double getHeading() {
        return MainApplication.getInstance().getLocationState().getLocation().getBearing();
    }

    public double getLatitude() {
        return MainApplication.getInstance().getLocationState().getLocation().getLatitude();
    }

    public double getLongitude() {
        return MainApplication.getInstance().getLocationState().getLocation().getLongitude();
    }

    public double getPrecision() {
        return MainApplication.getInstance().getLocationState().getLocation().getAccuracy();
    }

    public int getState() {
        if (MainApplication.getInstance().getLocationState().isActuallyHardwareGpsOn())
            return LocationService.ONLINE;
        else
            return LocationService.OFFLINE;
    }
}
