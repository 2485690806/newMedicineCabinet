package com.ycmachine.smartdevice.network.api;

import com.ycmachine.smartdevice.network.service.MedPointAuthService;
import com.ycmachine.smartdevice.network.service.MedPointHealthService;
import com.ycmachine.smartdevice.network.service.MedPointMachineService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    // 基础地址（注意：Retrofit 要求 baseUrl 必须以 "/" 结尾）
    private static final String BASE_URL = "https://api-v2.medpoint.uk/";
    // 单例 Retrofit
    private static Retrofit INSTANCE;

    // 初始化 Retrofit（懒加载）
    private static void initRetrofit() {
        INSTANCE = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OkHttpProvider.getInstance())  // 关联 OkHttpClient
                .addConverterFactory(GsonConverterFactory.create())  // Gson 解析
                // .addConverterFactory(FastJsonConverterFactory.create()) // 若用 FastJson
                .build();
    }

    // 获取认证 Service
    public static MedPointAuthService getAuthService() {
        if (INSTANCE == null) initRetrofit();
        return INSTANCE.create(MedPointAuthService.class);
    }

    // 获取健康检查 Service
    public static MedPointHealthService getHealthService() {
        if (INSTANCE == null) initRetrofit();
        return INSTANCE.create(MedPointHealthService.class);
    }

    // 获取机器操作 Service
    public static MedPointMachineService getMachineService() {
        if (INSTANCE == null) initRetrofit();
        return INSTANCE.create(MedPointMachineService.class);
    }
}