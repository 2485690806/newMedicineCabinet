package com.ycmachine.smartdevice.network.service;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MedPointAuthService {
    /**
     * 请求配对码：POST /auth/code
     * @param body 请求体：{"response_type":"device_code"}
     */
    @POST("auth/code")
    Call<ResponseBody> requestPairingCode(@Body Map<String, String> body);

    /**
     * 完成配对：POST /auth/token（grant_type=device_code）
     * @param body 请求体：{"grant_type":"device_code", "code":"设备码"}
     */
    @POST("auth/token")
    Call<ResponseBody> completePairing(@Body Map<String, String> body);

    /**
     * 刷新 Token：POST /auth/token（grant_type=refresh_token）
     * @param body 请求体：{"grant_type":"refresh_token", "refresh_token":"刷新令牌"}
     */
    @POST("auth/token")
    Call<ResponseBody> refreshAccessToken(@Body Map<String, String> body);

    // （可选）用实体类替代 Map，类型更安全（推荐）
    class PairingRequest {
        @SerializedName("response_type")
        private String responseType;

        public PairingRequest(String responseType) {
            this.responseType = responseType;
        }

        // Getter
        public String getResponseType() {
            return responseType;
        }
    }

    class TokenResponse {
        @SerializedName("access_token")
        private String accessToken;
        @SerializedName("refresh_token")
        private String refreshToken;

        // Getters
        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }
}