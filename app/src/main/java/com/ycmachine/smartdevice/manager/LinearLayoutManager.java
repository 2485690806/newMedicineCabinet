package com.ycmachine.smartdevice.manager;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.constent.ClientConstant;
import com.ycmachine.smartdevice.creator.LayerViewCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理包含RadioButton的LinearLayout容器，处理单选逻辑
 */
public class LinearLayoutManager {
    // 存储所有包含RadioButton的LinearLayout容器
    private List<LinearLayout> mContainerList = new ArrayList<>();
    // 点击事件监听器
    private OnContainerClickListener listener;

    // 单例模式
    private static final class Holder {
        static final LinearLayoutManager INSTANCE = new LinearLayoutManager();
    }

    public static LinearLayoutManager getInstance() {
        return Holder.INSTANCE;
    }

    private LinearLayoutManager() {}


    /**
     * 添加包含RadioButton的LinearLayout容器到管理列表
     * @param container 包含RadioButton的LinearLayout
     */
    public void addContainer(LinearLayout container) {
        if (container == null) return;

        mContainerList.add(container);
        RadioButton radioButton = getRadioButtonFromContainer(container);
        if (radioButton == null) return;

        // 修复：为整个容器设置点击事件（避免RadioButton被遮挡导致事件丢失）
        container.setOnClickListener(v -> {
            // 点击容器时，切换内部RadioButton的状态
            radioButton.setChecked(!radioButton.isChecked());
            // 触发单选逻辑
            setExclusiveChecked(container, radioButton);
        });

        // 保留RadioButton自身的点击事件（双重保障）
        radioButton.setOnClickListener(v -> {
            if (v instanceof RadioButton) {
                setExclusiveChecked(container, (RadioButton) v);
            }
        });
    }
    /**
     * 从LinearLayout容器中获取内部的RadioButton
     * @param container 容器
     * @return 容器中的RadioButton（如果存在）
     */
    private RadioButton getRadioButtonFromContainer(LinearLayout container) {
        if (container.getChildCount() > 0) {
            View child = container.getChildAt(0);
            if (child instanceof RadioButton) {
                return (RadioButton) child;
            }
        }
        return null;
    }

    /**
     * 设置唯一选中（取消其他容器中RadioButton的选中状态）
     * @param checkedContainer 被点击的容器
     * @param checkedButton 被点击的RadioButton
     */
    private void setExclusiveChecked(LinearLayout checkedContainer, RadioButton checkedButton) {
        // 操作中校验
        if (ClientConstant.IS_DOING) {
            checkedButton.setChecked(false);
            showToast(checkedContainer.getContext(), "操作正在执行中，请稍后再试");
            ClientConstant.IS_DOING = false;
            return;
        }

        // 取消其他容器中RadioButton的选中状态
        for (LinearLayout container : mContainerList) {
            if (container == checkedContainer) continue;

            RadioButton rb = getRadioButtonFromContainer(container);
            if (rb != null) {
                rb.setChecked(false);
            }
        }

        // 选中当前RadioButton
        checkedButton.setChecked(true);
        ClientConstant.IS_DOING = true;

// 触发点击回调
        try {
            String text = checkedButton.getText().toString();
            int number = Integer.parseInt(text);
            int[] tag = (int[]) checkedContainer.getTag();
            int currentLayer = tag != null && tag.length > 0 ? tag[0] : -1;

            // 增加日志，确认参数是否正确
            Logger.d("准备触发回调：currentLayer=" + currentLayer + ", number=" + number + ", listener=" + (listener != null));

            if (listener != null) {
                // 手动调用一次选中状态（确保选择器生效）
                checkedButton.setChecked(true);
                // 触发回调
                listener.onContainerClicked(checkedContainer, checkedButton, currentLayer, number);
            } else {
                Logger.e("listener为空，未设置监听器！");
            }
        } catch (NumberFormatException e) {
            Logger.e("数字解析错误：" + e.getMessage());
            showToast(checkedContainer.getContext(), "数据格式错误");
            ClientConstant.IS_DOING = false;
        } catch (Exception e) {
            // 捕获其他可能的异常，避免回调被阻断
            Logger.e("回调触发失败：" + e.getMessage());
            ClientConstant.IS_DOING = false;
        }
    }

