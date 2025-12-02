package com.example.exercise.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exercise.R;
import com.example.exercise.sdk.FluencyMonitor;

public class UserCenterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvUsername;
    private TextView tvSignature;
    private TextView tvFps;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);

        tvUsername = findViewById(R.id.tv_username);
        tvSignature = findViewById(R.id.tv_signature);
        tvFps = findViewById(R.id.tv_fps);

        tvUsername.setText(sharedPreferences.getString("username", "用户名"));
        tvSignature.setText(sharedPreferences.getString("signature", "欢迎来到信息App"));

        findViewById(R.id.tv_personal_info).setOnClickListener(this);
        findViewById(R.id.tv_favorites).setOnClickListener(this);
        findViewById(R.id.tv_history).setOnClickListener(this);
        findViewById(R.id.tv_weather).setOnClickListener(this);
        findViewById(R.id.tv_settings).setOnClickListener(this);
        findViewById(R.id.tv_about_us).setOnClickListener(this);
        findViewById(R.id.tv_feedback).setOnClickListener(this);

        FluencyMonitor.getInstance().start(fps -> {
            tvFps.setText(String.format("FPS: %d", fps));
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_personal_info) {
            Toast.makeText(this, "个人信息", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.tv_favorites) {
            Toast.makeText(this, "我的收藏", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.tv_history) {
            Toast.makeText(this, "浏览历史", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.tv_weather) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
        } else if (id == R.id.tv_settings) {
            Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.tv_about_us) {
            Toast.makeText(this, "关于我们", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.tv_feedback) {
            Toast.makeText(this, "意见反馈", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", tvUsername.getText().toString());
        editor.putString("signature", tvSignature.getText().toString());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FluencyMonitor.getInstance().stop();
        FluencyMonitor.getInstance().generateReport();
    }
}
