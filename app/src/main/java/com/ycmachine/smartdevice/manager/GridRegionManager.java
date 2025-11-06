package com.ycmachine.smartdevice.manager;

import com.ycmachine.smartdevice.handler.GridConfigHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import leesche.smartrecycling.base.entity.GridRegion;

/**
 * 格子区域管理类（单例）：管理所有层、所有摄像头的格子区域数据
 */
public class GridRegionManager {
    // 单例实例
    private static volatile GridRegionManager instance;

    // 核心存储结构：key为"层号_摄像头编号"（如"8_1"表示8层左摄像头），value为该组合对应的格子区域列表
    private Map<String, List<GridRegion>> gridRegionMap;

    private GridRegionManager() {
        gridRegionMap = new HashMap<>();
        // 初始化所有层和摄像头的格子数据（可根据实际情况扩展）
        initGridRegions();
    }

    private static final class YpgLogicHandlerHolder {
        static final GridRegionManager ToDiLogicHandler = new GridRegionManager();
    }

    public static GridRegionManager getInstance() {
        return YpgLogicHandlerHolder.ToDiLogicHandler;
    }

    private final Map<Integer, GridRegion> laneCodeToGrid = new HashMap<>();

    // 自定义方法：同时添加区域到系统并更新哈希表
    private void addGridRegionsAndUpdateMap(int level, int cameraNum, List<GridRegion> regions) {
        regions.forEach(grid -> laneCodeToGrid.put(Integer.valueOf(grid.getGridNumber()), grid)); // 填充哈希表
    }

