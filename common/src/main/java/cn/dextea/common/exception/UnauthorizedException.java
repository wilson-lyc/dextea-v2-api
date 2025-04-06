package cn.dextea.common.exception;

/**
 * @author Lai Yongchao
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
