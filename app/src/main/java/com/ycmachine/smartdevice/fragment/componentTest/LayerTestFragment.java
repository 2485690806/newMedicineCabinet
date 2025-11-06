package com.ycmachine.smartdevice.fragment.componentTest;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;
import com.ycmachine.smartdevice.constent.ClientConstant;
import com.ycmachine.smartdevice.entity.ypg.Layer;
import com.ycmachine.smartdevice.handler.ComponenTestHandler;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import leesche.smartrecycling.base.BaseFragment;
import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.utils.FileUtil;
import leesche.smartrecycling.base.utils.RxTimer;

public class LayerTestFragment extends BaseFragment implements RxTimer.OnTimeCounterListener {
    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_layer_test;
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

    @BindView(R2.id.et_step_count)
    EditText etStepCount;


    @BindView(R2.id.rb_recycle_layer)
    RadioButton rbRecycleLayer;

    // 绑定保存按钮布局
    @BindView(R2.id.ll_save_setting)
    View llSaveSetting;

    // 存储所有层级RadioButton的列表
    private List<RadioButton> layerRadioButtons = new ArrayList<>();

    /**
     * 初始化层级选择RadioButton
     */
    private void initLayerRadioButtons() {
        // 将所有层级RadioButton添加到列表
        layerRadioButtons.add(rbLayer1);
        layerRadioButtons.add(rbLayer2);
        layerRadioButtons.add(rbLayer3);
        layerRadioButtons.add(rbLayer4);
        layerRadioButtons.add(rbPickLayer);
        layerRadioButtons.add(rbLayer5);
        layerRadioButtons.add(rbLayer6);
        layerRadioButtons.add(rbLayer7);
        layerRadioButtons.add(rbLayer8);
        layerRadioButtons.add(rbLayer9);
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

            // 实现单选效果：仅选中当前点击的按钮
            for (RadioButton rb : layerRadioButtons) {
                rb.setChecked(rb == clickedRb);
            }

            // 处理层级选择逻辑
            handleLayerSelection(clickedRb);
        };

        // 为所有层级RadioButton绑定监听器
        for (RadioButton rb : layerRadioButtons) {
            rb.setOnClickListener(layerListener);
        }
        handleLayerOperation(1); // 1层逻辑
    }

    /**
     * 设置输入监听，实现输入完成后自动保存
     */
    private void setupInputListener() {
        // 监听输入文本变化（实时保存，可选）
        etStepCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(StringUtils.isBlank(s.toString()))
                    return;
                // 输入变化后立即保存（适合需要实时保存的场景）
//                saveStepCountToLocal(s.toString());
                ClientConstant.medicineCabinetLayer[nowLayerNumber - 1] = new Layer(Integer.parseInt(s.toString()));
            }
        });

        // 监听失去焦点事件（输入完成后，点击其他地方时保存）
        etStepCount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // 失去焦点时
//                saveStepCountToLocal(etStepCount.getText().toString());
            }
        });
    }


    /**
     * 处理层级选择逻辑
     */
    private void handleLayerSelection(RadioButton selectedRb) {
        String layerName = selectedRb.getText().toString();
        int layerId = selectedRb.getId();

        // 打印选中的层级信息
        Logger.d("选中层级：" + layerName + "，ID：" + layerId);

        // 根据选中的层级执行对应逻辑
        if (layerId == R.id.rb_layer_1) {
            handleLayerOperation(1); // 1层逻辑
        } else if (layerId == R.id.rb_layer_2) {
            handleLayerOperation(2); // 2层逻辑
        } else if (layerId == R.id.rb_layer_3) {
            handleLayerOperation(3); // 3层逻辑
        } else if (layerId == R.id.rb_layer_4) {
            handleLayerOperation(4); // 4层逻辑
        }else if (layerId == R.id.rb_layer_5) {
            handleLayerOperation(5); // 5层逻辑
        } else if (layerId == R.id.rb_layer_6) {
            handleLayerOperation(6); // 6层逻辑
        } else if (layerId == R.id.rb_layer_7) {
            handleLayerOperation(7); // 7层逻辑
        } else if (layerId == R.id.rb_layer_8) {
            handleLayerOperation(8); // 8层逻辑
        }else if (layerId == R.id.rb_layer_9) {
            handleLayerOperation(9); // 9层逻辑
        } else if (layerId == R.id.rb_pick_layer) {
            handleLayerOperation(10); // 取货层逻辑
        } else if (layerId == R.id.rb_recycle_layer) {
            handleLayerOperation(11); // 回收层逻辑
        } else if (layerId == R.id.rb_reposition) {
            handleReposition(); // 执行复位操作
        }
    }

    void handleReposition() {
        Log.d("Control", "执行复位操作");
        ComponenTestHandler.getInstance().YaxisReset();
    }
    private int nowLayerNumber = 1;

    /**
     * 处理1-8层的通用操作
     */
    private void handleLayerOperation(int layerNumber) {
        nowLayerNumber = layerNumber;
        Logger.d("执行" + layerNumber + "层操作");
        // 1-8层的通用业务逻辑
//        YpgLogicHandler.getInstance().handleLayerOperation(layerNumber, 0);

        Layer layer = ClientConstant.medicineCabinetLayer[layerNumber - 1];
        etStepCount.setText(layer.getBushu() + "");

    }

    /**
     * 保存设置按钮点击事件（通过ButterKnife的@OnClick绑定）
     */
    @OnClick(R2.id.ll_save_setting)
    void onSaveSettingClick() {
        if (ClientConstant.IS_DOING) {
            Toast.makeText(getActivity(), "操作正在执行中，无法保存", Toast.LENGTH_SHORT).show();

            ClientConstant.IS_DOING = false;
            return;

        }

        // 获取当前选中的层级
        RadioButton selectedRb = getSelectedLayerRadioButton();
        if (selectedRb == null) {
            Toast.makeText(getActivity(), "请先选择层级", Toast.LENGTH_SHORT).show();
            return;
        }

        // 执行保存逻辑（示例）
        Logger.d("保存设置：选中层级=" + selectedRb.getText());
        Toast.makeText(getActivity(), "设置已保存", Toast.LENGTH_SHORT).show();

        Gson gson = new Gson();
        FileUtil.writeFileSdcardFile(Constants.LAYER_LIST, gson.toJson(ClientConstant.medicineCabinetLayer));

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
        setupInputListener();
    }

    @Override
    protected void initInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onTimeEnd() {

    }
}
