package com.ycmachine.smartdevice.fragment.componentTest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;
import com.ycmachine.smartdevice.constent.ClientConstant;
import com.ycmachine.smartdevice.handler.ComponenTestHandler;
import com.ycmachine.smartdevice.handler.YpgLogicHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import leesche.smartrecycling.base.BaseFragment;
import leesche.smartrecycling.base.utils.RxTimer;

public class ComponentTestNumberFragment  extends BaseFragment implements RxTimer.OnTimeCounterListener{

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_machine_test_number;
    }
    // 绑定所有RadioButton（与布局ID对应）
    @BindView(R2.id.rb_layer_1)
    RadioButton rbLayer1;

    @BindView(R2.id.rb_layer_2)
    RadioButton rbLayer2;

    @BindView(R2.id.rb_layer_3)
    RadioButton rbLayer3;

    @BindView(R2.id.rb_layer_4)
    RadioButton rbLayer4;

    @BindView(R2.id.rb_pick_layer)
    RadioButton rbPickLayer;

    @BindView(R2.id.rb_layer_5)
    RadioButton rbLayer5;

    @BindView(R2.id.rb_layer_6)
    RadioButton rbLayer6;

    @BindView(R2.id.rb_layer_7)
    RadioButton rbLayer7;

    @BindView(R2.id.rb_layer_8)
    RadioButton rbLayer8;

    @BindView(R2.id.rb_layer_9)
    RadioButton rbLayer9;

    @BindView(R2.id.rb_reposition)
    RadioButton rbReposition;

    @BindView(R2.id.rb_recycle_layer)
    RadioButton rbRecycleLayer;

    // 存储所有层级RadioButton的列表
    private List<RadioButton> layerRadioButtons = new ArrayList<>();

    /**
     * 初始化层级单选按钮
     */
    private void initLayerRadioButtons() {
        // 将所有RadioButton添加到列表
        layerRadioButtons.add(rbLayer1);
        layerRadioButtons.add(rbLayer2);
        layerRadioButtons.add(rbLayer3);
        layerRadioButtons.add(rbLayer4);
        layerRadioButtons.add(rbLayer5);
        layerRadioButtons.add(rbLayer6);
        layerRadioButtons.add(rbLayer7);
        layerRadioButtons.add(rbLayer8);
        layerRadioButtons.add(rbLayer9);
        layerRadioButtons.add(rbPickLayer);
        layerRadioButtons.add(rbRecycleLayer);
        layerRadioButtons.add(rbReposition);

        // 设置全局单选监听器
        View.OnClickListener layerListener = v -> {
            RadioButton clickedRb = (RadioButton) v;

            // 操作执行中禁止切换
            if (ClientConstant.IS_DOING) {
                clickedRb.setChecked(false);
                Toast.makeText(
                        clickedRb.getContext(),
                        "操作正在执行中，请稍后再试",
                        Toast.LENGTH_SHORT
                ).show();

                ClientConstant.IS_DOING = false;
                return;
            }

            // 实现单选效果
            for (RadioButton rb : layerRadioButtons) {
                rb.setChecked(rb == clickedRb);
            }

            // 处理选中逻辑
            handleLayerSelection(clickedRb);
        };

        // 为所有RadioButton绑定监听器
        for (RadioButton rb : layerRadioButtons) {
            rb.setOnClickListener(layerListener);
        }
    }

    /**
     * 处理层级选择
     */
    private void handleLayerSelection(RadioButton selectedRb) {
        String layerInfo = selectedRb.getText().toString();
        int layerId = selectedRb.getId();

        Logger.d("选中层级: " + layerInfo);

        // 根据选中的层级执行对应操作
        if (layerId == R2.id.rb_layer_1) {
            handleNumberLayerOperation(1);
        } else if (layerId == R2.id.rb_layer_2) {
            handleNumberLayerOperation(2);
        } else if (layerId == R2.id.rb_layer_3) {
            handleNumberLayerOperation(3);
        } else if (layerId == R2.id.rb_layer_4) {
            handleNumberLayerOperation(4);
        } else if (layerId == R2.id.rb_layer_5) {
            handleNumberLayerOperation(5);
        } else if (layerId == R2.id.rb_layer_6) {
            handleNumberLayerOperation(6);
        } else if (layerId == R2.id.rb_layer_7) {
            handleNumberLayerOperation(7);
        } else if (layerId == R2.id.rb_layer_8) {
            handleNumberLayerOperation(8);
        } else if (layerId == R2.id.rb_pick_layer) {
            handleNumberLayerOperation(9);
        } else if (layerId == R2.id.rb_recycle_layer) {
            handleNumberLayerOperation(10);
        } else if (layerId == R2.id.rb_layer_9) {
            handleNumberLayerOperation(11);
        } else if (layerId == R2.id.rb_reposition) {
            handleReposition();
        }
    }

    void handleReposition() {
        Log.d("Control", "执行复位操作");
        ComponenTestHandler.getInstance().YaxisReset();
    }
    /**
     * 处理数字层级(1-8)操作
     */
    private void handleNumberLayerOperation(int layerNumber) {
        Logger.d("执行" + layerNumber + "层操作");
        // 数字层级通用操作逻辑
        updateStatus("已选择" + layerNumber + "层");
        YpgLogicHandler.getInstance().handleLayerOperation(layerNumber, 0);
    }

    /**
     * 处理取货层操作
     */
    private void handlePickLayerOperation() {
        Logger.d("执行取货层操作");
        // 取货层专属逻辑
        updateStatus("已选择取货层");
    }

    /**
     * 处理回收层操作
     */
    private void handleRecycleLayerOperation() {
        Logger.d("执行回收层操作");
        // 回收层专属逻辑
        updateStatus("已选择回收层");
    }

    /**
     * 更新状态显示
     */
    private void updateStatus(String status) {
        // 这里假设状态文本的id为tv_status
        // TextView tvStatus = llStatusContainer.findViewById(R.id.tv_status);
        // tvStatus.setText(status);
        Logger.d("当前状态: " + status);
    }

    /**
     * 状态容器点击事件（如果需要）
     */
    @OnClick(R2.id.ll_status_container)
    void onStatusContainerClick() {
        Toast.makeText(getActivity(), "当前状态区域点击", Toast.LENGTH_SHORT).show();
    }
    /**
     * 获取当前选中的层级RadioButton
     */
    private RadioButton getSelectedLayerRadioButton() {
        for (RadioButton rb : layerRadioButtons) {
            if (rb.isChecked()) {
                return rb;
            }
        }
        return null;
    }
    @Override
    protected void initView(View mRoot) {
        initLayerRadioButtons();
    }

    @Override
    protected void initInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onTimeEnd() {

    }
}
