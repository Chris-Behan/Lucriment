package com.lucriment.lucriment;

/**
 * Created by ChrisBehan on 5/25/2017.
 */

public class SessionRequest {
    private String subject;
    private String location;
    private TimeInterval time;
    private String studentName;
    private double cost;
    public SessionRequest(String subject, String location, TimeInterval time, String name, double cost) {
        this.subject = subject;
        this.location = location;
        this.time = time;
        this.studentName = name;
        this.cost = cost;
    }

    public SessionRequest(){

    }

    public String getStudentName() {
        return studentName;
    }

    public String getSubject() {
        return subject;
    }

    public String getLocation() {
        return location;
    }

    public double getCost() {
        return cost;
    }

    public TimeInterval getTime() {
        return time;
    }
}
