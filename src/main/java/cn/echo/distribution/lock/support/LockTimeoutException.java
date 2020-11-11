package cn.echo.distribution.lock.support;

/**
 * 锁超时异常类
 * @author lonyee
 */
public class LockTimeoutException extends RuntimeException {

    public LockTimeoutException() {
        super();
    }

    public LockTimeoutException(String message) {
        super(message);
    }
}
