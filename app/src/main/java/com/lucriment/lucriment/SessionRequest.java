package com.lucriment.lucriment;

/**
 * Created by ChrisBehan on 5/25/2017.
 */

public class SessionRequest {
    private String subject;
    private String location;
    private Availability time;
    private String studentName;

    public SessionRequest(String subject, String location, Availability time, String name) {
        this.subject = subject;
        this.location = location;
        this.time = time;
        this.studentName = name;
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

    public Availability getTime() {
        return time;
    }
}
