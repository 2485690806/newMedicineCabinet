package leesche.smartrecycling.base.strategy;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import leesche.smartrecycling.base.utils.DeviceUtil;

/**
 * 设备运行上下文
 * Created by samba on
 */
public class DevContext {
    private static final Class[] DEV_CLAZZ = {ZCStrategy.class, ZTLStrategy.class, MCStrategy.class, DefaultStrategy.class};
    //卓策设备策略名称
    public static final String ZC_LABEL = "ZC";
    //卓策设备标识
    public static final String ZC_MODE = "ZC-328|ZC-339";
    //定昌设备策略名称
    public static final String ZTL_LABEL = "ZTL";
    //定昌设备标识
    public static final String ZTL_MODE = "ZTL-rk3288|LXJ3288|ZTL-LXJ328802";
    //定昌设备策略名称
    public static final String MC_LABEL = "MC";
    //定昌设备标识
    public static final String MC_MODE = "rk3288";
    //默认设备策略名称
    public static final String DEFAULT_LABEL = "Default";
    //默认设备标识
    public static final String DEFAULT_MODE = Build.MODEL;

    private static DevContext mDevContext;

    private DevStrategy mDevStrategy;

    private Context mContext;

    private static Handler mHandler = new Handler(Looper.myLooper());

    /**
     * 初始化配置
     */
    public static void initDev(Context context) {
        final DevContext devContext = getInstance();
        devContext.mContext = context.getApplicationContext();
        DevStrategy devStrategy;
        for (Class clazz : DEV_CLAZZ) {
            devStrategy = getDevStrategy(devContext.mContext, clazz);
            if (devStrategy == null) {
                devStrategy = getDevStrategy(clazz);
            }
            if (devStrategy != null) {
                String[] modes = devStrategy.getMode().split("\\|");
                for (String mode : modes) {
                    if (Build.MODEL.equals(mode)) {
                        devContext.mDevStrategy = devStrategy;
//                        Logger.e("当前设备策略label=" + devStrategy.getLabel());
                        return;
                    }
                }
//                Logger.e("当前设备策略label=default");
            }
        }
    }

    /**
     * 创建实例
     */
    private static DevStrategy getDevStrategy(Context context, Class clazz) {
        DevStrategy devStrategy = null;
        try {
            Constructor cons = clazz.getDeclaredConstructor(new Class[]{Context.class});
            cons.setAccessible(true);
            devStrategy = (DevStrategy) cons.newInstance(new Object[]{context});
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (NoSuchMethodException e) {
        } catch (InvocationTargetException e) {
        }
        return devStrategy;
    }

    /**
     * 创建实例
     */
    private static DevStrategy getDevStrategy(Class clazz) {
        DevStrategy devStrategy = null;
        try {
            devStrategy = (DevStrategy) clazz.newInstance();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
        return devStrategy;
    }

    /**
     * 获取实例
     */
    public static DevContext getInstance() {
        if (mDevContext == null) {
            mDevContext = new DevContext();
        }
        return mDevContext;
    }

    /**
     * 获取运行时strategy
     */
    public DevStrategy getDevStrategy() {
        if (mDevStrategy == null) {
            throw new RuntimeException("未配置运行时设备策略");
        }
        return mDevStrategy;
    }

    /**
     * 获取运行时context
     */
    public Context getContext() {
        if (mContext == null) {
            throw new RuntimeException("未配置运行时设备策略");
        }
        return mContext;
    }

    /**
     * 获取默认Handler
     */
    public Handler getHandler() {
        return mHandler;
    }


}
