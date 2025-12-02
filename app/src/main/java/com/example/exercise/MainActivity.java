package com.example.exercise;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exercise.sdk.AnrMonitor;
import com.example.exercise.ui.activity.LoginActivity;

/**
 * 应用的主入口Activity。
 * <p>
 * 这个Activity在应用启动时被首先加载。它的主要职责是：
 * 1. 初始化全局的SDK，例如ANR监控。
 * 2. 立即将用户重定向到登录页面（LoginActivity）。
 * 3. 启动登录页后，将自身从任务栈中移除，确保用户按返回键时不会回到这个空白的启动页。
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Activity创建时的回调方法。
     *
     * @param savedInstanceState 如果Activity被重新创建，此参数会包含之前保存的状态。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 启动ANR（应用无响应）监控，使其能在应用的整个生命周期中工作
        AnrMonitor.getInstance().start();

        // 创建一个意图（Intent）来启动登录页面
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        // 启动LoginActivity
        startActivity(intent);
        // 结束当前的MainActivity
        finish();
    }
}
