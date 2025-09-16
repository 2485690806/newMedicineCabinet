package leesche.smartrecycling.base.service;


import android.content.Context;
import android.hardware.usb.UsbDevice;

import com.alibaba.android.arouter.facade.template.IProvider;

import java.util.List;

public interface FaceExportService extends IProvider {

    void activeAcFace(Context context);

    void newInstanceUsbMonitor(Context context);

    void registerMonitor(Context context);
    void  unregisterMonitor(Context context);
    List<UsbDevice> getUsbDevices(Context context);
}
