package leesche.smartrecycling.base.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import leesche.smartrecycling.base.http.HttpMethods;

public class StringUtil {

    public final static int DISTANCE = 1;
    public final static int WEIGHT = 2;
    public final static int BOTTLE = 3;
    private static final int MAX_SIZE = 10;
    public static ArrayList<String> stringList = new ArrayList<>(MAX_SIZE);
    public static ArrayList<String> resultstringList = new ArrayList<>(MAX_SIZE);

    public static void clearString(){
        stringList.clear();
        resultstringList.clear();
    }

    public static String  getErrorHint(int boxCode, int _errorCode, int type) {
        String errorHint = "";
        if (type == 1) {
            switch (_errorCode) {
                case 0:
                    errorHint="（测量仪） 正确";
                    break;
                case 1:
                    errorHint="测量仪错误码"+ _errorCode + "） 测距仪超出量程";
                    break;
                case 2:
                    errorHint="（测量仪错误码"+ _errorCode + "） 超出范围，比空载距离大";
                    break;
                case 3:
                    errorHint="（测量仪错误码"+ _errorCode + "） 参数错误";
                    break;
                case 15:
                    errorHint="（测量仪错误码"+ _errorCode + "） 未接入测距模块";
                    break;
                case 21:
                    errorHint="（测量仪错误码"+ _errorCode + "） 温度过高";
                    break;
                case 22:
                    errorHint="（测量仪错误码"+ _errorCode + "） 温度过低";
                    break;
                case 23:
                    errorHint="（测量仪错误码"+ _errorCode + "） 弱反射";
                    break;
                case 24:
                    errorHint="（测量仪错误码"+ _errorCode + "） 强反射";
                    break;
                case 25:
                    errorHint="（测量仪错误码 "+ _errorCode + " ） 超出量程";
                    break;
                case 26:
                    errorHint="（测量仪错误码 "+ _errorCode + " ） 光敏器件异常";
                    break;
                case 27:
                    errorHint="（测量仪错误码 "+ _errorCode + " ） 激光管异常";
                    break;
                case 28:
                    errorHint="（测量仪错误码 "+ _errorCode + " ） 硬件异常";
                    break;
                default:
                    errorHint="（测量仪错误码 "+ _errorCode + " ） 未定义";
                    break;
            }
            HttpMethods.getInstance().uploadBoxErrorMsg(boxCode + "", _errorCode + 100, errorHint);
        }
        if (type == 2) {
            switch (_errorCode) {
                case 0:
                    errorHint="（智能称） 正确";
                    break;
                case 2:
                    errorHint="（智能称错误码 "+ _errorCode + " ） 不稳定值";
                    break;
                case 4:
                    errorHint="（智能称错误码 "+ _errorCode + " ） 预值错误";
                    break;
                case 15:
                    errorHint="（智能称错误码 "+ _errorCode + " ） 未接入";
                    break;
                default:
                    errorHint="（智能称错误码 "+ _errorCode + " ） 未定义";
                    break;
            }
            HttpMethods.getInstance().uploadBoxErrorMsg(boxCode + "", _errorCode + 200, errorHint);
        }
        if (type == 3) {
            if (_errorCode == 0) {
                errorHint = "（瓶子计数）正确";
            } else {
                errorHint = "（计数器错误码 " + _errorCode + " ） 未定义";
            }
            HttpMethods.getInstance().uploadBoxErrorMsg(boxCode + "", _errorCode + 300, errorHint);
        }
        return errorHint;
    }

    public static int transUnitToInt(String unit){
        if("kg".equalsIgnoreCase(unit)){
            return 0;
        }
        if("个".equalsIgnoreCase(unit)||"per".equalsIgnoreCase(unit)){
            return 1;
        }
        if("次".equalsIgnoreCase(unit)){
            return 2;
        }
        return -1;
    }

    public static String addSpaceToString(String src){
        StringBuilder des = new StringBuilder();
        for (int i=0; i<src.length(); i++){
            des.append(src.charAt(i));
        }
        return des.toString();
    }