    /**
     * 初始化所有层和摄像头的格子区域数据
     * 这里以第8层为例，实际需补充1-7层的左右摄像头数据
     */
    private void initGridRegions() {
        // 初始化本地货道配置（必须先调用，加载缓存）
        GridConfigHandler.initGridConfig();

        // ------------------------------
        // 第7层 摄像头1（cameraNum=1）
        // ------------------------------
        List<GridRegion> level7Cam1Grids = GridConfigHandler.getLocalGridRegions(7, 1);
        if (level7Cam1Grids == null) { // 本地无配置，使用默认值
            level7Cam1Grids = List.of(
                    new GridRegion(297, 682, 408, 646, "0121"),
                    new GridRegion(678, 682, 474, 646, "0122"),
                    new GridRegion(1116, 682, 489, 646, "0123"),
                    new GridRegion(1572, 682, 498, 646, "0124"),
                    new GridRegion(2031, 682, 487, 646, "0125"),
                    new GridRegion(2481, 682, 498, 646, "0126")
            );
        }
        addGridRegions(7, 1, level7Cam1Grids);


        // ------------------------------
        // 第6层 摄像头1（cameraNum=1）
        // ------------------------------
        List<GridRegion> level6Cam1Grids = GridConfigHandler.getLocalGridRegions(6, 1);
        if (level6Cam1Grids == null) {
            level6Cam1Grids = List.of(
                    new GridRegion(297, 682, 408, 646, "0106"),
                    new GridRegion(678, 682, 474, 646, "0107"),
                    new GridRegion(1116, 682, 489, 646, "0108"),
                    new GridRegion(1572, 682, 498, 646, "0109"),
                    new GridRegion(2031, 682, 487, 646, "0110"),
                    new GridRegion(2481, 682, 498, 646, "0111")
            );
        }
        addGridRegions(6, 1, level6Cam1Grids);


        // ------------------------------
        // 第5层 摄像头1（cameraNum=1）
        // ------------------------------
        List<GridRegion> level5Cam1Grids = GridConfigHandler.getLocalGridRegions(5, 1);
        if (level5Cam1Grids == null) {
            level5Cam1Grids = List.of(
                    new GridRegion(297, 682, 408, 646, "091"),
                    new GridRegion(678, 682, 474, 646, "092"),
                    new GridRegion(1116, 682, 489, 646, "093"),
                    new GridRegion(1572, 682, 498, 646, "094"),
                    new GridRegion(2031, 682, 487, 646, "095"),
                    new GridRegion(2481, 682, 498, 646, "096")
            );
        }
        addGridRegions(5, 1, level5Cam1Grids);


        // ------------------------------
        // 第4层 摄像头1（cameraNum=1）
        // ------------------------------
        List<GridRegion> level4Cam1Grids = GridConfigHandler.getLocalGridRegions(4, 1);
        if (level4Cam1Grids == null) {
            level4Cam1Grids = List.of(
                    new GridRegion(297, 682, 408, 646, "076"),
                    new GridRegion(678, 682, 474, 646, "077"),
                    new GridRegion(1116, 682, 489, 646, "078"),
                    new GridRegion(1572, 682, 498, 646, "079"),
                    new GridRegion(2031, 682, 487, 646, "080"),
                    new GridRegion(2481, 682, 498, 646, "081")
            );
        }
        addGridRegions(4, 1, level4Cam1Grids);


        // ------------------------------
        // 第3层 摄像头1（cameraNum=1）
        // ------------------------------
        List<GridRegion> level3Cam1Grids = GridConfigHandler.getLocalGridRegions(3, 1);
        if (level3Cam1Grids == null) {
            level3Cam1Grids = List.of(
                    new GridRegion(297, 682, 408, 646, "061"),
                    new GridRegion(678, 682, 474, 646, "062"),
                    new GridRegion(1116, 682, 489, 646, "063"),
                    new GridRegion(1572, 682, 498, 646, "064"),
                    new GridRegion(2031, 682, 487, 646, "065"),
                    new GridRegion(2481, 682, 498, 646, "066")
            );
        }
        addGridRegions(3, 1, level3Cam1Grids);


        // ------------------------------
        // 第2层 摄像头1（cameraNum=1）
        // ------------------------------
        List<GridRegion> level2Cam1Grids = GridConfigHandler.getLocalGridRegions(2, 1);
        if (level2Cam1Grids == null) {
            level2Cam1Grids = List.of(
                    new GridRegion(234, 535, 573, 769, "046"),
                    new GridRegion(726, 535, 618, 769, "047"),
                    new GridRegion(1296, 535, 580, 769, "048"),
                    new GridRegion(1842, 535, 600, 769, "049"),
                    new GridRegion(2376, 535, 582, 769, "050")
            );
        }
        addGridRegions(2, 1, level2Cam1Grids);


        // ------------------------------
        // 第1层 摄像头1（cameraNum=1）
        // ------------------------------
        List<GridRegion> level1Cam1Grids = GridConfigHandler.getLocalGridRegions(1, 1);
        if (level1Cam1Grids == null) {
            level1Cam1Grids = List.of(
                    new GridRegion(234, 535, 573, 769, "031"),
                    new GridRegion(726, 535, 618, 769, "032"),
                    new GridRegion(1296, 535, 580, 769, "033"),
                    new GridRegion(1842, 535, 600, 769, "034"),
                    new GridRegion(2376, 535, 582, 769, "035")
            );
        }
        addGridRegions(1, 1, level1Cam1Grids);


        // ------------------------------
        // 第0层 摄像头1（cameraNum=1）
        // ------------------------------
        List<GridRegion> level0Cam1Grids = GridConfigHandler.getLocalGridRegions(0, 1);
        if (level0Cam1Grids == null) {
            level0Cam1Grids = List.of(
                    new GridRegion(288, 445, 624, 865, "016"),
                    new GridRegion(879, 445, 726, 865, "017"),
                    new GridRegion(1572, 445, 726, 865, "018"),
                    new GridRegion(2271, 445, 714, 865, "019")
            );
        }
        addGridRegions(0, 1, level0Cam1Grids);


        // ------------------------------
        // 第10层 摄像头1（cameraNum=1）
        // ------------------------------
        List<GridRegion> level10Cam1Grids = GridConfigHandler.getLocalGridRegions(10, 1);
        if (level10Cam1Grids == null) {
            level10Cam1Grids = List.of(
                    new GridRegion(288, 445, 624, 865, "001"),
                    new GridRegion(879, 445, 726, 865, "002"),
                    new GridRegion(1572, 445, 726, 865, "003"),
                    new GridRegion(2271, 445, 714, 865, "004")
            );
        }
        addGridRegions(10, 1, level10Cam1Grids);


        // ------------------------------
        // 第7层 摄像头2（cameraNum=2）
        // ------------------------------
        List<GridRegion> level7Cam2Grids = GridConfigHandler.getLocalGridRegions(7, 2);
        if (level7Cam2Grids == null) {
            level7Cam2Grids = List.of(
                    new GridRegion(732, 691, 492, 685, "0127"),
                    new GridRegion(1167, 691, 519, 685, "0128"),
                    new GridRegion(1638, 691, 492, 685, "0129"),
                    new GridRegion(2082, 691, 501, 685, "0130"),
                    new GridRegion(2535, 691, 513, 685, "0131"),
                    new GridRegion(2997, 691, 456, 685, "0132")
            );
        }
        addGridRegions(7, 2, level7Cam2Grids);


        // ------------------------------
        // 第6层 摄像头2（cameraNum=2）
        // ------------------------------
        List<GridRegion> level6Cam2Grids = GridConfigHandler.getLocalGridRegions(6, 2);
        if (level6Cam2Grids == null) {
            level6Cam2Grids = List.of(
                    new GridRegion(732, 691, 492, 685, "0112"),
                    new GridRegion(1167, 691, 519, 685, "0113"),
                    new GridRegion(1638, 691, 492, 685, "0114"),
                    new GridRegion(2082, 691, 501, 685, "0115"),
                    new GridRegion(2535, 691, 513, 685, "0116"),
                    new GridRegion(2997, 691, 456, 685, "0117")
            );
        }
        addGridRegions(6, 2, level6Cam2Grids);


        // ------------------------------
        // 第5层 摄像头2（cameraNum=2）
        // ------------------------------
        List<GridRegion> level5Cam2Grids = GridConfigHandler.getLocalGridRegions(5, 2);
        if (level5Cam2Grids == null) {
            level5Cam2Grids = List.of(
                    new GridRegion(732, 691, 492, 685, "097"),
                    new GridRegion(1167, 691, 519, 685, "098"),
                    new GridRegion(1638, 691, 492, 685, "099"),
                    new GridRegion(2082, 691, 501, 685, "0100"),
                    new GridRegion(2535, 691, 513, 685, "0101"),
                    new GridRegion(2997, 691, 456, 685, "0102")
            );
        }
        addGridRegions(5, 2, level5Cam2Grids);


        // ------------------------------
        // 第4层 摄像头2（cameraNum=2）
        // ------------------------------
        List<GridRegion> level4Cam2Grids = GridConfigHandler.getLocalGridRegions(4, 2);
        if (level4Cam2Grids == null) {
            level4Cam2Grids = List.of(
                    new GridRegion(732, 691, 492, 685, "082"),
                    new GridRegion(1167, 691, 519, 685, "083"),
                    new GridRegion(1638, 691, 492, 685, "084"),
                    new GridRegion(2082, 691, 501, 685, "085"),
                    new GridRegion(2535, 691, 513, 685, "086"),
                    new GridRegion(2997, 691, 456, 685, "087")
            );
        }
        addGridRegions(4, 2, level4Cam2Grids);


        // ------------------------------
        // 第3层 摄像头2（cameraNum=2）
        // ------------------------------
        List<GridRegion> level3Cam2Grids = GridConfigHandler.getLocalGridRegions(3, 2);
        if (level3Cam2Grids == null) {
            level3Cam2Grids = List.of(
                    new GridRegion(732, 691, 492, 685, "067"),
                    new GridRegion(1167, 691, 519, 685, "068"),
                    new GridRegion(1638, 691, 492, 685, "069"),
                    new GridRegion(2082, 691, 501, 685, "070"),
                    new GridRegion(2535, 691, 513, 685, "071"),
                    new GridRegion(2997, 691, 456, 685, "072")
            );
        }
        addGridRegions(3, 2, level3Cam2Grids);


        // ------------------------------
        // 第2层 摄像头2（cameraNum=2）
        // ------------------------------
        List<GridRegion> level2Cam2Grids = GridConfigHandler.getLocalGridRegions(2, 2);
        if (level2Cam2Grids == null) {
            level2Cam2Grids = List.of(
                    new GridRegion(729, 481, 585, 919, "051"),
                    new GridRegion(1257, 481, 598, 922, "052"),
                    new GridRegion(1806, 481, 594, 922, "053"),
                    new GridRegion(2352, 481, 597, 922, "054"),
                    new GridRegion(2895, 481, 556, 922, "055")
            );
        }
        addGridRegions(2, 2, level2Cam2Grids);


        // ------------------------------
        // 第1层 摄像头2（cameraNum=2）
        // ------------------------------
        List<GridRegion> level1Cam2Grids = GridConfigHandler.getLocalGridRegions(1, 2);
        if (level1Cam2Grids == null) {
            level1Cam2Grids = List.of(
                    new GridRegion(729, 481, 585, 919, "036"),
                    new GridRegion(1257, 481, 598, 922, "037"),
                    new GridRegion(1806, 481, 594, 922, "038"),
                    new GridRegion(2352, 481, 597, 922, "039"),
                    new GridRegion(2895, 481, 556, 922, "040")
            );
        }
        addGridRegions(1, 2, level1Cam2Grids);


        // ------------------------------
        // 第0层 摄像头2（cameraNum=2）
        // ------------------------------
        List<GridRegion> level0Cam2Grids = GridConfigHandler.getLocalGridRegions(0, 2);
        if (level0Cam2Grids == null) {
            level0Cam2Grids = List.of(
                    new GridRegion(711, 451, 799, 877, "020"),
                    new GridRegion(1383, 451, 750, 877, "021"),
                    new GridRegion(2064, 451, 750, 877, "022"),
                    new GridRegion(2733, 451, 708, 877, "023")
            );
        }
        addGridRegions(0, 2, level0Cam2Grids);


        // ------------------------------
        // 第10层 摄像头2（cameraNum=2）
        // ------------------------------
        List<GridRegion> level10Cam2Grids = GridConfigHandler.getLocalGridRegions(10, 2);
        if (level10Cam2Grids == null) {
            level10Cam2Grids = List.of(
                    new GridRegion(711, 451, 799, 877, "005"),
                    new GridRegion(1383, 451, 750, 877, "006"),
                    new GridRegion(2064, 451, 750, 877, "007"),
                    new GridRegion(2733, 451, 708, 877, "008")
            );
        }
        addGridRegions(10, 2, level10Cam2Grids);
    }

