package com.example.minego.models;

import androidx.annotation.NonNull;

public class Item {
    private ItemType type;
    private int count;


    public Item() {
    }

    public Item(ItemType type, int count) {
        this.type = type;
        this.count = count;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void increaseCount(int count) {
        this.count += count;
    }

    public void decreaseCount(int count) {
        this.count -= count;
    }


    @NonNull
    @Override
    public String toString() {
        return "Item{" +
                "type=" + type +
                ", count=" + count +
                '}';
    }

}
