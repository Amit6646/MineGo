package com.example.minego.models;

import com.google.firebase.database.Exclude;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Backpack {
    public String name;
    public int totalSize;
    private List<Item> items;

    public Backpack() {
    }

    public Backpack(String name, int totalSize) {
        this.name = name;
        this.totalSize = totalSize;
        items = new ArrayList<>();
    }

    public Backpack(String name, int totalSize, List<Item> items) {
        this.name = name;
        this.totalSize = totalSize;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!canAddItemToBackpack(item)) return false;

        for (int i = 0; i < items.size(); i++) {
            Item backpackItem = items.get(i);
            if (backpackItem.getType().equals(item.getType())) {
                // backpackItem = מה שיש לי כבר
                // item = מה שאני רוצה להוסיף
                backpackItem.increaseCount(item.getCount());
                return true;
            }
        }
        items.add(item);
        return true;
    }

    @Exclude
    public void removeItem(Item item) {

        // remove the item from the list
        boolean isRemoved = items.removeIf(new Predicate<Item>() {
            @Override
            public boolean test(Item it) {
                if (!it.getType().equals(item.getType())) return false;
                return it.getCount() <= item.getCount();
            }
        });

        if (isRemoved) return;

        // modify the list by the given item
        for (int i = 0; i < items.size(); i++) {
            Item it = items.get(i);
            if (!it.getType().equals(item.getType())) continue;

            it.decreaseCount(item.getCount());
            return;
        }

    }

    @Exclude
    public boolean isFull() {
        return currentSize() == totalSize;
    }

    @Exclude
    private boolean canAddItemToBackpack(Item item) {
        int total = currentSize();
        total += item.getCount();
        return total <= totalSize;
    }

}
