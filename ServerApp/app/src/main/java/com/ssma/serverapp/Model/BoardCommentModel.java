package com.ssma.serverapp.Model;

import java.util.HashMap;
import java.util.Map;

public class BoardCommentModel {
    public String comment;
    public String commentWriterUid;
    public int commentStarCount = 0;
    public Map<String, Boolean> commentStars = new HashMap<>();
    public Object yearToDay;
    public Object hourToMinute;
}
