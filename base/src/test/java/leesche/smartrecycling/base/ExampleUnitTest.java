package leesche.smartrecycling.base;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

import com.king.wechat.qrcode.WeChatQRCodeDetector;

import org.junit.Test;

import java.util.List;

import leesche.smartrecycling.base.utils.DrawableToBitmapUtil;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);


    }

    /**
     * BitmapDrawable 转 Bitmap（高效）
     * @param bitmapDrawable 输入的 BitmapDrawable
     * @param targetWidth 目标宽度（可选，0 表示不缩放）
     * @param targetHeight 目标高度（可选，0 表示不缩放）
     * @return 转换后的 Bitmap
     */
    public static Bitmap bitmapDrawableToBitmap(BitmapDrawable bitmapDrawable, int targetWidth, int targetHeight) {
        if (bitmapDrawable == null || bitmapDrawable.getBitmap() == null) {
            return null;
        }

        Bitmap originalBitmap = bitmapDrawable.getBitmap();
        // 若不需要缩放，直接返回原 Bitmap
        if (targetWidth <= 0 || targetHeight <= 0) {
            return originalBitmap;
        }

        // 按目标尺寸缩放（保持比例，避免拉伸）
        return Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);
    }
}