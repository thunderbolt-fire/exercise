# API 文档

## 概述

本文档详细说明了 Exercise App 使用的外部 API 和内部 SDK API 接口。

## 外部 API

### 高德天气 API

#### 基本信息
- **API 提供商**: 高德地图开放平台
- **官方文档**: https://lbs.amap.com/api/webservice/guide/api/weatherinfo
- **使用场景**: 获取实时天气和未来天气预报

#### 请求详情

**请求 URL**:
```
https://restapi.amap.com/v3/weather/weatherInfo
```

**请求方法**: `GET`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| key | String | 是 | 用户的 API Key | 78437de757a2693c3f9cb2aabf6f25fd |
| city | String | 是 | 城市编码 | 610100 (西安市) |
| extensions | String | 否 | 气象类型，base=实时，all=预报 | all |

**当前项目使用的请求**:
```
GET https://restapi.amap.com/v3/weather/weatherInfo?city=610100&extensions=all&key=78437de757a2693c3f9cb2aabf6f25fd
```

#### 响应格式

**成功响应示例**:
```json
{
  "status": "1",
  "count": "1",
  "info": "OK",
  "infocode": "10000",
  "forecasts": [
    {
      "city": "西安市",
      "adcode": "610100",
      "province": "陕西",
      "reporttime": "2025-12-18 03:00:00",
      "casts": [
        {
          "date": "2025-12-18",
          "week": "3",
          "dayweather": "晴",
          "nightweather": "晴",
          "daytemp": "8",
          "nighttemp": "-3",
          "daywind": "东北",
          "nightwind": "东北",
          "daypower": "≤3",
          "nightpower": "≤3"
        },
        {
          "date": "2025-12-19",
          "week": "4",
          "dayweather": "多云",
          "nightweather": "多云",
          "daytemp": "10",
          "nighttemp": "-2",
          "daywind": "东",
          "nightwind": "东",
          "daypower": "≤3",
          "nightpower": "≤3"
        }
        // ... 更多天气预报数据
      ]
    }
  ]
}
```

**字段说明**:

- `status`: 返回状态，1 表示成功，0 表示失败
- `count`: 返回结果的数量
- `info`: 返回状态说明
- `infocode`: 返回状态码，10000 表示正常
- `forecasts`: 预报数据列表
  - `city`: 城市名称
  - `adcode`: 城市编码
  - `province`: 省份名称
  - `reporttime`: 数据发布时间
  - `casts`: 未来几天的天气预报数组
    - `date`: 日期
    - `week`: 星期（1-7）
    - `dayweather`: 白天天气现象
    - `nightweather`: 晚间天气现象
    - `daytemp`: 白天温度（℃）
    - `nighttemp`: 晚间温度（℃）
    - `daywind`: 白天风向
    - `nightwind`: 晚间风向
    - `daypower`: 白天风力
    - `nightpower`: 晚间风力

#### 错误处理

**常见错误码**:

| infocode | 说明 | 处理方式 |
|----------|------|----------|
| 10000 | 请求成功 | - |
| 10001 | key 不存在或过期 | 检查 API Key 是否有效 |
| 10002 | 没有权限使用该服务 | 检查账号权限 |
| 10003 | 访问已超出日访问量 | 等待配额恢复或升级套餐 |
| 10004 | 单位时间内访问过于频繁 | 增加请求间隔 |
| 10005 | IP 白名单出错 | 检查 IP 白名单设置 |
| 20000 | 请求参数非法 | 检查参数格式 |
| 20001 | 缺少必填参数 | 补充必填参数 |
| 20800 | 规划点（包括起点、终点、途经点）不在中国范围内 | 修改城市编码 |

**项目中的错误处理**:
```java
@Override
public void onFailure(@NonNull Call call, @NonNull IOException e) {
    e.printStackTrace();
    runOnUiThread(() -> 
        Toast.makeText(WeatherActivity.this, 
            "获取天气数据失败", 
            Toast.LENGTH_SHORT).show()
    );
}
```

#### 数据模型映射

**Java 数据模型** (`WeatherResponse.java`):

```java
public class WeatherResponse {
    private String status;
    private String count;
    private String info;
    private String infocode;
    private List<Forecast> forecasts;
    
    public static class Forecast {
        private String city;
        private String adcode;
        private String province;
        private String reporttime;
        private List<Cast> casts;
    }
    
    public static class Cast {
        private String date;
        private String week;
        private String dayweather;
        private String nightweather;
        private String daytemp;
        private String nighttemp;
        private String daywind;
        private String nightwind;
        private String daypower;
        private String nightpower;
    }
}
```

