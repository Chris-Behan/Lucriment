package com.lucriment.lucriment;

/**
 * Created by ChrisBehan on 5/25/2017.
 */

public class SessionRequest {
    private boolean confirmed;
    private String location;
    private String tutorId;
    private String tutorName;
    private String studentId;
    private String studentName;
    private String subject;
    private double price;
    private TimeInterval time;
    private Review review;


    public SessionRequest(TimeInterval ti) {

    }

    public SessionRequest() {

    }

    public SessionRequest(String location, String tutorId, String tutorName, String studentId, String studentName, String subject, double price, TimeInterval time) {
        this.location = location;
        this.tutorId = tutorId;
        this.tutorName = tutorName;
        this.studentId = studentId;
        this.studentName = studentName;
        this.subject = subject;
        this.price = price;
        this.time = time;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getLocation() {
        return location;
    }

    public String getTutorId() {
        return tutorId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getSubject() {
        return subject;
    }

    public double getPrice() {
        return price;
    }
    public Review getReview(){return review;}

    public void setReview(Review review) {
        this.review = review;
    }

    public TimeInterval getTime() {
        return time;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}


