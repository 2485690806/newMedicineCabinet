package leesche.smartrecycling.base.service;

import android.content.Context;
import android.view.SurfaceView;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface HkExportService  extends IProvider {

    //开启服务
    void startHkService(Context context);
    //停止服务
    void stopHkService(Context context);
    //添加摄像头检测状态回调
    void addHkCallback(HkLoginCallback hkLoginCallback);
    //开启或者停止预览
    void startOrStopPreview(int index, SurfaceView surfaceView,int streamType, boolean isStart);
    //是否允许开启预览
    boolean canStartPreview(int index);
    //拍照上传
    void snapImgAndUploadOss(int index, String orderId, String filePath);

    void hkLogout (int index);
}
