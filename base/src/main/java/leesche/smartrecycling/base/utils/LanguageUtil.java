package leesche.smartrecycling.base.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import java.util.Locale;


public class LanguageUtil {

//    public static boolean LAN_NOT_HAVE_CHANGE = false;

    public static final String SP_NAME = "multi_language";
    public static final String IS_FOLLOW_SYSTEM = "is_follow_system";
    public static final String CURRENT_LANGUAGE = "current_language";
    public static final String CURRENT_COUNTRY = "current_country";
    public static final String LAST_SET_LAN = "last_set_lan";
    public static int curLanIndex = 0;

    public static void changeLanguage(Context context, String language, String country) {
        if (context == null || TextUtils.isEmpty(language)) {
            return;
        }
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = new Locale(language, country);
        resources.updateConfiguration(config, null);
        config.setLayoutDirection(Locale.getDefault());
        SharedPreferencesUtils.put(context, CURRENT_LANGUAGE, language);
        SharedPreferencesUtils.put(context, CURRENT_COUNTRY, country);
        SharedPreferencesUtils.put(context, IS_FOLLOW_SYSTEM, false);
        SharedPreferencesUtils.put(context, LAST_SET_LAN, curLanIndex);
    }

    public static void followSystemLanguage(Context context) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = Locale.getDefault();
        resources.updateConfiguration(config, null);
        SharedPreferencesUtils.put(context, IS_FOLLOW_SYSTEM, true);
    }

    public static void setLanguage(Context context, String lan, String country) {
        boolean isFollowSystem = (boolean) SharedPreferencesUtils.get(context, IS_FOLLOW_SYSTEM, false);
        if (!isFollowSystem) {
            String currentLanguage = (String) SharedPreferencesUtils.get(context, CURRENT_LANGUAGE, lan);
            String currentCountry = (String) SharedPreferencesUtils.get(context, CURRENT_COUNTRY, country);
            Resources resources = context.getResources();
            Configuration config = resources.getConfiguration();
            config.locale = new Locale(currentLanguage, currentCountry);
            resources.updateConfiguration(config, null);
            config.setLayoutDirection(Locale.getDefault());
//            String lastSetLan = (String) SharedPreferencesUtils.get(context, LanguageUtil.LAST_SET_LAN, "zh|");
//            String curSetLan = currentLanguage + "|" + currentCountry;
//            LAN_NOT_HAVE_CHANGE = curSetLan.equals(lastSetLan);
        }
    }

    public static void changeApplicationLan(Context context, int index) {
        curLanIndex = index;
        switch (index) {
            case 0:
                LanguageUtil.changeLanguage(context, Locale.SIMPLIFIED_CHINESE.getLanguage(), "");
                break;
            case 1:
                LanguageUtil.changeLanguage(context, Locale.TRADITIONAL_CHINESE.getLanguage(), "HK");
                break;
            case 3:
                LanguageUtil.changeLanguage(context, Locale.KOREA.getLanguage(), "");
                break;
            case 4:
                LanguageUtil.changeLanguage(context, "pl", "PL");
                break;
            case 5:
                LanguageUtil.changeLanguage(context, "ar", "SA");
                break;
            case 6:
                LanguageUtil.changeLanguage(context, "fr", "rFR");
                break;
            default:
                LanguageUtil.changeLanguage(context, Locale.ENGLISH.getLanguage(), "");
                break;
        }
//        ((Activity) context).recreate();
    }

    public interface LAN_TYPE {
        int CHINESE = 0;
        int TRADITIONAL_CHINESE = 1;
        int ENGLISH = 2;
    }
}
