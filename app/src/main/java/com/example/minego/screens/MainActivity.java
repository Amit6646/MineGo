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
import com.example.minego.models.User;
import com.example.minego.screens.Admin.Admin_UserProfile_activity;
import com.example.minego.screens.Admin.Admin_landing_Activity;
import com.example.minego.services.DatabaseService;
import com.example.minego.utils.MapManager;
import com.example.minego.utils.PermissionManager;
import com.example.minego.utils.SharedPreferencesUtil;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnLogout, btnAdmin, btnEditProfile, btnUpgrade;
    private MapManager mapManager;
    private User user;

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
        mapManager = new MapManager(this, mapView, new MapManager.OnMapManagerListener() {
            @Override
            public void onMinerClick(Miner miner) {
                double distance = miner.distanceToPlayerMeters(
                        mapManager.getPlayerLatitude(),
                        mapManager.getPlayerLongitude());
                Toast.makeText(MainActivity.this, "Distance to player: " + distance + " meters", Toast.LENGTH_SHORT).show();
                if (distance <= (double) user.upgrade.GetRadius()) {
                    startActivity(new Intent(MainActivity.this, Mini_Game_Activity.class));
                }

            }
        });

        if (PermissionManager.hasLocationPermissions(this)) {
            mapManager.enableLocation(MainActivity.this);
        } else {
            PermissionManager.requestLocationPermissions(this);
        }
    }

    private void setupUI() {

        //מגדיר את השחקן לפי המידע ששמור
        user = SharedPreferencesUtil.getUser(this);

        //מגדיר את הכפתור של להתנתק
        //מנתק את השחקן מהאפליקציה ומעביר אותו למסך LandingActivity
        btnLogout = findViewById(R.id.btn_main_logout);
        btnLogout.setOnClickListener(v -> {
            SharedPreferencesUtil.signOutUser(MainActivity.this);
            Intent mainIntent = new Intent(MainActivity.this, LandingActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
        });


        //מגדיר את הכפתור של האדמינים
        //ומציג אותו רק כאשר אתה אדמין
        //כאשר אתה לוחץ על הכפתור זה מעביר אותך למסך Admin_landing_Activity

        btnAdmin = findViewById(R.id.btn_main_admin);
        if (user.isAdmin()) {
            btnAdmin.setVisibility(android.view.View.VISIBLE);
        } else {
            btnAdmin.setVisibility(android.view.View.GONE);
        }
        btnAdmin.setOnClickListener(v -> startActivity(new Intent(this, Admin_landing_Activity.class)));

        //מגדיר את הכפתור של השיפורים
        //כאשר אתה לוחץ על הכפתור זה מעביר אותך למסך pgradesActivity
        btnUpgrade = findViewById(R.id.btn_main_upgrade);
        btnUpgrade.setOnClickListener(v -> startActivity(new Intent(this, UpgradesActivity.class)));


        //מגדיר את הכפתור של עריכת פרופיל
        //כאשר אתה לוחץ על הכפתור זה מעביר אותך למסך admin_UserProfile_activity
        //לפי המידע של המשתמש שלך
        btnEditProfile = findViewById(R.id.btn_main_editprofile);
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Admin_UserProfile_activity.class);

            intent.putExtra("USER_UID", user.getId());
            startActivity(intent);
    

        });
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
                mapManager.enableLocation(MainActivity.this);
            } else {
                Toast.makeText(this, "Location permission is required for the map", Toast.LENGTH_LONG).show();
            }
        }
    }
}