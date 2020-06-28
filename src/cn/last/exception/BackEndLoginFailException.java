package cn.last.exception;

public class BackEndLoginFailException extends RuntimeException {

    public BackEndLoginFailException() {
    }

    public BackEndLoginFailException(String message) {
        super(message);
    }

    public BackEndLoginFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public BackEndLoginFailException(Throwable cause) {
        super(cause);
    }

    public BackEndLoginFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
