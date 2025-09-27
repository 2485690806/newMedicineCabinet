package leesche.smartrecycling.base.entity;

import java.util.List;

/**
 * 封装某一层某一摄像头对应的所有格子区域
 */
public class LayerCameraGrid {
    private int level; // 层号（1-8层）
    private int cameraNum; // 摄像头编号（1：左，2：右）
    private List<GridRegion> gridRegions; // 该摄像头拍摄的图片中包含的格子区域

    public LayerCameraGrid(int level, int cameraNum, List<GridRegion> gridRegions) {
        this.level = level;
        this.cameraNum = cameraNum;
        this.gridRegions = gridRegions;
    }

    // Getter
    public int getLevel() {
        return level;
    }

    public int getCameraNum() {
        return cameraNum;
    }

    public List<GridRegion> getGridRegions() {
        return gridRegions;
    }
}
