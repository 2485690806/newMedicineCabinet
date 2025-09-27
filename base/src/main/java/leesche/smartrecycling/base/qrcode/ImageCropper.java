package leesche.smartrecycling.base.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import leesche.smartrecycling.base.entity.GridRegion;


public class ImageCropper {
    private static final String TAG = "ImageCropper";
    private static final String CROPPED_DIR = "cropped_qr_images";

    // 单个格子的区域信息（坐标、尺寸、编号）


    // 裁剪并保存单个格子的图片
    public static String cropGrid(Context context, String originalPath, GridRegion region, int level) {
        try {
            Bitmap original = BitmapFactory.decodeFile(originalPath);
            if (original == null) {
                Log.e(TAG, "Failed to load original image: " + originalPath);
                return null;
            }

            // 裁剪指定区域
            Bitmap cropped = Bitmap.createBitmap(original, region.x, region.y, region.width, region.height);

            // 保存裁剪后的图片
            File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), CROPPED_DIR);
            if (!dir.exists()) dir.mkdirs();
//            "_" + System.currentTimeMillis() +
            String fileName = "cropped_level_" + level + "_grid_" + region.gridNumber +  ".jpg";
            File output = new File(dir, fileName);

            FileOutputStream fos = new FileOutputStream(output);
            cropped.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

            // 释放Bitmap内存
            original.recycle();
            cropped.recycle();

            return output.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "Crop failed", e);
            return null;
        }
    }

    // 批量裁剪一张图片中的所有格子
    public static List<String> cropAllGrids(Context context, String originalPath, List<GridRegion> regions, int level) {
        List<String> croppedPaths = new ArrayList<>();
        for (GridRegion region : regions) {
            String path = cropGrid(context, originalPath, region, level);
            if (path != null) croppedPaths.add(path);
        }
        return croppedPaths;
    }
}