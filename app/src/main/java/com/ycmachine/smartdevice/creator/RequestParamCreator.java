package com.ycmachine.smartdevice.creator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParamCreator {
    // 私有构造：禁止实例化（工具类仅提供静态方法）
    private RequestParamCreator() {}

    /**
     * 创建袋子加载请求参数
     * @param barcode 条码
     * @param location 位置
     * @param pin PIN 码
     * @return 袋子加载参数 Map
     */
    public static Map<String, Object> createBagLoadedParam(String barcode, String location, String pin) {
        Map<String, Object> param = new HashMap<>();
        param.put("barcode", barcode);
        param.put("location", location);
        param.put("pin", pin);
        param.put("timestamp", getCurrentTimestamp()); // 当前时间戳（毫秒级）
        return param;
    }

    /**
     * 创建袋子更新请求参数
     * @param location 新位置
     * @param pin 新 PIN 码
     * @return 袋子更新参数 Map
     */
    public static Map<String, Object> createBagUpdateParam(String location, String pin) {
        Map<String, Object> param = new HashMap<>();
        param.put("location", location);
        param.put("pin", pin);
        param.put("timestamp", getCurrentTimestamp());
        return param;
    }

    /**
     * 创建袋子收集请求参数（豁免场景）
     * @param bagId 已获取的 bag_id
     * @param exemption 豁免类型（如 "A"）
     * @param amountPaid 支付金额（null 表示无需支付）
     * @return 袋子收集参数 Map
     */
    public static Map<String, Object> createBagCollectionParam(String bagId, String exemption, Double amountPaid) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", bagId);
        param.put("timestamp", getCurrentTimestamp());
        if (exemption != null) param.put("exemption", exemption);
        if (amountPaid != null) param.put("amount_paid", amountPaid);
        return param;
    }

    /**
     * 创建机器普通事件参数（如门开关）
     * @param type 事件类型（如 "door"）
     * @param message 事件描述（如 "opened"、"closed"）
     * @return 普通事件参数 Map
     */
    public static Map<String, Object> createNormalMachineEvent(String type, String message) {
        Map<String, Object> param = new HashMap<>();
        param.put("level", "info"); // 事件级别（info/error）
        param.put("code", type);
        param.put("message", message);
        param.put("time", getCurrentTimestamp());
        return param;
    }

    /**
     * 创建机器错误事件参数（如传感器超时）
     * @param errorCode 错误码（如 "SENSOR_TIMEOUT"）
     * @param errorMsg 错误描述（如 "No response from weight sensor"）
     * @return 错误事件参数 Map
     */
    public static Map<String, Object> createErrorMachineEvent(String errorCode, String errorMsg) {
        Map<String, Object> param = createNormalMachineEvent("system", "error");
        param.put("level", "error"); // 覆盖级别为 error
        param.put("code", errorCode);
        param.put("message", errorMsg);
        return param;
    }

    /**
     * 创建机器状态更新参数（温度、湿度、CPU 负载等）
     * @return 状态更新参数 Map
     */
    public static Map<String, Object> createMachineStatusParam() {
        List<Map<String, Object>> metricsList = new ArrayList<>();
        metricsList.add(createStatusMetrics()); // 具体指标参数
        return Map.of("data", metricsList);
    }

    /**
     * 构建状态指标详情（温度、湿度等）
     * @return 指标参数 Map
     */
    private static Map<String, Object> createStatusMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("temperature", 23.4); // 示例温度（可替换为真实数据）
        metrics.put("humidity", 45.2);    // 示例湿度
        metrics.put("cpu_load", 0.37);    // 示例 CPU 负载
        metrics.put("door_state", "closed"); // 示例门状态
        metrics.put("time", getCurrentTimestamp());
        return metrics;
    }

    /**
     * 获取当前时间戳（毫秒级，与服务端时间格式对齐）
     * @return 毫秒级时间戳
     */
    private static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}