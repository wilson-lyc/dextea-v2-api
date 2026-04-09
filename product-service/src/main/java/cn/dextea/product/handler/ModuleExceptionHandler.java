package cn.dextea.product.handler;

import cn.dev33.satoken.exception.NotPermissionException;
import cn.dextea.common.code.GlobalErrorCode;
import cn.dextea.common.model.common.DexteaApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class ModuleExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<DexteaApiResponse<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("文件体积过大：{}", e.getMessage());
        return ResponseEntity.badRequest().body(DexteaApiResponse.fail("文件体积过大"));
    }
    @ExceptionHandler(NotPermissionException.class)
    public DexteaApiResponse<Void> handleNotPermissionException(NotPermissionException e) {
        log.error("无权限：{}", e.getMessage());
        return DexteaApiResponse.forbidden(GlobalErrorCode.PERMISSION_LACK.getCode(),
                GlobalErrorCode.PERMISSION_LACK.getMsg());
    }
}