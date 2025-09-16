package leesche.smartrecycling.base;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.google.gson.JsonObject;
import com.leesche.logger.Logger;

import java.io.File;

import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.entity.PocEntity;
import leesche.smartrecycling.base.eventbus.BasicMessageEvent;
import leesche.smartrecycling.base.http.AwsOssFileHandler;
import leesche.smartrecycling.base.http.OssUploadFile;
import leesche.smartrecycling.base.utils.DataSourceOperator;
import leesche.smartrecycling.base.utils.DateUtil;
import leesche.smartrecycling.base.utils.FileUtil;

public class RemoteDataManager {

    private static RemoteDataManager dataManager;
    private boolean isAWS = false;

    public boolean isAWS() {
        return isAWS;
    }

    public void setAWS(boolean AWS) {
        isAWS = AWS;
    }

    public static RemoteDataManager getInstance() {
        if (dataManager == null) {
            dataManager = new RemoteDataManager();
        }
        return dataManager;
    }

    public void uploadCrashLogger2(String fileName) {
        String filePath = Constants.LOGGER + File.separator + "crash" + File.separatorChar + fileName + ".txt";
        File crash_file = new File(filePath);
        if (FileUtil.judeFileExists(crash_file)) {
            String objectName = "logger/" + Constants.YYY_DEVICE_ID + "/crash/" + crash_file.getName();
//            Logger.e("文件上传地址：" + AwsOssFileHandler.getInstance().getRemoteUrl(objectName));
            if (!isAWS) {
                OssUploadFile.getInstance().uploadFile(objectName, filePath, null, null);
            } else {
                AwsOssFileHandler.getInstance().uploadFile(objectName, filePath, null);
            }
        } else {
//            Logger.e("找不到" + fileName + ".txt文件");
        }
    }

    public void uploadCrashLogger(String filePath) {
        File crash_file = new File(filePath);
        if (FileUtil.judeFileExists(crash_file)) {
            String objectName = "logger/" + Constants.YYY_DEVICE_ID + "/anr/" + crash_file.getName();
//            Logger.e("文件上传地址：" + AwsOssFileHandler.getInstance().getRemoteUrl(objectName));
            if (!isAWS) {
                OssUploadFile.getInstance().uploadFile(objectName, filePath, null, null);
            } else {
                AwsOssFileHandler.getInstance().uploadFile(objectName, filePath, null);
            }
        } else {
//            Logger.e("找不到" + filePath);
        }
    }

