package leesche.smartrecycling.base.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalcUtil {

    public static final int TYPE_ADD = 0x00; // 加法
    public static final int TYPE_MULTIPLY = 0x01; // 乘法
    public static final int TYPE_DIVIDE = 0x02; // 除法
    public static final int TYPE_SUBTRACT = 0x03; // 减法
    /**
     *  加法
     * @param a
     * @param b
     * @return
     */
    public static Double add(Double a, Double b) {
        return calc(a, b, -1, TYPE_ADD, null);
    }
    /**
     * 减法
     * @param a
     * @param b
     * @return
     */

    public static Double sub(Double a, Double b) {
        return calc(a, b, 2, TYPE_SUBTRACT, null);
    }
    /**
     * 乘法
     * @param a
     * @param b
     * @return
     */

    public static Double multiply(Double a, Double b) {
        return calc(a, b, 2, TYPE_MULTIPLY, RoundingMode.HALF_UP);
    }

    /**
     * 除法
     * @param a
     * @param b
     * @return
     */

    public static Double divide(Double a, Double b) {
        return calc(a, b, 3, TYPE_DIVIDE, null);
    }

    public static float divide2(float a, float b) {
        return calc(a, b, -1, TYPE_DIVIDE, null);
    }


    /**
     * 乘法
     * @param a
     * @param b
     * @param scale 小数点后保留的位数
     * @param mode 保留的模式
     * @return
     */
    public static Double multiply(Double a, Double b, int scale, RoundingMode mode) {

        return calc(a, b, scale, TYPE_MULTIPLY, mode);
    }
    /**
     * 除法
     * @param a
     * @param b
     * @param scale 小数点后保留的位数
     * @param mode 保留的模式
     * @return
     */
    public static Double divide(double a, Double b, int scale, RoundingMode mode) {

        return calc(a, b, scale, TYPE_DIVIDE, mode);
    }
    /**
     *  计算
     * @param a
     * @param b
     * @param scale
     * @param type
     * @param mode
     * @return
     */
    private static Double calc(Double a, Double b, int scale, int type, RoundingMode mode) {
        BigDecimal result = null;

        BigDecimal bgA = new BigDecimal(String.valueOf(a));
        BigDecimal bgB = new BigDecimal(String.valueOf(b));
        switch (type) {
            case TYPE_ADD:
                result = bgA.add(bgB);
                break;
            case TYPE_MULTIPLY:
                result = bgA.multiply(bgB);
                break;
            case TYPE_DIVIDE:
                try {
                    result = bgA.divide(bgB);
                } catch (ArithmeticException e) {
                    result = bgA.divide(bgB,3, RoundingMode.HALF_DOWN);
                }
                break;
            case TYPE_SUBTRACT:
                result = bgA.subtract(bgB);
                break;
        }
        if (mode==null) {
            if(scale!=-1){
                result = result.setScale(scale, RoundingMode.HALF_DOWN);
            }
        }else{
            if(scale!=-1){
                result = result.setScale(scale,mode);
            }
        }
        return result.doubleValue();
    }

    private static float calc(float a, float b, int scale, int type, RoundingMode mode) {
        BigDecimal result = null;

        BigDecimal bgA = new BigDecimal(String.valueOf(a));
        BigDecimal bgB = new BigDecimal(String.valueOf(b));
        switch (type) {
            case TYPE_ADD:
                result = bgA.add(bgB);
                break;
            case TYPE_MULTIPLY:
                result = bgA.multiply(bgB);
                break;
            case TYPE_DIVIDE:
                try {
                    result = bgA.divide(bgB);
                } catch (ArithmeticException e) {
                    result = bgA.divide(bgB,3, RoundingMode.HALF_DOWN);
                }
                break;
            case TYPE_SUBTRACT:
                result = bgA.subtract(bgB);
                break;
        }
        if (mode==null) {
            if(scale!=-1){
                result = result.setScale(scale, RoundingMode.HALF_DOWN);
            }
        }else{
            if(scale!=-1){
                result = result.setScale(scale,mode);
            }
        }
        return result.floatValue();
    }
}
