/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package leesche.smartrecycling.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import leesche.smartrecycling.base.R;

public class ImageUtil {

    public static void resize(Bitmap bitmap, File outputFile, int maxWidth, int maxHeight) {
        try {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            // 图片大于最大高宽，按大的值缩放
            if (bitmapWidth > maxHeight || bitmapHeight > maxWidth) {
                float widthScale = maxWidth * 1.0f / bitmapWidth;
                float heightScale = maxHeight * 1.0f / bitmapHeight;

                float scale = Math.min(widthScale, heightScale);
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
            }
            LogUtil.i("APIService", "upload face size" + bitmap.getWidth() + "*" + bitmap.getHeight());
            // save image
            FileOutputStream out = new FileOutputStream(outputFile);
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resize(String inputPath, String outputPath, int dstWidth, int dstHeight) {
        try {
            int inWidth;
            int inHeight;

            // decode image size (decode metadata only, not the whole image)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(inputPath, options);

            // save width and height
            inWidth = options.outWidth;
            inHeight = options.outHeight;
            LogUtil.i("APIService", "origin " + inWidth + " " + inHeight);

            Matrix m = new Matrix();
            ExifInterface exif = new ExifInterface(inputPath);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            if (rotation != 0) {
                m.preRotate(ExifUtil.exifToDegrees(rotation));
            }

            int maxPreviewImageSize = Math.max(dstWidth, dstHeight);
            int size = Math.min(options.outWidth, options.outHeight);
            size = Math.min(size, maxPreviewImageSize);

            options = new BitmapFactory.Options();
            options.inSampleSize = ImageUtil.calculateInSampleSize(options, size, size);
            options.inScaled = true;
            options.inDensity = options.outWidth;
            options.inTargetDensity = size * options.inSampleSize;

            Bitmap roughBitmap = BitmapFactory.decodeFile(inputPath, options);
            roughBitmap = Bitmap.createBitmap(roughBitmap, 0, 0, roughBitmap.getWidth(),
                    roughBitmap.getHeight(), m, false);
            // save image
            FileOutputStream out = new FileOutputStream(outputPath);
            try {
                roughBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void saveBitmap(String outputPath, Bitmap bitmap) {
        // save image
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param context
     * @param bitmap
     * @return
     */
    public static Bitmap loadZoomBitmap(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int bmpHeight = bitmap.getHeight();
        int bmpWidth = bitmap.getWidth();
        int newHeight = 0;
        int zoom = 1;
        if (bmpWidth > 200) {
            zoom = bmpWidth / 200;
            newHeight = bmpHeight / zoom;
        } else {
            zoom = 1;
            newHeight = bmpHeight;
        }
        if (zoom <= 1) {
            return bitmap;
        }
        Bitmap bm = Bitmap.createScaledBitmap(bitmap, 200, newHeight, true);
        if (bitmap != null) {
            bitmap.recycle();
        }
        return bm;
    }

    public static Bitmap base642bitmap(String image) {
        if (TextUtils.isEmpty(image)) {
            return null;
        }
        byte[] bytes = Base64.decode(image.getBytes(), Base64.DEFAULT);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        return bitmap;
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left;
        float top;
        float right;
        float bottom;
        float dstLeft;
        float dstTop;
        float dstRight;
        float dstBottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dstLeft = 0;
            dstTop = 0;
            dstRight = width;
            dstBottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dstLeft = 0;
            dstTop = 0;
            dstRight = height;
            dstBottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width,
                height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dstLeft, (int) dstTop, (int) dstRight, (int) dstBottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);


        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }


//    public static Bitmap getBase64Image(HashMap<String, String> imageMap) {
//
//        Set<Map.Entry<String, String>> sets = imageMap.entrySet();
//        Bitmap bmp = null;
//        for (Map.Entry<String, String> entry : sets) {
//            bmp = base64ToBitmap(entry.getValue());
//        }
//        return bmp;
//    }

    public static Bitmap cropFaceImg(Bitmap bitmap, int needW) {
        Bitmap face = null;
        int x = bitmap.getWidth();
        int y = bitmap.getHeight();
        int left = (x - needW) / 2;
        int top = (y - needW) / 2;
        face = Bitmap.createBitmap(bitmap, left, top, needW, needW);

        return face;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xffF38F18;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap rawByteArray2RGBABitmap2(Bitmap bitmap, byte[] data, int width, int height) {


//        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, outputStream);
//        byte[] jpegData = outputStream.toByteArray();
//
//
//        Bitmap originalBitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
//        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
//
//        return resizedBitmap;

        int frameSize = width * height;
        int[] rgba = new int[frameSize];
        for (int h = 0; h < height; h++)
            for (int w = 0; w < width; w++) {
                int y = (0xff & ((int) data[h * width + w]));
                int u = (0xff & ((int) data[frameSize + (h >> 1) * width + (w & ~1) + 0]));
                int v = (0xff & ((int) data[frameSize + (h >> 1) * width + (w & ~1) + 1]));
                y = y < 16 ? 16 : y;

                int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));
                int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                rgba[h * width + w] = 0xff000000 + (r << 16) + (g << 8) + b;
            }

        bitmap.setPixels(rgba, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {

        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;

        } catch (OutOfMemoryError ex) {
        }
        return null;
    }

    public static void setTextViewDrawable(Context context, int resID, int position, TextView tv) {
        Drawable drawable = null;
        if (resID != -1) {
            drawable = context.getResources().getDrawable(resID);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        switch (position) {
            case 0:
                tv.setCompoundDrawables(drawable, null, null, null);
                break;
            case 1:
                tv.setCompoundDrawables(null, drawable, null, null);
                break;
            case 2:
                tv.setCompoundDrawables(null, null, drawable, null);
                break;
            case 3:
                tv.setCompoundDrawables(null, null, null, drawable);
                break;
            default:
                tv.setCompoundDrawables(drawable, null, null, null);
                break;
        }
    }

    public static void setTextViewDrawable2(Context context, int leftResId, int resID, int position, TextView tv) {
        Drawable drawable = context.getResources().getDrawable(resID);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        Drawable drawableLeft = context.getResources().getDrawable(leftResId);
        drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
        switch (position) {
            case 1:
                tv.setCompoundDrawables(null, drawable, null, null);
                break;
            case 2:
                tv.setCompoundDrawables(drawableLeft, null, drawable, null);
                break;
            case 3:
                tv.setCompoundDrawables(null, null, null, drawable);
                break;
            default:
                tv.setCompoundDrawables(drawable, null, null, null);
                break;
        }
    }
}
