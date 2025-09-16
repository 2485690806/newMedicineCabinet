package leesche.smartrecycling.base.utils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataConvertUtil {

    /**
     * 将int类型的数据转换为byte数组
     *
     * @param n int数据
     * @return 生成的byte数组
     */
    public static byte[] intToBytes(int n) {
        String s = String.valueOf(n);
        return s.getBytes();
    }

    /**
     * 将byte数组转换为int数据
     *
     * @param b 字节数组
     * @return 生成的int数据
     */
    public static int bytesToInt(byte[] b) {
        String s = new String(b);
        return Integer.parseInt(s);
    }

    public static int bytesToInt(byte b1, byte b2) {
        return (b1 & 0xFF) | ((b2 & 0xFF) << 8);
    }

    public static int byte2int(byte[] res) {
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000

        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或
                | ((res[2] << 24) >>> 8) | (res[3] << 24);
        return targets;
    }

    /**
     * 字节数组转十六进制字符串
     *
     * @param btyes 待转换的字节数组
     * @return
     */
    public static String byte2HexStr(byte[] btyes) {
        String temp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < btyes.length; n++) {
            temp = Integer.toHexString(btyes[n] & 0xFF);
            sb.append((temp.length() == 1) ? "0" + temp : temp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    public static String byte2HexStr2(byte[] btyes) {
        String temp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < btyes.length; n++) {
            temp = Integer.toHexString(btyes[n] & 0xFF);
            sb.append((temp.length() == 1) ? "0" + temp : temp);
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] hexStrtoBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }
        str = str.trim();
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    public static String hexStrToStr(String hexStr){
        byte[] resultBytes = hexStrtoBytes(hexStr);
        try {
            return new String(resultBytes, "UTF-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int hexStrToInteger(String hexStr){
        byte[] resultBytes = hexStrtoBytes(hexStr);
        return bytesToIntLittle(resultBytes, 0);
    }

    public static int hexStrToBigInteger(String hexStr){
        byte[] resultBytes = hexStrtoBytes(hexStr);
        return bytesToIntBig(resultBytes, 0);
    }

    public static int hexStrToBigInteger2(String hexStr){
        byte srcByte = (byte) Integer.parseInt(hexStr, 16);
        return srcByte & 0xFF;
    }

    public static long hexStrToBigLong(String hexStr){
        byte[] resultBytes = hexStrtoBytes(hexStr);
        return bytesToLong(resultBytes);
    }

    /**
     * 获取byte的实际值
     *
     * @param bytes
     * @return 实际长度的byte[]
     */
    public static byte[] getCopyByte(byte[] bytes) {
        if (null == bytes || 0 == bytes.length)
            return new byte[1];
        int length = getValidLength(bytes);
        byte[] bb = new byte[length];
        System.arraycopy(bytes, 0, bb, 0, length);
        return bb;
    }

    /**
     * 获取byte的实际长度
     *
     * @param bytes
     * @return
     */
    public static int getValidLength(byte[] bytes) {
        int i = 0;
        if (null == bytes || 0 == bytes.length)
            return i;
        for (; i < bytes.length; i++) {
            if (bytes[i] == '\0')
                break;
        }
        return i + 1;
    }

    /**
     * 16进制字符串转换为字符串
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gbk");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static String formatPrice(int a, int b) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format((float) a / b);
    }

    public static String formatAmount(double amount) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(amount);
    }

    /**
     * 以大端模式将int转成byte[]
     */
    public static byte[] intToBytesBig(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 以小端模式将int转成byte[]
     *
     * @param value
     * @return
     */
    public static byte[] intToBytesLittle(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    public static byte[] intToBytesLittle2(int value) {
        byte[] src = new byte[2];
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 以大端模式将byte[]转成int
     */
    public static int bytesToIntBig(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF));
        return value;
    }

    /**
     * 将字节数组转换为long整型数值
     *
     * @param arr
     * @return
     */
    public static long bytesToLong(byte[] arr) {
        int mask = 0xFF;
        int temp = 0;
        long result = 0;
        int len = Math.min(8, arr.length);
        for (int i = 0; i < len; i++) {
            result <<= 8;
            temp = arr[i] & mask;
            result |= temp;
        }
        return result;
    }

    /**
     * 以小端模式将byte[]转成int
     */
    public static int bytesToIntLittle(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }


    public static int bytesToIntLittleX(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0x7F) << 24));
        if ((src[offset + 3] & 0x80) != 0)
        {
            value = -value;
        }
        return value;
    }

    public static int getSecondTimestampTwo(Date date){
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime()/1000);
        return Integer.parseInt(timestamp);
    }
}
