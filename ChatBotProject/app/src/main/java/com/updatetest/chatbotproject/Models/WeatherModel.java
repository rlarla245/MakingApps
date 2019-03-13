package com.updatetest.chatbotproject.Models;

import java.util.ArrayList;

public class WeatherModel {
    public String cod = null;
    public Float message = null;
    public Integer cnt = null;
    public ArrayList<List> list = null;

    public static class List {
        public Integer dt = null;
        public Main main = null;
        public ArrayList<Weather> weather = null;
        public Clouds clouds = null;
        public Wind wind = null;
        public String dt_txt = null;

        public static class Main {
            public Float temp = null;
            public Float temp_min = null;
            public Float temp_max = null;
            public Float pressure = null;
            public Float sea_level = null;
            public Float grnd_level = null;
            public Float humidity = null;
            public Float temp_kf = null;
        }

        public static class Weather {
            public Integer id = null;
            public String main = null;
            public String description = null;
            public String icon = null;
        }

        public static class Clouds {
            public Integer all = null;
        }

        public static class Wind {
            public Float speed = null;
            public Float deg = null;
        }
    }
}
