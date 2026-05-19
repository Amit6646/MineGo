package com.example.minego.models;

public enum ItemType {
    stone("Stone"),
    iron("Iron"),
    gold("Gold"),
    ruby("Ruby"),
    diamond("Diamond");

    final String type;

    ItemType(String type) {
        this.type = type;
    }

    public static ItemType of(String type) {
        if (type == null || type.isBlank()) return null;

        type = type.trim().toLowerCase();
        type = Character.toUpperCase(type.charAt(0)) + type.substring(1);

        for (ItemType itemType : ItemType.values()) {
            if (itemType.type.equals(type)) {
                return itemType;
            }
        }

        return null; // not found
    }


    public String getType() {
        return type;
    }
}
