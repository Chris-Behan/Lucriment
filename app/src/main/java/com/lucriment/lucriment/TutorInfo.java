package com.lucriment.lucriment;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ChrisBehan on 5/9/2017.
 */

public class TutorInfo implements Parcelable {

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
    private String postalCode;
    private String about;
    private ArrayList<Availability> availability;
    private long phoneNumber;
    private ArrayList<String> subjects;
    private int rate;
    private String subjectString;


    public TutorInfo(){

    }



    public TutorInfo(UserInfo user, String postalCode, long phoneNumber, int rate, ArrayList<String> subjects) {
        this.fullName = user.getFullName();
        this.lastName = user.getLastName();
        this.firstName = user.getFirstName();
        this.id = user.getId();
        this.chatsWith = user.getChatsWith();
        this.email = user.getEmail();
        this.title = user.getTitle();
        this.userType = user.getUserType();
        this.subjects = subjects;
        this.postalCode = postalCode;
        this.about = about;
        this.availability = availability;
        this.phoneNumber = phoneNumber;
        this.rate = rate;

    }

    public TutorInfo(UserInfo user, String postalCode, long phoneNumber, int rate) {
        this.fullName = user.getFullName();
        this.lastName = user.getLastName();
        this.firstName = user.getFirstName();
        this.id = user.getId();
        this.chatsWith = user.getChatsWith();
        this.email = user.getEmail();
        this.title = user.getTitle();
        this.userType = user.getUserType();

        this.postalCode = postalCode;
        this.about = about;
        this.availability = availability;
        this.phoneNumber = phoneNumber;
        this.rate = rate;
    }


    public TutorInfo(Parcel in){
        String[] data = new String[14];
        in.readStringArray(data);
        this.fullName = data[0];
        this.lastName = data[1];
        this.firstName = data[2];
        this.id = data[3];
        this.chatsWith = data[4];
        this.email = data[5];
        this.title = data[6];
        this.userType = data[7];
        this.postalCode = data[8];
        this.about = data[9];
        this.phoneNumber = Long.parseLong(data[10]);
        this.rate = Integer.parseInt(data[11]);
        this.profileImage = data[12];
        this.subjectString = data[13];
        //this.subjects = data[13];
    }

    public String getFullName() {
        return fullName;
    }

    public String arrToString(ArrayList<String> arr){
        String arrString = "";
        if(arr!=null)
        for(String s:arr){
            if(arrString.equals("")){
                arrString = s;
            }else {
                arrString = arrString + "," + s;
            }
        }
        return arrString;
    }

    public String[] stringToArr(String text){
        String[] arr = text.split(",");
        return arr;
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

    public String returnSubjectString(){
        return subjectString;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public ArrayList<Availability> getAvailability() {
        return availability;
    }

    public void setAvailability(ArrayList<Availability> availability) {
        this.availability = availability;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }


    public void setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (subjects == null) {
            dest.writeStringArray(new String[]{this.fullName,this.lastName,this.firstName,this.id,this.chatsWith,this.email,this.title,this.userType,
                    this.postalCode,this.about, String.valueOf(this.phoneNumber),String.valueOf(this.rate),this.profileImage, this.subjectString});
        }else{
            dest.writeStringArray(new String[]{this.fullName,this.lastName,this.firstName,this.id,this.chatsWith,this.email,this.title,this.userType,
                    this.postalCode,this.about, String.valueOf(this.phoneNumber),String.valueOf(this.rate),this.profileImage, arrToString(this.subjects)});
        }


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
