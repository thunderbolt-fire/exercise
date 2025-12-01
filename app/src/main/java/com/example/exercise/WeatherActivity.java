package com.example.exercise;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private TextView tvCity, tvWeatherStatus, tvTemperature, tvTempHighLow;
    private TextView tvDayWeather, tvDayTemp, tvDayWind;
    private TextView tvNightWeather, tvNightTemp, tvNightWind;

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
    }

    private void fetchWeatherData() {
        Request request = new Request.Builder()
                .url("https://restapi.amap.com/v3/weather/weatherInfo?city=110101&extensions=all&&key=78437de757a2693c3f9cb2aabf6f25fd")
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
            }
        }
    }
}
