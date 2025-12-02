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

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private View btnWechatLogin;
    private View btnAppleLogin;

    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new UserDatabaseHelper(this);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnWechatLogin = findViewById(R.id.btn_wechat_login);
        btnAppleLogin = findViewById(R.id.btn_apple_login);

        etEmail.setText("test@example.com");
        etPassword.setText("123456");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (dbHelper.checkUser(email, password)) {
                    Intent intent = new Intent(LoginActivity.this, UserCenterActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "无效的账号或密码", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnWechatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "微信登录", Toast.LENGTH_SHORT).show();
            }
        });

        btnAppleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Apple登录", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
