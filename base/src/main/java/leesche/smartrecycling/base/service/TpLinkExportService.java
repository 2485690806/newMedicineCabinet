package leesche.smartrecycling.base.service;

import android.content.Context;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface TpLinkExportService extends IProvider {
    //开启服务
    void startTpService(Context context);

    //停止服务
    void stopTpService(Context context);

    //添加摄像头检测状态回调
    void setTpLinkCallback(TpLinkCallback tpLinkCallback);

    //开启或者停止预览
    void startOrStopPreview(Context context, int index, ViewGroup mViewHolder, boolean isStart);

    //拍照上传
    void snapImage(int index, String orderId, String savePath);

    //设备是否可以拍照
    boolean deviceCanSnapImg(int index);
}
