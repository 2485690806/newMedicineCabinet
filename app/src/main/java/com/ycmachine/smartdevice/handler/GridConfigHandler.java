package com.ycmachine.smartdevice.handler;

import static leesche.smartrecycling.base.common.Constants.GRID_CONFIG_FILE;

import android.util.Log;

import com.amazonaws.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.constent.GridConfig;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import leesche.smartrecycling.base.entity.GridRegion;
import leesche.smartrecycling.base.utils.FileUtil;

/**
 * 货道配置存储处理器（本地文件+JSON序列化）
 */
public class GridConfigHandler {
    // 货道配置存储文件路径（可自定义，与其他配置文件区分开）
    // 内存中缓存的货道配置（避免重复读取文件）
    private static List<GridConfig> sCachedGridConfigs;

    /**
     * 初始化货道配置：优先读本地文件，无则返回空（使用默认值）
     */
    public static List<GridConfig> initGridConfig() {
        if (sCachedGridConfigs != null) {
            return sCachedGridConfigs; // 缓存命中，直接返回
        }

        Gson gson = new Gson();
        try {
            String configContent = FileUtil.readFileSdcardFile(GRID_CONFIG_FILE);
            Logger.i("configContent"+configContent);
            if (StringUtils.isBlank(configContent)) {
                sCachedGridConfigs = new ArrayList<>(); // 本地无配置，返回空列表
            } else {
                // 解析JSON为GridConfig列表
                Type type = new TypeToken<List<GridConfig>>() {}.getType();
                sCachedGridConfigs = gson.fromJson(configContent, type);
                if (sCachedGridConfigs == null) {
                    sCachedGridConfigs = new ArrayList<>(); // 解析失败，返回空列表
                }
            }
        } catch (JsonSyntaxException e) {
            Log.e("GridConfigHandler", "JSON解析失败", e);
            sCachedGridConfigs = new ArrayList<>();
        }
        return sCachedGridConfigs;
    }

    /**
     * 保存单个货道配置到本地（覆盖对应层级+摄像头的配置）
     * @param level 层级
     * @param cameraNum 摄像头编号
     * @param gridRegions 修改后的货道列表
     */
    public static void saveGridConfig(int level, int cameraNum, List<GridRegion> gridRegions) {
        List<GridConfig> configs = initGridConfig(); // 先获取现有配置

        // 移除旧的同层级+摄像头配置（避免重复）
        configs.removeIf(config -> config.getLevel() == level && config.getCameraNum() == cameraNum);

        // 添加新配置
        configs.add(new GridConfig(level, cameraNum, gridRegions));

        // 序列化并写入本地文件
        Gson gson = new Gson();
        String jsonContent = gson.toJson(configs);
        FileUtil.writeFileSdcardFile(GRID_CONFIG_FILE, jsonContent);

        // 更新缓存
        sCachedGridConfigs = configs;
        Log.d("GridConfigHandler", "保存货道配置成功：level=" + level + ", cameraNum=" + cameraNum);
    }

    /**
     * 根据层级和摄像头编号，获取本地保存的货道配置（无则返回null）
     */
    public static List<GridRegion> getLocalGridRegions(int level, int cameraNum) {
        List<GridConfig> configs = initGridConfig();
        for (GridConfig config : configs) {
            if (config.getLevel() == level && config.getCameraNum() == cameraNum) {
                return config.getGridRegions(); // 找到匹配的配置，返回货道列表
            }
        }
        return null; // 无本地配置
    }

    /**
     * 清空本地货道配置（可选，用于重置）
     */
    public static void clearLocalGridConfig() {
        FileUtil.writeFileSdcardFile(GRID_CONFIG_FILE, "");
        sCachedGridConfigs = new ArrayList<>();
    }
}