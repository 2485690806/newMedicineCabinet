package leesche.smartrecycling.base.widget;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomConstraintLayout extends ConstraintLayout {
    public boolean mIsIntercept = false;

    public CustomConstraintLayout(Context context) {
        super(context);
    }

    public CustomConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mIsIntercept;
    }

    public boolean isIsIntercept() {
        return mIsIntercept;
    }

    public void setIsIntercept(boolean mIsIntercept) {
        this.mIsIntercept = mIsIntercept;
    }
}
