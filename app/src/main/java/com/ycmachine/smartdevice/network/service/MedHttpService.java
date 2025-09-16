package com.ycmachine.smartdevice.network.service;

import com.google.gson.JsonObject;

import io.reactivex.rxjava3.core.Observable;
import leesche.smartrecycling.base.entity.DevConfigEntity;
import leesche.smartrecycling.base.entity.HttpResult;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface MedHttpService {

    @POST("c/recycling/app/uploadRunningData")
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> uploadRunningData(@Field("$access_token") String token,
                                                         @Field("orderId") String orderId,
                                                         @Field("deviceUploadType") String deviceUploadType,
                                                         @Field("data") String data);

    @POST("c/recycling/app/uploadRunningLog")
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> uploadRunningLog(@Field("$access_token") String token,
                                                        @Field("orderId") Long orderId,
                                                        @Field("deviceStatus") String deviceStatus,
                                                        @Field("reason") String reason,
                                                        @Field("preOrderId") Long preOrderId,
                                                        @Field("timestamp") Long timestamp,
                                                        @Field("remark") String remark);

    @POST("c/recycling/app/getVoucherConfig")
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> getVoucherConfig(@Field("$access_token") String token);

    @POST("c/recycling/app/checkConfigVersion")
    @FormUrlEncoded
    Observable<HttpResult<DevConfigEntity>> checkConfigVersion(@Field("$access_token") String token,
                                                               @Field("version") String version);

    @POST("c/recycling/app/getCarbonWalletConfig")
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> getCarbonWalletConfig(@Field("$access_token") String token);

    /***************************************************************************************************
     *
     *                                  腾达路由器
     *
     * **************************************************************************************************
     */
    @GET("http://192.168.2.1/cgi-bin/webapi?op=login&username=root&password=admin")
    Observable<HttpResult<String>> getTDRouterToken();

    @GET("http://192.168.2.1/cgi-bin/webapi?op=get_module_signal")
    Observable<HttpResult<String>> getTD4GRouterInfo(@Query("access_token") String token);

    @GET("http://192.168.2.1/cgi-bin/npapply.cgi?action=Obtain&csq&iccid&imei&wan_conn_status")
    Observable<HttpResult<String>> getHL4GRouterInfo();

    // 检查barcode是否需要更新
    @POST("c/recycling/app/checkBarcodeUpdateVersion")
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> checkBarcodeUpdateVersion(@Field("$access_token") String token,
                                                                 @Field("version") String version);

    // 上传机器barcode版本号
    @POST("c/recycling/app/uploadBarcodeVersion")
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> uploadBarcodeVersion(@Field("$access_token") String token,
                                                               @Field("version") String version);
    // 上传机器barcode版本号
    @Streaming
    @GET
        Call<ResponseBody> downLoadJson(@Url String url);

    // 17.获取重量配置
    @POST("c/recycling/app/getWeightSetting")
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> getWeightSetting(@Field("$access_token") String token);

    // 18.更新条形码
    @POST("c/recycling/app/updateBarcodes")
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> updateBarcodes(@Field("$access_token") String token,
                                                      @Field("barcodes") String barcodes
    );


}
