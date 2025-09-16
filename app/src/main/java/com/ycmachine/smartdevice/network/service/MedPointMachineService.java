package com.ycmachine.smartdevice.network.service;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MedPointMachineService {
    // -------------------------- 袋子管理 --------------------------
    /**
     * 袋子加载：POST /machine/contents
     * @param bagData 请求体：{"barcode":"xxx", "location":"xxx", "pin":"xxx", "timestamp":xxx}
     */
    @POST("machine/contents")
    Call<ResponseBody> bagLoadedNotification(@Body Map<String, Object> bagData);

    /**
     * 袋子更新：PATCH /machine/contents/{bagId}
     * @param bagId 袋子ID（路径参数）
     * @param updateData 更新数据：{"location":"xxx", "pin":"xxx", "timestamp":xxx}
     */
    @PATCH("machine/contents/{bagId}")
    Call<ResponseBody> bagUpdateNotification(
            @Path("bagId") String bagId, 
            @Body Map<String, Object> updateData
    );

    /**
     * 袋子移除：DELETE /machine/contents/{bagId}
     * @param bagId 袋子ID（路径参数）
     */
    @DELETE("machine/contents/{bagId}")
    Call<ResponseBody> bagRemovedNotification(@Path("bagId") String bagId);

    // -------------------------- 袋子收集 --------------------------
    /**
     * 袋子收集：POST /machine/dispenses
     * @param payload 请求体：{"id":"袋子ID", "exemption":"A"/"amount_paid":12.50, "timestamp":xxx}
     */
    @POST("machine/dispenses")
    Call<ResponseBody> bagCollectedNotification(@Body Map<String, Object> payload);

    // -------------------------- 处方投递 --------------------------
    /**
     * 处方投递：POST /machine/prescriptions
     * @param body 请求体：{"barcode":"处方条形码"}
     */
    @POST("machine/prescriptions")
    Call<ResponseBody> prescriptionDropOff(@Body Map<String, String> body);

    // -------------------------- 事件上报 --------------------------
    /**
     * 机器事件：POST /machine/events
     * @param eventData 请求体：{"type":"door", "event":"opened", "ts":xxx}
     */
    @POST("machine/events")
    Call<ResponseBody> machineEvent(@Body Map<String, Object> eventData);

    // -------------------------- 状态更新 --------------------------
    /**
     * 机器状态：POST /machine/status
     * @param statusData 请求体：{"data":[{...}, {...}]}（包含温度、湿度等指标）
     */
    @POST("machine/status")
    Call<ResponseBody> machineStatusUpdate(@Body Map<String, Object> statusData);
}