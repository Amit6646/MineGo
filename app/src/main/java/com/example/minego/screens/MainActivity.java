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
import com.example.minego.models.Miner;
import com.example.minego.services.DatabaseService;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    MyLocationNewOverlay mLocationOverlay;
    private Marker myLocationMarker;
    private ArrayList<Miner> miners = new ArrayList<>();
    Button btnLogout, btnAdmin, btnEditProfile;



    private final android.os.Handler handler = new android.os.Handler();
    private final Runnable locationUpdater = new Runnable() {
        @Override public void run() {
            if (mLocationOverlay != null && myLocationMarker != null) {
                GeoPoint p = mLocationOverlay.getMyLocation();
                if (p != null) {
                    myLocationMarker.setPosition(p);
                    map.invalidate();
                }
            }
            handler.postDelayed(this, 1000); // update every 1s
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        EdgeToEdge.enable(this);
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

        btnEditProfile = findViewById(R.id.btn_main_editprofile);
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserEditProfileActivity.class);
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


//        Marker marker = new Marker(map);
//        marker.setPosition(new GeoPoint(31.9703, 34.7790));
//        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//        marker.setOnMarkerClickListener((marker1, mapView) -> {
//            Toast.makeText(MainActivity.this, "פה היוצר של המשחק גר (=", Toast.LENGTH_SHORT).show();
//            return true;
//        });


//        map.getOverlays().add(marker);
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

        if (mLocationOverlay != null) {
            mLocationOverlay.enableMyLocation();
            mLocationOverlay.enableFollowLocation();
        }
        handler.post(locationUpdater);


        DatabaseService.getInstance().getMinerList(new DatabaseService.DatabaseCallback<List<Miner>>() {
            @Override
            public void onCompleted(List<Miner> minerList) {
                miners.clear();
                miners.addAll(minerList);

                map.getOverlays().clear();

                if (myLocationMarker != null)
                    map.getOverlays().add(myLocationMarker);

                List<Marker> list = new ArrayList<>();
                for (Miner m : miners) {
                    Marker marker = new Marker(map);
                    marker.setPosition(m.asGeoPoint());
                    list.add(marker);
                }
                map.getOverlays().addAll(list);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        handler.removeCallbacks(locationUpdater);

        if (mLocationOverlay != null) {
            mLocationOverlay.disableMyLocation();
            mLocationOverlay.disableFollowLocation();
        }
        map.onPause();
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
        if (mLocationOverlay == null) {
            Toast.makeText(this, "Location overlay not initialized", Toast.LENGTH_LONG).show();
            return;
        }

        if (!map.getOverlays().contains(mLocationOverlay)) {
            map.getOverlays().add(mLocationOverlay);
        }

        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation(); // optional

        mLocationOverlay.runOnFirstFix(() -> runOnUiThread(() -> {
            GeoPoint p = mLocationOverlay.getMyLocation();
            if (p == null) return;

            // create marker once
            if (myLocationMarker == null) {
                myLocationMarker = new Marker(map);
                myLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                // optional: custom icon
                // myLocationMarker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_my_location));
                map.getOverlays().add(myLocationMarker);
            }

            myLocationMarker.setPosition(p);
            map.getController().animateTo(p);
            map.invalidate();



            // keep updating marker as location changes:
            mLocationOverlay.enableMyLocation(); // already enabled, but ok
        }));
    }

}