//package com.ycmachine.smartdevice.utils;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//
//
//public class SelfStartReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        //Android设备开机时会发送一条开机广播："android.intent.action.BOOT_COMPLETED"
//        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
//            Intent splashIntent = new Intent(context, ActivityBuy.class);
//            splashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(splashIntent);
//        }
//    }
//
//
//}