package com.example.exercise.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exercise.R;
import com.example.exercise.sdk.FluencyMonitor;

/**
 * 个人中心页面 Activity。
 * <p>
 * 负责显示用户信息，并处理页面上的各种交互事件，包括：
 * 1. 从SharedPreferences中加载并显示用户的昵称和签名。
 * 2. 启动流畅性监控（FluencyMonitor）并在界面上实时显示FPS。
 * 3. 页面销毁时，停止流畅性监控并生成性能报告。
 * 4. 提供一个“模拟ANR”的按钮，用于测试ANR监控SDK的功能。
 * 5. 处理到天气预报页面的跳转。
 */
public class UserCenterActivity extends AppCompatActivity implements View.OnClickListener {

    // UI 控件
    private TextView tvUsername;    // 用于显示用户名的TextView
    private TextView tvSignature;   // 用于显示用户签名的TextView
    private TextView tvFps;         // 用于显示实时FPS的TextView

    // SharedPreferences 对象，用于轻量级的数据存储（如用户信息）
    private SharedPreferences sharedPreferences;

    /**
     * Activity创建时的回调方法。
     *
     * @param savedInstanceState 如果Activity被重新创建，此参数会包含之前保存的状态。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        // 获取 SharedPreferences 实例
        sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);

        // 绑定UI控件
        tvUsername = findViewById(R.id.tv_username);
        tvSignature = findViewById(R.id.tv_signature);
        tvFps = findViewById(R.id.tv_fps);

        // 加载并显示存储的用户信息
        tvUsername.setText(sharedPreferences.getString("username", "用户名"));
        tvSignature.setText(sharedPreferences.getString("signature", "欢迎来到信息App"));

        // 为页面上的所有可点击视图设置点击监听器
        findViewById(R.id.tv_personal_info).setOnClickListener(this);
        findViewById(R.id.tv_favorites).setOnClickListener(this);
        findViewById(R.id.tv_history).setOnClickListener(this);
        findViewById(R.id.tv_weather).setOnClickListener(this);
        findViewById(R.id.tv_settings).setOnClickListener(this);
        findViewById(R.id.tv_about_us).setOnClickListener(this);
        findViewById(R.id.tv_feedback).setOnClickListener(this);
        findViewById(R.id.btn_simulate_anr).setOnClickListener(this);

        // 启动流畅性监控，并通过回调将实时FPS显示在tvFps上
        FluencyMonitor.getInstance().start(fps -> {
            tvFps.setText(String.format("FPS: %d", fps));
        });
    }

    /**
     * 处理所有在此Activity中注册的点击事件。
     *
     * @param v 被点击的 View 对象。
     */
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
            // 跳转到天气预报页面
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
        } else if (id == R.id.tv_settings) {
            Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.tv_about_us) {
            Toast.makeText(this, "关于我们", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.tv_feedback) {
            Toast.makeText(this, "意见反馈", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btn_simulate_anr) {
            // 模拟ANR：强制主线程休眠10秒，以阻塞UI并触发ANR
            SystemClock.sleep(10000);
        }
    }

    /**
     * 当Activity暂停时的回调方法。
     * 在此保存可能已更改的用户信息。
     */
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", tvUsername.getText().toString());
        editor.putString("signature", tvSignature.getText().toString());
        editor.apply(); // 异步保存
    }

    /**
     * Activity销毁时的回调方法。
     * 在此停止流畅性监控并生成报告，以清理资源并避免内存泄漏。
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止流畅性监控
        FluencyMonitor.getInstance().stop();
        // 生成并打印流畅性报告
        FluencyMonitor.getInstance().generateReport();
    }
}
