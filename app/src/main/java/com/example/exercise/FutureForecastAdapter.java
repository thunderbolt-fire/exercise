package com.example.exercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FutureForecastAdapter extends RecyclerView.Adapter<FutureForecastAdapter.ForecastViewHolder> {

    private final List<WeatherResponse.Cast> data = new ArrayList<>();

    public void submitData(List<WeatherResponse.Cast> casts) {
        data.clear();
        if (casts != null) {
            data.addAll(casts);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_future_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        WeatherResponse.Cast cast = data.get(position);
        holder.tvDate.setText(cast.getDate());
        holder.tvWeek.setText(formatWeek(cast.getWeek()));
        holder.tvDayWeather.setText(cast.getDayweather());
        holder.tvNightWeather.setText(String.format("夜间 %s", cast.getNightweather()));
        holder.tvDayTemp.setText(String.format("%s°", cast.getDaytemp()));
        holder.tvNightTemp.setText(String.format("%s°", cast.getNighttemp()));
        holder.tvDayWind.setText(String.format("%s %s级", cast.getDaywind(), cast.getDaypower()));
        holder.tvNightWind.setText(String.format("夜间 %s %s级", cast.getNightwind(), cast.getNightpower()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private String formatWeek(String week) {
        if (week == null) {
            return "";
        }
        switch (week) {
            case "1":
                return "周一";
            case "2":
                return "周二";
            case "3":
                return "周三";
            case "4":
                return "周四";
            case "5":
                return "周五";
            case "6":
                return "周六";
            case "7":
                return "周日";
            default:
                return week;
        }
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        final TextView tvDate;
        final TextView tvWeek;
        final TextView tvDayWeather;
        final TextView tvNightWeather;
        final TextView tvDayTemp;
        final TextView tvNightTemp;
        final TextView tvDayWind;
        final TextView tvNightWind;

        ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_forecast_date);
            tvWeek = itemView.findViewById(R.id.tv_forecast_week);
            tvDayWeather = itemView.findViewById(R.id.tv_forecast_day_weather);
            tvNightWeather = itemView.findViewById(R.id.tv_forecast_night_weather);
            tvDayTemp = itemView.findViewById(R.id.tv_forecast_day_temp);
            tvNightTemp = itemView.findViewById(R.id.tv_forecast_night_temp);
            tvDayWind = itemView.findViewById(R.id.tv_forecast_day_wind);
            tvNightWind = itemView.findViewById(R.id.tv_forecast_night_wind);
        }
    }
}

