# 项目架构设计文档

## 概述

Exercise App 是一个采用分层架构设计的 Android 应用，主要演示了用户认证、个人中心、天气预报和性能监控等核心功能。本文档详细说明了项目的架构设计、技术选型和设计模式。

## 技术栈

### 开发语言
- **Java 11**: 项目主要开发语言

### Android 组件
- **Minimum SDK**: 23 (Android 6.0)
- **Target SDK**: 36 (Android 14+)
- **AndroidX**: 使用最新的 AndroidX 库

### 核心依赖库
```gradle
// UI 组件
- androidx.appcompat (向后兼容)
- com.google.android.material (Material Design 组件)
- androidx.constraintlayout (约束布局)
- androidx.recyclerview (列表视图)

// 网络请求
- com.squareup.okhttp3:okhttp:4.12.0 (HTTP 客户端)

// 数据解析
- com.google.code.gson:gson:2.10.1 (JSON 解析)

// 测试框架
- junit (单元测试)
- androidx.test (Android 测试)
```

## 架构设计

### 整体架构图

```
┌─────────────────────────────────────────────────┐
│              UI Layer (用户界面层)                │
│  ┌──────────────────────────────────────────┐   │
│  │  Activities (Activity 组件)               │   │
│  │  - MainActivity (启动器)                  │   │
│  │  - LoginActivity (登录)                   │   │
│  │  - UserCenterActivity (个人中心)         │   │
│  │  - WeatherActivity (天气预报)            │   │
│  └──────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────┐   │
│  │  Adapters (适配器)                        │   │
│  │  - FutureForecastAdapter                 │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
                      ↕
┌─────────────────────────────────────────────────┐
│           Data Layer (数据层)                     │
│  ┌──────────────────────────────────────────┐   │
│  │  Database (数据库)                         │   │
│  │  - UserDatabaseHelper                    │   │
│  └──────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────┐   │
│  │  Models (数据模型)                         │   │
│  │  - User                                  │   │
│  │  - WeatherResponse                       │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
                      ↕
┌─────────────────────────────────────────────────┐
│            SDK Layer (SDK 层)                     │
│  ┌──────────────────────────────────────────┐   │
│  │  Performance Monitoring (性能监控)        │   │
│  │  - FluencyMonitor (流畅性监控)           │   │
│  │  - AnrMonitor (ANR 监控)                 │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
```

### 分层架构详解

#### 1. UI Layer (UI 层)

**职责**: 负责用户界面的展示和用户交互的处理

**包结构**: `com.example.exercise.ui.*`

**主要组件**:

- **Activity 组件** (`ui.activity`)
  - `MainActivity`: 应用启动入口，初始化全局 SDK，重定向到登录页
  - `LoginActivity`: 处理用户登录逻辑，验证用户凭据
  - `UserCenterActivity`: 展示用户信息，集成性能监控功能
  - `WeatherActivity`: 展示天气信息，实现标签页切换

- **Adapter 组件** (`ui.adapter`)
  - `FutureForecastAdapter`: RecyclerView 适配器，展示未来天气预报列表

**设计模式**:
- **MVC 模式**: Activity 作为 Controller，处理业务逻辑和用户交互
- **ViewHolder 模式**: 在 RecyclerView 中缓存视图，优化性能

#### 2. Data Layer (数据层)

**职责**: 负责数据的持久化存储、访问和数据模型定义

**包结构**: `com.example.exercise.data.*`

**主要组件**:

- **数据库管理** (`data`)
  - `UserDatabaseHelper`: SQLiteOpenHelper 的实现，管理用户数据库
    - 创建和升级数据库结构
    - 提供用户数据的 CRUD 操作
    - 实现用户登录验证逻辑

- **数据模型** (`data.model`)
  - `User`: POJO 类，封装用户信息（邮箱、密码）
  - `WeatherResponse`: 天气 API 响应的数据模型，支持 Gson 解析

**数据存储方案**:
1. **SQLite 数据库**: 存储用户账号密码
2. **SharedPreferences**: 存储用户昵称和签名

#### 3. SDK Layer (SDK 层)

**职责**: 提供客户端性能监控能力

**包结构**: `com.example.exercise.sdk.*`

**主要组件**:

- **FluencyMonitor (流畅性监控)**
  - 基于 `Choreographer` API 实现
  - 实时计算并回调 FPS 数据
  - 生成性能报告（平均 FPS、最低 FPS、卡顿次数）
  - 采用单例模式，全局唯一

- **AnrMonitor (ANR 监控)**
  - 采用"看门狗"（Watchdog）方案
  - 通过后台线程监控主线程响应状态
  - 捕获并记录 ANR 时的主线程堆栈信息
  - 采用单例模式，全局唯一

**设计模式**:
- **单例模式**: 确保监控器全局唯一
- **观察者模式**: 通过回调接口传递实时监控数据
- **看门狗模式**: ANR 监控的核心实现方案

## 关键技术实现

### 1. 用户认证流程

```
用户输入凭据
    ↓
LoginActivity.onClick()
    ↓
UserDatabaseHelper.checkUser()
    ↓
SQLite 查询验证
    ↓
验证成功 → 跳转 UserCenterActivity
验证失败 → 显示错误提示
```

### 2. 流畅性监控原理

```
FluencyMonitor.start()
    ↓
Choreographer.postFrameCallback()
    ↓
每一帧触发 doFrame()
    ↓
计算时间间隔和帧数
    ↓
当间隔 ≥ 1秒时，计算 FPS
    ↓
通过回调通知 UI 更新
```

