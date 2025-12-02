package com.example.exercise.sdk;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Choreographer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 流畅性监控器，用于实时计算并报告应用的帧率（FPS）。
 * <p>
 * 工作原理：
 * 1. 利用 Android 底层的 `Choreographer` API，该API会在每一帧绘制时发出回调。
 * 2. 通过计算两次 `doFrame` 回调之间的时间差，以及这段时间内的总帧数，可以精确地计算出当前的实时帧率。
 * 3. SDK会持续收集每一秒的FPS数据，并在监控结束时生成一份包含平均/最低帧率和卡顿次数的性能报告。
 */
public class FluencyMonitor {

    // 单例实例，确保全局只有一个流畅性监控器
    private static final FluencyMonitor INSTANCE = new FluencyMonitor();
    // 用于接收FPS数据的回调监听器
    private FluencyListener listener;
    // 一个与主线程Looper绑定的Handler，用于确保回调在主线程上执行
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // 用于存储监控期间每一秒的FPS历史数据
    private final List<Integer> fpsHistory = new ArrayList<>();
    // 监控开始时的时间戳，用于计算总时长
    private long monitoringStartTime;

    // 上一帧绘制的时间戳（纳秒）
    private long lastFrameTimeNanos = 0;
    // 在一个计算周期内累计的帧数
    private int frameCount = 0;
    // 监控是否正在运行的标志位
    private boolean isRunning = false;

    /**
     * Choreographer的回调实现。
     * 每当新的一帧被绘制时，系统就会调用此处的 `doFrame` 方法。
     */
    private final Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            // 如果监控已停止，则直接返回
            if (!isRunning) {
                return;
            }

            // 如果是第一帧，则只记录时间戳，不进行计算
            if (lastFrameTimeNanos == 0) {
                lastFrameTimeNanos = frameTimeNanos;
            } else {
                // 累计帧数
                frameCount++;
            }

            // 计算自上一个计算周期以来的时间差
            long intervalNanos = frameTimeNanos - lastFrameTimeNanos;
            // 当时间差超过1秒时，进行一次FPS计算和数据记录
            if (intervalNanos >= TimeUnit.SECONDS.toNanos(1)) {
                // 计算FPS的公式：总帧数 / (时间差 / 1秒的纳秒数)
                final double fps = frameCount * 1_000_000_000.0 / intervalNanos;
                int currentFps = (int) Math.round(fps);
                // 将当前计算出的FPS添加到历史记录中
                fpsHistory.add(currentFps);
                // 如果设置了监听器，则通过Handler在主线程上回调FPS数据
                if (listener != null) {
                    mainHandler.post(() -> listener.onFluencyData(currentFps));
                }
                // 重置上一计算周期的时间戳和帧数，开始新的计算周期
                lastFrameTimeNanos = frameTimeNanos;
                frameCount = 0;
            }

            // 请求下一次的帧绘制回调，形成一个持续的监控循环
            Choreographer.getInstance().postFrameCallback(this);
        }
    };

    /**
     * 私有构造函数，防止外部直接创建实例。
     */
    private FluencyMonitor() {}

    /**
     * 获取FluencyMonitor的单例。
     *
     * @return FluencyMonitor的唯一实例。
     */
    public static FluencyMonitor getInstance() {
        return INSTANCE;
    }

    /**
     * 启动流畅性监控。
     *
     * @param listener 用于接收实时FPS数据的回调监听器。
     */
    public void start(FluencyListener listener) {
        if (isRunning) return;
        this.listener = listener;
        this.lastFrameTimeNanos = 0;
        this.frameCount = 0;
        this.fpsHistory.clear();
        this.monitoringStartTime = System.currentTimeMillis();
        this.isRunning = true;
        Choreographer.getInstance().postFrameCallback(frameCallback);
    }

    /**
     * 停止流畅性监控。
     */
    public void stop() {
        isRunning = false;
        Choreographer.getInstance().removeFrameCallback(frameCallback);
    }

    /**
     * 生成并打印流畅性报告。
     * 报告包含平均FPS、最低FPS和高卡顿次数等关键性能指标。
     */
    public void generateReport() {
        if (fpsHistory.isEmpty()) {
            Log.d("FluencyReport", "No fluency data collected.");
            return;
        }

        long durationSeconds = (System.currentTimeMillis() - monitoringStartTime) / 1000;
        int sum = 0;
        int jankyFramesCount = 0;
        // 定义高卡顿的阈值，当FPS低于50时，我们认为是一次高卡顿
        final int JANK_THRESHOLD = 50;

        for (int fps : fpsHistory) {
            sum += fps;
            if (fps < JANK_THRESHOLD) {
                jankyFramesCount++;
            }
        }

        double averageFps = (double) sum / fpsHistory.size();
        int minFps = Collections.min(fpsHistory);

        StringBuilder report = new StringBuilder();
        report.append("\n");
        report.append("================ Fluency Report ================\n");
        report.append(String.format("| Monitoring Duration: %d seconds\n", durationSeconds));
        report.append(String.format("| Average FPS: %.2f\n", averageFps));
        report.append(String.format("| Minimum FPS: %d\n", minFps));
        report.append(String.format("| High Jank Count (FPS < %d): %d\n", JANK_THRESHOLD, jankyFramesCount));
        report.append("================================================");

        Log.d("FluencyReport", report.toString());

        // 清理数据，为下一次监控做准备
        fpsHistory.clear();
        listener = null;
    }

    /**
     * 用于接收实时FPS数据的回调接口。
     */
    public interface FluencyListener {
        /**
         * 当有新的FPS数据时，此方法被调用。
         *
         * @param fps 当前的帧率。
         */
        void onFluencyData(int fps);
    }
}
