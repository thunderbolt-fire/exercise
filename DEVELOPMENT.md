# 开发指南

## 目录

1. [环境准备](#环境准备)
2. [项目导入](#项目导入)
3. [项目构建](#项目构建)
4. [运行调试](#运行调试)
5. [开发流程](#开发流程)
6. [代码规范](#代码规范)
7. [常见问题](#常见问题)
8. [测试指南](#测试指南)

---

## 环境准备

### 必需软件

1. **Android Studio**
   - 版本: Android Studio Hedgehog (2023.1.1) 或更高
   - 下载: https://developer.android.com/studio
   - 推荐使用最新稳定版

2. **JDK**
   - 版本: JDK 11 或更高
   - Android Studio 通常自带 JDK
   - 验证: `java -version`

3. **Android SDK**
   - Minimum SDK: API 23 (Android 6.0)
   - Target SDK: API 36 (Android 14+)
   - Compile SDK: API 36

### 推荐工具

- **Android Emulator**: 用于测试（推荐 Pixel 5 API 33+）
- **Git**: 版本控制
- **Gradle**: 7.0+ (由 Android Studio 管理)

### 系统要求

- **操作系统**: Windows 10/11, macOS 10.14+, Linux (Ubuntu 18.04+)
- **内存**: 至少 8GB RAM (推荐 16GB)
- **磁盘空间**: 至少 10GB 可用空间

---

## 项目导入

### 从 Git 克隆

```bash
# 克隆项目
git clone https://github.com/thunderbolt-fire/exercise.git

# 进入项目目录
cd exercise
```

### 导入到 Android Studio

1. 打开 Android Studio
2. 选择 `File` → `Open`
3. 选择项目根目录（包含 `build.gradle.kts` 的目录）
4. 点击 `OK`
5. 等待 Gradle 同步完成

### 首次同步

首次打开项目时，Android Studio 会自动执行以下操作：

1. 下载所需的 Gradle 版本
2. 下载项目依赖库
3. 索引项目文件
4. 生成必要的构建文件

**注意**: 这个过程可能需要几分钟，取决于网络速度。

---

## 项目构建

### Gradle 同步

```bash
# 命令行同步（可选）
./gradlew sync
```

或在 Android Studio 中：
- 点击工具栏的 `Sync Project with Gradle Files` 图标
- 快捷键: `Ctrl+Shift+O` (Windows/Linux) 或 `Cmd+Shift+O` (Mac)

### 清理项目

当遇到构建问题时，可以清理项目：

```bash
# 清理构建产物
./gradlew clean

# 清理并重新构建
./gradlew clean build
```

或在 Android Studio 中：
- `Build` → `Clean Project`
- `Build` → `Rebuild Project`

### 构建 APK

```bash
# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease
```

生成的 APK 位置：
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release-unsigned.apk`

### 构建配置

项目使用 Kotlin DSL 构建脚本：

**根目录 `build.gradle.kts`**:
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
}
```

**app 目录 `build.gradle.kts`**:
```kotlin
android {
    namespace = "com.example.exercise"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "com.example.exercise"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
```

---

## 运行调试

### 在模拟器上运行

1. **创建模拟器**:
   - `Tools` → `Device Manager`
   - 点击 `Create Device`
   - 选择设备类型（推荐 Pixel 5）
   - 选择系统镜像（推荐 API 33 或更高）
   - 完成创建

2. **启动模拟器**:
   - 在 Device Manager 中点击启动图标

3. **运行应用**:
   - 点击工具栏的运行按钮（绿色三角）
   - 快捷键: `Shift+F10` (Windows/Linux) 或 `Ctrl+R` (Mac)

### 在真机上运行

1. **启用开发者选项**:
   - 打开手机设置
   - 关于手机 → 连续点击版本号 7 次
   - 返回设置 → 开发者选项

2. **启用 USB 调试**:
   - 开发者选项 → USB 调试（打开）

3. **连接设备**:
   - 使用 USB 线连接手机和电脑
   - 手机上允许 USB 调试授权

4. **验证连接**:
   ```bash
   adb devices
   ```
   应该显示已连接的设备

5. **运行应用**:
   - 在 Android Studio 中选择你的设备
   - 点击运行按钮

### 调试技巧

#### 使用 Logcat

查看日志输出：
- Android Studio 底部的 `Logcat` 标签
- 过滤器: 选择应用包名 `com.example.exercise`
- 日志级别: Verbose, Debug, Info, Warn, Error

**查看性能监控日志**:
```bash
# 查看 FPS 报告
adb logcat -s FluencyReport

# 查看 ANR 报告
adb logcat -s AnrReport
```

#### 断点调试

1. 在代码行号旁点击，设置断点（红点）
2. 点击调试按钮（虫子图标）或 `Shift+F9`
3. 应用暂停在断点处
4. 使用调试工具栏：
   - Step Over (F8): 单步执行
   - Step Into (F7): 进入方法
   - Step Out (Shift+F8): 跳出方法
   - Resume (F9): 继续执行

#### 布局检查器

查看 UI 层次结构：
- `Tools` → `Layout Inspector`
- 选择正在运行的进程
- 可以查看视图属性和层次关系

---

## 开发流程

### 添加新功能

1. **创建新分支**:
   ```bash
   git checkout -b feature/new-feature
   ```

2. **编写代码**:
   - 遵循现有的代码结构
   - UI 层放在 `ui` 包
   - 数据层放在 `data` 包
   - SDK 功能放在 `sdk` 包

3. **测试功能**:
   - 在模拟器或真机上测试
   - 检查 Logcat 输出
   - 验证 UI 和交互

4. **提交代码**:
   ```bash
   git add .
   git commit -m "feat: 添加新功能"
   git push origin feature/new-feature
   ```

### 修复 Bug

1. **创建 bugfix 分支**:
   ```bash
   git checkout -b bugfix/issue-description
   ```

2. **定位问题**:
   - 使用断点调试
   - 查看 Logcat 错误信息
   - 使用 Android Profiler 分析性能

3. **修复并测试**:
   - 修复代码
   - 验证修复效果
   - 确保没有引入新问题

4. **提交修复**:
   ```bash
   git add .
   git commit -m "fix: 修复XXX问题"
   git push origin bugfix/issue-description
   ```

### 代码审查

在提交 Pull Request 之前：

1. **自我审查**:
   - 检查代码格式
   - 移除调试代码和注释
   - 确保没有提交敏感信息

2. **运行静态分析**:
   - `Analyze` → `Inspect Code`
   - 修复警告和错误

3. **测试覆盖**:
   - 确保关键功能都经过测试
   - 验证边界情况

---

## 代码规范

### Java 编码规范

遵循 Google Java Style Guide：

1. **命名规范**:
   ```java
   // 类名: 大驼峰
   public class UserCenterActivity extends AppCompatActivity {}
   
   // 方法名: 小驼峰
   public void fetchWeatherData() {}
   
   // 常量: 全大写+下划线
   private static final String DATABASE_NAME = "user.db";
   
   // 变量: 小驼峰
   private TextView tvUsername;
   ```

2. **缩进**: 4 个空格（不使用 Tab）

3. **行宽**: 最多 100 字符

4. **注释**:
   ```java
   /**
    * 类的文档注释
    * <p>
    * 详细说明类的作用和使用方法
    */
   public class MyClass {
       /**
        * 方法的文档注释
        * @param param 参数说明
        * @return 返回值说明
        */
       public int myMethod(String param) {
           // 单行注释说明关键逻辑
           return 0;
       }
   }
   ```

### Android 组件规范

1. **Activity 命名**: `功能名Activity.java`
   - 示例: `LoginActivity.java`, `UserCenterActivity.java`

2. **布局文件命名**: `activity_功能名.xml`
   - 示例: `activity_login.xml`, `activity_user_center.xml`

3. **ID 命名**: `类型_描述`
   - 示例: `tv_username`, `btn_login`, `et_email`

4. **资源命名**:
   - Drawable: `类型_描述`
     - 示例: `ic_back.png`, `weather_background_sunny.xml`
   - String: `模块_描述`
     - 示例: `login_title`, `error_invalid_credentials`

### 包结构规范

```
com.example.exercise
├── MainActivity.java          # 应用入口
├── sdk/                       # SDK 层
│   ├── FluencyMonitor.java
│   └── AnrMonitor.java
├── ui/                        # UI 层
│   ├── activity/              # Activity 组件
│   │   ├── LoginActivity.java
│   │   ├── UserCenterActivity.java
│   │   └── WeatherActivity.java
│   └── adapter/               # 适配器
│       └── FutureForecastAdapter.java
└── data/                      # 数据层
    ├── UserDatabaseHelper.java
    └── model/                 # 数据模型
        ├── User.java
        └── WeatherResponse.java
```

### Git 提交规范

使用 Conventional Commits：

- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建或工具相关

示例：
```
feat: 添加天气预报功能
fix: 修复登录页面崩溃问题
docs: 更新 README 文档
refactor: 重构数据库访问层
```

---

## 常见问题

### 构建失败

**问题**: `Gradle sync failed`

**解决方案**:
1. 检查网络连接
2. 清理项目: `./gradlew clean`
3. 使 Caches 无效: `File` → `Invalidate Caches / Restart`
4. 检查 Gradle 版本兼容性

---

**问题**: `Duplicate resources`

**解决方案**:
1. 检查 `res` 目录下是否有重名文件
2. 确保同一类型资源名称唯一
3. 删除重复的资源文件

---

**问题**: `AAPT error: resource ... not found`

**解决方案**:
1. 同步 Gradle: `Sync Project with Gradle Files`
2. 清理并重新构建项目
3. 检查资源文件是否存在拼写错误

---

### 运行时错误

**问题**: `ClassCastException`

**解决方案**:
1. 检查 XML 布局中的控件类型
2. 确保 Java 代码中的类型与 XML 一致
3. 使用更通用的类型（如 `View` 而不是 `TextView`）

---

**问题**: `ActivityNotFoundException`

**解决方案**:
1. 检查 `AndroidManifest.xml` 中是否注册了 Activity
2. 确保包名路径正确
3. 重新同步 Gradle

---

**问题**: 应用崩溃，无错误信息

**解决方案**:
1. 查看 Logcat 中的完整堆栈跟踪
2. 使用断点调试定位崩溃位置
3. 检查是否有空指针异常

---

### 性能问题

**问题**: UI 卡顿

**解决方案**:
1. 使用 FluencyMonitor 检测 FPS
2. 将耗时操作移到后台线程
3. 优化布局层次，减少过度绘制
4. 使用 Android Profiler 分析性能

---

**问题**: 内存泄漏

**解决方案**:
1. 确保在 `onDestroy()` 中释放资源
2. 避免在静态变量中持有 Activity 引用
3. 使用 LeakCanary 检测内存泄漏
4. 及时关闭数据库和 Cursor

---

### 网络请求问题

**问题**: 网络请求失败

**解决方案**:
1. 检查 `AndroidManifest.xml` 中是否添加了 `INTERNET` 权限
2. 确认 API Key 有效
3. 检查网络连接
4. 查看 Logcat 中的错误信息

---

**问题**: `NetworkOnMainThreadException`

**解决方案**:
1. 使用 OkHttp 的异步 API (`enqueue`)
2. 不要使用同步 API (`execute`)
3. 将网络操作放在后台线程

---

## 测试指南

### 单元测试

位置: `app/src/test/java/`

运行单元测试：
```bash
./gradlew test
```

或在 Android Studio 中右键测试类，选择 `Run 'TestClassName'`

### 集成测试

位置: `app/src/androidTest/java/`

运行集成测试：
```bash
./gradlew connectedAndroidTest
```

需要连接模拟器或真机

### 手动测试清单

#### 登录功能
- [ ] 输入正确的账号密码，点击登录，能成功跳转
- [ ] 输入错误的账号密码，显示错误提示
- [ ] 点击微信登录，显示"功能开发中"提示
- [ ] 点击 Apple 登录，显示"功能开发中"提示

#### 个人中心
- [ ] 显示用户昵称和签名
- [ ] FPS 数据实时更新
- [ ] 点击"天气预报"跳转到天气页面
- [ ] 点击"模拟 ANR"，5 秒后 Logcat 输出 ANR 报告
- [ ] 退出后重新进入，用户信息保持

#### 天气预报
- [ ] 成功加载并显示天气数据
- [ ] 根据天气状况更换背景
- [ ] "当前天气"和"未来预报"标签切换正常
- [ ] 未来预报列表正确显示

#### 性能监控
- [ ] FPS 监控实时显示帧率
- [ ] 退出个人中心后，Logcat 输出 FluencyReport
- [ ] ANR 监控能捕获并报告主线程阻塞

---

## 最佳实践

### 1. 资源管理
- 及时关闭数据库连接和 Cursor
- 在 `onDestroy()` 中释放监控器和监听器
- 避免在静态变量中持有 Context 引用

### 2. 线程管理
- UI 操作必须在主线程
- 网络请求使用异步 API
- 耗时操作放在后台线程
- 使用 `runOnUiThread()` 更新 UI

### 3. 错误处理
- 捕获并处理异常
- 提供友好的错误提示
- 记录错误日志方便调试

### 4. 安全性
- **不要在代码中硬编码敏感信息**（API Key、密码等）
- 使用 `BuildConfig` 或本地配置文件存储 API Key
- 注意：项目当前使用的高德天气 API Key 是公开的测试密钥
- 生产环境中应申请自己的 API Key 并妥善保管

### 5. 代码质量
- 编写清晰的注释
- 遵循单一职责原则
- 避免重复代码
- 定期重构优化

### 6. 性能优化
- 避免在循环中创建对象
- 使用 ViewHolder 模式
- 合理使用缓存
- 优化布局层次

---

## 扩展阅读

- [Android 官方文档](https://developer.android.com/docs)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
- [OkHttp 文档](https://square.github.io/okhttp/)
- [Gson 用户指南](https://github.com/google/gson/blob/master/UserGuide.md)

---

## 获取帮助

如果遇到问题：

1. 查看本文档的"常见问题"部分
2. 搜索 Stack Overflow
3. 查看 Android 官方文档
4. 在项目仓库提交 Issue

祝开发愉快！ 🚀
