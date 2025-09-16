package leesche.smartrecycling.base;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigInteger;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import leesche.smartrecycling.base.utils.DataConvertUtil;
import leesche.smartrecycling.base.utils.DateUtil;

public class IotDeviceConfigBuilder {
    final static String IOT_PROTOCOL_JSON = "https://youyicloud-v2.oss-cn-shanghai.aliyuncs.com/sys/json/iot_%s.json";

    public static JSONArray getConfigJson(String response, String cmd) {
        if (TextUtils.isEmpty(response)) {
            return new JSONArray();
        }
        cmd = cmd.substring(20, cmd.length()-1);
        List<IotDeviceConfigBean> list = JSONArray.parseArray(response).toJavaList(IotDeviceConfigBean.class);
        if (list == null || list.isEmpty()) {
            return new JSONArray();
        }
        IotDeviceConfigBean lastBean = list.get(list.size() - 1);
        int oneLength = (lastBean.getStartIndex() + lastBean.getLength()) * 2;
        JSONArray array = new JSONArray();
        int count = cmd.length() / oneLength;
        if (count == 0) {
            count = 1;
        }
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                array.add(new JSONObject() {{
                    put("id", -1);
                }});
            }
            setValueforBean(list, array, getSubCmd(cmd, oneLength * i, oneLength * (i + 1)));
        }
        return array;
    }

    private static String getSubCmd(String cmd, int start, int end) {
        int length = cmd.length();
        if (length <= start) {
            return "";
        }
        if (length < end) {
            return cmd.substring(start, length);
        }
        return cmd.substring(start, end);
    }

    static void setValueforBean(List<IotDeviceConfigBean> list, JSONArray array, String cmd) {
        if (TextUtils.isEmpty(cmd)) {
            return;
        }
        for (IotDeviceConfigBean bean : list) {
            if (!"备用".equals(bean.getName())) {
                array.add(new JSONObject() {{
                    put("id", bean.getStartIndex());
                    put("name", bean.getName());
                    put("value", IotDeviceConfigBuilder.getBeanValue(cmd, bean));
                }});
                bean.setValue(IotDeviceConfigBuilder.getBeanValue(cmd, bean)+"");
            }
        }
    }

    static Object getBeanValue(String data, IotDeviceConfigBean bean) {
        int startIndex = bean.getStartIndex() * 2;
        int endIndex = (bean.getStartIndex() + bean.getLength()) * 2;
        if (data.length() < endIndex) {
            return null;
        }
        String subStr = data.substring(startIndex, endIndex);
        try {
            return getValue(bean, subStr);
        } catch (Exception e) {
            return "解析错误[" + subStr + "]";
        }
    }

    public static Object getValue(IotDeviceConfigBean bean, String subStr) {
        Object object = formatHexValue("0x" + subStr.toLowerCase(), bean);
        if (object != null) {
            return object;
        }
        if (bean.getClassType().equals(String.class.getName())) {
            String value = hexStrToString(subStr);
            return formatValue(value, bean);
        }
        if (bean.getClassType().equals(Integer.class.getName())) {
            Integer value = hexStrToInteger(subStr);
            return formatValue(value.toString(), bean);
        }
        if (bean.getClassType().equals(Time.class.getName())) {
            Integer value = hexStrToInteger(subStr);
            if (value != null) {
                return String.format("%02d", (value / 60)) + ":" + String.format("%02d", (value % 60));
            }
        }
        if (bean.getClassType().equals(Date.class.getName())) {
            if ("ffffffff".equalsIgnoreCase(subStr)) {
                return "无效";
            }
            Integer value = hexStrToInteger(subStr);
            if (value != null) {
                Date date = new Date(value * 1000L);
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            }
        }
        return "";
    }

    private static Object formatHexValue(String hexValue, IotDeviceConfigBean bean) {
        if (bean.getFormat() != null) {
            if (bean.getFormat().containsKey(hexValue)) {
                return bean.getFormat().get(hexValue);
            }
        }
        return null;
    }

    private static Object formatValue(String value, IotDeviceConfigBean bean) {
        if (bean.getFormat() != null) {
            if (bean.getFormat().containsKey(value)) {
                return bean.getFormat().get(value);
            }
            return value;
        }
        if (bean.getUnit() != null) {
            return value + "(" + bean.getUnit() + ")";
        }
        return value;
    }

    public static Integer hexStrToInteger(String hexString) {
        return new BigInteger(hexString, 16).intValue();
    }

    public static String hexStrToString(String hexString) {
        return DataConvertUtil.hexStrToStr(hexString);
    }

    public static class IotDeviceConfigBean {
        private String name;
        private Integer startIndex;
        private Integer length;
        private String classType;
        private String unit;
        private Map<String, String> format;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(Integer startIndex) {
            this.startIndex = startIndex;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        public String getClassType() {
            return classType;
        }

        public void setClassType(String classType) {
            this.classType = classType;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public Map<String, String> getFormat() {
            return format;
        }

        public void setFormat(Map<String, String> format) {
            this.format = format;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public IotDeviceConfigBean() {
        }

        public IotDeviceConfigBean(String name, Integer startIndex, Integer length, Class classType, String unit, Map<String, String> format) {
            this.name = name;
            this.startIndex = startIndex;
            this.length = length;
            this.classType = classType.getName();
            this.unit = unit;
            this.format = format;
        }
    }
}
