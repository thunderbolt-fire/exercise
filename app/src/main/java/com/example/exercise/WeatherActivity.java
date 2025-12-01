package com.example.exercise;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * WeatherActivity 负责展示当前天气和未来预报，支持在两个界面间切换。
 */
public class WeatherActivity extends AppCompatActivity {

    private TextView tvCity, tvWeatherStatus, tvTemperature, tvTempHighLow;
    private TextView tvDayWeather, tvDayTemp, tvDayWind;
    private TextView tvNightWeather, tvNightTemp, tvNightWind;
    private RecyclerView rvFutureForecast;
    private FutureForecastAdapter futureForecastAdapter;
    private View layoutCurrent;
    private View layoutForecast;
    private Button btnShowCurrent;
    private Button btnShowForecast;
    private TextView tvFutureCity;

    /** OkHttp 客户端实例，用于执行网络请求 */
    private final OkHttpClient client = new OkHttpClient();
    /** Gson 实例，将 JSON 字符串转换为 WeatherResponse 对象 */
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initViews(); // 绑定控件并初始化列表
        fetchWeatherData(); // 调起网络请求，获取天气数据
    }

    /**
     * 查找并缓存页面中的所有控件，同时设置 RecyclerView 与按钮监听。
     */
    private void initViews() {
        tvCity = findViewById(R.id.tv_city);
        tvWeatherStatus = findViewById(R.id.tv_weather_status);
        tvTemperature = findViewById(R.id.tv_temperature);
        tvTempHighLow = findViewById(R.id.tv_temp_high_low);
        tvDayWeather = findViewById(R.id.tv_day_weather);
        tvDayTemp = findViewById(R.id.tv_day_temp);
        tvDayWind = findViewById(R.id.tv_day_wind);
        tvNightWeather = findViewById(R.id.tv_night_weather);
        tvNightTemp = findViewById(R.id.tv_night_temp);
        tvNightWind = findViewById(R.id.tv_night_wind);
        layoutCurrent = findViewById(R.id.layout_current);
        layoutForecast = findViewById(R.id.layout_forecast);
        btnShowCurrent = findViewById(R.id.btn_show_current);
        btnShowForecast = findViewById(R.id.btn_show_forecast);
        tvFutureCity = findViewById(R.id.tv_future_city);
        rvFutureForecast = findViewById(R.id.rv_future_forecast);
        futureForecastAdapter = new FutureForecastAdapter();
        rvFutureForecast.setLayoutManager(new LinearLayoutManager(this));
        rvFutureForecast.setAdapter(futureForecastAdapter);
        setupTabs();
    }

    /**
     * 初始化底部切换按钮，默认显示当前天气。
     */
    private void setupTabs() {
        btnShowCurrent.setOnClickListener(v -> switchTab(true));
        btnShowForecast.setOnClickListener(v -> switchTab(false));
        switchTab(true);
    }

    /**
     * 根据参数显示对应的布局，并更新按钮的可用状态来模拟选中效果。
     */
    private void switchTab(boolean showCurrent) {
        layoutCurrent.setVisibility(showCurrent ? View.VISIBLE : View.GONE);
        layoutForecast.setVisibility(showCurrent ? View.GONE : View.VISIBLE);
        btnShowCurrent.setEnabled(!showCurrent);
        btnShowForecast.setEnabled(showCurrent);
    }

    /**
     * 向高德天气接口发起异步请求，获取城市的实时及未来天气数据。
     */
    private void fetchWeatherData() {
        Request request = new Request.Builder()
                .url("https://restapi.amap.com/v3/weather/weatherInfo?city=610100&extensions=all&&key=78437de757a2693c3f9cb2aabf6f25fd")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(WeatherActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    final WeatherResponse weatherResponse = gson.fromJson(responseBody, WeatherResponse.class);
                    runOnUiThread(() -> updateUi(weatherResponse));
                }
            }
        });
    }

    /**
     * 将接口返回的数据渲染到界面。
     * @param weatherResponse 解析好的天气实体，可能为 null。
     */
    private void updateUi(WeatherResponse weatherResponse) {
        if (weatherResponse != null && weatherResponse.getForecasts() != null && !weatherResponse.getForecasts().isEmpty()) {
            WeatherResponse.Forecast forecast = weatherResponse.getForecasts().get(0);
            if (forecast != null && forecast.getCasts() != null && !forecast.getCasts().isEmpty()) {
                WeatherResponse.Cast today = forecast.getCasts().get(0);
                // 顶部城市与温度信息
                tvCity.setText(forecast.getCity());
                tvWeatherStatus.setText(today.getDayweather());
                tvTemperature.setText(String.format("%s°", today.getDaytemp()));
                tvTempHighLow.setText(String.format("最高: %s° 最低: %s°", today.getDaytemp(), today.getNighttemp()));

                // 白天卡片
                tvDayWeather.setText(today.getDayweather());
                tvDayTemp.setText(String.format("%s°", today.getDaytemp()));
                tvDayWind.setText(String.format("%s %s级", today.getDaywind(), today.getDaypower()));

                // 夜间卡片
                tvNightWeather.setText(today.getNightweather());
                tvNightTemp.setText(String.format("%s°", today.getNighttemp()));
                tvNightWind.setText(String.format("%s %s级", today.getNightwind(), today.getNightpower()));

                // 未来预报列表
                tvFutureCity.setText(forecast.getCity());
                if (forecast.getCasts().size() > 1) {
                    futureForecastAdapter.submitData(forecast.getCasts().subList(1, forecast.getCasts().size()));
                } else {
                    futureForecastAdapter.submitData(null);
                }
            }
        }
    }
}
