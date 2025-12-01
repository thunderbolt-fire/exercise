package com.example.exercise;

import java.util.List;

/**
 * WeatherResponse 用于承接高德天气接口返回的整体 JSON 数据。
 * 只包含我们需要使用的字段，未使用的字段可以省略以简化解析。
 */
public class WeatherResponse {
    /** 接口的状态码：1 表示成功 */
    private String status;
    /** 查询结果数量 */
    private String count;
    /** 接口返回的说明信息 */
    private String info;
    /** 更精确的状态码 */
    private String infocode;
    /** 天气预报列表，通常只有一个元素，对应查询的城市 */
    private List<Forecast> forecasts;

    /**
     * 获取天气预报列表。
     * @return forecast 数组，可能为 null 或空。
     */
    public List<Forecast> getForecasts() {
        return forecasts;
    }

    /**
     * Forecast 表示一次查询下的完整预报信息，含城市、发布时间和多日预报。
     */
    public static class Forecast {
        /** 城市名称，例如“东城区” */
        private String city;
        /** 行政区划编码 */
        private String adcode;
        /** 所属省份 */
        private String province;
        /** 报告时间，格式如“2025-12-01 16:07:34” */
        private String reporttime;
        /** 多日预报列表，每个 Cast 对应一天的白天/夜间信息 */
        private List<Cast> casts;

        /**
         * 获取多日预报列表。
         */
        public List<Cast> getCasts() {
            return casts;
        }

        /**
         * 获取当前预报对应的城市名称。
         */
        public String getCity() {
            return city;
        }
    }

    /**
     * Cast 表示某一天的白天/夜间天气详情。
     */
    public static class Cast {
        /** 日期，格式为 yyyy-MM-dd */
        private String date;
        /** 星期几，接口返回 1-7 */
        private String week;
        /** 白天天气描述 */
        private String dayweather;
        /** 夜间天气描述 */
        private String nightweather;
        /** 白天温度（摄氏度，字符串形式） */
        private String daytemp;
        /** 夜间温度（摄氏度，字符串形式） */
        private String nighttemp;
        /** 白天风向 */
        private String daywind;
        /** 夜间风向 */
        private String nightwind;
        /** 白天风力等级 */
        private String daypower;
        /** 夜间风力等级 */
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