    public static HashMap<Integer, Integer> imageLevelMapLayer = new HashMap<Integer, Integer>() {{
        put(1, 10);
        put(2, 0);
        put(3, 1);
        put(4, 2);
        put(5, 3);
        put(6, 4);
        put(7, 5);
        put(8, 6);
        put(9, 7);
    }};
    public static HashMap<Integer, Integer> imageNameLevelMapLayer = new HashMap<Integer, Integer>() {{
        put(1, 0);
        put(2, 1);
        put(3, 2);
        put(4, 3);
        put(5, 4);
        put(6, 5);
        put(7, 6);
        put(8, 7);
        put(9, 10);
    }};

    public static HashMap<Integer, Integer> LevelMapLayer = new HashMap<Integer, Integer>() {{
        put(0, 10);
        put(1, 0);
        put(2, 1);
        put(3, 2);
        put(4, 3);
        put(5, 4);
        put(6, 5);
        put(7, 6);
        put(10, 7);
    }};
    // 定义在LevelMapLayer所在的类中
    public static Integer GetKeyByValue(int targetValue) {
        // 遍历HashMap的所有键值对
        for (Map.Entry<Integer, Integer> entry : LevelMapLayer.entrySet()) {
            // 找到值等于目标值的键
            if (entry.getValue() == targetValue) {
                return entry.getKey();
            }
        }
        return null; // 若没有匹配的值，返回null
    }
    /**
     * 添加某一层某一摄像头的格子区域数据
     *
     * @param level       层号
     * @param cameraNum   摄像头编号（1：左，2：右）
     * @param gridRegions 格子区域列表
     */
    public void addGridRegions(int level, int cameraNum, List<GridRegion> gridRegions) {
        String key = getKey(level, cameraNum);
        for (GridRegion gridRegion : gridRegions){
            gridRegion.setCameraNum(cameraNum);
        }
        gridRegionMap.put(key, gridRegions);
        addGridRegionsAndUpdateMap(level, cameraNum, gridRegions);
    }

