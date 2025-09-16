package leesche.smartrecycling.base.service;


import com.alibaba.android.arouter.facade.template.IProvider;
import com.google.gson.JsonObject;

import io.reactivex.rxjava3.core.Observer;


public interface IceExportService extends IProvider {

    void getDeviceAd(Observer<JsonObject> observer, String deviceCode, int screenNumber);

}
