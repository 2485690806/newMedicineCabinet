package leesche.smartrecycling.base.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.Base64;

public class BitmapUtil {
    // 水平翻转图像，也就是把镜中像左右翻过来
    public static Bitmap getHorizontalFlipBitmap(Bitmap bitmap) {
        try {
            Matrix matrix = new Matrix(); // 创建操作图片用的矩阵对象
            matrix.postScale(-1, 1); // 执行图片的旋转动作
            // 创建并返回旋转后的位图对象
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, false);
        } finally {
//            bitmap.recycle();
        }


    }

    // 垂直翻转图像，也就是把镜中像上下翻过来
    public static Bitmap getVerticalFlipBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix(); // 创建操作图片用的矩阵对象
        matrix.postScale(1, -1); // 执行图片的旋转动作
        // 创建并返回旋转后的位图对象
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);
    }

    // 获得比例缩放之后的位图对象
    public static Bitmap getScaleBitmap(Bitmap bitmap, double scaleRatio) {
        Matrix matrix = new Matrix(); // 创建操作图片用的矩阵对象
        matrix.postScale((float) scaleRatio, (float) scaleRatio);
        // 创建并返回缩放后的位图对象
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);
    }


    // 获得旋转角度之后的位图对象
    public static Bitmap getRotateBitmap(Bitmap bitmap, float rotateDegree) {
        Matrix matrix = new Matrix(); // 创建操作图片用的矩阵对象
        matrix.postRotate(rotateDegree); // 执行图片的旋转动作
        // 创建并返回旋转后的位图对象
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }

    //8宫格裁剪一半
    public static Bitmap cutHalf(Bitmap bitmap) {
        Bitmap half = Bitmap.createBitmap(bitmap.getWidth() / 2, bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(half);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return half;
    }

    public static Bitmap base64ToBitmap(String base64String){
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    public static Bitmap cutToShow(Bitmap bitmap, float cutW, float cutH) {
        float w = bitmap.getWidth();
        float h = bitmap.getHeight();
        float i = cutH / cutW;
        Bitmap sizePhoto;
        if (i < (h / w)) {
            sizePhoto = Bitmap.createBitmap((int) w, (int) (w * i), Bitmap.Config.ARGB_8888);
            Canvas canvasshiji = new Canvas(sizePhoto);
            canvasshiji.drawBitmap(bitmap, 0, -(h - (w * i)) / 2, null);
        } else {
            sizePhoto = Bitmap.createBitmap((int) (h / i), (int) h, Bitmap.Config.ARGB_8888);
            Canvas canvasshiji = new Canvas(sizePhoto);
            canvasshiji.drawBitmap(bitmap, -(w - (h / i)) / 2, 0, null);
        }
//        bitmap.recycle();
        return sizePhoto;
    }

    /**
     * 设置 Bitmap 的左上角和左下角为圆角
     *
     * @param bitmap 需要处理的 Bitmap
     * @param radius 圆角半径
     * @return 带圆角的 Bitmap
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float radius) {
        // 创建一个空的 Bitmap，大小与原 Bitmap 相同
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        // 初始化 Canvas 并将它设置为输出 Bitmap
        Canvas canvas = new Canvas(output);

        // 初始化 Paint，并设置抗锯齿
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xFF000000);  // 设置 Paint 的颜色为黑色

        // 创建一个 Path 用于定义左上和左下的圆角矩形区域
        Path path = new Path();
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());

        // 添加左上角和左下角的圆角路径
        path.moveTo(0, radius); // 移动到左侧中间位置
        path.arcTo(new RectF(0, 0, 2 * radius, 2 * radius), 180, 90); // 左上角圆角
        path.lineTo(rect.right, 0); // 上边界的直线
        path.lineTo(rect.right, rect.bottom); // 右边界的直线
        path.lineTo(radius, rect.bottom); // 底边界的直线
        path.arcTo(new RectF(0, rect.bottom - 2 * radius, 2 * radius, rect.bottom), 90, 90); // 左下角圆角
        path.close(); // 关闭路径

        // 使用 Paint 和 Path 绘制圆角区域
        canvas.drawPath(path, paint);

        // 设置 Paint 模式为 SRC_IN，确保绘制的 Bitmap 覆盖在圆角区域
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // 绘制原始 Bitmap 到 Canvas
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }

}
