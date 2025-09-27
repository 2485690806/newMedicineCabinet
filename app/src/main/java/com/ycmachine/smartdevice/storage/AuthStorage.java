package com.ycmachine.smartdevice.storage;

import android.content.Context;
import android.text.TextUtils;

import leesche.smartrecycling.base.utils.SharedPreferencesUtils;

public final class AuthStorage {
    private AuthStorage() {}

    private static final String KEY_ACCESS_TOKEN = "mp_auth_access_token";
    private static final String KEY_REFRESH_TOKEN = "mp_auth_refresh_token";
    private static final String KEY_DEVICE_CODE  = "mp_auth_device_code";
    private static final String KEY_USER_CODE    = "mp_auth_user_code";

    public static void saveAccessAndRefresh(Context ctx, String accessToken, String refreshToken) {
        if (ctx == null) return;
        if (!TextUtils.isEmpty(accessToken)) {
            SharedPreferencesUtils.put(ctx, KEY_ACCESS_TOKEN, accessToken);
        }
        if (!TextUtils.isEmpty(refreshToken)) {
            SharedPreferencesUtils.put(ctx, KEY_REFRESH_TOKEN, refreshToken);
        }
    }

    public static void saveDeviceAndUser(Context ctx, String deviceCode, String userCode) {
        if (ctx == null) return;
        if (!TextUtils.isEmpty(deviceCode)) {
            SharedPreferencesUtils.put(ctx, KEY_DEVICE_CODE, deviceCode);
        }
        if (!TextUtils.isEmpty(userCode)) {
            SharedPreferencesUtils.put(ctx, KEY_USER_CODE, userCode);
        }
    }

    public static String getAccessToken(Context ctx) {
        Object v = SharedPreferencesUtils.get(ctx, KEY_ACCESS_TOKEN, "");
        String s = v != null ? v.toString() : null;
        return TextUtils.isEmpty(s) ? null : s;
    }

    public static String getRefreshToken(Context ctx) {
        Object v = SharedPreferencesUtils.get(ctx, KEY_REFRESH_TOKEN, "");
        String s = v != null ? v.toString() : null;
        return TextUtils.isEmpty(s) ? null : s;
    }

    public static String getDeviceCode(Context ctx) {
        Object v = SharedPreferencesUtils.get(ctx, KEY_DEVICE_CODE, "");
        String s = v != null ? v.toString() : null;
        return TextUtils.isEmpty(s) ? null : s;
    }

    public static String getUserCode(Context ctx) {
        Object v = SharedPreferencesUtils.get(ctx, KEY_USER_CODE, "");
        String s = v != null ? v.toString() : null;
        return TextUtils.isEmpty(s) ? null : s;
    }

    public static void clear(Context ctx) {
        if (ctx == null) return;
        SharedPreferencesUtils.remove(ctx, KEY_ACCESS_TOKEN);
        SharedPreferencesUtils.remove(ctx, KEY_REFRESH_TOKEN);
        SharedPreferencesUtils.remove(ctx, KEY_DEVICE_CODE);
        SharedPreferencesUtils.remove(ctx, KEY_USER_CODE);
    }
}
