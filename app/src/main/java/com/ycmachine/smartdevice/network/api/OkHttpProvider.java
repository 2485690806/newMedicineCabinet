package com.ycmachine.smartdevice.network.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpProvider {
    // 单例 OkHttpClient
    private static OkHttpClient INSTANCE;
    // 认证拦截器（自动添加 Authorization 头）
    private static final AuthInterceptor AUTH_INTERCEPTOR = new AuthInterceptor();
    // 不需要认证的端点（避免给健康检查、配对接口加 Token）
    private static final List<String> NO_AUTH_ENDPOINTS = Arrays.asList(
            "auth/code", 
            "auth/token", 
            "health"
    );

    // 获取 OkHttpClient 实例（单例）
    public static OkHttpClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)  // 连接超时（对齐原代码）
                    .readTimeout(30, TimeUnit.SECONDS)     // 读取超时（对齐原代码）
                    .addInterceptor(AUTH_INTERCEPTOR)      // 添加认证拦截器
                    .build();
        }
        return INSTANCE;
    }

    // 设置 AccessToken（登录/刷新后调用）
    public static void setAccessToken(String accessToken) {
        AUTH_INTERCEPTOR.setAccessToken(accessToken);
    }

    // 清除 AccessToken（登出时调用）
    public static void clearAccessToken() {
        AUTH_INTERCEPTOR.setAccessToken(null);
    }

    // 认证拦截器：判断请求是否需要加 Token
    private static class AuthInterceptor implements Interceptor {
        private String accessToken;

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            String requestUrl = original.url().toString();

            // 1. 判断是否需要认证（排除不需要的端点）
            boolean needAuth = true;
            for (String endpoint : NO_AUTH_ENDPOINTS) {
                if (requestUrl.contains(endpoint)) {
                    needAuth = false;
                    break;
                }
            }

            // 2. 不需要认证：直接放行
            if (!needAuth) {
                return chain.proceed(original);
            }

            // 3. 需要认证且有 Token：添加 Authorization 头
            if (accessToken != null && !accessToken.isBlank()) {
                Request authorizedRequest = original.newBuilder()
                        .header("Authorization", "Bearer " + accessToken)
                        .build();
                return chain.proceed(authorizedRequest);
            }

            // 4. 无 Token：直接放行（由后端返回 401 错误）
            return chain.proceed(original);
        }
    }
}