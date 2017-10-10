package com.lucriment.lucriment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ChrisBehan on 6/9/2017.
 */

public class Review implements Parcelable{
    private String author;
    private double rating;
    private String text;
    private long timeStamp;
    private String authorId;

    public Review(){

    }
    public Review(String author, double rating, String text, long timeStamp, String id) {
        this.author = author;
        this.rating = rating;
        this.text = text;
        this.timeStamp = timeStamp;
        this.authorId = id;
    }
    public Review(Parcel in){
        String[] data = new String[5];
        in.readStringArray(data);
        this.author = data[0];
        this.rating = Double.valueOf(data[1]);
        this.text = data[2];
        this.timeStamp = Long.valueOf(data[3]);
        this.authorId = data[4];
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthor() {
        return author;
    }

    public double getRating() {
        return rating;
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
        dest.writeStringArray(new String[]{this.author, String.valueOf(this.rating), this.text, String.valueOf(this.timeStamp), this.authorId});
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
