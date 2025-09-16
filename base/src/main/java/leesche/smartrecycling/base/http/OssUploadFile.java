package leesche.smartrecycling.base.http;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.alibaba.sdk.android.oss.model.OSSObjectSummary;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.leesche.logger.Logger;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.entity.OssEntity;
import leesche.smartrecycling.base.utils.DateUtil;
import leesche.smartrecycling.base.utils.DeviceUtil;
import leesche.smartrecycling.base.utils.FileUtil;
import leesche.smartrecycling.base.utils.LogUtil;
import leesche.smartrecycling.base.utils.ThreadManager;
import leesche.smartrecycling.base.utils.ZipUtil;

public class OssUploadFile {

    private static final int MAX_SYCN_FILE_COUNT = 500;

    public OSS getOss() {
        return oss;
    }

    OSS oss;

    public static String endPoint = "oss-cn-shanghai.aliyuncs.com";
    public static String stsServer = "http://open.youyiyun.tech/api/sdk/sts_sign?accessKeyId=LTAIgppy8JA67cCT";
    public static String bucketName = "youyicloud-app";
    OssEntity ossEntity;
    private String ossHostFromLocalSave = "";

    static OssUploadFile ossUploadFile = null;
    OssUploadCallback ossUploadCallback;
    DownloadFileCallback downloadFileCallback;
    List<String> imgWaitToActiveList = new ArrayList<>();
    List<OSSAsyncTask<PutObjectResult>> ossAsyncTaskList;

    private int totalCounts = 0;
    private int finalCounts = 0;
    private List<String> faceList = new ArrayList<>();

    private void addOssAsyncTaskList(OSSAsyncTask<PutObjectResult> ossAsyncTask) {
        if (ossAsyncTaskList == null) {
            ossAsyncTaskList = new ArrayList<>();
        }
        ossAsyncTaskList.add(ossAsyncTask);
    }

    public void cancelExistTask() {
        if (ossAsyncTaskList != null && ossAsyncTaskList.size() > 0) {
            for (OSSAsyncTask<PutObjectResult> ossAsyncTask : ossAsyncTaskList) {
                ossAsyncTask.cancel();
            }
            ossAsyncTaskList.clear();
        }
    }

    public static OssUploadFile getInstance() {
        if (ossUploadFile == null) {
            ossUploadFile = new OssUploadFile();
        }
        return ossUploadFile;
    }

    public String getOssHostFromLocalSave() {
        return ossHostFromLocalSave;
    }

    public void setOssHostFromLocalSave(String _ossHostFromLocalSave) {
        if (ossHostFromLocalSave.equals(_ossHostFromLocalSave)) return;
//        Logger.i("【OSS HOST】 local: " + _ossHostFromLocalSave);
        this.ossHostFromLocalSave = _ossHostFromLocalSave;
    }

    public OssEntity getOssEntity() {
        return ossEntity;
    }

    public String getRemoteUrl(String objectName) {
//        http://youyicloud-app.oss-cn-shanghai.aliyuncs.com/ljc/789464/2021_06_17/ljc1623912821336.jpg
        if (ossEntity != null) {
            return ossEntity.getHost() + File.separator + objectName;
        }
        String ossHost = "http://youyicloud-app.oss-cn-shanghai.aliyuncs.com";
        if (!TextUtils.isEmpty(ossHostFromLocalSave)) {
            ossHost = ossHostFromLocalSave;
        }
        return ossHost + File.separator + objectName;
    }

    public String getObjectName(String httpUrl) {
        if (ossEntity == null) {
            return "";
        }
        return httpUrl.replace(ossEntity.getHost() + File.separator, "");
    }

    public void setOssEntity(OssEntity ossEntity) {
        this.ossEntity = ossEntity;
    }

    public void setOssUploadCallback(OssUploadCallback ossUploadCallback) {
        this.ossUploadCallback = ossUploadCallback;
    }

    public interface DownloadFileCallback {
        void onResult(List<String> paths);
    }

    public interface OssUploadCallback {
        void onSuccess(List<OSSObjectSummary> objectSummaries);

        void onFail();
    }

