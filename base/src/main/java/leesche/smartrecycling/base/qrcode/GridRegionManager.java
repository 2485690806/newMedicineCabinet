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

        // 第8层右摄像头（cameraNum=2）的格子区域（示例，需替换为实际数据）
        List<GridRegion> level9Cam1Grids = List.of(
                new GridRegion(348, 112, 391, 553, "0106"),
                new GridRegion(720, 112, 450, 553, "0107"),
                new GridRegion(1155, 112, 453, 553, "0108"),
                new GridRegion(1593, 112, 483, 553, "0109"),
                new GridRegion(2046, 112, 483, 553, "0110"),
                new GridRegion(2490, 112, 450, 553, "0111"),
                new GridRegion(318, 658, 390, 685, "0121"),
                new GridRegion(696, 658, 451, 685, "0122"),
                new GridRegion(1134, 658, 474, 685, "0123"),
                new GridRegion(1590, 658, 489, 685, "0124"),
                new GridRegion(2043, 658, 498, 685, "0125"),
                new GridRegion(2499, 658, 471, 685, "0126")
        );
        addGridRegions(7, 1, level9Cam1Grids); // 添加8层右摄像头数据


        List<GridRegion> level8Cam1Grids = List.of(
                new GridRegion(336, 124, 396, 565, "091"),
                new GridRegion(699, 124, 453, 565, "092"),
                new GridRegion(1119, 124, 495, 565, "093"),
                new GridRegion(1566, 124, 477, 565, "094"),
                new GridRegion(2013, 124, 489, 565, "095"),
                new GridRegion(2454, 124, 489, 565, "096"),

                new GridRegion(297, 682, 408, 646, "0106"),
                new GridRegion(678, 682, 474, 646, "0107"),
                new GridRegion(1116, 682, 489, 646, "0108"),
                new GridRegion(1572, 682, 498, 646, "0109"),
                new GridRegion(2031, 682, 487, 646, "0110"),
                new GridRegion(2481, 682, 498, 646, "0111")
        );
        addGridRegions(6, 1, level8Cam1Grids); // 添加8层右摄像头数据


        List<GridRegion> level7Cam1Grids = List.of(
                new GridRegion(336, 136, 382, 568, "076"),
                new GridRegion(696, 136, 453, 568, "077"),
                new GridRegion(1122, 136, 474, 568, "078"),
                new GridRegion(1560, 136, 483, 568, "079"),
                new GridRegion(2013, 136, 490, 568, "080"),
                new GridRegion(2463, 136, 483, 568, "081"),

                new GridRegion(306, 679, 426, 685, "091"),
                new GridRegion(681, 679, 468, 685, "092"),
                new GridRegion(1116, 679, 495, 685, "093"),
                new GridRegion(1575, 679, 486, 685, "094"),
                new GridRegion(2025, 679, 483, 685, "095"),
                new GridRegion(2478, 679, 486, 685, "096")
        );
        addGridRegions(5, 1, level7Cam1Grids); // 添加8层右摄像头数据


        List<GridRegion> level6Cam1Grids = List.of(
                new GridRegion(339, 145, 402, 556, "061"),
                new GridRegion(696, 145, 456, 556, "062"),
                new GridRegion(1122, 145, 474, 556, "063"),
                new GridRegion(1554, 145, 483, 556, "064"),
                new GridRegion(2013, 145, 490, 556, "065"),
                new GridRegion(2463, 145, 483, 556, "066"),

                new GridRegion(309, 679, 393, 664, "076"),
                new GridRegion(681, 679, 474, 664, "077"),
                new GridRegion(1116, 679, 486, 664, "078"),
                new GridRegion(1575, 679, 480, 664, "079"),
                new GridRegion(2025, 679, 495, 664, "080"),
                new GridRegion(2478, 679, 501, 664, "081")
        );
        addGridRegions(4, 1, level6Cam1Grids); // 添加8层


        List<GridRegion> level5Cam1Grids = List.of(

                new GridRegion(333, 64, 474, 601, "046"),
                new GridRegion(777, 64, 543, 601, "047"),
                new GridRegion(1296, 64, 567, 601, "048"),
                new GridRegion(1824, 64, 576, 601, "049"),
                new GridRegion(2364, 64, 570, 601, "050"),

                new GridRegion(297, 145, 421, 574, "061"),
                new GridRegion(678, 145, 474, 574, "062"),
                new GridRegion(1119, 145, 489, 574, "063"),
                new GridRegion(1563, 145, 501, 574, "064"),
                new GridRegion(2028, 145, 483, 574, "065"),
                new GridRegion(2472, 145, 495, 574, "066")
        );
        addGridRegions(3, 1, level5Cam1Grids); // 添加8层右摄像头数据


        List<GridRegion> level4Cam1Grids = List.of(
                new GridRegion(303, 535, 487, 769, "046"),
                new GridRegion(759, 535, 564, 769, "047"),
                new GridRegion(1296, 535, 580, 769, "048"),
                new GridRegion(1833, 535, 577, 769, "049"),
                new GridRegion(2376, 535, 582, 769, "050")
        );
        addGridRegions(2, 1, level4Cam1Grids); // 添加8层


        List<GridRegion> level3Cam1Grids = List.of(
                new GridRegion(303, 535, 487, 769, "031"),
                new GridRegion(759, 535, 564, 769, "032"),
                new GridRegion(1296, 535, 580, 769, "033"),
                new GridRegion(1833, 535, 577, 769, "034"),
                new GridRegion(2376, 535, 582, 769, "035")
        );
        addGridRegions(1, 1, level3Cam1Grids); // 添加8层


        List<GridRegion> level2Cam1Grids = List.of(
                new GridRegion(288, 445, 624, 865, "016"),
                new GridRegion(879, 445, 726, 865, "017"),
                new GridRegion(1572, 445, 726, 865, "018"),
                new GridRegion(2271, 445, 714, 865, "019")
        );
        addGridRegions(0, 1, level2Cam1Grids); // 添加8层右摄像头数据


        List<GridRegion> level1Cam1Grids = List.of(
                new GridRegion(294, 409, 628, 901, "001"),
                new GridRegion(888, 409, 717, 901, "002"),
                new GridRegion(1566, 409, 720, 901, "003"),
                new GridRegion(2247, 409, 720, 901, "004")
        );
        addGridRegions(10, 1, level1Cam1Grids); // 添加8层右摄像头数据


        List<GridRegion> level9Cam2Grids = List.of(
                new GridRegion(693, 157, 447, 553, "0112"),
                new GridRegion(1119, 157, 477, 553, "0113"),
                new GridRegion(1569, 157, 477, 553, "0114"),
                new GridRegion(2016, 157, 477, 553, "0115"),
                new GridRegion(2460, 157, 459, 553, "0116"),
                new GridRegion(2874, 157, 426, 553, "0117"),

                new GridRegion(657, 734, 468, 630, "0127"),
                new GridRegion(1095, 734, 486, 630, "0128"),
                new GridRegion(1560, 734, 486, 630, "0129"),
                new GridRegion(2019, 734, 480, 630, "0130"),
                new GridRegion(2460, 734, 477, 630, "0131"),
                new GridRegion(2907, 734, 435, 630, "0132")
        );
        addGridRegions(7, 2, level9Cam2Grids); // 添加8层左摄像头数据


        List<GridRegion> level8Cam2Grids = List.of(
                new GridRegion(690, 196, 450, 541, "097"),
                new GridRegion(1101, 196, 474, 541, "098"),
                new GridRegion(1551, 196, 469, 541, "099"),
                new GridRegion(1983, 196, 472, 541, "0100"),
                new GridRegion(2424, 196, 450, 541, "0101"),
                new GridRegion(2832, 196, 438, 541, "0102"),

                new GridRegion(642, 733, 462, 634, "0112"),
                new GridRegion(1083, 733, 489, 634, "0113"),
                new GridRegion(1545, 733, 475, 634, "0114"),
                new GridRegion(1986, 733, 484, 634, "0115"),
                new GridRegion(2430, 733, 468, 634, "0116"),
                new GridRegion(2847, 733, 459, 634, "0117")
        );
        addGridRegions(6, 2, level8Cam2Grids); // 添加8层右摄像头数据


        List<GridRegion> level7Cam2Grids = List.of(
                new GridRegion(642, 157, 462, 634, "082"),
                new GridRegion(1083, 157, 489, 634, "083"),
                new GridRegion(1545, 157, 475, 634, "084"),
                new GridRegion(1986, 157, 484, 634, "085"),
                new GridRegion(2430, 157, 468, 634, "086"),
                new GridRegion(2847, 157, 459, 634, "087"),

                new GridRegion(690, 721, 450, 541, "097"),
                new GridRegion(1101, 721, 474, 541, "098"),
                new GridRegion(1551, 721, 469, 541, "099"),
                new GridRegion(1983, 721, 472, 541, "0100"),
                new GridRegion(2424, 721, 450, 541, "0101"),
                new GridRegion(2832, 721, 438, 541, "0102")

        );
        addGridRegions(5, 2, level7Cam2Grids); // 添加8层右摄像头数据


        List<GridRegion> level6Cam2Grids = List.of(

                new GridRegion(687, 145, 447, 565, "067"),
                new GridRegion(1107, 145, 463, 565, "068"),
                new GridRegion(1548, 145, 465, 565, "069"),
                new GridRegion(1992, 145, 463, 565, "070"),
                new GridRegion(2427, 145, 444, 565, "071"),
                new GridRegion(2829, 145, 429, 565, "072"),

                new GridRegion(654, 706, 462, 670, "082"),
                new GridRegion(1083, 706, 492, 670, "083"),
                new GridRegion(1539, 706, 477, 670, "084"),
                new GridRegion(1980, 706, 486, 670, "085"),
                new GridRegion(2430, 706, 468, 670, "086"),
                new GridRegion(2847, 706, 459, 670, "087")

                );
        addGridRegions(4, 2, level6Cam2Grids); // 添加8层右摄像头数据


        List<GridRegion> level5Cam2Grids = List.of(

                new GridRegion(687, 46, 531, 655, "051"),
                new GridRegion(1182, 46, 558, 655, "052"),
                new GridRegion(1713, 46, 561, 655, "053"),
                new GridRegion(2217, 46, 564, 655, "054"),
                new GridRegion(2724, 46, 537, 655, "055"),

                new GridRegion(657, 700, 462, 682, "067"),
                new GridRegion(1083, 700, 495, 682, "068"),
                new GridRegion(1542, 700, 483, 682, "069"),
                new GridRegion(1986, 700, 474, 682, "070"),
                new GridRegion(2421, 700, 471, 682, "071"),
                new GridRegion(2853, 700, 444, 682, "072")


                );
        addGridRegions(3, 2, level5Cam2Grids); // 添加8层右摄像头数据


        List<GridRegion> level4Cam2Grids = List.of(

                new GridRegion(648, 529, 555, 877, "051"),
                new GridRegion(1164, 529, 591, 877, "052"),
                new GridRegion(1719, 529, 549, 877, "053"),
                new GridRegion(2223, 529, 570, 877, "054"),
                new GridRegion(2739, 529, 558, 877, "055")

                );
        addGridRegions(2, 2, level4Cam2Grids); // 添加8层右摄像头数据


        List<GridRegion> level3Cam2Grids = List.of(

                new GridRegion(654, 514, 546, 877, "036"),
                new GridRegion(1164, 514, 591, 877, "037"),
                new GridRegion(1719, 514, 549, 877, "038"),
                new GridRegion(2223, 514, 570, 877, "039"),
                new GridRegion(2739, 514, 558, 877, "040")

                );
        addGridRegions(1, 2, level3Cam2Grids); // 添加8层右摄像头数据


        List<GridRegion> level2Cam2Grids = List.of(

                new GridRegion(630, 451, 726, 946, "020"),
                new GridRegion(1311, 451, 711, 877, "021"),
                new GridRegion(1983, 451, 708, 877, "022"),
                new GridRegion(2637, 451, 696, 877, "023")

                );
        addGridRegions(0, 2, level2Cam2Grids); // 添加8层右摄像头数据


        List<GridRegion> level1Cam2Grids = List.of(

                new GridRegion(651, 484, 708, 910, "005"),
                new GridRegion(1314, 484, 702, 910, "006"),
                new GridRegion(1977, 484, 702, 910, "007"),
                new GridRegion(2619, 484, 681, 910, "008")

                );
        addGridRegions(10, 2, level1Cam2Grids); // 添加8层右摄像头数据


