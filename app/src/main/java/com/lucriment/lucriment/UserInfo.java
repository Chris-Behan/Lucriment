package com.lucriment.lucriment;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ChrisBehan on 5/4/2017.
 */

public class UserInfo implements Parcelable {

    private String fullName;
    private String lastName;
    private String firstName;
    private String id;
    private String chatsWith;
    private String email;
    private String title;
    private String profileImage;
    private String userType;
    private ArrayList<String> savedTutors;


    public UserInfo(){

    }

    public UserInfo(String fullName, String lastName, String firstName, String id, String email,  String userType) {
        this.fullName = fullName;
        this.lastName = lastName;
        this.firstName = firstName;
        this.id = id;

        this.email = email;


        this.userType = userType;
    }

    public UserInfo(Parcel in ){
        String[] data = new String[9];
        in.readStringArray(data);
        this.fullName = data[0];
        this.lastName = data[1];
        this.firstName = data[2];
        this.id = data[3];
        this.chatsWith = data[4];
        this.email = data[5];
        this.title = data[6];
        this.profileImage = data[7];
        this.userType = data[8];




    }



    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatsWith() {
        return chatsWith;
    }

    public void setChatsWith(String chatsWith) {
        this.chatsWith = chatsWith;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public ArrayList<String> getSavedTutors() {
        return savedTutors;
    }

    public void setSavedTutors(ArrayList<String> savedTutors) {
        this.savedTutors = savedTutors;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.fullName,this.lastName,this.firstName,this.id,this.chatsWith,this.email,this.title,this.profileImage,
        this.userType});

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