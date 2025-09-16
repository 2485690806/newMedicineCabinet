package leesche.smartrecycling.base.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;

public class DebounceUtils {
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final Map<String, Runnable> runnableMap = new HashMap<>();

    /**
     * 防抖执行方法
     * @param key 用于标识不同的防抖操作（通常可以用 view.getId()）
     * @param delayMillis 防抖时间间隔（毫秒）
     * @param runnable 要执行的操作
     */
    public static void debounce(final String key, long delayMillis, final Runnable runnable) {
        // 移除之前未执行的相同key的Runnable
        Runnable previous = runnableMap.remove(key);
        if (previous != null) {
            handler.removeCallbacks(previous);
        }

        // 创建新的Runnable
        Runnable r = new Runnable() {
            @Override
            public void run() {
                runnable.run();
                runnableMap.remove(key);
            }
        };

        // 保存新的Runnable并延迟执行
        runnableMap.put(key, r);
        handler.postDelayed(r, delayMillis);
    }

    /**
     * 取消指定key的防抖操作
     * @param key 要取消的防抖操作key
     */
    public static void cancelDebounce(String key) {
        Runnable previous = runnableMap.remove(key);
        if (previous != null) {
            handler.removeCallbacks(previous);
        }
    }
}