//        // 第8层右摄像头（cameraNum=2）的格子区域（示例，需替换为实际数据）
//        List<GridRegion> level1RightGrids = List.of(
//                new GridRegion(775, 142, 422, 541, "097"),
//                new GridRegion(1213, 142, 422, 541, "098"),
//                new GridRegion(1659, 142, 422, 541, "099"),
//                new GridRegion(2113, 142, 422, 541, "0100"),
//                new GridRegion(2573, 142, 422, 541, "0101"),
//                new GridRegion(3037, 142, 422, 541, "0102"),
//                new GridRegion(749, 766, 434,643, "0112"),
//                new GridRegion(1215, 766, 434, 643, "0113"),
//                new GridRegion(1677, 766, 434, 643, "0114"),
//                new GridRegion(2129, 766, 434, 643, "0115"),
//                new GridRegion(2593, 766, 434, 643, "0116"),
//                new GridRegion(3049, 766, 434, 643, "0117")
//        );
//        addGridRegions(1, 1, level1RightGrids); // 添加8层右摄像头数据


//-------- 第3层 photo_cam1_level_3_1755701591142.jpg----------------


//-------- 第3层 photo_cam2_level_3_1755701591142.jpg----------------
        List<GridRegion> level3RightGrids = List.of(
                new GridRegion(426, 496, 469, 833, "046"),
                new GridRegion(891, 496, 528, 833, "047"),
                new GridRegion(1431, 496, 528, 833, "048"),
                new GridRegion(1944, 496, 528, 833, "049"),
                new GridRegion(2478, 496, 534, 833, "050")
        );
        addGridRegions(3, 2, level3RightGrids); // 添加8层右摄像头数据

