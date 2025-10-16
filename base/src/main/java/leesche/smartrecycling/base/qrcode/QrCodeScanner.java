package leesche.smartrecycling.base.qrcode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;
import com.google.zxing.qrcode.QRCodeReader;
import com.king.wechat.qrcode.WeChatQRCodeDetector;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class QrCodeScanner {
    private static final String TAG = "QrCodeScanner";
    // 最大处理尺寸（平衡效率和识别率，720p足够）
    private static final int MAX_PROCESS_SIZE = 1280;
    // 支持的旋转角度（处理倾斜二维码）
    private static final int[] ROTATION_ANGLES = {0, 15, 30, -15};

    /**
     * 从图片路径识别所有二维码（增强版，支持多场景抗干扰）
     */
    public static List<String> scan(String imagePath) {

        Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath);
        if (originalBitmap == null) {
            Log.e(TAG, "无法加载图片: " + imagePath);
            return null;
        }

        try {
//            OpenCVQRCodeDetector openCVQRCodeDetector = new OpenCVQRCodeDetector();
//            List<String> results = new ArrayList<>();
//            openCVQRCodeDetector.detectAndDecodeMulti(originalBitmap, results);

            List<String> results = WeChatQRCodeDetector.detectAndDecode(originalBitmap);

//            List<String> results = multiStrategyScan(originalBitmap);
            Log.d(TAG, "最终识别结果数量: " + (results != null ? results.size() : 0));
            return results;
        } finally {
            // 强制回收原图，避免内存泄漏
            if (originalBitmap != null && !originalBitmap.isRecycled()) {
                originalBitmap.recycle();
            }
        }
    }
    public static List<String>  decodeImage(Bitmap originalBitmap){
        List<String> results = WeChatQRCodeDetector.detectAndDecode(originalBitmap);
        return results;
    }

    /**
     * 加载图片并按最大尺寸缩放（避免超大图片占用过多资源）
     */
    private static Bitmap loadAndResizeImage(String imagePath) {
        // 先获取图片尺寸，避免直接加载大图导致OOM
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // 计算缩放比例（确保宽高不超过MAX_PROCESS_SIZE）
        int scale = 1;
        int maxDimension = Math.max(options.outWidth, options.outHeight);
        if (maxDimension > MAX_PROCESS_SIZE) {
            scale = (int) Math.ceil((float) maxDimension / MAX_PROCESS_SIZE);
        }

        // 按比例加载图片
        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // 保留完整色彩信息用于预处理
        return BitmapFactory.decodeFile(imagePath, options);
    }

    /**
     * 多策略识别：多角度旋转 + 多二值化方法，提升抗干扰性
     */
    private static List<String> multiStrategyScan(Bitmap originalBitmap) {
        List<String> allResults = new ArrayList<>();

        // 1. 生成预处理后的图像（灰度化+降噪）
//        Bitmap preprocessedBitmap = preprocessImage(originalBitmap);

        // 2. 尝试多角度旋转识别（处理倾斜二维码）
        for (int angle : ROTATION_ANGLES) {
            Bitmap rotatedBitmap = rotateBitmap(originalBitmap, angle);
            if (rotatedBitmap == null) continue;

            // 3. 对每个角度的图像，尝试两种二值化方法（Hybrid和GlobalHistogram）
//            List<String> hybridResults = scanWithBinarizer(rotatedBitmap, new HybridBinarizer(null));
//            List<String> globalResults = scanWithBinarizer(rotatedBitmap, new GlobalHistogramBinarizer(null));
            List<String> strings = WeChatQRCodeDetector.detectAndDecode(rotatedBitmap);

            Log.d(TAG, "最终识别结果数量: " + (strings != null ? strings.size() : 0));
            // 合并结果（去重）
            addUniqueResults(allResults, strings);
//            addUniqueResults(allResults, globalResults);

            // 回收旋转后的临时Bitmap
            if (!rotatedBitmap.isRecycled()) {
                rotatedBitmap.recycle();
            }

            // 若已识别到结果，可提前退出（平衡效率和完整性）
            if (!allResults.isEmpty()) {
                Log.d(TAG, "在角度 " + angle + " 识别到结果，提前退出");
                break;
            }
        }

        // 回收预处理图像
//        if (preprocessedBitmap != null && !preprocessedBitmap.isRecycled()) {
//            preprocessedBitmap.recycle();
//        }

        return allResults;
    }

    /**
     * 图像预处理：灰度化 + 对比度增强 + 轻度降噪（核心抗干扰步骤）
     */
    private static Bitmap preprocessImage(Bitmap original) {
        if (original == null) return null;

        int width = original.getWidth();
        int height = original.getHeight();

        // 1. 灰度化（减少色彩干扰）
        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0); // 饱和度0 → 灰度图
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(original, 0, 0, paint);

        // 2. 对比度增强（突出黑白模块边界）
        Bitmap contrastBitmap = enhanceContrast(grayBitmap);

        // 3. 轻度高斯模糊降噪（去除椒盐噪点，保留边缘）
        Bitmap denoisedBitmap = gaussianBlur(contrastBitmap, 3); // 半径3的模糊，平衡降噪和细节

        // 回收中间过程的Bitmap
