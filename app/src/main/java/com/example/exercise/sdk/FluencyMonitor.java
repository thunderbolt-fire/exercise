package com.example.exercise.sdk;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Choreographer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FluencyMonitor {

    private static final FluencyMonitor INSTANCE = new FluencyMonitor();
    private FluencyListener listener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final List<Integer> fpsHistory = new ArrayList<>();
    private long monitoringStartTime;

    private long lastFrameTimeNanos = 0;
    private int frameCount = 0;
    private boolean isRunning = false;

    private final Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            if (!isRunning) {
                return;
            }

            if (lastFrameTimeNanos == 0) {
                lastFrameTimeNanos = frameTimeNanos;
            } else {
                frameCount++;
            }

            long intervalNanos = frameTimeNanos - lastFrameTimeNanos;
            if (intervalNanos >= TimeUnit.SECONDS.toNanos(1)) {
                final double fps = frameCount * 1_000_000_000.0 / intervalNanos;
                int currentFps = (int) Math.round(fps);
                fpsHistory.add(currentFps);
                if (listener != null) {
                    mainHandler.post(() -> listener.onFluencyData(currentFps));
                }
                lastFrameTimeNanos = frameTimeNanos;
                frameCount = 0;
            }

            Choreographer.getInstance().postFrameCallback(this);
        }
    };

    private FluencyMonitor() {}

    public static FluencyMonitor getInstance() {
        return INSTANCE;
    }

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

    public void stop() {
        isRunning = false;
        Choreographer.getInstance().removeFrameCallback(frameCallback);
    }

    public void generateReport() {
        if (fpsHistory.isEmpty()) {
            Log.d("FluencyReport", "No fluency data collected.");
            return;
        }

        long durationSeconds = (System.currentTimeMillis() - monitoringStartTime) / 1000;
        int sum = 0;
        int jankyFramesCount = 0;
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

        fpsHistory.clear();
        listener = null;
    }

    public interface FluencyListener {
        void onFluencyData(int fps);
    }
}
