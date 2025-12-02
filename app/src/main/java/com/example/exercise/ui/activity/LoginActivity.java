package com.example.exercise.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exercise.R;
import com.example.exercise.data.UserDatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 登录页面 Activity。
 * <p>
 * 负责处理用户的登录流程，包括：
 * 1. 显示登录界面，包含邮箱、密码输入框和登录按钮。
 * 2. 处理用户的输入和点击事件。
 * 3. 调用数据库帮助类（UserDatabaseHelper）来验证用户凭据。
 * 4. 登录成功后，跳转到个人中心页面（UserCenterActivity）。
 * 5. 登录失败时，向用户显示提示信息。
 */
public class LoginActivity extends AppCompatActivity {

    // UI 控件
    private TextInputEditText etEmail;       // 邮箱输入框
    private TextInputEditText etPassword;    // 密码输入框
    private Button btnLogin;                 // 登录按钮
    private View btnWechatLogin;             // 微信登录按钮
    private View btnAppleLogin;              // Apple登录按钮

    // 数据库帮助类的实例，用于访问和操作用户数据库
    private UserDatabaseHelper dbHelper;

    /**
     * Activity创建时的回调方法。
     *
     * @param savedInstanceState 如果Activity被重新创建，此参数会包含之前保存的状态。
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

        // 将默认的测试用户名和密码设置到输入框中，方便调试
        etEmail.setText("test@example.com");
        etPassword.setText("123456");

        // 为登录按钮设置点击监听器
        btnLogin.setOnClickListener(v -> {
            // 获取用户输入的邮箱和密码
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            // 调用数据库帮助类的方法来验证用户凭据
            if (dbHelper.checkUser(email, password)) {
                // 如果验证成功，创建一个意图（Intent）以启动个人中心页面
                Intent intent = new Intent(LoginActivity.this, UserCenterActivity.class);
                // 启动新的Activity
                startActivity(intent);
                // 结束当前的LoginActivity，这样用户按返回键时不会回到登录页面
                finish();
            } else {
                // 如果验证失败，显示一个短暂的提示消息（Toast）
                Toast.makeText(LoginActivity.this, "无效的账号或密码", Toast.LENGTH_SHORT).show();
            }
        });

        // 为微信登录按钮设置点击监听器
        btnWechatLogin.setOnClickListener(v -> {
            // 显示一个提示，表示功能正在开发中
            Toast.makeText(LoginActivity.this, "微信登录功能开发中", Toast.LENGTH_SHORT).show();
        });

        // 为Apple登录按钮设置点击监听器
        btnAppleLogin.setOnClickListener(v -> {
            // 显示一个提示，表示功能正在开发中
            Toast.makeText(LoginActivity.this, "Apple登录功能开发中", Toast.LENGTH_SHORT).show();
        });
    }
}
