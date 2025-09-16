package com.ycmachine.smartdevice.network.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ycmachine.smartdevice.network.service.MedHttpService;

import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.entity.DevConfigEntity;
import leesche.smartrecycling.base.http.HttpBuilder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class MedHttpMethods extends HttpBuilder {

    private final MedHttpService medHttpService;
    Gson gson;
    JsonParser jsonParser;

    public MedHttpMethods() {
        super();
        medHttpService = getRetrofit().create(MedHttpService.class);
        jsonParser = new JsonParser();
        gson = new Gson();
    }

    private static class SingletonHolder {
        private static final MedHttpMethods INSTANCE = new MedHttpMethods();
    }

    public static MedHttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }



    public void getVoucherConfig(Observer<JsonObject> observer) {
        Observable observable = medHttpService.getVoucherConfig(Constants.DEVICE_TOKEN).map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }


    // 检查barcode是否需要更新
    public void checkBarcodeUpdateVersion(Observer<JsonObject> observer, String version) {

        if (version == null) version = "0.0";

        Observable observable = medHttpService.checkBarcodeUpdateVersion(Constants.DEVICE_TOKEN, version).map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    // 上传机器barcode版本号
    public void uploadBarcodeVersion(Observer<JsonObject> observer, String version) {


        Observable observable = medHttpService.uploadBarcodeVersion(Constants.DEVICE_TOKEN, version).map(new HttpResultFunc<>());

        toSubscribe(observable, observer);


    }


    public void downLoadJson(retrofit2.Callback<ResponseBody> downLoadJsonOnNex, String url) {

        new Thread(() -> {

            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://youyiyun-oss.oss-cn-shanghai.aliyuncs.com/") // 基础URL，实际会使用@Url的完整URL
                    .callbackExecutor(Executors.newSingleThreadExecutor()) // 指定回调在子线程执行
                    .build();

            MedHttpService service = retrofit.create(MedHttpService.class);
            Call<ResponseBody> call = service.downLoadJson(url);

            call.enqueue(downLoadJsonOnNex);
        }).start();

    }


    public void checkConfigVersion(Observer<DevConfigEntity> observer, String version) {
        Observable observable = medHttpService.checkConfigVersion(Constants.DEVICE_TOKEN, version).map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    public void getCarbonWalletConfig(Observer<DevConfigEntity> observer) {
        Observable observable = medHttpService.getCarbonWalletConfig(Constants.DEVICE_TOKEN).map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    public Observable<String> getTDRouterToken() {
        return medHttpService.getTDRouterToken().map(new HttpResultFunc<>());
    }

    public Observable<String> getTD4GRouterInfo(String token) {
        return medHttpService.getTD4GRouterInfo(token).map(new HttpResultFunc<>());
    }


    public Observable<String> getHLGRouterInfo() {
        return medHttpService.getHL4GRouterInfo().map(new HttpResultFunc<>());
    }

    public void getWeightSetting(Observer<DevConfigEntity> observer) {
        Observable observable = medHttpService.getWeightSetting(Constants.DEVICE_TOKEN).map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    public void updateBarcodes(Observer<DevConfigEntity> observer, String barcodes) {
        Observable observable = medHttpService.updateBarcodes(Constants.DEVICE_TOKEN, barcodes).map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

}
