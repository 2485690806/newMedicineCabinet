package com.ycmachine.smartdevice.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;
import com.ycmachine.smartdevice.constent.ClientConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import leesche.smartrecycling.base.BaseFragment;
import leesche.smartrecycling.base.utils.RxTimer;

public class ControlModeFragment  extends BaseFragment implements RxTimer.OnTimeCounterListener{

    @BindView(R2.id.rb_forward)
     RadioButton rbForward;   // 正转

    @BindView(R2.id.rb_backward)
     RadioButton rbBackward;  // 反转

    @BindView(R2.id.rb_stop)
     RadioButton rbStop;      // 停止

    @BindView(R2.id.rb_standard)
     RadioButton rbStandard;  // 标准取货层出货模式

    @BindView(R2.id.rb_recycle)
     RadioButton rbRecycle;   // 回收层出货模式

    private List<RadioButton> allRadioButtons = new ArrayList<>();
    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_control_mode;
    }

    @Override
    protected void initView(View mRoot) {

        // 将所有选项添加到列表
        allRadioButtons.add(rbForward);
        allRadioButtons.add(rbBackward);
        allRadioButtons.add(rbStop);
        allRadioButtons.add(rbStandard);
        allRadioButtons.add(rbRecycle);


        // 设置全局单选监听器：点击任意选项时，仅选中当前选项，取消其他所有选项
        View.OnClickListener globalListener = v -> {


            RadioButton clickedRb = (RadioButton) v;
            if(ClientConstant.IS_DOING){
                clickedRb.setChecked(false);
                Toast.makeText(
                        clickedRb.getContext(),  // 获取按钮所在的上下文
                        "操作正在执行中，请稍后再试",  // 提示消息（替换为你的message）
                        Toast.LENGTH_SHORT
                ).show();  // 显示弹窗

                ClientConstant.IS_DOING = false;
                return;
            }

            // 遍历所有选项，仅选中当前点击的按钮
            for (RadioButton rb : allRadioButtons) {
                rb.setChecked(rb == clickedRb);
            }

            // 判断点击的是哪个RadioButton（通过id）
            int clickedId = clickedRb.getId();
            if (clickedId == R.id.rb_forward) {
                // 点击了“正转”
                Logger.d("点击了正转");
                handleForward();
            } else if (clickedId == R.id.rb_backward) {
                // 点击了“反转”
                Logger.d("点击了反转");
                handleBackward();
            } else if (clickedId == R.id.rb_stop) {
                // 点击了“停止”
                Logger.d("点击了停止");
                handleStop();
            } else if (clickedId == R.id.rb_standard) {
                // 点击了“标准取货层”
                Logger.d("点击了标准取货层出货模式");
                handleStandard();
            } else if (clickedId == R.id.rb_recycle) {
                // 点击了“回收层”
                Logger.d("点击了回收层出货模式");
                handleRecycle();
            }
        };

        // 为所有选项绑定全局监听器
        for (RadioButton rb : allRadioButtons) {
            rb.setOnClickListener(globalListener);
        }
    }
    public void checkDefaultRadioButton() {
        // 默认选中第一个按钮
        if (!allRadioButtons.isEmpty()) {
            for (RadioButton rb : allRadioButtons) {
                rb.setChecked(false);
            }
            allRadioButtons.get(0).setChecked(true);
            handleForward(); // 默认选中正转
        }
    }

    @Override
    protected void initInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onTimeEnd() {

    }


    // 对应的处理方法
    private void handleForward() {
        /* 正转逻辑 */
        ClientConstant.currentWorkFlow = ClientConstant.WorkFlow.Forward;
    }
    private void handleBackward() {
        /* 反转逻辑 */
        ClientConstant.currentWorkFlow = ClientConstant.WorkFlow.Backward;
    }
    private void handleStop() {
        /* 停止逻辑 */
        ClientConstant.currentWorkFlow = ClientConstant.WorkFlow.Stop;
    }
    private void handleStandard() {
        /* 标准取货层逻辑 */
        ClientConstant.currentWorkFlow = ClientConstant.WorkFlow.Standard;
    }
    private void handleRecycle() {
        /* 回收层逻辑 */
        ClientConstant.currentWorkFlow = ClientConstant.WorkFlow.Recycle;
    }

}
