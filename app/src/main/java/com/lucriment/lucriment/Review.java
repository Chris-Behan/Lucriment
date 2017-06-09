package com.lucriment.lucriment;

/**
 * Created by ChrisBehan on 6/9/2017.
 */

public class Review {
    private String author;
    private double Rating;
    private String text;
    private long timeStamp;

    public Review(String author, double rating, String text, long timeStamp) {
        this.author = author;
        Rating = rating;
        this.text = text;
        this.timeStamp = timeStamp;
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
}
