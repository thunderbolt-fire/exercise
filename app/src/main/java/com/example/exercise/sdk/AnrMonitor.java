package com.example.exercise.sdk;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * ANR（应用无响应）监控器。
 * 采用“看门狗”（Watchdog）方案实现，不依赖任何特殊权限，稳定且可靠。
 * <p>
 * 工作原理：
 * 1. 启动一个后台“看门狗”线程，专门用于监控主线程（UI线程）的响应状态。
 * 2. 看门狗线程会周期性地（每隔5秒）向主线程的消息队列发送一个轻量级的任务。
 * 3. 如果主线程能够及时执行这个任务，说明它没有被阻塞；如果主线程因耗时操作而卡顿，就无法按时执行该任务。
 * 4. 当看门狗线程在等待一段时间后，发现主线程未能执行任务，就判定发生了ANR，并立即捕获主线程当前的堆栈信息，生成报告。
 */
public class AnrMonitor {

    // 单例实例，确保全局只有一个ANR监控器
    private static final AnrMonitor INSTANCE = new AnrMonitor();
    // 后台的看门狗线程实例
    private AnrWatchdogThread watchdogThread;

    /**
     * 私有构造函数，防止外部直接创建实例。
     */
    private AnrMonitor() {}

    /**
     * 获取AnrMonitor的单例。
     *
     * @return AnrMonitor的唯一实例。
     */
    public static AnrMonitor getInstance() {
        return INSTANCE;
    }

    /**
     * 启动ANR监控。
     * 如果监控线程尚未运行，则创建并启动它。
     */
    public void start() {
        if (watchdogThread == null) {
            watchdogThread = new AnrWatchdogThread();
            watchdogThread.start();
        }
    }

    /**
     * 停止ANR监控。
     * 中断看门狗线程，使其停止运行，并释放资源。
     */
    public void stop() {
        if (watchdogThread != null) {
            watchdogThread.interrupt();
            watchdogThread = null;
        }
    }

    /**
     * 内部类，实现了具体的“看门狗”逻辑。
     */
    private static class AnrWatchdogThread extends Thread {
        // ANR的超时阈值，设置为5秒，与Android系统的ANR判断标准一致
        private static final int ANR_TIMEOUT_MS = 5000; // 5 seconds
        // 一个与主线程Looper绑定的Handler，用于向主线程发送任务
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        // 一个volatile布尔标志，用于标记主线程是否在规定时间内响应。volatile确保了跨线程的可见性。
        private volatile boolean mainThreadResponded = true;

        /**
         * 构造函数，为线程设置一个描述性的名称。
         */
        public AnrWatchdogThread() {
            super("ANR Watchdog");
        }

        // 一个将被发送到主线程的轻量级任务。它的唯一作用就是将mainThreadResponded标志位重置为true，表示主线程“还活着”。
        private final Runnable tickle = () -> mainThreadResponded = true;

        /**
         * 线程的主循环体。
         */
        @Override
        public void run() {
            // 只要线程没有被中断，就持续循环
            while (!isInterrupted()) {
                try {
                    // 1. 在每次检查开始前，先将响应标志位设为false
                    mainThreadResponded = false;
                    // 2. 向主线程发送“喂狗”任务。如果主线程没卡住，它会很快执行这个任务，把标志位改回true。
                    mainThreadHandler.post(tickle);

                    // 3. 看门狗线程自己休眠5秒，给主线程留下响应时间
                    Thread.sleep(ANR_TIMEOUT_MS);

                    // 4. 5秒后，检查主线程是否已经响应。如果标志位仍然是false，说明主线程被卡住了。
                    if (!mainThreadResponded) {
                        // 判定发生ANR，立即捕获并记录报告
                        captureAndLogAnrReport();
                    }
                } catch (InterruptedException e) {
                    // 如果线程在休眠时被外部中断（例如调用了stop()方法），则跳出循环，优雅地终止线程。
                    break;
                }
            }
        }

        /**
         * 捕获并记录ANR报告。
         * 报告包含时间戳和主线程的完整堆栈信息，这些是分析ANR原因的关键。
         */
        private void captureAndLogAnrReport() {
            StringBuilder report = new StringBuilder();
            report.append("\n");
            report.append("================ ANR Report ================\n");
            report.append("Timestamp: ").append(new java.util.Date()).append("\n");

            // 获取主线程的实例
            Thread mainThread = Looper.getMainLooper().getThread();
            report.append("\n--- Thread: ").append(mainThread.getName()).append(" (Main Thread) ---\n");
            // 获取并遍历主线程的堆栈轨迹
            for (StackTraceElement element : mainThread.getStackTrace()) {
                report.append("    at ").append(element.toString()).append("\n");
            }

            report.append("===============================================");
            // 使用Error级别打印日志，使其在Logcat中以红色显示，更醒目
            Log.e("AnrReport", report.toString());
        }
    }
}
