package leesche.smartrecycling.base.utils;

import android.net.TrafficStats;

public class NetSpeed {

    private static final String TAG = NetSpeed.class.getSimpleName();
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    static NetSpeed netSpeed;

    public static NetSpeed newInstance() {
        if (netSpeed == null) {
            netSpeed = new NetSpeed();
        }
        return netSpeed;
    }

    public long getNetSpeed(int uid) {
        try {
            long nowTotalRxBytes = getTotalRxBytes(uid);
            long nowTimeStamp = System.currentTimeMillis();
            long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
            lastTimeStamp = nowTimeStamp;
            lastTotalRxBytes = nowTotalRxBytes;
            return speed;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    //getApplicationInfo().uid
    public long getTotalRxBytes(int uid) {
        return TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }
}
