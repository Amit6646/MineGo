package com.example.minego.screens;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minego.R;
import com.example.minego.models.Miner;
import com.example.minego.services.DatabaseService;
import com.example.minego.utils.MapManager;
import com.example.minego.utils.PermissionManager;
import com.example.minego.utils.SharedPreferencesUtil;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MapManager mapManager;
    Button btnLogout, btnAdmin, btnEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // OSMDroid config
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupUI();

        // Initialize MapManager with the MapView from layout
        MapView mapView = findViewById(R.id.adminmap);
        mapManager = new MapManager(this, mapView);

        mapManager.setBehavior(new MapManager.MapBehavior() {
            @Override
            public void onSetup(MapView map) {

            }

            @Override
            public boolean onMapTap(GeoPoint p, MapView map) {
                return false;
            }

            @Override
            public void onTeardown(MapView map) {

            }
        });

        if (PermissionManager.hasLocationPermissions(this)) {
            mapManager.enableLocation();
        } else {
            PermissionManager.requestLocationPermissions(this);
        }
    }

    private void setupUI() {
        btnLogout = findViewById(R.id.btn_main_logout);
        btnLogout.setOnClickListener(v -> {
            SharedPreferencesUtil.signOutUser(MainActivity.this);
            Intent mainIntent = new Intent(MainActivity.this, LandingActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
        });

        btnAdmin = findViewById(R.id.btn_main_admin);
        btnAdmin.setOnClickListener(v -> startActivity(new Intent(this, AdminActivity.class)));

        btnEditProfile = findViewById(R.id.btn_main_editprofile);
        btnEditProfile.setOnClickListener(v -> startActivity(new Intent(this, UserEditProfileActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapManager.onResume();

        DatabaseService.getInstance().getMinerList(new DatabaseService.DatabaseCallback<List<Miner>>() {
            @Override
            public void onCompleted(List<Miner> minerList) {
                mapManager.updateMinerMarkers(minerList);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(MainActivity.this, "Failed to load miners", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mapManager.onPause();
    }

    // --- Permissions Logic ---
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionManager.REQUEST_CODE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapManager.enableLocation();
            } else {
                Toast.makeText(this, "Location permission is required for the map", Toast.LENGTH_LONG).show();
            }
        }
    }
}