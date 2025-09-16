package leesche.smartrecycling.base.http;

import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.leesche.logger.Logger;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.entity.HttpResult;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;


public abstract class HttpBuilder {

    private static final int DEFAULT_TIMEOUT = 30;

    private Retrofit retrofit;

    //构造方法私有
    public HttpBuilder() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(tokenInterceptor);
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(50 * 1000, TimeUnit.MILLISECONDS); //读取超时
        builder.writeTimeout(50 * 1000, TimeUnit.MILLISECONDS); //写入超时
        builder.retryOnConnectionFailure(true);
        try {
            //   Create a trust manager that does not validate certificate chains
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                @Override
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            };

            builder.sslSocketFactory(getSslSocketFactory(trustManager), trustManager).hostnameVerifier((hostname, session) -> true);
            retrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(ResponseConvertFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
        } catch (Exception e) {
            Logger.i("【HttpBuilder】" + e.getMessage());
            retrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(ResponseConvertFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
        }
    }

    Interceptor tokenInterceptor = chain -> {
        Request request = chain.request();
        Response response = chain.proceed(request);
//        if (response != null) {
//            Logger.i("[系统]OKHttp status code: " + response.code() + " url: " + response.request().url());
//        }
        return response;
    };

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public <T> void toSubscribe(Observable<T> o, Observer<T> s) {
    // o:被订阅的 Observable 数据源（如网络请求返回的 Observable）
        // s: 订阅者 Observer 接收数据或错误回调

//        EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.NET_REQUEST_LOADING));
        o.subscribeOn(Schedulers.io()) // 在 IO 线程执行
                .unsubscribeOn(Schedulers.io())  // 在 IO 线程取消订阅（防止内存泄漏
                .observeOn(AndroidSchedulers.mainThread()) // 在主线程处理结果回调
                .subscribe(s); // 订阅
    }


    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    public class HttpResultFunc<T> implements Function<HttpResult<T>, T> {
        @Override
        public T apply(HttpResult<T> httpResult) throws Exception {
//            EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.USER_HINT_INFO));
            if (200 == Integer.parseInt(httpResult.getCode())) {
                if (httpResult.getResult() == null) {
                    HttpUrls.HTTP_ERROR_CODE = 0;
                    Constants.NET_LEVEL = Constants.NetStatus.NORMAL_NET;
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("result", "无结果");
                    return (T) jsonObject;
                }
            } else {
                throw new ApiException(400, TextUtils.isEmpty((CharSequence) httpResult.getResult()) ? httpResult.getMsg() : (String) httpResult.getResult());
            }
            return httpResult.getResult();
        }
    }

//    private List<ConnectionSpec> getSpecsBelowLollipopMR1(OkHttpClient.Builder okb) {
//
//        try {
//            SSLContext sc = SSLContext.getInstance("TLSv1.2");
//            sc.init(null, null, null);
//            okb.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));
//            ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//                    .tlsVersions(TlsVersion.TLS_1_2)
//                    .build();
//
//            List<ConnectionSpec> specs = new ArrayList<>();
//            specs.add(cs);
//            specs.add(ConnectionSpec.COMPATIBLE_TLS);
//            return specs;
//        } catch (Exception exc) {
//            Timber.e("OkHttpTLSCompat Error while setting TLS 1.2"+ exc);
//            return null;
//        }
//    }

    private SSLSocketFactory sslSocketFactory = null;
    private SSLContext sslContext = null;

    private SSLSocketFactory getSslSocketFactory(X509TrustManager trustManager) {
        try {

            // Install the all-trusting trust manager
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trustManager}, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            sslSocketFactory = sslContext.getSocketFactory();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslSocketFactory;
    }
}
