package com.ycmachine.smartdevice.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.List;

import leesche.smartrecycling.base.entity.GridRegion;

/**
 * 自定义图片货道框绘制View
 * 功能：
 * 1. 按比例缩放显示高分辨率图片（如3840x2160）并居中
 * 2. 根据状态绘制红色货道框（支持"所有货道"或"单个货道"模式）
 * 3. 支持触摸编辑货道框（移动位置/调整边缘大小）
 * 4. 货道框与x/y/w/h输入框双向同步（自动缩放转换）
 * 5. 支持确认识别、保存设置按钮事件绑定
 */
public class GridImageBoxView extends View {
    // 1. 绘制工具
    private final Paint mRedBoxPaint; // 红色货道框画笔
    private final Paint mEditBoxPaint; // 编辑状态货道框画笔（加粗高亮）

    // 2. 图片相关
    private Bitmap mTargetBitmap; // 要显示的图片
    private float mScale = 1f; // 图片缩放比例（相对于原始尺寸）
    private int mScaledWidth; // 缩放后图片宽度
    private int mScaledHeight; // 缩放后图片高度
    private int mImageLeftOffset; // 图片在View中的左偏移（用于居中显示）
    private int mImageTopOffset; // 图片在View中的上偏移（用于居中显示）

    // 3. 货道数据（二选一：要么显示所有货道，要么显示单个货道）
    private List<GridRegion> mAllGridRegions; // 所有货道（选中层数时用）
    private GridRegion mSelectedGridRegion; // 单个货道（选中货道时用）

    // 4. 触摸编辑相关
    private GridRegion mEditingRegion; // 当前编辑的货道框
    private int mEditMode; // 编辑模式：0-无操作，1-移动，2-左边缘，3-右边缘，4-上边缘，5-下边缘
    private static final int MODE_NONE = 0;
    private static final int MODE_MOVE = 1;
    private static final int MODE_LEFT = 2;
    private static final int MODE_RIGHT = 3;
    private static final int MODE_TOP = 4;
    private static final int MODE_BOTTOM = 5;
    private float mLastTouchX; // 上一次触摸的原始图片X坐标
    private float mLastTouchY; // 上一次触摸的原始图片Y坐标
    private int mEdgeThreshold; // 边缘检测阈值（用于判断是否点击边缘）
    private OnRegionEditedListener mEditListener; // 编辑回调监听器

    // 5. 输入框和按钮引用（新增）
    private EditText mXEt;
    private EditText mYEt;
    private EditText mWEt;
    private EditText mHEt;
    private LinearLayout mllSaveSetting;
    private LinearLayout mllConfirmRecognition;
    private OnButtonClickListener mButtonClickListener; // 按钮点击回调

    // 新增：文本监听开关（避免设置数据时触发反向同步）
    private boolean mTextWatcherEnabled = true;
    // 保存文本监听器引用，用于控制开关
    private TextWatcher mTextWatcher;


    // 构造方法（必须实现所有重载，确保XML和代码创建都能正常工作）
    public GridImageBoxView(Context context) {
        super(context);
        mEdgeThreshold = dp2px(5);
        mRedBoxPaint = initRedBoxPaint(false);
        mEditBoxPaint = initRedBoxPaint(true);
    }

