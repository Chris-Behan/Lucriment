package com.lucriment.lucriment;

/**
 * Created by ChrisBehan on 6/12/2017.
 */

public class Rating {
    private double totalScore;
    private int numberOfReviews;
    public Rating(){}

    public Rating(double totalScore, int numberOfReviews) {
        this.totalScore = totalScore;
        this.numberOfReviews = numberOfReviews;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }
}
