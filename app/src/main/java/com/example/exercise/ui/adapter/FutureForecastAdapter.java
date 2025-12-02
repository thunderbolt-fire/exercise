package com.example.exercise.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exercise.R;
import com.example.exercise.data.model.WeatherResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 未来天气预报的RecyclerView适配器。
 * <p>
 * 负责将从API获取的未来多日天气数据（`WeatherResponse.Cast`对象列表）
 * 绑定到`item_future_forecast.xml`布局上，并在列表中显示。
 */
public class FutureForecastAdapter extends RecyclerView.Adapter<FutureForecastAdapter.ForecastViewHolder> {

    // 内部持有的天气数据列表，用于驱动适配器
    private final List<WeatherResponse.Cast> data = new ArrayList<>();

    /**
     * 更新适配器的数据源。
     * 此方法会先清空旧数据，然后添加新数据，并通知UI刷新。
     *
     * @param casts 从API返回的未来多日天气预报列表，可以为null或空。
     */
    public void submitData(List<WeatherResponse.Cast> casts) {
        data.clear();
        if (casts != null) {
            data.addAll(casts);
        }
        // 通知RecyclerView数据已发生变化，需要重绘
        notifyDataSetChanged();
    }

    /**
     * 当RecyclerView需要一个新的ViewHolder时调用。
     *
     * @param parent   ViewHolder将被添加到其中的父ViewGroup。
     * @param viewType 视图类型，在有多种item类型时使用。
     * @return 一个新的ForecastViewHolder实例。
     */
    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用LayoutInflater从XML布局文件创建新的视图
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_future_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    /**
     * 当RecyclerView需要将数据绑定到一个ViewHolder上时调用。
     *
     * @param holder   需要被绑定数据的ViewHolder。
     * @param position 列表中的位置。
     */
    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        WeatherResponse.Cast cast = data.get(position);
        // 将天气数据绑定到ViewHolder中的UI控件上
        holder.tvDate.setText(cast.getDate());
        holder.tvWeek.setText(formatWeek(cast.getWeek()));
        holder.tvDayWeather.setText(cast.getDayweather());
        holder.tvNightWeather.setText(String.format("夜间 %s", cast.getNightweather()));
        holder.tvDayTemp.setText(String.format("%s°", cast.getDaytemp()));
        holder.tvNightTemp.setText(String.format("%s°", cast.getNighttemp()));
        holder.tvDayWind.setText(String.format("%s %s级", cast.getDaywind(), cast.getDaypower()));
        holder.tvNightWind.setText(String.format("夜间 %s %s级", cast.getNightwind(), cast.getNightpower()));
    }

    /**
     * 返回数据列表中的项目总数。
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * 将API返回的数字星期（1-7）格式化为中文（周一-周日）。
     *
     * @param week 代表星期的数字字符串。
     * @return 格式化后的中文字符串。
     */
    private String formatWeek(String week) {
        if (week == null) {
            return "";
        }
        switch (week) {
            case "1": return "周一";
            case "2": return "周二";
            case "3": return "周三";
            case "4": return "周四";
            case "5": return "周五";
            case "6": return "周六";
            case "7": return "周日";
            default: return week;
        }
    }

    /**
     * ViewHolder内部类，用于缓存item布局中的视图控件，避免每次绑定数据时都重复调用`findViewById`。
     */
    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        final TextView tvDate, tvWeek, tvDayWeather, tvNightWeather, tvDayTemp, tvNightTemp, tvDayWind, tvNightWind;

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
