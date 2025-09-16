package leesche.smartrecycling.base.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.leesche.logger.Logger;

import java.io.IOException;
import java.lang.reflect.Type;

import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.entity.HttpResult;
import okhttp3.ResponseBody;
import retrofit2.Converter;

class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final Type type;

    GsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
//        Logger.i("[response]" + response);
        if (Constants.IS_OTHER_PLATFORM) {
            HttpResult httpResult = new HttpResult<>();
            JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
            if (jsonObject.get("message") == null) {
                try {
                    return gson.fromJson(response, type);
                } catch (JsonParseException e) {
//                    Logger.i("【数据无法正常解析】" + response);
                }
                httpResult = gson.fromJson(response, HttpResult.class);
                return (T) httpResult;
            } else {
                if (!jsonObject.get("message").getAsBoolean()) {
                    httpResult.setCode("400");
                    httpResult.setMsg(jsonObject.toString());
                } else {
                    httpResult.setCode("200");
                    if (jsonObject.get("data") == null) {
                        try {
                            if (jsonObject.get("result").isJsonNull()) {
                                httpResult.setResult(jsonObject);
                            } else {
                                httpResult.setResult(jsonObject.get("result").getAsJsonObject());
                            }
                        } catch (Exception e) {
                            httpResult.setResult(jsonObject);
                        }
                    } else {
                        httpResult.setResult(jsonObject);
                        if (jsonObject.get("data").isJsonObject()) {
                            httpResult.setResult(jsonObject.get("data").getAsJsonObject());
                        }
                        if (jsonObject.get("data").isJsonArray()) {
                            httpResult.setResult(jsonObject.get("data").getAsJsonArray());
                        }
                    }
                }
                if (jsonObject.get("message") != null) {
                    httpResult.setMsg(jsonObject.get("message").getAsString());
                }
            }
            return (T) httpResult;
        }
        if (response.contains("iccid") || (response.contains("reason") && response.contains("code"))) {
            HttpResult<Object> httpResult = new HttpResult<>();
            httpResult.setResult(response);
            httpResult.setCode("200");
            return (T) httpResult;
        }
        try {
            return gson.fromJson(response, type);
        } catch (Exception e) {
            Logger.i("[系统]数据无法正解析 " + response);
        }
        HttpResult httpResult = gson.fromJson(response, HttpResult.class);
        return (T) httpResult;
    }
}
