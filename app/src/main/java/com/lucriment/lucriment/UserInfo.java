package com.lucriment.lucriment;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private String headline;
    private String profileImage;
    private String userType;
    private ArrayList<String> savedTutors;
    private String customer_id;
    private Rating rating;


    public UserInfo(){

    }

    public UserInfo(String fullName, String lastName, String firstName, String id, String email,  String userType, String headline) {
        this.fullName = fullName;
        this.lastName = lastName;
        this.firstName = firstName;
        this.id = id;
        this.email = email;
        this.userType = userType;
        this.headline = headline;
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
        String[] data = new String[10];
        in.readStringArray(data);
        this.fullName = data[0];
        this.lastName = data[1];
        this.firstName = data[2];
        this.id = data[3];
        this.chatsWith = data[4];
        this.email = data[5];
        this.headline = data[6];
        this.profileImage = data[7];
        this.userType = data[8];
        this.customer_id = data[9];




    }

    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("fullName",fullName);
        result.put("lastName",lastName);
        result.put("firstName",firstName);
        result.put("id",id);
        result.put("email",email);
        result.put("userType",userType);
        return result;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
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

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
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
        dest.writeStringArray(new String[]{this.fullName,this.lastName,this.firstName,this.id,this.chatsWith,this.email,this.headline,this.profileImage,
        this.userType,this.customer_id});

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