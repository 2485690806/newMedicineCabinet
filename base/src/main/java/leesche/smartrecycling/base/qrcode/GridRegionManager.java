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
        List<GridRegion> level7Cam1Grids = List.of(

                new GridRegion(297, 682, 408, 646,  "0121"),
                new GridRegion(678, 682, 474, 646,"0122"),
                new GridRegion(1116, 682, 489, 646,"0123"),
                new GridRegion(1572, 682, 498, 646,  "0124"),
                new GridRegion(2031, 682, 487, 646,"0125"),
                new GridRegion(2481, 682, 498, 646,"0126")
        );
        addGridRegions(7, 1, level7Cam1Grids); // 添加8层右摄像头数据


        List<GridRegion> level6Cam1Grids = List.of(
                new GridRegion(297, 682, 408, 646, "0106"),
                new GridRegion(678, 682, 474, 646, "0107"),
                new GridRegion(1116, 682, 489, 646, "0108"),
                new GridRegion(1572, 682, 498, 646, "0109"),
                new GridRegion(2031, 682, 487, 646, "0110"),
                new GridRegion(2481, 682, 498, 646, "0111")
        );
        addGridRegions(6, 1, level6Cam1Grids); // 添加8层右摄像头数据


        List<GridRegion> level5Cam1Grids = List.of(

                new GridRegion(297, 682, 408, 646, "091"),
                new GridRegion(678, 682, 474, 646,"092"),
                new GridRegion(1116, 682, 489, 646,"093"),
                new GridRegion(1572, 682, 498, 646, "094"),
                new GridRegion(2031, 682, 487, 646, "095"),
                new GridRegion(2481, 682, 498, 646,"096")
        );
        addGridRegions(5, 1, level5Cam1Grids); // 添加8层右摄像头数据


        List<GridRegion> level4Cam1Grids = List.of(

                new GridRegion(297, 682, 408, 646,"076"),
                new GridRegion(678, 682, 474, 646, "077"),
                new GridRegion(1116, 682, 489, 646,"078"),
                new GridRegion(1572, 682, 498, 646,  "079"),
                new GridRegion(2031, 682, 487, 646, "080"),
                new GridRegion(2481, 682, 498, 646, "081")
        );
        addGridRegions(4, 1, level4Cam1Grids); // 添加8层


        List<GridRegion> level3Cam1Grids = List.of(

//                new GridRegion(333, 64, 474, 601, "046"),
//                new GridRegion(777, 64, 543, 601, "047"),
//                new GridRegion(1296, 64, 567, 601, "048"),
//                new GridRegion(1824, 64, 576, 601, "049"),
//                new GridRegion(2364, 64, 570, 601, "050"),

                new GridRegion(297, 682, 408, 646,"061"),
                new GridRegion(678, 682, 474, 646, "062"),
                new GridRegion(1116, 682, 489, 646,"063"),
                new GridRegion(1572, 682, 498, 646, "064"),
                new GridRegion(2031, 682, 487, 646, "065"),
                new GridRegion(2481, 682, 498, 646,  "066")
        );
        addGridRegions(3, 1, level3Cam1Grids); // 添加8层右摄像头数据


        List<GridRegion> level2Cam1Grids = List.of(
                new GridRegion(234, 535, 573, 769, "046"),
                new GridRegion(726, 535, 618, 769, "047"),
                new GridRegion(1296, 535, 580, 769, "048"),
                new GridRegion(1842, 535, 600, 769, "049"),
                new GridRegion(2376, 535, 582, 769,"050")
        );
        addGridRegions(2, 1, level2Cam1Grids); // 添加8层


        List<GridRegion> level1Cam1Grids = List.of(
                new GridRegion(234, 535, 573, 769, "031"),
                new GridRegion(726, 535, 618, 769, "032"),
                new GridRegion(1296, 535, 580, 769, "033"),
                new GridRegion(1842, 535, 600, 769, "034"),
                new GridRegion(2376, 535, 582, 769, "035")
        );
        addGridRegions(1, 1, level1Cam1Grids); // 添加8层


        List<GridRegion> level0Cam1Grids = List.of(
                new GridRegion(288, 445, 624, 865, "016"),
                new GridRegion(879, 445, 726, 865, "017"),
                new GridRegion(1572, 445, 726, 865, "018"),
                new GridRegion(2271, 445, 714, 865, "019")
        );
        addGridRegions(0, 1, level0Cam1Grids); // 添加8层右摄像头数据


        List<GridRegion> level10Cam1Grids = List.of(
                new GridRegion(288, 445, 624, 865, "001"),
                new GridRegion(879, 445, 726, 865,  "002"),
                new GridRegion(1572, 445, 726, 865, "003"),
                new GridRegion(2271, 445, 714, 865,  "004")
        );
        addGridRegions(10, 1, level10Cam1Grids); // 添加8层右摄像头数据


        List<GridRegion> level7Cam2Grids = List.of(

                new GridRegion(732, 691, 492, 685, "0127"),
                new GridRegion(1167, 691, 519, 685, "0128"),
                new GridRegion(1638, 691, 492, 685, "0129"),
                new GridRegion(2082, 691, 501, 685, "0130"),
                new GridRegion(2535, 691, 513, 685, "0131"),
                new GridRegion(2997, 691, 456, 685, "0132")
        );
        addGridRegions(7, 2, level7Cam2Grids); // 添加8层左摄像头数据


        List<GridRegion> level6Cam2Grids = List.of(

                new GridRegion(732, 691, 492, 685, "0112"),
                new GridRegion(1167, 691, 519, 685,  "0113"),
                new GridRegion(1638, 691, 492, 685,  "0114"),
                new GridRegion(2082, 691, 501, 685,"0115"),
                new GridRegion(2535, 691, 513, 685,  "0116"),
                new GridRegion(2997, 691, 456, 685,  "0117")
        );
        addGridRegions(6, 2, level6Cam2Grids); // 添加8层右摄像头数据


        List<GridRegion> level5Cam2Grids = List.of(

                new GridRegion(732, 691, 492, 685, "097"),
                new GridRegion(1167, 691, 519, 685,  "098"),
                new GridRegion(1638, 691, 492, 685, "099"),
                new GridRegion(2082, 691, 501, 685,"0100"),
                new GridRegion(2535, 691, 513, 685, "0101"),
                new GridRegion(2997, 691, 456, 685, "0102")

        );
        addGridRegions(5, 2, level5Cam2Grids); // 添加8层右摄像头数据


        List<GridRegion> level4Cam2Grids = List.of(



                new GridRegion(732, 691, 492, 685, "082"),
                new GridRegion(1167, 691, 519, 685,  "083"),
                new GridRegion(1638, 691, 492, 685,"084"),
                new GridRegion(2082, 691, 501, 685,"085"),
                new GridRegion(2535, 691, 513, 685,  "086"),
                new GridRegion(2997, 691, 456, 685, "087")

                );
        addGridRegions(4, 2, level4Cam2Grids); // 添加8层右摄像头数据


        List<GridRegion> level3Cam2Grids = List.of(


                new GridRegion(732, 691, 492, 685,  "067"),
                new GridRegion(1167, 691, 519, 685, "068"),
                new GridRegion(1638, 691, 492, 685, "069"),
                new GridRegion(2082, 691, 501, 685, "070"),
                new GridRegion(2535, 691, 513, 685,"071"),
                new GridRegion(2997, 691, 456, 685,  "072")


                );
        addGridRegions(3, 2, level3Cam2Grids); // 添加8层右摄像头数据


        List<GridRegion> level2Cam2Grids = List.of(

                new GridRegion(729, 481, 585, 919,"051"),
                new GridRegion(1257, 481, 598, 922,"052"),
                new GridRegion(1806, 481, 594, 922,"053"),
                new GridRegion(2352, 481, 597, 922,"054"),
                new GridRegion(2895, 481, 556, 922, "055")

                );
        addGridRegions(2, 2, level2Cam2Grids); // 添加8层右摄像头数据


        List<GridRegion> level1Cam2Grids = List.of(

                new GridRegion(729, 481, 585, 919,"036"),
                new GridRegion(1257, 481, 598, 922,"037"),
                new GridRegion(1806, 481, 594, 922, "038"),
                new GridRegion(2352, 481, 597, 922, "039"),
                new GridRegion(2895, 481, 556, 922, "040")

                );
        addGridRegions(1, 2, level1Cam2Grids); // 添加8层右摄像头数据


        List<GridRegion> level0Cam2Grids = List.of(

                new GridRegion(711, 451, 799, 877, "020"),
                new GridRegion(1383, 451, 750, 877, "021"),
                new GridRegion(2064, 451, 750, 877, "022"),
                new GridRegion(2733, 451, 708, 877, "023")

                );
        addGridRegions(0, 2, level0Cam2Grids); // 添加8层右摄像头数据


        List<GridRegion> level10Cam2Grids = List.of(

                new GridRegion(711, 451, 799, 877, "005"),
                new GridRegion(1383, 451, 750, 877, "006"),
                new GridRegion(2064, 451, 750, 877,  "007"),
                new GridRegion(2733, 451, 708, 877, "008")

                );
        addGridRegions(10, 2, level10Cam2Grids); // 添加8层右摄像头数据


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
