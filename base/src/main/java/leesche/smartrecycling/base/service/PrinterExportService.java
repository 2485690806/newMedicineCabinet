package leesche.smartrecycling.base.service;


import android.app.Activity;

import com.alibaba.android.arouter.facade.template.IProvider;

import java.util.List;

import leesche.smartrecycling.base.entity.AIBottlePrintEntity;

public interface PrinterExportService extends IProvider {

    /**
     * 打开&连接打印机
     */
    void openPrinterPort(Activity activity);

    /**
     * 添加回调（获取打印机状态信息）
     */
    void addPrinterCallback(ICommonCallback iCommonCallback);

    boolean printAiBottleTicket(AIBottlePrintEntity aiBottlePrintEntity);

    /**
     * 关闭&断开打印机
     */
    void closePrinterPorts();

    int getPrinterStatus(long delayMills);
}
