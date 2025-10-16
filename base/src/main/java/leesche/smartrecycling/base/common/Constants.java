package leesche.smartrecycling.base.common;

import android.os.Environment;

import java.io.File;

public class Constants {
    /**
     * 调试开关
     */
    public static boolean isPowerOn = false;
    public static boolean IS_TEST = true; //是否测试环境
    public static boolean DEVICE_STATUS = true;
    public static boolean isSGServer = true;
    public static boolean IS_OTHER_PLATFORM = false;//第三方接口
    public static String VERSION_UPDATE_URL = "https://youyicloud-app.oss-cn-shanghai.aliyuncs.com/new_smart_recycling/update.json";
    public static String FIRMWARE_VERSION_UPDATE_URL = "https://youyicloud-app.oss-cn-shanghai.aliyuncs.com/new_smart_recycling/update_firmware.json";
    public static String EVALUATION_URL = "https://youyicloud-app.oss-cn-shanghai.aliyuncs.com/app_config/";
    public static String UPDATE_TYPE = "";

    /**
     * 缓存文件夹
     */
    public static final String SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String BASE_CACHE_DIR = SDCARD_DIR + "/YCEquipment";
    public static final String BASE_APP_UPDATE = SDCARD_DIR + "/AutoInstaller";
    public static String LOGGER = BASE_CACHE_DIR + "/logger";
    public static String LAYER_LIST = BASE_CACHE_DIR + "/layer_list.txt";
    public static String LAYER_VALUE = BASE_CACHE_DIR + "/layer_value.txt";
    public static String WELCOME_AUDIO_OGG = BASE_CACHE_DIR + "/welocome.OGG";
    public static String LOGIN_AUDIO_OGG = BASE_CACHE_DIR + "/login.OGG";
    public static final String VIDEO_CACHE_DIR = BASE_CACHE_DIR + "/Video";
    public static final String LOG_CACHE_DIR = BASE_CACHE_DIR + "/log";
    public static final String ACFACE_DIR = BASE_CACHE_DIR + "/acface";
    public static final String APK_CACHE_DIR = BASE_CACHE_DIR + "/apk";
    public static String AD_CACHE_DIR = BASE_CACHE_DIR + "/ad.txt";
    public static String START_AD_CACHE_DIR = BASE_CACHE_DIR + "/start_ad.txt";
    public static String FINISH_AD_CACHE_DIR = BASE_CACHE_DIR + "/finish_ad.txt";
    public static String CANCEL_AD_CACHE_DIR = BASE_CACHE_DIR + "/cancel_ad.txt";
    public static final String POC_CAMERA_DIR = BASE_CACHE_DIR + "/poc_video";
    public static final String FFMPEG_RECORD_VIDEO = Constants.POC_CAMERA_DIR + "/FFMPEG";
    public static final String UPDATE_CONFIG = BASE_CACHE_DIR + "/update.json";
    public static final String FACE_FEATURES = Constants.ACFACE_DIR + File.separator + "register" + File.separator + "features";
    public static final String FACE_IMGS = Constants.ACFACE_DIR + File.separator + "register" + File.separator + "imgs";
    public static final String FACE_SYNC = FACE_FEATURES + File.separator + "/sync_record.txt";
    public static String TEST_IMG = BASE_CACHE_DIR + "/test.jpg";

    /**
     * http协议头
     */
    public static final String PROTOCOL_HEADER = "https:";

    public static final String PROTOCOL_HEADER_HTTP = "http:";

    /**
     * 测试环境
     */
    public static final String DEV_HOST = "//devcloud.youyiyun.tech/";
//    public static final String DEV_HOST = "//devcloud.youyiyun.tech/recycling/";

    /**
     * 正式环境
     */
    public static final String HOST = "//cloud.youyiyun.tech/";
    public static String HXNH_HOST = "//hxnh.top/recycling/";

    //韩国服务器地址 http://everysolution.youyiyun.tech/management-sys/index
    public static final String ES_HOST = "//everysolution.youyiyun.tech/";
    //第三方websocket服务器地址
    public static String THR_WEBSOCKET_URL = "ws://devcloud.youyiyun.tech:8010/websocket";

    /**
     * 音量高低
     */
    public static boolean VOLUME_HIGH = true;
    /**
     * web socket 地址
     */
    public static String WEB_SOCKET_URL = null;

    public static boolean IS_KOREA_DEV = false;
    public static String APP_VER;

    /**
     * 基本地址
     */
    public static String BASE_URL = PROTOCOL_HEADER + HOST;

