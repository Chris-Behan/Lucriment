package com.lucriment.lucriment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ChrisBehan on 5/9/2017.
 */

public class TutorInfo implements Parcelable {

    private String email;
    private String name;
    private String education;
    private String classes;
    private String id;
    private double rate;


    public TutorInfo(){

    }

    public TutorInfo(String email, String name, String education, String classes, double rate, String id) {

        this.email = email;
        this.name = name;
        this.education = education;
        this.classes = classes;
        this.rate = rate;
        this.id = id;
    }

    public TutorInfo(Parcel in){
        String[] data = new String[6];
        in.readStringArray(data);
        this.email = data[0];
        this.name = data[1];
        this.education = data[2];
        this.classes = data[3];
        this.rate = Double.parseDouble(data[4]);
        this.id = data[5];
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

    public String getID(){return id;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.email, this.name, this.education, this.classes, String.valueOf(this.rate), this.id});

    }

    public static final Parcelable.Creator<TutorInfo> CREATOR = new Parcelable.Creator<TutorInfo>(){
        @Override
        public TutorInfo createFromParcel(Parcel source) {
            return new TutorInfo(source);
        }

        @Override
        public TutorInfo[] newArray(int size) {
            return new TutorInfo[size];
        }
    };


}
