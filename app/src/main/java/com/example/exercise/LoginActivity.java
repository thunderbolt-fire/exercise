package com.example.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

/**
 * 登录页面 Activity。
 * 负责处理用户登录逻辑，包括UI交互、输入验证和页面跳转。
 */
public class LoginActivity extends AppCompatActivity {

    // UI 控件
    private TextInputEditText etEmail;       // 邮箱输入框
    private TextInputEditText etPassword;    // 密码输入框
    private Button btnLogin;        // 登录按钮
    private View btnWechatLogin; // 微信登录按钮 (使用View以匹配LinearLayout)
    private View btnAppleLogin;  // Apple登录按钮 (使用View以匹配LinearLayout)

    // 数据库帮助类的实例，用于访问数据库
    private UserDatabaseHelper dbHelper;

    /**
     * Activity 创建时调用。
     * 在此方法中，我们初始化UI控件、数据库帮助类，并设置按钮的点击监听器。
     * @param savedInstanceState 如果Activity被重新创建，此参数包含之前保存的状态。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置此Activity的布局文件
        setContentView(R.layout.activity_login);

        // 初始化数据库帮助类
        dbHelper = new UserDatabaseHelper(this);

        // 通过ID找到布局文件中的UI控件
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnWechatLogin = findViewById(R.id.btn_wechat_login);
        btnAppleLogin = findViewById(R.id.btn_apple_login);

        // 为登录按钮设置点击监听器
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户输入的邮箱和密码
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                // 调用数据库帮助类的方法来验证凭据
                if (dbHelper.checkUser(email, password)) {
                    // 如果验证成功，创建一个意图（Intent）以启动 UserCenterActivity
                    Intent intent = new Intent(LoginActivity.this, UserCenterActivity.class);
                    // 启动新的Activity
                    startActivity(intent);
                    // 结束当前的LoginActivity，这样用户按返回键时不会回到登录页面
                    finish();
                } else {
                    // 如果验证失败，显示一个短暂的提示消息（Toast）
                    Toast.makeText(LoginActivity.this, "无效的账号或密码", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 为微信登录按钮设置点击监听器
        btnWechatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示一个提示，表示功能正在开发中
                Toast.makeText(LoginActivity.this, "微信登录", Toast.LENGTH_SHORT).show();
            }
        });

        // 为Apple登录按钮设置点击监听器
        btnAppleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示一个提示，表示功能正在开发中
                Toast.makeText(LoginActivity.this, "Apple登录", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
