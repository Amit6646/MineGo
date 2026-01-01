package com.example.minego.utils;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public interface MapBehavior {
    // Called when the strategy is attached to the manager
    void onSetup(MapView map);

    // Called when the user taps the map. Returns true if consumed.
    boolean onMapTap(GeoPoint p, MapView map);

    // Called when the activity pauses (cleanup)
    void onTeardown(MapView map);
}
