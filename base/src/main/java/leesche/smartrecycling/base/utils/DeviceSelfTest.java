package leesche.smartrecycling.base.utils;

import static android.content.Context.USB_SERVICE;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.HashMap;

public class DeviceSelfTest {
    private static final String TAG = "DeviceSelfTest";

    public static int cameraStatus(Context context) {
        Log.i(TAG, "---------cameraStatus---------");
            return Camera.getNumberOfCameras();

    }

    private static int findCanonDevice(Context context) {
        UsbManager usbManager = (UsbManager) context.getSystemService(USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            if (device.getProductName() == null) continue;
            if (device.getProductName().contains("Canon")) return 1;
        }
        return 0;
    }

    public static int printerStatus() {
        Log.i(TAG, "---------printerStatus---------");
        return 1;
    }

    public static int beautyStatus() {
        Log.i(TAG, "---------beautyStatus---------");
        return 1;
    }



}
