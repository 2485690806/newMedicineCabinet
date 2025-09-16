package com.ycmachine.smartdevice.utils.serial;//package com.faceunity.app.utils.serial;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//
//import com.faceunity.app.R;
//
//
//public class MainActivity extends AppCompatActivity implements SerialInter {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        SerialManage.getInstance().init(this);//串口初始化
//        SerialManage.getInstance().open();//打开串口
//
//
//
//        SerialManage.getInstance().send("Z");//发送指令 Z
//
//
//
//
//    }
//
//    @Override
//    public void connectMsg(String path, boolean isSucc) {
//        String msg = isSucc ? "成功" : "失败";
//        Log.e("串口连接回调", "串口 "+ path + " -连接" + msg);
//    }
//
//    @Override//若在串口开启的方法中 传入false 此处不会返回数据
//    public void readData(String path, byte[] bytes, int size) {
//        Log.e("串口数据回调","串口 "+ path + " -获取数据" + bytes);
//    }
//}