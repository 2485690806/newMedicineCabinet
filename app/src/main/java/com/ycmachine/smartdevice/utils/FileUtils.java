//package com.ycmachine.smartdevice.utils;
//
//import android.content.Context;
//import android.telephony.TelephonyManager;
//
//import com.zcapi;
//
//import java.lang.reflect.Method;
//
//public class FileUtils {
//    public static zcapi zcApi = new zcapi();
//
//
//    public static String readDeviceId(String filePath) {
////        try {
////            File file = new File(filePath);
////            if(file.isFile() && file.exists()) {
////                InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
////                BufferedReader br = new BufferedReader(isr);
////                String readLine = br.readLine();
////                br.close();
////                return readLine;
////            } else {
////                return zcApi.getBuildSerial();
////            }
////        } catch (Exception e) {
////            System.out.println("文件读取错误!");
////        }
//        return zcApi.getBuildSerial();
//    }
//
//
//    /**
//     * getImei获取 deviceId
//     *
//     * @param context
//     * @param slotId  slotId为卡槽Id，它的值为 0、1；
//     * @return
//     */
//    public static String getDeviceIdByGetImei(Context context, int slotId) {
//
//        try {
//
//            TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//
//                return tm.getImei();
//            }
//            Method method = tm.getClass().getMethod("getImei", int.class);
//
//            return method.invoke(tm, slotId).toString();
//
//        } catch (Throwable e) {
//
//        }
//
//        return "";
//
//    }
//
//}
