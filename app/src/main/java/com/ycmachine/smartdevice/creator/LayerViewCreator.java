package com.ycmachine.smartdevice.creator;

import android.content.Context;
import android.view.Gravity;
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
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, DpUtils.dpToPx(mContext, 5)); // 层间距5dp
        container.setLayoutParams(layoutParams);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setGravity(Gravity.CENTER_VERTICAL);
        return container;
    }

    // 创建层标题（如T1-T8）
    public TextView createTitleView(String title) {
        TextView titleView = new TextView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                DpUtils.dpToPx(mContext, 40), // 宽度40dp
                DpUtils.dpToPx(mContext, 35)  // 高度35dp
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
                DpUtils.dpToPx(mContext, 35)  // 高度35dp
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
}