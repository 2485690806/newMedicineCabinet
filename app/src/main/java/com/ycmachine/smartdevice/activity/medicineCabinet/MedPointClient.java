//package com.ycmachine.smartdevice.activity;
//
//import com.alibaba.fastjson.JSON;
//import com.facebook.stetho.json.ObjectMapper;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.util.Map;
//
//import lombok.var;
//
//public class MedPointClient {
//
//    private static final String BASE_URL = "https://api-v2.medpoint.uk";
//    private static final int CONNECT_TIMEOUT = 10000; // 10秒连接超时
//    private static final int READ_TIMEOUT = 30000;    // 30秒读取超时
//    private final ObjectMapper mapper;
//
//    public MedPointClient() {
//        this.mapper = new ObjectMapper();
//    }
//
//    /* =========================
//       Helper: generic request
//       ========================= */
//    private String send(String url, String method, String bearerToken, Object body)
//            throws IOException {
//
//        HttpURLConnection connection = null;
//        OutputStream os = null;
//        InputStream is = null;
//
//        try {
//            // 创建连接
//            URL requestUrl = new URL(url);
//            connection = (HttpURLConnection) requestUrl.openConnection();
//
//            // 设置基础参数
//            connection.setRequestMethod(method.toUpperCase());
//            connection.setConnectTimeout(CONNECT_TIMEOUT);
//            connection.setReadTimeout(READ_TIMEOUT);
//            connection.setRequestProperty("Content-Type", "application/json");
//
//            // 设置Authorization头
//            if (bearerToken != null && !bearerToken.isBlank()) {
//                connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
//            }
//
//            // 处理有请求体的方法（POST、PUT、PATCH等）
//            if (!"GET".equalsIgnoreCase(method) && !"DELETE".equalsIgnoreCase(method)) {
//                connection.setDoOutput(true); // 允许输出
//                String json = (body == null) ? "" : mapToJson(body);
//
//                // 写入请求体
//                os = connection.getOutputStream();
//                os.write(json.getBytes(StandardCharsets.UTF_8));
//                os.flush();
//            }
//
//            // 获取响应码
//            int responseCode = connection.getResponseCode();
//
//            // 处理响应流（错误响应可能在getErrorStream）
//            is = (responseCode >= 200 && responseCode < 300)
//                    ? connection.getInputStream()
//                    : connection.getErrorStream();
//
//            // 处理204无内容情况
//            if (responseCode == 204) {
//                return null;
//            }
//
//            // 读取响应内容
//            String responseBody = readInputStream(is);
//
//            // 处理错误状态码
//            if (responseCode < 200 || responseCode >= 300) {
//                throw new IOException("HTTP " + responseCode + " - " + responseBody);
//            }
//
//            return responseBody;
//
//        } finally {
//            // 关闭资源
//            if (os != null) try { os.close(); } catch (IOException e) { /* 忽略关闭异常 */ }
//            if (is != null) try { is.close(); } catch (IOException e) { /* 忽略关闭异常 */ }
//            if (connection != null) connection.disconnect();
//        }
//    }
//
//    // 工具方法：从输入流读取字符串
//    private String readInputStream(InputStream is) throws IOException {
//        if (is == null) return "";
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = br.readLine()) != null) {
//            sb.append(line);
//        }
//        return sb.toString();
//    }
//
//    /* =========================
//       Auth / Pairing
//       ========================= */
//
//    /** POST /auth/code  -> 获取device_code (pairing) */
//    public String requestPairingCode() throws IOException {
//        String url = BASE_URL + "/auth/code";
//        Map<String, Object> body = Map.of("response_type", "device_code");
//        return send(url, "POST", null, body);
//    }
//
//    /** POST /auth/token 带grant_type=device_code */
//    public String completePairing(String deviceCode) throws IOException {
//        String url = BASE_URL + "/auth/token";
//        Map<String, Object> body = Map.of(
//                "grant_type", "device_code",
//                "code", deviceCode
//        );
//        return send(url, "POST", null, body);
//    }
//
//    /** POST /auth/token 带grant_type=refresh_token */
//    public String refreshAccessToken(String refreshToken) throws IOException {
//        String url = BASE_URL + "/auth/token";
//        Map<String, Object> body = Map.of(
//                "grant_type", "refresh_token",
//                "refresh_token", refreshToken
//        );
//        return send(url, "POST", null, body);
//    }
//
//    /* =========================
//       Health
//       ========================= */
//
//    /** GET /health */
//    public String getHealthStatus() throws IOException {
//        String url = BASE_URL + "/health";
//        return send(url, "GET", null, null);
//    }
//
//    /* =========================
//       Machine: bags / prescriptions
//       ========================= */
//
//    /** POST /machine/contents  (bag loaded) */
//    public String bagLoadedNotification(String accessToken, Map<String, Object> bagData)
//            throws IOException {
//        String url = BASE_URL + "/machine/contents";
//        return send(url, "POST", accessToken, bagData);
//    }
//
//    /** DELETE /machine/contents/{bagId}  (bag removed) */
//    public String bagRemovedNotification(String accessToken, String bagId)
//            throws IOException {
//        String url = BASE_URL + "/machine/contents/" + bagId;
//        return send(url, "DELETE", accessToken, null);
//    }
//
//    /** POST /machine/dispenses (bag collected) */
//    public String bagCollectedNotification(String accessToken, String bagId, Map<String, Object> data)
//            throws IOException {
//        String url = BASE_URL + "/machine/dispenses";
//        var payload = new java.util.HashMap<String, Object>();
//        payload.put("id", bagId);
//        if (data != null) payload.putAll(data);
//        return send(url, "POST", accessToken, payload);
//    }
//
//    /** PATCH /machine/contents/{bagId}  (bag update) */
//    public String bagUpdateNotification(String accessToken, String bagId, Map<String, Object> updateData)
//            throws IOException {
//        String url = BASE_URL + "/machine/contents/" + bagId;
//        return send(url, "PATCH", accessToken, updateData);
//    }
//
//    /** POST /machine/prescriptions  (drop-off) */
//    public String prescriptionDropOff(String accessToken, String barcode)
//            throws IOException {
//        String url = BASE_URL + "/machine/prescriptions";
//        Map<String, Object> body = Map.of("barcode", barcode);
//        return send(url, "POST", accessToken, body);
//    }
//
//    /* =========================
//       Machine: events & status
//       ========================= */
//
//    /** POST /machine/events */
//    public String machineEvent(String accessToken, Map<String, Object> eventData)
//            throws IOException {
//        String url = BASE_URL + "/machine/events";
//        return send(url, "POST", accessToken, eventData);
//    }
//
//    /** POST /machine/status */
//    public String machineStatusUpdate(String accessToken, Map<String, Object> statusData)
//            throws IOException {
//        String url = BASE_URL + "/machine/status";
//        return send(url, "POST", accessToken, statusData);
//    }
//
//    /* =========================
//       工具方法
//       ========================= */
//
//    /** 解析JSON响应为Map */
//    public Map<String, Object> asJsonMap(String json) throws IOException {
//        if (json == null || json.isBlank()) return Map.of();
//        return jsonToMap(json);
//    }
//
//
//    // 替代 mapper.writeValueAsString(body)：Map → JSON 字符串
//    public static String mapToJson(Object body) {
//        if (body == null) return "";
//        return JSON.toJSONString(body); // 一行搞定
//    }
//
//    // 替代 mapper.readValue(json, new TypeReference<>() {})：JSON → Map
//    public static Map<String, Object> jsonToMap(String json) {
//        if (json == null || json.isBlank()) return Map.of();
//        // 直接指定目标类型，无需 TypeToken（Fastjson2 自动处理泛型）
//        return JSON.parseObject(json, Map.class);
//    }
//}
