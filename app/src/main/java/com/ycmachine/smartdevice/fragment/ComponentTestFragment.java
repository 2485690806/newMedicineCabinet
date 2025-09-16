package com.ycmachine.smartdevice.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;
import com.ycmachine.smartdevice.constent.ClientConstant;
import com.ycmachine.smartdevice.fragment.componentTest.ComponentTestNumberFragment;
import com.ycmachine.smartdevice.fragment.componentTest.ComponentTestRightFragment;
import com.ycmachine.smartdevice.fragment.componentTest.LayerTestFragment;
import com.ycmachine.smartdevice.fragment.componentTest.LiftOperationFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import leesche.smartrecycling.base.BaseFragment;
import leesche.smartrecycling.base.HomeFragmentListener;
import leesche.smartrecycling.base.utils.RxTimer;

public class ComponentTestFragment extends BaseFragment implements RxTimer.OnTimeCounterListener, HomeFragmentListener {

    // 子Fragment实例
    private Fragment componentTestNumberFragment;
    private Fragment componentTestRightFragment;
    private Fragment layerTestFragment;
    private Fragment liftOperationFragment;
    private Fragment currentChildFragment; // 当前显示的子Fragment

    // 子Fragment的标签（用于状态恢复）
    private final String[] CHILD_FRAGMENT_TAGS = {
            "componentTestNumberFragment",
            "componentTestRightFragment",
            "layerTestFragment",
            "liftOperationFragment",
    };

    // 使用@BindView注解获取所有RadioButton
    @BindView(R2.id.rb_lift)
    RadioButton rbLift;         // 升降机

    @BindView(R2.id.rb_x_axis)
    RadioButton rbXAxis;        // X轴

    @BindView(R2.id.rb_upper_door)
    RadioButton rbUpperDoor;    // 上侧门

    @BindView(R2.id.rb_lower_door)
    RadioButton rbLowerDoor;    // 下侧门

    @BindView(R2.id.rb_recycle_door)
    RadioButton rbRecycleDoor;  // 回收门

    @BindView(R2.id.rb_pick_door)
    RadioButton rbPickDoor;     // 取货门

    @BindView(R2.id.rb_single_test)
    RadioButton rbSingleTest;   // 单层测试

    @BindView(R2.id.rb_layer_setting)
    RadioButton rbLayerSetting; // 层数设置


