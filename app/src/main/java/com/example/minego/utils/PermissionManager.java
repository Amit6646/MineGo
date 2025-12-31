package com.example.minego.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;

public class PermissionManager {

    public static final int REQUEST_CODE_LOCATION = 1;

    // The permissions we need for this app
    public static final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    /**
     * Checks if all permissions in the array are granted.
     */
    public static boolean hasLocationPermissions(Context context) {
        for (String permission : LOCATION_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Requests the permissions.
     */
    public static void requestLocationPermissions(Activity activity) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : LOCATION_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    activity,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_CODE_LOCATION
            );
        }
    }
}