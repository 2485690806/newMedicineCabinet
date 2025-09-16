package leesche.smartrecycling.base.strategy;

import android.annotation.SuppressLint;

import ZtlApi.ZtlManager;

public class DevManagerUtil {
    @SuppressLint("StaticFieldLeak")
    public static ZtlManager ztlManager;

    public static ZtlManager getZtlManager() {
        if (ztlManager == null) {
            ztlManager = ZtlManager.GetInstance();
            ztlManager.setContext(DevContext.getInstance().getContext());
        }
        return ztlManager;
    }

}
