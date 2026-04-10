package com.example.minego.models;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.minego.services.DatabaseService;
import com.example.minego.utils.SharedPreferencesUtil;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Upgrade implements Serializable {
    private static final int MAX_MINE_LEVEL = 3;
    private static final int MAX_RADIUS_LEVEL = 4;
    private static final int MAX_EFFICIENCY = 4;
    private static final int MAX_TIME = 4;

    private int MineLevel;
    private int Radius_Level;
    private int efficiency;
    private int time;

    public Upgrade() {
    }

    public Upgrade(int mineLevel, int radius, int efficiency, int time) {
        MineLevel = mineLevel;
        Radius_Level = radius;
        this.efficiency = efficiency;
        this.time = time;
    }

    public boolean UpgradeMineLevel() {
        if (MineLevel < MAX_MINE_LEVEL) {
            MineLevel++;
            return true;
        }
        return false;
    }

    public boolean UpgradeMineLevel(Context context) {
        boolean ok = UpgradeMineLevel();
        if (ok) {
            updateUpgradeindb(context, null);
        }
        return ok;
    }

    public boolean UpgradeRadius() {
        if (Radius_Level < MAX_RADIUS_LEVEL) {
            Radius_Level++;
            return true;
        }
        return false;
    }

    public boolean UpgradeRadius(Context context) {
        boolean ok = UpgradeRadius();
        if (ok) {
            updateUpgradeindb(context, null);
        }
        return ok;
    }

    public boolean Upgradeefficiency() {
        if (efficiency < MAX_EFFICIENCY) {
            efficiency++;
            return true;
        }
        return false;
    }

    public int MaxUpgradeMineLevel() {
        return MAX_MINE_LEVEL;
    }

    public int MaxUpgradeRadius() {
        return MAX_RADIUS_LEVEL;
    }

    public int MaxUpgradeEfficiency() {
        return MAX_EFFICIENCY;
    }

    public int MaxUpgradeTime() {
        return MAX_TIME;
    }

    public boolean Upgradeefficiency(Context context) {
        boolean ok = Upgradeefficiency();
        if (ok) {
            updateUpgradeindb(context, null);
        }
        return ok;
    }

    /**
     * מעדכן את השדרוג של המשתמש המחובר ב-Firebase (Realtime DB) תחת users/{uid}.
     * אם אין משתמש מחובר/uid, הפעולה לא תתבצע.
     */
    public void updateUpgradeindb(Context context, @Nullable DatabaseService.DatabaseCallback<User> callback) {
        User localUser = SharedPreferencesUtil.getUser(context);
        if (localUser == null || localUser.getId() == null || localUser.getId().isEmpty()) {
            if (callback != null) {
                callback.onCompleted(null);
            }
            return;
        }
        updateUpgradeindb(localUser.getId(), context, callback);
    }

    /**
     * מעדכן את השדרוג של משתמש מסוים ב-Firebase (Realtime DB) תחת users/{uid}.
     * הפעולה מתבצעת באמצעות טרנזקציה כדי לא לדרוס שדות אחרים.
     */
    public void updateUpgradeindb(String uid, @Nullable Context contextForLocalSave, @Nullable DatabaseService.DatabaseCallback<User> callback) {
        if (uid == null || uid.isEmpty()) {
            if (callback != null) {
                callback.onCompleted(null);
            }
            return;
        }

        DatabaseService.getInstance().updateUser(uid, currentUser -> {
            if (currentUser == null) {
                return null;
            }
            currentUser.setUpgrade(this);
            return currentUser;
        }, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User updatedUser) {
                if (contextForLocalSave != null && updatedUser != null) {
                    SharedPreferencesUtil.saveUser(contextForLocalSave, updatedUser);
                }
                if (callback != null) {
                    callback.onCompleted(updatedUser);
                }
            }

            @Override
            public void onFailed(Exception e) {
                if (callback != null) {
                    callback.onFailed(e);
                }
            }
        });
    }

    public int getMineLevel() {
        return MineLevel;
    }

    public void setMineLevel(int mineLevel) {
        MineLevel = mineLevel;
    }

    public int getRadius_Level() {
        return Radius_Level;
    }

    /**
     * Firebase Realtime DB (JavaBeans) expects the setter name to match the getter.
     * We keep this method to ensure {@code Radius_Level} is properly deserialized from DB.
     */
    public void setRadius_Level(int radius) {
        Radius_Level = radius;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(int efficiency) {
        this.efficiency = efficiency;
    }

    @Exclude
    public int getMineHp () {
        int Hp = 2;
        if (MineLevel == 1) {
            Hp = 5;
        }
        else if (MineLevel == 2) {
            Hp = 10;
        }
        else if (MineLevel == 3) {
            Hp = 15;
        }
        Hp *= 15;
        return Hp;
    }

    @Exclude
    public int getMineDrop () {
        if (MineLevel == 1) {
            // stone & Iron
            return 2;

        }
        else if (MineLevel == 2) {
            // Stone & Iron & Gold & Rube
            return 4;

        }
        else if (MineLevel == 3) {
            // Stone & Iron & Gold & Rube & Diamond
            return 5;
        }
        return 0;
    }
    public int GetRadius () {
        if (Radius_Level == 1) {
            return 20;
        }
        if (Radius_Level == 2) {
            return 30;
        }
        if (Radius_Level == 3) {
            return 45;
        }
        if (Radius_Level == 4) {
            return 60;
        }
        return 15;
    }
}
