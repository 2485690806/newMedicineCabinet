package leesche.smartrecycling.base.service;


import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;

import com.alibaba.android.arouter.facade.template.IProvider;

import java.util.List;

public interface EasyDlExportService extends IProvider {

    void newInstanceUsbMonitor(Context context);
    void registerMonitor(Context context);
    void  unregisterMonitor(Context context);
    List<UsbDevice> getUsbDevices(Context context);
    void initDetectManager(Context context, EasyDLCallback easyDLCallback);
    void startToDetectObject(Bitmap bitmap, int index);
    void unInitDetectManager();
    void requestUvcCameraPermission(Context context);
}
