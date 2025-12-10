package com.example.minego.screens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minego.R;
import com.example.minego.utils.SharedPreferencesUtil;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    MyLocationNewOverlay mLocationOverlay;
    Button btnLogout, btnAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLogout = findViewById(R.id.btn_main_logout);
        btnLogout.setOnClickListener(v -> {
            SharedPreferencesUtil.signOutUser(MainActivity.this);
            Intent mainIntent = new Intent(MainActivity.this, LandingActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
        });

        btnAdmin = findViewById(R.id.btn_main_admin);
        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(intent);
        });
        // Initialize MapView
        map = findViewById(R.id.adminmap);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // Initialize the location overlay before checking permissions
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);

        // Request location permissions
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setMaxZoomLevel(21.0);
        map.setMinZoomLevel(8.0);

        IMapController mapController = map.getController();
        mapController.setZoom(18.5);

        // Set a fallback starting point until we have a valid location
        mapController.setCenter(new GeoPoint(31.9703, 34.7790));

        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(31.9703, 34.7790));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        marker.setOnMarkerClickListener((marker1, mapView) -> {
            Toast.makeText(MainActivity.this, "פה היוצר של המשחק גר (=", Toast.LENGTH_SHORT).show();
            return true;
        });

        map.getOverlays().add(marker);
        map.invalidate();
    }


    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            // If permissions are already granted, enable location
            enableLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            boolean allPermissionsGranted = true;

            // Check if all permissions are granted
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // Permissions granted, enable location
                enableLocation();
            } else {
                // Permissions denied, show a message to the user
                Toast.makeText(this, "Permissions required for location services", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void enableLocation() {
        // Check if mLocationOverlay is properly initialized
        if (mLocationOverlay != null) {
            mLocationOverlay.enableMyLocation();
            map.getOverlays().add(mLocationOverlay);

            // Optionally, you can check if location is available immediately
            checkLocationAvailability();
        } else {
            // Handle the case where mLocationOverlay is not initialized (shouldn't happen)
            Toast.makeText(this, "Location overlay not initialized", Toast.LENGTH_LONG).show();
        }
    }

    private void checkLocationAvailability() {
        if (mLocationOverlay.getMyLocation() != null) {
            // Location is available immediately
            GeoPoint myLocation = mLocationOverlay.getMyLocation();
            Toast.makeText(MainActivity.this, myLocation.toString(), Toast.LENGTH_LONG).show();

            // Set a fallback starting point until we have a valid location
            map.getController().setCenter(myLocation);
        } else {
            // ahhhhhhh!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }
    }

}