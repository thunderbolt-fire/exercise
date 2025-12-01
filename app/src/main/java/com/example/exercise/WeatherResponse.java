package com.example.exercise;

import java.util.List;

public class WeatherResponse {
    private String status;
    private String count;
    private String info;
    private String infocode;
    private List<Forecast> forecasts;

    public List<Forecast> getForecasts() {
        return forecasts;
    }

    public static class Forecast {
        private String city;
        private String adcode;
        private String province;
        private String reporttime;
        private List<Cast> casts;

        public List<Cast> getCasts() {
            return casts;
        }

        public String getCity() {
            return city;
        }
    }

    public static class Cast {
        private String date;
        private String week;
        private String dayweather;
        private String nightweather;
        private String daytemp;
        private String nighttemp;
        private String daywind;
        private String nightwind;
        private String daypower;
        private String nightpower;

        public String getDate() { return date; }
        public String getWeek() { return week; }
        public String getDayweather() { return dayweather; }
        public String getNightweather() { return nightweather; }
        public String getDaytemp() { return daytemp; }
        public String getNighttemp() { return nighttemp; }
        public String getDaywind() { return daywind; }
        public String getNightwind() { return nightwind; }
        public String getDaypower() { return daypower; }
        public String getNightpower() { return nightpower; }
    }
}
