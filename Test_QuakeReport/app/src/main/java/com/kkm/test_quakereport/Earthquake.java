package com.kkm.test_quakereport;

public class Earthquake {
    // 진도
    private String mMagnitude;

    // 위치
    private String mLocation;

    // 날짜
    private String mDate;

    // 파라미터 3개인 생성자
    public Earthquake(String magnitude, String location, String date) {
        mMagnitude = magnitude;
        mLocation = location;
        mDate = date;
    }

    public String getMagnitude() {
        return mMagnitude;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getmDate() {
        return mDate;
    }
}
