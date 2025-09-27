package com.ycmachine.smartdevice.network.manager;

import com.ycmachine.smartdevice.network.api.RetrofitManager;
import com.ycmachine.smartdevice.network.api.ResponseHandler;
import com.ycmachine.smartdevice.network.service.MedPointHealthService;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class HealthRequestManager {
    // 单例模式
    private static volatile HealthRequestManager instance;
    private final MedPointHealthService healthService;

    private HealthRequestManager() {
        this.healthService = RetrofitManager.getHealthService();
    }

    public static HealthRequestManager getInstance() {
        if (instance == null) {
            synchronized (HealthRequestManager.class) {
                if (instance == null) {
                    instance = new HealthRequestManager();
                }
            }
        }
        return instance;
    }

    /**
     * 执行健康检查请求
     * @return 健康状态响应字符串
     * @throws IOException 网络异常
     */
    public String getHealthStatus() throws IOException {
        Response<ResponseBody> response = healthService.getHealthStatus().execute();
        return ResponseHandler.handleResponse(response);
    }
}