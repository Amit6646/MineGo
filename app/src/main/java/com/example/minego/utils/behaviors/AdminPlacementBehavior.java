package com.example.minego.utils.behaviors;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.example.minego.utils.MapBehavior;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class AdminPlacementBehavior implements MapBehavior {
    private Marker placementMarker;
    private final Drawable icon;
    private GeoPoint selectedPosition;

    public AdminPlacementBehavior(Context context, int iconResId) {
        this.icon = context.getDrawable(iconResId);
    }

    @Override
    public void onSetup(MapView map) {
        placementMarker = new Marker(map);
        placementMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        placementMarker.setIcon(icon);
    }

    @Override
    public boolean onMapTap(GeoPoint p, MapView map) {
        // Update position logic
        this.selectedPosition = p;
        placementMarker.setPosition(p);

        if (!map.getOverlays().contains(placementMarker)) {
            map.getOverlays().add(placementMarker);
        }

        map.invalidate();
        return true; // We handled the tap
    }

    @Override
    public void onTeardown(MapView map) {
        if (placementMarker != null) {
            map.getOverlays().remove(placementMarker);
        }
    }

    public GeoPoint getSelectedPosition() {
        return selectedPosition;
    }
}