//        grayBitmap.recycle();
//        contrastBitmap.recycle();

        return denoisedBitmap;
    }

    /**
     * 增强图像对比度（让二维码黑白更分明）
     */
    private static Bitmap enhanceContrast(Bitmap grayBitmap) {
        int width = grayBitmap.getWidth();
        int height = grayBitmap.getHeight();
        int[] pixels = new int[width * height];
        grayBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        // 计算灰度直方图，找到合适的阈值增强对比度
        int[] histogram = new int[256];
        for (int pixel : pixels) {
            int gray = (pixel >> 16) & 0xFF; // 灰度值（0-255）
            histogram[gray]++;
        }

        // 简单对比度增强：拉伸灰度范围（将低灰度压到0，高灰度提到255）
        int minGray = 0;
        while (minGray < 255 && histogram[minGray] < 5) minGray++; // 跳过前5个低像素
        int maxGray = 255;
        while (maxGray > 0 && histogram[maxGray] < 5) maxGray--; // 跳过最后5个高像素

        for (int i = 0; i < pixels.length; i++) {
            int gray = (pixels[i] >> 16) & 0xFF;
            // 线性拉伸灰度到0-255
            int newGray = (gray - minGray) * 255 / (maxGray - minGray);
            newGray = Math.max(0, Math.min(255, newGray));
            pixels[i] = 0xFF000000 | (newGray << 16) | (newGray << 8) | newGray; // ARGB格式
        }

        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }

    /**
     * 轻度高斯模糊（降噪，保留二维码边缘）
     * 简化实现，实际可使用OpenCV的Imgproc.GaussianBlur提升效率
     */
    private static Bitmap gaussianBlur(Bitmap bitmap, int radius) {
        if (radius <= 0) return bitmap;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] blurred = new int[width * height];
        int size = radius * 2 + 1;
        int[] kernel = createGaussianKernel(size, 1.0); // 高斯核

        // 应用高斯模糊（简化版，只处理灰度值）
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int sum = 0;
                int weightSum = 0;
                for (int ky = -radius; ky <= radius; ky++) {
                    for (int kx = -radius; kx <= radius; kx++) {
                        int nx = Math.min(width - 1, Math.max(0, x + kx));
                        int ny = Math.min(height - 1, Math.max(0, y + ky));
                        int gray = (pixels[ny * width + nx] >> 16) & 0xFF;
                        int weight = kernel[(ky + radius) * size + (kx + radius)];
                        sum += gray * weight;
                        weightSum += weight;
                    }
                }
                int newGray = sum / weightSum;
                blurred[y * width + x] = 0xFF000000 | (newGray << 16) | (newGray << 8) | newGray;
            }
        }

        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.setPixels(blurred, 0, width, 0, 0, width, height);
        return result;
    }

    /**
     * 创建高斯核（用于模糊计算）
     */
    private static int[] createGaussianKernel(int size, double sigma) {
        int[] kernel = new int[size * size];
        int center = size / 2;
        double sum = 0;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int dx = x - center;
                int dy = y - center;
                double value = Math.exp(-(dx * dx + dy * dy) / (2 * sigma * sigma));
                kernel[y * size + x] = (int) (value * 100); // 放大为整数，避免浮点数计算
                sum += kernel[y * size + x];
            }
        }

        // 归一化核（确保权重和为100，简化计算）
        for (int i = 0; i < kernel.length; i++) {
            kernel[i] = (int) (kernel[i] * 100 / sum);
        }
        return kernel;
    }

    /**
     * 旋转Bitmap（处理倾斜二维码）
     */
    private static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        if (angle == 0 || bitmap == null) return bitmap;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.postRotate(angle);

        // 旋转后可能需要调整尺寸（如90度旋转后宽高互换）
        Bitmap rotated = Bitmap.createBitmap(
                bitmap, 0, 0, width, height,
                matrix, true // 过滤边缘，让旋转后的图像更清晰
        );
        return rotated;
    }

    /**
     * 使用指定的二值化器识别二维码
     */
    private static List<String> scanWithBinarizer(Bitmap bitmap, Binarizer binarizer) {
        List<String> results = new ArrayList<>();
        if (bitmap == null) return results;

        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            // 创建 luminance source
            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            // 使用指定的二值化器（Hybrid或GlobalHistogram）
            BinaryBitmap binaryBitmap = new BinaryBitmap(
                    binarizer instanceof HybridBinarizer ?
                            new HybridBinarizer(source) : new GlobalHistogramBinarizer(source)
            );

            // 优化识别参数
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.of(BarcodeFormat.QR_CODE)); // 只识别QR码
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8"); // 支持中文等字符
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); // 开启强力识别模式
            hints.put(DecodeHintType.ALLOWED_LENGTHS, new int[]{4, 8, 16, 32, 64, 128}); // 限制常见二维码长度，减少误判

            // 多二维码识别器
            QRCodeReader qrReader = new QRCodeReader();
            MultipleBarcodeReader multiReader = new GenericMultipleBarcodeReader(qrReader);

            // 执行识别
            Result[] rawResults = multiReader.decodeMultiple(binaryBitmap, hints);
            if (rawResults != null) {
                for (Result result : rawResults) {
                    String content = result.getText();
                    if (content != null && !content.isEmpty()) {
                        results.add(content);
                        Log.d(TAG, "识别到内容: " + content);
                    }
                }
            }
        } catch (NotFoundException e) {
            // 未找到二维码，属于正常情况，不打错误日志
        } catch (Exception e) {
            Log.e(TAG, "识别出错（二值化器: " + binarizer.getClass().getSimpleName() + "）", e);
        }
        return results;
    }

    /**
     * 向结果列表添加不重复的内容
     */
    private static void addUniqueResults(List<String> allResults, List<String> newResults) {
        if (newResults == null) return;
        for (String result : newResults) {
            if (!allResults.contains(result)) {
                allResults.add(result);
            }
        }
    }
}