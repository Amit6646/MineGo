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
        adminmap.setTileSource(TileSourceFactory.MAPNIK);
        adminmap.setBuiltInZoomControls(true);
        adminmap.setMultiTouchControls(true);
        adminmap.setMaxZoomLevel(21.0);
        adminmap.setMinZoomLevel(8.0);
        IMapController mapController = adminmap.getController();
        mapController.setZoom(18.5);

        Marker marker = new Marker(adminmap);
        marker.setIcon(getDrawable(R.drawable.girlplayer));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        // Set a fallback starting point until we have a valid location
        mapController.setCenter(new GeoPoint(31.9703, 34.7790));


        //your items
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        items.add(new OverlayItem("Title", "Description", new GeoPoint(0.0d,0.0d))); // Lat/Lon decimal degrees

        marker.setOnMarkerClickListener((marker1, mapView) -> {
            Toast.makeText(AdminActivity.this, "", Toast.LENGTH_SHORT).show();
            return true;
        });

        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                double lat = p.getLatitude();
                double lon = p.getLongitude();

                //Toast.makeText(AdminActivity.this,
                //        "Lat: " + lat + ", Lon: " + lon,
                //        Toast.LENGTH_SHORT).show();
                marker.setPosition(new GeoPoint(lat, lon));
                adminmap.getOverlays().add(marker);
                adminmap.invalidate();
                markerloc = true;


                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        MapEventsOverlay OverlayEvents = new MapEventsOverlay(mReceive);
        adminmap.getOverlays().add(OverlayEvents);


        btnSave = findViewById(R.id.btn_admin_save);
        btnSave.setOnClickListener(v -> {
            // TODO change to miner
            String id = DatabaseService.getInstance().generateMinerId();
            Miner miner = new Miner(id, marker.getPosition());
            adminmap.getOverlays().remove(marker);
            adminmap.invalidate();
            createMinerInDatabase(miner);
            markerloc = false;
        });


    }
    private void createMinerInDatabase(Miner miner) {
        if (markerloc == false)
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