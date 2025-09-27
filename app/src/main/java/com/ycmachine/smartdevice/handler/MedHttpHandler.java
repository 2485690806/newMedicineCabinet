package com.ycmachine.smartdevice.handler;

import android.content.Context;

import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.creator.RequestParamCreator;
import com.ycmachine.smartdevice.network.api.OkHttpProvider;
import com.ycmachine.smartdevice.network.api.ResponseHandler;
import com.ycmachine.smartdevice.network.manager.AuthRequestManager;
import com.ycmachine.smartdevice.network.manager.HealthRequestManager;
import com.ycmachine.smartdevice.network.manager.MachineRequestManager;
import com.ycmachine.smartdevice.storage.AuthStorage;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import leesche.smartrecycling.base.utils.DataSourceOperator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedHttpHandler {


    // -------------------------- 管理类单例（核心替换：删除原static服务实例，改用管理类） --------- -----------------
    private final AuthRequestManager authManager = AuthRequestManager.getInstance();
    private final HealthRequestManager healthManager = HealthRequestManager.getInstance();
    private final MachineRequestManager machineManager = MachineRequestManager.getInstance();

    // 线程池（保留原逻辑：单线程避免并发冲突）
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public Context context;
    // 关键数据（保留原定义：跨方法共享）
    private String accessToken; // 认证Token
    private String bagId;       // 袋子ID
    private String deviceCode;  // 设备码
    private String userCode;    // 用户码
    private String refreshToken;// 刷新Token


    private static final class YpgLogicHandlerHolder {
        static final MedHttpHandler ToDiLogicHandler = new MedHttpHandler();
    }

    public static MedHttpHandler getInstance() {
        return MedHttpHandler.YpgLogicHandlerHolder.ToDiLogicHandler;
    }

    public void initialize(Context context) {
        this.context = context;
        executor.submit(() -> {
            appendLog("\n=== 开始【完整测试】（所有步骤）===\n");
            try {
                // 恢复认证数据
                restoreAuthFromStorageLocals();

                // 1. 健康检查（同步调用）
                testHealthCheckSync();

                // 2. 认证（若未认证则执行，此处补充原代码遗漏的认证步骤）
                if (accessToken == null) {
                    accessToken = testAuthenticationSync();
                    if (accessToken == null) {
                        appendLog("=== 完整测试：认证失败，终止后续步骤 ===\n");
                        return;
                    }
                }

                // 7. 状态更新
                testMachineStatusUpdateSync();


            } catch (Exception e) {
                System.err.println("完整测试失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【完整测试】执行失败 ===\n");
            }
        });

    }

    /**
     * 8. 有新袋子时的完整测试（调用上面各步骤的同步版本，确保顺序执行）
     */
    public void recordBag(String barcode,String location,String pin) {
        executor.submit(() -> {
            appendLog("\n=== 开始【完整测试】（所有步骤）===\n");
            try {
                // 1. 健康检查（同步调用）
                testHealthCheckSync();

                // 2. 认证（若未认证则执行，此处补充原代码遗漏的认证步骤）
                if (accessToken == null) {
                    accessToken = testAuthenticationSync();
                    if (accessToken == null) {
                        appendLog("=== 完整测试：认证失败，终止后续步骤 ===\n");
                        return;
                    }
                }

                // 3. 袋子管理（获取BagId）
                bagId = testBagManagementSync(barcode,location,pin);
                if (bagId != null) {
                    // 4. 袋子收集
                    DataSourceOperator.getInstance().updateBagIdByItemQr(barcode,bagId);
                    testBagCollectionSync(bagId);
                }

                // 5. 处方投递
                testPrescriptionDropOffSync(barcode);

                appendLog("\n=== 【完整测试袋子】全部执行完成 ===\n");

            } catch (Exception e) {
                System.err.println("完整测试失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【完整测试】执行失败 ===\n");
            }
        });
    }


    private void appendLog(String content) {
        Logger.i(content);
    }

    // -------------------------- 认证数据恢复（保留原逻辑：从AuthStorage读取数据） --------------------------
    private void restoreAuthFromStorageLocals() {
        String storedAccess = AuthStorage.getAccessToken(context);
        String storedRefresh = AuthStorage.getRefreshToken(context);
        String storedDevice = AuthStorage.getDeviceCode(context);
        String storedUser = AuthStorage.getUserCode(context);

        // 恢复AccessToken
        if (storedAccess != null && !storedAccess.isEmpty()) {
            accessToken = storedAccess;
            OkHttpProvider.setAccessToken(accessToken);
            appendLog("[Auth] restore: 已从本地恢复access token\n");
        }
        // 恢复RefreshToken
        if (storedRefresh != null && !storedRefresh.isEmpty()) {
            refreshToken = storedRefresh;
        }
        // 恢复DeviceCode
        if (storedDevice != null && !storedDevice.isEmpty()) {
            deviceCode = storedDevice;
        }
        // 恢复UserCode
        if (storedUser != null && !storedUser.isEmpty()) {
            userCode = storedUser;
        }
        if(deviceCode == null || deviceCode.isEmpty() || userCode == null || userCode.isEmpty()){
            registerMachine();
        } else if(accessToken == null || accessToken.isEmpty()){
            obtainAccessToken();
            appendLog("[Auth] restore: 未找到access_token，请完成配对获取Token（2.2）\n");
        } else {
            appendLog("[Auth] restore: 认证信息完整，可直接使用\n");
        }

        // 刷新按钮状态
        final boolean hasDevice = deviceCode != null && !deviceCode.isEmpty();
        final boolean hasRefresh = refreshToken != null && !refreshToken.isEmpty();
//        mainHandler.post(() -> {
//            findViewById(R.id.btn_obtain_token).setEnabled(hasDevice);
//            findViewById(R.id.btn_refresh_token).setEnabled(hasRefresh);
//        });
    }



    // -------------------------- 核心业务方法（重点替换：原网络请求 -> 管理类调用） --------------------------

    /**
     * 1. 健康检查（调用HealthRequestManager）
     */
    private void testHealthCheck() {
        executor.submit(() -> {
            appendLog("\n=== 开始执行【健康检查】===\n");
            try {
                // 替换：原healthService.getHealthStatus() -> 健康管理类调用
                String healthStatus = healthManager.getHealthStatus();
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
     * 2.1 注册机器（请求配对码，调用AuthRequestManager）
     */
    private void registerMachine() {
        executor.submit(() -> {
            appendLog("\n=== 开始执行【2.1 注册机器 / Register machine】===\n");
            try {
                // 替换：原requestPairingCode() -> 认证管理类调用
                String pairingCode = authManager.requestPairingCode();
                if (pairingCode == null) {
                    System.err.println("请求配对码失败");
                    appendLog("=== 【2.1 注册机器】执行失败 ===\n");
                    return;
                }

                // 解析配对码（替换：原getStringFromMap -> 认证管理类的getValueFromTokenMap）
                Map<String, Object> pairingData = ResponseHandler.parseJsonToMap(pairingCode);
                deviceCode = authManager.getValueFromTokenMap(pairingData, "device_code");
                userCode = authManager.getValueFromTokenMap(pairingData, "user_code");
                System.out.println("设备码 device_code: " + deviceCode + ", 用户码 user_code: " + userCode);

                // 持久化保存（保留原逻辑）
                AuthStorage.saveDeviceAndUser(context, deviceCode, userCode);
                // 启用"获取Token"按钮（保留原逻辑）
//                mainHandler.post(() -> findViewById(R.id.btn_obtain_token).setEnabled(deviceCode != null && !deviceCode.isEmpty()));

                appendLog("=== 【2.1 注册机器】成功：已获取device_code和user_code（请在门户输入user_code授权）===\n");
            } catch (Exception e) {
                System.err.println("注册机器失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【2.1 注册机器】执行失败 ===\n");
            }
        });
    }

    /**
     * 2.2 获取Token（完成配对，调用AuthRequestManager）
     */
    private void obtainAccessToken() {
        executor.submit(() -> {
            if (deviceCode == null) {
                registerMachine();
                appendLog("\n=== 请先执行【2.1 注册机器】以获取device_code（First run 2.1 Register machine）===\n");
                return;
            }

            appendLog("\n=== 开始执行【2.2 获取Token / Obtain token】===\n");
            try {
                // 替换：原completePairing() -> 认证管理类调用
                String tokenResponse = authManager.completePairing(deviceCode);
                if (tokenResponse == null) {
                    System.err.println("完成配对失败");
                    appendLog("=== 【2.2 获取Token】执行失败 ===\n");
                    return;
                }

                // 解析Token（替换：原getStringFromMap/maskToken -> 认证管理类方法）
                Map<String, Object> tokenData = ResponseHandler.parseJsonToMap(tokenResponse);
                accessToken = authManager.getValueFromTokenMap(tokenData, "access_token");
                this.refreshToken = authManager.getValueFromTokenMap(tokenData, "refresh_token");
                System.out.println("获取到access token: " + authManager.maskToken(accessToken));

                // 全局设置Token+持久化（保留原逻辑）
                OkHttpProvider.setAccessToken(accessToken);
                AuthStorage.saveAccessAndRefresh(context, accessToken, refreshToken);
                // 启用"刷新Token"按钮（保留原逻辑）
//                mainHandler.post(() -> findViewById(R.id.btn_refresh_token).setEnabled(refreshToken != null && !refreshToken.isEmpty()));

                appendLog("=== 【2.2 获取Token】执行成功（已获取并设置Token，已保存refresh_token，使用2.3刷新）===\n");
            } catch (Exception e) {
                System.err.println("获取Token失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【2.2 获取Token】执行失败 ===\n");
            }
        });
    }

    /**
     * 2.3 刷新Token（调用AuthRequestManager）
     */
    private void refreshAccessTokenStep() {
        executor.submit(() -> {
            if (refreshToken == null) {
                obtainAccessToken();
                appendLog("\n=== 请先执行【2.2 获取Token】以获取refresh_token（First run 2.2 Obtain token）===\n");
                return;
            }

            appendLog("\n=== 开始执行【2.3 刷新Token / Refresh token】===\n");
            try {
                // 替换：原refreshAccessToken() -> 认证管理类调用
                String refreshedTokenResponse = authManager.refreshAccessToken(refreshToken);
                if (refreshedTokenResponse == null) {
                    System.err.println("刷新Token失败");
                    appendLog("=== 【2.3 刷新Token】执行失败 ===\n");
                    return;
                }

                // 解析新Token（替换：原getStringFromMap/maskToken -> 认证管理类方法）
                Map<String, Object> refreshedData = ResponseHandler.parseJsonToMap(refreshedTokenResponse);
                accessToken = authManager.getValueFromTokenMap(refreshedData, "access_token");
                String newRefresh = authManager.getValueFromTokenMap(refreshedData, "refresh_token");
                if (newRefresh != null && !newRefresh.isEmpty()) {
                    refreshToken = newRefresh;
                }

                // 全局更新Token+持久化（保留原逻辑）
                OkHttpProvider.setAccessToken(accessToken);
                AuthStorage.saveAccessAndRefresh(context, accessToken, refreshToken);
                System.out.println("刷新后的access token: " + authManager.maskToken(accessToken));

                appendLog("=== 【2.3 刷新Token】执行成功（已更新AccessToken）===\n");
            } catch (Exception e) {
                System.err.println("刷新Token失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【2.3 刷新Token】执行失败 ===\n");
            }
        });
    }

//    /**
//     * 3. 袋子管理（调用MachineRequestManager + RequestParamCreator）
//     */
//    private void testBagManagement(String barcode, String location, String pin) {
//
//        executor.submit(() -> {
//            if (accessToken == null) {
//                obtainAccessToken();
//                appendLog("\n=== 【袋子管理】需先执行【认证】===\n");
//                return;
//            }
//
//            appendLog("\n=== 开始执行【袋子管理】===\n");
//            try {
//                // 1. 构建请求参数（替换：原createBagData -> 参数创建工具类）
//                Map<String, Object> bagParam = RequestParamCreator.createBagLoadedParam(barcode, location, pin);
//                // 2. 发送袋子加载请求（替换：原machineService.bagLoadedNotification -> 机器管理类）
//                String bagLoadedResponse = machineManager.bagLoadedNotification(bagParam);
//                System.out.println("袋子加载响应: " + bagLoadedResponse);
//
//                // 3. 解析BagId（替换：原getStringFromMap -> 机器管理类的getBagIdFromResponse）
//                Map<String, Object> bagResponseData = ResponseHandler.parseJsonToMap(bagLoadedResponse);
//                bagId = machineManager.getBagIdFromResponse(bagResponseData);
//                System.out.println("获取到袋子ID: " + bagId);
//
//                // 启用袋子收集按钮（保留原逻辑）
////                mainHandler.post(() -> btnBagCollection.setEnabled(true));
//                appendLog("=== 【袋子管理】执行成功（BagId: " + bagId + "）===\n");
//
//            } catch (Exception e) {
//                System.err.println("袋子管理失败: " + e.getMessage());
//                e.printStackTrace();
////                mainHandler.post(() -> btnBagCollection.setEnabled(false));
//                appendLog("=== 【袋子管理】执行失败 ===\n");
//            }
//        });
//    }

    /**
     * 3.A 袋子更新（调用MachineRequestManager + RequestParamCreator）
     */
    public void BagUpdateOnly(String location, String pin) {
        if (accessToken == null) {
            obtainAccessToken();
            appendLog("\n=== 【袋子更新】需先执行【认证】===\n");
            return;
        }
        if (bagId == null) {
            appendLog("\n=== 【袋子更新】需先执行【袋子管理】获取BagId ===\n");
            return;
        }

        // 读取输入框（UI线程操作）

        executor.submit(() -> {
            appendLog("\n=== 开始执行【袋子更新（手动）】===\n");
            try {
                // 1. 构建更新参数（替换：原createBagUpdateData -> 参数创建工具类）
                Map<String, Object> updateParam = RequestParamCreator.createBagUpdateParam(location, pin);
                // 2. 发送更新请求（替换：原machineService.bagUpdateNotification -> 机器管理类）
                String updateResponse = machineManager.bagUpdateNotification(bagId, updateParam);
                System.out.println("袋子更新响应: " + updateResponse);

                appendLog("=== 【袋子更新（手动）】执行完成 ===\n");
            } catch (Exception e) {
                System.err.println("袋子更新失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【袋子更新（手动）】执行失败 ===\n");
            }
        });
    }

    /**
     * 3.B 袋子移除（调用MachineRequestManager）
     */
    public void BagRemove(String bagId) {
        if (accessToken == null) {
            obtainAccessToken();
            appendLog("\n=== 【袋子移除】需先执行【认证】===\n");
            return;
        }
        if (bagId == null) {
            appendLog("\n=== 【袋子移除】需先执行【袋子管理】获取BagId ===\n");
            return;
        }

        executor.submit(() -> {
            appendLog("\n=== 开始执行【袋子移除】===\n");
            try {
                // 替换：原machineService.bagRemovedNotification -> 机器管理类
                machineManager.bagRemovedNotification(bagId);
                System.out.println("袋子移除响应");
                appendLog("=== 【袋子移除】执行完成 ===\n");
            } catch (Exception e) {
                System.err.println("袋子移除失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【袋子移除】执行失败 ===\n");
            }
        });
    }

//    /**
//     * 4. 袋子收集（调用MachineRequestManager + RequestParamCreator）
//     */
//    private void testBagCollection(String bagId) {
//        executor.submit(() -> {
//            if (bagId == null) {
//                appendLog("\n=== 【袋子收集】需先执行【袋子管理】获取BagId ===\n");
//                return;
//            }
//
//            appendLog("\n=== 开始执行【袋子收集】===\n");
//            try {
//                // 1. 构建收集参数（替换：原createCollectionData -> 参数创建工具类）
//                Map<String, Object> collectionParam = RequestParamCreator.createBagCollectionParam(bagId, "A", null);
//                // 2. 发送收集请求（替换：原machineService.bagCollectedNotification -> 机器管理类）
//                machineManager.bagCollectedNotification(collectionParam);
//                System.out.println("豁免收集响应");
//
//                appendLog("=== 【袋子收集】执行完成 ===\n");
//            } catch (Exception e) {
//                System.err.println("袋子收集失败: " + e.getMessage());
//                e.printStackTrace();
//                appendLog("=== 【袋子收集】执行失败 ===\n");
//            }
//        });
//    }
//
//    /**
//     * 5. 处方投递（调用MachineRequestManager）
//     */
//    private void testPrescriptionDropOff() {
//        executor.submit(() -> {
//            if (accessToken == null) {
//                obtainAccessToken();
//                appendLog("\n=== 【处方投递】需先执行【认证】===\n");
//                return;
//            }
//
//            appendLog("\n=== 开始执行【处方投递】===\n");
//            try {
//                // 读取条码（保留原逻辑，可改为从输入框获取）
//                String barcode = "RXBARCODE-00123456789";
//                // 替换：原machineService.prescriptionDropOff -> 机器管理类
//                String response = machineManager.prescriptionDropOff(barcode);
//                System.out.println("处方投递响应: " + response);
//
//                appendLog("=== 【处方投递】执行完成 ===\n");
//            } catch (Exception e) {
//                System.err.println("处方投递失败: " + e.getMessage());
//                e.printStackTrace();
//                appendLog("=== 【处方投递】执行失败 ===\n");
//            }
//        });
//    }

    /**
     * 6. 机器事件上报（调用MachineRequestManager + RequestParamCreator）
     */
    private void testMachineEvents() {
        executor.submit(() -> {
            if (accessToken == null) {
                obtainAccessToken();
                appendLog("\n=== 【事件上报】需先执行【认证】===\n");
                return;
            }

            appendLog("\n=== 开始执行【机器事件上报】===\n");
            try {
                // 1. 构建门打开事件（替换：原createMachineEvent -> 参数创建工具类）
                Map<String, Object> openEvent = RequestParamCreator.createNormalMachineEvent("door", "opened");
                machineManager.reportMachineEvent(openEvent);
                System.out.println("门打开事件响应");

                // 2. 构建门关闭事件
                Map<String, Object> closeEvent = RequestParamCreator.createNormalMachineEvent("door", "closed");
                machineManager.reportMachineEvent(closeEvent);
                System.out.println("门关闭事件响应");

                // 3. 构建错误事件（替换：原createErrorEvent -> 参数创建工具类）
                Map<String, Object> errorEvent = RequestParamCreator.createErrorMachineEvent(
                        "SENSOR_TIMEOUT", "No response from weight sensor"
                );
                machineManager.reportMachineEvent(errorEvent);
                System.out.println("错误事件响应");

                appendLog("=== 【机器事件上报】执行完成 ===\n");
            } catch (Exception e) {
                System.err.println("事件上报失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【机器事件上报】执行失败 ===\n");
            }
        });
    }

    /**
     * 7. 机器状态更新（调用MachineRequestManager + RequestParamCreator）
     */
    private void testMachineStatusUpdate() {
        executor.submit(() -> {
            if (accessToken == null) {
                obtainAccessToken();
                appendLog("\n=== 【状态更新】需先执行【认证】===\n");
                return;
            }

            appendLog("\n=== 开始执行【机器状态更新】===\n");
            try {
                // 1. 构建状态参数（替换：原createStatusData -> 参数创建工具类）
                Map<String, Object> statusParam = RequestParamCreator.createMachineStatusParam();
                // 2. 发送状态更新请求（替换：原machineService.machineStatusUpdate -> 机器管理类）
                machineManager.updateMachineStatus(statusParam);
                System.out.println("状态更新响应");

                appendLog("=== 【机器状态更新】执行完成 ===\n");
            } catch (Exception e) {
                System.err.println("状态更新失败: " + e.getMessage());
                e.printStackTrace();
                appendLog("=== 【机器状态更新】执行失败 ===\n");
            }
        });
    }

//    /**
//     * 8. 有新袋子时的完整测试（调用上面各步骤的同步版本，确保顺序执行）
//     */
//    private void runFullTest(String barcode,String location,String pin) {
//        executor.submit(() -> {
//            appendLog("\n=== 开始【完整测试】（所有步骤）===\n");
//            try {
//                // 1. 健康检查（同步调用）
//                testHealthCheckSync();
//
//                // 2. 认证（若未认证则执行，此处补充原代码遗漏的认证步骤）
//                if (accessToken == null) {
//                    accessToken = testAuthenticationSync();
//                    if (accessToken == null) {
//                        appendLog("=== 完整测试：认证失败，终止后续步骤 ===\n");
//                        return;
//                    }
//                }
//
//                // 3. 袋子管理（获取BagId）
//                bagId = testBagManagementSync(barcode,location,pin);
//                if (bagId != null) {
//                    // 4. 袋子收集
//                    testBagCollectionSync(bagId);
//                }
//
//                // 5. 处方投递
//                testPrescriptionDropOffSync(barcode);
//
//                // 6. 事件上报
//                testMachineEventsSync("door","opened");
//
//                // 7. 状态更新
//                testMachineStatusUpdateSync();
//
//                // 启用袋子收集按钮
////                mainHandler.post(() -> btnBagCollection.setEnabled(true));
//                appendLog("\n=== 【完整测试】全部执行完成 ===\n");
//
//            } catch (Exception e) {
//                System.err.println("完整测试失败: " + e.getMessage());
//                e.printStackTrace();
//                appendLog("=== 【完整测试】执行失败 ===\n");
//            }
//        });
//    }


    // -------------------------- 完整测试的同步方法（替换为管理类调用） --------------------------
    private void testHealthCheckSync() throws IOException {
        appendLog("\n--- 完整测试：健康检查 ---\n");
        String healthStatus = healthManager.getHealthStatus();
        System.out.println("健康状态: " + healthStatus);
    }

    private String testAuthenticationSync() throws IOException {
        appendLog("\n--- 完整测试：认证 ---\n");
        // 1. 请求配对码
        String pairingCode = authManager.requestPairingCode();
        if (pairingCode == null) return null;
        // 2. 解析DeviceCode
        Map<String, Object> pairingData = ResponseHandler.parseJsonToMap(pairingCode);
        String deviceCode = authManager.getValueFromTokenMap(pairingData, "device_code");
        // 3. 完成配对
        String tokenResponse = authManager.completePairing(deviceCode);
        if (tokenResponse == null) return null;
        // 4. 解析AccessToken
        Map<String, Object> tokenData = ResponseHandler.parseJsonToMap(tokenResponse);
        String accessToken = authManager.getValueFromTokenMap(tokenData, "access_token");
        this.refreshToken = authManager.getValueFromTokenMap(tokenData, "refresh_token");

        // 全局设置+持久化
        System.out.println("获取到access token: " + authManager.maskToken(accessToken));
        OkHttpProvider.setAccessToken(accessToken);
        AuthStorage.saveAccessAndRefresh(context, accessToken, refreshToken);
        return accessToken;
    }

    private String testBagManagementSync(String barcode,String location,String pin) throws IOException {

        appendLog("\n--- 完整测试：袋子管理 ---\n");
        // 构建参数+发送请求
        Map<String, Object> bagParam = RequestParamCreator.createBagLoadedParam(barcode, location, pin);
        String bagLoadedResponse = machineManager.bagLoadedNotification(bagParam);
        // 解析BagId
        Map<String, Object> bagResponseData = ResponseHandler.parseJsonToMap(bagLoadedResponse);
        String bagId = machineManager.getBagIdFromResponse(bagResponseData);

        System.out.println("袋子加载响应: " + bagLoadedResponse);
        System.out.println("获取到袋子ID: " + bagId);
        return bagId;
    }

    private void testBagCollectionSync(String bagId) throws IOException {
        appendLog("\n--- 完整测试：袋子收集 ---\n");
        Map<String, Object> collectionParam = RequestParamCreator.createBagCollectionParam(bagId, "A", null);
        machineManager.bagCollectedNotification(collectionParam);
        System.out.println("豁免收集响应");
    }

    private void testPrescriptionDropOffSync(String barcode) throws IOException {
        appendLog("\n--- 完整测试：处方投递 ---\n");
        String response = machineManager.prescriptionDropOff(barcode);
        System.out.println("处方投递响应: " + response);
    }

    private void testMachineEventsSync(String type,String message) throws IOException {
        appendLog("\n--- 完整测试：事件上报 ---\n");
        Map<String, Object> openEvent = RequestParamCreator.createNormalMachineEvent(type, message);
        machineManager.reportMachineEvent(openEvent);
        System.out.println("门打开事件响应");
    }

    private void testMachineStatusUpdateSync() throws IOException {
        appendLog("\n--- 完整测试：状态更新 ---\n");
        Map<String, Object> statusParam = RequestParamCreator.createMachineStatusParam();
        machineManager.updateMachineStatus(statusParam);
        System.out.println("状态更新响应");
    }


    // -------------------------- 生命周期管理（保留原逻辑：关闭线程池+恢复System输出） --------------------------
    public void onDestroy() {
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
        // 恢复System.out/err（避免影响其他页面）
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {}
        }));
    }

}
