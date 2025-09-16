package leesche.smartrecycling.base.utils;

public enum DefaultModel {
    YOLOV5S_FP("ztl_things_rec_rk3576_yolov8s_fp_2025.5.05_1952.rknn"),
    YOLOV5S_INT8("ztl_things_rec_rk3588_yolov8s_fp_2025.5.05_1952.rknn");

    public final String name;

    DefaultModel(String name) {
        this.name = name;
    }
}
