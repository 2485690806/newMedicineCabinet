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
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface CornerstoneService {

    /**
     * 取得回收機參數
     */
    @GET
    Observable<HttpResult<JsonObject>> getConfig(@Url String url,
                                                 @Header("timestamp") long timestamp,
                                                 @Header("sign") String sign,
                                                 @Query("kid") int kid);

    /**
     * 取得食物盒狀態
     */
    @GET
    Observable<HttpResult<JsonObject>> checkFoodContainer(@Url String url,
                                                          @Header("timestamp") long timestamp,
                                                          @Header("sign") String sign,
                                                          @Query("containerNo") String containerNo);

    /**
     * 取得條碼資料庫更新時間
     */



    /**
     * 取得膠樽資料庫
     */
}
