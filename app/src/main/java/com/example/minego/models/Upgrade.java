package com.example.minego.models;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.minego.R;
import com.example.minego.services.DatabaseService;
import com.example.minego.utils.SharedPreferencesUtil;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

public class Upgrade implements Serializable {
    private static final int MAX_MINE_LEVEL = 3;
    private static final int MAX_RADIUS_LEVEL = 3;
    private static final int MAX_EFFICIENCY = 4;
    private static final int MAX_BACKPACK_SIZE = 4;
    private int MineLevel;
    private int RadiusLevel;
    private int efficiency;
    private int backpacksize;


    public boolean checkprice(Item[] items, List<Item> UserBackPack) {
        if (UserBackPack.isEmpty()) return false;
        if (items == null) return false;
        if (items.length == 0) return false;
        if (MineLevel == MaxUpgradeMineLevel()) return false;

        boolean ok = false;
        for (int i = 0; i < items.length; i++) {
            Item item = items[i];
            for (int j = 0; j < UserBackPack.size(); j++) {
                Item backpackItem = UserBackPack.get(j);
                if (backpackItem.getType().equals(item.getType())) {
                    if (backpackItem.getCount() >= item.getCount()) {
                        ok = true;
                    }
                }
            }
            if (ok == true) {
                ok = false;
            } else {
                return false;
            }
        }
        return true;
    }

    public Upgrade(int mineLevel, int radius, int efficiency, int backpacksize) {
        this.MineLevel = mineLevel;
        this.RadiusLevel = radius;
        this.efficiency = efficiency;
        this.backpacksize = backpacksize;
    }

    public Upgrade() {
    }


    // -- Mine Level --

    @Exclude
    private final int[] mineImages = {
            R.drawable.mineupgradelevel1,
            R.drawable.mineupgradelevel2,
            R.drawable.mineupgradelevel3,
            R.drawable.mineupgradelevel4
    };

    @Exclude
    public boolean UpgradeMineLevel() {
        if (MineLevel > MAX_MINE_LEVEL) {
            return false;
        }

        MineLevel++;
        return true;
    }

    @Exclude
    public boolean UpgradeMineLevel(Context context) {
        if (MineLevel >= MAX_MINE_LEVEL) {
            return false;
        }
        User user = SharedPreferencesUtil.getUser(context);



        MineLevel++;


        updateUpgradeindb(context, null);
        return true;
    }

    @Exclude
    public int getMineLevelImage() {
        int ml = MineLevel;
        if (ml < 0) {
            ml = 0;
        } else if (ml >= mineImages.length) {
            ml = mineImages.length - 1;
        }
        return mineImages[ml];
    }

    @Exclude
    public Item[] PriceMineLevel() {
        Item[] items;
        if (MineLevel == 0) {
            items = new Item[1];
            items[0] = new Item(ItemType.stone, 3);
        }
        else if(MineLevel == 1){
            items = new Item[2];
            items[0] = new Item(ItemType.stone, 5);
            items[1] = new Item(ItemType.iron, 3);
        }
        else if(MineLevel == 2){
            items = new Item[3];
            items[0] = new Item(ItemType.iron, 7);
            items[1] = new Item(ItemType.gold, 5);
            items[2] = new Item(ItemType.ruby, 3);
        }
        else{
            items = null;
        }
        return items;
    }

    @Exclude
    public String getMineUpgradeCostText() {
        String text = "";
        if (MineLevel >= MAX_MINE_LEVEL) {
            return "Max Level";
        } else {
            Item[] items = PriceMineLevel();
            if (items == null) {
                return text;
            } else {
                for (int i = 0; i < items.length; i++) {
                    if (i == items.length - 1){
                        text += items[i].getType() + ":" + items[i].getCount();
                    }
                    else {
                        text += items[i].getType() + ":" + items[i].getCount() + " + ";
                    }
                }

            }

        }
        return text;
    }

    //Radius


    @Exclude
    private final int[] RadiusImages = {
            R.drawable.radiuslevel1,
            R.drawable.radiuslevel2,
            R.drawable.radiuslevel3,
            R.drawable.radiuslevel4
    };

