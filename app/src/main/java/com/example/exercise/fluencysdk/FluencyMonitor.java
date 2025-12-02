package com.example.exercise.fluencysdk;

import android.os.Handler;
import android.os.Looper;
import android.view.Choreographer;

import java.util.concurrent.TimeUnit;

public class FluencyMonitor {

    private static final FluencyMonitor INSTANCE = new FluencyMonitor();
    private FluencyListener listener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

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
                if (listener != null) {
                    mainHandler.post(() -> listener.onFluencyData((int) Math.round(fps)));
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
        this.isRunning = true;
        Choreographer.getInstance().postFrameCallback(frameCallback);
    }

    public void stop() {
        isRunning = false;
        Choreographer.getInstance().removeFrameCallback(frameCallback);
        listener = null;
    }

    public interface FluencyListener {
        void onFluencyData(int fps);
    }
}
