package com.ycmachine.smartdevice.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ycmachine.smartdevice.entity.ypg.Layer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class LayerStorageUtil {
    private static final String TAG = "LayerJsonStorageUtil";
    private static final String FILE_NAME = "medicine_cabinet_layers.json";
    private static final Gson gson = new Gson();

    /**
     * 保存Layer数组到本地（JSON格式）
     */
    public static void saveLayers(Context context, Layer[] layers) {
        if (layers == null) {
            Log.e(TAG, "Cannot save null layers array");
            return;
        }

        // 将数组转换为JSON字符串
        String json = gson.toJson(layers);

        // 使用try-with-resources自动关闭流
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(json.getBytes(StandardCharsets.UTF_8));
            Log.d(TAG, "Layers saved successfully");
        } catch (IOException e) {
            Log.e(TAG, "Failed to save layers: " + e.getMessage());
        }
    }

    /**
     * 从本地读取Layer数组（JSON格式），如果没有则返回默认值
     */
    public static Layer[] loadLayers(Context context) {
        try (FileInputStream fis = context.openFileInput(FILE_NAME)) {
            // 读取文件内容
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            String json = new String(buffer, StandardCharsets.UTF_8);

            // 定义数组类型
            Type type = new TypeToken<Layer[]>() {}.getType();

            // 将JSON转换为Layer数组
            Layer[] layers = gson.fromJson(json, type);

            if (layers != null) {
                Log.d(TAG, "Layers loaded successfully");
                return layers;
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to load layers: " + e.getMessage());
        }

        // 读取失败或文件不存在，返回默认数组
        return getDefaultLayers();
    }

    /**
     * 获取默认的Layer数组
     */
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
}
