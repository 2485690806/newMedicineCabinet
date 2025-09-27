package leesche.smartrecycling.base.qrcode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class QrCodeScanner {
    private static final String TAG = "QrCodeScanner";

    // 从图片路径识别所有二维码（使用GenericMultipleBarcodeReader）
    public static List<String> scan(String imagePath) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap == null) {
                Log.e(TAG, "无法加载图片: " + imagePath);
                return null;
            }

            return scanMultipleQRCodes(bitmap);
        } catch (Exception e) {
            Log.e(TAG, "二维码识别失败", e);
            return null;
        }
    }

    // 从Bitmap识别多个二维码
    private static List<String> scanMultipleQRCodes(Bitmap bitmap) {
        List<String> results = new ArrayList<>();

        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            // 创建 luminance source
            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            // 配置识别参数（只识别QR码）
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.of(com.google.zxing.BarcodeFormat.QR_CODE));
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); // 开启更严格的识别模式

            // 创建单二维码识别器
            QRCodeReader qrReader = new QRCodeReader();
            // 包装为多二维码识别器
            MultipleBarcodeReader multiReader = new GenericMultipleBarcodeReader(qrReader);

            // 识别多个二维码
            Result[] rawResults = multiReader.decodeMultiple(binaryBitmap, hints);

            if (rawResults != null && rawResults.length > 0) {
                for (Result result : rawResults) {
                    String qrContent = result.getText();
                    if (qrContent != null && !qrContent.isEmpty()) {
                        results.add(qrContent);
                        Log.d(TAG, "识别到二维码: " + qrContent);
                    }
                }
            } else {
                Log.w(TAG, "未识别到任何二维码");
            }

            return results;
        } catch (NotFoundException e) {
            Log.w(TAG, "未找到二维码", e);
            return results;
        } catch (Exception e) {
            Log.e(TAG, "识别过程出错", e);
            return results;
        } finally {
            // 回收Bitmap资源
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }
}
