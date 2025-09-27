package com.ycmachine.smartdevice.network.manager;

import com.ycmachine.smartdevice.network.api.RetrofitManager;
import com.ycmachine.smartdevice.network.service.MedPointAuthService;
import com.ycmachine.smartdevice.network.api.ResponseHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class AuthRequestManager {
    // 单例模式（避免重复创建）
    private static volatile AuthRequestManager instance;
    private final MedPointAuthService authService;

    // 私有构造：通过 RetrofitManager 获取服务实例
    private AuthRequestManager() {
        this.authService = RetrofitManager.getAuthService();
    }

    // 单例获取方法
    public static AuthRequestManager getInstance() {
        if (instance == null) {
            synchronized (AuthRequestManager.class) {
                if (instance == null) {
                    instance = new AuthRequestManager();
                }
            }
        }
        return instance;
    }

    /**
     * 1. 请求配对码（Request Pairing Code）
     * @return 配对码响应字符串（含 device_code、user_code）
     * @throws IOException 网络异常
     */
    public String requestPairingCode() throws IOException {
        Map<String, String> requestBody = Collections.singletonMap("response_type", "device_code");
        Response<ResponseBody> response = authService.requestPairingCode(requestBody).execute();
        return ResponseHandler.handleResponse(response);
    }

    /**
     * 2. 完成配对（获取 AccessToken）
     * @param deviceCode 从配对码响应中解析的 device_code
     * @return Token 响应字符串（含 access_token、refresh_token）
     * @throws IOException 网络异常
     */
    public String completePairing(String deviceCode) throws IOException {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "device_code");
        requestBody.put("code", deviceCode);
        
        Response<ResponseBody> response = authService.completePairing(requestBody).execute();
        return ResponseHandler.handleResponse(response);
    }

    /**
     * 3. 刷新 AccessToken
     * @param refreshToken 之前获取的 refresh_token
     * @return 新 Token 响应字符串（含新 access_token、新 refresh_token）
     * @throws IOException 网络异常
     */
    public String refreshAccessToken(String refreshToken) throws IOException {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "refresh_token");
        requestBody.put("refresh_token", refreshToken);
        
        Response<ResponseBody> response = authService.refreshAccessToken(requestBody).execute();
        return ResponseHandler.handleResponse(response);
    }

    /**
     * 辅助方法：从 Token 响应 Map 中解析指定字段（如 access_token、refresh_token）
     * @param tokenData Token 响应解析后的 Map
     * @param key 要获取的字段名
     * @return 字段值（null 表示字段不存在或值为空）
     */
    public String getValueFromTokenMap(Map<String, Object> tokenData, String key) {
        if (tokenData == null || !tokenData.containsKey(key)) return null;
        Object value = tokenData.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 辅助方法：隐藏 Token 中间部分（安全显示用）
     * @param token 原始 Token
     * @return 隐藏后的 Token（如：abc123...789xyz）
     */
    public String maskToken(String token) {
        if (token == null || token.length() <= 10) return "******";
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}