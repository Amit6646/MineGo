package com.example.minego.models;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.minego.R;
import com.example.minego.services.DatabaseService;
import com.example.minego.utils.SharedPreferencesUtil;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Upgrade implements Serializable {


    private static final int MAX_MINE_LEVEL = 3;
    private static final int MAX_RADIUS_LEVEL = 3;
    private static final int MAX_EFFICIENCY = 4;
    private static final int MAX_BACKPACK_SIZE = 3;
    @Exclude
    private final int[] mineImages = {
            R.drawable.mineupgradelevel1,
            R.drawable.mineupgradelevel2,
            R.drawable.mineupgradelevel3,
            R.drawable.mineupgradelevel4
    };
    @Exclude
    private final int[] RadiusImages = {
            R.drawable.radiuslevel1,
            R.drawable.radiuslevel2,
            R.drawable.radiuslevel3,
            R.drawable.radiuslevel4
    };
    @Exclude
    private final int[] efficiencyImages = {
            R.drawable.efficiencyupgradelevel1,
            R.drawable.efficiencyupgradelevel2,
            R.drawable.efficiencyupgradelevel3,
            R.drawable.efficiencyupgradelevel4,
            R.drawable.efficiencyupgradelevel5
    };

    @Exclude
    private final int[] backpackImages = {
            R.drawable.backpackupgradelevel1,
            R.drawable.backpackupgradelevel2,
            R.drawable.backpackupgradelevel3,
            R.drawable.backpackupgradelevel4
    };
    private int MineLevel;
    private int RadiusLevel;
    private int efficiency;
    private int backpacksize;
    @Exclude
    private transient User pendingUserForDbMerge;

    public Upgrade(int mineLevel, int radius, int efficiency, int backpacksize) {
        this.MineLevel = mineLevel;
        this.RadiusLevel = radius;
        this.efficiency = efficiency;
        this.backpacksize = backpacksize;
    }


    // -- Mine Level --

    public Upgrade() {
    }

    public boolean checkprice(Item[] items, List<Item> UserBackPack) {
        if (UserBackPack.isEmpty()) return false;
        if (items == null) return false;
        if (items.length == 0) return false;

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

    private boolean takeprice(Item[] price, Backpack backpack) {
        if (!checkprice(price, backpack.getItems())) return false;

        List<Item> items = backpack.getItems();

        for (int i = 0; i < price.length; i++) {
            Item itemprice = price[i];
            for (int j = 0; j < items.size(); j++) {
                if (itemprice.getType().equals(items.get(j).getType())) {
                    items.get(j).removeCount(itemprice.getCount());
                }

            }

        }
        backpack.setItems(items);

        return true;
    }

    @Exclude
    public boolean UpgradeMineLevel(Context context) {
        if (MineLevel >= MAX_MINE_LEVEL) {
            return false;
        }
        User user = SharedPreferencesUtil.getUser(context);

        Item[] items = PriceMineLevel();
        if (!takeprice(items, user.getBackpack())) return false;

        this.pendingUserForDbMerge = user;

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

    //Radius

    @Exclude
    public Item[] PriceMineLevel() {
        Item[] items;
        if (MineLevel == 0) {
            items = new Item[1];
            items[0] = new Item(ItemType.stone, 3);
        } else if (MineLevel == 1) {
            items = new Item[2];
            items[0] = new Item(ItemType.stone, 5);
            items[1] = new Item(ItemType.iron, 3);
        } else if (MineLevel == 2) {
            items = new Item[3];
            items[0] = new Item(ItemType.iron, 7);
            items[1] = new Item(ItemType.gold, 5);
            items[2] = new Item(ItemType.ruby, 3);
        } else {
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
                    if (i == items.length - 1) {
                        text += items[i].getType() + ":" + items[i].getCount();
                    } else {
                        text += items[i].getType() + ":" + items[i].getCount() + " + ";
                    }
                }

            }

        }
        return text;
    }

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
    public boolean UpgradeRadius(Context context) {
        if (RadiusLevel >= MAX_RADIUS_LEVEL) {
            return false;
        }
        User user = SharedPreferencesUtil.getUser(context);

        Item[] items = PriceRadius();
        if (!takeprice(items, user.getBackpack())) return false;

        this.pendingUserForDbMerge = user;

        RadiusLevel++;

        updateUpgradeindb(context, null);
        return true;
    }

    @Exclude
    public String getRadiusUpgradeCostText() {
        String text = "";
        if (RadiusLevel >= MAX_RADIUS_LEVEL) {
            return "Max Level";
        } else {
            Item[] items = PriceRadius();
            if (items == null) {
                return text;
            } else {
                for (int i = 0; i < items.length; i++) {
                    if (i == items.length - 1) {
                        text += items[i].getType() + ":" + items[i].getCount();
                    } else {
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
        } else if (RadiusLevel == 1) {
            items = new Item[2];
            items[0] = new Item(ItemType.stone, 8);
            items[1] = new Item(ItemType.iron, 5);
        } else if (RadiusLevel == 2) {
            items = new Item[3];
            items[0] = new Item(ItemType.iron, 12);
            items[1] = new Item(ItemType.gold, 9);
            items[2] = new Item(ItemType.ruby, 5);
        } else {
            items = null;
        }
        return items;
    }


    //efficiency


    @Exclude
    public int getefficiencyImages() {
        int el = efficiency;
        if (el < 0) {
            el = 0;
        } else if (el >= efficiencyImages.length) {
            el = efficiencyImages.length - 1;

        }
        return efficiencyImages[el];
    }

    @Exclude
    public boolean Upgradeefficiency(Context context) {
        if (efficiency >= MAX_EFFICIENCY) {
            return false;
        }
        User user = SharedPreferencesUtil.getUser(context);

        Item[] items = PriceEfficiency();
        if (!takeprice(items, user.getBackpack())) return false;

        this.pendingUserForDbMerge = user;

        efficiency++;


        updateUpgradeindb(context, null);
        return true;
    }

    public Item[] PriceEfficiency() {
        Item[] items;
        if (efficiency == 0) {
            items = new Item[1];
            items[0] = new Item(ItemType.stone, 12);
        } else if (efficiency == 1) {
            items = new Item[2];
            items[0] = new Item(ItemType.stone, 10);
            items[1] = new Item(ItemType.iron, 8);
        } else if (efficiency == 2) {
            items = new Item[3];
            items[0] = new Item(ItemType.iron, 16);
            items[1] = new Item(ItemType.gold, 12);
            items[2] = new Item(ItemType.ruby, 8);
        } else if (efficiency == 3) {
            items = new Item[4];
            items[0] = new Item(ItemType.iron, 20);
            items[1] = new Item(ItemType.gold, 12);
            items[2] = new Item(ItemType.ruby, 10);
            items[3] = new Item(ItemType.diamond, 6);

        } else {
            items = null;
        }
        return items;
    }


    @Exclude
    public String getEfficiencyUpgradeCostText() {
        String text = "";
        if (efficiency >= MAX_EFFICIENCY) {
            return "Max Level";
        } else {
            Item[] items = PriceEfficiency();
            if (items == null) {
                return text;
            } else {
                for (int i = 0; i < items.length; i++) {
                    if (i == items.length - 1) {
                        text += items[i].getType() + ":" + items[i].getCount();
                    } else {
                        text += items[i].getType() + ":" + items[i].getCount() + " + ";
                    }
                }

            }

        }
        return text;
    }

 // --------------------------------------

    @Exclude
    public int getBackpackImage() {
        int bpl = backpacksize;
        if (bpl < 0) {
            bpl = 0;
        } else if (bpl >= backpackImages.length) {
            bpl = backpackImages.length - 1;
        }
        return backpackImages[bpl];
    }

    @Exclude
    public boolean UpgradeBackPack(Context context) {
        if (backpacksize >= MAX_BACKPACK_SIZE) {
            return false;
        }
        User user = SharedPreferencesUtil.getUser(context);

        Item[] items = priceBackpack();
        if (!takeprice(items, user.getBackpack())) return false;

        this.pendingUserForDbMerge = user;

        backpacksize++;
        user.syncBackpackCapacityFromUpgrade();

        updateUpgradeindb(context, null);
        return true;
    }


    public Item[] priceBackpack() {
        Item[] items;
        if (backpacksize == 0) {
            items = new Item[2];
            items[0] = new Item(ItemType.stone, 5);
            items[1] = new Item(ItemType.iron, 2);

        } else if (backpacksize == 1) {
            items = new Item[2];
            items[0] = new Item(ItemType.iron, 8);
            items[1] = new Item(ItemType.gold, 5);
        } else if (backpacksize == 2) {
            items = new Item[3];
            items[0] = new Item(ItemType.iron, 20);
            items[1] = new Item(ItemType.gold, 12);
            items[2] = new Item(ItemType.ruby, 8);
            items[2] = new Item(ItemType.diamond, 5);

        }
        else {
            items = null;
        }
        return items;
    }
    @Exclude
    public String getbackpackUpgradeCostText() {
        String text = "";
        if (backpacksize >= MAX_BACKPACK_SIZE) {
            return "Max Level";
        } else {
            Item[] items = priceBackpack();
            if (items == null) {
                return text;
            } else {
                for (int i = 0; i < items.length; i++) {
                    if (i == items.length - 1) {
                        text += items[i].getType() + ":" + items[i].getCount();
                    } else {
                        text += items[i].getType() + ":" + items[i].getCount() + " + ";
                    }
                }

            }

        }
        return text;
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
        return MAX_BACKPACK_SIZE;
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
        return 15;
    }

    @Exclude
    public int GetEfficiencyReward() {
        if (efficiency == 1) {
            return 2;
        }
        if (efficiency == 2) {
            return 3;
        }
        if (efficiency == 3) {
            return 4;
        }
        if (efficiency == 4) {
            return 5;
        }
        return 1;
    }


    @Exclude
    public int GetBackPackSize() {
        if (backpacksize == 1) {
            return 15;
        }
        if (backpacksize == 2) {
            return 20;
        }
        if (backpacksize == 3) {
            return 40;
        }
        return 10;
    }


    @Exclude
    public Item GetItemDrop() {
        Item item = new Item();

        int level = getMineDrop();
        int random = ThreadLocalRandom.current().nextInt(1, 101);
        item.setType(determineType(level, random));
        item.setCount(ThreadLocalRandom.current().nextInt(1, 4));
        return item;
    }


    @Exclude

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

    // database


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

    
    @Exclude
    public void updateUpgradeindb(String uid, @Nullable Context contextForLocalSave, @Nullable DatabaseService.DatabaseCallback<User> callback) {
        if (uid == null || uid.isEmpty()) {
            if (callback != null) {
                callback.onCompleted(null);
            }
            return;
        }

        final User mergeSource = this.pendingUserForDbMerge;

        DatabaseService.getInstance().updateUser(uid, currentUser -> {
            if (currentUser == null) {
                return null;
            }
            currentUser.setUpgrade(this);
            if (mergeSource != null && mergeSource.getBackpack() != null) {
                currentUser.setBackpack(mergeSource.getBackpack());
            }
            currentUser.syncBackpackCapacityFromUpgrade();
            return currentUser;
        }, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User updatedUser) {
                pendingUserForDbMerge = null;
                if (contextForLocalSave != null && updatedUser != null) {
                    SharedPreferencesUtil.saveUser(contextForLocalSave, updatedUser);
                }
                if (callback != null) {
                    callback.onCompleted(updatedUser);
                }
            }

            @Override
            public void onFailed(Exception e) {
                pendingUserForDbMerge = null;
                if (callback != null) {
                    callback.onFailed(e);
                }
            }
        });
    }


}
