package leesche.smartrecycling.base.service;


import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface TTSExportService extends IProvider {

    /**
     *  初始化TTS
     */
    void initTTS(boolean isTest, Context context, ICommonCallback callback);

    /**
     * 播报TTS 内容
     *
     * @param content 播报内容
     * @param type    播报方式
     */
    void speak(String content, int type);

    /**
     * 释放TTS
     */
    void releaseTTS();

}
