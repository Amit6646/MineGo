package com.example.minego.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class User {

    public String id;
    public String username;
    public String password;
    public int xp;

    public Backpack backpack;
    public Stats stats;
    public ArrayList<Upgrade> upgrades;

    public User() {
    }

    public User(String id, String username, String password, int xp, Backpack backpack, Stats stats, ArrayList<Upgrade> upgrades) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.xp = xp;
        this.backpack = backpack;
        this.stats = stats;
        this.upgrades = upgrades;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public Backpack getBackpack() {
        return backpack;
    }

    public void setBackpack(Backpack backpack) {
        this.backpack = backpack;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public ArrayList<Upgrade> getUpgrades() {
        return upgrades;
    }

    public void setUpgrades(ArrayList<Upgrade> upgrades) {
        this.upgrades = upgrades;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", xp=" + xp +
                ", backpack=" + backpack +
                ", stats=" + stats +
                ", upgrades=" + upgrades +
                '}';
    }
}
