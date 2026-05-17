package com.example.minego.models;

import androidx.annotation.NonNull;

import java.io.Serializable;


public class User implements Serializable {

    public String id;
    public String username;
    public String password;
    public String email;

    public Backpack backpack;
    public Upgrade upgrade;

    public boolean admin;

    public Gender gender;


    public User() {
    }

    public User(String id, String username, String password, int xp, Backpack backpack,
                 Upgrade upgrade, boolean admin, Gender gender, String email) {

        this.id = id;
        this.admin = admin;
        this.username = username;
        this.password = password;
        this.backpack = backpack;
        this.upgrade = upgrade;
        this.gender = gender;
        this.email = email;

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

    public Backpack getBackpack() {
        return backpack;
    }

    public void setBackpack(Backpack backpack) {
        this.backpack = backpack;
    }

    public Upgrade getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(Upgrade upgrade) {
        this.upgrade = upgrade;
    }

    /**
     * Applies {@link Upgrade#GetBackPackSize()} to {@link Backpack#totalSize}.
     * Capacity is derived from upgrade level; totalSize is not stored in Firebase.
     */
    public void syncBackpackCapacityFromUpgrade() {
        if (upgrade == null) {
            return;
        }
        if (backpack == null) {
            backpack = new Backpack();
        }
        int cap = upgrade.GetBackPackSize();
        int used = backpack.currentSize();
        backpack.totalSize = Math.max(cap, used);
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email=" + email +
                ", backpack=" + backpack +
                ", upgrades=" + upgrade +
                ". admin= " + admin +
                '}';
    }
}
