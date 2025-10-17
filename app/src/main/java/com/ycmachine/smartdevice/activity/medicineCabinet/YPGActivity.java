package com.ycmachine.smartdevice.activity.medicineCabinet;

import static com.ycmachine.smartdevice.constent.ClientConstant.REQUEST_CODE_SELECT_MEDIA;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;
import com.ycmachine.smartdevice.activity.cameraTest.CameraActivity;
import com.ycmachine.smartdevice.constent.ClientConstant;
import com.ycmachine.smartdevice.fragment.ComponentTestFragment;
import com.ycmachine.smartdevice.fragment.ControlModeFragment;
import com.ycmachine.smartdevice.fragment.PlanImageFragment;
import com.ycmachine.smartdevice.fragment.RealImageFragment;
import com.ycmachine.smartdevice.handler.ComponenTestHandler;
import com.ycmachine.smartdevice.handler.HeatParser;
import com.ycmachine.smartdevice.handler.InitMachineHandler;
import com.ycmachine.smartdevice.handler.MedHttpHandler;
import com.ycmachine.smartdevice.handler.YpgLogicHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import leesche.smartrecycling.base.BaseActivity;
import leesche.smartrecycling.base.HomeFragmentListener;
import leesche.smartrecycling.base.common.EventType;
import leesche.smartrecycling.base.eventbus.BasicMessageEvent;
import leesche.smartrecycling.base.handler.LocalConfigManager;
import leesche.smartrecycling.base.utils.LanguageUtil;
import leesche.smartrecycling.base.utils.SharedPreferencesUtils;
import leesche.smartrecycling.base.utils.StringUtil;
import leesche.smartrecycling.base.utils.UriToPathUtil;


public class YPGActivity extends BaseActivity implements HomeFragmentListener {

    // 定义高度（单位：dp）
    private int checkedHeight;  // 选中时高度：65dp
    private int uncheckedHeight;  // 未选中时高度：55dp

    Fragment componentTestFragment;
    Fragment controlModeFragment;
    Fragment topCurrentFragment;
    Fragment planImageFragment;
    Fragment realImageFragment;
    Fragment leftCurrentFragment;

    @BindView(R2.id.sendCmd)
    TextView sendCmd;

    @BindView(R2.id.recCmd)
    TextView recCmd;


    @BindView(R2.id.warehouse_status)
    public TextView warehouseStatus;

    @BindView(R2.id.dmzt_status)
    public TextView dmztStatus;

    @BindView(R2.id.Y_axis_lower_limit)
    public TextView YAxisLowerLimit;

    @BindView(R2.id.Y_axis_upper_limit)
    public TextView YAxisUpperLimit;


    @BindView(R2.id.language_text)
    TextView languageText;
    @BindView(R2.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R2.id.rb_control_mode)
    RadioButton rbControlMode;
    @BindView(R2.id.rb_component_test)
    RadioButton rbComponentTest;

    String[] LEFT_FRAGMENT_TAGS = new String[]{"planImageFragment", "realImageFragment"};
    String[] TOP_FRAGMENT_TAGS = new String[]{"componentTestFragment", "controlModeFragment"};

    private int nowLanIndex = 0; // 默认图层索引

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        nowLanIndex = (int) SharedPreferencesUtils.get(this, LanguageUtil.LAST_SET_LAN, 2);
        LanguageUtil.changeApplicationLan(this, nowLanIndex);

        super.onCreate(savedInstanceState);



        languageText.setText((nowLanIndex == 0) ? "简体中文" : "English");

//        HeatParser.getInstance().setContext(this);
//        HeatParser.getInstance().setWarehouseStatus(warehouseStatus);


        YpgLogicHandler.getInstance().init(this, sendCmd, recCmd);
        InitMachineHandler.init();

