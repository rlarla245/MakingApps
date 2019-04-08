package com.du.instagramprototypeproject.model;

public class AlarmDTO {
    // 받는 사람 아이디
    public String destinationUid;
    // 보내는 사람 정보
    public String userId;
    public String uid;
    // 숫자에 따라 보내는 문구를 변경시키는 듯
    public int kind; //0 : 좋아요, 1: 팔로우, 2: 메세지
    public String message;
    public String imageUid;
}
