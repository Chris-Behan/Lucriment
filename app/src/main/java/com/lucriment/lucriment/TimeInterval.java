package com.lucriment.lucriment;

import android.icu.util.Calendar;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ChrisBehan on 5/30/2017.
 */

public class TimeInterval implements Parcelable{
    private long from;
    private long to;

    public TimeInterval(){

    }

    public TimeInterval(Parcel in){
        String[] data = new String[2];
        in.readStringArray(data);
        this.from = Long.valueOf(data[0]);
        this.to = Long.valueOf(data[1]);
    }

    public TimeInterval(long from, long to) {
        this.from = from;
        this.to = to;
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }

    public String returnTime(){
        return getFrom() + " - " + getTo();
    }

    public double returnTimeInHours(){
        double timeInHours = to-from;
        timeInHours = timeInHours/(double)1000;
        timeInHours =  (timeInHours/(double)3600);
        return timeInHours;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{String.valueOf(this.from),String.valueOf(this.to)});
    }

    public static final Parcelable.Creator<TimeInterval> CREATOR = new Parcelable.Creator<TimeInterval>(){
        @Override
        public TimeInterval createFromParcel(Parcel source) {
            return new TimeInterval(source);
        }

        @Override
        public TimeInterval[] newArray(int size) {
            return new TimeInterval[size];
        }
    };
}
