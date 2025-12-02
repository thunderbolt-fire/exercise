package com.example.exercise.sdk;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class AnrMonitor {

    private static final AnrMonitor INSTANCE = new AnrMonitor();
    private AnrWatchdogThread watchdogThread;

    private AnrMonitor() {}

    public static AnrMonitor getInstance() {
        return INSTANCE;
    }

    public void start() {
        if (watchdogThread == null) {
            watchdogThread = new AnrWatchdogThread();
            watchdogThread.start();
        }
    }

    public void stop() {
        if (watchdogThread != null) {
            watchdogThread.interrupt();
            watchdogThread = null;
        }
    }

    private static class AnrWatchdogThread extends Thread {
        private static final int ANR_TIMEOUT_MS = 5000; // 5 seconds
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        private volatile boolean mainThreadResponded = true;

        public AnrWatchdogThread() {
            super("ANR Watchdog");
        }

        private final Runnable tickle = () -> mainThreadResponded = true;

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    mainThreadResponded = false;
                    mainThreadHandler.post(tickle);

                    Thread.sleep(ANR_TIMEOUT_MS);

                    if (!mainThreadResponded) {
                        // Main thread did not respond in time, potential ANR
                        captureAndLogAnrReport();
                    }
                } catch (InterruptedException e) {
                    // Thread was interrupted, exit gracefully
                    break;
                }
            }
        }

        private void captureAndLogAnrReport() {
            StringBuilder report = new StringBuilder();
            report.append("\n");
            report.append("================ ANR Report ================\n");
            report.append("Timestamp: ").append(new java.util.Date()).append("\n");

            Thread mainThread = Looper.getMainLooper().getThread();
            report.append("\n--- Thread: ").append(mainThread.getName()).append(" (Main Thread) ---\n");
            for (StackTraceElement element : mainThread.getStackTrace()) {
                report.append("    at ").append(element.toString()).append("\n");
            }

            report.append("===============================================");
            Log.e("AnrReport", report.toString());
        }
    }
}
