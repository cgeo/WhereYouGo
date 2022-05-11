package menion.android.whereyougo.utils;

import menion.android.whereyougo.R;

import android.content.Context;
import android.content.pm.PackageManager;

public class CgeoUtils {

    private CgeoUtils() {
        // utility class
    }

    public static boolean isInstalled(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getString(R.string.cgeo_package), 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
