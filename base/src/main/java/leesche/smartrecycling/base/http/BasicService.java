package leesche.smartrecycling.base.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.reactivex.rxjava3.core.Observable;
import leesche.smartrecycling.base.entity.CommonUserEntity;
import leesche.smartrecycling.base.entity.DeviceInfo;
import leesche.smartrecycling.base.entity.HttpResult;
import leesche.smartrecycling.base.entity.RecyclerEntity;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BasicService {

    /**
     * APP登录获取access token
     *
     * @param mac     从设备ID
     * @param imei    主设备ID
     * @param version
     * @return
     */
    @POST(HttpUrls.APP_LOGIN)
    @FormUrlEncoded
    Observable<HttpResult<DeviceInfo>> appLogin(@Field("mac") String mac
            , @Field("imei") String imei, @Field("version") String version
            , @Field("firmwareVersion") String firmwareVersion);

    /**
     * 广告列表
     *
     * @param token
     * @return
     */
    @GET(HttpUrls.AD_LIST)
    Observable<HttpResult<JsonArray>> getDeviceAdvert(@Query("$access_token") String token);

    /**
     * 获取分类及箱子信息
     *
     * @param token
     * @return
     */
    @POST(HttpUrls.DELIVERY_TYPE_LIST)
    @FormUrlEncoded
    Observable<HttpResult<JsonArray>> getDeviceBoxAndType(@Field("$access_token") String token);

    /**
     * 投递者手机号登录
     *
     * @param phone
     * @return
     */
    @POST(HttpUrls.USER_PHONE_LOGIN)
    @FormUrlEncoded
    Observable<HttpResult<CommonUserEntity>> loginByPhone(@Field("phone") String phone,
                                                          @Field("loginType") String loginType,
                                                          @Field("$access_token") String token);

    /**
     * 投递者门牌号
     *
     * @param phone
     * @return
     */
    @POST(HttpUrls.PLATE_LOGIN)
    @FormUrlEncoded
    Observable<HttpResult<CommonUserEntity>> loginByDoorplate(@Field("doorplate") String phone,
                                                              @Field("$access_token") String token);

    /**
     * IC卡卡号登录
     *
     * @param icCard
     * @return
     */
    @POST(HttpUrls.USER_IC_LOGIN)
    @FormUrlEncoded
    Observable<HttpResult<CommonUserEntity>> loginByIcCard(@Field("icCard") String icCard,
                                                           @Field("$access_token") String token);

    /**
     * 公益账号登录
     *
     * @return
     */
    @POST(HttpUrls.PUBLIC_ACCOUNT_LOGIN)
    @FormUrlEncoded
    Observable<HttpResult<CommonUserEntity>> loginByPublicAccount(@Field("$access_token") String token);

    /**
     * 反扫码登录
     *
     * @return
     */
    @POST(HttpUrls.USER_BACK_SCAN_LOGIN)
    @FormUrlEncoded
    Observable<HttpResult<CommonUserEntity>> loginByBackScan(
            @Field("scanResult") String scanResult,
            @Field("$access_token") String token);

    /**
     * 用户投递垃圾详情
     *
     * @param uToken
     * @param result
     * @return
     */
    @POST(HttpUrls.POST_DELIVERY_DETAIL)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> rubbishPosting(
            @Field("orderId") String orderId,
            @Field("result") String result,
            @Field("app_user_token") String uToken,
            @Field("$access_token") String token);

    @POST(HttpUrls.POST_DELIVERY_DETAIL)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> rubbishPosting(
            @Field("orderId") String orderId,
            @Field("result") String result,
            @Field("recoveryTime") Long recoveryTime,
            @Field("rvmDetail") String rvmDetail,
            @Field("app_user_token") String uToken,
            @Field("$access_token") String token);


    @POST(HttpUrls.POST_DELIVERY_DETAIL)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> rubbishPosting2(
            @Field("orderId") String orderId,
            @Field("result") String result,
            @Field("userAppraise") String userAppraise,
            @Field("app_user_token") String uToken,
            @Field("$access_token") String token);

    /**
     * 绑定IC卡
     *
     * @param icCard
     * @param isCheck
     * @param token
     * @return
     */
    @POST(HttpUrls.BIND_IC_CARD)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> bindIcCard(@Field("icCard") String icCard
            , @Field("isCheck") boolean isCheck, @Field("app_user_token") String uToken
            , @Field("$access_token") String token);


    /*********************************************************************************
     *
     *                         回收员
     *
     * *******************************************************************************
     **/

    /**
     * 回收员手机号登录
     *
     * @param phone
     * @return
     */
    @POST("c/recycling/app/recycler/loginByPhone")
    @FormUrlEncoded
    Observable<HttpResult<RecyclerEntity>> recyclerLoginByPhone(@Field("phone") String phone,
                                                                @Field("password") String password,
                                                                @Field("$access_token") String token);

    /**
     * 回收员反扫码登录
     *
     * @return
     */
    @POST(HttpUrls.RECYCLER_BACK_SCAN_LOGIN)
    @FormUrlEncoded
    Observable<HttpResult<CommonUserEntity>> loginByBackScan2(
            @Field("scanResult") String scanResult,
            @Field("$access_token") String token);

    /**
     * 回收员IC卡卡号登
     *
     * @param icCard
     * @return
     */
    @POST(HttpUrls.RECYCLER_IC_LOGIN)
    @FormUrlEncoded
    Observable<HttpResult<CommonUserEntity>> loginByIcCard2(@Field("icCard") String icCard,
                                                            @Field("$access_token") String token);

    /**
     * 回收员手机号登录
     *
     * @param phone
     * @return
     */
    @POST(HttpUrls.RECYCLE_PHONE_LOGIN)
    @FormUrlEncoded
    Observable<HttpResult<CommonUserEntity>> recyclerLoginByFace(@Field("phone") String phone,
                                                                 @Field("loginType") String loginType,
                                                                 @Field("$access_token") String token);

    /**
     * 返回箱子列表
     *
     * @param token
     * @return
     */
    @POST(HttpUrls.RECYCLING_BOX_LIST)
    @FormUrlEncoded
    Observable<HttpResult<JsonArray>> getDeviceBox(
            @Field("app_recycler_token") String rToken,
            @Field("$access_token") String token);

    /**
     * 回收员开箱请求
     *
     * @param rToken
     * @param boxCode
     * @param token
     * @return
     */
    @POST(HttpUrls.RECYCLER_OPEN_BOX)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> openBoxRequest(@Field("boxCode") String boxCode,
                                                      @Field("app_recycler_token") String rToken,
                                                      @Field("$access_token") String token);

    /**
     * 回收员开箱请求
     *
     * @param rToken
     * @param boxCode
     * @param token
     * @return
     */
    @POST(HttpUrls.RECYCLER_OPEN_BOX)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> openBoxRequest(@Field("boxCode") String boxCode,
                                                      @Field("app_confirmer_token") String app_confirmer_token,
                                                      @Field("app_recycler_token") String rToken,
                                                      @Field("$access_token") String token);

    /**
     * 扣除积分 成功开门 不成功不开门
     *
     * @param recoveryRecordId
     * @param payPassword
     * @param rToken
     * @param token
     * @return
     */
    @POST(HttpUrls.RECYCLER_OPEN_BY_POINT)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> outerPayByPoint(@Field("recoveryRecordId") int recoveryRecordId,
                                                       @Field("payPassword") String payPassword,
                                                       @Field("app_recycler_token") String rToken,
                                                       @Field("$access_token") String token);

    /**
     * 外部回收员支付确认
     *
     * @param recoveryRecordId
     * @param rToken
     * @param token
     * @return
     */
    @POST(HttpUrls.RECYCLE_PAY_CONFIRM)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> checkOuterPayResult(@Field("recoveryRecordId") int recoveryRecordId,
                                                           @Field("app_recycler_token") String rToken,
                                                           @Field("$access_token") String token);

    /**
     * 上报箱子相对可放置高度
     *
     * @param boxCode
     * @param distance
     * @param token
     * @return
     */
    @POST(HttpUrls.POST_BOX_DISTACE)
    @FormUrlEncoded
    Observable<HttpResult<String>> uploadBoxDistance(@Field("boxCode") String boxCode,
                                                     @Field("distance") int distance,
                                                     @Field("$access_token") String token);

    /**
     * @param boxCode
     * @param errorCode
     * @param errorMsg
     * @param token
     * @return
     */
    @POST(HttpUrls.POST_BOX_ERROR)
    @FormUrlEncoded
    Observable<HttpResult<String>> uploadBoxErrorMsg(@Field("boxCode") String boxCode,
                                                     @Field("errorCode") int errorCode,
                                                     @Field("errorMsg") String errorMsg,
                                                     @Field("$access_token") String token);

    /**
     * 上报未关门的箱子
     */
    @POST(HttpUrls.POST_NO_CLOSE_BOX)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> uploadUnCloseBoxDoor(@Field("boxCode") String boxCode,
                                                            @Field("$access_token") String token);

    /**
     * 上报地址信息
     *
     * @param info
     * @param token
     * @return
     */
    @POST(HttpUrls.POST_LOCATION_INFO)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> uploadLocationInfo(@Field("info") String info,
                                                          @Field("$access_token") String token);

    @POST(HttpUrls.DEVICE_TRACK)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> uploadDeviceTrack(@Field("detail") String detail,
                                                         @Field("$access_token") String token);

    /**
     * 上报ICCID 用于查询sim卡号
     *
     * @param iccid
     * @param token
     * @return
     */
    @POST(HttpUrls.POST_ICCID)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> uploadICCID(@Field("iccid") String iccid,
                                                   @Field("$access_token") String token);

    /**
     * 日志上报
     *
     * @param flag
     * @param requestBody
     * @return
     */
    @POST(HttpUrls.POST_LOGGER)
    Observable<JsonObject> logUpload(@Path("flag") String flag, @Body RequestBody requestBody);

    /**
     * 上报警告信息
     *
     * @param boxCode
     * @param alarmType
     * @param alarmStatus
     * @param token
     * @return
     */
    @POST(HttpUrls.POST_ALERT_INFO)
    @FormUrlEncoded
    Observable<JsonObject> uploadBoxAlarmStatus(@Field("boxCode") String boxCode,
                                                @Field("alarmType") String alarmType,
                                                @Field("alarmStatus") String alarmStatus,
                                                @Field("$access_token") String token);

    @POST(HttpUrls.POST_NET_SIGNAL)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> uploadSignal(@Field("signalType") String signalType,
                                                    @Field("signalLattice") int signalLattice,
                                                    @Field("signalIntensity") int signalIntensity,
                                                    @Field("$access_token") String token);


    /***********************************************************************
     *
     *   2019年12月30日
     *
     * *********************************************************************
     */
    @POST(HttpUrls.POST_DELIVERY_OFFLINE)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> postingByOffline(
            @Field("app_user_token") String uToken,
            @Field("loginType") String loginType,
            @Field("loginValue") String loginValue,
            @Field("orderId") String orderId,
            @Field("result") String result,
            @Field("$access_token") String token);

    @POST(HttpUrls.POST_DELIVERY_OFFLINE)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> postingByOffline4(
            @Field("app_user_token") String uToken,
            @Field("loginType") String loginType,
            @Field("loginValue") String loginValue,
            @Field("orderId") String orderId,
            @Field("recoveryTime") String recoveryTime,
            @Field("gmt") String gmt,
            @Field("result") String result,
            @Field("$access_token") String token);

    @POST(HttpUrls.POST_DELIVERY_OFFLINE)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> postingByOffline2(
            @Field("app_user_token") String uToken,
            @Field("loginType") String loginType,
            @Field("loginValue") String loginValue,
            @Field("userAppraise") String userAppraise,
            @Field("orderId") String orderId,
            @Field("result") String result,
            @Field("$access_token") String token);


    /***********************************************************************
     *
     *   2020年1月3日
     *
     * *********************************************************************
     */
    @POST("c/app/oss_sts_sign")
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> getOssStsSign();

    @POST(HttpUrls.ROUTE_INFO)
    @FormUrlEncoded
    Observable<HttpResult<JsonArray>> getRouteInfo(@Field("$access_token") String token);

    @POST(HttpUrls.ACTIVE_SITE)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> activeDeviceSite(@Field("lineId") int id,
                                                        @Field("siteId") int siteId,
                                                        @Field("recyclingStatus") String recyclingStatus,
                                                        @Field("$access_token") String token);

    @POST(HttpUrls.ACTIVE_OSS)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> getOSSSts(@Field("$access_token") String token);

    /*****************************************************************************
     *
     *                     2023年4月3日15:18:34 视频上传（优化前端友好提示）
     *
     * ***************************************************************************
     */
    @POST("c/recycling/app/reportUploadFileInfo")
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> reportUploadFileInfo(@Field("$access_token") String token,
                                                            @Field("orderId") String orderId,
                                                            @Field("fileNameList") String fileNameList);

    @POST("c/recycling/app/reportUploadProgress")
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> reportUploadProgress(@Field("$access_token") String token,
                                                            @Field("orderId") String orderId,
                                                            @Field("fileName") String fileName,
                                                            @Field("progress") int progress);

    /***************************************************************
     *
     * 2022年9月3日09:24:56 饮料瓶回收机修改在线/离线上报记录接口
     *
     */
    @POST(HttpUrls.POST_DELIVERY_DETAIL)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> rubbishPosting3(
            @Field("orderId") String orderId,
            @Field("pointCouponCodeNumber") String pointCouponCodeNumber,
            @Field("recoveryTime") String recoveryTime,
            @Field("gmt") String gmt,
            @Field("result") String result,
            @Field("app_user_token") String uToken,
            @Field("$access_token") String token,
            @Field("rvmDetail") String rvmDetail);

    @POST(HttpUrls.POST_DELIVERY_OFFLINE)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> postingByOffline3(
            @Field("orderId") String orderId,
            @Field("pointCouponCodeNumber") String pointCouponCodeNumber,
            @Field("recoveryTime") String recoveryTime,
            @Field("gmt") String gmt,
            @Field("result") String result,
            @Field("app_user_token") String uToken,
            @Field("$access_token") String token,
            @Field("rvmDetail") String rvmDetail,
            @Field("loginType") String loginType,
            @Field("loginValue") String loginValue);


    @POST(HttpUrls.incrBoxCapacityNum)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> incrBoxCapacityNum(
            @Field("acceptNum") Integer acceptNum,
            @Field("boxCode") Integer boxCode,
            @Field("$access_token") String token);

    @POST(HttpUrls.incrPrintVoucherNum)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> incrPrintVoucherNum(
            @Field("printNum") Integer printNum,
            @Field("$access_token") String token);

    @POST(HttpUrls.resetPrintVoucherNum)
    @FormUrlEncoded
    Observable<HttpResult<JsonObject>> resetPrintVoucherNum(
            @Field("$access_token") String token);
}