    public void uploadSystemLogger(String fileName, boolean isDelete) {
//        /storage/emulated/0/TestRecyclingSDK/logger/request
        String localPath = Constants.LOGGER + File.separator + "request" + File.separatorChar + fileName + ".txt";
        File request_file = new File(localPath);
        if (FileUtil.judeFileExists(request_file)) {
            Logger.e("[系统]日志 准备上传" + fileName + ".txt文件");
            String objectName = "logger/" + Constants.YYY_DEVICE_ID + "/request/" + request_file.getName();
            if (!isAWS) {
                OssUploadFile.getInstance().uploadFile(objectName, localPath, null, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                    @Override
                    public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                        if (isDelete) {
                            FileUtil.deleteSingleFile(localPath);
                        }
                    }

                    @Override
                    public void onFailure(PutObjectRequest putObjectRequest, ClientException clientException, ServiceException serviceException) {
                        if (clientException != null) {
                            Logger.i("[系统]日志 上传失败 客户端错误：" + clientException.getMessage());
                            return;
                        }
                        if (serviceException != null) {
                            Logger.i("[系统]日志 上传失败 服务端错误：" + serviceException.getMessage());
                        }
                    }
                });
            } else {
                AwsOssFileHandler.getInstance().uploadFile(objectName, localPath, new TransferListener() {
                    @Override
                    public void onStateChanged(int i, TransferState transferState) {
                        if (transferState.equals(TransferState.COMPLETED)) {
                            String remoteUrl = AwsOssFileHandler.getInstance().getRemoteUrl(objectName);
//                            Logger.i("【日志上传】 上传成功: " + remoteUrl);
                        }
                    }

                    @Override
                    public void onProgressChanged(int i, long l, long l1) {

                    }

                    @Override
                    public void onError(int i, Exception e) {
//                        Logger.i("【日志上传】 上传失败" + e.getMessage());
                    }
                });
            }
        } else {
//            Logger.e("找不到" + fileName + ".txt文件");
        }
    }

    public void uploadSystemFile(String path) {
        File request_file = new File(path);
        if (FileUtil.judeFileExists(request_file)) {
//            Logger.e("准备上传" + new File(path).getName() + "文件");
            String objectName = "logger/" + Constants.YYY_DEVICE_ID + "/sysFile/" + request_file.getName();
            OssUploadFile.getInstance().uploadFile(objectName, path, null, null);
        } else {
//            Logger.e("找不到 " + path + " 目标文件");
        }
    }

    public void checkDeliveryVideo(Context context, BasicMessageEvent event) {
        if (event.getContent() != null) {
            String orderId = event.getContent();
//            Logger.i("视频订单ID: " + orderId);
            PocEntity pocEntity = DataSourceOperator.getInstance().getPocDao().queryPocFromDb(orderId);
            if (pocEntity != null) {
                Intent intent = new Intent();
                intent.setAction("YYY_ACTION_POC_RECORD_VIDEO");
                intent.putExtra("start_time", pocEntity.getStart_time());
                intent.putExtra("end_time", pocEntity.getEnd_time());
                intent.putExtra("order", pocEntity.getOrder_id());
                context.sendBroadcast(intent);
            } else {
//                Logger.e("订单号：" + orderId + "没有查询到任何数据");
            }
        } else {
            checkDeliveryVideo2(context, event);
        }
    }

    public void uploadWeightVideo(Context context, BasicMessageEvent event) {
        String orderId;
        if (event != null) {
            orderId = event.getContent();
        } else {
            orderId = Constants.ORDER_ID;
        }
//        Logger.i("视频订单ID: " + orderId);
        PocEntity pocEntity = DataSourceOperator.getInstance().getPocDao().queryPocFromDb(orderId);
        if (pocEntity != null) {
//            initUploadVideo(context);
            Intent intent = new Intent();
            intent.setAction("YYY_ACTION_POC_RECORD_VIDEO2");
            intent.putExtra("start_time", pocEntity.getStart_time());
            intent.putExtra("end_time", pocEntity.getEnd_time());
            intent.putExtra("second_start_time", pocEntity.getSecond_start_time());
            intent.putExtra("second_end_time", pocEntity.getSecond_end_time());
            intent.putExtra("order", pocEntity.getOrder_id());
            context.sendBroadcast(intent);
        } else {
//            Logger.e("订单号：" + orderId + "没有查询到任何数据");
        }
    }

    public void checkDeliveryVideo2(Context context, BasicMessageEvent event) {
        JsonObject jsonObject = (JsonObject) event.getObject();
        String orderId = jsonObject.get("orderId").getAsString();
        String pocType = jsonObject.get("pocType").getAsString();
        if (!TextUtils.isEmpty(orderId)) {
//            initUploadVideo(context);
//            Logger.e("【视频查看】订单号" + orderId);
            Intent intent = new Intent();
            if (pocType.contains("image")) {
                intent.setAction("YYY_ACTION_POC_REMOTE_IMG");
            }
            if (pocType.contains("video")) {
                long startTime = jsonObject.get("startTime").getAsLong();
                long endTime = jsonObject.get("endTime").getAsLong();
                intent.setAction("YYY_ACTION_POC_REMOTE_VIDEO");
                intent.putExtra("start_time", startTime);
                intent.putExtra("end_time", endTime);
            }
            intent.putExtra("order", orderId);
            context.sendBroadcast(intent);
        } else {
//            Logger.e("【视频查看 订单号未填写");
        }
    }

    public void checkKdVideo(Context context, String[] videoInfoS) {
        if (!TextUtils.isEmpty(videoInfoS[0])) {
//            Logger.e("【视频查看】订单号" + videoInfoS[0]);
            Intent intent = new Intent();
            long startTime = DateUtil.getTimeMillis(videoInfoS.length > 3 ? videoInfoS[2] : videoInfoS[1], DateUtil.FORMAT_ORDERID);
            long endTime = DateUtil.getTimeMillis(videoInfoS.length > 3 ? videoInfoS[3] : videoInfoS[2], DateUtil.FORMAT_ORDERID);
            intent.setAction("KD_ACTION_POC_RECORD");
            intent.putExtra("start_time", startTime);
            intent.putExtra("end_time", endTime);
            intent.putExtra("order", videoInfoS[0]);
            if (videoInfoS.length > 3) {
                intent.putExtra("serverNo", videoInfoS[1]);
            }
            context.sendBroadcast(intent);
        } else {
//            Logger.e("【视频查看 订单号未填写");
        }
    }

//    private SubscriberOnNextListener2<JsonObject> reportPocInfoOnNext;
//    private SubscriberOnNextListener2<JsonObject> reportPocProgressOnNext;

//    public void initUploadVideo(Context context) {
//        if (reportPocInfoOnNext == null) {
//            reportPocInfoOnNext = new SubscriberOnNextListener2<JsonObject>() {
//                @Override
//                public void onNext(JsonObject jsonObject) {
//                    Logger.i("【视频上传】 file info (success)：" + jsonObject.toString());
//                }
//
//                @Override
//                public void onError(String e) {
//                    Logger.i("【视频上传】 文件信息(error)：" + e);
//                }
//            };
//            reportPocProgressOnNext = new SubscriberOnNextListener2<JsonObject>() {
//                @Override
//                public void onNext(JsonObject jsonObject) {
//                    Logger.i("【视频上传】 progress (success)：" + jsonObject.toString());
//                }
//
//                @Override
//                public void onError(String e) {
//                    Logger.i("【视频上传】 progress (error)：" + e);
//                }
//            };
//        }
//    }
//
//    public void requestPocWithFileInfo(String orderId, String nameList){
//        if(reportPocInfoOnNext!=null){
//            HttpMethods.getInstance().reportUploadFileInfo(new ProgressSubscriber<JsonObject>(reportPocInfoOnNext, this));
//        }
//    }
}
