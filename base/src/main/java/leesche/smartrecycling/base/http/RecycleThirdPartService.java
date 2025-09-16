package leesche.smartrecycling.base.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import leesche.smartrecycling.base.entity.CommonUserEntity;
import leesche.smartrecycling.base.entity.HttpResult;
import leesche.smartrecycling.base.entity.ThrRecyclerEntity;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public interface RecycleThirdPartService {

    /**
     * 机器登录获取token
     */
//    @POST(HttpUrls.OTHER_SERVER_URL + "/recycling/app/device/login")
    @POST
    Observable<HttpResult<JsonObject>> thrLoginDevice(@Url String url, @Body RequestBody requestBody);

    /**
     * 用户登录
     */
//    @POST(HttpUrls.OTHER_SERVER_URL + "/recycling/app/user/login")
    @POST
    Observable<HttpResult<CommonUserEntity>> thrUserLogin(@Url String url,
                                                          @Header("App-Device-Token") String appDeviceToken,
                                                          @Body RequestBody requestBody);

    //上报投递记录
//    @POST(HttpUrls.OTHER_SERVER_URL + "/recycling/app/user/posting")\
    @POST
    Observable<HttpResult<JsonObject>> thrUsePosting(@Url String url,
                                                     @Header("App-Device-Token") String appDeviceToken,
                                                     @Header("App-User-Token") String appUserToken,
                                                     @Body RequestBody requestBody);

    //上报离线投递记录
//    @POST(HttpUrls.OTHER_SERVER_URL + "/recycling/app/postingByOffline")
    @POST
    Observable<HttpResult<JsonObject>> thrPostingByOffline(@Url String url,
                                                           @Header("App-Device-Token") String appDeviceToken,
                                                           @Body RequestBody requestBody);

    //上报溢满度
//    @POST(HttpUrls.OTHER_SERVER_URL + "/recycling/app/uploadDeviceBoxOverflowDegree")
    @POST
    Observable<HttpResult<JsonObject>> thrUploadBoxOverflow(@Url String url,
                                                            @Header("App-Device-Token") String appDeviceToken,
                                                            @Body RequestBody requestBody);

    /**
     * 上报告警信息
     */
//    @POST(HttpUrls.OTHER_SERVER_URL + "/recycling/app/uploadBoxAlarmStatus")
    @POST
    Observable<HttpResult<JsonObject>> thrUploadBoxAlarmStatus(@Url String url,
                                                               @Header("App-Device-Token") String appDeviceToken,
                                                               @Body RequestBody requestBody);

    //获取屏幕广告和屏保
//    @POST(HttpUrls.OTHER_SERVER_URL + "/recycling/app/getDeviceAdvert")
    @POST
    Observable<HttpResult<JsonArray>> thrDeviceAdvert(@Url String url,
                                                      @Header("App-Device-Token") String appDeviceToken,
                                                      @Body RequestBody requestBody);

    //获取亚马逊token
//    @POST(HttpUrls.OTHER_SERVER_URL + "/recycling/app/awsS3StsSign")
    @POST
    Observable<HttpResult<JsonObject>> thrAwsS3StsSign(@Url String url,
                                                       @Header("App-Device-Token") String appDeviceToken,
                                                       @Body RequestBody requestBody);

    //获取阿里云token
//    @POST(HttpUrls.OTHER_SERVER_URL + "/recycling/app/ossStsSign")
    @POST
    Observable<HttpResult<JsonObject>> thrOssStsSign(@Url String url,
                                                     @Header("App-Device-Token") String appDeviceToken,
                                                     @Body RequestBody requestBody);


    //上传图片到本地服务器
//    @POST(HttpUrls.OTHER_SERVER_URL + "/recycling/app/updateOrderFile")
    @POST
    @Multipart
    Observable<HttpResult<JsonObject>> thrUpdateOrderFile(@Url String url,
                                                          @PartMap Map<String, RequestBody> map,
                                                          @Part List<MultipartBody.Part> parts);

    @POST
    Observable<HttpResult<ThrRecyclerEntity>> thrRecyclerLogin(@Url String url,
                                                               @Header("App-Device-Token") String appDeviceToken,
                                                               @Body RequestBody requestBody);

    @POST
    Observable<HttpResult<JsonObject>> thrRecyclerOpenBox(@Url String url,
                                                     @Header("App-Device-Token") String appDeviceToken,
                                                     @Header("App-Recycler-Token") String appUserToken,
                                                     @Body RequestBody requestBody);

    @POST
    Observable<HttpResult<JsonArray>> thrGetRecycleBox(@Url String url,
                                                       @Header("App-Device-Token") String appDeviceToken,
                                                       @Header("App-Recycler-Token") String appUserToken);
}
