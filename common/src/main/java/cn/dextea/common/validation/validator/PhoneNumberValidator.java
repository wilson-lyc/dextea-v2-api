package cn.dextea.common.validation.validator;

import cn.dextea.common.validation.annotation.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 手机号格式验证器
 * 中国手机号: 以1开头的11位数字
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    // 中国手机号正则: 1[3-9]开头的11位数字
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // null 值由 @NotBlank/@NotNull 控制
        }
        return value.matches(PHONE_REGEX);
    }
}
