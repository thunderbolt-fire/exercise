package com.example.exercise;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View; // 非常重要使用这个接收View对象
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 个人中心页面 Activity。
 * 负责显示用户信息，并处理页面上的点击事件。
 */
public class UserCenterActivity extends AppCompatActivity implements View.OnClickListener {

    // UI 控件
    private TextView tvUsername;    // 用于显示用户名的TextView
    private TextView tvSignature;   // 用于显示用户签名的TextView

    // SharedPreferences 对象，用于轻量级的数据存储
    private SharedPreferences sharedPreferences;

    /**
     * Activity 创建时调用。
     * 在此方法中，我们初始化UI控件、SharedPreferences，加载并显示存储的用户信息，
     * 并为页面上的各个条目设置点击监听器。
     * @param savedInstanceState 如果Activity被重新创建，此参数包含之前保存的状态。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置此Activity的布局文件
        setContentView(R.layout.activity_user_center);

        // 获取 SharedPreferences 实例。"user_info" 是存储文件的名称，MODE_PRIVATE 表示只有本应用可以访问。
        sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);

        // 通过ID找到布局文件中的UI控件
        tvUsername = findViewById(R.id.tv_username);
        tvSignature = findViewById(R.id.tv_signature);

        // 从 SharedPreferences 中读取用户名和签名，并设置到TextView上。
        // 如果找不到对应的值，则使用提供的默认值（"用户名" 和 "欢迎来到信息App"）。
        tvUsername.setText(sharedPreferences.getString("username", "用户名"));
        tvSignature.setText(sharedPreferences.getString("signature", "欢迎来到信息App"));

        // 为各个可点击的TextView设置点击监听器
        // 由于本类实现了 View.OnClickListener 接口，所以可以直接将 this 作为监听器传入。
        findViewById(R.id.tv_personal_info).setOnClickListener(this);
        findViewById(R.id.tv_favorites).setOnClickListener(this);
        findViewById(R.id.tv_history).setOnClickListener(this);
        findViewById(R.id.tv_settings).setOnClickListener(this);
        findViewById(R.id.tv_about_us).setOnClickListener(this);
        findViewById(R.id.tv_feedback).setOnClickListener(this);
    }

    /**
     * 处理所有在此Activity中注册的点击事件。
     * 通过检查被点击 View 的 ID，来判断是哪个条目被点击，并作出相应的响应。
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
        } else if (id == R.id.tv_settings) {
            Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.tv_about_us) {
            Toast.makeText(this, "关于我们", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.tv_feedback) {
            Toast.makeText(this, "意见反馈", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 当Activity暂停时调用（例如，用户切换到其他应用或按Home键）。
     * 在此方法中，我们将用户在个人中心页面上可能修改过的信息（用户名和签名）保存到SharedPreferences中，
     * 以便下次打开时能够保持最新状态。
     */
    @Override
    protected void onPause() {
        super.onPause();
        // 获取 SharedPreferences 的编辑器对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // 将 TextView 中的当前文本存入 SharedPreferences
        editor.putString("username", tvUsername.getText().toString());
        editor.putString("signature", tvSignature.getText().toString());
        // 提交修改。apply()方法是异步的，效率更高，适用于不需要立即知道保存结果的场景。
        editor.apply();
    }
}
