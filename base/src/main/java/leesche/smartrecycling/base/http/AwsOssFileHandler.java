package leesche.smartrecycling.base.http;

import android.content.Context;
import android.text.TextUtils;

import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import leesche.smartrecycling.base.entity.OssEntity;

public class AwsOssFileHandler implements TransferListener {

    static AwsOssFileHandler awsOssFileHandler = null;
    OssEntity ossEntity;
    private String ossHostFromLocalSave = "";
    TransferUtility utility;
    private String objectName = "";

    public static AwsOssFileHandler getInstance() {
        if (awsOssFileHandler == null) {
            synchronized (AwsOssFileHandler.class) {
                awsOssFileHandler = new AwsOssFileHandler();
            }
        }
        return awsOssFileHandler;
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
        return httpUrl.replace(ossEntity.getHost() + File.separator, "");
    }

    public void initOSS(Context context, OssEntity ossEntity) {
        this.ossEntity = ossEntity;
        JSONObject jsonConfig = new JSONObject();
        JSONObject s3TransferUtility = new JSONObject();
        try {
            jsonConfig.putOpt("S3TransferUtility", s3TransferUtility);
            s3TransferUtility.put("Region", ossEntity.getRegion());
            s3TransferUtility.put("Bucket", ossEntity.getBucket());
        } catch (JSONException e) {
            return;
        }
        AWSSessionCredentials credentials = new AWSSessionCredentials() {
            @Override
            public String getSessionToken() {
                return ossEntity.getSts_config().getSecurityToken();
            }

            @Override
            public String getAWSAccessKeyId() {
                return ossEntity.getSts_config().getAccessKeyId();
            }

            @Override
            public String getAWSSecretKey() {
                return ossEntity.getSts_config().getAccessKeySecret();
            }
        };
        TransferNetworkLossHandler.getInstance(context);
        utility = TransferUtility.builder()
                .context(context)
                .s3Client(new AmazonS3Client(credentials))
                .awsConfiguration(new AWSConfiguration(jsonConfig))
                .build();
    }

    public void uploadFile(String objectName, String uploadFilePath, TransferListener listener) {
        if (utility != null) {
            TransferObserver observe = utility.upload(objectName, new File(uploadFilePath));
            observe.setTransferListener(listener);
        }
    }

    public void downloadFile(String objectName, String uploadFilePath, TransferListener listener) {
        if (utility != null) {
            TransferObserver observe = utility.download(objectName, new File(uploadFilePath));
            observe.setTransferListener(listener);
        }
    }

    public void uploadFile(String objectName, String uploadFilePath) {
        if (utility != null) {
            this.objectName = objectName;
            TransferObserver observe = utility.upload(objectName, new File(uploadFilePath));
            observe.setTransferListener(this);
        }
    }

    @Override
    public void onStateChanged(int i, TransferState transferState) {
        if (transferState == TransferState.COMPLETED) {
//            Logger.i("【AWS文件上传】 oss adr: " + getRemoteUrl(objectName));
        }
    }

    @Override
    public void onProgressChanged(int i, long l, long l1) {

    }

    @Override
    public void onError(int i, Exception e) {
//        Logger.i("【AWS文件上传】 error: " + e.getMessage());
    }
}
