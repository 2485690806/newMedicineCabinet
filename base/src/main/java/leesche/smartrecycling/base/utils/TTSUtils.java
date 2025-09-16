package leesche.smartrecycling.base.utils;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TTSUtils {

     TextToSpeech textToSpeech;
    private static TTSUtils instance;

    private TTSUtils() {
        // Private constructor to prevent instantiation from outside the class
    }

    public static TTSUtils getInstance(Context context) throws IllegalAccessException, InstantiationException {
        if (instance == null) {
            instance = new TTSUtils();
            instance.initTTS(context);
        }
//        instance.initTTS(MainActivity.class.newInstance());
        return instance;
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        initTTS();
//        // 在这里添加需要加载的函数或 class
//        // ...
//    }
    public void initTTS(Context context){
        //实例化自带语音对象
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == textToSpeech.SUCCESS) {
                // Toast.makeText(MainActivity.this,"成功输出语音",
                // Toast.LENGTH_SHORT).show();
                // Locale loc1=new Locale("us");
                // Locale loc2=new Locale("china");

                textToSpeech.setPitch(1.0f);//方法用来控制音调
                textToSpeech.setSpeechRate(1.0f);//用来控制语速

                //判断是否支持下面两种语言
                int result1 = textToSpeech.setLanguage(Locale.US);
                int result2 = textToSpeech.setLanguage(Locale.
                        SIMPLIFIED_CHINESE);
                boolean a = (result1 == TextToSpeech.LANG_MISSING_DATA || result1 == TextToSpeech.LANG_NOT_SUPPORTED);
                boolean b = (result2 == TextToSpeech.LANG_MISSING_DATA || result2 == TextToSpeech.LANG_NOT_SUPPORTED);

                Log.i("zhh_tts", "US支持否？--》" + a +
                        "\nzh-CN支持否》--》" + b);

            } else {
//                     Toast.makeText(MainActivity.this,"成功输出语音",
//                     Toast.LENGTH_SHORT).show();
            }

        });


    }

    public void startAuto(String data) {
        Log.d(TAG, "startAuto: "+data);
        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        textToSpeech.setPitch(1.0f);
        // 设置语速
        textToSpeech.setSpeechRate(1.5f);
        textToSpeech.speak(data,//输入中文，若不支持的设备则不会读出来
                TextToSpeech.QUEUE_FLUSH, null);


    }

    protected void onStop() {
        textToSpeech.stop(); // 不管是否正在朗读TTS都被打断
        textToSpeech.shutdown(); // 关闭，释放资源
    }

}
