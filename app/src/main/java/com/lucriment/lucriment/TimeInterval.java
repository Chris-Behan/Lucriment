package com.lucriment.lucriment;

import android.icu.util.Calendar;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;

/**
 * Created by ChrisBehan on 5/30/2017.
 */

public class TimeInterval implements Parcelable{
    private long from;
    private long to;
    private String[] monthNames = new String[]{"January","February","March","April","May","June","July",
            "August","September","October","November","December"};
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

    public double returnFromValue(){
        double timeInHours = from;
        timeInHours = timeInHours/(double)1000;
        timeInHours =  (timeInHours/(double)3600);
        return timeInHours;
    }
    public double returnToValue(){
        double timeInHours = to;
        timeInHours = timeInHours/(double)1000;
        timeInHours =  (timeInHours/(double)3600);
        return timeInHours;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int returnDay(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(from);
        int toMinute = cal.get(Calendar.DAY_OF_MONTH);
        return toMinute;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public int returnMonth(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(from);
        int toMinute =  cal.get(Calendar.MONTH);
        return toMinute;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public int returnYear(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(from);
        int toMinute =  cal.get(Calendar.YEAR);
        return toMinute;
    }

    public String returnFormattedDate(){
        String fd = returnDate() +" "+ returnFromTime()+" - "+returnToTime();
        return fd;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String returnDate(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(from);
        String date = "" + monthNames[cal.get(Calendar.MONTH)] + ", " +cal.get(Calendar.DAY_OF_MONTH) + ", " + cal.get(Calendar.YEAR);

        return date;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String returnFromTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(from);
        String from = "" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE);
        return from;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String returnToTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(to);
        String to = "" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE);
        return to;
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
