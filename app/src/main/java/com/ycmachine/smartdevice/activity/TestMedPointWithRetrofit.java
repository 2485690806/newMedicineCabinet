package com.ycmachine.smartdevice.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.network.api.OkHttpProvider;
import com.ycmachine.smartdevice.network.api.ResponseHandler;
import com.ycmachine.smartdevice.network.api.RetrofitManager;
import com.ycmachine.smartdevice.network.service.MedPointAuthService;
import com.ycmachine.smartdevice.network.service.MedPointHealthService;
import com.ycmachine.smartdevice.network.service.MedPointMachineService;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class TestMedPointWithRetrofit extends AppCompatActivity {
    // 服务实例（保留static，避免重复创建）
    private static final MedPointAuthService authService = RetrofitManager.getAuthService();
    private static final MedPointHealthService healthService = RetrofitManager.getHealthService();
    private static final MedPointMachineService machineService = RetrofitManager.getMachineService();

    // 线程池（单线程，避免并发网络请求冲突）
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    // 主线程Handler（用于更新UI日志）
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // UI控件
    private TextView tvLog;
    private ScrollView scView;
    private Button btnBagCollection; // 袋子收集按钮（需依赖bagId，初始置灰）

    // 关键数据（跨方法共享）
    private String accessToken; // 认证成功后的Token
    private String bagId;       // 袋子管理获取的BagId


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. 设置布局
        setContentView(R.layout.activity_test_med_point);

        // 2. 初始化UI控件
        initViews();

        // 3. 重定向System.out/err到日志框（让原来的System.out打印到UI）
        redirectSystemOutputToLog();

        // 4. 初始状态：袋子收集按钮置灰（需先执行袋子管理）
        btnBagCollection.setEnabled(false);
    }


    /**
     * 初始化UI控件 + 绑定按钮点击事件
     */
    private void initViews() {
        // 找到日志框
        tvLog = findViewById(R.id.tv_log);

        // 找到所有按钮并绑定点击事件
        findViewById(R.id.btn_health_check).setOnClickListener(v -> testHealthCheck());
        findViewById(R.id.btn_authentication).setOnClickListener(v -> testAuthentication());
        findViewById(R.id.btn_bag_management).setOnClickListener(v -> testBagManagement());
        btnBagCollection = findViewById(R.id.btn_bag_collection);
        scView = findViewById(R.id.sc_view);
        btnBagCollection.setOnClickListener(v -> testBagCollection(bagId));
        findViewById(R.id.btn_prescription_drop).setOnClickListener(v -> testPrescriptionDropOff());
        findViewById(R.id.btn_machine_event).setOnClickListener(v -> testMachineEvents());
        findViewById(R.id.btn_machine_status).setOnClickListener(v -> testMachineStatusUpdate());
        findViewById(R.id.btn_full_test).setOnClickListener(v -> runFullTest());
    }


    /**
     * 重定向System.out和System.err到日志框
     * （原来的System.out.println会自动显示在右侧UI）
     */
    private void redirectSystemOutputToLog() {
        // 重定向System.out
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                appendLog(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                appendLog(new String(b, off, len));
            }
        }));

        // 重定向System.err（错误信息也显示在日志框，颜色不变，可自行改为红色）
        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                appendLog(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                appendLog(new String(b, off, len));
            }
        }));
    }


    /**
     * 追加日志到UI（必须在主线程执行）
     */
    private void appendLog(String content) {
        Logger.i(content);
        mainHandler.post(() -> {
            tvLog.append(content);
            // 自动滚动到最底部（显示最新日志）
            scView.fullScroll(ScrollView.FOCUS_DOWN);
//            tvLog.post(() -> tvLog.scrollTo(0, tvLog.getBottom()));
        });
    }


    // -------------------------- 按钮对应的测试方法 --------------------------

    /**
     * 1. 测试健康检查（按钮1）
     */
    private void testHealthCheck() {
        executor.submit(() -> {
            appendLog("\n=== 开始执行【健康检查】===\n");
            try {
                Call<ResponseBody> call = healthService.getHealthStatus();
                Response<ResponseBody> response = call.execute();
                String healthStatus = ResponseHandler.handleResponse(response);

                System.out.println("健康检查响应: " + healthStatus);
                appendLog("=== 【健康检查】执行完成 ===\n");
            } catch (Exception e) {
                System.err.println("健康检查失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【健康检查】执行失败 ===\n");
            }
        });
    }


    /**
     * 2. 测试认证（按钮2）
     */
    private void testAuthentication() {
        executor.submit(() -> {
            appendLog("\n=== 开始执行【认证】===\n");
            try {
                // 2.1 请求配对码
                String pairingCode = requestPairingCode();
                if (pairingCode == null) {
                    System.err.println("请求配对码失败");
                    appendLog("=== 【认证】执行失败 ===\n");
                    return;
                }

                // 2.2 解析配对码
                Map<String, Object> pairingData = ResponseHandler.parseJsonToMap(pairingCode);
                String deviceCode = getStringFromMap(pairingData, "device_code");
                String userCode = getStringFromMap(pairingData, "user_code");
                System.out.println("设备码: " + deviceCode + ", 用户码: " + userCode);

                // 2.3 完成配对
                String tokenResponse = completePairing(deviceCode);
                if (tokenResponse == null) {
                    System.err.println("完成配对失败");
                    appendLog("=== 【认证】执行失败 ===\n");
                    return;
                }

                // 2.4 解析Token
                Map<String, Object> tokenData = ResponseHandler.parseJsonToMap(tokenResponse);
                accessToken = getStringFromMap(tokenData, "access_token");
                String refreshToken = getStringFromMap(tokenData, "refresh_token");
                System.out.println("获取到access token: " + maskToken(accessToken));

                // 2.5 刷新Token（可选）
                String refreshedToken = refreshAccessToken(refreshToken);
                if (refreshedToken != null) {
                    accessToken = getStringFromMap(ResponseHandler.parseJsonToMap(refreshedToken), "access_token");
                    System.out.println("刷新后的access token: " + maskToken(accessToken));
                }

                // 2.6 设置全局Token（供其他接口使用）
                OkHttpProvider.setAccessToken(accessToken);
                appendLog("=== 【认证】执行成功（已获取Token）===\n");

            } catch (Exception e) {
                System.err.println("认证失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【认证】执行失败 ===\n");
            }
        });
    }


    /**
     * 3. 测试袋子管理（按钮3）
     */
    private void testBagManagement() {
        executor.submit(() -> {
            // 先检查是否有Token（认证未通过则提示）
            if (accessToken == null) {
                appendLog("\n=== 【袋子管理】需先执行【认证】===\n");
                return;
            }

            appendLog("\n=== 开始执行【袋子管理】===\n");
            try {
                // 3.1 加载袋子
                Map<String, Object> bagData = createBagData("ABC123456789", "21", "123456");
                String bagLoadedResponse = machineService.bagLoadedNotification(bagData).execute().body().string();
                System.out.println("袋子加载响应: " + bagLoadedResponse);

                // 3.2 解析BagId
                Map<String, Object> bagResponseData = ResponseHandler.parseJsonToMap(bagLoadedResponse);
                bagId = getStringFromMap(bagResponseData, "id");
                System.out.println("获取到袋子ID: " + bagId);

                // 3.3 更新袋子
                Map<String, Object> updateData = createBagUpdateData("26", "987654");
                String updateResponse = machineService.bagUpdateNotification(bagId, updateData).execute().body().string();
                System.out.println("袋子更新响应: " + updateResponse);

                // 3.4 移除袋子（可选，根据需求决定是否保留）
                // String removeResponse = machineService.bagRemovedNotification(bagId).execute().body().string();
                // System.out.println("袋子移除响应: " + removeResponse);

                appendLog("=== 【袋子管理】执行成功（BagId: " + bagId + "）===\n");
                // 启用袋子收集按钮（已获取bagId）
                mainHandler.post(() -> btnBagCollection.setEnabled(true));

            } catch (Exception e) {
                System.err.println("袋子管理失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【袋子管理】执行失败 ===\n");
                // 置灰袋子收集按钮
                mainHandler.post(() -> btnBagCollection.setEnabled(false));
            }
        });
    }


    /**
     * 4. 测试袋子收集（按钮4，需先执行袋子管理）
     */
    private void testBagCollection(String bagId) {
        executor.submit(() -> {
            if (bagId == null) {
                appendLog("\n=== 【袋子收集】需先执行【袋子管理】获取BagId ===\n");
                return;
            }

            appendLog("\n=== 开始执行【袋子收集】===\n");
            try {
                // 4.1 豁免场景
                Map<String, Object> exemptData = createCollectionData(bagId, "A", null);
                String exemptResponse = machineService.bagCollectedNotification(exemptData).execute().body().string();
                System.out.println("豁免收集响应: " + exemptResponse);

                // 4.2 付费场景
                Map<String, Object> paidData = createCollectionData(bagId, null, 12.50);
                String paidResponse = machineService.bagCollectedNotification(paidData).execute().body().string();
                System.out.println("付费收集响应: " + paidResponse);

                appendLog("=== 【袋子收集】执行完成 ===\n");
            } catch (Exception e) {
                System.err.println("袋子收集失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【袋子收集】执行失败 ===\n");
            }
        });
    }


    /**
     * 5. 测试处方投递（按钮5）
     */
    private void testPrescriptionDropOff() {
        executor.submit(() -> {
            if (accessToken == null) {
                appendLog("\n=== 【处方投递】需先执行【认证】===\n");
                return;
            }

            appendLog("\n=== 开始执行【处方投递】===\n");
            try {
                Map<String, String> prescriptionData = Collections.singletonMap("barcode", "RXBARCODE-00123456789");
                String response = machineService.prescriptionDropOff(prescriptionData).execute().body().string();
                System.out.println("处方投递响应: " + response);

                appendLog("=== 【处方投递】执行完成 ===\n");
            } catch (Exception e) {
                System.err.println("处方投递失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【处方投递】执行失败 ===\n");
            }
        });
    }


    /**
     * 6. 测试机器事件上报（按钮6）
     */
    private void testMachineEvents() {
        executor.submit(() -> {
            if (accessToken == null) {
                appendLog("\n=== 【事件上报】需先执行【认证】===\n");
                return;
            }

            appendLog("\n=== 开始执行【机器事件上报】===\n");
            try {
                // 门打开事件
                Map<String, Object> openEvent = createMachineEvent("door", "opened");
                String openResponse = machineService.machineEvent(openEvent).execute().body().string();
                System.out.println("门打开事件响应: " + openResponse);

                // 门关闭事件
                Map<String, Object> closeEvent = createMachineEvent("door", "closed");
                String closeResponse = machineService.machineEvent(closeEvent).execute().body().string();
                System.out.println("门关闭事件响应: " + closeResponse);

                // 错误事件
                Map<String, Object> errorEvent = createErrorEvent("SENSOR_TIMEOUT", "No response from weight sensor");
                String errorResponse = machineService.machineEvent(errorEvent).execute().body().string();
                System.out.println("错误事件响应: " + errorResponse);

                appendLog("=== 【机器事件上报】执行完成 ===\n");
            } catch (Exception e) {
                System.err.println("事件上报失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【机器事件上报】执行失败 ===\n");
            }
        });
    }


    /**
     * 7. 测试机器状态更新（按钮7）
     */
    private void testMachineStatusUpdate() {
        executor.submit(() -> {
            if (accessToken == null) {
                appendLog("\n=== 【状态更新】需先执行【认证】===\n");
                return;
            }

            appendLog("\n=== 开始执行【机器状态更新】===\n");
            try {
                Map<String, Object> statusData = createStatusData();
                String response = machineService.machineStatusUpdate(statusData).execute().body().string();
                System.out.println("状态更新响应: " + response);

                appendLog("=== 【机器状态更新】执行完成 ===\n");
            } catch (Exception e) {
                System.err.println("状态更新失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【机器状态更新】执行失败 ===\n");
            }
        });
    }


    /**
     * 8. 执行完整测试（按钮8，按顺序执行所有步骤）
     */
    private void runFullTest() {
        executor.submit(() -> {
            appendLog("\n=== 开始【完整测试】（所有步骤）===\n");
            try {
                // 1. 健康检查
                testHealthCheckSync();

                // 2. 认证（获取Token）
                accessToken = testAuthenticationSync();
                if (accessToken == null) {
                    System.err.println("认证失败，完整测试终止");
                    appendLog("=== 【完整测试】终止（认证失败）===\n");
                    return;
                }

                // 3. 袋子管理（获取BagId）
                bagId = testBagManagementSync();
                if (bagId != null) {
                    // 4. 袋子收集
                    testBagCollectionSync(bagId);
                }

                // 5. 处方投递
                testPrescriptionDropOffSync();

                // 6. 事件上报
                testMachineEventsSync();

                // 7. 状态更新
                testMachineStatusUpdateSync();

                appendLog("\n=== 【完整测试】全部执行完成 ===\n");
                // 启用袋子收集按钮
                mainHandler.post(() -> btnBagCollection.setEnabled(true));

            } catch (Exception e) {
                System.err.println("完整测试失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【完整测试】执行失败 ===\n");
            }
        });
    }


    // -------------------------- 辅助方法（原逻辑保留，改为同步执行） --------------------------
    // （以下方法为原代码中的static方法，改为非static同步方法，供上面的异步任务调用）

    private String requestPairingCode() throws IOException {
        Map<String, String> requestBody = Collections.singletonMap("response_type", "device_code");
        Response<ResponseBody> response = authService.requestPairingCode(requestBody).execute();
        return ResponseHandler.handleResponse(response);
    }

    private String completePairing(String deviceCode) throws IOException {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "device_code");
        requestBody.put("code", deviceCode);
        Response<ResponseBody> response = authService.completePairing(requestBody).execute();
        return ResponseHandler.handleResponse(response);
    }

    private String refreshAccessToken(String refreshToken) throws IOException {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "refresh_token");
        requestBody.put("refresh_token", refreshToken);
        Response<ResponseBody> response = authService.refreshAccessToken(requestBody).execute();
        return ResponseHandler.handleResponse(response);
    }

    private Map<String, Object> createBagData(String barcode, String location, String pin) {
        Map<String, Object> data = new HashMap<>();
        data.put("barcode", barcode);
        data.put("location", location);
        data.put("pin", pin);
        data.put("timestamp", nowEpoch());
        return data;
    }

    private Map<String, Object> createBagUpdateData(String location, String pin) {
        Map<String, Object> data = new HashMap<>();
        data.put("location", location);
        data.put("pin", pin);
        data.put("timestamp", nowEpoch());
        return data;
    }

    private Map<String, Object> createCollectionData(String bagId, String exemption, Double amountPaid) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", bagId);
        data.put("timestamp", nowEpoch());
        if (exemption != null) data.put("exemption", exemption);
        if (amountPaid != null) data.put("amount_paid", amountPaid);
        return data;
    }

    private Map<String, Object> createMachineEvent(String type, String event) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", type);
        data.put("event", event);
        data.put("ts", nowEpoch());
        return data;
    }

    private Map<String, Object> createErrorEvent(String code, String message) {
        Map<String, Object> data = createMachineEvent("system", "error");
        data.put("code", code);
        data.put("message", message);
        return data;
    }

    private Map<String, Object> createStatusData() {
        List<Map<String, Object>> metrics = new ArrayList<>();
        metrics.add(createMetric("temperature", 23.4));
        metrics.add(createMetric("humidity", 45.2));
        metrics.add(createMetric("cpu_load", 0.37));
        metrics.add(createMetric("door_state", "closed"));
        return Collections.singletonMap("data", metrics);
    }

    private Map<String, Object> createMetric(String name, Object value) {
        Map<String, Object> metric = new HashMap<>();
        metric.put("name", name);
        metric.put("value", value);
        metric.put("ts", nowEpoch());
        return metric;
    }

    private long nowEpoch() {
        return System.currentTimeMillis() / 1000;
    }

    private String getStringFromMap(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) return null;
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private String maskToken(String token) {
        if (token == null || token.length() <= 10) return "******";
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }


    // -------------------------- 完整测试的同步方法 --------------------------
    // （供runFullTest调用，按顺序同步执行）
    private void testHealthCheckSync() throws IOException {
        appendLog("\n--- 完整测试：健康检查 ---\n");
        Call<ResponseBody> call = healthService.getHealthStatus();
        Response<ResponseBody> response = call.execute();
        String healthStatus = ResponseHandler.handleResponse(response);
        System.out.println("健康状态: " + healthStatus);
    }

    private String testAuthenticationSync() throws IOException {
        appendLog("\n--- 完整测试：认证 ---\n");
        String pairingCode = requestPairingCode();
        if (pairingCode == null) return null;
        Map<String, Object> pairingData = ResponseHandler.parseJsonToMap(pairingCode);
        String deviceCode = getStringFromMap(pairingData, "device_code");
        String tokenResponse = completePairing(deviceCode);
        if (tokenResponse == null) return null;
        Map<String, Object> tokenData = ResponseHandler.parseJsonToMap(tokenResponse);
        String accessToken = getStringFromMap(tokenData, "access_token");
        System.out.println("获取到access token: " + maskToken(accessToken));
        OkHttpProvider.setAccessToken(accessToken);
        return accessToken;
    }

    private String testBagManagementSync() throws IOException {
        appendLog("\n--- 完整测试：袋子管理 ---\n");
        Map<String, Object> bagData = createBagData("ABC123456789", "21", "123456");
        String bagLoadedResponse = machineService.bagLoadedNotification(bagData).execute().body().string();
        System.out.println("袋子加载响应: " + bagLoadedResponse);
        Map<String, Object> bagResponseData = ResponseHandler.parseJsonToMap(bagLoadedResponse);
        String bagId = getStringFromMap(bagResponseData, "id");
        System.out.println("获取到袋子ID: " + bagId);
        return bagId;
    }

    private void testBagCollectionSync(String bagId) throws IOException {
        appendLog("\n--- 完整测试：袋子收集 ---\n");
        Map<String, Object> exemptData = createCollectionData(bagId, "A", null);
        String exemptResponse = machineService.bagCollectedNotification(exemptData).execute().body().string();
        System.out.println("豁免收集响应: " + exemptResponse);
    }

    private void testPrescriptionDropOffSync() throws IOException {
        appendLog("\n--- 完整测试：处方投递 ---\n");
        Map<String, String> prescriptionData = Collections.singletonMap("barcode", "RXBARCODE-00123456789");
        String response = machineService.prescriptionDropOff(prescriptionData).execute().body().string();
        System.out.println("处方投递响应: " + response);
    }

    private void testMachineEventsSync() throws IOException {
        appendLog("\n--- 完整测试：事件上报 ---\n");
        Map<String, Object> openEvent = createMachineEvent("door", "opened");
        String openResponse = machineService.machineEvent(openEvent).execute().body().string();
        System.out.println("门打开事件响应: " + openResponse);
    }

    private void testMachineStatusUpdateSync() throws IOException {
        appendLog("\n--- 完整测试：状态更新 ---\n");
        Map<String, Object> statusData = createStatusData();
        String response = machineService.machineStatusUpdate(statusData).execute().body().string();
        System.out.println("状态更新响应: " + response);
    }


    // -------------------------- 生命周期管理 --------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭线程池（避免内存泄漏）
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        // 恢复System.out/err（可选，避免影响其他页面）
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {}
        }));
    }
}