package com.ycmachine.smartdevice.activity.medicineCabinet;

import static leesche.smartrecycling.base.common.Constants.TEST_IMG;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ycmachine.smartdevice.R;
import com.ycmachine.smartdevice.R2;
import com.ycmachine.smartdevice.manager.CabinetQrManager;
import com.ycmachine.smartdevice.manager.GridImageAdapter;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import leesche.smartrecycling.base.BaseActivity;
import leesche.smartrecycling.base.entity.GridRegion;
import leesche.smartrecycling.base.entity.QrCodeBinding;
import leesche.smartrecycling.base.qrcode.GridRegionManager;
import leesche.smartrecycling.base.qrcode.ImageCropper;
import leesche.smartrecycling.base.qrcode.QrCodeScanner;
import leesche.smartrecycling.base.utils.DrawableToBitmapUtil;

public class TestActivity extends BaseActivity {

    @BindView(R2.id.rv_grid_images)
    RecyclerView mRecyclerView;

    private GridImageAdapter mAdapter;
    private List<String> mCroppedImagePaths;

    @BindView(R2.id.imageView)
    ImageView imageView;

    private void initRecyclerView() {

        // 设置布局管理器，可以根据需要改为GridLayoutManager
        // 线性布局（垂直）
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        // 如果需要网格布局，可以这样设置（例如2列）        try {
        ////            OpenCVQRCodeDetector openCVQRCodeDetector = new OpenCVQRCodeDetector();
        ////            List<String> results = new ArrayList<>();
        ////            openCVQRCodeDetector.detectAndDecodeMulti(originalBitmap, results);
        //// 低版本兼容：加载 VectorDrawable
        //            Drawable vectorDrawable = AppCompatResources.getDrawable(this, leesche.smartrecycling.base.R.drawable.test  );
        //            Bitmap vectorBitmap = DrawableToBitmapUtil.drawableToBitmap(vectorDrawable, 200, 200);
        //
        //            List<String> strings = QrCodeScanner.decodeImage(vectorBitmap);
        //            System.out.println("111"+JSON.toJSONString(strings));
        //
        //        } finally {
        //
        //        }
        // mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mAdapter = new GridImageAdapter(this, mCroppedImagePaths);
        mRecyclerView.setAdapter(mAdapter);


    }

    CabinetQrManager.OnProcessListener listener = new CabinetQrManager.OnProcessListener() {
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
    // 第八层左边的六个格子
//    List<GridRegion> gridRegions = List.of(
//            new GridRegion(490, 216, 364, 501, 91),
//            new GridRegion(874, 216, 364, 501, 92),
//            new GridRegion(1290, 216, 364, 501, 93),
//            new GridRegion(1714, 216, 364, 501, 94),
//            new GridRegion(2138, 216, 364, 501, 95),
//            new GridRegion(2554, 216, 364, 501, 96),
//            new GridRegion(442, 800, 373, 577, 106),
//            new GridRegion(846, 800, 373, 577, 107),
//            new GridRegion(1262, 800, 373, 577, 108),
//            new GridRegion(1710, 800, 373, 577, 109),
//            new GridRegion(2142, 800, 373, 577, 110),
//            new GridRegion(2574, 800, 373, 577, 111)
//    );
    public void startPreview(View view) {

//        GridRegion gridRegion= new GridRegion(1, 100, 100, 400, 400);
//        String cropGrid = ImageCropper.cropGrid(this, TEST_IMG, gridRegion, 1);

        List<GridRegion> gridRegions = GridRegionManager.getInstance().getGridRegions(5, 2);

        List<String> strings = ImageCropper.cropAllGrids(this, TEST_IMG, gridRegions, 1);
//        System.out.println("Cropped image path: " + cropGrid);
        mCroppedImagePaths = strings;


//        List<String> qrCodes = QrCodeScanner.scan(TEST_IMG);
//        Logger.i("qrcode:"+JSON.toJSONString(qrCodes));

        initRecyclerView();

    }

    public void stopPreview(View view) {

        List<GridRegion> gridRegions = GridRegionManager.getInstance().getGridRegions(5, 2);

        CabinetQrManager.getInstance().processPhoto("0",1, TEST_IMG, gridRegions,listener);
//        imageView.setImageResource(R.drawable.ic_launcher_background);
    }

    public void showResult(View view) {

        QrCodeBinding abc123 = CabinetQrManager.getInstance().findByItemQr("089");
        System.out.println("Found binding: " + JSON.toJSONString(abc123));
//        imageView.setImageResource(R.drawable.ic_launcher_background);
    }


    @Override
    public int initLayout() {
        return R.layout.activity_test;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        Glide.with(this)
                .load(new File(TEST_IMG)) // 从文件路径加载
                .diskCacheStrategy(DiskCacheStrategy.NONE)  // 禁用磁盘缓存
                .skipMemoryCache(true)                     // 禁用内存缓存
                .into(imageView);

        CabinetQrManager.getInstance().init(this);

    }

    @Override
    public void initData() {

    }
}
