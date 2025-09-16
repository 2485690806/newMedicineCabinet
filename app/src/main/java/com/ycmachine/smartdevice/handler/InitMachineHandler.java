package com.ycmachine.smartdevice.handler;

import android.util.Log;

import com.amazonaws.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ycmachine.smartdevice.constent.ClientConstant;
import com.ycmachine.smartdevice.entity.ypg.Layer;

import java.lang.reflect.Type;

import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.utils.FileUtil;

public class InitMachineHandler {

    public static void init() {
        getByFile();
        initLayerValue();
    }

    private static void getByFile() {
        Gson gson = new Gson();
        try {
            String serverConfig = FileUtil.readFileSdcardFile(Constants.LAYER_LIST);
            if (StringUtils.isBlank(serverConfig)) {
                // 使用默认值
                ClientConstant.medicineCabinetLayer =  getDefaultLayers();
            } else {
                Type type = new TypeToken<Layer[]>() {}.getType();
                Layer[] parsedLayers = gson.fromJson(serverConfig, type);
                // 校验解析结果，若为null则使用默认值
                if (parsedLayers == null) {
                    ClientConstant.medicineCabinetLayer = getDefaultLayers();
                } else {
                    ClientConstant.medicineCabinetLayer = parsedLayers;
                }
            }
        } catch (JsonSyntaxException e) {
            // 处理JSON格式错误
            Log.e("getByFile", "JSON解析失败", e);
            ClientConstant.medicineCabinetLayer = getDefaultLayers();
        }
    }

    // 提取默认值为单独方法，提高代码复用性
    private static Layer[] getDefaultLayers() {
        return new Layer[]{
                new Layer(450),
                new Layer(365),
                new Layer(295),
                new Layer(215),
                new Layer(160),
                new Layer(105),
                new Layer(50),
                new Layer(0),
                new Layer(450),
                new Layer(80),
        };
    }
    private static int[] getDefaultLayersValues() {

        return  new int[]{1, 3, 5, 7};
    }

    private static void initLayerValue() {
        Gson gson = new Gson();
        try {
            String serverConfig = FileUtil.readFileSdcardFile(Constants.LAYER_VALUE);
            if (StringUtils.isBlank(serverConfig)) {
                // 默认值类型与目标类型一致（int[]）
                ClientConstant.LayerValues = getDefaultLayersValues();
            } else {
                // 解析类型与目标类型一致（int[]）
                Type type = new TypeToken<int[]>() {}.getType();
                ClientConstant.LayerValues = gson.fromJson(serverConfig, type);
                // 可选：验证解析结果非空
                if (ClientConstant.LayerValues == null) {
                    ClientConstant.LayerValues = getDefaultLayersValues();
                }
            }
        } catch (JsonSyntaxException e) {
            // 处理JSON格式错误
            Log.e("initLayerValue", "JSON解析失败", e);
            ClientConstant.LayerValues = getDefaultLayersValues();
        }
    }


}