        initLeftFragment(savedInstanceState);
        showLeftFragment(planImageFragment);
        initTopFragment(savedInstanceState);
        showTopFragment(controlModeFragment);

    }

    @Override
    public int initLayout() {
        return R.layout.activity_ypg;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

//        // 将dp转换为像素（避免不同设备尺寸问题）
//        checkedHeight = dpToPx(65);
//        uncheckedHeight = dpToPx(55);
//
//
//        // 初始设置：默认选中的项设为65dp
//        setRadioButtonHeight(rbControlMode, checkedHeight);
//        setRadioButtonHeight(rbComponentTest, uncheckedHeight);
//
//        // 监听选中状态变化
//        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
//            if (checkedId == R.id.rb_control_mode) {
//                // 控制模式选中
//                setRadioButtonHeight(rbControlMode, checkedHeight);
//                setRadioButtonHeight(rbComponentTest, uncheckedHeight);
//            } else if (checkedId == R.id.rb_component_test) {
//                // 部件测试选中
//                setRadioButtonHeight(rbControlMode, uncheckedHeight);
//                setRadioButtonHeight(rbComponentTest, checkedHeight);
//            }
//        });


        LocalConfigManager.getInstance().loadAdDataFromLocal();
        if(!LocalConfigManager.getInstance().getIdleBannerList().isEmpty()){
            startActivity(new Intent(this, UserMedPointActivity.class));

        }
    }


    @Override
    public void initData() {

    }

    private void initLeftFragment(Bundle savedInstanceState) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // 1. 获取或创建Fragment实例（复用逻辑不变）
        if (savedInstanceState != null) {
            planImageFragment = fragmentManager.findFragmentByTag(LEFT_FRAGMENT_TAGS[0]);
            realImageFragment = fragmentManager.findFragmentByTag(LEFT_FRAGMENT_TAGS[1]);
            // 恢复当前显示的Fragment（逻辑不变）
            String currentTag = savedInstanceState.getString("LEFT_CURRENT_FRAGMENT_TAG");
            leftCurrentFragment = resolveCurrentFragment(currentTag, planImageFragment, realImageFragment);
        } else {
            // 首次启动：创建新Fragment
            planImageFragment = new PlanImageFragment();
            realImageFragment = new RealImageFragment();
            leftCurrentFragment = realImageFragment; // 默认显示realImageFragment
        }

        // 2. 首次添加时，在同一事务中完成“添加+隐藏+显示”
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (savedInstanceState == null) { // 仅首次添加时执行
            // 添加所有Fragment到容器
            ft.add(R.id.fl_left_root, planImageFragment, LEFT_FRAGMENT_TAGS[0])
                    .add(R.id.fl_left_root, realImageFragment, LEFT_FRAGMENT_TAGS[1]);

            // 隐藏非目标Fragment（关键：同一事务中隐藏）
            if (leftCurrentFragment == realImageFragment) {
                ft.hide(planImageFragment); // 隐藏planImage
            } else {
                ft.hide(realImageFragment); // 隐藏realImage
            }
            // 显示目标Fragment（同一事务中显示）
            ft.show(leftCurrentFragment);
        } else {
            // 非首次启动：只需确保显示目标，隐藏其他（通过showLeftFragment处理）
            showLeftFragment(leftCurrentFragment);
        }
        ft.commitNowAllowingStateLoss(); // 立即执行事务，避免延迟显示
    }

    // 辅助方法：根据tag解析当前Fragment
    private Fragment resolveCurrentFragment(String tag, Fragment... fragments) {
        if (tag == null) return fragments[0]; // 默认返回第一个
        for (Fragment f : fragments) {
            if (tag.equals(f.getTag())) return f;
        }
        return fragments[0];
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存“当前显示的左侧Fragment”标签（关键：用明确的键区分左侧/右侧Fragment）
        if (leftCurrentFragment != null) {
            outState.putString("LEFT_CURRENT_FRAGMENT_TAG", leftCurrentFragment.getTag());
        }
    }

    private void createNewLeftFragment() {
        planImageFragment = new PlanImageFragment();
        realImageFragment = new RealImageFragment();
    }

    private void showLeftFragment(Fragment targetFragment) {
        if (targetFragment == null || targetFragment == leftCurrentFragment) {
            return; // 目标为空或已显示，直接返回
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        // 隐藏当前显示的Fragment
        if (leftCurrentFragment != null) {
            ft.hide(leftCurrentFragment);
        }

        // 显示目标Fragment
        ft.show(targetFragment);

        // 提交事务（用commitAllowingStateLoss避免状态异常）
        ft.commitAllowingStateLoss();

        // 更新“当前显示的Fragment”引用
        leftCurrentFragment = targetFragment;
    }

    public void toSystem(View view) {

        startActivity(new Intent(Settings.ACTION_SETTINGS));
    }

    private void initTopFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            componentTestFragment = fragmentManager.findFragmentByTag(TOP_FRAGMENT_TAGS[0]);
            controlModeFragment = fragmentManager.findFragmentByTag(TOP_FRAGMENT_TAGS[1]);
            String curFragment = savedInstanceState.getString("lastVisibleFragment");
            if (curFragment != null) {
                switch (curFragment) {
                    case "componentTestFragment":
                        topCurrentFragment = componentTestFragment;
                        break;
                    case "controlModeFragment":
                        topCurrentFragment = controlModeFragment;
                        break;
                }
            } else {
                createNewTopFragment();
            }
        } else {
            createNewTopFragment();
        }
    }

    private void createNewTopFragment() {
        componentTestFragment = new ComponentTestFragment();
        controlModeFragment = new ControlModeFragment();
    }

    private void showTopFragment(Fragment fragment) {
        if (topCurrentFragment != fragment) { // 避免重复显示同一 Fragment
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (topCurrentFragment == null) { // 第一次显示 Fragment
                // 所有 Fragment 提前添加到容器中
                ft.add(R.id.fl_top_root, componentTestFragment, TOP_FRAGMENT_TAGS[0])
                        .add(R.id.fl_top_root, controlModeFragment, TOP_FRAGMENT_TAGS[1]);

                // 隐藏其他 Fragment
                ft.hide(componentTestFragment).hide(controlModeFragment);

                // 状态安全：使用 commitAllowingStateLoss() 防止在异常状态下提交事务导致崩溃
                ft.show(fragment).commitAllowingStateLoss();
            } else {
                ft.hide(topCurrentFragment).show(fragment).commitAllowingStateLoss();
            }
            topCurrentFragment = fragment;
        }
    }


