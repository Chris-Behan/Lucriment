package com.lucriment.lucriment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ChrisBehan on 5/4/2017.
 */

public class UserInfo implements Parcelable {

    public String accountType;
    public String school;
    public String email;
    public String name;
    public String bio;
    public String firebasetoken;
    public String id;
    public String myChats;

    public UserInfo(){

    }

    public UserInfo(Parcel in ){
        String[] data = new String[6];
        in.readStringArray(data);
        this.email = data[0];
        this.name = data[1];
        this.school = data[2];
        this.bio = data[3];
        this.firebasetoken = data[4];
        this.id = data[5];


    }

    public UserInfo(String accountType, String school, String email, String name, String id) {
        this.accountType = accountType;
        this.school = school;
        this.email = email;
        this.name = name;
        this.firebasetoken = firebasetoken;
        this.id = id;
        this.myChats = "";
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getSchool() {
        return school;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getFirebasetoken() {
        return firebasetoken;
    }

    public String getUid() {
        return id;
    }

    public String getMyChats() {
        return myChats;
    }

    public void setMyChats(String myChats) {
        this.myChats = myChats;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.email, this.name, this.school, this.bio, this.firebasetoken, this.id});

    }

    public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>(){
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo( source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}