    public static void setTextViewStyles(TextView textView, boolean isLeftToRight) {
        int[] colors = {Color.parseColor("#0171F4"), Color.parseColor("#00E3F9"),
                Color.parseColor("#0171F4")};//颜色的数组
        float[] position = {0f, 0.3f, 0.9f};//颜色渐变位置的数组
        LinearGradient mLinearGradient;
        if(isLeftToRight){
            mLinearGradient = new LinearGradient(
                    0,
                    0,
                    textView.getPaint().getTextSize() * textView.getText().length(),
                    0,
                    colors,
                    position,
                    Shader.TileMode.CLAMP);
        }else{
            mLinearGradient = new LinearGradient(
                    0,
                    0,
                    0,
                    textView.getPaint().descent() - textView.getPaint().ascent(),
                    colors,
                    position,
                    Shader.TileMode.CLAMP);
        }
        textView.getPaint().setShader(mLinearGradient);
        textView.invalidate();
    }

    public static void setTextView2Styles(TextView textView, boolean isLeftToRight) {
        int[] colors = {Color.parseColor("#FFFEFD"), Color.parseColor("#A6C3F0")};//颜色的数组
        float[] position = {0f, 0.9f};//颜色渐变位置的数组
        LinearGradient mLinearGradient;
        if(isLeftToRight){
            mLinearGradient = new LinearGradient(
                    0,
                    0,
                    textView.getPaint().getTextSize() * textView.getText().length(),
                    0,
                    colors,
                    position,
                    Shader.TileMode.CLAMP);
        }else{
            mLinearGradient = new LinearGradient(
                    0,
                    0,
                    0,
                    textView.getPaint().descent() - textView.getPaint().ascent(),
                    colors,
                    position,
                    Shader.TileMode.CLAMP);
        }
        textView.getPaint().setShader(mLinearGradient);
        textView.invalidate();
    }

    public static void setTextView3Styles(TextView textView, boolean isLeftToRight) {
        int[] colors = {Color.parseColor("#FAFEFF"), Color.parseColor("#59C8FF")};//颜色的数组
        float[] position = {0f, 0.9f};//颜色渐变位置的数组
        LinearGradient mLinearGradient;
        if(isLeftToRight){
            mLinearGradient = new LinearGradient(
                    0,
                    0,
                    textView.getPaint().getTextSize() * textView.getText().length(),
                    0,
                    colors,
                    position,
                    Shader.TileMode.CLAMP);
        }else{
            mLinearGradient = new LinearGradient(
                    0,
                    0,
                    0,
                    textView.getPaint().descent() - textView.getPaint().ascent(),
                    colors,
                    position,
                    Shader.TileMode.CLAMP);
        }
        textView.getPaint().setShader(mLinearGradient);
        textView.invalidate();
    }


    public static void setTextStyle(TextView tv, String text,int textSize, int start, int end){
        SpannableString styledText = new SpannableString(text);
        styledText.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new AbsoluteSizeSpan(textSize), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(styledText, TextView.BufferType.SPANNABLE);
    }

    public static String formatFloatString(float number) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(number);
    }


    public static void receveTextCmd(String cmd, Activity context, TextView weighresult) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                weighresult.setText(addResultString(cmd));
            }
        });
    }

    public static void sendTextCmd(String cmd, Activity context,TextView weighsend) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                weighsend.setText(addString(cmd));
            }
        });
    }


    public static String addString(String str) {
        if (stringList.size() >= MAX_SIZE) {
            stringList.remove(0); // 移除最早添加的字符串
        }
        stringList.add(str); // 添加新字符串

        return String.join("\n", stringList);
    }


    public static String addResultString(String str) {
        if (resultstringList.size() >= MAX_SIZE) {
            resultstringList.remove(0); // 移除最早添加的字符串
        }
        resultstringList.add(str); // 添加新字符串

        return String.join("\n", resultstringList);
    }

}