//    @Override
//    public void onClick(View v) {
//
//
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void skipToFragment(int position) {
        if (position == ClientConstant.PageFlag.componentTestFragment) {
            showTopFragment(componentTestFragment);
        }
        if (position == ClientConstant.PageFlag.controlModeFragment) {
            showTopFragment(controlModeFragment);
        }
        if (position == ClientConstant.PageFlag.planImageFragment) {
            showLeftFragment(planImageFragment);
        }
        if (position == ClientConstant.PageFlag.realImageFragment) {
            showLeftFragment(realImageFragment);
        }
    }

    @Override
    public void onMessageEvent(BasicMessageEvent event) {
        switch (event.getMessage_id()) {
            case EventType.BasicEvent.HEAT_STATUS:

                warehouseStatus.setText(HeatParser.getInstance().isWarehouse() ? getString(R.string.off) : getString(R.string.on));
                dmztStatus.setText(HeatParser.getInstance().isQhmxxw() ? getString(R.string.open) : getString(R.string.close));

                YAxisLowerLimit.setText(HeatParser.getInstance().isSjjds() ? getString(R.string.on) : getString(R.string.off));
                YAxisUpperLimit.setText(HeatParser.getInstance().isDmzt() ? getString(R.string.on) : getString(R.string.off));
                break;
        }

    }

    @OnClick({R2.id.rb_real_image, R2.id.rb_plan_image, R2.id.rb_control_mode, R2.id.rb_component_test})
    public void onViewClick(View view) {

        if (view.getId() == R.id.rb_real_image) {

            showLeftFragment(realImageFragment);
        }
        if (view.getId() == R.id.rb_plan_image) {

            showLeftFragment(planImageFragment);
        }
        if (view.getId() == R.id.rb_control_mode) {
            ((ControlModeFragment) controlModeFragment).checkDefaultRadioButton();
            showTopFragment(controlModeFragment);
        }
        if (view.getId() == R.id.rb_component_test) {
            ((ComponentTestFragment) componentTestFragment).checkDefaultRadioButton();
            showTopFragment(componentTestFragment);
        }
    }

    public void clearCmd(View view) {
        sendCmd.setText("");
        recCmd.setText("");
        StringUtil.clearString();
    }

    public void toCameraActivity(View view) {

//        HeatParser.getInstance().snapDevice();
        startActivity(new Intent(this, CameraActivity.class));
    }

    public void changeLanguage(View view) {
        // 获取当前已保存的语言索引（默认0）
        int currentLanIndex = (int) SharedPreferencesUtils.get(this, LanguageUtil.LAST_SET_LAN, 2);

        // 计算要切换到的目标语言索引（在0和2之间切换）
        int targetLanIndex = (currentLanIndex == 0) ? 2 : 0;
        languageText.setText((currentLanIndex == 0) ? "简体中文" : "English");
        // 切换应用语言
        LanguageUtil.changeApplicationLan(this, targetLanIndex);

        // 保存新的语言索引
        SharedPreferencesUtils.put(this, LanguageUtil.LAST_SET_LAN, targetLanIndex);

        // 延迟重建Activity以应用语言变更
        recCmd.postDelayed(this::recreate, 800);
//        reBootApp(this);
    }

    public void oneClickTesting(View view) {
        // 一键测试

        YpgLogicHandler.getInstance().openAllGoodsChannel();

    }
    public void selectAD(View view) {
        openMediaSelector("*/*", true);
    }


    private void openMediaSelector(String mimeType, boolean multiSelect) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType(mimeType);
        // 如果需要同时支持图片和视频，可以使用下面的代码
        // intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});

        // 设置为多选模式
        if (multiSelect) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }

        // 允许访问文档
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, REQUEST_CODE_SELECT_MEDIA);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_MEDIA && resultCode == RESULT_OK && data != null) {
            List<String> selectedUris = new ArrayList<>();

            // 处理多选情况
            if (data.getClipData() != null) {
                // 假设你通过 ClipData 获取多选的文件 URI（之前的代码）
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    int count = clipData.getItemCount();
                    Logger.i("FileSelect", "已选择 " + count + " 个文件：");

                    for (int i = 0; i < count; i++) {
                        Uri contentUri = clipData.getItemAt(i).getUri();
                        // 调用工具类转换为真实路径
                        String realPath = UriToPathUtil.getRealPathFromUri(this, contentUri);

                        if (!TextUtils.isEmpty(realPath)) {
                            Logger.i("FileSelect. 真实路径：" + realPath);
                            selectedUris.add(realPath);
                        } else {
                            Logger.e("FileSelect. 路径转换失败" + realPath + "，Content URI：" + contentUri);
                        }
                    }
                }
//                int count = data.getClipData().getItemCount();
//                for (int i = 0; i < count; i++) {
//                    Uri uri = data.getClipData().getItemAt(i).getUri();
//                    selectedUris.add(uri);
//                }
            }
            // 处理单选情况
            else if (data.getData() != null) {
                Uri contentUri = data.getData();
                // 调用工具类转换为真实路径
                String realPath = UriToPathUtil.getRealPathFromUri(this, contentUri);

                if (!TextUtils.isEmpty(realPath)) {
                    Logger.i("FileSelect", ". 真实路径：" + realPath);
                    selectedUris.add(realPath);
                } else {
                    Logger.e("FileSelect", ". 路径转换失败，Content URI：" + contentUri);
                }
            }


            JsonArray jsonArray = new JsonArray();
            for (int i = 0; i < selectedUris.size(); i++) {
                // 创建JSONObject并设置属性
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", i);
                jsonObject.addProperty("module", "app-screen");
                jsonObject.addProperty("localCache", selectedUris.get(i));
                // 将JSONObject添加到JSONArray
                jsonArray.add(jsonObject);
            }
            LocalConfigManager.getInstance().parseAdJsonToDisplay(jsonArray, true);


            Logger.i("111" + JSON.toJSONString(LocalConfigManager.getInstance().getIdleBannerList()));

            // 显示选择结果
            showSelectedResult(selectedUris);
        }
    }
    /**
     * 显示选中的媒体文件信息
     */
    private void showSelectedResult(List<String> uris) {
        if (uris.isEmpty()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("已选择 ").append(uris.size()).append(" 个文件：\n\n");

        for (int i = 0; i < uris.size(); i++) {
            sb.append(i + 1).append(". ").append(uris.get(i)).append("\n");
        }

        Logger.i(sb.toString());
        Toast.makeText(this, "已选择 " + uris.size() + " 个文件", Toast.LENGTH_SHORT).show();
    }
    public void toUserPage(View view) {
        startActivity(new Intent(this, UserMedPointActivity.class));
    }


    public static void reBootApp(Context context) {
        // 重启app
        Intent intent = new Intent(context, YPGActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    // 动态设置RadioButton的高度
    private void setRadioButtonHeight(RadioButton radioButton, int height) {
        RadioGroup.LayoutParams params = (RadioGroup.LayoutParams) radioButton.getLayoutParams();
        params.height = height;
        radioButton.setLayoutParams(params);
    }

    // dp转px工具方法
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    public void selfCheck(View view) {

        Toast.makeText(
                this,  // 获取按钮所在的上下文
                "状态自检成功",  // 提示消息（替换为你的message）
                Toast.LENGTH_SHORT
        ).show();  // 显示弹窗
        // 一键自检
        ComponenTestHandler.getInstance().selfCheck();

    }

    public void MedPoint(View view){

        Toast.makeText(
                this,  // 获取按钮所在的上下文
                "Successfully sent the request to register the machine",  // 提示消息（替换为你的message）
                Toast.LENGTH_SHORT
        ).show();  // 显示弹窗
        MedHttpHandler.getInstance().registerMachine();
    }
}