    /**
     * 用户二维码登录地址
     */
    public static String USER_QR_LOGIN = BASE_URL + "c/recycling?";

    public static String DEVICE_TOKEN = "";

    public static String LOGIN_TYPE = "phone";
    public static String LOGIN_VALUE = "";

    //定时重启系统标志
    public static boolean SYS_REBOOT_FLAG = false;

    /**
     * 设备ID
     */
    public static String MAC_ADDRESS = ""; //510472053637534D05D3FF36

    /**
     * 游艺云平台唯一ID
     */
    public static int YYY_DEVICE_ID = 999;

    public static int KID = 999;

    /**
     * 代理商ID
     */
    public static int AGENT_ID = 0;
    /**
     * 用户token
     */
    public static String USER_TOKEN = "";

    /**
     * 用户手机号码
     */
    public static String USER_PHONE = "";


    /**
     * 回收员token
     */
    public static String RECYCLER_TOKEN = "";

    /**
     * 设备码
     */
    public static String DEVICE_CODE = "";

    /**
     * 风扇开启时间间隔
     */
    public static String FAN_ON_INTERVAL = "15";

    /**
     * 网络级别  0/无网络 1/超弱网络 2/正常网络
     */
    public static int NET_LEVEL = 2;


    public static final String ad_small_suffix = "?x-oss-process=image/resize,w_1080,h_1920,m_fill/auto-orient,1/quality,q_70";
    public static final String bottom_small_suffix = "?x-oss-process=image/resize,w_1080,h_768,m_fill/auto-orient,1/quality,q_70";
    public static final String small_suffix = "?x-oss-process=image/resize,w_200,h_200,m_fill/auto-orient,1/quality,q_70";

    /**
     * 是否允许后台上传
     */
    public static boolean CAN_UPLOAD = false;

    /**
     * 订单ID
     */
    public static String ORDER_ID = "";


    /**
     * 阿里云消息推送类型
     */
    public interface aLiPushMsgType {
        String APP_UPGRADE = "app_upgrade";
        String APP_UPGRADE_MANUAL = "app_upgrade_manual";
        String REFRESH_PRODUCT_LIST = "refresh";
        String RESTART_DEVICE = "restart_device";
        String USER_LOGIN = "user_login";
        String FIRMWARE_UPDATE = "firmware_update";
        String UPDATE_DEVICESTATUS = "update_deviceStatus";

        //{"msg_type":"reboot_app"}
        String REBOOT_APP = "reboot_app";//重启APP
        String REBOOT_SYS = "reboot_sys";//重启设备
        String REMOTELY_MODIFY_CONFIG = "modify_config";//远程修改设备配置文件

        //oss://youyicloud-app/logger/9436/request/
        String UPLOAD_LOGGER_REQUEST = "upload_logger_request";
        //上传crash日志 {"data":"20200323","msg_type":"upload_logger_crash"}
        String UPLOAD_LOGGER_CRASH = "upload_logger_crash";
        String ACTIVE_AC_FACE = "active_ac_face";//激活人脸识别
        String SYSC_FILE = "sysc_file";//同步文件
        String THIRD_PARTY_EXCHANGE = "$remote_control";//第三方兑换商品
        String USER_FACE_IMG_CONFIG = "user_face_img_config";//服务端下发人脸图片
        String POC_UPLOAD = "poc_upload";//监控视频 图片上传
        String APP_CONFIG = "app_config";//服务端下发配置文件
        String DELIVERY_MODEL = "delivery_model";//修改有偿/公益方式投递 新加坡需求
        //{"msg_type":"exc_internal_cmd"}
        String EXC_INTERNAL_CMD = "exc_internal_cmd";
        //{"data":"/sdcard/AutoInstaller/","msg_type":"remove_local_file"}
        String REMOVE_LOCAL_FILE = "remove_local_file";
        String REFRESH_DEVICE = "refresh";//刷新设备
        String MONITOR_CHECK = "monitor_check";//检测监控摄像头
        String NOTIFY_FILE_UPLOAD = "file_upload";//图片重新上传
        String INSTALL_APP = "install_app";

    }


    public interface NetStatus {
        int NO_NET = 0;
        int WEAK_NET = 1;
        int NORMAL_NET = 2;
    }

    public interface LoginType {

        String PHONE = "phone";
        String FACE = "face";
        String IC_CARD = "icCard";
        String PUBLIC_ACCOUNT = "publicAccount";
        String BACK_SCAN = "backScan";
        String PLATE = "plate";
        String WECHAT = "wechat";
        String OPERATIONS = "operations";
    }
}

