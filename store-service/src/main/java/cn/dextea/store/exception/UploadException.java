package cn.dextea.store.exception;

/**
 * @author Lai Yongchao
 */
public class UploadException extends RuntimeException {
    public UploadException(String message) {
        super(message);
    }
    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
