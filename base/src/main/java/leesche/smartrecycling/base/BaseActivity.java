package leesche.smartrecycling.base;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.leesche.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;


import butterknife.ButterKnife;
import butterknife.Unbinder;
import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.eventbus.BasicMessageEvent;
import leesche.smartrecycling.base.eventbus.SerialMsgEvent;
import leesche.smartrecycling.base.utils.AssetHelper;
import leesche.smartrecycling.base.utils.DefaultModel;
import leesche.smartrecycling.base.utils.FileUtil;
import leesche.smartrecycling.base.utils.NetBroadcastReceiver;
import leesche.smartrecycling.base.utils.NetUtil;

public abstract class BaseActivity extends AppCompatActivity implements NetBroadcastReceiver.NetChangeListener{

    public static NetBroadcastReceiver.NetChangeListener listener;
    private NetBroadcastReceiver netBroadcastReceiver;

    private final int MIN_DELAY_TIME = 500;  // 两次点击间隔不能少于1000ms
    private long lastClickTime;
    Unbinder unbinder;

    private Handler mWorkerHandler;
    private long mWorkerThreadID = -1;
    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mWorkerHandler == null) {
            mWorkerHandler = HandlerThreadHandler.createHandler(TAG);
            mWorkerThreadID = mWorkerHandler.getLooper().getThread().getId();
        }
        EventBus.getDefault().register(this);
        hideBottomUIMenu();
        setContentView(initLayout());
        unbinder = ButterKnife.bind(this);
        initView(savedInstanceState);
        initData();

        listener = this;
        //Android 7.0以上需要动态注册
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //实例化IntentFilter对象
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            netBroadcastReceiver = new NetBroadcastReceiver();
            //注册广播接收
            registerReceiver(netBroadcastReceiver, filter);
        }
        checkNet();

    }

    private void blackeningView() {
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        getWindow().getDecorView().setLayerType(View.LAYER_TYPE_HARDWARE, paint);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BasicMessageEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SerialMsgEvent event) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        Log.i("BaseActiviy", intent.getAction());
        finish();
    }

    private void hideBottomUIMenu() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * 设置布局
     *
     * @return
     */
    public abstract int initLayout();

    /**
     * 初始化布局
     */
    public abstract void initView(Bundle savedInstanceState);

    /**
     * 设置数据
     */
    public abstract void initData();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
        if (mWorkerHandler != null) {
            try {
                mWorkerHandler.getLooper().quit();
            } catch (final Exception e) {
                //
            }
            mWorkerHandler = null;
        }
    }

    public boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }


    public final synchronized void queueEvent(final Runnable task, final long delayMillis) {
        if ((task == null) || (mWorkerHandler == null)) return;
        try {
            mWorkerHandler.removeCallbacks(task);
            if (delayMillis > 0) {
                mWorkerHandler.postDelayed(task, delayMillis);
            } else if (mWorkerThreadID == Thread.currentThread().getId()) {
                task.run();
            } else {
                mWorkerHandler.post(task);
            }
        } catch (final Exception e) {
            // ignore
        }
    }


    /**
     * 网络类型
     */
    public static int netType;

    /**
     * 网络变化之后的类型
     */
    @Override
    public void onChangeListener(int status) {
        // TODO Auto-generated method stub
        netType = status;
//        Log.i("netType", "netType:" + status);
        if (!isNetConnect()) {

//            Log.e("netType", "网络异常，请检查网络");
//            showNetDialog();
//            T.showShort("网络异常，请检查网络，哈哈");
        } else {
//            Log.e("netType", "网络恢复正常");
//            hideNetDialog();
//            T.showShort("网络恢复正常");
        }
    }

//    /**
//     * 隐藏设置网络框
//     */
//    private void hideNetDialog() {
//        if (alertDialog != null) {
//            alertDialog.dismiss();
//        }
//        alertDialog = null;
//    }

    /**
     * 初始化时判断有没有网络
     */
    public boolean checkNet() {
        this.netType = NetUtil.getNetWorkState(BaseActivity.this);
        if (!isNetConnect()) {
        }
        return isNetConnect();
    }

    /**
     * 判断有无网络 。
     *
     * @return true 有网, false 没有网络.
     */
    public static boolean isNetConnect() {
        if (netType == 1) {
            return true;
        } else if (netType == 0) {
            return true;
        } else if (netType == -1) {
            return false;
        }
        return false;
    }

//    /**
//     * 弹出设置网络框
//     */
//    private void showNetDialog() {
//        if (alertDialog == null) {
//            alertDialog = new MyAlertDialog(this).builder().setTitle("网络异常")
//                    .setNegativeButton("取消", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//
//                        }
//                    }).setPositiveButton("设置", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
//                            startActivity(intent);
//                        }
//                    }).setCancelable(false);
//        }
//        alertDialog.show();
//        showMsg("网络异常，请检查网络");
//    }
//    public void showMsg(String msg) {
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//    }

    //    public YoloV5Detect yolov5Detect;
//
//    protected void  initYoloV5(){
//        yolov5Detect = new YoloV5Detect();
//        String productMode = Build.MODEL.trim();
//
//        try {
//            if (productMode.contains("3588")) {
//
//                com.leesche.logger.Logger.e("3588设备，使用int8模型" + AssetHelper.assetFilePath(this, DefaultModel.YOLOV5S_INT8.name));
//                yolov5Detect.init(AssetHelper.assetFilePath(this, DefaultModel.YOLOV5S_INT8.name),
//                        AssetHelper.assetFilePath(this, "coco_80_labels_list.txt"), false);
//
//
//            } else if (productMode.contains("3576")) {
//                com.leesche.logger.Logger.e("3576设备，使用fp模型" + AssetHelper.assetFilePath(this, DefaultModel.YOLOV5S_FP.name));
//                yolov5Detect.init(AssetHelper.assetFilePath(this, DefaultModel.YOLOV5S_FP.name),
//                        AssetHelper.assetFilePath(this, "coco_80_labels_list.txt"), false);
//
//            } else {
//                com.leesche.logger.Logger.e("不存在合适的");
//            }
//        } catch (IOException e) {
//            Logger.e("Error reading assets", e);
//            finish();
//        }
//
//
//        Resources resources = this.getResources();
////            int drawableId = R.drawable.testai;
////            int drawableId = R.drawable.aitest;
//        int drawableId = R.drawable.aitest2;
//        Bitmap bitmap = BitmapFactory.decodeResource(resources, drawableId);
//        boolean detectResult = yolov5Detect.detect(bitmap);
//        if (detectResult) {
//            // 如果检测成功，将处理后的图像显示在ImageView上
//            String compressImgDir = Constants.POC_CAMERA_DIR + "/compress";
//            String fileName = "Test.jpg";
//            String imgFilePath = compressImgDir + File.separator + fileName;
//            FileUtil.saveBitmapKR(imgFilePath, bitmap);
//            Logger.i("[系统]拍照路径---: " + imgFilePath);
//
//        }
//
//    }
    protected void showShortMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}