//-------- 第4层 photo_cam1_level_4_1755701596526----------------


//-------- 第4层 photo_cam2_level_4_1755701596526----------------


        List<GridRegion> level5RightGrids = List.of(
                new GridRegion(723, 674, 441, 633, "067"),
                new GridRegion(1167, 674, 450, 633, "068"),
                new GridRegion(1629, 674, 465, 633, "069"),
                new GridRegion(2088, 674, 463, 633, "070"),
                new GridRegion(2541, 674, 468, 633, "071"),
                new GridRegion(2994, 674, 414, 633, "072"),

                new GridRegion(750, 94, 504, 643, "051"),
                new GridRegion(1182, 94, 465, 544, "052"),
                new GridRegion(1626, 94, 480, 544, "053"),
                new GridRegion(2094, 94, 451, 544, "054"),
                new GridRegion(2547, 94, 441, 544, "055")
        );
        addGridRegions(5, 2, level5RightGrids); // 添加8层右摄像头数据


//-------- 第6层 photo_cam1_level_6_1755701601787.jpg----------------


//-------- 第6层 photo_cam2_level_6_1755701601787.jpg----------------

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
     *
     * @param level       层号
     * @param cameraNum   摄像头编号（1：左，2：右）
     * @param gridRegions 格子区域列表
     */
    public void addGridRegions(int level, int cameraNum, List<GridRegion> gridRegions) {
        String key = getKey(level, cameraNum);
        gridRegionMap.put(key, gridRegions);
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
}
