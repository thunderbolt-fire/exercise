package com.example.exercise;

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

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ConstraintLayout weatherLayout;
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

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initViews();
        fetchWeatherData();
    }

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
        futureForecastAdapter = new FutureForecastAdapter();
        rvFutureForecast.setLayoutManager(new LinearLayoutManager(this));
        rvFutureForecast.setAdapter(futureForecastAdapter);
        setupTabs();
    }

    private void setupTabs() {
        btnShowCurrent.setOnClickListener(v -> switchTab(true));
        btnShowForecast.setOnClickListener(v -> switchTab(false));
        switchTab(true);
    }

    private void switchTab(boolean showCurrent) {
        layoutCurrent.setVisibility(showCurrent ? View.VISIBLE : View.GONE);
        layoutForecast.setVisibility(showCurrent ? View.GONE : View.VISIBLE);
        btnShowCurrent.setEnabled(!showCurrent);
        btnShowForecast.setEnabled(showCurrent);
    }

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

    private void updateUi(WeatherResponse weatherResponse) {
        if (weatherResponse != null && weatherResponse.getForecasts() != null && !weatherResponse.getForecasts().isEmpty()) {
            WeatherResponse.Forecast forecast = weatherResponse.getForecasts().get(0);
            if (forecast != null && forecast.getCasts() != null && !forecast.getCasts().isEmpty()) {
                WeatherResponse.Cast today = forecast.getCasts().get(0);
                tvCity.setText(forecast.getCity());
                tvWeatherStatus.setText(today.getDayweather());
                tvTemperature.setText(String.format("%s°", today.getDaytemp()));
                tvTempHighLow.setText(String.format("最高: %s° 最低: %s°", today.getDaytemp(), today.getNighttemp()));

                tvDayWeather.setText(today.getDayweather());
                tvDayTemp.setText(String.format("%s°", today.getDaytemp()));
                tvDayWind.setText(String.format("%s %s级", today.getDaywind(), today.getDaypower()));

                tvNightWeather.setText(today.getNightweather());
                tvNightTemp.setText(String.format("%s°", today.getNighttemp()));
                tvNightWind.setText(String.format("%s %s级", today.getNightwind(), today.getNightpower()));

                tvFutureCity.setText(forecast.getCity());
                if (forecast.getCasts().size() > 1) {
                    futureForecastAdapter.submitData(forecast.getCasts().subList(1, forecast.getCasts().size()));
                } else {
                    futureForecastAdapter.submitData(null);
                }

                updateWeatherBackground(today.getDayweather());
            }
        }
    }

    private void updateWeatherBackground(String weather) {
        if (weather.contains("晴")) {
            weatherLayout.setBackgroundResource(R.drawable.weather_background_sunny);
        } else if (weather.contains("雨")) {
            weatherLayout.setBackgroundResource(R.drawable.weather_background_rainy);
        } else if (weather.contains("多云")) {
            weatherLayout.setBackgroundResource(R.drawable.weather_background_cloudy);
        } else {
            weatherLayout.setBackgroundResource(R.drawable.weather_background_default);
        }
    }
}