    public GridImageBoxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mEdgeThreshold = dp2px(5);
        mRedBoxPaint = initRedBoxPaint(false);
        mEditBoxPaint = initRedBoxPaint(true);
    }

    public GridImageBoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mEdgeThreshold = dp2px(5);
        mRedBoxPaint = initRedBoxPaint(false);
        mEditBoxPaint = initRedBoxPaint(true);
    }

    /**
     * 初始化货道框画笔
     * @param isEditing 是否为编辑状态（编辑状态线条更粗）
     */
    private Paint initRedBoxPaint(boolean isEditing) {
        Paint paint = new Paint();
        paint.setColor(Color.RED); // 红色框
        paint.setStyle(Paint.Style.STROKE); // 仅描边（不填充内部）
        paint.setStrokeWidth(isEditing ? dp2px(3) : dp2px(2)); // 编辑状态线宽3dp，默认2dp
        paint.setAntiAlias(true); // 抗锯齿，避免线条边缘毛糙
        paint.setStrokeCap(Paint.Cap.ROUND); // 线条端点圆角，更美观
        return paint;
    }

    // ------------------------------
    // 对外API：绑定输入框和按钮（新增核心方法）
    // ------------------------------

    /**
     * 绑定x/y/w/h输入框（双向同步用）
     */
    public void bindEditTexts(EditText xEt, EditText yEt, EditText wEt, EditText hEt) {
        this.mXEt = xEt;
        this.mYEt = yEt;
        this.mWEt = wEt;
        this.mHEt = hEt;
        // 给输入框设置文本变化监听，实现“输入框→货道框”同步
        setEditTextChangeListener();
    }

    /**
     * 绑定确认识别和保存设置按钮
     */
    public void bindButtons(LinearLayout llConfirmRecognition, LinearLayout llSaveSetting) {
        this.mllConfirmRecognition = llConfirmRecognition;
        this.mllSaveSetting = llSaveSetting;
        // 设置按钮点击事件
        setButtonClickListener();
    }

    /**
     * 设置按钮点击回调监听
     */
    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.mButtonClickListener = listener;
    }

    /**
     * 获取当前图片缩放比例（对外提供，用于坐标转换）
     */
    public float getScale() {
        return mScale;
    }

    // ------------------------------
    // 对外API：设置图片和货道数据（原有逻辑优化）
    // ------------------------------

    /**
     * 设置要显示的图片（修复：添加文本监听开关）
     */
    public void setTargetBitmap(Bitmap bitmap) {
        this.mTargetBitmap = bitmap;
        calculateScaleAndOffset();

        // 暂停文本监听
        mTextWatcherEnabled = false;
        syncRegionToEditText();
        mTextWatcherEnabled = true;

        invalidate();
    }


    /**
     * 设置要绘制的所有货道（选中层数时调用）
     * @param regions 货道区域列表（包含x/y/w/h坐标）
     */
    public void setAllGridRegions(List<GridRegion> regions) {
        this.mAllGridRegions = regions;
        this.mSelectedGridRegion = null;
        stopEditing();
        // 清空输入框
        clearEditTexts();
        invalidate();
    }


    /**
     * 设置要绘制的单个货道（强化切换逻辑）
     */
    public void setSelectedGridRegion(GridRegion region) {
        if (region == null) {
            this.mSelectedGridRegion = null;
            clearEditTexts();
            invalidate();
            return;
        }

        // 关键：先保存新货道的原始坐标（避免被输入框残留值污染）
        int newX = region.getX();
        int newY = region.getY();
        int newW = region.getWidth();
        int newH = region.getHeight();

        // 强制更新当前货道（使用新货道的原始坐标）
        this.mSelectedGridRegion = new GridRegion(); // 新建对象避免引用污染
        this.mSelectedGridRegion.setX(newX);
        this.mSelectedGridRegion.setY(newY);
        this.mSelectedGridRegion.setWidth(newW);
        this.mSelectedGridRegion.setHeight(newH);
        this.mSelectedGridRegion.setCameraNum(region.getCameraNum());
        this.mSelectedGridRegion.setGridNumber(region.getGridNumber());

        this.mAllGridRegions = null;
        stopEditing();

        // 暂停文本监听，强制同步新货道的精确值
        mTextWatcherEnabled = false;
        syncRegionToEditText(); // 用新货道的原始坐标重新计算输入框值
        mTextWatcherEnabled = true;

        invalidate();
        Log.d("SwitchDebug", "切换货道后原始坐标：x=" + newX + ", y=" + newY + ", w=" + newW + ", h=" + newH);
    }


    /**
     * 清除所有货道框（显示纯原图）
     */
    public void clearAllGridRegions() {
        this.mAllGridRegions = null;
        this.mSelectedGridRegion = null;
        stopEditing();
        // 清空输入框
        clearEditTexts();
        invalidate();
    }

    /**
     * 设置货道框编辑监听器
     */
    public void setOnRegionEditedListener(OnRegionEditedListener listener) {
        this.mEditListener = listener;
    }

    /**
     * 停止当前编辑状态
     */
    public void stopEditing() {
        mEditingRegion = null;
        mEditMode = MODE_NONE;
    }

    // ------------------------------
    // 图片缩放和偏移计算（原有逻辑不变）
    // ------------------------------

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateScaleAndOffset();
        // 视图尺寸变化后同步输入框
        syncRegionToEditText();
    }

    /**
     * 计算图片缩放比例和居中偏移量
     */
    private void calculateScaleAndOffset() {
        if (mTargetBitmap == null) return;

        // 原始图片尺寸
        int bitmapWidth = mTargetBitmap.getWidth();
        int bitmapHeight = mTargetBitmap.getHeight();
        // View可用尺寸 = 总尺寸 - 左右padding（关键！）
        int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        // 计算缩放比例（取宽高方向最小值，确保图片不变形）
        float scaleX = (float) viewWidth / bitmapWidth;
        float scaleY = (float) viewHeight / bitmapHeight;
        mScale = Math.min(scaleX, scaleY);

        // 缩放后图片尺寸
        mScaledWidth = (int) (bitmapWidth * mScale);
        mScaledHeight = (int) (bitmapHeight * mScale);

        // 图片居中偏移（基于可用尺寸计算）
        mImageLeftOffset = getPaddingLeft() + (viewWidth - mScaledWidth) / 2;
        mImageTopOffset = getPaddingTop() + (viewHeight - mScaledHeight) / 2;

        // 调试日志：打印关键参数
        Log.d("CoordDebug", "原始图尺寸：" + bitmapWidth + "x" + bitmapHeight);
        Log.d("CoordDebug", "View可用尺寸：" + viewWidth + "x" + viewHeight);
        Log.d("CoordDebug", "缩放比例：" + mScale);
        Log.d("CoordDebug", "图片偏移：left=" + mImageLeftOffset + ", top=" + mImageTopOffset);
    }

    // ------------------------------
    // 核心绘制逻辑（原有逻辑优化：编辑状态用高亮画笔）
    // ------------------------------

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTargetBitmap == null) {
            return;
        }

        // 绘制缩放后的图片
        @SuppressLint("DrawAllocation") Rect src = new Rect(0, 0, mTargetBitmap.getWidth(), mTargetBitmap.getHeight());
        @SuppressLint("DrawAllocation") Rect dst = new Rect(
                mImageLeftOffset,
                mImageTopOffset,
                mImageLeftOffset + mScaledWidth,
                mImageTopOffset + mScaledHeight
        );
        canvas.drawBitmap(mTargetBitmap, src, dst, null);

        // 绘制货道框
        if (mSelectedGridRegion != null) {
            drawSingleGridRegion(canvas, mSelectedGridRegion);
        } else if (mAllGridRegions != null && !mAllGridRegions.isEmpty()) {
            for (GridRegion region : mAllGridRegions) {
                drawSingleGridRegion(canvas, region);
            }
        }
    }

    /**
     * 绘制单个货道框（优化：编辑状态使用高亮画笔）
     * @param canvas 画布
     * @param region 货道区域（包含原始x/y/w/h）
     */
    private void drawSingleGridRegion(Canvas canvas, GridRegion region) {
        int originalX = region.getX();
        int originalY = region.getY();
        int originalW = region.getWidth();
        int originalH = region.getHeight();

        // 原始坐标 → View显示坐标（关键公式）
        float scaledX = originalX * mScale;
        float scaledY = originalY * mScale;
        float scaledRight = (originalX + originalW) * mScale;
        float scaledBottom = (originalY + originalH) * mScale;

        // 加上图片在View中的偏移量（居中偏移）
        int displayLeft = mImageLeftOffset + (int) scaledX;
        int displayTop = mImageTopOffset + (int) scaledY;
        int displayRight = mImageLeftOffset + (int) scaledRight;
        int displayBottom = mImageTopOffset + (int) scaledBottom;

        // 调试日志：对比原始坐标和转换后坐标
        Log.d("CoordDebug", "原始坐标：(" + originalX + "," + originalY + ")，宽高：" + originalW + "x" + originalH);
        Log.d("CoordDebug", "转换后坐标：(" + displayLeft + "," + displayTop + ")，宽高：" + (displayRight - displayLeft) + "x" + (displayBottom - displayTop));

        // 编辑中的货道框用高亮画笔
        Paint usedPaint = (mEditingRegion == region) ? mEditBoxPaint : mRedBoxPaint;
        canvas.drawRect(displayLeft, displayTop, displayRight, displayBottom, usedPaint);
    }

    // ------------------------------
    // 触摸事件处理（优化：编辑后同步输入框）
    // ------------------------------

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTargetBitmap == null) {
            return super.onTouchEvent(event);
        }

        // 将触摸坐标转换为原始图片坐标（View坐标 → 原始图片坐标）
        float viewTouchX = event.getX();
        float viewTouchY = event.getY();
        float originalX = (viewTouchX - mImageLeftOffset) / mScale;
        float originalY = (viewTouchY - mImageTopOffset) / mScale;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mEditingRegion = findRegionAt(originalX, originalY);
                if (mEditingRegion != null) {
                    mLastTouchX = originalX;
                    mLastTouchY = originalY;
                    mEditMode = getEditMode(mEditingRegion, originalX, originalY);
                    invalidate();
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mEditMode != MODE_NONE && mEditingRegion != null) {
                    float dx = originalX - mLastTouchX;
                    float dy = originalY - mLastTouchY;
                    updateRegion(mEditingRegion, dx, dy);
                    mLastTouchX = originalX;
                    mLastTouchY = originalY;
                    // 移动时实时同步输入框
                    syncRegionToEditText();
                    invalidate();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mEditingRegion != null) {
                    if (mEditListener != null) {
                        mEditListener.onRegionEdited(mEditingRegion);
                    }
                    // 编辑完成后同步输入框
                    syncRegionToEditText();
                }
                mEditMode = MODE_NONE;
                mEditingRegion = null;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 查找触摸位置所在的货道框（原有逻辑不变）
     */
    private GridRegion findRegionAt(float x, float y) {
        if (mSelectedGridRegion != null && isPointInRegion(mSelectedGridRegion, x, y)) {
            return mSelectedGridRegion;
        }
        if (mAllGridRegions != null) {
            for (GridRegion region : mAllGridRegions) {
                if (isPointInRegion(region, x, y)) {
                    return region;
                }
            }
        }
        return null;
    }

    /**
     * 判断点是否在货道框内（包含边缘）（原有逻辑不变）
     */
    private boolean isPointInRegion(GridRegion region, float x, float y) {
        int rx = region.getX();
        int ry = region.getY();
        int rRight = rx + region.getWidth();
        int rBottom = ry + region.getHeight();
        return x >= rx && x <= rRight && y >= ry && y <= rBottom;
    }

    /**
     * 判断当前触摸位置的编辑模式（移动或调整边缘）（原有逻辑不变）
     */
    private int getEditMode(GridRegion region, float x, float y) {
        int rx = region.getX();
        int ry = region.getY();
        int rRight = rx + region.getWidth();
        int rBottom = ry + region.getHeight();

        // 左边缘
        if (x >= rx - mEdgeThreshold && x <= rx + mEdgeThreshold && y >= ry && y <= rBottom) {
            return MODE_LEFT;
        }
        // 右边缘
        if (x >= rRight - mEdgeThreshold && x <= rRight + mEdgeThreshold && y >= ry && y <= rBottom) {
            return MODE_RIGHT;
        }
        // 上边缘
        if (y >= ry - mEdgeThreshold && y <= ry + mEdgeThreshold && x >= rx && x <= rRight) {
            return MODE_TOP;
        }
        // 下边缘
        if (y >= rBottom - mEdgeThreshold && y <= rBottom + mEdgeThreshold && x >= rx && x <= rRight) {
            return MODE_BOTTOM;
        }
        // 内部（移动模式）
        return MODE_MOVE;
    }

    /**
     * 根据编辑模式更新货道框坐标/大小（原有逻辑不变）
     */
    private void updateRegion(GridRegion region, float dx, float dy) {
        int rx = region.getX();
        int ry = region.getY();
        int rw = region.getWidth();
        int rh = region.getHeight();

        switch (mEditMode) {
            case MODE_MOVE:
                // 移动整个框（限制不超出图片范围）
                int newX = Math.max(0, (int) (rx + dx));
                int newY = Math.max(0, (int) (ry + dy));
                // 限制右边界
                if (newX + rw > mTargetBitmap.getWidth()) {
                    newX = mTargetBitmap.getWidth() - rw;
                }
                // 限制下边界
                if (newY + rh > mTargetBitmap.getHeight()) {
                    newY = mTargetBitmap.getHeight() - rh;
                }
                region.setX(newX);
                region.setY(newY);
                break;

            case MODE_LEFT:
                // 调整左边缘（确保宽度为正且不超出图片左边界）
                int newLeftX = (int) (rx + dx);
                int newLeftW = (int) (rw - dx);
                if (newLeftW > dp2px(10) && newLeftX >= 0) { // 最小宽度10dp
                    region.setX(newLeftX);
                    region.setWidth(newLeftW);
                }
                break;

            case MODE_RIGHT:
                // 调整右边缘（确保宽度为正且不超出图片右边界）
                int newRightW = (int) (rw + dx);
                if (newRightW > dp2px(10) && rx + newRightW <= mTargetBitmap.getWidth()) {
                    region.setWidth(newRightW);
                }
                break;

            case MODE_TOP:
                // 调整上边缘（确保高度为正且不超出图片上边界）
                int newTopY = (int) (ry + dy);
                int newTopH = (int) (rh - dy);
                if (newTopH > dp2px(10) && newTopY >= 0) { // 最小高度10dp
                    region.setY(newTopY);
                    region.setHeight(newTopH);
                }
                break;

            case MODE_BOTTOM:
                // 调整下边缘（确保高度为正且不超出图片下边界）
                int newBottomH = (int) (rh + dy);
                if (newBottomH > dp2px(10) && ry + newBottomH <= mTargetBitmap.getHeight()) {
                    region.setHeight(newBottomH);
                }
                break;
        }
    }

    // ------------------------------
    // 输入框同步核心逻辑（新增）
    // ------------------------------

    /**
     * 货道框数据 → 输入框（使用整数存储，避免浮点数误差）
     */
    private void syncRegionToEditText() {
        if (mSelectedGridRegion == null || mXEt == null || mYEt == null || mWEt == null || mHEt == null) {
            return;
        }

        // 原始坐标 → 缩放后坐标（直接取整，彻底避免小数误差）
        int scaledX = Math.round(mSelectedGridRegion.getX() * mScale);
        int scaledY = Math.round(mSelectedGridRegion.getY() * mScale);
        int scaledW = Math.round(mSelectedGridRegion.getWidth() * mScale);
        int scaledH = Math.round(mSelectedGridRegion.getHeight() * mScale);

        // 强制覆盖输入框，不留残留值
        mXEt.setText(String.valueOf(scaledX));
        mYEt.setText(String.valueOf(scaledY));
        mWEt.setText(String.valueOf(scaledW));
        mHEt.setText(String.valueOf(scaledH));
    }


    /**
     * 输入框数据 → 货道框（强化校验，避免累积放大）
     */
    private void syncEditTextToRegion() {
        if (mSelectedGridRegion == null || mTargetBitmap == null
                || mXEt == null || mYEt == null || mWEt == null || mHEt == null) {
            return;
        }

        try {
            // 输入框值（整数）→ 原始坐标（强制用整数计算）
            int scaledX = Integer.parseInt(mXEt.getText().toString().trim());
            int scaledY = Integer.parseInt(mYEt.getText().toString().trim());
            int scaledW = Integer.parseInt(mWEt.getText().toString().trim());
            int scaledH = Integer.parseInt(mHEt.getText().toString().trim());

            // 计算原始坐标（用Math.round确保整数结果）
            int originalX = Math.max(0, Math.round(scaledX / mScale));
            int originalY = Math.max(0, Math.round(scaledY / mScale));
            int originalW = Math.max(dp2px(10), Math.round(scaledW / mScale));
            int originalH = Math.max(dp2px(10), Math.round(scaledH / mScale));

            // 严格限制边界（防止超出图片范围导致的异常放大）
            int maxImageWidth = mTargetBitmap.getWidth();
            int maxImageHeight = mTargetBitmap.getHeight();
            originalX = Math.min(originalX, maxImageWidth - originalW);
            originalY = Math.min(originalY, maxImageHeight - originalH);

            // 关键：如果计算出的原始坐标与输入框通过缩放还原的值偏差过大，强制修正
            int checkX = Math.round(originalX * mScale);
            int checkY = Math.round(originalY * mScale);
            int checkW = Math.round(originalW * mScale);
            int checkH = Math.round(originalH * mScale);

            // 偏差超过1px时，强制用输入框的值反推（避免累积误差）
            if (Math.abs(checkX - scaledX) > 1) {
                originalX = Math.round(scaledX / mScale);
            }
            if (Math.abs(checkY - scaledY) > 1) {
                originalY = Math.round(scaledY / mScale);
            }
            if (Math.abs(checkW - scaledW) > 1) {
                originalW = Math.round(scaledW / mScale);
            }
            if (Math.abs(checkH - scaledH) > 1) {
                originalH = Math.round(scaledH / mScale);
            }

            // 最终更新
            mSelectedGridRegion.setX(originalX);
            mSelectedGridRegion.setY(originalY);
            mSelectedGridRegion.setWidth(originalW);
            mSelectedGridRegion.setHeight(originalH);
            invalidate();

        } catch (NumberFormatException e) {
            Log.e("SyncError", "输入框数据格式错误", e);
        }
    }


    /**
     * 设置输入框文本变化监听（修复：添加开关控制）
     */
    private void setEditTextChangeListener() {
        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // 关键修复：只有启用时才执行反向同步
                if (mTextWatcherEnabled) {
                    syncEditTextToRegion();
                }
            }
        };

        // 给四个输入框都设置监听
        if (mXEt != null) mXEt.addTextChangedListener(mTextWatcher);
        if (mYEt != null) mYEt.addTextChangedListener(mTextWatcher);
        if (mWEt != null) mWEt.addTextChangedListener(mTextWatcher);
        if (mHEt != null) mHEt.addTextChangedListener(mTextWatcher);
    }

    /**
     * 清空输入框内容
     */
    private void clearEditTexts() {
        if (mXEt != null) mXEt.setText("");
        if (mYEt != null) mYEt.setText("");
        if (mWEt != null) mWEt.setText("");
        if (mHEt != null) mHEt.setText("");
    }

    // ------------------------------
    // 按钮点击事件逻辑（新增）
    // ------------------------------

    /**
     * 设置按钮点击监听
     */
    private void setButtonClickListener() {
        // 确认识别按钮
        if (mllConfirmRecognition != null) {
            mllConfirmRecognition.setOnClickListener(v -> {
                if (mButtonClickListener != null) {
                    mButtonClickListener.onConfirmRecognitionClick(mSelectedGridRegion);
                }
            });
        }

        // 保存设置按钮
        if (mllSaveSetting != null) {
            mllSaveSetting.setOnClickListener(v -> {
                if (mButtonClickListener != null) {
                    // 保存前先同步输入框最新数据
                    syncEditTextToRegion();
                    mButtonClickListener.onSaveSettingClick(
                            mSelectedGridRegion.getX(),
                            mSelectedGridRegion.getY(),
                            mSelectedGridRegion.getWidth(),
                            mSelectedGridRegion.getHeight(),
                            mSelectedGridRegion
                    );
                }
            });
        }
    }

    // ------------------------------
    // 工具方法：dp转px（原有逻辑不变）
    // ------------------------------
    private int dp2px(float dpValue) {
        if (dpValue <= 0) {
            return 0;
        }
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    // ------------------------------
    // 回调接口（新增按钮回调接口）
    // ------------------------------

    /**
     * 货道框编辑完成回调（原有）
     */
    public interface OnRegionEditedListener {
        void onRegionEdited(GridRegion region);
    }

    /**
     * 按钮点击回调（新增）
     */
    public interface OnButtonClickListener {
        /**
         * 确认识别按钮点击（传入当前选中的货道框）
         */
        void onConfirmRecognitionClick(GridRegion selectedRegion);

        /**
         * 保存设置按钮点击（传入原始坐标的x/y/w/h）
         */
        void onSaveSettingClick(int x, int y, int width, int height, GridRegion gridRegion);
    }


    /**
     * 根据货道框裁剪出对应的Bitmap（仅包含货道框内的区域）
     * @param region 货道框区域（包含原始图片的x/y/w/h）
     * @return 裁剪后的Bitmap（货道框内的区域），失败返回null
     */
    public Bitmap getCroppedBitmapInRegion(GridRegion region) {
        if (mTargetBitmap == null || region == null) {
            Log.e("CropError", "原图或货道框为空，无法裁剪");
            return null;
        }

        // 获取货道框在原始图片中的坐标（关键：直接使用原始坐标，不经过缩放）
        int originalX = region.getX();
        int originalY = region.getY();
        int originalWidth = region.getWidth();
        int originalHeight = region.getHeight();

        // 校验裁剪区域是否在原图范围内（避免越界崩溃）
        int bitmapWidth = mTargetBitmap.getWidth();
        int bitmapHeight = mTargetBitmap.getHeight();
        if (originalX < 0 || originalY < 0
                || originalWidth <= 0 || originalHeight <= 0
                || originalX + originalWidth > bitmapWidth
                || originalY + originalHeight > bitmapHeight) {
            Log.e("CropError", "货道框超出原图范围，无法裁剪");
            return null;
        }

        try {
            // 从原图中裁剪出货道框内的区域（使用原始坐标）
            return Bitmap.createBitmap(
                    mTargetBitmap,       // 原图
                    originalX,           // 裁剪区域左上角X（原始坐标）
                    originalY,           // 裁剪区域左上角Y（原始坐标）
                    originalWidth,       // 裁剪宽度（原始宽度）
                    originalHeight       // 裁剪高度（原始高度）
            );
        } catch (Exception e) {
            Log.e("CropError", "裁剪货道框内Bitmap失败", e);
            return null;
        }
    }
}