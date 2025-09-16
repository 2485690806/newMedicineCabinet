package leesche.smartrecycling.base.utils;

/**
 * desc:等待机制类，来自AndroidPn框架
 * create at: 2017/7/18 14:59
 * create by: yyw
 */
public class WaitingTimer {
    private static final long TIME_01 = 5 * 1000;
    private static final long TIME_02 = 10 * 1000;
    private static final long TIME_03 = 15 * 1000;
    private static final long TIME_04 = 30 * 1000;
    private static final long TIME_05 = 20 * 1000;

    private static final int COUNT_01 = 5;
    private static final int COUNT_02 = 10;
    private static final int COUNT_03 = 30;
    private static final int COUNT_04 = 40;

    private int count;

    public WaitingTimer() {
        this(1);
    }

    public WaitingTimer(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addCount() {
        addCount(1);
    }

    public void addCount(int increment) {
        count += increment;
    }

    /**
     * 获取对应次数的等待时长
     */
    public long waitTime() {
        if (count > COUNT_04) {
            return TIME_05;
        }
        if (count > COUNT_03) {
            return TIME_04;
        }
        if (count > COUNT_02) {
            return TIME_03;
        }
        return count <= COUNT_01 ? TIME_01 : TIME_02;
    }
}
