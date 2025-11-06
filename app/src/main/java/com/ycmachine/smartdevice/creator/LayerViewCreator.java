package com.ycmachine.smartdevice.creator;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.utils.DpUtils;


// 负责创建所有视图（层容器、标题、数字按钮）
public class LayerViewCreator {
    private Context mContext;

    public LayerViewCreator(Context context) {
        this.mContext = context;
    }

    // 创建每层的容器（横向LinearLayout）
    public LinearLayout createLayerContainer() {
        LinearLayout container = new LinearLayout(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT // 高度包裹内容，避免占满父容器
        );
        layoutParams.setMargins(0, 0, 0, DpUtils.dpToPx(mContext, 5)); // 层间距保持不变
        container.setLayoutParams(layoutParams);
        container.setOrientation(LinearLayout.HORIZONTAL);

        // 关键修改：垂直方向顶部对齐，水平方向居中（根据需求可调整为LEFT）
        container.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

        return container;
    }
    // 创建层标题（如T1-T8）
    public TextView createTitleView(String title) {
        TextView titleView = new TextView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                DpUtils.dpToPx(mContext, 40), // 宽度40dp
                DpUtils.dpToPx(mContext, 34)  // 高度35dp
        );
        params.setMargins(0, 0, DpUtils.dpToPx(mContext, 3), 0); // 与数字按钮间距3dp
        titleView.setLayoutParams(params);
        titleView.setBackgroundResource(R.drawable.number_layers); // 标题背景
        titleView.setGravity(Gravity.CENTER);
        titleView.setText(title);
        titleView.setTextColor(ContextCompat.getColor(mContext, R.color.layer_title_color)); // #C27121
        titleView.setTextSize(18);
        return titleView;
    }

    // 创建数字单选按钮（如1-84）
    public RadioButton createNumberRadioButton(int number) {
        RadioButton radioButton = new RadioButton(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                DpUtils.dpToPx(mContext, 33), // 宽度30dp
                DpUtils.dpToPx(mContext, 34)  // 高度35dp
        );
        radioButton.setLayoutParams(params);
        radioButton.setBackgroundResource(R.drawable.number_layers_bg_selector); // 背景选择器
        radioButton.setButtonDrawable(null); // 隐藏默认单选图标
        radioButton.setGravity(Gravity.CENTER);
        radioButton.setText(String.valueOf(number));
        radioButton.setTextColor(mContext.getResources().getColorStateList(R.drawable.number_layers_text_color_selector)); // 文字颜色选择器
        radioButton.setTextSize(17);
        return radioButton;
    }

    private static final int BOTTOM_TEXT_ID = 10001;
    // 创建数字单选按钮（带底部红底白字文本）
    public LinearLayout createNumberRadioButton(int number, String bottomText) {
        // 1. 创建外层容器（垂直排列，包含按钮和底部文本）
        LinearLayout container = new LinearLayout(mContext);
        container.setOrientation(LinearLayout.VERTICAL); // 垂直排列
        container.setGravity(Gravity.CENTER); // 内部视图居中

        // 2. 创建数字单选按钮（核心按钮）
        RadioButton radioButton = new RadioButton(mContext);
        LinearLayout.LayoutParams rbParams = new LinearLayout.LayoutParams(
                DpUtils.dpToPx(mContext, 34), // 宽度33dp
                DpUtils.dpToPx(mContext, 34)  // 高度34dp
        );
        radioButton.setLayoutParams(rbParams);
        radioButton.setBackgroundResource(R.drawable.number_layers_bg_selector); // 按钮背景选择器
        radioButton.setButtonDrawable(null); // 隐藏默认单选图标
        radioButton.setGravity(Gravity.CENTER);
        radioButton.setText(String.valueOf(number));
        radioButton.setTextColor(mContext.getResources().getColorStateList(R.drawable.number_layers_text_color_selector)); // 文字颜色选择器
        radioButton.setTextSize(17);

        // 3. 创建底部红底白字文本（默认隐藏）
        TextView bottomTextView = new TextView(mContext);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        tvParams.topMargin = DpUtils.dpToPx(mContext, -5); // 与按钮的间距2dp
        bottomTextView.setLayoutParams(tvParams);
//        bottomTextView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.holo_red_dark)); // 红底
        bottomTextView.setBackgroundColor(Color.parseColor("#ac33c1"));
        bottomTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.white)); // 白字
        bottomTextView.setTextSize(12); // 小字
        bottomTextView.setId(BOTTOM_TEXT_ID); // 设置固定id，便于后续查找
        bottomTextView.setPadding(
                DpUtils.dpToPx(mContext, 5),
                DpUtils.dpToPx(mContext, 1),
                DpUtils.dpToPx(mContext, 5),
                DpUtils.dpToPx(mContext, 1)
        ); // 内边距

        // 4. 控制底部文本的显示（有内容则显示，否则隐藏）
        if (bottomText != null && !bottomText.isEmpty()) {
            bottomTextView.setText(bottomText);
            bottomTextView.setVisibility(View.VISIBLE);
        } else {
            bottomTextView.setVisibility(View.INVISIBLE);
        }

        // 5. 将按钮和文本添加到容器
        container.addView(radioButton);
        container.addView(bottomTextView);

        // 6. 设置容器的布局参数（整体宽度和高度）
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                DpUtils.dpToPx(mContext, 33), // 容器宽度=按钮宽度
                ViewGroup.LayoutParams.WRAP_CONTENT // 容器高度=内容高度（按钮+文本，自动适应）
        );
        container.setLayoutParams(containerParams);

        // 7. 将按钮的点击事件透传到容器（可选，根据需要）
        container.setOnClickListener(v -> radioButton.setChecked(!radioButton.isChecked()));

        return container; // 返回整个容器（包含按钮和文本）
    }

    // 新增：从容器中获取底部文本TextView
    public TextView getBottomTextView(LinearLayout container) {
        if (container == null) return null;
        // 根据固定id查找底部文本视图
        return container.findViewById(BOTTOM_TEXT_ID);
    }


}