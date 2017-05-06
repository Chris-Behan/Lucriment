package com.lucriment.lucriment;

/**
 * Created by ChrisBehan on 5/4/2017.
 */

public class UserInfo {

    public String accountType;
    public String school;
    public String email;

    public UserInfo(){

    }

    public UserInfo(String accountType, String school, String email) {
        this.accountType = accountType;
        this.school = school;
        this.email = email;
    }
}
