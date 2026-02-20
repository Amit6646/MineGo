package com.example.minego.models;

import androidx.annotation.NonNull;

import org.osmdroid.util.GeoPoint;

import java.util.concurrent.ThreadLocalRandom;

public class Miner {

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
