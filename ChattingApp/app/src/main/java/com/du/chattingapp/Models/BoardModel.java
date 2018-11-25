package com.du.chattingapp.Models;

import java.util.HashMap;
import java.util.Map;

public class BoardModel {
    public String boardTitle;
    public String boardDescription;
    public String boardUid;
    public String boardUserId;
    public int boardStarCount = 0;
    public Map<String, Boolean> boardStars = new HashMap<>();
    public Object timeStamp;

}
