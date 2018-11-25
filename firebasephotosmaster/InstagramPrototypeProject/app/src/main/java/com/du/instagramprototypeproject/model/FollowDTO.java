package com.du.instagramprototypeproject.model;

import java.util.HashMap;
import java.util.Map;

public class FollowDTO {
    public String userId;
    public int followerCount = 0;
    public Map<String, Boolean> followers = new HashMap<>();

    public int followingCount = 0;
    // 해쉬맵에 내 uid, true/false 이런 식으로 기입됩니다.
    public Map<String, Boolean> followings = new HashMap<>();
}