    @Exclude
    public int getRadiusImage() {
        int rl = RadiusLevel;
        if (rl < 0) {
            rl = 0;
        } else if (rl >= RadiusImages.length) {
            rl = RadiusImages.length - 1;
        }
        return RadiusImages[rl];
    }
    @Exclude
    public boolean UpgradeRadius() {
        if (RadiusLevel < MAX_RADIUS_LEVEL) {
            RadiusLevel++;
            return true;
        }
        return false;
    }

    @Exclude
    public boolean UpgradeRadius(Context context) {
        updateUpgradeindb(context, null);
        return false;
    }

    public String getRadiusUpgradeCostText() {
        String text = "";
        if (MineLevel >= MAX_MINE_LEVEL) {
            return "Max Level";
        } else {
            Item[] items = PriceRadius();
            if (items == null) {
                return text;
            } else {
                for (int i = 0; i < items.length; i++) {
                    if (i == items.length - 1){
                        text += items[i].getType() + ":" + items[i].getCount();
                    }
                    else {
                        text += items[i].getType() + ":" + items[i].getCount() + " + ";
                    }
                }

            }

        }
        return text;
    }
    
    public Item[] PriceRadius() {
        Item[] items;
        if (RadiusLevel == 0) {
            items = new Item[1];
            items[0] = new Item(ItemType.stone, 5);
        }
        else if(RadiusLevel == 1){
            items = new Item[2];
            items[0] = new Item(ItemType.stone, 8);
            items[1] = new Item(ItemType.iron, 5);
        }
        else if(RadiusLevel == 2){
            items = new Item[3];
            items[0] = new Item(ItemType.iron, 12);
            items[1] = new Item(ItemType.gold, 9);
            items[2] = new Item(ItemType.ruby, 5);
        }
        else{
            items = null;
        }
        return items;
    }





    @Exclude
    public boolean Upgradeefficiency() {
        if (efficiency < MAX_EFFICIENCY) {
            efficiency++;
            return true;
        }
        return false;
    }

    @Exclude
    public int MaxUpgradeMineLevel() {
        return MAX_MINE_LEVEL;
    }

    @Exclude
    public int MaxUpgradeRadius() {
        return MAX_RADIUS_LEVEL;
    }

    @Exclude
    public int MaxUpgradeEfficiency() {
        return MAX_EFFICIENCY;
    }

    @Exclude
    public int MaxUpgradeBackpack() {
        return  MAX_BACKPACK_SIZE;
    }

    @Exclude
    public boolean Upgradeefficiency(Context context) {
        boolean ok = Upgradeefficiency();
        if (ok) {
            updateUpgradeindb(context, null);
        }
        return ok;
    }

    public int getMineLevel() {
        return MineLevel;
    }

    public void setMineLevel(int mineLevel) {
        MineLevel = mineLevel;
    }

    public int getRadiusLevel() {
        return RadiusLevel;
    }

    public void setRadiusLevel(int radiusLevel) {
        RadiusLevel = radiusLevel;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(int efficiency) {
        this.efficiency = efficiency;
    }

    public int getBackpacksize() {
        return backpacksize;
    }

    public void setBackpacksize(int backpacksize) {
        this.backpacksize = backpacksize;
    }


    //rewards

    @Exclude
    public int getMineHp() {
        int Hp = 2;
        if (MineLevel == 1) {
            Hp = 5;
        } else if (MineLevel == 2) {
            Hp = 10;
        } else if (MineLevel == 3) {
            Hp = 15;
        }
        Hp *= 15;
        return Hp;
    }

    @Exclude
    public int getMineDrop() {
        if (MineLevel == 1) {
            // stone & Iron
            return 2;

        } else if (MineLevel == 2) {
            // Stone & Iron & Gold & Rube
            return 4;

        } else if (MineLevel == 3) {
            // Stone & Iron & Gold & Rube & Diamond
            return 5;
        }
        // stone level 0
        return 1;
    }

    @Exclude
    public int GetRadius() {
        if (RadiusLevel == 1) {
            return 20;
        }
        if (RadiusLevel == 2) {
            return 30;
        }
        if (RadiusLevel == 3) {
            return 45;
        }
        if (RadiusLevel == 4) {
            return 60;
        }
        return 15;
    }



    // database


    /**
     * מעדכן את השדרוג של המשתמש המחובר ב-Firebase (Realtime DB) תחת users/{uid}.
     * אם אין משתמש מחובר/uid, הפעולה לא תתבצע.
     */
    @Exclude
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
    @Exclude
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




}
