package leesche.smartrecycling.base.utils;

import android.os.Handler;
import android.os.Looper;

//import com.leesche.yyyiotlib.serial.manager.helper.RvmHelper;

public class WeightMonitor {
    private static final int STABLE_THRESHOLD = 3; // Weight stability threshold (g)
    private static final int CHECK_INTERVAL = 100; // Check interval (milliseconds)
    private static final int STABLE_COUNT = 3; // Continuous stable times
    
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable weightCheckRunnable = new Runnable() {
        @Override
        public void run() {
            checkWeight();
        }
    };
    
    private int stableCount = 0;
    private Integer lastWeight = null;
    private boolean isMonitoring = false;
    private WeightStableListener listener;
    
    public interface WeightStableListener {
        void onWeightStable(int stableWeight);
        void onWeightUpdate(int currentWeight);
    }
    
    public void startMonitoring(WeightStableListener listener) {
        this.listener = listener;
        this.isMonitoring = true;
        this.stableCount = 0;
        this.lastWeight = null;
        
        // 立即开始第一次检查
        handler.post(weightCheckRunnable);
    }
    
    public void stopMonitoring() {
        isMonitoring = false;
        handler.removeCallbacks(weightCheckRunnable);
    }
    
    private void checkWeight() {
        if (!isMonitoring) return;

        //Call the method to get the weight
//        RvmHelper.getInstance().getWeightByWId(1);

        // Set up the next check
        handler.postDelayed(weightCheckRunnable, CHECK_INTERVAL);
    }

    // Call this method in the callback to process the weight result
    public void handleWeightResult(int currentWeight) {
        if (!isMonitoring) return;
        
        if (listener != null) {
            listener.onWeightUpdate(currentWeight);
        }
        
        if (lastWeight != null) {
            int difference = Math.abs(currentWeight - lastWeight);
            
            if (difference <= STABLE_THRESHOLD) {
                stableCount++;
                
                if (stableCount >= STABLE_COUNT) {
                    // 重量已经稳定
                    stopMonitoring();
                    if (listener != null) {
                        listener.onWeightStable(currentWeight);
                    }
                    return;
                }
            } else {
                stableCount = 0; // 重置稳定计数
            }
        }
        
        lastWeight = currentWeight;
    }
}