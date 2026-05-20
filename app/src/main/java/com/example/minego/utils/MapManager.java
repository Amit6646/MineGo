package com.example.minego.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import androidx.core.content.ContextCompat;

import com.example.minego.R;
import com.example.minego.models.Gender;
import com.example.minego.models.Miner;
import com.example.minego.models.User;

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

    private final MapView map;
    private final Context context;
    private final Handler handler = new Handler();
    private final OnMapManagerListener listener;
    private MyLocationNewOverlay locationOverlay;
    private Marker myLocationMarker;
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
    private MapBehavior currentBehavior; // <--- The Injected Logic
    private User user;

    public MapManager(Context context, MapView map, OnMapManagerListener listener) {
        this.context = context;
        this.map = map;
        this.listener = listener;
        initMap();
    }

    private void initMap() {
        //
        map.setTileSource(TileSourceFactory.MAPNIK); // מגדיר את סוג המפה ברקע
        map.setMultiTouchControls(true); // מגדיר שהמפה תעבוד לדגובות של יותר מאצבע אחת
        map.setMaxZoomLevel(21.0); // מגדיר את המקסימום ששחקן יכול לעשות זום
        map.setMinZoomLevel(15.0); // מגדיר את המינימום ששחקן יכול לעשות זום

        IMapController mapController = map.getController();
        mapController.setZoom(17.0); // מגדיר את הזום שבוא השחקן מתחיל
        mapController.setCenter(new GeoPoint(0, 0)); // מגדיר את הנקודה בעולם שבה השחקן מתחיל

        // מוסיף לי מאזין לחיצה על המפה
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (currentBehavior != null) {
                    return currentBehavior.onMapTap(p, map);
                }
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        map.getOverlays().add(new MapEventsOverlay(mReceive));
    }

    public void enableLocation(Context context) {
        // FIX: Initialize the overlay if it is null
        user = SharedPreferencesUtil.getUser(context);


        if (locationOverlay == null) {
            locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map);
            map.getOverlays().add(locationOverlay);

        }

        if (!map.getOverlays().contains(locationOverlay)) {
            map.getOverlays().add(locationOverlay);
        }

        locationOverlay.enableMyLocation();
        locationOverlay.enableFollowLocation();

        locationOverlay.runOnFirstFix(() -> {
            GeoPoint p = locationOverlay.getMyLocation();
            if (p != null) {
                handler.post(() -> {
                    if (myLocationMarker == null) {
                        myLocationMarker = new Marker(map);
                        myLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        map.getOverlays().add(myLocationMarker);
                        SetPlayerGender(user.getGender());
                    }
                    myLocationMarker.setPosition(p);
                    map.getController().animateTo(p);
                });
            }
        });
    }

    private void SetPlayerGender(Gender gender) {
        if (gender == Gender.Male)
            myLocationMarker.setIcon(ContextCompat.getDrawable(context, R.drawable.boyplayer));
        else
            myLocationMarker.setIcon(ContextCompat.getDrawable(context, R.drawable.girlplayer));


    }

    /**
     * קו רוחב (latitude) של השחקן במעלות.
     *
     * @return הערך מ-GPS; {@link Double#NaN} אם שכבת המיקום לא פעילה או שעדיין אין תיקון.
     */
    public double getPlayerLatitude() {
        if (locationOverlay == null) {
            return Double.NaN;
        }
        GeoPoint p = locationOverlay.getMyLocation();
        return p != null ? p.getLatitude() : Double.NaN;
    }

    /**
     * קו אורך (longitude) של השחקן במעלות.
     *
     * @return הערך מ-GPS; {@link Double#NaN} אם שכבת המיקום לא פעילה או שעדיין אין תיקון.
     */
    public double getPlayerLongitude() {
        if (locationOverlay == null) {
            return Double.NaN;
        }
        GeoPoint p = locationOverlay.getMyLocation();
        return p != null ? p.getLongitude() : Double.NaN;
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
            marker.setIcon(scaledDrawable(R.drawable.mine1, 96));
            //marker.setIcon(ContextCompat.getDrawable(context, R.drawable.mine1));


            map.getOverlays().add(marker);
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    OnClickMine(m);
                    return false;
                }
            });


        }
        map.invalidate();
    }


    private Drawable scaledDrawable(int drawableRes, int sizeDp) {
        Drawable d = ContextCompat.getDrawable(context, drawableRes);
        if (d == null) return null;

        int sizePx = (int) (sizeDp * context.getResources().getDisplayMetrics().density);

        Bitmap b = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        d.setBounds(0, 0, sizePx, sizePx);
        d.draw(c);

        return new BitmapDrawable(context.getResources(), b);
    }

    public void OnClickMine(Miner miner) {
        listener.onMinerClick(miner);
    }

    public void onResume() {
        map.onResume();
        if (locationOverlay != null) {
            locationOverlay.enableMyLocation();
            handler.post(locationUpdater);
        }
    }

    public void onPause() {
        handler.removeCallbacks(locationUpdater);
        if (locationOverlay != null) locationOverlay.disableMyLocation();
        map.onPause();
    }

    public interface OnMapManagerListener {
        void onMinerClick(Miner miner);
    }
}