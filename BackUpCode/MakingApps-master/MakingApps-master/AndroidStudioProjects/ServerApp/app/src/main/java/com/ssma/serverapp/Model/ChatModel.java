package com.ssma.serverapp.Model;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {
    // 채팅방 유저
    public Map<String, Boolean> users = new HashMap<>();

    // 채팅방 대화 내용
    public Map<String, Comment> message_comments = new HashMap<>();

    public static class Comment {
        public String uid;
        public String message;
        public Object timestamp;
        public Map<String,Object> readUsers = new HashMap<>();
    }
}
