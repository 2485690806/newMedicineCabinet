package com.ycmachine.smartdevice.network.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MedPointHealthService {
    /**
     * 健康检查：GET /health
     */
    @GET("health")
    Call<ResponseBody> getHealthStatus();
}