    // 存储所有RadioButton的列表
    private List<RadioButton> allRadioButtons = new ArrayList<>();


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_machine_test; // 包含子Fragment容器fl_child_container
    }

    @Override
    protected void initView(View mRoot) {
        initRadioButtons();
        setRadioButtonListeners();
    }

    @Override
    protected void initInstanceState(Bundle savedInstanceState) {
        // 初始化子Fragment（在View创建后）
        initChildFragments(savedInstanceState); // 传入savedInstanceState用于状态恢复
    }


    // 初始化所有RadioButton到列表中
    private void initRadioButtons() {
        allRadioButtons.add(rbLift);         // 升降机
        allRadioButtons.add(rbXAxis);        // X轴
        allRadioButtons.add(rbUpperDoor);    // 上侧门
        allRadioButtons.add(rbLowerDoor);    // 下侧门
        allRadioButtons.add(rbRecycleDoor);  // 回收门
        allRadioButtons.add(rbPickDoor);     // 取货门
        allRadioButtons.add(rbSingleTest);   // 单层测试
        allRadioButtons.add(rbLayerSetting); // 层数设置
    }
    public void checkDefaultRadioButton() {
        // 设置默认选中第一个按钮
        if (!allRadioButtons.isEmpty()) {
            allRadioButtons.get(0).setChecked(true);
            for (RadioButton rb : allRadioButtons) {
                rb.setChecked(false);
            }
            handleRadioChecked(allRadioButtons.get(0)); // 处理默认选中逻辑
        }
    }

    // 设置点击事件，实现单选效果
    private void setRadioButtonListeners() {
        View.OnClickListener listener = v -> {
            RadioButton clickedRb = (RadioButton) v;
            for (RadioButton rb : allRadioButtons) {
                rb.setChecked(rb == clickedRb);
            }
            // 处理选中逻辑
            handleRadioChecked(clickedRb);
        };

        for (RadioButton rb : allRadioButtons) {
            rb.setOnClickListener(listener);
        }
    }

    // 处理选中后的业务逻辑
    private void handleRadioChecked(RadioButton checkedRb) {
        // 获取当前选中按钮的ID
        int checkedId = checkedRb.getId();
        checkedRb.setChecked(true); // 确保选中状态
        // 根据ID匹配不同按钮，执行对应逻辑
        switch (checkedId) {
            case R2.id.rb_lift:
                // 升降机按钮逻辑
                handleLiftOperation();
                break;
            case R2.id.rb_x_axis:
                // X轴按钮逻辑
                handleXAxisOperation();
                break;
            case R2.id.rb_upper_door:
                // 上侧门按钮逻辑
                handleUpperDoorOperation();
                break;
            case R2.id.rb_lower_door:
                // 下侧门按钮逻辑
                handleLowerDoorOperation();
                break;
            case R2.id.rb_recycle_door:
                // 回收门按钮逻辑
                handleRecycleDoorOperation();
                break;
            case R2.id.rb_pick_door:
                // 取货门按钮逻辑
                handlePickDoorOperation();
                break;
            case R2.id.rb_single_test:
                // 单层测试按钮逻辑
                handleSingleTestOperation();
                break;
            case R2.id.rb_layer_setting:
                // 层数设置按钮逻辑
                handleLayerSettingOperation();
                break;
            default:
                // 未知按钮，可做默认处理或忽略
                break;
        }
    }

    // 以下是各按钮对应的处理方法（示例）
    private void handleLiftOperation() {
        // 升降机操作逻辑
        // 例如：发送控制指令、更新UI等
        showChildFragment(liftOperationFragment);
        ClientConstant.currentWorkFlow = ClientConstant.WorkFlow.LiftHandler;
    }

    private void handleXAxisOperation() {
        // X轴操作逻辑
        showChildFragment(componentTestRightFragment);
        ClientConstant.currentWorkFlow = ClientConstant.WorkFlow.XAxis;
    }

    private void handleUpperDoorOperation() {
        // 上侧门操作逻辑
        showChildFragment(componentTestRightFragment);
        ClientConstant.currentWorkFlow = ClientConstant.WorkFlow.UpperSideDoor;
    }

    private void handleLowerDoorOperation() {
        // 下侧门操作逻辑
        showChildFragment(componentTestRightFragment);
        ClientConstant.currentWorkFlow = ClientConstant.WorkFlow.LowerSideDoor;
    }

    private void handleRecycleDoorOperation() {
        // 回收门操作逻辑
        showChildFragment(componentTestRightFragment);
        ClientConstant.currentWorkFlow = ClientConstant.WorkFlow.RecyclingDoor;
    }

    private void handlePickDoorOperation() {
        // 取货门操作逻辑
        showChildFragment(componentTestRightFragment);
        ClientConstant.currentWorkFlow = ClientConstant.WorkFlow.PickUpDoor;
    }

    private void handleSingleTestOperation() {
        // 单层测试操作逻辑
        showChildFragment(componentTestNumberFragment);
        ClientConstant.currentWorkFlow = ClientConstant.WorkFlow.SingleLayerTesting;
    }

    private void handleLayerSettingOperation() {
        // 层数设置操作逻辑
        showChildFragment(layerTestFragment);
        ClientConstant.currentWorkFlow = ClientConstant.WorkFlow.LayerSetting;
    }


    /**
     * 初始化子Fragment（关键：使用getChildFragmentManager()）
     */
    private void initChildFragments(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // 从保存的状态中恢复子Fragment（使用getChildFragmentManager()）
            componentTestNumberFragment = getChildFragmentManager().findFragmentByTag(CHILD_FRAGMENT_TAGS[0]);
            componentTestRightFragment = getChildFragmentManager().findFragmentByTag(CHILD_FRAGMENT_TAGS[1]);
            layerTestFragment = getChildFragmentManager().findFragmentByTag(CHILD_FRAGMENT_TAGS[2]);
            liftOperationFragment = getChildFragmentManager().findFragmentByTag(CHILD_FRAGMENT_TAGS[3]);

            // 恢复上次显示的子Fragment
            String lastChildFragment = savedInstanceState.getString("lastChildFragment");
            if (lastChildFragment != null) {
                switch (lastChildFragment) {
                    case "componentTestNumberFragment":
                        currentChildFragment = componentTestNumberFragment;
                        break;
                    case "componentTestRightFragment":
                        currentChildFragment = componentTestRightFragment;
                        break;
                    case "layerTestFragment":
                        currentChildFragment = layerTestFragment;
                        break;
                    case "liftOperationFragment":
                        currentChildFragment = liftOperationFragment;
                        break;
                }
            } else {
                createChildFragments();
                showChildFragment(liftOperationFragment); // 默认显示第一个子Fragment
            }
        } else {
            // 首次创建：初始化子Fragment并显示默认的
            createChildFragments();
            showChildFragment(liftOperationFragment);
        }
    }

    /**
     * 创建子Fragment实例
     */
    private void createChildFragments() {
        componentTestNumberFragment = new ComponentTestNumberFragment(); // 子Fragment1
        componentTestRightFragment = new ComponentTestRightFragment();   // 子Fragment2
        layerTestFragment = new LayerTestFragment();   // 子Fragment2
        liftOperationFragment = new LiftOperationFragment();   // 子Fragment2
    }

    /**
     * 显示指定的子Fragment（关键：使用getChildFragmentManager()管理事务）
     */
    private void showChildFragment(Fragment targetFragment) {
        if (targetFragment == null || targetFragment.equals(currentChildFragment)) {
            return; // 避免空指针或重复显示
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        // 如果子Fragment未添加过，先添加到容器
        if (!targetFragment.isAdded()) {
            transaction.add(R.id.fl_child_container, targetFragment,
                    getTagForFragment(targetFragment));
        }

        // 隐藏当前显示的子Fragment，显示目标子Fragment
        if (currentChildFragment != null) {
            transaction.hide(currentChildFragment);
        }
        transaction.show(targetFragment);

        // 提交事务（状态不安全时用commitAllowingStateLoss()）
        transaction.commitAllowingStateLoss();

        // 更新当前子Fragment引用
        currentChildFragment = targetFragment;
    }

    /**
     * 根据子Fragment实例获取对应的标签
     */
    private String getTagForFragment(Fragment fragment) {
        if (fragment instanceof ComponentTestNumberFragment) {
            return CHILD_FRAGMENT_TAGS[0];
        } else if (fragment instanceof ComponentTestRightFragment) {
            return CHILD_FRAGMENT_TAGS[1];
        } else if (fragment instanceof LayerTestFragment) {
            return CHILD_FRAGMENT_TAGS[2];
        }
        return "";
    }

    /**
     * 保存子Fragment状态（在父Fragment销毁前调用）
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前显示的子Fragment标签
        if (currentChildFragment != null) {
            outState.putString("lastChildFragment", getTagForFragment(currentChildFragment));
        }
    }

    // 示例：切换子Fragment的方法（可在按钮点击等事件中调用）
    public void switchToRightFragment() {
        showChildFragment(componentTestRightFragment);
    }

    @Override
    public void onTimeEnd() {
        // 定时器结束回调
    }

    @Override
    public void skipToFragment(int position) {
        // 实现接口方法，可用于从外部切换子Fragment
        if (position == 0) {
            showChildFragment(componentTestNumberFragment);
        } else if (position == 1) {
            showChildFragment(componentTestRightFragment);
        }
    }
}
