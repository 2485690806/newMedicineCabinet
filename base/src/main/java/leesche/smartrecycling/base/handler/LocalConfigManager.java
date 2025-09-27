package leesche.smartrecycling.base.handler;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.leesche.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import leesche.smartrecycling.base.common.Constants;
import leesche.smartrecycling.base.entity.AdEntity;
import leesche.smartrecycling.base.utils.FileUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalConfigManager {
    List<AdEntity> idleBannerList = new ArrayList<>();
    List<AdEntity> startBannerList = new ArrayList<>();
    List<AdEntity> finishBannerList = new ArrayList<>();
    List<AdEntity> cancelBannerList = new ArrayList<>();
    Gson gson = new Gson();

    private static final class LocalConfigManagerHolder {
        static final LocalConfigManager ToDiLogicHandler = new LocalConfigManager();
    }

    public static LocalConfigManager getInstance() {
        return LocalConfigManager.LocalConfigManagerHolder.ToDiLogicHandler;
    }


    public void loadAdDataFromLocal() {
        if (new File(Constants.AD_CACHE_DIR).exists()) {
            String adDataJsonStr = FileUtil.readFileSdcardFile(Constants.AD_CACHE_DIR);
            if (!TextUtils.isEmpty(adDataJsonStr)) {
                JsonArray jsonArray = new JsonParser().parse(adDataJsonStr).getAsJsonArray();
                parseAdJsonToDisplay(jsonArray, false);
            }
        } else {
            idleBannerList = new ArrayList<>();
            Logger.i("【AD】 ad file not exist when load local ad with no network");
        }
//        addLocalDefaultAd(true);
    }

    public void parseAdJsonToDisplay(JsonArray jsonArray, boolean haveNet) {
        AdEntity adEntity = null;
        if (jsonArray != null && jsonArray.size() > 0) {
            if (haveNet) {
                FileUtil.writeFileSdcardFile(Constants.AD_CACHE_DIR, jsonArray.toString());
            }
            if (idleBannerList.size() > 0) idleBannerList.clear();
            List<String> videoUrls = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (gson == null) gson = new Gson();
                adEntity = gson.fromJson(jsonArray.get(i), AdEntity.class);
                if ("app-screen".equals(adEntity.getModule())) {
                    adEntity.setId(idleBannerList.size());
                    idleBannerList.add(adEntity);
                }
            }

        } else {
            FileUtil.deleteSingleFile(Constants.AD_CACHE_DIR);
        }
    }

    public void loadStartAdDataFromLocal() {
        if (new File(Constants.START_AD_CACHE_DIR).exists()) {
            String adDataJsonStr = FileUtil.readFileSdcardFile(Constants.START_AD_CACHE_DIR);
            if (!TextUtils.isEmpty(adDataJsonStr)) {
                JsonArray jsonArray = new JsonParser().parse(adDataJsonStr).getAsJsonArray();
                parseStartAdJsonToDisplay(jsonArray, false);
            }
        } else {
            Logger.i("【AD】 ad file not exist when load local ad with no network");
        }
//        addLocalDefaultAd(true);
    }

    public void parseStartAdJsonToDisplay(JsonArray jsonArray, boolean haveNet) {
        AdEntity adEntity = null;
        if (jsonArray != null && jsonArray.size() > 0) {
            if (haveNet) {
                FileUtil.writeFileSdcardFile(Constants.START_AD_CACHE_DIR, jsonArray.toString());
            }
            if (startBannerList.size() > 0) startBannerList.clear();
            List<String> videoUrls = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (gson == null) gson = new Gson();
                adEntity = gson.fromJson(jsonArray.get(i), AdEntity.class);
                if ("app-screen".equals(adEntity.getModule())) {
                    adEntity.setId(startBannerList.size());
                    startBannerList.add(adEntity);
                }
            }

        } else {
//            createAd(ad_default);
        }
    }

    public void loadFinishAdDataFromLocal() {
        if (new File(Constants.FINISH_AD_CACHE_DIR).exists()) {
            String adDataJsonStr = FileUtil.readFileSdcardFile(Constants.FINISH_AD_CACHE_DIR);
            if (!TextUtils.isEmpty(adDataJsonStr)) {
                JsonArray jsonArray = new JsonParser().parse(adDataJsonStr).getAsJsonArray();
                parseFinishAdJsonToDisplay(jsonArray, false);
            }
        } else {
            Logger.i("【AD】 ad file not exist when load local ad with no network");
        }
//        addLocalDefaultAd(true);
    }

    public void parseFinishAdJsonToDisplay(JsonArray jsonArray, boolean haveNet) {
        AdEntity adEntity = null;
        if (jsonArray != null && jsonArray.size() > 0) {
            if (haveNet) {
                FileUtil.writeFileSdcardFile(Constants.FINISH_AD_CACHE_DIR, jsonArray.toString());
            }
            if (finishBannerList.size() > 0) finishBannerList.clear();
            List<String> videoUrls = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (gson == null) gson = new Gson();
                adEntity = gson.fromJson(jsonArray.get(i), AdEntity.class);
                if ("app-screen".equals(adEntity.getModule())) {
                    adEntity.setId(finishBannerList.size());
                    finishBannerList.add(adEntity);
                }
            }

        } else {
//            createAd(ad_default);
        }
    }

    public void loadCancelAdDataFromLocal() {
        if (new File(Constants.CANCEL_AD_CACHE_DIR).exists()) {
            String adDataJsonStr = FileUtil.readFileSdcardFile(Constants.CANCEL_AD_CACHE_DIR);
            if (!TextUtils.isEmpty(adDataJsonStr)) {
                JsonArray jsonArray = new JsonParser().parse(adDataJsonStr).getAsJsonArray();
                parseCancelAdJsonToDisplay(jsonArray, false);
            }
        } else {
            Logger.i("【AD】 ad file not exist when load local ad with no network");
        }
//        addLocalDefaultAd(true);
    }

    public void parseCancelAdJsonToDisplay(JsonArray jsonArray, boolean haveNet) {
        AdEntity adEntity = null;
        if (jsonArray != null && jsonArray.size() > 0) {
            if (haveNet) {
                FileUtil.writeFileSdcardFile(Constants.CANCEL_AD_CACHE_DIR, jsonArray.toString());
            }
            if (cancelBannerList.size() > 0) cancelBannerList.clear();
            List<String> videoUrls = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (gson == null) gson = new Gson();
                adEntity = gson.fromJson(jsonArray.get(i), AdEntity.class);
                if ("app-screen".equals(adEntity.getModule())) {
                    adEntity.setId(cancelBannerList.size());
                    cancelBannerList.add(adEntity);
                }
            }

        } else {
//            createAd(ad_default);
        }
    }
}