**Gson 自动映射**:
```java
final WeatherResponse weatherResponse = 
    gson.fromJson(responseBody, WeatherResponse.class);
```

---

## 内部 SDK API

### FluencyMonitor (流畅性监控)

#### 单例获取

```java
FluencyMonitor monitor = FluencyMonitor.getInstance();
```

#### 启动监控

```java
/**
 * 启动流畅性监控
 * @param listener FPS 数据回调监听器
 */
public void start(FluencyListener listener)
```

**使用示例**:
```java
FluencyMonitor.getInstance().start(fps -> {
    tvFps.setText(String.format("FPS: %d", fps));
});
```

**参数说明**:
- `listener`: 实现 `FluencyListener` 接口的回调对象
  - `void onFluencyData(int fps)`: 当有新的 FPS 数据时被调用

**注意事项**:
- 回调在主线程执行，可以直接更新 UI
- 每秒回调一次，提供实时 FPS 数据
- 重复调用 `start()` 会被忽略

#### 停止监控

```java
/**
 * 停止流畅性监控
 */
public void stop()
```

**使用示例**:
```java
@Override
protected void onDestroy() {
    super.onDestroy();
    FluencyMonitor.getInstance().stop();
}
```

**注意事项**:
- 必须在 Activity 销毁时调用，避免内存泄漏
- 停止后不再接收 FPS 回调

#### 生成报告

```java
/**
 * 生成并打印流畅性报告
 * 报告包含平均 FPS、最低 FPS 和高卡顿次数
 */
public void generateReport()
```

**使用示例**:
```java
@Override
protected void onDestroy() {
    super.onDestroy();
    FluencyMonitor.getInstance().stop();
    FluencyMonitor.getInstance().generateReport();
}
```

**报告格式**:
```
================ Fluency Report ================
| Monitoring Duration: 30 seconds
| Average FPS: 58.50
| Minimum FPS: 45
| High Jank Count (FPS < 50): 3
================================================
```

**报告字段说明**:
- **Monitoring Duration**: 监控总时长（秒）
- **Average FPS**: 平均帧率
- **Minimum FPS**: 最低帧率
- **High Jank Count**: 高卡顿次数（FPS < 50 的秒数）

**日志输出**:
- Tag: `FluencyReport`
- Level: `DEBUG`
- 查看方式: Android Studio Logcat 或 `adb logcat -s FluencyReport`

---

### AnrMonitor (ANR 监控)

#### 单例获取

```java
AnrMonitor monitor = AnrMonitor.getInstance();
```

#### 启动监控

```java
/**
 * 启动 ANR 监控
 */
public void start()
```

**使用示例**:
```java
// 在应用启动时启动
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AnrMonitor.getInstance().start();
    // ...
}
```

**工作原理**:
- 启动后台看门狗线程
- 每 5 秒向主线程发送一个任务
- 如果主线程 5 秒内未响应，判定为 ANR
- 自动捕获主线程堆栈并生成报告

**注意事项**:
- 应该在应用启动时（`MainActivity.onCreate()`）启动
- 全局只有一个监控实例
- 重复调用 `start()` 会被忽略

#### 停止监控

```java
/**
 * 停止 ANR 监控
 */
public void stop()
```

**使用示例**:
```java
@Override
protected void onDestroy() {
    super.onDestroy();
    AnrMonitor.getInstance().stop();
}
```

**注意事项**:
- 通常不需要手动停止，让监控在整个应用生命周期运行
- 停止后看门狗线程会被中断并释放资源

#### 模拟 ANR

在 `UserCenterActivity` 中提供了"模拟 ANR"按钮用于测试：

```java
// 强制主线程休眠 10 秒，触发 ANR
SystemClock.sleep(10000);
```

**测试步骤**:
1. 启动应用并登录
2. 在个人中心点击"模拟 ANR"按钮
3. 等待约 5 秒
4. 查看 Logcat 输出的 ANR 报告

#### ANR 报告格式

```
================ ANR Report ================
Timestamp: Wed Dec 18 03:02:18 CST 2025

--- Thread: main (Main Thread) ---
    at android.os.SystemClock.sleep(SystemClock.java:132)
    at com.example.exercise.ui.activity.UserCenterActivity.onClick(UserCenterActivity.java:101)
    at android.view.View.performClick(View.java:7506)
    at android.widget.TextView.performClick(TextView.java:14230)
    ...
===============================================
```

