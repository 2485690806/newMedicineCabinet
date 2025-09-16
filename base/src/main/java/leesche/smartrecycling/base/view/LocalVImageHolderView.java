package leesche.smartrecycling.base.view;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnCompletionListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.widget.PLVideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import leesche.smartrecycling.base.R;
import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.entity.AdEntity;
import leesche.smartrecycling.base.handler.LocalConfigManager;
import leesche.smartrecycling.base.http.glide.GlideApp;
import leesche.smartrecycling.base.strategy.DevContext;
import leesche.smartrecycling.base.utils.NetSpeed;

public class LocalVImageHolderView extends Holder<AdEntity> {

    private ImageView iv_ad_img;
    private PLVideoView pl_ad_video;

    private FrameLayout fl_video;
    private ImageView coverView;

    //        private LinearLayout loadingView;

    private Callback callback;
    private int curId;
    String imageHref;
    private int type = 0;
    private List<AdEntity> bannerList = new ArrayList<>();

    public interface Callback {
        void onCanTurn(int position, long time);
    }

    public void setCallback(Callback mCallback) {
        this.callback = mCallback;
    }

    public LocalVImageHolderView(View itemView, int... type) {
        super(itemView);
        this.type = type[0];
//        this.changeTime = AppConfigHandler.getInstance().getDevConfigEntity().getAdvertSetting().getRotationSeconds() * 1000;

        bannerList = LocalConfigManager.getInstance().getIdleBannerList();

    }


    @Override
    protected void initView(View itemView) {
        iv_ad_img = itemView.findViewById(R.id.iv_ad_img);
        pl_ad_video = itemView.findViewById(R.id.pl_ad_video);
        fl_video = itemView.findViewById(R.id.fl_video);
        coverView = itemView.findViewById(R.id.coverView);
    }

    private Integer changeTime = 8000;

    @Override
    public void updateUI(AdEntity data) {
        if (data == null) {
            return;
        }
        String localPath = data.getLocalCache();
        imageHref = data.getLocalCache();
//        imageHref = data.getImgHref();
////        String localPath = SystemFuncHandler.getInstance().getLocalFilePath(imageHref);
//        if (!TextUtils.isEmpty(localPath) && new File(localPath).exists()) {
//            localPath = data.getLocalCache();
////            Logger.i("[系统]广告 从本地加载资源：" + localPath);
//        } else {
//            if (imageHref.endsWith("mp4")) {
//                if (callback != null) callback.onCanTurn(curId, changeTime);
//                return;
//            }
//            localPath = imageHref;
//        }
        if (pl_ad_video != null) {
            pl_ad_video.stopPlayback();
        }
        if (imageHref != null && !imageHref.isEmpty()) {
            curId = data.getId();
            if (imageHref.endsWith("gif")) {
                fl_video.setVisibility(View.GONE);
                iv_ad_img.setVisibility(View.VISIBLE);
                GlideApp.with(DevContext.getInstance().getContext()
                                .getApplicationContext())
                        .asGif()
                        .load(localPath)
                        .into(iv_ad_img);
                if (callback != null) callback.onCanTurn(curId, changeTime);
                return;
            }
            if (imageHref.endsWith("mp4")) {
                fl_video.setVisibility(View.VISIBLE);
                startVideo(localPath);
//                preVideo();

                return;
            }
            fl_video.setVisibility(View.GONE);
            iv_ad_img.setVisibility(View.VISIBLE);
            GlideApp.with(DevContext.getInstance().getContext()
                            .getApplicationContext())
                    .load(localPath)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                    .into(iv_ad_img);
            if (callback != null) callback.onCanTurn(curId, changeTime);
        }
    }

//    private void preVideo() {
//
//        if (curId < bannerList.size() - 1) {
//            AdEntity data = bannerList.get(curId + 1);
//            String imageHref = data.getImgHref();
//            String localPath = SystemFuncHandler.getInstance().getLocalFilePath(imageHref);
//            if (imageHref.endsWith("mp4")) {
//                pl_ad_video.setVideoPath(localPath);
//            }
//
//        } else {
//            AdEntity data = bannerList.get(0);
//            String imageHref = data.getImgHref();
//            String localPath = SystemFuncHandler.getInstance().getLocalFilePath(imageHref);
//            if (imageHref.endsWith("mp4")) {
//                pl_ad_video.setVideoPath(localPath);
//            }
//        }
//    }

    /**
     * 初始化播放配置
     */
    private void initAvOptions() {
        AVOptions options = new AVOptions();
        //超时时间
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        //解码方式
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_HW_DECODE);
        //设置播放方式 1/直播 0/点播
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 0);
        //设置缓存文件夹
        options.setString(AVOptions.KEY_CACHE_DIR, Constants.VIDEO_CACHE_DIR);
        //播放位置
        options.setInteger(AVOptions.KEY_START_POSITION, 0);
        pl_ad_video.setAVOptions(options);


