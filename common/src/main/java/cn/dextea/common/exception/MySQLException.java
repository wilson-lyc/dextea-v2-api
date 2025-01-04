package cn.dextea.common.exception;

public class MySQLException extends RuntimeException {
    public MySQLException(String message) {
        super("MySQL Error:" + message);
    }
    public MySQLException(String message, Throwable cause) {
        super("MySQL Error:"+message, cause);
    }
}
