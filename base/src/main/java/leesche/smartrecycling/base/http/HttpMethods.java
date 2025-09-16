package leesche.smartrecycling.base.http;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.leesche.logger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.TimeZone;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import leesche.smartrecycling.base.utils.SoundPoolHelper;
import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.entity.CommonUserEntity;
import leesche.smartrecycling.base.entity.RecyclerEntity;
import leesche.smartrecycling.base.entity.UserInfoEntity;
import leesche.smartrecycling.base.strategy.DevContext;
import leesche.smartrecycling.base.utils.SharedPreferencesUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.http.Field;


public class HttpMethods extends HttpBuilder {

    private BasicService basicService;

    public HttpMethods() {
        super();
        basicService = getRetrofit().create(BasicService.class);
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    //获取单例
    public static HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public BasicService getBasicService() {
        return basicService;
    }

    public void setBasicService(BasicService basicService) {
        this.basicService = basicService;
    }

    /*************************************************************************************
     *
     *                             客户端 C 端接口
     *
     * ***********************************************************************************
     */

    /**
     * 设备登录 获取token 信息
     *
     * @param observer
     * @param mac
     * @param imei
     * @param version
     * @param firmwareVersion
     */
    public void appLogin(Observer<JsonObject> observer, String mac, String imei
            , String version, String firmwareVersion) {
        Observable observable = basicService.appLogin(mac, imei, version, firmwareVersion)
                .map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * 获取商品列表
     *
     * @param observer
     */
    public void getDeviceBoxAndType(Observer<JsonArray> observer) {
        Observable observable = basicService.getDeviceBoxAndType(Constants.DEVICE_TOKEN)
                .map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * 获取设备广告
     *
     * @param observer
     */
    public void getDeviceAd(Observer<JsonArray> observer) {
        Observable observable = basicService.getDeviceAdvert(Constants.DEVICE_TOKEN)
                .map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * 投递者手机号码登录
     *
     * @param observer
     * @param phoneNum
     */
    public void loginByPhone(Observer<CommonUserEntity> observer, String phoneNum, String loginType) {

        Observable observable = basicService.loginByPhone(phoneNum, loginType, Constants.DEVICE_TOKEN)
                .map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * 投递者门牌号码登录
     *
     * @param observer
     * @param doorNum
     */
    public void loginByDoorplate(Observer<CommonUserEntity> observer, String doorNum) {

        Observable observable = basicService.loginByDoorplate(doorNum, Constants.DEVICE_TOKEN)
                .map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * IC卡卡号登录
     *
     * @param observer
     * @param icCard
     */
    public void loginByIcCard(Observer<UserInfoEntity> observer, String icCard) {

        Observable observable = basicService.loginByIcCard(icCard, Constants.DEVICE_TOKEN)
                .map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * IC卡卡号登录 共用回收员
     *
     * @param observer
     * @param icCard
     */
    public void loginByIcCard2(Observer<CommonUserEntity> observer, String icCard) {

        Observable observable = basicService.loginByIcCard2(icCard, Constants.DEVICE_TOKEN)
                .map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * 公益账号登录
     *
     * @param observer
     */
    public void loginByPublicAccount(Observer<CommonUserEntity> observer) {

        Observable observable = basicService.loginByPublicAccount(Constants.DEVICE_TOKEN)
                .map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * 反扫码登录
     *
     * @param observer
     */
    public void loginByBackScan(Observer<UserInfoEntity> observer, String scanResult) {

        Observable observable = basicService.loginByBackScan(scanResult, Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * 反扫码登录 共用回收员
     *
     * @param observer
     */
    public void loginByBackScan2(Observer<CommonUserEntity> observer, String scanResult) {
//        Logger.i("【User Login】 scan ==>  scanResult: " + scanResult + " token: " + Constants.DEVICE_TOKEN);
        Observable observable = basicService.loginByBackScan2(scanResult, Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * 投递垃圾提交
     *
     * @param observer
     * @param result
     * @param user_token
     */
    public void rubbishPosting(Observer<JsonObject> observer, String orderId, String result, String user_token) {

        Observable observable = basicService.rubbishPosting(orderId, result, user_token, Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    public void rubbishPosting4(Observer<JsonObject> observer, String orderId, String result, String rvmDetail, String user_token) {
        // 用这个推送


        Observable observable = basicService.rubbishPosting(orderId, result, System.currentTimeMillis() / 1000L, rvmDetail, user_token, Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    public void bindIcCard(Observer<JsonObject> observer, String icCard) {

        Observable observable = basicService.bindIcCard(icCard, false
                        , Constants.USER_TOKEN, Constants.DEVICE_TOKEN)
                .map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }


    /**
     * 回收员手机号码登录
     *
     * @param observer
     * @param phoneNum
     */
    public void recyclerLoginByPhone(Observer<RecyclerEntity> observer, String phoneNum, String pwd) {

        Observable observable = basicService.recyclerLoginByPhone(phoneNum, pwd, Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * 回收员人脸登录
     *
     * @param observer
     * @param phoneNum
     * @param loginType
     */
    public void recyclerLoginByFace(Observer<CommonUserEntity> observer, String phoneNum, String loginType) {

        Observable observable = basicService.recyclerLoginByFace(phoneNum, loginType, Constants.DEVICE_TOKEN)
                .map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * 获取箱子列表
     *
     * @param observer
     */
    public void getDeviceBox(Observer<JsonArray> observer) {

        Observable observable = basicService.getDeviceBox(
                        Constants.RECYCLER_TOKEN
                        , Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * 请求打开箱门
     *
     * @param observer
     */
    public void openBoxRequest(Observer<JsonObject> observer, String boxCode) {
        Observable observable = basicService.openBoxRequest(boxCode
                        , Constants.RECYCLER_TOKEN, Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * 请求打开箱门
     *
     * @param observer
     */
    public void openBoxRequest(Observer<JsonObject> observer, String boxCode, String app_confirmer_token) {
        Observable observable = basicService.openBoxRequest(boxCode,
                        app_confirmer_token,
                        Constants.RECYCLER_TOKEN,
                        Constants.DEVICE_TOKEN)
                .map(new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * 扣除积分 成功开门 不成功不开门
     *
     * @param observer
     */
    public void outerPayByPoint(Observer<JsonObject> observer, int recoveryRecordId, String payPassword) {
        Observable observable = basicService.outerPayByPoint(recoveryRecordId, payPassword
                        , Constants.RECYCLER_TOKEN, Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * 外部回收员支付结果
     *
     * @param observer
     */
    public void checkOuterPayResult(Observer<JsonObject> observer, int payRecordId) {

        Observable observable = basicService.checkOuterPayResult(payRecordId
                        , Constants.RECYCLER_TOKEN, Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    /**
     * 上报箱子相对剩余高度
     */
    public void uploadBoxDistance(String boxCode, int distance) {
        Observable observable = basicService.uploadBoxDistance(boxCode, distance, Constants.DEVICE_TOKEN);
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
//                        Logger.i("【距离】 上报成功");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
//                        Logger.i("【距离】 上报失败：箱子号->" + boxCode + " 失败原因->" + throwable.getMessage());
                    }
                });
    }

    /**
     * 上报箱子错误信息
     */
    public void uploadBoxErrorMsg(String boxCode, int errorCode, String errorMsg) {
        Observable observable = basicService.uploadBoxErrorMsg(boxCode, errorCode, errorMsg, Constants.DEVICE_TOKEN);
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Log.i("uploadBoxErrorMsg", o.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("uploadBoxErrorMsg", throwable.getMessage());
                    }
                });
    }

    /**
     * 上报地址信息
     */
    public void uploadLocationInfo(String content) {
        Observable observable = basicService.uploadLocationInfo(content, Constants.DEVICE_TOKEN);
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 设备运行路径
     */
    public void uploadDeviceTrack(String content) {
        Observable observable = basicService.uploadDeviceTrack(content, Constants.DEVICE_TOKEN);
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 日志上报
     */

    public void logUpload(String flag, String content) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), content);
        Observable observable = basicService.logUpload(flag, requestBody);
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 警告
     */
    String _boxCode = "";
    String _alarmType = "";
    String _alarmStatus = "";

    public void uploadBoxAlarmStatus(String boxCode, String alarmType, String alarmStatus, final int count) {
        _boxCode = boxCode;
        _alarmType = alarmType;
        _alarmStatus = alarmStatus;
        Observable observable = basicService.uploadBoxAlarmStatus(boxCode, alarmType
                , alarmStatus, Constants.DEVICE_TOKEN);
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        //{"msg":"java.lang.reflect.InvocationTargetException","code":"400"}
//                        if (Constants.IS_TEST)
//                        Logger.d("【uploadBoxAlarmStatus】" + " boxCode: " + boxCode + " alarmType: " + _alarmType + " alarmStatus: " + _alarmStatus);
                        JsonObject result = (JsonObject) o;
                        if (result.get("code").getAsInt() != 200) {
                            if (count > 0) {
                                int _count = count - 1;
                                uploadBoxAlarmStatus(_boxCode, _alarmType, _alarmStatus, _count);
                            }
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
//                        if (Constants.IS_TEST)
                        Log.d("uploadBoxAlarmStatus:", "上报失败");
                        if (count > 0) {
                            int _count = count - 1;
                            uploadBoxAlarmStatus(_boxCode, _alarmType, _alarmStatus, _count);
                        }
                    }
                });
    }


    /**
     * 上报ICCID 用于识别流量卡号
     *
     * @param iccid
     */
    @SuppressLint("CheckResult")
    public void uploadICCID(String iccid) {
        basicService.uploadICCID(iccid, Constants.DEVICE_TOKEN).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe((Consumer) o -> {

                }, throwable -> {

                });
    }

    /************************************
     *
     *  2019年4月10日 上报未关门箱子
     *
     * **********************************
     */
    @SuppressLint("CheckResult")
    public void uploadUnCloseBoxDoor(String boxCode) {
        basicService.uploadUnCloseBoxDoor(boxCode, Constants.DEVICE_TOKEN).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(o -> {
                }, throwable -> {
                });
    }

    /**
     * 离线上报投递记录
     */
    public void postingByOffline(Observer<JsonObject> observer, String uToken, String loginType, String loginValue
            , String orderId, String result) {
        Observable observable = basicService.postingByOffline(uToken, loginType, loginValue
                        , orderId, result, Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    public void postingByOffline4(Observer<JsonObject> observer, String uToken, String loginType, String loginValue
            , String orderId, String result, String recoveryTime) {
        Observable observable = basicService.postingByOffline4(uToken, loginType, loginValue
                        , orderId, recoveryTime, getTimeZone(), result, Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    public void postingByOffline2(Observer<JsonObject> observer, String uToken, String loginType, String loginValue,
                                  String userAppraise, String orderId, String result) {
        Observable observable = basicService.postingByOffline2(uToken, loginType, loginValue,
                        userAppraise, orderId, result, Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    @SuppressLint("CheckResult")
    public void reportUploadFileInfo(String orderId, String fileNameList) {
        basicService.reportUploadFileInfo(Constants.DEVICE_TOKEN, orderId, fileNameList)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe((Consumer) o -> {
//                    Logger.i("【视频上传】 file info (success): " + o.toString());
                }, throwable -> {
//                    Logger.i("【视频上传】 file info (success): " + throwable.getMessage());
                });
    }

    @SuppressLint("CheckResult")
    public void reportUploadProgress(String orderId, String fileName, int progress) {
        basicService.reportUploadProgress(Constants.DEVICE_TOKEN, orderId, fileName, progress)
                .subscribeOn(Schedulers.io()) // 在IO线程执行网络请求
                .unsubscribeOn(Schedulers.io())// 取消订阅时也在IO线程
                .subscribe((Consumer) o -> {
//                    Logger.i("【视频上传】 progress (success): " + o.toString());
                }, throwable -> {
//                    Logger.i("【视频上传】 progress (error): " + throwable.getMessage());
                });
    }

    /**
     * 上传设备网络
     *
     * @param observer
     * @param signalType
     * @param signalLattice
     * @param signalIntensity
     */
    public void uploadSignal(Observer<JsonArray> observer, String signalType, int signalLattice, int signalIntensity) {
        Observable observable = basicService.uploadSignal(signalType, signalLattice, signalIntensity, Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    private void putSharedPreferences(int type, @NonNull String url) {
        if (type == 0) {
            SharedPreferencesUtils.put(DevContext.getInstance().getContext()
                    , SharedPreferencesUtils.WELCOME_AUDIO_NAME, url);
            SoundPoolHelper.setSoundPoolHelper(null);
        }
        if (type == 1) {
            SharedPreferencesUtils.put(DevContext.getInstance().getContext()
                    , SharedPreferencesUtils.LOGIN_AUDIO_NAME, url);
            SoundPoolHelper.setSoundPoolHelper(null);
        }
    }

    private void writeFile(InputStream inputStream, File file) {
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);

            byte[] b = new byte[1024];

            int len;
            while ((len = inputStream.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            inputStream.close();
            fos.close();

        } catch (FileNotFoundException e) {
//            listener.onFail("FileNotFoundException");
        } catch (IOException e) {
//            listener.onFail("IOException");
        }

    }

    public void rubbishPosting2(Observer<JsonObject> observer, String orderId, String result, String userAppraise, String user_token) {
        Observable observable = basicService.rubbishPosting2(orderId, result, userAppraise, user_token, Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    public void getRouteInfo(Observer<JsonArray> observer) {
        Observable observable = basicService.getRouteInfo(Constants.DEVICE_TOKEN)
                .map(new HttpResultFunc<>());
        toSubscribe(observable, observer);
    }

    public void activeDeviceSite(Observer<JsonObject> observer, int routeId, int siteId) {
        Observable observable = basicService.activeDeviceSite(routeId, siteId, "recycled", Constants.DEVICE_TOKEN)
                .map(new HttpResultFunc<>());
        toSubscribe(observable, observer);
    }

    public void getOSSSts(Observer<JsonObject> observer) {
        Observable observable = basicService.getOSSSts(Constants.DEVICE_TOKEN)
                .map(new HttpResultFunc<>());
        toSubscribe(observable, observer);
    }

    public void rubbishPosting3(Observer<JsonObject> observer, String order, String code, String recoveryTime, String result, String user_token, String rvmDetail) {
        Logger.i("【投放接口上报参数】" + " orderId: " + order + " pointCouponCodeNumber: " + code + " recoveryTime: " + recoveryTime + " rvmDetail: " + rvmDetail +
                 " gmt: " + getTimeZone() + " result: " + result + " app_user_token: " + user_token + " app_user_token: " + Constants.DEVICE_TOKEN);
        Observable observable = basicService.rubbishPosting3(order, code, recoveryTime, getTimeZone(), result, user_token, Constants.DEVICE_TOKEN, rvmDetail)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    public void postingByOffline3(Observer<JsonObject> observer,
                                  String order, String code, String recoveryTime,
                                  String result, String user_token, String rvmDetail,
                                  String loginType, String loginValue) {

//        Logger.i("【离线投放接口上报参数】" + " orderId: " + order + " pointCouponCodeNumber: " + code + " recoveryTime: " + recoveryTime + " rvmDetail: " + rvmDetail +
//                " gmt: " + getTimeZone() + " result: " + result + " app_user_token: " + user_token + " app_user_token: " + Constants.DEVICE_TOKEN);

        Observable observable = basicService.postingByOffline3(order, code,
                        recoveryTime, getTimeZone(), result,
                        user_token, Constants.DEVICE_TOKEN, rvmDetail,loginType,loginValue)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }
    public void incrBoxCapacityNum(Observer<JsonObject> observer,Integer acceptNum,Integer boxCode) {

        Observable observable = basicService.incrBoxCapacityNum(acceptNum, boxCode,Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    public void incrPrintVoucherNum(Observer<JsonObject> observer,Integer printNum) {

        Observable observable = basicService.incrPrintVoucherNum(printNum, Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    public void resetPrintVoucherNum(Observer<JsonObject> observer) {

        Observable observable = basicService.resetPrintVoucherNum(Constants.DEVICE_TOKEN)
                .map((Function) new HttpResultFunc<>());

        toSubscribe(observable, observer);
    }

    public String getTimeZone() {
        int gmt = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (3600 * 1000);
        String timeZone = String.valueOf(gmt);
        if (gmt > 0) timeZone = "+" + timeZone;
        return timeZone;
    }
}
