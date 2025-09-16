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
//                    if (adEntity.getImgHref().endsWith("mp4")) {
//                        if (!adEntity.getImgHref().startsWith("http")) {
//                            adEntity.setImgHref(Constants.PROTOCOL_HEADER + adEntity.getImgHref());
//                        }
//                    } else {
//                        if (!adEntity.getImgHref().startsWith("http")) {
//                            adEntity.setImgHref(Constants.PROTOCOL_HEADER + adEntity.getImgHref()
//                                    + Constants.ad_small_suffix);
//                        } else {
//                            adEntity.setImgHref(adEntity.getImgHref() + Constants.ad_small_suffix);
//                        }
//                    }
//                    String localPath = SystemFuncHandler.getInstance().getLocalFilePath(adEntity.getImgHref());
//                    adEntity.setLocalCache(localPath);
                    idleBannerList.add(adEntity);
//                    if (!new File(localPath).exists()) {
//                        videoUrls.add(adEntity.getImgHref());
//                    }
                }
            }
//            if (haveNet) {
//                SystemFuncHandler.getInstance().downloadVideoFile(videoUrls);
//            }
        } else {
//            createAd(ad_default);
        }
    }
}
