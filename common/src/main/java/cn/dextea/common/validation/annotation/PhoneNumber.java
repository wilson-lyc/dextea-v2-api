package cn.dextea.common.validation.annotation;

import cn.dextea.common.validation.validator.PhoneNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * 手机号格式验证注解
 * 验证中国手机号格式: 1开头的11位数字
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {PhoneNumberValidator.class})
public @interface PhoneNumber {

    String message() default "";

    String fieldName() default "手机号";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
