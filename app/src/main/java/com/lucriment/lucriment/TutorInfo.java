package com.lucriment.lucriment;

/**
 * Created by ChrisBehan on 5/9/2017.
 */

public class TutorInfo {

    private String email;
    private String name;
    private String education;
    private String classes;
    private double rate;

    public TutorInfo(){

    }

    public TutorInfo(String email, String name, String education, String classes, double rate) {

        this.email = email;
        this.name = name;
        this.education = education;
        this.classes = classes;
        this.rate = rate;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getEducation() {
        return education;
    }

    public String getClasses() {
        return classes;
    }

    public double getRate() {
        return rate;
    }
}
