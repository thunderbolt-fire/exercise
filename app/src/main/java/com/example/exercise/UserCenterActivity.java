package com.example.exercise;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserCenterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvUsername;
    private TextView tvSignature;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);

        tvUsername = findViewById(R.id.tv_username);
        tvSignature = findViewById(R.id.tv_signature);

        tvUsername.setText(sharedPreferences.getString("username", "用户名"));
        tvSignature.setText(sharedPreferences.getString("signature", "欢迎来到信息App"));

        findViewById(R.id.tv_personal_info).setOnClickListener(this);
        findViewById(R.id.tv_favorites).setOnClickListener(this);
        findViewById(R.id.tv_history).setOnClickListener(this);
        findViewById(R.id.tv_settings).setOnClickListener(this);
        findViewById(R.id.tv_about_us).setOnClickListener(this);
        findViewById(R.id.tv_feedback).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_personal_info) {
            Toast.makeText(this, "个人信息", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.tv_favorites) {
            Toast.makeText(this, "我的收藏", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.tv_history) {
            Toast.makeText(this, "浏览历史", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.tv_settings) {
            Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.tv_about_us) {
            Toast.makeText(this, "关于我们", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.tv_feedback) {
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
}
