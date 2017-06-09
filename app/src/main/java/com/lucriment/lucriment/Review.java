package com.lucriment.lucriment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ChrisBehan on 6/9/2017.
 */

public class Review implements Parcelable{
    private String author;
    private double Rating;
    private String text;
    private long timeStamp;

    public Review(){

    }
    public Review(String author, double rating, String text, long timeStamp) {
        this.author = author;
        this.Rating = rating;
        this.text = text;
        this.timeStamp = timeStamp;
    }
    public Review(Parcel in){
        String[] data = new String[4];
        in.readStringArray(data);
        this.author = data[0];
        this.Rating = Double.valueOf(data[1]);
        this.text = data[2];
        this.timeStamp = Long.valueOf(data[3]);
    }


    public String getAuthor() {
        return author;
    }

    public double getRating() {
        return Rating;
    }

    public String getText() {
        return text;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.author, String.valueOf(this.Rating), this.text, String.valueOf(this.timeStamp)});
    }
    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>(){
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review( source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
