package leesche.smartrecycling.base.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import leesche.smartrecycling.base.R;
import leesche.smartrecycling.base.common.Constants;

public class SoundPoolHelper {

    /*常量*/
    public final static int TYPE_MUSIC = AudioManager.STREAM_MUSIC;
    public final static int TYPE_ALARM = AudioManager.STREAM_ALARM;
    public final static int TYPE_RING = AudioManager.STREAM_RING;

    public final static int RING_TYPE_MUSIC = RingtoneManager.TYPE_ALARM;
    public final static int RING_TYPE_ALARM = RingtoneManager.TYPE_NOTIFICATION;
    public final static int RING_TYPE_RING = RingtoneManager.TYPE_RINGTONE;

    /*变量*/
    private SoundPool soundPool;
    private int NOW_RINGTONE_TYPE = RingtoneManager.TYPE_NOTIFICATION;
    private int maxStream;
    private Map<String, Integer> ringtoneIds;
    private int lastStream = 0;
    Context context;

    private static SoundPoolHelper soundPoolHelper = null;

    public static SoundPoolHelper getInstance(Context context) {
        if (soundPoolHelper == null) {
            String language = context.getResources().getConfiguration().locale.getCountry();
            soundPoolHelper = new SoundPoolHelper(29, SoundPoolHelper.TYPE_MUSIC).setRingtoneType(SoundPoolHelper.RING_TYPE_MUSIC);
            String audioPath = Constants.BASE_CACHE_DIR + File.separator + "yyy_config" + File.separator + "audio";
            if (new File(audioPath).exists()) {
                File file = new File(audioPath);
                File[] files = file.listFiles();
                if (files != null) {
                    for (File value : files) {
                        String fileName = value.getName().replace(".ogg", "").replace(".OGG", "");
                        soundPoolHelper.load(context, fileName, value.getAbsolutePath());
//                        Logger.i("【System】 audio load: " + value.getAbsolutePath());
                    }
                } else {
                    loadDefaultAudio(context, language);
                }
            } else {
                loadDefaultAudio(context, language);
            }
        }
        return soundPoolHelper;
    }

    private static void loadDefaultAudio(Context context, String language) {
//        Logger.i("【Audio】" + "loading file form local");
        if ("SG".equalsIgnoreCase(language)) {
            initSGDefaultAudio(context);
            return;
        }
        if ("CN".equalsIgnoreCase(language)) {
            initDefaultAudio(context);
            return;
        }
        initCommonDefaultAudio(context);
    }

    private static void initCommonDefaultAudio(Context context) {
        if (new File(Constants.WELCOME_AUDIO_OGG).exists()) {
            soundPoolHelper.load(context, "welcome", Constants.WELCOME_AUDIO_OGG);
        }
        if (new File(Constants.LOGIN_AUDIO_OGG).exists()) {
            soundPoolHelper.load(context, "login", Constants.LOGIN_AUDIO_OGG);
        } else {
            soundPoolHelper.load(context, "login", R.raw.login);
        }
        soundPoolHelper.load(context, "counter", R.raw.counter);
    }

    private static void initSGDefaultAudio(Context context) {
        soundPoolHelper.load(context, "welcome", R.raw.welcome2en)
                .load(context, "open", R.raw.delivery2en)
                .load(context, "follow", R.raw.thank_you2en);
    }

    private static void initDefaultAudio(Context context) {
        soundPoolHelper.load(context, "type", R.raw.type)
                .load(context, "open", R.raw.open)
                .load(context, "close", R.raw.close)
                .load(context, "no_close", R.raw.no_close)
                .load(context, "open2", R.raw.open2)
                .load(context, "thank", R.raw.thank)
                .load(context, "commodity", R.raw.commodity)
                .load(context, "time_over", R.raw.timeover)
                .load(context, "paper", R.raw.paper)
                .load(context, "follow", R.raw.follow)
                .load(context, "a0", R.raw.a0)
                .load(context, "a1", R.raw.a1)
                .load(context, "a2", R.raw.a2)
                .load(context, "a3", R.raw.a3)
                .load(context, "a4", R.raw.a4)
                .load(context, "a5", R.raw.a5)
                .load(context, "a6", R.raw.a6)
                .load(context, "a7", R.raw.a7)
                .load(context, "a8", R.raw.a8)
                .load(context, "a9", R.raw.a9)
                .load(context, "take_photo", R.raw.take_photo_hint)
                .load(context, "face_identify", R.raw.face_identify_hint)
                .load(context, "finish_photo", R.raw.finish_photo_hint)
                .load(context, "take_photo1", R.raw.take_photo_hint1)
                .load(context, "finish_deliver", R.raw.finish_delivery_hint)
                .load(context, "closing", R.raw.opening)
                .load(context, "opening", R.raw.closing);
        if (new File(Constants.WELCOME_AUDIO_OGG).exists()) {
            soundPoolHelper.load(context, "welcome", Constants.WELCOME_AUDIO_OGG);
        }
        if (new File(Constants.LOGIN_AUDIO_OGG).exists()) {
            soundPoolHelper.load(context, "login", Constants.LOGIN_AUDIO_OGG);
        } else {
            soundPoolHelper.load(context, "login", R.raw.login);
        }
    }

