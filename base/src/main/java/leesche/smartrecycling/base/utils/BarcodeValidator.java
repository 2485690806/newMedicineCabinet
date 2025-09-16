package leesche.smartrecycling.base.utils;

import android.text.TextUtils;

/**
 * 条码字符串校验工具类
 * 支持EAN-13、EAN-8等常见商品条码的有效性验证
 */
public class BarcodeValidator {
    // 校验结果状态
    public enum ResultStatus {
        VALID,               // 有效
        INVALID_LENGTH,      // 长度无效
        INVALID_CHARACTER,   // 包含非数字字符
        INVALID_CHECK_DIGIT  // 校验码错误
    }
    
    /**
     * 校验结果类，包含状态和详细信息
     */
    public static class ValidationResult {
        public ResultStatus status;
        public String message;
        
        public ValidationResult(ResultStatus status, String message) {
            this.status = status;
            this.message = message;
        }
        
        public boolean isValid() {
            return status == ResultStatus.VALID;
        }
    }
    
    /**
     * 验证EAN-13条码
     * @param barcode 待验证的EAN-13条码字符串
     * @return 校验结果
     */
    public static ValidationResult validateEan13(String barcode) {
        // 检查是否为空
        if (TextUtils.isEmpty(barcode)) {
            return new ValidationResult(ResultStatus.INVALID_LENGTH, "EAN-13条码不能为空");
        }
        
        // 检查长度是否为13位
        if (barcode.length() != 13) {
            return new ValidationResult(ResultStatus.INVALID_LENGTH, 
                    "EAN-13条码必须是13位数字，实际为" + barcode.length() + "位");
        }
        
        // 检查是否全为数字
        if (!barcode.matches("\\d+")) {
            return new ValidationResult(ResultStatus.INVALID_CHARACTER, 
                    "EAN-13条码只能包含数字字符");
        }
        
        // 提取前12位和校验码
        String first12Digits = barcode.substring(0, 12);
        int checkDigit = Character.getNumericValue(barcode.charAt(12));
        
        // 计算校验码并验证
        int calculatedCheckDigit = calculateEan13CheckDigit(first12Digits);
        if (checkDigit != calculatedCheckDigit) {
            return new ValidationResult(ResultStatus.INVALID_CHECK_DIGIT, 
                    "EAN-13校验码错误，应为" + calculatedCheckDigit + 
                    "，实际为" + checkDigit);
        }
        
        return new ValidationResult(ResultStatus.VALID, "EAN-13条码有效");
    }
    
    /**
     * 验证EAN-8条码
     * @param barcode 待验证的EAN-8条码字符串
     * @return 校验结果
     */
    public static ValidationResult validateEan8(String barcode) {
        // 检查是否为空
        if (TextUtils.isEmpty(barcode)) {
            return new ValidationResult(ResultStatus.INVALID_LENGTH, "EAN-8条码不能为空");
        }
        
        // 检查长度是否为8位
        if (barcode.length() != 8) {
            return new ValidationResult(ResultStatus.INVALID_LENGTH, 
                    "EAN-8条码必须是8位数字，实际为" + barcode.length() + "位");
        }
        
        // 检查是否全为数字
        if (!barcode.matches("\\d+")) {
            return new ValidationResult(ResultStatus.INVALID_CHARACTER, 
                    "EAN-8条码只能包含数字字符");
        }
        
        // 提取前7位和校验码
        String first7Digits = barcode.substring(0, 7);
        int checkDigit = Character.getNumericValue(barcode.charAt(7));
        
        // 计算校验码并验证
        int calculatedCheckDigit = calculateEan8CheckDigit(first7Digits);
        if (checkDigit != calculatedCheckDigit) {
            return new ValidationResult(ResultStatus.INVALID_CHECK_DIGIT, 
                    "EAN-8校验码错误，应为" + calculatedCheckDigit + 
                    "，实际为" + checkDigit);
        }
        
        return new ValidationResult(ResultStatus.VALID, "EAN-8条码有效");
    }
    
    /**
     * 自动识别并验证条码类型
     * 目前支持EAN-13和EAN-8
     * @param barcode 待验证的条码字符串
     * @return 校验结果
     */
    public static ValidationResult validateAuto(String barcode) {
        if (TextUtils.isEmpty(barcode)) {
            return new ValidationResult(ResultStatus.INVALID_LENGTH, "条码不能为空");
        }
        
        // 根据长度尝试识别条码类型
        if (barcode.length() == 13) {
            return validateEan13(barcode);
        } else if (barcode.length() == 8) {
            return validateEan8(barcode);
        } else {
            return new ValidationResult(ResultStatus.INVALID_LENGTH, 
                    "不支持的条码长度：" + barcode.length() + "位，目前支持EAN-13(13位)和EAN-8(8位)");
        }
    }
    
    /**
     * 计算EAN-13的校验码
     * @param first12Digits 前12位数字
     * @return 校验码
     */
    public static int calculateEan13CheckDigit(String first12Digits) {
        if (TextUtils.isEmpty(first12Digits) || first12Digits.length() != 12) {
            throw new IllegalArgumentException("EAN-13前12位必须是12位数字");
        }
        
        int sumOdd = 0;  // 奇数位之和(从右向左编号)
        int sumEven = 0; // 偶数位之和(从右向左编号)
        
        // 从左到右遍历，对应从右向左编号的12→1
        for (int i = 0; i < first12Digits.length(); i++) {
            int digit = Character.getNumericValue(first12Digits.charAt(i));
            
            // 位置计算：i=0对应第12位(偶数位)，i=1对应第11位(奇数位)，依此类推
            if ((i + 1) % 2 == 0) {
                // 奇数位(从右向左)，需要乘以3
                sumOdd += digit;
            } else {
                // 偶数位(从右向左)，不乘
                sumEven += digit;
            }
        }
        
        int total = sumOdd * 3 + sumEven;
        return (10 - (total % 10)) % 10;
    }
    
    /**
     * 计算EAN-8的校验码
     * @param first7Digits 前7位数字
     * @return 校验码
     */
    public static int calculateEan8CheckDigit(String first7Digits) {
        if (TextUtils.isEmpty(first7Digits) || first7Digits.length() != 7) {
            throw new IllegalArgumentException("EAN-8前7位必须是7位数字");
        }
        
        int sumOdd = 0;  // 奇数位之和(从右向左编号)
        int sumEven = 0; // 偶数位之和(从右向左编号)
        
        // 从左到右遍历，对应从右向左编号的7→1
        for (int i = 0; i < first7Digits.length(); i++) {
            int digit = Character.getNumericValue(first7Digits.charAt(i));
            
            // 位置计算：i=0对应第7位(奇数位)，i=1对应第6位(偶数位)，依此类推
            if ((i + 1) % 2 == 1) {
                // 奇数位(从右向左)，需要乘以3
                sumOdd += digit;
            } else {
                // 偶数位(从右向左)，不乘
                sumEven += digit;
            }
        }
        
        int total = sumOdd * 3 + sumEven;
        return (10 - (total % 10)) % 10;
    }
}
