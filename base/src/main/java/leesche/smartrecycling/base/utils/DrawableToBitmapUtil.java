package leesche.smartrecycling.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class DrawableToBitmapUtil {
    /**
     * 将 res/drawable 资源转换为 Bitmap
     * @param context 上下文（用于获取资源）
     * @param drawableResId Drawable 资源ID（如 R.drawable.ic_logo）
     * @param width 目标 Bitmap 宽度（像素）
     * @param height 目标 Bitmap 高度（像素）
     * @return 转换后的 Bitmap（null 表示失败）
     */
    public static Bitmap drawableResToBitmap(Context context, int drawableResId, int width, int height) {
        if (context == null || drawableResId == 0 || width <= 0 || height <= 0) {
            return null;
        }

        // 1. 从资源加载 Drawable
        Drawable drawable = context.getResources().getDrawable(drawableResId, context.getTheme());
        if (drawable == null) {
            return null;
        }

        // 2. 调用通用转换方法（适配所有 Drawable 类型）
        return drawableToBitmap(drawable, width, height);
    }

    /**
     * 通用 Drawable 转 Bitmap 方法（核心）
     * @param drawable 任意类型的 Drawable
     * @param width 目标宽度
     * @param height 目标高度
     * @return 转换后的 Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        if (drawable == null || width <= 0 || height <= 0) {
            return null;
        }

        Bitmap bitmap = null;
        try {
            // 情况1：如果是 BitmapDrawable，直接提取 Bitmap（避免重复绘制，更高效）
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getBitmap() != null) {
                    // 按目标尺寸缩放（避免拉伸/模糊）
                    return Bitmap.createScaledBitmap(
                            bitmapDrawable.getBitmap(),
                            width,
                            height,
                            true // 开启抗锯齿，提升画质
                    );
                }
            }

            // 情况2：非 BitmapDrawable（如 VectorDrawable、StateListDrawable），通过 Canvas 绘制
            // 创建空白 Bitmap（ARGB_8888 格式：高质量，支持透明）
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap); // 将 Bitmap 作为 Canvas 的绘制目标

            // 设置 Drawable 的边界（必须，否则 Drawable 会绘制在 Canvas 左上角，可能只显示部分）
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas); // 将 Drawable 绘制到 Bitmap 上

        } catch (OutOfMemoryError e) {
            // 捕获内存不足异常（避免崩溃）
            e.printStackTrace();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle(); // 回收无效 Bitmap，释放内存
            }
            bitmap = null;
        }
        return bitmap;
    }
}