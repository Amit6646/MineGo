package com.example.minego.models;

import java.util.List;
import java.util.function.Predicate;

public class Backpack {
    public String name;
    public int totalSize;
    private List<Item> items;



    public int currentSize() {
        int total = 0;
        for (Item item : items) {
            total += item.count;
        }
        return total;
    }

    public boolean addItem(Item item) {
        if (!canAddItemToBackpack(item)) return false;

        for (int i = 0; i < items.size(); i++) {
            Item item1 = items.get(i);
            if (item1.name.equals(item.name)) {
                item1.count += item.count;
                return true;
            }
        }
        items.add(item);
        return true;
    }

    public void removeItem(Item item) {

        // remove the item from the list
        boolean isRemoved = items.removeIf(new Predicate<Item>() {
            @Override
            public boolean test(Item it) {
                if (!it.name.equals(item.name)) return false;
                return it.count <= item.count;
            }
        });

        if (isRemoved) return;

        // modify the list by the given item
        for (int i = 0; i < items.size(); i++) {
            Item it = items.get(i);
            if (!it.name.equals(item.name)) continue;

            it.count -= item.count;
            return;
        }

    }

    public boolean isFull() {
        return currentSize() == totalSize;
    }

    private boolean canAddItemToBackpack(Item item) {
        int total = currentSize();
        total += item.count;
        return total <= totalSize;
    }
}