**FPS 计算公式**:
```
FPS = 帧数 × 1,000,000,000 / 时间间隔(纳秒)
```

### 3. ANR 监控原理

```
AnrMonitor.start()
    ↓
启动看门狗线程
    ↓
周期性向主线程发送任务
    ↓
等待 5 秒
    ↓
检查主线程是否响应
    ↓
未响应 → 捕获堆栈，生成报告
已响应 → 继续下一轮监控
```

### 4. 天气数据获取流程

```
WeatherActivity.onCreate()
    ↓
fetchWeatherData()
    ↓
OkHttp 异步请求高德 API
    ↓
Gson 解析 JSON 响应
    ↓
runOnUiThread() 更新 UI
    ↓
根据天气状况更新背景
```

## 线程模型

### 主线程
- UI 渲染和事件处理
- SharedPreferences 读写
- 接收性能监控回调

### 工作线程
- **OkHttp 线程池**: 处理网络请求
- **ANR 看门狗线程**: 监控主线程响应状态
- **SQLite 操作**: 虽然在主线程执行，但建议迁移到后台线程

### 线程安全
- `volatile` 关键字: 确保 ANR 监控标志位的可见性
- `Handler`: 确保回调在主线程执行
- 单例模式: 线程安全的懒汉式实现

## 生命周期管理

### Activity 生命周期

```
MainActivity:
  onCreate() → 启动 AnrMonitor → 跳转 LoginActivity → finish()

LoginActivity:
  onCreate() → 初始化 UI → 设置监听器
  用户登录成功 → 跳转 UserCenterActivity → finish()

UserCenterActivity:
  onCreate() → 启动 FluencyMonitor
  onPause() → 保存用户信息到 SharedPreferences
  onDestroy() → 停止 FluencyMonitor → 生成报告

WeatherActivity:
  onCreate() → 初始化 UI → 发起网络请求
  onResponse() → 更新 UI
```

## 数据流转

### 登录流程数据流
```
用户输入 (UI)
  ↓
LoginActivity (Controller)
  ↓
UserDatabaseHelper (Data Layer)
  ↓
SQLite 数据库
  ↓
验证结果 → LoginActivity
  ↓
Intent → UserCenterActivity
```

### 性能监控数据流
```
Choreographer (系统)
  ↓
FluencyMonitor.doFrame()
  ↓
计算 FPS
  ↓
FluencyListener.onFluencyData()
  ↓
Handler.post() → 主线程
  ↓
TextView.setText() (UI)
```

## 资源管理

### 布局文件
- `activity_main.xml`: 空白启动页
- `activity_login.xml`: 登录界面（邮箱、密码、第三方登录按钮）
- `activity_user_center.xml`: 个人中心（头像、昵称、菜单列表、FPS 显示）
- `activity_weather.xml`: 天气预报（当前天气、未来预报、标签切换）
- `item_future_forecast.xml`: 未来天气列表项

### Drawable 资源
- 用户头像和图标
- 天气背景资源
  - `weather_background_sunny.xml`: 晴天背景
  - `weather_background_rainy.xml`: 雨天背景
  - `weather_background_cloudy.xml`: 多云背景
  - `weather_background_default.xml`: 默认背景

### 权限配置
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
仅需要网络权限用于天气 API 请求

## 性能优化

### 已实现的优化
1. **ViewHolder 模式**: RecyclerView 中缓存视图，避免重复 `findViewById`
2. **异步网络请求**: 使用 OkHttp 的异步 API，不阻塞主线程
3. **单例模式**: 监控器全局唯一，减少对象创建开销
4. **数据库连接管理**: 及时关闭 Cursor 和 Database，避免内存泄漏

### 可优化的方向
1. 将数据库操作迁移到后台线程
2. 引入 ViewModel 和 LiveData，实现数据与 UI 的解耦
3. 使用 Retrofit 替代 OkHttp，简化网络层代码
4. 引入 Room 数据库，替代原生 SQLite
5. 添加图片加载库（如 Glide）优化图片加载

## 扩展性设计

### SDK 模块化
SDK 层设计为独立的包 (`com.example.exercise.sdk`)，具有以下特点：
- **高内聚**: SDK 功能集中，职责单一
- **低耦合**: 通过回调接口与 UI 层交互
- **易扩展**: 可以轻松添加新的监控功能（如内存监控、网络监控）

### 数据层抽象
数据层通过 Helper 类封装数据库操作，未来可以：
- 实现 Repository 模式统一数据来源
- 添加网络数据源和本地缓存机制
- 实现数据同步和离线支持

### UI 层解耦
UI 层通过 Intent 在不同 Activity 间导航，未来可以：
- 引入 Navigation Component 统一导航管理
- 实现单 Activity 多 Fragment 架构
- 添加 ViewModel 实现配置变更时的数据保持

## 总结

Exercise App 采用了清晰的分层架构，将 UI、数据和 SDK 功能分离到不同的包中。这种设计具有以下优势：

1. **可维护性强**: 代码结构清晰，易于定位和修改
2. **可测试性好**: 各层职责单一，便于编写单元测试
3. **可扩展性高**: SDK 层可独立演进，UI 层可灵活调整
4. **性能优异**: 合理使用线程和资源管理，保证流畅性

项目展示了 Android 开发的核心技能，包括 UI 设计、数据持久化、网络请求、性能监控等，是学习 Android 开发的优秀示例。
