package com.example.stmart;

public class ReadwriteUserDetails {
    public String Collegeid,DOB,gender,mobile;

    public ReadwriteUserDetails(){ } //without empty constructor we cant have snapshot of data

    public ReadwriteUserDetails(String textCollegeid,String textDOB,String textGender,String textMobile)
    {
        //this.fullName=textFullName;
        this.Collegeid=textCollegeid;
        this.DOB=textDOB;
        this.gender=textGender;
        this.mobile=textMobile;
    }
}
