package com.example.minego.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class MyDate implements Serializable, Comparable<MyDate> {
    public int year;
    public int month;
    public int day;

    public MyDate() {
    }

    public MyDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MyDate myDate = (MyDate) o;
        return year == myDate.year && month == myDate.month && day == myDate.day;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day);
    }

    @NonNull
    @Override
    public String toString() {
        return "MyDate{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                "data=" + day + "/" + month + "/" + year +
                '}';
    }

    @Override
    public int compareTo(MyDate o) {
        if (this.year != o.year) {
            return Integer.compare(this.year, o.year);
        }
        if (this.month != o.month) {
            return Integer.compare(this.month, o.month);
        }
        return Integer.compare(this.day, o.day);
    }
}
