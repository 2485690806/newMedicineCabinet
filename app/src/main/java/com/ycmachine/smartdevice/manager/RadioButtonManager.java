package com.ycmachine.smartdevice.manager;

import android.widget.RadioButton;
import android.widget.Toast;

import com.ycmachine.smartdevice.constent.ClientConstant;

import java.util.ArrayList;
import java.util.List;

// 管理所有单选按钮，处理全局单选逻辑
public class RadioButtonManager {
    private List<RadioButton> mRadioButtons = new ArrayList<>();


    private static final class RadioButtonManagerHolder {
        static final RadioButtonManager ToDiLogicHandler = new RadioButtonManager();
    }

    public static RadioButtonManager getInstance() {
        return RadioButtonManager.RadioButtonManagerHolder.ToDiLogicHandler;
    }


    // 添加单选按钮到管理列表
    public void addRadioButton(RadioButton radioButton) {
        mRadioButtons.add(radioButton);
        // 绑定点击事件
        radioButton.setOnClickListener(v -> setExclusiveChecked((RadioButton) v));
    }

    // 设置唯一选中（取消其他按钮选中状态）
    private void setExclusiveChecked(RadioButton checkedButton) {

        if(ClientConstant.IS_DOING){
            checkedButton.setChecked(false);
            Toast.makeText(
                    checkedButton.getContext(),  // 获取按钮所在的上下文
                    "操作正在执行中，请稍后再试",  // 提示消息（替换为你的message）
                    Toast.LENGTH_SHORT
            ).show();  // 显示弹窗
            ClientConstant.IS_DOING = false;
            return;
        }
        ClientConstant.IS_DOING = true;

        for (RadioButton rb : mRadioButtons) {
            rb.setChecked(rb == checkedButton);
        }

        String text = checkedButton.getText().toString();
        int number = Integer.parseInt(text);
        listener.onRadioButtonClicked(checkedButton, number);

    }

    // 初始化默认选中第一个按钮
    public void setDefaultChecked() {
        if (!mRadioButtons.isEmpty()) {
            mRadioButtons.get(0).setChecked(true);
        }
    }

    public void clearCheckedButton(){

        for (RadioButton rb : mRadioButtons) {
            rb.setChecked(false);
        }
    }


    private OnRadioButtonClickListener listener; // 点击事件接口

    // 点击事件回调接口
    public interface OnRadioButtonClickListener {
        void onRadioButtonClicked(RadioButton radioButton, int number);
        void onTextViewButtonClicked( int currentLayer);
    }

    // 设置监听器
    public void setOnRadioButtonClickListener(OnRadioButtonClickListener listener) {
        this.listener = listener;
    }


    // 一键层级测试
    public void setTextViewClick(int currentLayer) {



        listener.onTextViewButtonClicked(currentLayer);

    }
}