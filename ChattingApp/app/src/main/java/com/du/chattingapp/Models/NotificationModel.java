package com.du.chattingapp.Models;

public class NotificationModel {
    public String to;

    public Data data = new Data();
    public Notification notification = new Notification();

    public static class Notification {
        public String title;
        public String text;
    }

    public static class Data {
        public String title;
        public String text;
    }
}
