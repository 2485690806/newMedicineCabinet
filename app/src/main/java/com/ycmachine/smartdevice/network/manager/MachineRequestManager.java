package com.ycmachine.smartdevice.network.manager;

import com.ycmachine.smartdevice.network.api.RetrofitManager;
import com.ycmachine.smartdevice.network.service.MedPointMachineService;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class MachineRequestManager {
    // 单例模式
    private static volatile MachineRequestManager instance;
    private final MedPointMachineService machineService;

    private MachineRequestManager() {
        this.machineService = RetrofitManager.getMachineService();
    }

    public static MachineRequestManager getInstance() {
        if (instance == null) {
            synchronized (MachineRequestManager.class) {
                if (instance == null) {
                    instance = new MachineRequestManager();
                }
            }
        }
        return instance;
    }

    // -------------------------- 袋子相关请求 --------------------------
    /**
     * 袋子加载通知（袋子管理核心步骤）
     * @param bagData 请求参数（由 RequestParamCreator 创建）
     * @return 响应字符串（含 bag_id）
     * @throws IOException 网络异常
     */
    public String bagLoadedNotification(Map<String, Object> bagData) throws IOException {
        Response<ResponseBody> response = machineService.bagLoadedNotification(bagData).execute();
        return response.body().string();
    }

    /**
     * 袋子更新通知（更新位置、PIN 码）
     * @param bagId 已获取的 bag_id
     * @param updateData 请求参数（由 RequestParamCreator 创建）
     * @return 响应字符串
     * @throws IOException 网络异常
     */
    public String bagUpdateNotification(String bagId, Map<String, Object> updateData) throws IOException {
        Response<ResponseBody> response = machineService.bagUpdateNotification(bagId, updateData).execute();
        return response.body().string();
    }

    /**
     * 袋子移除通知
     * @param bagId 已获取的 bag_id
     * @throws IOException 网络异常
     */
    public void bagRemovedNotification(String bagId) throws IOException {
        machineService.bagRemovedNotification(bagId).execute();
    }

    /**
     * 袋子收集通知（豁免场景）
     * @param collectionData 请求参数（由 RequestParamCreator 创建）
     * @throws IOException 网络异常
     */
    public void bagCollectedNotification(Map<String, Object> collectionData) throws IOException {
        machineService.bagCollectedNotification(collectionData).execute();
    }

    // -------------------------- 处方相关请求 --------------------------
    /**
     * 处方投递
     * @param barcode 处方条码
     * @return 响应字符串
     * @throws IOException 网络异常
     */
    public String prescriptionDropOff(String barcode) throws IOException {
        Map<String, String> requestBody = Map.of("barcode", barcode);
        Response<ResponseBody> response = machineService.prescriptionDropOff(requestBody).execute();
        return response.body().string();
    }

    // -------------------------- 机器事件/状态请求 --------------------------
    /**
     * 上报机器事件（如门开关、错误）
     * @param eventData 请求参数（由 RequestParamCreator 创建）
     * @throws IOException 网络异常
     */
    public void reportMachineEvent(Map<String, Object> eventData) throws IOException {
        machineService.machineEvent(eventData).execute();
    }

    /**
     * 更新机器状态（如温度、湿度、CPU 负载）
     * @param statusData 请求参数（由 RequestParamCreator 创建）
     * @throws IOException 网络异常
     */
    public void updateMachineStatus(Map<String, Object> statusData) throws IOException {
        machineService.machineStatusUpdate(statusData).execute();
    }

    /**
     * 辅助方法：从袋子响应 Map 中解析 bag_id
     * @param bagResponseData 袋子加载响应解析后的 Map
     * @return bag_id（null 表示解析失败）
     */
    public String getBagIdFromResponse(Map<String, Object> bagResponseData) {
        if (bagResponseData == null || !bagResponseData.containsKey("id")) return null;
        Object value = bagResponseData.get("id");
        return value != null ? value.toString() : null;
    }
}