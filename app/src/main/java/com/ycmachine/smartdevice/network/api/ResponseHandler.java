package com.ycmachine.smartdevice.network.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ResponseHandler {
    private static final Gson GSON = new Gson();

    /**
     * 处理 Retrofit 响应：204 返回 null，非 200-300 抛异常
     */
    public static String handleResponse(Response<ResponseBody> response) throws IOException {
        if (response.isSuccessful()) {
            // 处理 204 No Content（无响应体）
            if (response.code() == 204) {
                return null;
            }
            // 读取响应体（注意：ResponseBody 只能读取一次）
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            String bodyStr = body.string();
            // 处理 Content-Length 为 0 的情况
            String contentLength = response.headers().get("Content-Length");
            if (contentLength != null && "0".equals(contentLength)) {
                return null;
            }
            return bodyStr;
        } else {
            // 处理 HTTP 错误（如 400、401、500）
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
            throw new IOException("HTTP " + response.code() + " - " + errorBody);
        }
    }

    /**
     * 解析 JSON 字符串为 Map（对齐原 asJsonMap 方法）
     */
    public static Map<String, Object> parseJsonToMap(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        return GSON.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
    }

    /**
     * （可选）解析 JSON 为实体类（类型更安全）
     */
    public static <T> T parseJsonToEntity(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) {
            return null;
        }
        return GSON.fromJson(json, clazz);
    }
}