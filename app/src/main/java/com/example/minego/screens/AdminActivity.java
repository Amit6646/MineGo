package com.example.minego.screens;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minego.R;
import com.example.minego.models.Miner;
import com.example.minego.services.DatabaseService;
import com.example.minego.utils.MapManager;
import com.example.minego.utils.behaviors.AdminPlacementBehavior;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {
    private MapView adminmap;
    private MapManager mapManager;
    private AdminPlacementBehavior adminBehavior;
    Button  btnSave;
    boolean markerloc = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize MapView
        adminmap = findViewById(R.id.adminmap);
        mapManager = new MapManager(this, adminmap);

        adminBehavior = new AdminPlacementBehavior(this, R.drawable.girlplayer);
        mapManager.setBehavior(adminBehavior);


        btnSave = findViewById(R.id.btn_admin_save);
        btnSave.setOnClickListener(v -> {
            // TODO change to miner
            GeoPoint pos = adminBehavior.getSelectedPosition();
            String id = DatabaseService.getInstance().generateMinerId();
            Miner miner = new Miner(id, pos);
//            adminmap.getOverlays().remove(marker);
//            adminmap.invalidate();
            createMinerInDatabase(miner);
            markerloc = false;
        });


    }
    private void createMinerInDatabase(Miner miner) {
        if (!markerloc)
        {
            Toast.makeText(AdminActivity.this, "אתה צריך לבחור מיקום בשביל ליצור מכרה", Toast.LENGTH_SHORT).show();
        }
        else {
            DatabaseService databaseService = DatabaseService.getInstance();
            databaseService.createNewMiner(miner, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                }

                @Override
                public void onFailed(Exception e) {
                }
            });
        }
    }
}