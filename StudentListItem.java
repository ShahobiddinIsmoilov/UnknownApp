package com.rollermine.unknownapp;

public class StudentListItem {
    private final String mColor;
    private final String mPhoneNumber;
    private final String mStudentNameValueSmall;
    private final String mCleanablesValueSmall;
    private final String mUncleanablesValueSmall;
    private final String mMessage;

    public StudentListItem(/*int*/String color, String phone, String text1, String text2, String text3, String msg) {
        mColor = color;
        mPhoneNumber = phone;
        mStudentNameValueSmall = text1;
        mCleanablesValueSmall = text2;
        mUncleanablesValueSmall = text3;
        mMessage = msg;
    }

    public String getColorSmall() {
        return mColor;
    }

    public String getPhoneNumberSmall() {
        return mPhoneNumber;
    }

    public String getStudentNameValueSmall() {
        return mStudentNameValueSmall;
    }

    public String getCleanablesValueSmall() {
        return mCleanablesValueSmall;
    }

    public String getUncleanablesValueSmall() {
        return mUncleanablesValueSmall;
    }

    public String getMessageSmall() {return mMessage; }
}

