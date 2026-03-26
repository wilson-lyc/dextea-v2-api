package cn.dextea.common.web.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dextea.common.code.GlobalErrorCode;
import cn.dextea.common.web.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Sa-Token 接口异常处理基类。
 *
 * <p>供使用 {@link cn.dextea.common.web.response.ApiResponse} 的模块复用登录态和权限异常处理逻辑。</p>
 */
@Slf4j
public abstract class AbstractSaTokenApiExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public ApiResponse<Void> handleNotLoginException(NotLoginException e) {
        log.warn("Request not login: {}", e.getMessage());
        return ApiResponse.unauthorized("请先登录");
    }

    @ExceptionHandler(NotPermissionException.class)
    public ApiResponse<Void> handleNotPermissionException(NotPermissionException e) {
        log.warn("Request permission denied: {}", e.getMessage());
        return ApiResponse.forbidden(GlobalErrorCode.PERMISSION_LACK.getMsg());
    }
}
