package cn.dextea.store.web.exception;

import cn.dev33.satoken.exception.NotPermissionException;
import cn.dextea.common.code.GlobalErrorCode;
import cn.dextea.common.model.common.DexteaApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ModuleApiExceptionHandler {
    @ExceptionHandler(NotPermissionException.class)
    public DexteaApiResponse<Void> handleNotPermissionException(NotPermissionException e) {
        log.warn("Permission denied: {}", e.getMessage());
        return DexteaApiResponse.forbidden(GlobalErrorCode.PERMISSION_LACK.getCode(), GlobalErrorCode.PERMISSION_LACK.getMsg());
    }
}
