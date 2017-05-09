package com.lucriment.lucriment;

/**
 * Created by ChrisBehan on 5/4/2017.
 */

public class UserInfo {

    public String accountType;
    public String school;
    public String email;
    public String name;

    public UserInfo(){

    }

    public UserInfo(String accountType, String school, String email, String name) {
        this.accountType = accountType;
        this.school = school;
        this.email = email;
        this.name = name;
    }
}