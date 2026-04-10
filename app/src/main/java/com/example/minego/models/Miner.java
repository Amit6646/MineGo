package com.example.minego.models;

import androidx.annotation.NonNull;

import org.osmdroid.util.GeoPoint;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Miner {

    /** רדיוס כדור הארץ במטרים (שימוש בנוסחת Haversine) */
    private static final double EARTH_RADIUS_METERS = 6371000.0;

    private String id;

    private double lat;
    private double lon;
    private Upgrade upgrade;


    public Miner() {
    }

    public Miner(String id, double lat, double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public Miner(String id, GeoPoint geoPoint) {
        this.id = id;
        this.lat = geoPoint.getLatitude();
        this.lon = geoPoint.getLongitude();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @NonNull
    @Override
    public String toString() {
        return "Miner{" +
                "id='" + id + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }

    public GeoPoint asGeoPoint() {
        return new GeoPoint(this.lat, this.lon);
    }

    /**
     * מרחק במטרים בין שתי נקודות על פני כדור הארץ.
     * <p>
     * נוסחת Haversine:
     * <pre>
     *   φ₁, φ₂ — קו רוחב ברדיאנים;  Δφ = φ₂ − φ₁;  Δλ — הפרש אורך ברדיאנים
     *   a = sin²(Δφ/2) + cos φ₁ · cos φ₂ · sin²(Δλ/2)
     *   c = 2 · atan2( √a, √(1−a) )
     *   d = R · c        (R = רדיוס כדור הארץ במטרים)
     * </pre>
     *
     * @param lat1 קו רוחב נקודה ראשונה (מעלות)
     * @param lon1 קו אורך נקודה ראשונה (מעלות)
     * @param lat2 קו רוחב נקודה שנייה (מעלות)
     * @param lon2 קו אורך נקודה שנייה (מעלות)
     * @return מרחק קווי על פני השטח במטרים
     */
    public static double haversineDistanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double sinHalfDeltaPhi = Math.sin(deltaPhi / 2.0);
        double sinHalfDeltaLambda = Math.sin(deltaLambda / 2.0);
        double a = sinHalfDeltaPhi * sinHalfDeltaPhi
                + Math.cos(phi1) * Math.cos(phi2) * sinHalfDeltaLambda * sinHalfDeltaLambda;
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return EARTH_RADIUS_METERS * c;
    }

    /** מרחק במטרים מהמכרה הזה עד לשחקן (קו רוחב / אורך במעלות). */
    public double distanceToPlayerMeters(double playerLat, double playerLon) {
        return haversineDistanceMeters(playerLat, playerLon, lat, lon);
    }

    /** מרחק במטרים מהמכרה הזה עד לנקודת שחקן במפה. */
    public double distanceToPlayerMeters(GeoPoint playerLocation) {
        if (playerLocation == null) {
            return Double.NaN;
        }
        return distanceToPlayerMeters(playerLocation.getLatitude(), playerLocation.getLongitude());
    }

    public Item GetItemDrop()
    {
        Item item = new Item();
        int level = upgrade.getMineDrop();
        int random = ThreadLocalRandom.current().nextInt(1, 101);

        ItemType selectedType = determineType(level, random);

        item.setType(selectedType);
        item.setCount(ThreadLocalRandom.current().nextInt(1, 3));
        return item;

    }

    private ItemType determineType(int level, int random) {
        switch (level) {
            case 1:
                return ItemType.stone; // 100%

            case 2:
                if (random <= 70) return ItemType.stone; // 70%
                return ItemType.iron; // 30%

            case 3:
                if (random <= 50) return ItemType.stone; // 50%
                if (random <= 80) return ItemType.iron; // 30%
                return ItemType.gold; // 20%

            case 4:
                if (random <= 40) return ItemType.stone; // 40%
                if (random <= 70) return ItemType.iron; // 30%
                if (random <= 90) return ItemType.gold; // 20%
                return ItemType.ruby; // 10%

            case 5:
                if (random <= 35) return ItemType.stone; // 35%
                if (random <= 60) return ItemType.iron; // 25%
                if (random <= 80) return ItemType.gold; // 20%
                if (random <= 92) return ItemType.ruby; // 12%
                return ItemType.diamond; // 8%

            default:
                return ItemType.stone;
        }
    }
}
