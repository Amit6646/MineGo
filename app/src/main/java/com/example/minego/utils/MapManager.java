package com.example.minego.utils;

import android.content.Context;
import android.os.Handler;
import com.example.minego.models.Miner;
import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.List;

public class MapManager {

    public interface MapBehavior {
        // Called when the strategy is attached to the manager
        void onSetup(MapView map);

        // Called when the user taps the map. Returns true if consumed.
        boolean onMapTap(GeoPoint p, MapView map);

        // Called when the activity pauses (cleanup)
        void onTeardown(MapView map);
    }

    private final MapView map;
    private final Context context;
    private MyLocationNewOverlay locationOverlay;
    private Marker myLocationMarker;
    private MapBehavior currentBehavior; // <--- The Injected Logic
    private final Handler handler = new Handler();

    public MapManager(Context context, MapView map) {
        this.context = context;
        this.map = map;
        initMap();
    }

    private void initMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setMaxZoomLevel(21.0);
        map.setMinZoomLevel(8.0);

        IMapController mapController = map.getController();
        mapController.setZoom(18.5);

        // Global Event Receiver that delegates to the Interface
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (currentBehavior != null) {
                    return currentBehavior.onMapTap(p, map);
                }
                return false;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) { return false; }
        };
        map.getOverlays().add(new MapEventsOverlay(mReceive));
    }

    private final Runnable locationUpdater = new Runnable() {
        @Override
        public void run() {
            if (locationOverlay != null && myLocationMarker != null) {
                GeoPoint p = locationOverlay.getMyLocation();
                if (p != null) {
                    myLocationMarker.setPosition(p);
                    map.invalidate();
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

    /**
     * INJECT YOUR LOGIC HERE
     */
    public void setBehavior(MapBehavior behavior) {
        // Cleanup old behavior if exists
        if (this.currentBehavior != null) {
            this.currentBehavior.onTeardown(map);
        }

        this.currentBehavior = behavior;

        // Setup new behavior
        if (this.currentBehavior != null) {
            this.currentBehavior.onSetup(map);
        }
    }

    public MapBehavior getBehavior() {
        return currentBehavior;
    }

    public void enableLocation() {
        if (!map.getOverlays().contains(locationOverlay)) {
            map.getOverlays().add(locationOverlay);
        }
        locationOverlay.enableMyLocation();

        locationOverlay.runOnFirstFix(() -> {
            GeoPoint p = locationOverlay.getMyLocation();
            if (p != null) {
                handler.post(() -> {
                    if (myLocationMarker == null) {
                        myLocationMarker = new Marker(map);
                        myLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        map.getOverlays().add(myLocationMarker);
                    }
                    myLocationMarker.setPosition(p);
                    map.getController().animateTo(p);
                });
            }
        });
    }

    public void updateMinerMarkers(List<Miner> miners) {
        // Clear all markers but KEEP the location overlay and user marker
        map.getOverlays().clear();

        if (locationOverlay != null) map.getOverlays().add(locationOverlay);
        if (myLocationMarker != null) map.getOverlays().add(myLocationMarker);

        for (Miner m : miners) {
            Marker marker = new Marker(map);
            marker.setPosition(m.asGeoPoint());
            // You can add more customization here (icons, titles, etc.)
            map.getOverlays().add(marker);
        }
        map.invalidate();
    }

    public void onResume() {
        map.onResume();
        if (locationOverlay != null) locationOverlay.enableMyLocation();
        handler.post(locationUpdater);
    }

    public void onPause() {
        handler.removeCallbacks(locationUpdater);
        if (locationOverlay != null) locationOverlay.disableMyLocation();
        map.onPause();
    }
}
