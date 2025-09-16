package com.ycmachine.smartdevice.creator;

import com.ycmachine.smartdevice.handler.HeatParser;

// 状态更新观察者接口，所有需要更新的UI组件都要实现此接口
public interface HeatStatusObserver {
    // 当状态更新时调用此方法，传递最新状态
    void onHeatStatusUpdated(HeatParser status);
}
    