    public void initOSS(Context context) {
        ThreadManager.getThreadPollProxy().execute(new Runnable() {
            @Override
            public void run() {
                OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);
                ClientConfiguration conf = new ClientConfiguration();
                conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
                conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
                conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
                conf.setMaxErrorRetry(3); // 失败后最大重试次数，默认2次
                oss = new OSSClient(context, endPoint, credentialProvider, conf);
                OSSLog.enableLog();
            }
        });
    }

    public void initOSS(Context context, OssEntity ossEntity) {
        setOssEntity(ossEntity);
//        endPoint = ossEntity.getEndpoint();
        bucketName = ossEntity.getBucket();
        ThreadManager.getThreadPollProxy().execute(() -> {
            OSSStsTokenCredentialProvider ossStsTokenCredentialProvider = new OSSStsTokenCredentialProvider(
                    ossEntity.getSts_config().getAccessKeyId(),
                    ossEntity.getSts_config().getAccessKeySecret(),
                    ossEntity.getSts_config().getSecurityToken());
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
            conf.setSocketTimeout(25 * 1000); // socket超时，默认15秒
            conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
            conf.setMaxErrorRetry(3); // 失败后最大重试次数，默认2次
            conf.setHttpDnsEnable(false);
            oss = new OSSClient(context, ossEntity.getEndpoint(), ossStsTokenCredentialProvider, conf);
            if (Constants.IS_TEST) OSSLog.enableLog();
        });
    }

    // 构造上传请求。
    public void uploadFile(String objectName, String uploadFilePath,
                           OSSProgressCallback<PutObjectRequest> ossProgressCallback,
                           OSSCompletedCallback<PutObjectRequest, PutObjectResult> ossCompletedCallback) {
        if (oss != null) {
            PutObjectRequest put = new PutObjectRequest(bucketName, objectName, uploadFilePath);
//            if(ossEntity!=null&&!TextUtils.isEmpty(ossEntity.getUri())){
//                put.setUploadUri(Uri.parse(ossEntity.getUri()));
//            }
            // 异步上传时可以设置进度回调。
            put.setProgressCallback(ossProgressCallback);
            OSSAsyncTask<PutObjectResult> task = oss.asyncPutObject(put, ossCompletedCallback);
            addOssAsyncTaskList(task);
//            task.cancel(); // 可以取消任务。
//            task.waitUntilFinished(); // 等待任务完成。
        } else {

//            Logger.i("【OSS】 is null");
        }
    }


    public void uploadFile2(String objectName, String uploadFilePath,
                            OSSProgressCallback<PutObjectRequest> ossProgressCallback,
                            OSSCompletedCallback<PutObjectRequest, PutObjectResult> ossCompletedCallback) {
        if (oss != null) {
            PutObjectRequest put = new PutObjectRequest(bucketName, objectName, uploadFilePath);
            // 异步上传时可以设置进度回调。
            put.setProgressCallback(ossProgressCallback);
            OSSAsyncTask task = oss.asyncPutObject(put, ossCompletedCallback);
            // task.cancel(); // 可以取消任务。
//            task.waitUntilFinished(); // 等待任务完成。
        }
    }

    public void uploadAiImages(String zipFileFilePath) {
        String orderId = new File(zipFileFilePath).getName().replace(".zip", "");
        String objectName = "EasyDL/" + Constants.AGENT_ID + File.separator + DateUtil.getToday2() + File.separator + orderId;
        OssUploadFile.getInstance().uploadFile(objectName, zipFileFilePath, null,
                new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                    @Override
                    public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                        String remoteUrl = OssUploadFile.getInstance().getRemoteUrl(objectName);
//                        Logger.i("【EasyDL Image】 oss path: " + remoteUrl);
                        FileUtil.deleteSingleFile(zipFileFilePath);
                    }

                    @Override
                    public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                        if (e != null) {
//                            Logger.i("【EasyDL Image】 client error: " + e.getMessage());
                            if (e.getMessage().contains("the length of file is 0")) {
                                FileUtil.deleteSingleFile(zipFileFilePath);
                            }
                        }
                        if (e1 != null) {
//                            Logger.i("【EasyDL Image】 service error: " + e1.getMessage());
                        }
                    }
                });
    }

    public void downloadFile(String objectName, String localSavePath) {
        if (oss == null) return;
        // 构造下载文件请求。
        GetObjectRequest request = new GetObjectRequest(bucketName, objectName);
        Map<String, String> mapRequest = new HashMap<>();
//        mapRequest.put("If-Unmodified-Since", timePoint);
        request.setRequestHeaders(mapRequest);
        OSSAsyncTask task = oss.asyncGetObject(request, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                long length = result.getContentLength();
                byte[] buffer = new byte[(int) length];
                int readCount = 0;
                try {
                    while (readCount < length) {
                        readCount += result.getObjectContent().read(buffer, readCount, (int) length - readCount);
                    }
                    String[] fileDirs = request.getObjectKey().split("/");
                    String fileName = fileDirs[fileDirs.length - 1];
                    String filePath = localSavePath + File.separator + fileName;
//                    Logger.i("本地已同步文件名：" + filePath);
                    if (filePath.endsWith("jpg")) {
                        imgWaitToActiveList.add(filePath);
                    }
                    FileOutputStream fout = new FileOutputStream(filePath);
                    fout.write(buffer);
                    fout.close();
                } catch (Exception e) {
                    OSSLog.logInfo(e.toString());
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientException
                    , ServiceException serviceException) {
                if (clientException != null) {
                    // Local exception, such as a network exception
                    clientException.printStackTrace();
                }
                if (serviceException != null) {
                    // Service exception
//                    Log.e("ErrorCode", serviceException.getErrorCode());
//                    Log.e("RequestId", serviceException.getRequestId());
//                    Log.e("HostId", serviceException.getHostId());
//                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
//        task.cancel(); // 可以取消任务。
//        task.waitUntilFinished(); // 等待任务完成。
    }

    /**
     * 查询文件下载
     */
    public void checkFileToDownload(String filePoint, DownloadFileCallback downloadFileCallback) {
        if (oss == null) return;
        this.downloadFileCallback = downloadFileCallback;
        imgWaitToActiveList.clear();
        ListObjectsRequest listObjects = new ListObjectsRequest(bucketName);
        File file = new File(Constants.FACE_SYNC);
        if (file.exists()) {
            String marker = "";
            marker = FileUtil.readFileSdcardFile(Constants.FACE_SYNC);
            Log.i("OSSFile", filePoint + marker);
            listObjects.setPrefix(filePoint + marker + File.separator);
        } else {
            listObjects.setPrefix(filePoint);
        }
        listObjects.setMaxKeys(MAX_SYCN_FILE_COUNT);
        // 设置成功、失败回调，发送异步列举请求
        OSSAsyncTask task = oss.asyncListObjects(listObjects, new OSSCompletedCallback<ListObjectsRequest
                , ListObjectsResult>() {
            @Override
            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
                int fileSize = result.getObjectSummaries().size();
                String lastSyncDate = "";
                if (fileSize == 0) {
                    if (!new File(Constants.FACE_SYNC).exists()) return;
                    lastSyncDate = FileUtil.readFileSdcardFile(Constants.FACE_SYNC);
                    if (DateUtil.getDiffFormToday(lastSyncDate) > 0) {
                        lastSyncDate = DateUtil.addDate(lastSyncDate, 1);
                        FileUtil.writeFileSdcardFile(Constants.FACE_SYNC, lastSyncDate);
//                        Logger.d("已同步日期至：" + lastSyncDate);
                    }
                    return;
                }
                int noNeedDownloadCount = 0;
                for (int i = 0; i < result.getObjectSummaries().size(); i++) {
                    String objectKey = result.getObjectSummaries().get(i).getKey();
                    String[] dirS = objectKey.split("/");
                    String fileName = "";
                    if (dirS.length <= 3) {
                        continue;
                    } else {
                        lastSyncDate = dirS[2];
                        fileName = dirS[3];
                    }
                    FileUtil.createDir(Constants.ACFACE_DIR);
                    FileUtil.createDir(Constants.FACE_FEATURES);
                    FileUtil.createDir(Constants.FACE_IMGS);
                    File file = new File(Constants.FACE_FEATURES + File.separator + fileName);
                    if (file.exists()) {
                        //将本地文件的时间同需要下载的文件时间做比较看是否需要下载
                        Date date = result.getObjectSummaries().get(i).getLastModified();
                        if (file.lastModified() < date.getTime()) {
//                            Logger.i("正在下载文件路径：" + objectKey);
//                            if(objectKey.endsWith("jpg")){
//                                OssUploadFile.getInstance().downloadFile(objectKey, Constants.FACE_IMGS);
//                            }else{
                            OssUploadFile.getInstance().downloadFile(objectKey, Constants.FACE_FEATURES);
//                            }
                        } else {
                            noNeedDownloadCount++;
                            if (noNeedDownloadCount == fileSize) {
                                //如果当前日期是今天 不修改， 如果不是今天 则修改为后一天，日期不能超过今天
                                if (DateUtil.getDiffFormToday(lastSyncDate) > 0) {
                                    lastSyncDate = DateUtil.addDate(lastSyncDate, 1);
//                                    Logger.d("已同步日期至：" + lastSyncDate);
                                }
                            }
                        }
                    } else {
//                        Logger.i("正在下载文件路径：" + objectKey);
//                        if(objectKey.endsWith("jpg")){
//                            OssUploadFile.getInstance().downloadFile(objectKey, Constants.FACE_IMGS);
//                        }else{
                        OssUploadFile.getInstance().downloadFile(objectKey, Constants.FACE_FEATURES);
//                        }
                    }
                }
                //保存同步日期
                if (!TextUtils.isEmpty(lastSyncDate)) {
                    FileUtil.writeFileSdcardFile(Constants.FACE_SYNC, lastSyncDate);
                }
                if (fileSize == MAX_SYCN_FILE_COUNT) {
                    checkFileToDownload(filePoint, downloadFileCallback);
                } else {
                    SystemClock.sleep(1000);
                    if (imgWaitToActiveList.size() > 0) {
                        downloadFileCallback.onResult(imgWaitToActiveList);
                    } else {
//                        Logger.i("没有需要本地激活的人脸图片");
                    }
                }
            }

            @Override
            public void onFailure(ListObjectsRequest request, ClientException clientExcepion
                    , ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    LogUtil.e("ErrorCode", serviceException.getErrorCode());
                    LogUtil.e("RequestId", serviceException.getRequestId());
                    LogUtil.e("HostId", serviceException.getHostId());
                    LogUtil.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        task.waitUntilFinished();
    }

    public void checkFileToDownload2(FragmentActivity activity, String filePoint) {
        if (Constants.IS_TEST) Log.i("OSSObjectSummary", "OSS文件路径：" + filePoint);
        if (oss == null) return;
        ListObjectsRequest listObjects = new ListObjectsRequest(bucketName);
        listObjects.setPrefix(filePoint);
        // 设置成功、失败回调，发送异步列举请求
        OSSAsyncTask task = oss.asyncListObjects(listObjects, new OSSCompletedCallback<ListObjectsRequest
                , ListObjectsResult>() {
            @Override
            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
                FileUtil.createDir(Constants.ACFACE_DIR);
                FileUtil.createDir(Constants.FACE_FEATURES);
                handlerFaceListToDownload(activity, result.getObjectSummaries());
            }

            @Override
            public void onFailure(ListObjectsRequest request, ClientException clientExcepion
                    , ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                }
            }
        });
        task.waitUntilFinished();
    }

    private void handlerFaceListToDownload(FragmentActivity activity, List<OSSObjectSummary> objectSummaries) {
        if (Constants.IS_TEST)
            Log.i("OSSObjectSummary", "onSuccess::" + objectSummaries.size() + "个文件");
        String lastSyncDate = "";
        String[] dirS;
//        DownloadFragment.showDialog(activity);
        if (faceList.size() > 0) faceList.clear();
        String downloadUrl = "";
        for (OSSObjectSummary ossObjectSummary : objectSummaries) {
//            Log.i("FileDownloadListener", "onSuccess::" + ossObjectSummary.getKey());
            //判断文件是否存在 如果存在 比较更新日期 大于本地保存的日期就添加到下载列表
            dirS = ossObjectSummary.getKey().split("/");
            String fileName = "";
            if (dirS.length <= 3) {
                continue;
            } else {
                lastSyncDate = dirS[2];
                fileName = dirS[3];
            }
            File file = new File(Constants.FACE_FEATURES + File.separator + fileName);
            downloadUrl = OssUploadFile.getInstance().getOssEntity().getHost()
                    + File.separator + ossObjectSummary.getKey();
            if (file.exists()) {
                Date date = ossObjectSummary.getLastModified();
                if (file.lastModified() < date.getTime()) {
                    //删除文件 并下载更新
                    file.delete();
                    faceList.add(downloadUrl);
                    if (Constants.IS_TEST)
                        Log.i("OSSObjectSummary", "onSuccess::" + downloadUrl + "(文件已添加到下载列表)");
                } else {
                    //已下载 不添加到下载列表
                    if (Constants.IS_TEST)
                        Log.i("OSSObjectSummary", "onSuccess::" + downloadUrl + "(文件已下载)");
                }
            } else {
                //新文件 直接添加到下载列表
                faceList.add(downloadUrl);
                if (Constants.IS_TEST)
                    Log.i("OSSObjectSummary", "onSuccess::" + downloadUrl + "(文件已添加到下载列表2)");
            }
        }
        if (faceList.size() > 0) {
            if (!TextUtils.isEmpty(lastSyncDate)) {
                FileUtil.writeFileSdcardFile(Constants.FACE_SYNC, lastSyncDate);
            }
            downloadFaceFile(activity, faceList, Constants.FACE_FEATURES);
        } else {
            if (TextUtils.isEmpty(downloadUrl)) {
//                DownloadFragment.setText("数据已同步完成");
                SystemClock.sleep(1500);
//                DownloadFragment.dismissDialog();
            } else {
                faceList.add(downloadUrl);
                downloadFaceFile(activity, faceList, Constants.FACE_FEATURES);
            }
        }
    }

    public void downloadFaceFile(Activity context, List<String> faceList, String root_path) {
        finalCounts = 0;
        final FileDownloadListener parallelTarget = createListener(context);
        final List<BaseDownloadTask> taskList = new ArrayList<>();
        int i = 0;
        for (String faceUrl : faceList) {
            taskList.add(FileDownloader.getImpl().create(faceUrl).setTag(++i));
        }
        totalCounts = faceList.size();
//        DownloadFragment.setText(String.format("下载进度 %1$d / %2$d", finalCounts, totalCounts));
        new FileDownloadQueueSet(parallelTarget)
                .disableCallbackProgressTimes()
                .setAutoRetryTimes(3)
                .setDirectory(root_path)
                .downloadTogether(taskList)
                .start();
    }

    private FileDownloadListener createListener(Activity context) {
        return new FileDownloadListener() {
            @Override
            protected boolean isInvalid() {
                return context.isFinishing();
            }

            @Override
            protected void pending(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
                Log.i("FileDownloadListener", "pending");
            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                Log.i("FileDownloadListener", "connected");
            }

            @Override
            protected void progress(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
                Log.i("FileDownloadListener", "connected");
            }

            @Override
            protected void blockComplete(final BaseDownloadTask task) {
                Log.i("FileDownloadListener", "blockComplete");
            }

            @Override
            protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
                super.retry(task, ex, retryingTimes, soFarBytes);
                Log.i("FileDownloadListener", "retry");
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                String filePath = task.getPath() + File.separator + task.getFilename();
//                Logger.i("【Download File】 success: " + filePath);
                finalCounts++;
                if (task.getFilename().contains("zip")) {
                    ThreadManager.getThreadPollProxy().execute(() -> {
                        try {
                            String path = Constants.BASE_CACHE_DIR + "/AI";
                            if (task.getFilename().contains("yyy_config")) {
                                path = Constants.BASE_CACHE_DIR + "/yyy_config/audio";
                            }
                            ZipUtil.unZipFolder(filePath, path, context);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                if (task.getFilename().equals("scale_config.json")) {
                    DeviceUtil.restartApp(context, "称重配置更新");
                    return;
                }
                updateDownProgress();
            }

            @Override
            protected void paused(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
                Log.i("FileDownloadListener", "paused");
                finalCounts++;
                updateDownProgress();
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                Log.i("FileDownloadListener", "error");
                finalCounts++;
                updateDownProgress();
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                Log.i("FileDownloadListener", "warn");
                finalCounts++;
                updateDownProgress();
            }
        };
    }

    private void updateDownProgress() {
//        if (DownloadFragment.getDownloadDialogFragment() == null) return;
//        if (finalCounts == totalCounts) {
//            DownloadFragment.setText("数据已同步完成");
//            SystemClock.sleep(1500);
//            DownloadFragment.dismissDialog();
//        } else {
//            DownloadFragment.setText(String.format("下载进度 %1$d / %2$d", finalCounts, totalCounts));
//        }
    }
}
