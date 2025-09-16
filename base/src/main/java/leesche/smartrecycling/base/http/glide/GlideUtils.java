package leesche.smartrecycling.base.http.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;

import leesche.smartrecycling.base.R;
import leesche.smartrecycling.base.common.Constants;

public class GlideUtils {

    public static void loadNormalImg(Context context, String url, ImageView imageView){
        if (!url.startsWith("http")) {
            url = Constants.PROTOCOL_HEADER + url;
        }
        GlideApp.with(context).asBitmap().load(url).into(imageView);
    }

    public static void loadCircleImg(Context context, String url, ImageView imageView){
        if(TextUtils.isEmpty(url)){
            GlideApp.with(context).asBitmap().load(R.drawable.user).into(imageView);
            return;
        }
        if (!url.startsWith("http")) {
            url = Constants.PROTOCOL_HEADER + url;
        }
        GlideApp.with(context).asBitmap().load(url).placeholder(R.drawable.user).into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setColorFilter(0x3302C359, PorterDuff.Mode.SRC_OVER);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    public static void loadCircleImg(Context context, File file, ImageView imageView){
        GlideApp.with(context).asBitmap().load(file).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.user)
                .into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }
}
