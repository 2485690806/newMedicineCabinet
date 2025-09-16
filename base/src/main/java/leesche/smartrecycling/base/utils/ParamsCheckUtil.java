package leesche.smartrecycling.base.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;

import org.greenrobot.eventbus.EventBus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import leesche.smartrecycling.base.R;
import leesche.smartrecycling.base.common.EventType;
import leesche.smartrecycling.base.eventbus.BasicMessageEvent;

public class ParamsCheckUtil {

    public static boolean isPhone(String phone) {

        //(^((13[0-9])|(14[5-8])|(15([0-3]|[5-9]))|(16[6])|(17[0|4|6|7|8])|(18[0-9])|(19[8-9]))\d{8}$)|(^((170[0|5])|(174[0|1]))\d{7}$)|(^(14[1|4])\d{10}$)
        if (TextUtils.isEmpty(phone)) {
//            EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.USER_HINT_INFO
//                    , "请输入手机号码"));
//            ToastUtil.showErrorMsg("请输入已注册的账号", Gravity.TOP);
            return false;
        }
//        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[0-9]))\\d{8}$";
        if (phone.length() != 11) {
//            EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.USER_HINT_INFO
//                    , "手机号码应为11位数"));
//            ToastUtil.showErrorMsg("手机号码应为11位数", Gravity.TOP);
            return false;
        }
//        else {
//            Pattern p = Pattern.compile(regex);
//            Matcher m = p.matcher(phone);
//            boolean isMatch = m.matches();
//            if (!isMatch) {
//                ToastUtil.showErrorMsg("请填入正确的手机号码");
//            }
//            return isMatch;
        return true;
    }

    public static boolean isHKPhoneLegal(String str) throws PatternSyntaxException {
        // ^ 匹配输入字符串开始的位置
        // \d 匹配一个或多个数字，其中 \ 要转义，所以是 \\d
        // $ 匹配输入字符串结尾的位置
        String regExp = "^([456789])\\d{7}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        // ^ 匹配输入字符串开始的位置
        // \d 匹配一个或多个数字，其中 \ 要转义，所以是 \\d
        // $ 匹配输入字符串结尾的位置
        String regExp = "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[3,5,6,7,8])" +
                "|(18[0-9])|(19[8,9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isPhoneLegal(String str) throws PatternSyntaxException {
        return isChinaPhoneLegal(str) || isHKPhoneLegal(str);
    }
}
