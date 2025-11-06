package com.ycmachine.smartdevice.activity.cameraTest;


import static com.ycmachine.smartdevice.manager.GridRegionManager.imageLevelMapLayer;
import static com.ycmachine.smartdevice.manager.GridRegionManager.imageNameLevelMapLayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.leesche.logger.Logger;
import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;
import com.ycmachine.smartdevice.constent.ClientConstant;
import com.ycmachine.smartdevice.creator.LayerViewCreator;
import com.ycmachine.smartdevice.entity.ypg.LayerParam;
import com.ycmachine.smartdevice.handler.GridConfigHandler;
import com.ycmachine.smartdevice.manager.CabinetQrManager;
import com.ycmachine.smartdevice.manager.GridImageBoxView;
import com.ycmachine.smartdevice.manager.GridRegionManager;
import com.ycmachine.smartdevice.manager.RadioButtonManager;

import java.io.File;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import leesche.smartrecycling.base.BaseActivity;
import leesche.smartrecycling.base.HomeFragmentListener;
import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.entity.GridRegion;
import leesche.smartrecycling.base.qrcode.QrCodeScanner;
import leesche.smartrecycling.base.utils.FileUtil;
import leesche.smartrecycling.base.utils.RxTimer;

public class   ImageRecognitionActivity extends BaseActivity implements RxTimer.OnTimeCounterListener,
        HomeFragmentListener{

    @BindView(R2.id.radio_group_layers)
    RadioGroup radioGroupLayers;


    @BindView(R2.id.grid_left)
    GridImageBoxView mGridLeft; // 左图（左摄像头）

    @BindView(R2.id.grid_right)
    GridImageBoxView mGridRight; // 右图（右摄像头）

    @BindView(R2.id.x_et)
    EditText xEt;
    @BindView(R2.id.y_et)
    EditText yEt;
    @BindView(R2.id.h_et)
    EditText hEt;
    @BindView(R2.id.w_et)
    EditText wEt;

    @BindView(R2.id.ll_save_setting)
    LinearLayout llSaveSetting;

    @BindView(R2.id.ll_confirm_recognition)
    LinearLayout llConfirmRecognition;


    @BindView(R2.id.layer_num)
    TextView layerNum;

    @BindView(R2.id.goods_num)
    TextView goodsNum;







    public void backToActivity(View view) {
        finish();
    }

    @Override
    public int initLayout() {
        return R.layout.activity_image_recognition;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

    }


    @Override
    protected void onResume() {
        super.onResume();

// 1. 初始化组件
        LayerViewCreator viewCreator = new LayerViewCreator(this); // 视图创建器


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



                    GridRegion leftRegions = GridRegionManager.getInstance().getGridRegionByCode(layerNumber, 1, buttonNumber);
                    GridRegion rightRegions = GridRegionManager.getInstance().getGridRegionByCode(layerNumber, 2, buttonNumber);

                    Logger.d("选中的层数: " + layerNumber + ", 数字: " + buttonNumber +"regions" +leftRegions.gridNumber);
                    layerNum.setText(buttonNumber +getString(R.string.number));

                    // 3. 加载左右摄像头的图片（根据层号和摄像头编号获取图片路径）
                    loadImageForView(mGridLeft, layerNumber, 1);
                    loadImageForView(mGridRight, layerNumber, 2);
                    mGridLeft.clearAllGridRegions();
                    mGridRight.clearAllGridRegions();

                    if(leftRegions.getCameraNum()== 1){
                        mGridLeft.setSelectedGridRegion(leftRegions);
                        mGridLeft.bindEditTexts(xEt, yEt, wEt, hEt);
                        mGridLeft.bindButtons(llConfirmRecognition, llSaveSetting);
                        mGridLeft.setOnButtonClickListener(new GridImageBoxView.OnButtonClickListener() {
                            @Override
                            public void onConfirmRecognitionClick(GridRegion selectedRegion) {
                                // 你的识别逻辑（传入当前选中的货道框）
                                Logger.i("mGridLeft" + JSON.toJSONString(selectedRegion));

                                Bitmap croppedBitmap = mGridLeft.getCroppedBitmapInRegion(selectedRegion);

                                snapSelectGridRegion(croppedBitmap);

                            }

                            @Override
                            public void onSaveSettingClick(int x, int y, int width, int height,GridRegion region) {
                                // 你的保存逻辑（传入原始坐标的x/y/w/h）
                                Logger.i("mGridLeft"+x +"-" +y+"-" + width +"-" +height);
                                List<GridRegion> gridRegionsByLevel = GridRegionManager.getInstance().getGridRegionsByLevel(layerNumber, region.getCameraNum());
                                for (GridRegion gridRegion : gridRegionsByLevel){
                                    if(Objects.equals(gridRegion.getGridNumber(), region.getGridNumber())){

                                        gridRegion.setX(x);
                                        gridRegion.setY(y);
                                        gridRegion.setWidth(width);
                                        gridRegion.setHeight(height);
                                    }
                                }
                                GridConfigHandler.saveGridConfig(imageLevelMapLayer.get(layerNumber), region.getCameraNum(), gridRegionsByLevel);
                                Toast.makeText(ImageRecognitionActivity.this, "货道配置保存成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        mGridRight.setSelectedGridRegion(rightRegions);
                        mGridRight.bindEditTexts(xEt, yEt, wEt, hEt);
                        mGridRight.bindButtons(llConfirmRecognition, llSaveSetting);
                        mGridRight.setOnButtonClickListener(new GridImageBoxView.OnButtonClickListener() {
                            @Override
                            public void onConfirmRecognitionClick(GridRegion selectedRegion) {
                                // 你的识别逻辑（传入当前选中的货道框）
                                Logger.i("mGridRight" + JSON.toJSONString(selectedRegion));

                                Bitmap croppedBitmap = mGridRight.getCroppedBitmapInRegion(selectedRegion);

                                snapSelectGridRegion(croppedBitmap);
                            }

                            @Override
                            public void onSaveSettingClick(int x, int y, int width, int height,GridRegion region) {
                                Logger.i("mGridRight"+x +"-" +y+"-" + width +"-" +height + "region" + JSON.toJSONString(region));
                                List<GridRegion> gridRegionsByLevel = GridRegionManager.getInstance().getGridRegionsByLevel(layerNumber, region.getCameraNum());
                                for (GridRegion gridRegion : gridRegionsByLevel){
                                    if(Objects.equals(gridRegion.getGridNumber(), region.getGridNumber())){

                                        gridRegion.setX(x);
                                        gridRegion.setY(y);
                                        gridRegion.setWidth(width);
                                        gridRegion.setHeight(height);
                                    }
                                }
                                GridConfigHandler.saveGridConfig(imageLevelMapLayer.get(layerNumber), region.getCameraNum(), gridRegionsByLevel);
                                Toast.makeText(ImageRecognitionActivity.this, "货道配置保存成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    ClientConstant.IS_DOING = false;

                }
            }

            @Override
            public void onTextViewButtonClicked(int currentLayer) {
                // 点击 文字
                // 2. 获取该层左右摄像头的货道数据
                List<GridRegion> leftRegions = GridRegionManager.getInstance()
                        .getGridRegionsByLevel(currentLayer, 1); // 左摄像头（1）
                List<GridRegion> rightRegions = GridRegionManager.getInstance()
                        .getGridRegionsByLevel(currentLayer, 2); // 右摄像头（2）

                mGridLeft.clearAllGridRegions();
                mGridRight.clearAllGridRegions();
                Logger.d("选中的层数: " + currentLayer);

                // 3. 加载左右摄像头的图片（根据层号和摄像头编号获取图片路径）
                loadImageForView(mGridLeft, currentLayer, 1);
                loadImageForView(mGridRight, currentLayer, 2);

                // 4. 绘制所有货道的框
                mGridLeft.setAllGridRegions(leftRegions);
                mGridRight.setAllGridRegions(rightRegions);
                ClientConstant.IS_DOING = false;
            }
        });


    }

    private void snapSelectGridRegion(Bitmap croppedBitmap) {
        if (croppedBitmap == null) {
            Toast.makeText(ImageRecognitionActivity.this, "无法获取货道内图片，请重试", Toast.LENGTH_SHORT).show();
            return;
        }
        // （如果识别方法需要文件路径，可先将croppedBitmap保存为临时文件）
        new Thread(() -> {
            // 示例：如果识别方法需要路径，先保存裁剪后的Bitmap到本地
            String tempImagePath = FileUtil.saveBitmapToTempFile(croppedBitmap,"crop_temp_");
            if (tempImagePath == null) {
                runOnUiThread(() -> Toast.makeText(ImageRecognitionActivity.this, "保存裁剪图片失败", Toast.LENGTH_SHORT).show());
                return;
            }

            // 调用识别方法（传入货道框内的图片路径）
            List<String> results = QrCodeScanner.scan(tempImagePath);

            // 3. 处理识别结果（切换到主线程更新UI）
            runOnUiThread(() -> {
                if (results != null && !results.isEmpty()) {
                    Toast.makeText(ImageRecognitionActivity.this, "识别成功：" + results.toString(), Toast.LENGTH_SHORT).show();
                    StringBuilder sb = new StringBuilder();
                    if (results != null && !results.isEmpty()) {
                        for (String s : results) {
                            sb.append(s).append(","); // 每个元素后加逗号
                        }
                        // 删除最后一个多余的逗号
                        sb.deleteCharAt(sb.length() - 1);
                    }

                    String joined = sb.toString();

                    goodsNum.setText(joined +getString(R.string.number));

                } else {
                    Toast.makeText(ImageRecognitionActivity.this, "货道内未识别到内容", Toast.LENGTH_SHORT).show();
                }

                // 及时回收裁剪后的Bitmap，避免内存泄漏
                if (croppedBitmap != null && !croppedBitmap.isRecycled()) {
                    croppedBitmap.recycle();
                }
            });
        }).start();
    }

    CabinetQrManager.OnProcessListener QrListener = new CabinetQrManager.OnProcessListener() {
        @Override
        public void onSuccess() {
            // 处理成功的回调
            System.out.println("Binding success: ");
        }

        @Override
        public void onError(Exception e) {
            // 处理失败的回调
            System.out.println("Binding failed: " + e);
        }
    };

    @Override
    public void initData() {

    }

    @Override
    public void skipToFragment(int position) {

    }

    @Override
    public void onTimeEnd() {

    }




    // 工具方法：根据层号和摄像头编号加载图片
    private void loadImageForView(GridImageBoxView view, int currentFloor, int currentCameraNum) {
        // 实际项目中：根据level和cameraNum获取图片路径（本地/网络）

        File baseDir = new File(Constants.IMAGE_FILE);


        File latestImageFile = FileUtil.getLatestImageFile(baseDir, currentCameraNum, imageNameLevelMapLayer.get(currentFloor) );

// 加载图片到自定义View（GridImageBoxView）
        if (latestImageFile != null && latestImageFile.exists()) {
            // 使用Glide加载本地文件
//                    .override(1080, 1920) // 限制最大尺寸（根据UI需求调整，如1080×1920）

            // 2. 使用Glide加载，自动压缩适配
            Glide.with(this)
                    .asBitmap()
                    .override(Target.SIZE_ORIGINAL) // 自动按View尺寸压缩
                    .load(latestImageFile) // 支持File、资源ID、URL
                    .format(DecodeFormat.PREFER_RGB_565) // 降低色彩精度（2字节/像素，比ARGB_8888省一半内存）
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                            view.setTargetBitmap(resource);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            // 加载失败时显示默认图
                            view.setTargetBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default_test));
                        }
                    });
        } else {
            // 无图片时，可设置默认图
            if (currentCameraNum == 1) {
                int imageResId = R.drawable.default_test; // 你的图片资源
                // 加载图片并自动压缩至View尺寸
                Glide.with(this)
                        .asBitmap()
                        .load(imageResId)
                        .override(Target.SIZE_ORIGINAL) // 自动按View尺寸压缩
                        .format(DecodeFormat.PREFER_RGB_565) // 优先RGB_565格式
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                mGridLeft.setTargetBitmap(resource); // 设置到View
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // 加载清除时释放资源
                                mGridLeft.setTargetBitmap(null);
                            }
                        });
//                Bitmap bitmap = view.loadOptimizedBitmap(R.drawable.default_test, view);
//                mGridLeft.setTargetBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default_test));
            } else {
                int imageResId = R.drawable.default_test; // 你的图片资源
                Glide.with(this)
                        .asBitmap()
                        .load(imageResId)
                        .override(Target.SIZE_ORIGINAL) // 自动按View尺寸压缩
                        .format(DecodeFormat.PREFER_RGB_565) // 优先RGB_565格式
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                mGridRight.setTargetBitmap(resource); // 设置到View
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // 加载清除时释放资源
                                mGridRight.setTargetBitmap(null);
                            }
                        });
//                mGridRight.setTargetBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default_test));
            }
        }
    }


}
