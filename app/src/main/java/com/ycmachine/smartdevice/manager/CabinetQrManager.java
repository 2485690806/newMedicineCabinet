package com.ycmachine.smartdevice.manager;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.handler.MedHttpHandler;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import leesche.smartrecycling.base.entity.GridRegion;
import leesche.smartrecycling.base.entity.QrCodeBinding;
import leesche.smartrecycling.base.qrcode.ImageCropper;
import leesche.smartrecycling.base.qrcode.QrCodeScanner;
import leesche.smartrecycling.base.utils.DataSourceOperator;



public class CabinetQrManager {
    private static final String TAG = "CabinetQrManager";
    private Context context;
    //    private final AppDatabase db;
    private ExecutorService executor;


    private static final class YpgLogicHandlerHolder {

        static final CabinetQrManager ToDiLogicHandler = new CabinetQrManager();
    }

    public static CabinetQrManager getInstance() {
        return YpgLogicHandlerHolder.ToDiLogicHandler;
    }


    public void init(Context context) {
        this.context = context;
//        this.db = AppDatabase.getInstance(context);
        this.executor = Executors.newSingleThreadExecutor();
    }

    // 处理原始照片：裁剪+识别+绑定
    public void processPhoto(String cameraNum, int level, String originalPath,
                             List<GridRegion> gridRegions,
                             OnProcessListener listener) {
        executor.execute(() -> {
            try {
                // 1. 裁剪所有格子
                List<String> croppedPaths = ImageCropper.cropAllGrids(context, originalPath, gridRegions, level);

                // 2. 逐个识别并绑定
                for (int i = 0; i < croppedPaths.size() && i < gridRegions.size(); i++) {
                    String croppedPath = croppedPaths.get(i);
                    GridRegion region = gridRegions.get(i);

                    List<String> qrCodes = QrCodeScanner.scan(croppedPath);
                    Logger.w(level+ "level:"+region.gridNumber+"qrCodes:"+JSON.toJSONString(qrCodes));
                    if (qrCodes != null && !qrCodes.isEmpty()) {
                        // 假设第1个是“格子二维码”，第2个是“物品二维码”
                        String gridQr = null;
                        String itemQr = null;
                        for (String qr : qrCodes) {
                            Logger.d("Detected QR: " + qr);
                            if (Integer.valueOf(qr).equals(Integer.valueOf(region.gridNumber))) {
                                gridQr = qr;
                            } else {
                                itemQr = qr; // 药袋的二维码
                            }
                        }
                        if(itemQr == null){
                            continue;
                        }

                        if (gridQr == null) { // 如果没有识别出格子二维码，就用默认的
                            gridQr = region.gridNumber;
                        }

                        QrCodeBinding binding = new QrCodeBinding(
                                gridQr, itemQr, level, region.gridNumber,
                                originalPath, croppedPath
                        );

                        QrCodeBinding byGridQr = DataSourceOperator.getInstance().findByGridQr(gridQr);
                        if (byGridQr != null && !StringUtils.isBlank(itemQr)) {
                            if (byGridQr.getItemQrCode().equals(itemQr)) {
                                Logger.d("Already bound (same): " + JSON.toJSONString(byGridQr));
                                continue; // 已绑定且相同，跳过
                            }
//                            if (!StringUtils.isBlank(itemQr)) {
//                                Logger.d("Item QR code is empty, skipping binding.");
//                                // 可能被取走了
//                                DataSourceOperator.getInstance().deleteByItemQrCode(itemQr);
//                                // 发送取走
//                                if (!StringUtils.isBlank(byGridQr.getBagId()))
//                                    MedHttpHandler.getInstance().BagRemove(byGridQr.getBagId());
//
//                                continue; // 物品二维码为空，跳过
//                            }

                            // 已绑定但不同，删除旧的，继续绑定新的，再发更新给后台
                            Logger.d("Already bound (different), updating: " + JSON.toJSONString(byGridQr));
                            DataSourceOperator.getInstance().deleteByItemQrCode(itemQr);

                            DataSourceOperator.getInstance().insertQrCodeBindingToDb(binding);

                        }else {
                            // 录入后台
                            MedHttpHandler.getInstance().recordBag(itemQr, level + "", region.gridNumber + "");

                            Logger.d("Inserting binding to DB: " + JSON.toJSONString(binding));

                            // 没绑定，直接绑定
                            DataSourceOperator.getInstance().insertQrCodeBindingToDb(binding);

                            Logger.d("Bound: level=" + level + ", grid=" + region.gridNumber + ", itemQr=" + itemQr);

                        }
                    } else {
                        Logger.w("QR count invalid: " + (qrCodes != null ? qrCodes.size() : "null"));
                    }
                }

                // 回调成功
                if (listener != null) listener.onSuccess();
            } catch (Exception e) {
                Logger.e("Process failed"+ JSON.toJSONString(e));
                if (listener != null) listener.onError(e);
            }
        });
    }

    // 根据“物品二维码”查询位置
    public QrCodeBinding findByItemQr(String itemQr) {
//        return null;
        return DataSourceOperator.getInstance().findByItemQrCode(itemQr);
    }

    // 回调接口
    public interface OnProcessListener {
        void onSuccess();

        void onError(Exception e);
    }

    // 释放资源
    public void release() {
        executor.shutdown();
    }
}