package leesche.smartrecycling.base.http.thr;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.leesche.logger.Logger;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.entity.DevConfigEntity;
import leesche.smartrecycling.base.entity.ThrAuditLogEntity;
import leesche.smartrecycling.base.entity.ThrAuditLogItemEntity;
import leesche.smartrecycling.base.http.HttpBuilder;
import leesche.smartrecycling.base.http.subscribers.ProgressSubscriber;
import leesche.smartrecycling.base.http.subscribers.SubscriberOnNextListener2;
import leesche.smartrecycling.base.utils.CrashHandler;
import okhttp3.RequestBody;

public class ThrHttpMethods extends HttpBuilder {

    private final ThrHttpService thrHttpService;

    public void setThirdPartyApiServer(DevConfigEntity.ThirdPartyApiServer thirdPartyApiServer) {
        this.thirdPartyApiServer = thirdPartyApiServer;
    }

    private DevConfigEntity.ThirdPartyApiServer thirdPartyApiServer;
    Gson gson;
    JsonParser jsonParser;
    private String thrServiceRequestToken;


    public DevConfigEntity.ThirdPartyApiServer getThirdPartyApiServer() {

        return thirdPartyApiServer;
    }


    public String getThrServiceRequestToken() {
        return thrServiceRequestToken;
    }

    public void setThrServiceRequestToken(String thrServiceRequestToken) {
        this.thrServiceRequestToken = thrServiceRequestToken;
    }

    public ThrHttpMethods() {
        super();
        thrHttpService = getRetrofit().create(ThrHttpService.class);
        jsonParser = new JsonParser();
        gson = new Gson();
    }

    private static class SingletonHolder {
        private static final ThrHttpMethods INSTANCE = new ThrHttpMethods();
    }

    public static ThrHttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void sendAuditLog(Observer<JsonObject> observer) {
        String host = "https://testapi.recycler.at/api/v1";
        if (thirdPartyApiServer != null) host = thirdPartyApiServer.getServerDomain();
        String url = host + "/recycling/app/audit/log";

        CrashHandler.getInstance().getThrAuditLogEntity().setPeriodEndTime(System.currentTimeMillis() / 1000);
        CrashHandler.getInstance().getThrAuditLogEntity().setSerialNumber(Constants.DEVICE_CODE);
        String accessTokenParams = gson.toJson(CrashHandler.getInstance().getThrAuditLogEntity());

//        Logger.i("sendAuditLog"+accessTokenParams);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), accessTokenParams);
        Observable<JsonObject> observable = thrHttpService.sendAuditLog(url, thrServiceRequestToken, body).map(new HttpResultFunc<>());
        toSubscribe(observable, observer);
    }

    private SubscriberOnNextListener2<JsonObject> sendAuditLogOnNext;


    public void setSendAuditLogOnNext() {

        sendAuditLogOnNext = new SubscriberOnNextListener2<JsonObject>() {
            @Override
            public void onNext(JsonObject jsonObject) {
                Logger.i("[系统]发送日志成功：" + jsonObject.toString());

                CrashHandler.getInstance().setThrAuditLogEntity(new ThrAuditLogEntity());
//                thrAuditLogEntity = new ThrAuditLogEntity();
            }

            @Override
            public void onError(String e) {
                Logger.i("[系统]发送日志成功 失败：" + e);
            }
        };

    }

    public void sendAuditLog(String logLevel, String message, boolean isSend,String attemptId) {


        try {

            ThrAuditLogItemEntity thrAuditLogItemEntity = new ThrAuditLogItemEntity();
            thrAuditLogItemEntity.setLogLevel(logLevel);
            thrAuditLogItemEntity.setMessage(message);

            thrAuditLogItemEntity.setAttemptId(attemptId);
            thrAuditLogItemEntity.setTimestamp(System.currentTimeMillis() / 1000);
//            thrAuditLogEntity.getLogMessages().add(thrAuditLogItemEntity);
            CrashHandler.getInstance().addThrAuditLogEntity(thrAuditLogItemEntity);

            if (!isSend) {
                return;
            }

            if (CrashHandler.getInstance().getThrAuditLogEntity() != null
                    && CrashHandler.getInstance().getThrAuditLogEntity().getLogMessages() != null
                    && CrashHandler.getInstance().getThrAuditLogEntity().getLogMessages().size() > 0) {
                sendAuditLog(new ProgressSubscriber<>(sendAuditLogOnNext));
            } else {
                Logger.i("[系统]没有日志需要发送");
            }
        } catch (Exception e) {

        }
    }


}
