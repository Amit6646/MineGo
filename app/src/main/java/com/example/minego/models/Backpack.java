package com.example.minego.models;

import com.google.firebase.database.Exclude;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Backpack {

    @Exclude
    public int totalSize;
    private List<Item> items;

    public Backpack() {
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        if (totalSize < 0) throw new IllegalArgumentException("Total size cannot be negative.");
        if (totalSize < currentSize())
            throw new IllegalArgumentException("Total size must be greater than the current size.");
        this.totalSize = totalSize;
    }

    public List<Item> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Exclude
    public int currentSize() {
        int total = 0;
        for (Item item : getItems()) {
            total += item.getCount();
        }
        return total;
    }

    @Exclude
    public boolean addItem(@NotNull final Item item) {
        //בודק עם יש לי מספיק מקום בתיק בשביל להוסיף את הפריטים
        if (!canAddItemToBackpack(item)) return false;

        //עובר על כל התאים שיש לי בתיק ומחפס תא את הפריט שיש לי
        for (int i = 0; i < items.size(); i++) {
            //בגדיר את הפריט במקום מסויים כ backpackItem
            Item backpackItem = items.get(i);

            //בודק עם הפריט שיש לי והפריט בתא מסויים זה אותו פריט
            if (backpackItem.getType().equals(item.getType())) {
                //מוסיף לי את הפריט לתיק גב
                backpackItem.increaseCount(item.getCount());
                return true;
            }
        }

        //יוצא לי תא חדש בתיק בשביל הפריט הזה
        items.add(item);
        return true;
    }

    @Exclude
    public void removeItem(Item item) {
        boolean isRemoved = items.removeIf(new Predicate<Item>() {
            @Override
            public boolean test(Item it) {
                if (!it.getType().equals(item.getType())) return false;
                return it.getCount() <= item.getCount();
            }
        });

        if (isRemoved) return;

        for (int i = 0; i < items.size(); i++) {
            Item it = items.get(i);
            if (!it.getType().equals(item.getType())) continue;

            it.decreaseCount(item.getCount());
            return;
        }
    }

    @Exclude
    private boolean canAddItemToBackpack(Item item) {
        int total = currentSize();
        total += item.getCount();
        return total <= totalSize;
    }

}
