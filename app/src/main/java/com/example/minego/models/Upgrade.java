package com.example.minego.models;

public class Upgrade {
    private int MineLevel;
    private int Radius;
    private int efficiency;

    public int getMineLevel() {
        return MineLevel;
    }

    public void setMineLevel(int mineLevel) {
        MineLevel = mineLevel;
    }

    public int getRadius() {
        return Radius;
    }

    public void setRadius(int radius) {
        Radius = radius;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(int efficiency) {
        this.efficiency = efficiency;
    }
    public int getMineHp () {
        if (MineLevel == 1) {
            return 10;
        }
        else if (MineLevel == 2) {
            return 50;
        }
        else if (MineLevel == 3) {
            return 125;
        }
        return 0;
    }

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
        }
        return 0;


    }
}
