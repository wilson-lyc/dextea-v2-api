package cn.dextea.common.validation.validator;

import cn.dextea.common.validation.annotation.EnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;

/**
 * 枚举值验证器实现
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Integer> {

    private Class<? extends Enum<?>> enumClass;
    private String methodName;
    private String fieldName;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
        this.methodName = constraintAnnotation.method();
        this.fieldName = constraintAnnotation.fieldName();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null 值由 @NotNull 控制
        }

        try {
            Method method = enumClass.getMethod(methodName, Integer.class);
            Boolean result = (Boolean) method.invoke(null, value);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            // 如果枚举类没有对应的校验方法，fallback 到枚举值比对
            return fallbackValidate(value);
        }
    }

    private boolean fallbackValidate(Integer value) {
        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant instanceof EnumWithValue) {
                if (((EnumWithValue<?>) enumConstant).getValue().equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 支持枚举值比对的接口
     */
    public interface EnumWithValue<T> {
        T getValue();
    }
}
