package cn.last.exception;

public class DevLoginFailException extends RuntimeException {

    public DevLoginFailException() {
        super();
    }

    public DevLoginFailException(String message) {
        super(message);
    }

    public DevLoginFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public DevLoginFailException(Throwable cause) {
        super(cause);
    }

    protected DevLoginFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
