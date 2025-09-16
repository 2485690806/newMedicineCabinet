package leesche.smartrecycling.base.http;

public class HttpUrls {

    public static int HTTP_ERROR_CODE = 0;
    public static String lastErrorMsg;
    static final String DEVICE_FLAG = "c/recycling";
    //    static final String DEVICE_FLAG = "c/weighing";
    public static String TR_HOST_ADDRESS = "https://baseuatapi.cstl.com.hk";
    public static String WEB_HOST_ADDRESS = "wss://baseuatapi.cstl.com.hk/kws";
    public static String APP_UPGRADE_URL = "https://youyicloud-app.oss-cn-shanghai.aliyuncs.com/Cornerstone/Foodpanda";
    public static String FW_UPGRADE_URL = "https://youyicloud-app.oss-cn-shanghai.aliyuncs.com/Cornerstone/FW";
    public static String SV_UPGRADE_URL = "https://youyicloud-app.oss-cn-shanghai.aliyuncs.com/Cornerstone/Basic";

    /*******************************************************************************************************************
     *
     *                                         公用接口地址
     *
     * *****************************************************************************************************************
     */
    static final String ACTIVE_OSS = "c/recycling/app/oss_sts_sign";//获取OSS STS
    static final String APP_LOGIN = DEVICE_FLAG + "/app/login"; //设备登录
    static final String AD_LIST = DEVICE_FLAG + "/app/getDeviceAdvert"; //广告列表
    static final String USER_PHONE_LOGIN = DEVICE_FLAG + "/app/user/loginByPhone";//用户手机号投递
    static final String USER_IC_LOGIN = DEVICE_FLAG + "/app/user/loginByIcCard";//用户IC卡登录
    static final String USER_BACK_SCAN_LOGIN = DEVICE_FLAG + "/app/user/loginByBackScan";//用反扫码登录
    static final String RECYCLER_IC_LOGIN = DEVICE_FLAG + "/app/loginByIcCard"; //IC卡登录
    static final String RECYCLER_BACK_SCAN_LOGIN = DEVICE_FLAG + "/app/loginByBackScan";//反扫码登录

    /*******************************************************************************************************************
     *
     *                                         回收机
     *
     * *****************************************************************************************************************
     */

    static final String DELIVERY_TYPE_LIST = "c/recycling/app/getDeviceBoxAndType"; //投递类型
    static final String PLATE_LOGIN = "c/recycling/app/user/loginByDoorplate";//门牌号登录
    static final String PUBLIC_ACCOUNT_LOGIN = "c/recycling/app/user/loginByPublicAccount"; //公益账号登录
    static final String POST_DELIVERY_DETAIL = "c/recycling/app/user/posting";//上报投递详情
    static final String BIND_IC_CARD = "c/recycling/app/user/bindIcCard";//绑定IC

    static final String RECYCLING_BOX_LIST = "c/recycling/app/recycler/getDeviceBox";//回收箱列表
    static final String RECYCLE_PHONE_LOGIN = "c/recycling/app/recycler/loginByPhone";//回收员手机号登录
    static final String RECYCLER_OPEN_BOX = "c/recycling/app/recycler/openBoxRequest";//回收员开箱请求
    static final String RECYCLER_OPEN_BY_POINT = "c/recycling/app/recycler/outerPayByPoint";//回收员使用积分开箱
    static final String RECYCLE_PAY_CONFIRM = "c/recycling/app/recycler/checkOuterPayResult";//外部回收员支付确认

    static final String POST_BOX_DISTACE = "c/recycling/app/calculateDeviceBoxOverflowDegree";//上报箱子距离
    static final String POST_NO_CLOSE_BOX = "c/recycling/app/uploadUnCloseBoxDoor";//上报未关门箱子
    static final String POST_LOCATION_INFO = "c/app/uploadLocationInfo";//上报地址信息
    static final String POST_ICCID = "c/app/uploadLocationInfo";//上报ICCID 用于查询sim卡号
    static final String POST_LOGGER = "https://cloud.youyiyun.tech/log/app/{flag}";//上报日志
    static final String POST_ALERT_INFO = "c/recycling/app/uploadBoxAlarmStatus";//上报告警信息
    static final String POST_MCU_ATTR = "c/recycling/app/uploadBoxAttrValue";//上报MCU温度登其他属性值
    static final String POST_NET_SIGNAL = "c/app/uploadSignal";//上报网络信号
    static final String POST_DELIVERY_OFFLINE = "c/recycling/app/user/postingByOffline";//离线投递记录上报
    static final String CONFIRM_BY_IC_CARD = "c/recycling/app/recycler/checkConfirmerInfoByIcCard";//交接员校验
    static final String COUNT_DELIVERY_TYPE_LIST = "c/recycling/app/user/getDeviceBoxAndType";//计次投递类型接口
    static final String DEVICE_TRACK = "c/recycling/app/uploadDeviceTrack";//设备运行路径
    static final String POST_BOX_ERROR = "c/recycling/app/uploadBoxErrorMsg";//上报箱子配件错误
    static final String POST_EVALUATION = "c/recycling/app/orderAppraise";//上报评价接口
    static final String ROUTE_INFO = "c/recycling/app/getDeviceLineList";//路线信息
    static final String ACTIVE_SITE = "c/recycling/app/activeDeviceSite";//线路站点激活

    static final String incrBoxCapacityNum = "c/recycling/app/incrBoxCapacityNum";//投递瓶子数量上报，不勾选Tom服务器也要上报
    static final String incrPrintVoucherNum = "c/recycling/app/incrPrintVoucherNum";//打印数量上报
    static final String resetPrintVoucherNum = "c/recycling/app/recycler/resetPrintVoucherNum";//运维员重置打印数量

    /*******************************************************************************************************************
     *
     *                                         Carbon Wallet
     *
     * *****************************************************************************************************************
     */
    public static int campaignId;
    public static int storeID;
    public static String secretKey;
    public static String itemName;

    /*******************************************************************************************************************
     *
     *                                         SFTP 服务器
     *
     * *****************************************************************************************************************
     */
    public static String sftURL;
    public static int port;
    public static String loginName;
    public static String accessKeyFileName;
    public static String downloadPath;
    public static String uploadPath;


}

