package com.lucriment.lucriment;

import android.icu.util.Calendar;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;

import java.sql.Time;

/**
 * Created by ChrisBehan on 5/19/2017.
 */

public class Availability implements Parcelable{

    private String frequency;
    private String[] monthNames = new String[]{"","January","February","March","April","May","June","July",
    "August","September","October","November","December"};
    private TimeInterval time;

    public  Availability(){

    }
    public Availability(TimeInterval ti, String frequency){
        this.time = ti;
        this.frequency = frequency;
    }



    public Availability(Parcel in){
        String[] data = new String[8];
        in.readStringArray(data);

        this.frequency = data[7];


    }

    public TimeInterval gettime() {
        return time;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setDay(int day) {

    }
    public String timeprocess(){
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String returnDate(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time.getFrom());
        String date = "" + monthNames[cal.get(Calendar.MONTH)] + ", " +cal.get(Calendar.DAY_OF_MONTH) + ", " + cal.get(Calendar.YEAR);

        return date;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String returnFromTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time.getFrom());
        String from = "" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE);
        return from;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String returnToTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time.getTo());
        String to = "" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE);
        return to;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeStringArray(new String[]{String.valueOf(2),String.valueOf(2),String.valueOf(2),
                String.valueOf(2),String.valueOf(2),String.valueOf(2),String.valueOf(2),
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
