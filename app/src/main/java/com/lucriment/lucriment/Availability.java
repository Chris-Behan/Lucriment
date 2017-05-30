package com.lucriment.lucriment;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Time;

/**
 * Created by ChrisBehan on 5/19/2017.
 */

public class Availability implements Parcelable{
    private int day, month, year, fromhour, fromminute, tohour, tominute;
    private String frequency;
    private String[] monthNames = new String[]{"","January","February","March","April","May","June","July",
    "August","September","October","November","December"};
    private TimeInterval timeInterval;

    public  Availability(){

    }
    public Availability(TimeInterval ti, String frequency){
        this.timeInterval = ti;
        this.frequency = frequency;
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
    public Availability(String from, String to, int day, int month, int year){
        this.fromhour = Integer.valueOf(from.substring(0,from.indexOf(':')));
        this.fromminute = Integer.valueOf(from.substring(from.indexOf(':')+1,from.length()));
        this.tohour = Integer.valueOf(to.substring(0,to.indexOf(':')));
        this.tominute = Integer.valueOf(to.substring(to.indexOf(':')+1,to.length()));
        this.day = day;
        this.month = month;
        this.year = year;
        this.frequency = "Never";
    }

    public Availability(Parcel in){
        String[] data = new String[8];
        in.readStringArray(data);
        this.day = Integer.parseInt(data[0]);
        this.month = Integer.parseInt(data[1]);
        this.year = Integer.parseInt(data[2]);
        this.fromhour = Integer.parseInt(data[3]);
        this.fromminute = Integer.parseInt(data[4]);
        this.tohour = Integer.parseInt(data[5]);
        this.tominute = Integer.parseInt(data[6]);
        this.frequency = data[7];


    }

    public TimeInterval getTimeInterval() {
        return timeInterval;
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

    public int getFromValue(){
        return fromhour*60 + fromminute;
    }

    public int getToValue(){
        return tohour*60 + tominute;
    }

    public double getTimeInHours(){
        return ((double)(getToValue() - getFromValue()))/60;
    }


    public String getDate(){return (getMonthName(month) + ", " + day + ", ");}

    public String getFromTime(){
        return (fromhour +":"+fromminute);
    }
    public String getToTime(){
        if(tominute == 0){
            return (tohour+":00");
        }else {
            return (tohour + ":" + tominute);
        }
    }

    public String getFrequency() {
        return frequency;
    }

    public String getTime(){
        return getDate()+"  " + getFromTime()+" - " + getToTime();
    }

    public String getMonthName(int month){

        return monthNames[month];
    }

    public void setDay(int day) {
        this.day = day;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeStringArray(new String[]{String.valueOf(this.day),String.valueOf(this.month),String.valueOf(this.year),
                String.valueOf(this.fromhour),String.valueOf(this.fromminute),String.valueOf(this.tohour),String.valueOf(this.tominute),
                this.frequency});
    }

    public static final Parcelable.Creator<Availability> CREATOR = new Parcelable.Creator<Availability>(){
        @Override
        public Availability createFromParcel(Parcel source) {
            return new Availability(source);
        }

        @Override
        public Availability[] newArray(int size) {
            return new Availability[size];
        }
    };
}