    public static SoundPoolHelper getInstance2(Context context) {
        if (soundPoolHelper == null) {
            String audioPath = Constants.BASE_CACHE_DIR + File.separator + "yyy_config" + File.separator + "audio";
            if (new File(audioPath).exists()) {
                File file = new File(audioPath);
                File[] files = file.listFiles();
                if (files != null) {
                    soundPoolHelper = new SoundPoolHelper(files.length + 1, SoundPoolHelper.TYPE_MUSIC)
                            .setRingtoneType(SoundPoolHelper.RING_TYPE_MUSIC);
                    for (File value : files) {
                        String fileName = value.getName().replace(".ogg", "").replace(".OGG", "");
                        soundPoolHelper.load(context, fileName, value.getAbsolutePath());
//                        Logger.i("【Audio】path of file：" + value.getAbsolutePath());
                    }
                } else {
//                    Logger.e("【Audio】" + "loading file from local");
                    soundPoolHelper.load(context, "closing", R.raw.closing)
                            .load(context, "opening", R.raw.opening);
                }
                soundPoolHelper.load(context, "beep", R.raw.beep);
            } else {
                soundPoolHelper = new SoundPoolHelper(4, SoundPoolHelper.TYPE_MUSIC)
                        .setRingtoneType(SoundPoolHelper.RING_TYPE_MUSIC);
//                Logger.e("【Audio】" + "loading file from local");
                soundPoolHelper.load(context, "closing", R.raw.closing)
                        .load(context, "opening", R.raw.opening)
                        .load(context, "beep", R.raw.beep);
            }
            soundPoolHelper.context = context;
        }
        return soundPoolHelper;
    }

    public static SoundPoolHelper getInstance3(Context context) {
        if (soundPoolHelper == null) {
            soundPoolHelper = new SoundPoolHelper(1, SoundPoolHelper.TYPE_MUSIC)
                    .setRingtoneType(SoundPoolHelper.RING_TYPE_MUSIC);
            soundPoolHelper.load(context, "beep", R.raw.beep);
        }
        return soundPoolHelper;
    }

    public static void setSoundPoolHelper(SoundPoolHelper soundPoolHelper) {
        SoundPoolHelper.soundPoolHelper = soundPoolHelper;
    }

    /*方法*/

    public SoundPoolHelper() {
        this(1, TYPE_MUSIC);
    }

    public SoundPoolHelper(int maxStream) {
        this(maxStream, TYPE_ALARM);
    }

    public SoundPoolHelper(int maxStream, int streamType) {
        soundPool = new SoundPool(maxStream, streamType, 1);
        this.maxStream = maxStream;
        ringtoneIds = new HashMap<>();
    }

    /**
     * 设置RingtoneType，这只是关系到加载哪一个默认音频
     * 需要在load之前调用
     *
     * @param ringtoneType ringtoneType
     * @return this
     */
    public SoundPoolHelper setRingtoneType(int ringtoneType) {
        NOW_RINGTONE_TYPE = ringtoneType;
        return this;
    }

    /**
     * 加载音频资源
     *
     * @param context 上下文
     * @param resId   资源ID
     * @return this
     */
    public SoundPoolHelper load(Context context, @NonNull String ringtoneName, @RawRes int resId) {
        if (maxStream == 0)
            return this;
        maxStream--;
        ringtoneIds.put(ringtoneName, soundPool.load(context, resId, 1));
        return this;
    }

    /**
     * 加载默认的铃声
     *
     * @param context 上下文
     * @return this
     */
    public SoundPoolHelper loadDefault(Context context) {
        Uri uri = getSystemDefaultRingtoneUri(context);
        if (uri == null)
            load(context, "welcome", R.raw.welcome);
        else
            load(context, "default", DensityUtil.uri2Path(context, uri));
        return this;
    }

    /**
     * 加载铃声
     *
     * @param context      上下文
     * @param ringtoneName 自定义铃声名称
     * @param ringtonePath 铃声路径
     * @return this
     */
    public SoundPoolHelper load(Context context, @NonNull String ringtoneName, @NonNull String ringtonePath) {
        if (maxStream == 0)
            return this;
        maxStream--;
        ringtoneIds.put(ringtoneName, soundPool.load(ringtonePath, 1));
        return this;
    }

