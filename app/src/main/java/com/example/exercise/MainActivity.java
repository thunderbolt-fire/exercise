package com.example.exercise;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 应用的主入口 Activity。
 * 根据当前的逻辑，这个 Activity 的唯一作用是立即启动 LoginActivity，
 * 然后将自身从任务栈中移除。这样确保了用户总是从登录页面开始。
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Activity 创建时调用。
     * @param savedInstanceState 如果Activity被重新创建，此参数包含之前保存的状态。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 创建一个意图（Intent）来启动 LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        // 启动 LoginActivity
        startActivity(intent);
        // 结束当前的 MainActivity，以便用户按返回键时不会回到这个空白的页面
        finish();
    }
}
