//package leesche.smartrecycling.base.qrcode;
//
//import android.util.Log;
//
//import com.google.zxing.LuminanceSource;
//
//public class RGBLuminanceSource extends LuminanceSource {
//    private static final String TAG = "RGBLuminanceSource";
//    private final byte[] originalLuminances; // 原始灰度数据（必须非空）
//    private final int originalWidth;         // 原始图像总宽度
//    private final int originalHeight;        // 原始图像总高度
//    private final int currentLeft;           // 当前区域左偏移（相对于原始图像）
//    private final int currentTop;            // 当前区域上偏移（相对于原始图像）
//
//    /**
//     * 原始图像构造函数（从Bitmap像素创建）
//     * @param width 图像宽度
//     * @param height 图像高度
//     * @param pixels Bitmap的像素数组（必须非空）
//     */
//    public RGBLuminanceSource(int width, int height, int[] pixels) {
//        super(width, height);
//        // 1. 校验输入参数非空
//        if (pixels == null) {
//            throw new IllegalArgumentException("pixels 不能为 null（Bitmap 可能为空或未正确获取像素）");
//        }
//        if (width <= 0 || height <= 0) {
//            throw new IllegalArgumentException("图像宽高必须大于0（width=" + width + ", height=" + height + "）");
//        }
//        // 2. 初始化原始数据字段
//        this.originalWidth = width;
//        this.originalHeight = height;
//        this.currentLeft = 0;
//        this.currentTop = 0;
//        // 3. 计算灰度数据（确保生成非空数组）
//        this.originalLuminances = calculateLuminances(pixels, width, height);
//        if (this.originalLuminances == null) {
//            throw new RuntimeException("灰度数据计算失败，originalLuminances 为 null");
//        }
//    }
//
//    /**
//     * 裁剪区域构造函数（私有，仅内部调用，复用原始灰度数据）
//     * @param cropWidth 裁剪后宽度
//     * @param cropHeight 裁剪后高度
//     * @param originalWidth 原始图像总宽度
//     * @param originalHeight 原始图像总高度
//     * @param currentLeft 裁剪区域左偏移（相对于原始图像）
//     * @param currentTop 裁剪区域上偏移（相对于原始图像）
//     * @param originalLuminances 原始灰度数据（必须非空）
//     */
//    private RGBLuminanceSource(int cropWidth, int cropHeight,
//                               int originalWidth, int originalHeight,
//                               int currentLeft, int currentTop,
//                               byte[] originalLuminances) {
//        super(cropWidth, cropHeight);
//        // 1. 校验原始灰度数据非空（裁剪的核心依赖）
//        if (originalLuminances == null) {
//            throw new IllegalArgumentException("裁剪时 originalLuminances 不能为 null（原始灰度数据丢失）");
//        }
//        // 2. 校验裁剪区域边界
//        if (currentLeft < 0 || currentTop < 0
//                || currentLeft + cropWidth > originalWidth
//                || currentTop + cropHeight > originalHeight) {
//            throw new IllegalArgumentException(
//                    "裁剪区域超出原始图像范围：" +
//                            "currentLeft=" + currentLeft + ", currentTop=" + currentTop +
//                            ", cropWidth=" + cropWidth + ", cropHeight=" + cropHeight +
//                            ", originalWidth=" + originalWidth + ", originalHeight=" + originalHeight
//            );
//        }
//        // 3. 初始化裁剪区域字段（复用原始灰度数据）
//        this.originalLuminances = originalLuminances;
//        this.originalWidth = originalWidth;
//        this.originalHeight = originalHeight;
//        this.currentLeft = currentLeft;
//        this.currentTop = currentTop;
//    }
//
//    /**
//     * 计算原始图像的灰度数据（仅在原始构造函数中调用）
//     * @return 非空的灰度数据数组
//     */
//    private byte[] calculateLuminances(int[] pixels, int width, int height) {
//        try {
//            byte[] luminances = new byte[width * height];
//            for (int y = 0; y < height; y++) {
//                int rowIndex = y * width;
//                for (int x = 0; x < width; x++) {
//                    int pixel = pixels[rowIndex + x];
//                    // 标准灰度值计算（人眼对绿色敏感，加权平均）
//                    int r = (pixel >> 16) & 0xFF;
//                    int g = (pixel >> 8) & 0xFF;
//                    int b = pixel & 0xFF;
//                    luminances[rowIndex + x] = (byte) ((r * 38 + g * 75 + b * 15) >> 7);
//                }
//            }
//            return luminances;
//        } catch (Exception e) {
//            Log.e(TAG, "计算灰度数据失败", e);
//            return null; // 异常时返回null，后续构造函数会拦截
//        }
//    }
//
//    /**
//     * 支持裁剪（必须返回true）
//     */
//    @Override
//    public boolean isCropSupported() {
//        return true;
//    }
//
//    /**
//     * 裁剪逻辑（返回新的裁剪区域实例，复用原始灰度数据）
//     */
//    @Override
//    public LuminanceSource crop(int cropLeft, int cropTop, int cropWidth, int cropHeight) {
//        // 计算裁剪区域在原始图像中的绝对偏移（当前实例可能已是裁剪区域）
//        int newLeft = this.currentLeft + cropLeft;
//        int newTop = this.currentTop + cropTop;
//        // 调用私有构造函数创建裁剪实例（自动校验参数）
//        return new RGBLuminanceSource(
//                cropWidth, cropHeight,
//                this.originalWidth, this.originalHeight,
//                newLeft, newTop,
//                this.originalLuminances // 传递原始灰度数据（确保非空）
//        );
//    }
//
//    /**
//     * 获取指定行的灰度数据（添加空值校验）
//     */
//    @Override
//    public byte[] getRow(int y, byte[] row) {
//        // 1. 校验行索引有效
//        if (y < 0 || y >= getHeight()) {
//            throw new IllegalArgumentException("行索引超出范围：y=" + y + ", 最大高度=" + getHeight());
//        }
//        // 2. 校验原始灰度数据非空
//        if (originalLuminances == null) {
//            throw new RuntimeException("getRow 失败：originalLuminances 为 null");
//        }
//        int width = getWidth();
//        // 3. 初始化行数据数组
//        if (row == null || row.length < width) {
//            row = new byte[width];
//        }
//        // 4. 计算原始数据中的起始位置并复制
//        int startIndex = (currentTop + y) * originalWidth + currentLeft;
//        System.arraycopy(originalLuminances, startIndex, row, 0, width);
//        return row;
//    }
//
//    /**
//     * 获取裁剪区域的灰度矩阵（修复空指针问题，添加校验）
//     */
//    @Override
//    public byte[] getMatrix() {
//        // 1. 校验原始灰度数据非空（核心修复点）
//        if (originalLuminances == null) {
//            throw new RuntimeException("getMatrix 失败：originalLuminances 为 null（可能是初始化或裁剪时数据丢失）");
//        }
//        int width = getWidth();
//        int height = getHeight();
//        byte[] matrix = new byte[width * height];
//        // 2. 逐行复制原始数据中的裁剪区域
//        for (int y = 0; y < height; y++) {
//            int originalRowStart = (currentTop + y) * originalWidth + currentLeft;
//            int matrixRowStart = y * width;
//            // 3. 再次校验索引范围（避免越界）
//            if (originalRowStart + width > originalLuminances.length) {
//                throw new RuntimeException(
//                        "复制数据越界：originalRowStart=" + originalRowStart +
//                                ", width=" + width + ", 原始数组长度=" + originalLuminances.length
//                );
//            }
//            System.arraycopy(originalLuminances, originalRowStart, matrix, matrixRowStart, width);
//        }
//        return matrix;
//    }
//
//    /**
//     * 辅助方法：获取原始灰度数据（用于调试）
//     */
//    public byte[] getOriginalLuminances() {
//        return originalLuminances;
//    }
//}