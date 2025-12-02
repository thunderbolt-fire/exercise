package com.example.exercise.data.model;

import java.util.List;

/**
 * 天气API响应的数据模型 (POJO)。
 * <p>
 * 这个类及其嵌套类用于通过Gson将高德天气API返回的JSON数据直接映射为Java对象。
 * 为了简化解析，这里只定义了应用中实际需要用到的字段。
 */
public class WeatherResponse {
    private String status;       // 接口返回状态，"1"表示成功
    private String count;        // 返回结果的数量
    private String info;         // 状态说明
    private String infocode;     // 状态码
    private List<Forecast> forecasts; // 预报列表，通常只包含一个我们查询的城市

    /**
     * 获取天气预报列表。
     */
    public List<Forecast> getForecasts() {
        return forecasts;
    }

    /**
     * 嵌套类，表示一个城市的完整预报信息。
     */
    public static class Forecast {
        private String city;       // 城市名称
        private String adcode;     // 行政区划编码
        private String province;   // 所属省份
        private String reporttime; // 数据发布时间
        private List<Cast> casts;  // 包含未来多日预报的列表

        /**
         * 获取多日预报列表。
         */
        public List<Cast> getCasts() {
            return casts;
        }

        /**
         * 获取城市名称。
         */
        public String getCity() {
            return city;
        }
    }

    /**
     * 嵌套类，表示某一天的具体天气详情。
     */
    public static class Cast {
        private String date;         // 日期 (yyyy-MM-dd)
        private String week;         // 星期 (1-7)
        private String dayweather;   // 白天天气现象
        private String nightweather; // 夜间天气现象
        private String daytemp;      // 白天温度 (°C)
        private String nighttemp;    // 夜间温度 (°C)
        private String daywind;      // 白天风向
        private String nightwind;    // 夜间风向
        private String daypower;     // 白天风力等级
        private String nightpower;   // 夜间风力等级

        // Getter 方法
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
