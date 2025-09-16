//package com.ycmachine.smartdevice.utils;
//
//import static android.content.ContentValues.TAG;
//import static tp.xmaihh.serialport.utils.ByteUtil.Byte2Hex;
//
//import android.util.Log;
//
//import java.nio.charset.Charset;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class TrayUtils {
//
//
//    public static void main(String[] args) {
//
////        byte[] bytes = convertToDisplayData("1.23", getByteLength("1.23"));
////        System.out.println(toHexString(bytes));
//
//        List<Integer> leftNowWeightList = Arrays.asList(10, 20, 30, 0, 0);
//        List<Integer> leftWeightList = Arrays.asList(10, 10, 50, 0, 0);
//        Map<Integer, Integer> resultMap = compareWeights(leftNowWeightList, leftWeightList);
//        System.out.println(resultMap);
//    }
//
//    public static Map<Integer, Integer> compareWeights(List<Integer> leftNowWeightList, List<Integer> leftWeightList) {
//        // 检查输入是否合法
//        if (leftNowWeightList == null || leftWeightList == null || leftNowWeightList.size() != leftWeightList.size()) {
////            throw new IllegalArgumentException("输入数组不能为空，且长度必须相同");
//            Log.e(TAG, "输入数组不能为空，且长度必须相同");
//            return null;
//        }
//
//        // 创建结果 Map
//        Map<Integer, Integer> resultMap = new HashMap<>();
//
//        // 遍历数组
//        for (int i = 0; i < leftNowWeightList.size(); i++) {
//            int nowWeight = leftNowWeightList.get(i);
//            int originalWeight = leftWeightList.get(i);
//
//            if (nowWeight == 32767 || originalWeight == 32767) {
//                continue;
//            }
//            if (nowWeight < 0) {
//                nowWeight = 0;
//            }
//            if (originalWeight < 0) {
//                originalWeight = 0;
//            }
//
//
//            // 判断重量是否减少
//            if (nowWeight < originalWeight) {
//                int reducedWeight = originalWeight - nowWeight;
//
//                int threshold = 10; // 阈值 （g)
//
//                if (reducedWeight > threshold) { // 如果重量减少超过阈值
//
//                    resultMap.put(i, reducedWeight); // 记录柜子编号和减少的重量
//                }
////                resultMap.put(i, reducedWeight);
//            } else if (nowWeight > originalWeight) { // 某个柜子的重量增加了，说明有人误操作了，不停止他的订单
//
//                int reducedWeight = nowWeight - originalWeight;
//
//                int threshold = 10; // 阈值 （g)
//
//                if (reducedWeight > threshold) { // 如果重量增加少超过阈值
//
//                    return null;
//
//                }
//
//
//            }
//        }
//
//        return resultMap;
//    }
//
//    /**
//     * 获取左边柜子称重数据
//     */
//    public static List<Integer> getLeftWeight(byte[] bytes) {
//        if (bytes == null || bytes.length < 8) {
////            throw new IllegalArgumentException("数据长度不足");
//            Log.e(TAG, "getLeftWeight: 数据长度不足");
//            return null;
//        }
//        try {
//
//            int leftWeight1 = parseWeight(bytes, 3); // 1
//            int leftWeight2 = parseWeight(bytes, 7); // 3
//            int leftWeight3 = parseWeight(bytes, 11); // 5
//            int leftWeight4 = parseWeight(bytes, 15); // 7
//            int leftWeight5 = parseWeight(bytes, 19); // 9
//            return Arrays.asList(leftWeight1, leftWeight2, leftWeight3, leftWeight4, leftWeight5);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    /**
//     * 获取右边边柜子称重数据
//     */
//    public static List<Integer> getRightWeight(byte[] bytes) {
//        if (bytes == null || bytes.length < 8) {
////            throw new IllegalArgumentException("数据长度不足");
//            Log.e(TAG, "getRightWeight: 数据长度不足");
//            return null;
//        }
//        try {
//            int leftWeight1 = parseWeight(bytes, 5); // 2
//            int leftWeight2 = parseWeight(bytes, 9); // 4
//            int leftWeight3 = parseWeight(bytes, 13); // 6
//            int leftWeight4 = parseWeight(bytes, 17); // 8
//            int leftWeight5 = parseWeight(bytes, 21); // 10
//            return Arrays.asList(leftWeight1, leftWeight2, leftWeight3, leftWeight4, leftWeight5);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//
//    /**
//     * 解析称重数据
//     *
//     * @param bytes      数据帧
//     * @param startIndex 目标数据的起始索引
//     * @return 解析后的重量值
//     */
//    public static int parseWeight(byte[] bytes, int startIndex) {
//        if (bytes == null || bytes.length < startIndex + 2) {
//            Log.e(TAG, "parseWeight: 数据长度不足");
//            throw new IllegalArgumentException("数据长度不足");
////            return 0;
//        }
//        byte highByte = bytes[startIndex];
//        byte lowByte = bytes[startIndex + 1];
//        return (short) (((highByte & 0xFF) << 8) | (lowByte & 0xFF));
//    }
//
//    /**
//     * 解析称重数据
//     *
//     * @param bytes      数据帧
//     * @param startIndex 目标数据的起始索引
//     * @return 解析后的重量值
//     */
//    public static int parseWeight1(byte[] bytes, int startIndex) {
//        if (bytes == null || bytes.length < startIndex + 2) {
//            throw new IllegalArgumentException("数据长度不足");
//        }
//        byte highByte = bytes[startIndex];
//        byte lowByte = bytes[startIndex + 1];
//        return (short) (((highByte & 0xFF) << 8) | (lowByte & 0xFF));
//    }
//
//
//    public static String toVoiceString(String str) { // 获取语音播报的命令
//        byte[] gb2312Bytes = convertToGB2312(str);
//        String VoiceTest = toHexString(gb2312Bytes);
//
//        int charLength = getCharLength(str);
//        int byteLength = getByteLength(str);
//
//        return "0110000300" + Integer.toHexString(charLength) + Integer.toHexString(byteLength) + VoiceTest;
//    }
//
//    public static byte[] convertToGB2312(String input) {
//        // 使用GB2312编码将字符串转换为字节数组
//        byte[] gb2312Bytes = input.getBytes(Charset.forName("GB2312"));
//
//        // 如果字节数组长度不足8字节，则在末尾补零
//        if (gb2312Bytes.length < 8) {
//            byte[] paddedBytes = new byte[8];
//            System.arraycopy(gb2312Bytes, 0, paddedBytes, 0, gb2312Bytes.length);
//            return paddedBytes;
//        }
//
//        return gb2312Bytes;
//    }
//
//    /**
//     * Convert a byte array into its hex string equivalent.
//     */
////    public static String toHexString(byte[] data) {
////        char[] chars = new char[data.length * 2];
////        for (int i = 0; i < data.length; i++) {
////            chars[i * 2] = HEX_DIGITS[(data[i] >> 4) & 0xf];
////            chars[i * 2 + 1] = HEX_DIGITS[data[i] & 0xf];
////        }
////        return new String(chars).toLowerCase();
////    }
////    static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
//    public static String toHexString(byte[] inBytArr) {
//        StringBuilder strBuilder = new StringBuilder();
//        int j = inBytArr.length;
//        for (int i = 0; i < j; i++) {
//            strBuilder.append(Byte2Hex(Byte.valueOf(inBytArr[i])));
//            strBuilder.append("");
//        }
//        return strBuilder.toString();
//    }
//
//    /**
//     * 将字符串转换为数码管显示的字节数组
//     *
//     * @param input       输入字符串（例如 "1.2.3.4"）
//     * @param totalLength 总字节长度（例如 8）
//     * @return 符合规则的字节数组
//     */
//    public static byte[] convertToDisplayData(String input, int totalLength) {
//        // 1. 将输入字符串转为有效字节数组（字符转ASCII）
//        byte[] validBytes = input.getBytes();
//
//        // 2. 如果有效内容超过总长度，截断到总长度
//        if (validBytes.length > totalLength) {
//            validBytes = Arrays.copyOf(validBytes, totalLength);
//        }
//
//        // 3. 如果有效内容长度正好等于总长度，直接返回
//        if (validBytes.length == totalLength) {
//            return validBytes;
//        }
//
//        // 4. 创建结果数组并填充0x00
//        byte[] result = new byte[totalLength];
//        Arrays.fill(result, (byte) 0x00);
//
//        // 5. 将有效内容复制到右侧，实现右对齐
//        int startPos = totalLength - validBytes.length;
//        System.arraycopy(validBytes, 0, result, startPos, validBytes.length);
//
//        return result;
//    }
//
//    /**
//     * 获取字符串的字长（字符长度）
//     *
//     * @param str 输入字符串
//     * @return 字长（字符长度）
//     */
//    public static int getCharLength(String str) {
//        if (str == null) {
//            return 0;
//        }
//        return str.length();
//    }
//
//    /**
//     * 获取字符串的字节长度（默认使用UTF-8编码）
//     *
//     * @param str 输入字符串
//     * @return 字节长度
//     */
//    public static int getByteLength(String str) {
//        if (str == null) {
//            return 0;
//        }
//        try {
//            // 使用UTF-8编码获取字节长度
//            return str.getBytes("UTF-8").length;
//        } catch (Exception e) {
//            // 如果编码失败，使用默认编码
//            return str.getBytes().length;
//        }
//    }
//
//
//    public static String getCmd(String cmd) {
//        String cmd1 = GetCRC_MODBUS(cmd);
//
//        String start = cmd1.substring(0, 2);
//        String end = cmd1.substring(cmd1.length() - 2);
//
//
//        return cmd + end + start;
//    }
//
//
//    public static String GetCRC_MODBUS(String str) {
//        byte[] bytes = toBytes(str);
//        int crc = 0xFFFF; // MODBUS标准初始值
//        int polynomial = 0xA001; // MODBUS标准多项式
//
//        for (byte b : bytes) {
//            crc ^= (b & 0xFF); // 确保无符号处理
//            for (int i = 0; i < 8; i++) {
//                if ((crc & 0x0001) != 0) {
//                    crc = (crc >>> 1) ^ polynomial; // 无符号右移并异或
//                } else {
//                    crc = crc >>> 1;
//                }
//            }
//        }
//
//        // 将CRC的高字节和低字节合并，并确保顺序为高字节在前
//        String result = String.format("%02X%02X", (crc >> 8) & 0xFF, crc & 0xFF);
//        return result;
//    }
//
//    public static byte[] toBytes(String hexStr) {
//        if (hexStr.length() % 2 != 0) {
////            throw new IllegalArgumentException("Hex string must be even-length");
//            Log.e("TAG", "Hex string must be even-length");
//        }
//        byte[] bytes = new byte[hexStr.length() / 2];
//        for (int i = 0; i < bytes.length; i++) {
//            int index = i * 2;
//            int val = Integer.parseInt(hexStr.substring(index, index + 2), 16);
//            bytes[i] = (byte) val;
//        }
//        return bytes;
//    }
//
//
//    public static String intTohex(int n) { // 将int转为16进制,并补0
//        StringBuffer s = new StringBuffer();
//        String a;
//        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
//        while (n != 0) {
//            s = s.append(b[n % 16]);
//            n = n / 16;
//        }
//        a = s.reverse().toString();
//        if ("".equals(a)) {
//            a = "00";
//        }
//        if (a.length() == 1) {
//            a = "0" + a;
//        }
//        return a;
//    }
//
//
//    public static String totrayString(String str) { // 获取价格标签的命令
//        return convertStringToAscii(formatPrice(str, 2, 5));
//
//    }
//
//
//    public static String convertStringToAscii(String input) {
//        StringBuilder result = new StringBuilder();
//
//        for (char c : input.toCharArray()) {
//            if (c == '0') {
//                // 如果字符是 '0'，直接替换为 "00"
//                result.append("00");
//            } else {
//                // 否则转换为 ASCII 码的十六进制表示
//                String hex = Integer.toHexString((int) c).toUpperCase();
//                result.append(hex).append("");
//            }
//        }
//
//        // 去掉最后一个多余的空格
//        return result.toString().trim();
//    }
//
//
//    // 格式化价格：整数部分固定长度，小数部分固定长度
//    private static String formatPrice(String price, int integerLen, int decimalLen) {
//        String[] parts = price.split("\\.");
//        String integerPart = parts.length > 0 ? parts[0] : "0";
//        String decimalPart = parts.length > 1 ? parts[1] : "0";
//
//        // 处理整数部分：补前导零到固定长度
//        integerPart = String.format("%0" + integerLen + "d", Integer.parseInt(integerPart));
//        if (integerPart.length() > integerLen) {
//            integerPart = integerPart.substring(0, integerLen);
//        }
//
//        // 处理小数部分：补尾随零到固定长度
//        decimalPart = String.format("%-" + decimalLen + "s", decimalPart).replace(' ', '0');
//        if (decimalPart.length() > decimalLen) {
//            decimalPart = decimalPart.substring(0, decimalLen);
//        }
//
//        return integerPart + "." + decimalPart;
//    }
//
//
//    /**
//     * 将高位在前的16进制字节数据转换为实际温度值
//     *
//     * @param bytes 包含两个字节的数组（高位在前）
//     * @return 实际温度值（单位℃），若数据无效返回 null
//     */
//    public static Double parseTemperature(byte[] bytes) {
//        if (bytes == null || bytes.length < 2) {
//            throw new IllegalArgumentException("需要至少两个字节");
//        }
//
//        // 合并高位和低位字节（高位在前）
//        int high = bytes[0] & 0xFF; // 转无符号
//        int low = bytes[1] & 0xFF;
//        int rawValue = (high << 8) | low;
//
//        // 转换为有符号 short
//        short signedValue = (short) rawValue;
//
//        // 处理无效值 0x7FFF
//        if (signedValue == 0x7FFF) {
//            return null; // 或返回 Double.NaN 表示无效
//        }
//
//        // 计算实际温度（保留一位小数）
//        return signedValue / 10.0;
//    }
//
//
//    public static String convert(String number) {
//        // 拆分整数部分和小数部分
//        String[] parts = number.split("\\.");
//        String integerPart = parts[0];
//        String decimalPart = parts.length > 1 ? parts[1] : "";
//
//        // 格式化整数部分为两位，左侧补零
//        integerPart = String.format("%02d", Integer.parseInt(integerPart));
//        // 格式化小数部分为两位，右侧补零
//        decimalPart = String.format("%-2s", decimalPart).replace(' ', '0').substring(0, 2);
//
//        // 组合成四个字符
//        String combined = integerPart + decimalPart;
//        if (combined.length() != 4) {
//            throw new IllegalArgumentException("Invalid number format");
//        }
//
//        // 转换为ASCII的两位十六进制字符串
//        String part0 = toHex(combined.charAt(0)).equals("30") ? "00" : toHex(combined.charAt(0));
//        String part1 = toHex(combined.charAt(1));
//        String part2 = toHex(combined.charAt(2));
//        String part3 = toHex(combined.charAt(3)).equals("30") ? "00" : toHex(combined.charAt(0));
//
//        // 拼接成最终格式
//        return part0 + "00" + part1 + "2E" + part2 + "00" + part3 + "00";
//    }
//
//    private static String toHex(char c) {
//        // 将字符转换为大写的两位十六进制字符串
//        return String.format("%02X", (int) c);
//    }
//}