**报告字段说明**:
- **Timestamp**: ANR 发生的时间戳
- **Thread**: 线程名称（通常是 main）
- **Stack Trace**: 主线程的完整堆栈信息
  - 可以定位到导致 ANR 的代码位置
  - 包含完整的方法调用链

**日志输出**:
- Tag: `AnrReport`
- Level: `ERROR`
- 查看方式: Android Studio Logcat 或 `adb logcat -s AnrReport`

---

## 数据库 API

### UserDatabaseHelper

#### 创建实例

```java
UserDatabaseHelper dbHelper = new UserDatabaseHelper(context);
```

#### 添加用户

```java
/**
 * 向数据库中添加一个新用户
 * @param user 要添加的 User 对象
 */
public void addUser(User user)
```

**使用示例**:
```java
User newUser = new User("user@example.com", "password123");
dbHelper.addUser(newUser);
```

**注意事项**:
- 如果用户已存在（邮箱重复），则忽略此次插入
- 使用 `CONFLICT_IGNORE` 策略处理冲突

#### 验证用户

```java
/**
 * 检查数据库中是否存在与给定邮箱和密码匹配的用户
 * @param email 用户输入的邮箱
 * @param password 用户输入的密码
 * @return 如果凭据匹配返回 true，否则返回 false
 */
public boolean checkUser(String email, String password)
```

**使用示例**:
```java
String email = etEmail.getText().toString();
String password = etPassword.getText().toString();

if (dbHelper.checkUser(email, password)) {
    // 登录成功
    Intent intent = new Intent(this, UserCenterActivity.class);
    startActivity(intent);
    finish();
} else {
    // 登录失败
    Toast.makeText(this, "无效的账号或密码", Toast.LENGTH_SHORT).show();
}
```

**默认测试账号**:
- 邮箱: `test@example.com`
- 密码: `123456`

---

## SharedPreferences API

### 用户信息存储

项目使用 SharedPreferences 存储用户昵称和签名。

#### 保存数据

```java
SharedPreferences sharedPreferences = 
    getSharedPreferences("user_info", Context.MODE_PRIVATE);
SharedPreferences.Editor editor = sharedPreferences.edit();
editor.putString("username", "用户昵称");
editor.putString("signature", "个性签名");
editor.apply(); // 异步保存
```

**存储时机**: `UserCenterActivity.onPause()`

#### 读取数据

```java
SharedPreferences sharedPreferences = 
    getSharedPreferences("user_info", Context.MODE_PRIVATE);
String username = sharedPreferences.getString("username", "用户名");
String signature = sharedPreferences.getString("signature", "欢迎来到信息App");
```

**读取时机**: `UserCenterActivity.onCreate()`

**存储键值**:
- `user_info`: SharedPreferences 文件名
- `username`: 用户昵称的键
- `signature`: 个性签名的键

---

## 网络请求 API

### OkHttp 使用

项目使用 OkHttp 3.x 进行网络请求。

#### 创建客户端

```java
private final OkHttpClient client = new OkHttpClient();
```

#### 构建请求

```java
Request request = new Request.Builder()
    .url("https://restapi.amap.com/v3/weather/weatherInfo?city=610100&extensions=all&key=xxx")
    .build();
```

#### 异步执行

```java
client.newCall(request).enqueue(new Callback() {
    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        // 请求失败处理
        e.printStackTrace();
        runOnUiThread(() -> 
            Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) 
            throws IOException {
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            // 解析响应
            WeatherResponse weatherResponse = 
                gson.fromJson(responseBody, WeatherResponse.class);
            // 在主线程更新 UI
            runOnUiThread(() -> updateUi(weatherResponse));
        }
    }
});
```

**注意事项**:
- 回调在子线程执行，更新 UI 需要使用 `runOnUiThread()`
- 必须检查 `response.isSuccessful()`
- `response.body().string()` 只能调用一次

---

## 总结

本文档涵盖了 Exercise App 使用的所有 API：

1. **外部 API**: 高德天气 API，提供实时和预报天气数据
2. **性能监控 SDK**: FluencyMonitor 和 AnrMonitor，提供 FPS 和 ANR 监控能力
3. **数据库 API**: UserDatabaseHelper，管理用户数据
4. **本地存储 API**: SharedPreferences，存储用户信息
5. **网络请求 API**: OkHttp，处理 HTTP 请求

所有 API 都经过实际测试，可以直接在项目中使用。
