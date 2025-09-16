package leesche.smartrecycling.base.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;

public class ThrottleUtils {
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final Map<String, Long> lastExecutionMap = new HashMap<>();
    private static final Map<String, Runnable> pendingRunnables = new HashMap<>();

    /**
     * 节流执行方法（立即执行第一次，之后在指定间隔内不执行）
     * @param key 用于标识不同的节流操作
     * @param intervalMillis 节流时间间隔（毫秒）
     * @param runnable 要执行的操作
     */
    public static void throttle(final String key, long intervalMillis, final Runnable runnable) {
        Long lastExecutionTime = lastExecutionMap.get(key);
        long currentTime = System.currentTimeMillis();

        // 如果是第一次执行或已超过间隔时间，立即执行
        if (lastExecutionTime == null || (currentTime - lastExecutionTime >= intervalMillis)) {
            lastExecutionMap.put(key, currentTime);
            runnable.run();
        } else {
            // 否则，取消之前pending的操作（如果有），并重新调度一个延迟执行
            cancelPending(key);
            
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    lastExecutionMap.put(key, System.currentTimeMillis());
                    runnable.run();
                    pendingRunnables.remove(key);
                }
            };
            
            pendingRunnables.put(key, r);
            long delay = intervalMillis - (currentTime - lastExecutionTime);
            handler.postDelayed(r, delay);
        }
    }

    /**
     * 节流执行方法（延迟执行第一次，之后在指定间隔内不执行）
     * @param key 用于标识不同的节流操作
     * @param intervalMillis 节流时间间隔（毫秒）
     * @param runnable 要执行的操作
     */
    public static void throttleTrailing(final String key, long intervalMillis, final Runnable runnable) {
        Long lastExecutionTime = lastExecutionMap.get(key);
        long currentTime = System.currentTimeMillis();

        // 如果是第一次执行或已超过间隔时间
        if (lastExecutionTime == null || (currentTime - lastExecutionTime >= intervalMillis)) {
            lastExecutionMap.put(key, currentTime);
            cancelPending(key);
            handler.postDelayed(() -> {
                lastExecutionMap.put(key, System.currentTimeMillis());
                runnable.run();
            }, intervalMillis);
        }
        // 否则忽略这次调用
    }

    /**
     * 取消指定key的节流操作
     * @param key 要取消的节流操作key
     */
    public static void cancelThrottle(String key) {
        cancelPending(key);
        lastExecutionMap.remove(key);
    }

    private static void cancelPending(String key) {
        Runnable pending = pendingRunnables.remove(key);
        if (pending != null) {
            handler.removeCallbacks(pending);
        }
    }
}