    /**
     * 初始化默认选中第一个容器中的RadioButton
     */
    public void setDefaultChecked() {
        if (!mContainerList.isEmpty()) {
            LinearLayout firstContainer = mContainerList.get(0);
            RadioButton firstRb = getRadioButtonFromContainer(firstContainer);
            if (firstRb != null) {
                firstRb.setChecked(true);
                // 触发默认选中回调
                if (listener != null) {
                    try {
                        int number = Integer.parseInt(firstRb.getText().toString());
                        int[] tag = (int[]) firstContainer.getTag();
                        int currentLayer = tag != null && tag.length > 0 ? tag[0] : -1;
                        listener.onContainerClicked(firstContainer, firstRb, currentLayer, number);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 清空所有选中状态
     */
    public void clearChecked() {
        for (LinearLayout container : mContainerList) {
            RadioButton rb = getRadioButtonFromContainer(container);
            if (rb != null) {
                rb.setChecked(false);
            }
        }
        ClientConstant.IS_DOING = false;
    }

    /**
     * 释放资源（页面销毁时调用）
     */
    public void clear() {
        mContainerList.clear();
        listener = null;
        ClientConstant.IS_DOING = false;
    }

    /**
     * 显示Toast
     */
    private void showToast(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    // 点击事件回调接口
    public interface OnContainerClickListener {
        /**
         * 容器中的RadioButton被点击
         * @param container 包含RadioButton的容器
         * @param radioButton 被点击的RadioButton
         * @param currentLayer 层数（从Tag中获取）
         * @param number 按钮数字
         */
        void onContainerClicked(LinearLayout container, RadioButton radioButton, int currentLayer, int number);

        /**
         * 层级标题被点击（复用原逻辑）
         * @param currentLayer 层数
         */
        void onTextViewClicked(int currentLayer);
    }

    /**
     * 设置监听器
     */
    public void setOnContainerClickListener(OnContainerClickListener listener) {
        this.listener = listener;
    }

    /**
     * 处理层级标题点击（供标题View调用）
     */
    public void setTextViewClick(int currentLayer) {
        if (listener != null) {
            listener.onTextViewClicked(currentLayer);
        }
    }

    /**
     * 动态更新指定按钮下方的文本
     * @param targetLayer 目标层数（currentLayer）
     * @param targetNum 目标数字（num）
     * @param newBottomText 新的底部文本（null则隐藏）
     * @param viewCreator 用于获取底部文本视图的创建器
     */
    public void updateBottomText(int targetLayer, int targetNum, String newBottomText, LayerViewCreator viewCreator) {
        Logger.i("targetLayer"+targetLayer+"targetNum"+targetNum+"newBottomText"+newBottomText);
        // 遍历所有容器，查找匹配的层数和数字
        for (LinearLayout container : mContainerList) {
            // 获取容器的Tag（存储的[层数, 数字]）
            Object tag = container.getTag();
            if (tag instanceof int[]) {
                int[] layerNum = (int[]) tag;
                int currentLayer = layerNum[0];
                int currentNum = layerNum[1];

                // 匹配目标层数和数字
                if (currentLayer == targetLayer && currentNum == targetNum) {
                    Logger.i("currentLayer"+currentLayer+"currentNum"+currentNum);
                    // 通过LayerViewCreator获取底部文本视图
                    TextView bottomTextView = viewCreator.getBottomTextView(container);
                    if (bottomTextView != null) {
                        // 更新文本内容并控制显示/隐藏
                        if (newBottomText != null && !newBottomText.isEmpty()) {
                            bottomTextView.setText(newBottomText);
                            bottomTextView.setVisibility(View.VISIBLE);
                        } else {
                            bottomTextView.setText("");
                            bottomTextView.setVisibility(View.GONE);
                        }
                    }
                    return; // 找到后退出循环
                }
            }
        }
    }


}