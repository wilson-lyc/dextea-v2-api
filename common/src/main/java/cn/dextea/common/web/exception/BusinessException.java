package cn.dextea.common.web.exception;

import cn.dextea.common.code.GlobalErrorCode;
import cn.dextea.common.code.ResponseCode;
import lombok.Getter;

/**
 * 业务异常，用于 Service 层主动抛出带错误码的异常。
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

    public BusinessException(GlobalErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }
}