    /**
     * 根据层号和摄像头编号，获取对应的格子区域列表
     *
     * @param level     层号
     * @param cameraNum 摄像头编号（1：左，2：右）
     * @return 格子区域列表（无数据则返回空列表）
     */
    public List<GridRegion> getGridRegions(int level, int cameraNum) {
        String key = getKey(level, cameraNum);
        return gridRegionMap.getOrDefault(key, new ArrayList<>());
    }

    /**
     * 生成map的key："层号_摄像头编号"
     */
    public static String getKey(int level, int cameraNum) {
        return level + "_" + cameraNum;
    }

    /**
     * 根据层号、摄像头编号和货道号（code），查找对应的GridRegion
     * @param level 层号（如7、8）
     * @param cameraNum 摄像头编号（1=左，2=右）
     * @param code 货道号（如"0121"）
     * @return 对应的GridRegion，无匹配则返回null
     */
    public GridRegion getGridRegionByCode(int level, int cameraNum, int code) {

        return laneCodeToGrid.get(code);
    }

    /**
     * 根据层号和摄像头编号，获取对应的格子区域列表
     *
     * @param level     层号（如0、1、2、7、10等）
     * @param cameraNum 摄像头编号（1：左摄像头，2：右摄像头）
     * @return 格子区域列表（无数据则返回空列表，避免空指针）
     */
    public List<GridRegion> getGridRegionsByLevel(int level, int cameraNum) {
        String key = getKey(imageLevelMapLayer.get(level), cameraNum);
        return gridRegionMap.getOrDefault(key, new ArrayList<>());
    }
}
