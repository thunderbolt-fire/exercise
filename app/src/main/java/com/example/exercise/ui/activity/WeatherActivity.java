package com.example.exercise.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exercise.R;
import com.example.exercise.data.model.WeatherResponse;
import com.example.exercise.ui.adapter.FutureForecastAdapter;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 天气预报页面 Activity。
 * <p>
 * 负责展示天气信息，其功能包括：
 * 1. 通过OkHttp从高德天气API异步获取天气数据。
 * 2. 使用Gson将返回的JSON数据解析为Java对象（WeatherResponse）。
 * 3. 将解析出的实时天气和未来预报数据更新到UI上。
 * 4. 实现“当前天气”和“未来预报”两个标签页的切换功能。
 * 5. 根据获取到的天气状况（晴、雨、多云等），动态地更换页面背景。
 */
public class WeatherActivity extends AppCompatActivity {

    // UI 控件
    private ConstraintLayout weatherLayout; // 页面的根布局，用于更换背景
    private TextView tvCity, tvWeatherStatus, tvTemperature, tvTempHighLow;
    private TextView tvDayWeather, tvDayTemp, tvDayWind;
    private TextView tvNightWeather, tvNightTemp, tvNightWind;
    private RecyclerView rvFutureForecast; // 用于显示未来天气预报的列表
    private FutureForecastAdapter futureForecastAdapter; // 上述列表的适配器
    private View layoutCurrent;     // “当前天气”布局
    private View layoutForecast;    // “未来预报”布局
    private Button btnShowCurrent;  // “当前天气”标签按钮
    private Button btnShowForecast; // “未来预报”标签按钮
    private TextView tvFutureCity;  // “未来预报”页面中的城市名称

    // OkHttp 和 Gson 的实例，用于网络请求和JSON解析
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    /**
     * Activity创建时的回调方法。
     *
     * @param savedInstanceState 如果Activity被重新创建，此参数会包含之前保存的状态。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initViews(); // 初始化所有UI控件
        fetchWeatherData(); // 发起网络请求获取天气数据
    }

    /**
     * 绑定并初始化所有UI控件。
     */
    private void initViews() {
        weatherLayout = findViewById(R.id.weather_layout);
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

        // 初始化RecyclerView和它的适配器
        futureForecastAdapter = new FutureForecastAdapter();
        rvFutureForecast.setLayoutManager(new LinearLayoutManager(this));
        rvFutureForecast.setAdapter(futureForecastAdapter);

        // 设置底部标签页
        setupTabs();
    }

    /**
     * 设置底部标签按钮的点击事件和初始状态。
     */
    private void setupTabs() {
        btnShowCurrent.setOnClickListener(v -> switchTab(true));
        btnShowForecast.setOnClickListener(v -> switchTab(false));
        switchTab(true); // 默认显示“当前天气”
    }

    /**
     * 根据参数切换显示的标签页。
     *
     * @param showCurrent 如果为true，显示“当前天气”；否则显示“未来预报”。
     */
    private void switchTab(boolean showCurrent) {
        layoutCurrent.setVisibility(showCurrent ? View.VISIBLE : View.GONE);
        layoutForecast.setVisibility(showCurrent ? View.GONE : View.VISIBLE);
        // 更新按钮的可用状态来模拟“选中”效果
        btnShowCurrent.setEnabled(!showCurrent);
        btnShowForecast.setEnabled(showCurrent);
    }

    /**
     * 使用OkHttp向高德天气API发起异步网络请求。
     */
    private void fetchWeatherData() {
        Request request = new Request.Builder()
                .url("https://restapi.amap.com/v3/weather/weatherInfo?city=610100&extensions=all&&key=78437de757a2693c3f9cb2aabf6f25fd")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 请求失败时，在主线程上显示一个Toast提示
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(WeatherActivity.this, "获取天气数据失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    // 使用Gson将JSON字符串解析为WeatherResponse对象
                    final WeatherResponse weatherResponse = gson.fromJson(responseBody, WeatherResponse.class);
                    // 在主线程上更新UI
                    runOnUiThread(() -> updateUi(weatherResponse));
                }
            }
        });
    }

    /**
     * 将从API获取到的天气数据更新到UI上。
     *
     * @param weatherResponse 解析后的天气数据对象。
     */
    private void updateUi(WeatherResponse weatherResponse) {
        if (weatherResponse != null && weatherResponse.getForecasts() != null && !weatherResponse.getForecasts().isEmpty()) {
            WeatherResponse.Forecast forecast = weatherResponse.getForecasts().get(0);
            if (forecast != null && forecast.getCasts() != null && !forecast.getCasts().isEmpty()) {
                WeatherResponse.Cast today = forecast.getCasts().get(0);

                // 更新顶部的实时天气信息
                tvCity.setText(forecast.getCity());
                tvWeatherStatus.setText(today.getDayweather());
                tvTemperature.setText(String.format("%s°", today.getDaytemp()));
                tvTempHighLow.setText(String.format("最高: %s° 最低: %s°", today.getDaytemp(), today.getNighttemp()));

                // 更新白天天气卡片
                tvDayWeather.setText(today.getDayweather());
                tvDayTemp.setText(String.format("%s°", today.getDaytemp()));
                tvDayWind.setText(String.format("%s %s级", today.getDaywind(), today.getDaypower()));

                // 更新夜间天气卡片
                tvNightWeather.setText(today.getNightweather());
                tvNightTemp.setText(String.format("%s°", today.getNighttemp()));
                tvNightWind.setText(String.format("%s %s级", today.getNightwind(), today.getNightpower()));

                // 更新“未来预报”页面的数据
                tvFutureCity.setText(forecast.getCity());
                if (forecast.getCasts().size() > 1) {
                    futureForecastAdapter.submitData(forecast.getCasts().subList(1, forecast.getCasts().size()));
                } else {
                    futureForecastAdapter.submitData(null);
                }

                // 根据当天的天气状况，动态更新页面背景
                updateWeatherBackground(today.getDayweather());
            }
        }
    }

    /**
     * 根据天气描述，动态设置页面的背景。
     *
     * @param weather 天气描述字符串（例如“晴”、“小雨”等）。
     */
    private void updateWeatherBackground(String weather) {
        if (weather.contains("晴")) {
            weatherLayout.setBackgroundResource(R.drawable.weather_background_sunny);
        } else if (weather.contains("雨")) {
            weatherLayout.setBackgroundResource(R.drawable.weather_background_rainy);
        } else if (weather.contains("多云")) {
            weatherLayout.setBackgroundResource(R.drawable.weather_background_cloudy);
        } else {
            // 对于其他未明确指定的天气，使用一个默认的背景
            weatherLayout.setBackgroundResource(R.drawable.weather_background_default);
        }
    }
}
