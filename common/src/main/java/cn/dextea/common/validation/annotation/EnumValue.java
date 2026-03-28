package cn.dextea.common.validation.annotation;

import cn.dextea.common.validation.validator.EnumValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * 枚举值验证注解
 * 用于验证字段值是否在指定枚举类的有效值范围内
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {EnumValueValidator.class})
public @interface EnumValue {

    /**
     * 枚举类
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * 枚举中用于校验的方法名，默认为 "isValid"
     * 方法签名必须是: public static boolean isValid(Integer value)
     */
    String method() default "isValid";

    /**
     * 错误消息
     */
    String message() default "";

    /**
     * 字段中文名称，用于错误消息
     */
    String fieldName() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
