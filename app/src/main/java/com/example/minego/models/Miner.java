package com.example.minego.models;

import androidx.annotation.NonNull;

import org.osmdroid.util.GeoPoint;

public class Miner {

    private String id;

    private double lat;
    private double lon;

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
}
