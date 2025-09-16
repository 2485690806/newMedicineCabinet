package leesche.smartrecycling.base.http.glide;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.FrameLayout;

import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;

public class MyTargetFlView extends CustomViewTarget<FrameLayout, Drawable> {

    FrameLayout view;

    public MyTargetFlView(@NonNull FrameLayout view) {
        super(view);
        this.view = view;
    }

    @Override
    protected void onResourceCleared(@Nullable Drawable placeholder) {

    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {

    }

    @Override
    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
        this.view.setBackground(resource);
    }
}
