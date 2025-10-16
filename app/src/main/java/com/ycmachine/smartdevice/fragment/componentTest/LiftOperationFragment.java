package com.ycmachine.smartdevice.fragment.componentTest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;
import com.ycmachine.smartdevice.constent.ClientConstant;
import com.ycmachine.smartdevice.handler.ComponenTestHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import leesche.smartrecycling.base.BaseFragment;
import leesche.smartrecycling.base.utils.RxTimer;

public class LiftOperationFragment extends BaseFragment implements RxTimer.OnTimeCounterListener{
    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_machine_lift_operation;
    }

    @BindView(R2.id.rb_reposition)
    RadioButton rbReposition;

    @BindView(R2.id.rb_decline)
    RadioButton rbDecline;

    @BindView(R2.id.rb_stop)
    RadioButton rbStop;

    @BindView(R2.id.rb_rise)
    RadioButton rbRise;
    // 声明列表存储所有RadioButton
    private List<RadioButton> controlRadioButtons = new ArrayList<>();


    private List<RadioButton> allRadioButtons = new ArrayList<>();

    @Override
    protected void initView(View mRoot) {
        initRadioButtons();
    }
    // 在初始化方法中（如onCreate或initViews）设置监听器
    private void initRadioButtons() {
        // 将所有选项添加到列表
        controlRadioButtons.add(rbRise);      // 上升
        controlRadioButtons.add(rbDecline);   // 下降
        controlRadioButtons.add(rbStop);      // 停止
        controlRadioButtons.add(rbReposition);// 复位

        // 设置全局单选监听器
        View.OnClickListener controlListener = v -> {
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
            for (RadioButton rb : controlRadioButtons) {
                rb.setChecked(rb == clickedRb);
            }

            // 处理对应点击事件
            handleRadioClick(clickedRb.getId());
        };

        // 为所有RadioButton绑定监听器
        for (RadioButton rb : controlRadioButtons) {
            rb.setOnClickListener(controlListener);
        }
    }

    // 处理不同RadioButton的点击事件
    private void handleRadioClick(int checkedId) {
        if (checkedId == R2.id.rb_rise) {
            // 上升逻辑
            handleRise();
        } else if (checkedId == R2.id.rb_decline) {
            // 下降逻辑
            handleDecline();
        } else if (checkedId == R2.id.rb_stop) {
            // 停止逻辑
            handleStop();
        } else if (checkedId == R2.id.rb_reposition) {
            // 复位逻辑
            handleReposition();
        }
    }

    private void handleRise() {
        Log.d("Control", "执行上升操作");
        // 实际业务逻辑：如发送上升指令到设备

        ComponenTestHandler.getInstance().YaxisRises();
    }

    private void handleDecline() {
        Log.d("Control", "执行下降操作");
        ComponenTestHandler.getInstance().YaxisDecline();
    }

    private void handleStop() {
        Log.d("Control", "执行停止操作");
        ComponenTestHandler.getInstance().YaxisStop();
    }

    private void handleReposition() {
        Log.d("Control", "执行复位操作");
        ComponenTestHandler.getInstance().YaxisReset();
    }
    @Override
    protected void initInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onTimeEnd() {

    }
}
