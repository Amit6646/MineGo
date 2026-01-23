package com.example.minego.models;

import java.util.List;

public class Backpack {
    public String name;
    public int BackPackSize;
    private List<Item> items;





    public int totalItems(List<Item> items) {
        int count = 0;

        for (int i = 0; i < items.size(); i++)
        {
            count += items.get(i).count;
        }
        return count;
    }

    public boolean addItem(Item item) {
        if (isFull()) return false;

        for (int i = 0; i < items.size(); i++) {
            Item item1 = items.get(i);
            if (item1.name.equals(item)) {
                item1.count += item.count;
                return true;
            }
        }
        items.add(item);
        return true;
    }

    public void removeItem(Item item, int count) {
        if (item.count >= count)
        {
            item.count -= count;
        }
        if (item.count == 0)
        {
            items.remove(item);
        }
    }

    public boolean isFull() {
        int itemscount = totalItems(items);
        return itemscount == BackPackSize;
    }
}