//        String[] hour_minute = ConfigManager.getInstance().getConfigEntity().getAudioOnTime().split(":");
//        int hour = Integer.parseInt(hour_minute[0]);
//        int minute = Integer.parseInt(hour_minute[1]);
//        String[] hour_minute_end = ConfigManager.getInstance().getConfigEntity().getAudioOffTime().split(":");
//        int hour_end = Integer.parseInt(hour_minute_end[0]);
//        int minute_end = Integer.parseInt(hour_minute_end[1]);
//        if (DateUtil.isCurrentInTimeScope(hour, minute, hour_end, minute_end)) {
//            pl_ad_video.setVolume(1, 1);
//        } else {
//            pl_ad_video.setVolume(0, 0);
//        }

        pl_ad_video.setOnCompletionListener(mOnCompletionListener);
        pl_ad_video.setOnErrorListener(mOnErrorListener);
        if (type == 0) {
            pl_ad_video.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_ORIGIN);
        } else {
            pl_ad_video.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        }
//        pl_ad_video.setBufferingIndicator(loadingView);
    }

    private PLOnCompletionListener mOnCompletionListener = new PLOnCompletionListener() {
        @Override
        public void onCompletion() {

//            pl_ad_video.stopPlayback();
//            if (callback != null) callback.onCanTurn(curId, 1000);

            xunHuanBoFang();
        }
    };

    private void xunHuanBoFang() {
        if (curId < bannerList.size() - 1) {
            curId = curId + 1;
        } else {
            curId = 0;
        }


        AdEntity data = bannerList.get(curId);
//        String imageHref = data.getImgHref();
//        String localPath = SystemFuncHandler.getInstance().getLocalFilePath(imageHref);
        String localPath = data.getLocalCache();
        imageHref = data.getLocalCache();

        if (imageHref.endsWith("gif")) {
            fl_video.setVisibility(View.GONE);
            iv_ad_img.setVisibility(View.VISIBLE);
            GlideApp.with(DevContext.getInstance().getContext()
                            .getApplicationContext())
                    .asGif()
                    .load(localPath)
                    .into(iv_ad_img);
        } else if (imageHref.endsWith("mp4")) {
            startVideo(localPath);
            return; // 假设视频播放有自己的回调控制
        } else {
            fl_video.setVisibility(View.GONE);
            iv_ad_img.setVisibility(View.VISIBLE);
            GlideApp.with(DevContext.getInstance().getContext()
                            .getApplicationContext())
                    .load(localPath)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                    .into(iv_ad_img);
        }

        // 对于非mp4内容（gif和其他图片），8秒后重新执行
        if (!imageHref.endsWith("mp4")) {
            new Handler(Looper.getMainLooper()).postDelayed(this::xunHuanBoFang, changeTime); // 8秒延迟
        }
    }

    private void startVideo(String localPath) {
        pl_ad_video.setVideoPath(localPath);
        pl_ad_video.addCache(localPath);
        pl_ad_video.setCoverView(coverView);
        initAvOptions();
        pl_ad_video.start();
        fl_video.setVisibility(View.VISIBLE);
        iv_ad_img.setVisibility(View.GONE);

//        iv_ad_img.setVisibility(View.GONE);
    }

    private PLOnErrorListener mOnErrorListener = new PLOnErrorListener() {
        @Override
        public boolean onError(int errorCode, Object o) {
//            Logger.i("errorCode：" + errorCode + "Object" + JSON.toJSONString(o));
            switch (errorCode) {
                case ERROR_CODE_IO_ERROR:
//                    Logger.i("player IO Error!");
                    long speed = NetSpeed.newInstance().getNetSpeed(android.os.Process.myPid());
                    if (speed < 15 && callback != null) callback.onCanTurn(curId, 1000);
                    return false;
                case ERROR_CODE_OPEN_FAILED:
//                    Logger.i("player failed to open player !");
                    String fileName = imageHref.replace("https://", "")
                            .replace("/", "-");
                    String videoCachePath = Constants.VIDEO_CACHE_DIR + File.separator + fileName + ".mp4";
                    File file = new File(videoCachePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    if (callback != null) callback.onCanTurn(curId, 1000);
                    break;
                case ERROR_CODE_SEEK_FAILED:
//                    Logger.i("failed to seek !");
                    if (callback != null) callback.onCanTurn(curId, 1000);
                    return true;
                case ERROR_CODE_CACHE_FAILED:
//                    Logger.i("player failed to cache url !");
                    if (callback != null) callback.onCanTurn(curId, 1000);
                    break;
                default:
//                    Logger.i("player unknown error !");
                    if (callback != null) callback.onCanTurn(curId, 1000);
                    break;
            }
            return true;
        }
    };

    // 添加停止视频的方法
    public void stopVideo() {
        if (pl_ad_video != null) {
            // 停止播放并释放资源
            pl_ad_video.stopPlayback(); // 停止播放
        }
    }

    // 添加停止视频的方法
    public void closeVolume() {
        if (pl_ad_video != null) {
            // 停止播放并释放资源
            pl_ad_video.setVolume(0, 0);
        }
    }
}
