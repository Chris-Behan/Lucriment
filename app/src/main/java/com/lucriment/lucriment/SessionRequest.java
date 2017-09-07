package com.lucriment.lucriment;

/**
 * Created by ChrisBehan on 5/25/2017.
 */

public class SessionRequest {
    private boolean confirmed;
    private boolean sessionDeclined = false, sessionCancelled = false;
    private String location;
    private String tutorId;
    private String tutorName;
    private String studentId;
    private String studentName;
    private String subject;
    private double price;
    private TimeInterval time;
    private Review studentReview, tutorReview;


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
    public SessionRequest(String location, String tutorId, String tutorName, String studentId, String studentName, String subject, double price, TimeInterval time,
                          Review studentReview) {
        this.location = location;
        this.tutorId = tutorId;
        this.tutorName = tutorName;
        this.studentId = studentId;
        this.studentName = studentName;
        this.subject = subject;
        this.price = price;
        this.time = time;
        this.studentReview = studentReview;
    }
    public SessionRequest(String location, String tutorId, String tutorName, String studentId, String studentName, String subject, double price, TimeInterval time,
                          Review tutorReview,Review studentReview) {
        this.location = location;
        this.tutorId = tutorId;
        this.tutorName = tutorName;
        this.studentId = studentId;
        this.studentName = studentName;
        this.subject = subject;
        this.price = price;
        this.time = time;
        this.tutorReview = tutorReview;
        this.studentReview = studentReview;
    }


    public boolean isSessionDeclined() {
        return sessionDeclined;
    }

    public void setSessionDeclined(boolean sessionDeclined) {
        this.sessionDeclined = sessionDeclined;
    }

    public boolean isSessionCancelled() {
        return sessionCancelled;
    }

    public void setSessionCancelled(boolean sessionCancelled) {
        this.sessionCancelled = sessionCancelled;
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
    public Review getStudentReview(){return studentReview;}

    public void setStudentReview(Review review) {
        this.studentReview = review;
    }

    public Review getTutorReview() {
        return tutorReview;
    }

    public void setTutorReview(Review tutorReview) {
        this.tutorReview = tutorReview;
    }

    public TimeInterval getTime() {
        return time;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}


