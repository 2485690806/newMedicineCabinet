package com.ycmachine.smartdevice.constent;

import java.util.List;
import leesche.smartrecycling.base.entity.GridRegion;

/**
 * 货道配置存储实体（用于JSON序列化）
 */
public class GridConfig {
    private int level; // 层级（如0、1、2...）
    private int cameraNum; // 摄像头编号（1或2）
    private List<GridRegion> gridRegions; // 该层级+摄像头对应的货道列表

    // 必须有默认构造方法（Gson解析需要）
    public GridConfig() {}

    public GridConfig(int level, int cameraNum, List<GridRegion> gridRegions) {
        this.level = level;
        this.cameraNum = cameraNum;
        this.gridRegions = gridRegions;
    }

    // Getter和Setter（必须，Gson序列化/反序列化需要）
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getCameraNum() { return cameraNum; }
    public void setCameraNum(int cameraNum) { this.cameraNum = cameraNum; }
    public List<GridRegion> getGridRegions() { return gridRegions; }
    public void setGridRegions(List<GridRegion> gridRegions) { this.gridRegions = gridRegions; }
}