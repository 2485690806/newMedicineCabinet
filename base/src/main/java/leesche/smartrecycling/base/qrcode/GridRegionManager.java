package leesche.smartrecycling.base.qrcode;

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


    /**
     * 初始化所有层和摄像头的格子区域数据
     * 这里以第8层为例，实际需补充1-7层的左右摄像头数据
     */
    private void initGridRegions() {
        // 第8层左摄像头（cameraNum=1）的格子区域（用户提供的示例数据）
        List<GridRegion> level1LeftGrids = List.of(
                new GridRegion(490, 216, 364, 501, "091"),
                new GridRegion(874, 216, 364, 501, "092"),
                new GridRegion(1290, 216, 364, 501, "093"),
                new GridRegion(1714, 216, 364, 501, "094"),
                new GridRegion(2138, 216, 364, 501, "095"),
                new GridRegion(2554, 216, 364, 501, "096"),
                new GridRegion(442, 800, 373, 597, "0106"),
                new GridRegion(846, 800, 373, 597, "0107"),
                new GridRegion(1262, 800, 373, 597, "0108"),
                new GridRegion(1710, 800, 373, 597, "0109"),
                new GridRegion(2142, 800, 373, 597, "0110"),
                new GridRegion(2574, 800, 373, 597, "0111")
        );
        addGridRegions(1, 2, level1LeftGrids); // 添加8层左摄像头数据

        // 第8层右摄像头（cameraNum=2）的格子区域（示例，需替换为实际数据）
        List<GridRegion> level1RightGrids = List.of(
                new GridRegion(775, 142, 422, 541, "097"),
                new GridRegion(1213, 142, 422, 541, "098"),
                new GridRegion(1659, 142, 422, 541, "099"),
                new GridRegion(2113, 142, 422, 541, "0100"),
                new GridRegion(2573, 142, 422, 541, "0101"),
                new GridRegion(3037, 142, 422, 541, "0102"),
                new GridRegion(749, 766, 434,643, "0112"),
                new GridRegion(1215, 766, 434, 643, "0113"),
                new GridRegion(1677, 766, 434, 643, "0114"),
                new GridRegion(2129, 766, 434, 643, "0115"),
                new GridRegion(2593, 766, 434, 643, "0116"),
                new GridRegion(3049, 766, 434, 643, "0117")
        );
        addGridRegions(1, 1, level1RightGrids); // 添加8层右摄像头数据

        List<GridRegion> level2LeftGrids = List.of(
                new GridRegion(509, 168, 338, 435, "061"),
                new GridRegion(863, 168, 396, 435, "062"),
                new GridRegion(1277, 168, 338, 447, "063"),
                new GridRegion(1677, 168, 411, 437, "064"),
                new GridRegion(2111, 168, 421, 435, "065"),
                new GridRegion(2547, 168, 414, 435, "066"),
                new GridRegion(429, 724, 370,605, "076"),
                new GridRegion(813, 724, 432, 591, "077"),
                new GridRegion(1255, 724, 426, 591, "078"),
                new GridRegion(1695, 724, 415,591, "079"),
                new GridRegion(2127, 724, 431, 591, "080"),
                new GridRegion(2579, 724, 435, 591, "081")
        );
        addGridRegions(2, 2, level2LeftGrids); // 添加8层右摄像头数据

        List<GridRegion> level2RightGrids = List.of(
                new GridRegion(764, 76, 410, 479, "067"),
                new GridRegion(1200, 76, 430, 467, "068"),
                new GridRegion(1660, 76, 450, 469, "069"),
                new GridRegion(2124, 76, 429, 471, "070"),
                new GridRegion(2574, 76, 437, 469, "071"),
                new GridRegion(3030, 76, 389, 465, "072"),
                new GridRegion(746, 674, 441,633, "082"),
                new GridRegion(1210, 674, 450, 633, "083"),
                new GridRegion(1658, 674, 450, 633, "084"),
                new GridRegion(2122, 674, 450, 633, "085"),
                new GridRegion(2584, 674, 429, 633, "086"),
                new GridRegion(3044, 674, 414, 633, "087")
        );
        addGridRegions(2, 1, level2RightGrids); // 添加8层右摄像头数据


//-------- 第3层 photo_cam1_level_3_1755701591142.jpg----------------

        List<GridRegion> level3LeftGrids = List.of(
                new GridRegion(759, 439, 510, 898, "051"),
                new GridRegion(1275, 439, 546,892, "052"),
                new GridRegion(1830, 439, 546, 892, "053"),
                new GridRegion(2376, 439, 570, 892, "054"),
                new GridRegion(2952, 439, 546, 892, "055")
        );
        addGridRegions(3, 1, level3LeftGrids); // 添加8层右摄像头数据

//-------- 第3层 photo_cam2_level_3_1755701591142.jpg----------------
        List<GridRegion> level3RightGrids = List.of(
                new GridRegion(426, 496, 469,833, "046"),
                new GridRegion(891, 496, 528, 833, "047"),
                new GridRegion(1431, 496, 528, 833, "048"),
                new GridRegion(1944, 496, 528, 833, "049"),
                new GridRegion(2478, 496, 534, 833, "050")
        );
        addGridRegions(3, 2, level3RightGrids); // 添加8层右摄像头数据

//-------- 第4层 photo_cam1_level_4_1755701596526----------------

        List<GridRegion> level4LeftGrids = List.of(
                new GridRegion(759, 373, 669,925, "020"),
                new GridRegion(1431, 373, 669,925, "021"),
                new GridRegion(2121, 373, 693, 925, "022"),
                new GridRegion(2814, 373, 678, 925, "023")
        );
        addGridRegions(4, 1, level4LeftGrids); // 添加8层



//-------- 第4层 photo_cam2_level_4_1755701596526----------------
        List<GridRegion> level4RightGrids = List.of(
                new GridRegion(441, 337, 603,961, "016"),
                new GridRegion(1041, 337, 660, 833, "017"),
                new GridRegion(1689, 337, 649, 833, "018"),
                new GridRegion(2334, 337, 654, 833, "019")
        );
        addGridRegions(4, 2, level4RightGrids); // 添加8层右摄像头数据


//-------- 第6层 photo_cam1_level_6_1755701601787.jpg----------------

        List<GridRegion> level6LeftGrids = List.of(
                new GridRegion(753, 161, 681,1380, "005"),
                new GridRegion(1422, 161, 681,1380, "006"),
                new GridRegion(2112, 161, 690, 1380, "007"),
                new GridRegion(2811, 161, 693, 1380, "008")
        );
        addGridRegions(6, 1, level6LeftGrids); // 添加8层



//-------- 第6层 photo_cam2_level_6_1755701601787.jpg----------------
        List<GridRegion> level6RightGrids = List.of(
                new GridRegion(417, 145, 616,1380, "001"),
                new GridRegion(1026, 145, 667, 1380, "002"),
                new GridRegion(1689, 145, 663, 1380, "003"),
                new GridRegion(2340, 145, 684, 1380, "004")
        );
        addGridRegions(6, 2, level6RightGrids); // 添加8层右摄像头数据



        // TODO：补充1-7层的左/右摄像头格子数据
        // 例如：第1层左摄像头
        // List<GridRegion> level1LeftGrids = ...;
        // addGridRegions(1, 1, level1LeftGrids);
        // 第1层右摄像头
        // List<GridRegion> level1RightGrids = ...;
        // addGridRegions(1, 2, level1RightGrids);
    }

    /**
     * 添加某一层某一摄像头的格子区域数据
     * @param level 层号
     * @param cameraNum 摄像头编号（1：左，2：右）
     * @param gridRegions 格子区域列表
     */
    public void addGridRegions(int level, int cameraNum, List<GridRegion> gridRegions) {
        String key = getKey(level, cameraNum);
        gridRegionMap.put(key, gridRegions);
    }

    /**
     * 根据层号和摄像头编号，获取对应的格子区域列表
     * @param level 层号
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
    private String getKey(int level, int cameraNum) {
        return level + "_" + cameraNum;
    }
}
