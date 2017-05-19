package com.lucriment.lucriment;

/**
 * Created by ChrisBehan on 5/19/2017.
 */

public class Availability {
    private int day, month, year, fromhour, fromminute, tohour, tominute;
    private String frequency;

    public  Availability(){

    }

    public Availability(int day, int month, int year, int fromhour, int fromminute, int tohour, int tominute, String frequency) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.fromhour = fromhour;
        this.fromminute = fromminute;
        this.tohour = tohour;
        this.tominute = tominute;
        this.frequency = frequency;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getFromhour() {
        return fromhour;
    }

    public int getFromminute() {
        return fromminute;
    }

    public int getTohour() {
        return tohour;
    }

    public int getTominute() {
        return tominute;
    }

    public String getFrequency() {
        return frequency;
    }
}
