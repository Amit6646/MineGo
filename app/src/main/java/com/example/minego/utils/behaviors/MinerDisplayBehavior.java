package com.example.minego.utils.behaviors;

import com.example.minego.models.Miner;
import com.example.minego.utils.MapBehavior;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import java.util.List;

public class MinerDisplayBehavior implements MapBehavior {

    @Override
    public void onSetup(MapView map) {
        // Maybe clear old non-location markers?
    }

    @Override
    public boolean onMapTap(GeoPoint p, MapView map) {
        // User clicked the map. Maybe deselect something?
        // Return false to let the map handle it (like closing bubbles)
        return false;
    }

    @Override
    public void onTeardown(MapView map) {
        // Cleanup logic
    }

    // Helper to update miners specifically for this behavior
    public void displayMiners(MapView map, List<Miner> miners) {
        // Add markers logic...
        for (Miner m : miners) {
            Marker marker = new Marker(map);
            marker.setPosition(m.asGeoPoint());
            map.getOverlays().add(marker);
        }
        map.invalidate();
    }
}