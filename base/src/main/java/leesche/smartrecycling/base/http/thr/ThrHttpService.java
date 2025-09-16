package leesche.smartrecycling.base.http.thr;

import com.google.gson.JsonObject;

import io.reactivex.rxjava3.core.Observable;
import leesche.smartrecycling.base.entity.HttpResult;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ThrHttpService {



    @POST
    Observable<HttpResult<JsonObject>> sendAuditLog(@Url String url,
                                                    @Header("App-Device-Token") String appDeviceToken,
                                                    @Body RequestBody requestBody);


}
