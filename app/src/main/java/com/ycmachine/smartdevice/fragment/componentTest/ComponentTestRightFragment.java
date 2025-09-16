package com.ycmachine.smartdevice.fragment.componentTest;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;
import com.ycmachine.smartdevice.constent.ClientConstant;
import com.ycmachine.smartdevice.handler.HeatParser;
import com.ycmachine.smartdevice.handler.YpgLogicHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import leesche.smartrecycling.base.BaseFragment;
import leesche.smartrecycling.base.common.EventType;
import leesche.smartrecycling.base.eventbus.BasicMessageEvent;
import leesche.smartrecycling.base.utils.RxTimer;

public class ComponentTestRightFragment extends BaseFragment implements RxTimer.OnTimeCounterListener {
    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_machine_test_right;
    }

    private List<RadioButton> directionRadioButtons = new ArrayList<>();


    @BindView(R2.id.rb_reverse)
    RadioButton rbReverse;

    @BindView(R2.id.rb_forward)
    RadioButton rbForward;

    @BindView(R2.id.rb_stop)
    RadioButton rbStop;

    @BindView(R2.id.tv_upper_door_open)
    TextView tvUpperDoorOpen;

    @BindView(R2.id.tv_upper_door_open_status)
    TextView tvUpperDoorOpenStatus;

    @BindView(R2.id.tv_upper_door_close)
    TextView tvUpperDoorClose;

    @BindView(R2.id.tv_upper_door_close_status)
    TextView tvUpperDoorCloseStatus;

    @Override
    protected void initView(View mRoot) {
        initDirectionRadioButtons();
    }

    @Override
    public void onMessageEvent(BasicMessageEvent event) {
        switch (event.getMessage_id()) {
            case EventType.BasicEvent.HEAT_STATUS:

//                warehouseStatus.setText(HeatParser.getInstance().isWarehouse() ? "开启" : "关闭");
//                dmztStatus.setText(HeatParser.getInstance().isQhmxxw() ? "开启" : "关闭");
                break;
        }

    }

    public void initData() {
        switch (ClientConstant.currentWorkFlow) {
            case XAxis:
                tvUpperDoorOpen.setText("X轴落货光眼");
                tvUpperDoorOpenStatus.setText(HeatParser.getInstance().isXzhouGuanYan() ? getString(R.string.on) : getString(R.string.off));

                tvUpperDoorClose.setVisibility(View.GONE);
                tvUpperDoorCloseStatus.setVisibility(View.GONE);
                break;
            case UpperSideDoor:

                tvUpperDoorClose.setVisibility(View.VISIBLE);
                tvUpperDoorClose.setText("上侧门关闭限位");
                tvUpperDoorCloseStatus.setVisibility(View.VISIBLE);
                tvUpperDoorCloseStatus.setText(HeatParser.getInstance().isScmxxw() ? getString(R.string.on) : getString(R.string.off));

                tvUpperDoorOpen.setText("上侧门打开限位");
                tvUpperDoorOpenStatus.setText(HeatParser.getInstance().isScmsxw() ? getString(R.string.on) : getString(R.string.off));
                break;
            case LowerSideDoor:

                tvUpperDoorClose.setVisibility(View.VISIBLE);
                tvUpperDoorClose.setText("下侧门关闭限位");
                tvUpperDoorCloseStatus.setVisibility(View.VISIBLE);
                tvUpperDoorCloseStatus.setText(HeatParser.getInstance().isXcmxxw() ? getString(R.string.on) : getString(R.string.off));

                tvUpperDoorOpen.setText("下侧门打开限位");
                tvUpperDoorOpenStatus.setText(HeatParser.getInstance().isXcmsxw() ? getString(R.string.on) : getString(R.string.off));


                break;
            case RecyclingDoor:

                tvUpperDoorClose.setVisibility(View.VISIBLE);
                tvUpperDoorClose.setText("回收门关闭限位");
                tvUpperDoorCloseStatus.setVisibility(View.VISIBLE);
                tvUpperDoorCloseStatus.setText(HeatParser.getInstance().isHsmgbxw() ? getString(R.string.on) : getString(R.string.off));

                tvUpperDoorOpen.setText("回收门打开限位");
                tvUpperDoorOpenStatus.setText(HeatParser.getInstance().isHsmdkxw() ? getString(R.string.on) : getString(R.string.off));


                break;
            case PickUpDoor:

                tvUpperDoorClose.setVisibility(View.VISIBLE);
                tvUpperDoorClose.setText("取货门关闭限位");
                tvUpperDoorCloseStatus.setVisibility(View.VISIBLE);
                tvUpperDoorCloseStatus.setText(HeatParser.getInstance().isQhmxxw() ? getString(R.string.on) : getString(R.string.off));

                tvUpperDoorOpen.setText("取货门打开限位");
                tvUpperDoorOpenStatus.setText(HeatParser.getInstance().isQhmsxw() ? getString(R.string.on) : getString(R.string.off));

                break;
            default:
                break;

        }

    }

    private void initDirectionRadioButtons() {
        directionRadioButtons.add(rbForward); // 正转
        directionRadioButtons.add(rbReverse); // 反转
        directionRadioButtons.add(rbStop); // 停止（注意：如果与其他模块的 rbStop 重名，需区分变量名）

        View.OnClickListener directionListener = v -> {
            RadioButton clickedRb = (RadioButton) v;
            if (ClientConstant.IS_DOING) {
                clickedRb.setChecked(false);
                Toast.makeText(
                        clickedRb.getContext(),
                        "操作正在执行中，请稍后再试",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }
            for (RadioButton rb : directionRadioButtons) {
                rb.setChecked(rb == clickedRb);
            }

            handleDirectionClick(clickedRb.getId());
        };

        for (RadioButton rb : directionRadioButtons) {
            rb.setOnClickListener(directionListener);
        }
    }

    private void handleDirectionClick(int clickedId) {
        if (clickedId == R.id.rb_forward) {
            Logger.d("点击了正转");
            startForwardRotation();
        } else if (clickedId == R.id.rb_reverse) {
            Logger.d("点击了反转");
            startReverseRotation();
        } else if (clickedId == R.id.rb_stop) {
            Logger.d("点击了停止");
            stopRotation();
        }
    }

    // 正转操作具体实现
    private void startForwardRotation() {
        YpgLogicHandler.getInstance().handleLayerOperation(1, 0);
    }

    // 反转操作具体实现
    private void startReverseRotation() {
        YpgLogicHandler.getInstance().handleLayerOperation(2, 0);
    }

    // 停止操作具体实现
    private void stopRotation() {
        YpgLogicHandler.getInstance().handleLayerOperation(3, 0);
    }

    @Override
    protected void initInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onTimeEnd() {

    }
}
