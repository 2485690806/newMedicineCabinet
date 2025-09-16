package leesche.smartrecycling.base.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

import io.reactivex.rxjava3.core.Observer;
import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.utils.Md5Util;
import okhttp3.RequestBody;

public class CornerStoreHttpMethods extends HttpBuilder {

    private CornerstoneService bottleService;
    Gson gson;

    public CornerStoreHttpMethods() {
        super();
        bottleService = getRetrofit().create(CornerstoneService.class);
    }

    private static class SingletonHolder {
        private static final CornerStoreHttpMethods INSTANCE = new CornerStoreHttpMethods();
    }

    public static CornerStoreHttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void initRunEVN(int type) {
        if (type == 0) {
            HttpUrls.TR_HOST_ADDRESS = "https://baseuatapi.cstl.com.hk";
            HttpUrls.WEB_HOST_ADDRESS = "wss://baseuatapi.cstl.com.hk/kws";
            HttpUrls.campaignId = 461;
            HttpUrls.storeID = 12;
            HttpUrls.secretKey = "KJaD9xcCSNLBDy646R5Xkm6cCatFFRUy";
            HttpUrls.sftURL = "octrvm.cstl.com.hk";
            HttpUrls.port = 22;
            HttpUrls.loginName = "octdev";
            HttpUrls.accessKeyFileName = "octdev.pri";
            HttpUrls.downloadPath = "/home/octdev/download";
            HttpUrls.uploadPath = "/home/octdev/upload";
        }
        if (type == 1) {
            HttpUrls.TR_HOST_ADDRESS = "https://baseapi.cstl.com.hk";
            HttpUrls.WEB_HOST_ADDRESS = "wss://baseapi.cstl.com.hk/kws";
            HttpUrls.campaignId = 177;
            HttpUrls.storeID = 12;
            HttpUrls.secretKey = "KJaD9xcCSNLBDy646R5Xkm6cCatFFRUy";
            HttpUrls.sftURL = "";
            HttpUrls.port = 0;
            HttpUrls.loginName = "";
            HttpUrls.accessKeyFileName = "";
            HttpUrls.downloadPath = "";
            HttpUrls.uploadPath = "";
        }
    }

    public void getConfig(Observer<JsonObject> observer) {
        long timeStamp = System.currentTimeMillis();
        String rawSignStr = Md5Util.GetMD5Code(Constants.KID + "" + timeStamp + "rvmsign", false);
        String url = HttpUrls.TR_HOST_ADDRESS + "/kiosk/api/getconfig";
        toSubscribe(bottleService.getConfig(url, timeStamp, rawSignStr, Constants.KID).map(new HttpResultFunc<>()), observer);
    }

    public void checkFoodContainer(Observer<JsonObject> observer, String containerNo) {
        long timeStamp = System.currentTimeMillis();
        String rawSignStr = Md5Util.GetMD5Code(containerNo + timeStamp + "rvmsign", false);
        String url = HttpUrls.TR_HOST_ADDRESS + "/kiosk/api/checkFoodContainer";
        toSubscribe(bottleService.checkFoodContainer(url, timeStamp, rawSignStr,
                containerNo).map(new HttpResultFunc<>()), observer);
    }

    public void getLastUpdate(Observer<JsonObject> observer) {
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("kid", Constants.YYY_DEVICE_ID);
        if (gson == null) gson = new Gson();
        long timeStamp = System.currentTimeMillis();
        String rawSignStr = Md5Util.GetMD5Code(Constants.YYY_DEVICE_ID + timeStamp + "rvmsign", false);
        String accessTokenParams = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), accessTokenParams);
        String url = HttpUrls.TR_HOST_ADDRESS + "/kiosk/api/getLastUpdate";
//        toSubscribe(bottleService.getConfig(url, timeStamp, rawSignStr, body).map(new HttpResultFunc<>()), observer);
    }

    public void getBarcodeList(Observer<JsonObject> observer) {
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("kid", Constants.YYY_DEVICE_ID);
        if (gson == null) gson = new Gson();
        long timeStamp = System.currentTimeMillis();
        String rawSignStr = Md5Util.GetMD5Code(Constants.YYY_DEVICE_ID + timeStamp + "rvmsign", false);
        String accessTokenParams = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), accessTokenParams);
        String url = HttpUrls.TR_HOST_ADDRESS + "/kiosk/api/getBarcodeList";
//        toSubscribe(bottleService.getConfig(url, timeStamp, rawSignStr, body).map(new HttpResultFunc<>()), observer);
    }
}
