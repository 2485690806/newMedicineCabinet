package leesche.smartrecycling.base.http.glide;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
        int diskCacheSizeBytes = 1024 * 1024 * 100; // 500 MB
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, "glide", diskCacheSizeBytes));
//        builder.setDiskCache(new DiskLruCacheFactory(Constants.AD_IMAGE_DIR, diskCacheSizeBytes));
    }
}