    /**
     * int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) ：
     * 1)该方法的第一个参数指定播放哪个声音；
     * 2) leftVolume 、
     * 3) rightVolume 指定左、右的音量：
     * 4) priority 指定播放声音的优先级，数值越大，优先级越高；
     * 5) loop 指定是否循环， 0 为不循环， -1 为循环；
     * 6) rate 指定播放的比率，数值可从 0.5 到 2 ， 1 为正常比率。
     */
    public void play(@NonNull String ringtoneName, boolean isLoop, boolean isHigh) {
//        String[] hour_minute = ConfigManager.getInstance().getConfigEntity().getAudioOnTime().split(":");
//        int hour = Integer.parseInt(hour_minute[0]);
//        int minute = Integer.parseInt(hour_minute[1]);
//        String[] hour_minute_end = ConfigManager.getInstance().getConfigEntity().getAudioOffTime().split(":");
//        int hour_end = Integer.parseInt(hour_minute_end[0]);
//        int minute_end = Integer.parseInt(hour_minute_end[1]);
//        Constants.VOLUME_HIGH = DateUtil.isCurrentInTimeScope(hour, minute, hour_end, minute_end);
        soundPool.stop(lastStream);
        if (ringtoneIds.containsKey(ringtoneName)) {
            if (Constants.VOLUME_HIGH) {
                if ("time_over".equals(ringtoneName)) {
                    lastStream = soundPool.play(ringtoneIds.get(ringtoneName), 1f
                            , 1f, 1, 2, 1);
                } else {
                    lastStream = soundPool.play(ringtoneIds.get(ringtoneName), 1f
                            , 1f, 1, isLoop ? -1 : 0, 1);
                }
            }
        }
    }


    public void play(@NonNull String ringtoneName, int count) {
//        String[] hour_minute = ConfigManager.getInstance().getConfigEntity().getAudioOnTime().split(":");
//        int hour = Integer.parseInt(hour_minute[0]);
//        int minute = Integer.parseInt(hour_minute[1]);
//        String[] hour_minute_end = ConfigManager.getInstance().getConfigEntity().getAudioOffTime().split(":");
//        int hour_end = Integer.parseInt(hour_minute_end[0]);
//        int minute_end = Integer.parseInt(hour_minute_end[1]);
//        Constants.VOLUME_HIGH = DateUtil.isCurrentInTimeScope(hour, minute, hour_end, minute_end);
        soundPool.stop(lastStream);
        if (ringtoneIds.containsKey(ringtoneName) && Constants.VOLUME_HIGH) {
            lastStream = soundPool.play(ringtoneIds.get(ringtoneName), 1f
                    , 1f, 1, count, 1);
        }
    }

    public void play2(@NonNull String ringtoneName, int count) {
        String lastSetLan = (String) SharedPreferencesUtils.get(context, LanguageUtil.LAST_SET_LAN, "zh|");
        if(lastSetLan != null && lastSetLan.contains("zh")){
//            String[] hour_minute = ConfigManager.getInstance().getConfig2Entity().getAudioOnTime().split(":");
//            int hour = Integer.parseInt(hour_minute[0]);
//            int minute = Integer.parseInt(hour_minute[1]);
//            String[] hour_minute_end = ConfigManager.getInstance().getConfig2Entity().getAudioOffTime().split(":");
//            int hour_end = Integer.parseInt(hour_minute_end[0]);
//            int minute_end = Integer.parseInt(hour_minute_end[1]);
//            Constants.VOLUME_HIGH = DateUtil.isCurrentInTimeScope(hour, minute, hour_end, minute_end);
            soundPool.stop(lastStream);
            if (ringtoneIds.containsKey(ringtoneName) && Constants.VOLUME_HIGH) {
                lastStream = soundPool.play(ringtoneIds.get(ringtoneName), 1f
                        , 1f, 1, count, 1);
            }
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (soundPool != null) {
            soundPool.release();
        }
        soundPoolHelper = null;

    }

    public void resetSoundPoolHelper(Context context) {
        release();
        getInstance2(context);
    }

    /**
     * 获取系统默认铃声的Uri
     *
     * @param context 上下文
     * @return uri
     */
    private Uri getSystemDefaultRingtoneUri(Context context) {
        try {
            return RingtoneManager.getActualDefaultRingtoneUri(context, NOW_RINGTONE_TYPE);
        } catch (Exception e) {
            return null;
        }
    }

}
