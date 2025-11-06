package com.ycmachine.smartdevice.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;
import com.ycmachine.smartdevice.constent.ClientConstant;
import com.ycmachine.smartdevice.creator.LayerViewCreator;
import com.ycmachine.smartdevice.entity.ypg.LayerParam;
import com.ycmachine.smartdevice.handler.YpgLogicHandler;
import com.ycmachine.smartdevice.manager.RadioButtonManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import leesche.smartrecycling.base.BaseFragment;
import leesche.smartrecycling.base.utils.RxTimer;


public class PlanImageFragment  extends BaseFragment implements RxTimer.OnTimeCounterListener{



    @BindView(R2.id.radio_group_layers)
    RadioGroup radioGroupLayers;

    @Override
    public void initData() {

    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_machine_image_plan;
    }

    @Override
    protected void initView(View mRoot) {

// 1. 初始化组件
        LayerViewCreator viewCreator = new LayerViewCreator(activity); // 视图创建器


        // 2. 循环生成每层布局
        for (LayerParam param : ClientConstant.layerParams) {
            // 当前层的层数（关键：需要传递给按钮）
            int currentLayer = param.getLayerNumber();

            // 创建层容器
            LinearLayout layerContainer = viewCreator.createLayerContainer();
            TextView titleView = viewCreator.createTitleView(param.getLayerTitle());

            titleView.setOnClickListener(v -> RadioButtonManager.getInstance().setTextViewClick(currentLayer));
            // 添加层标题
            layerContainer.addView(titleView);
            // 添加数字按钮
            for (int num = param.getStartNum(); num <= param.getEndNum(); num++) {
                RadioButton radioButton = viewCreator.createNumberRadioButton(num);

                // 绑定层数信息到按钮（使用Tag存储，可存对象或基本类型）
                // 这里用数组存储 [层数, 数字]，也可自定义一个简单类
                radioButton.setTag(new int[]{currentLayer, num});

                layerContainer.addView(radioButton);
                RadioButtonManager.getInstance().addRadioButton(radioButton); // 交给管理器管理
            }
            // 添加到父容器
            radioGroupLayers.addView(layerContainer);
        }

        // 3. 设置默认选中
//        radioManager.setDefaultChecked();

        RadioButtonManager.getInstance().setOnRadioButtonClickListener(new RadioButtonManager.OnRadioButtonClickListener() {
            @Override
            public void onRadioButtonClicked(RadioButton radioButton, int number) {
                // 从Tag中获取层数信息（与setTag时的类型对应）
                int[] layerInfo = (int[]) radioButton.getTag();
                if (layerInfo != null && layerInfo.length >= 1) {
                    int layerNumber = layerInfo[0]; // 层数
                    int buttonNumber = layerInfo[1]; // 按钮数字（与参数number一致，可验证）

                    // 打印日志（示例）
                    Logger.d("选中的层数: " + layerNumber + ", 数字: " + buttonNumber);

                    // 执行业务逻辑（根据层数和数字处理）
                    YpgLogicHandler.getInstance().handleLayerOperation(layerNumber, buttonNumber);
                }
            }

            @Override
            public void onTextViewButtonClicked(int currentLayer) {
                // 一键层数测试
                Logger.i("一键层数测试"+currentLayer);

                if(ClientConstant.IS_DOING){
                    Toast.makeText(
                            requireContext(),  // 获取按钮所在的上下文
                            "操作正在执行中，请稍后再试",  // 提示消息（替换为你的message）
                            Toast.LENGTH_SHORT
                    ).show();  // 显示弹窗

                    ClientConstant.IS_DOING = false;
                    return;
                }
                ClientConstant.IS_DOING = true;


                YpgLogicHandler.getInstance().handleCurrentLayerTest(currentLayer);
            }
        });

    }

    @Override
    protected void initInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onTimeEnd() {

    }
}