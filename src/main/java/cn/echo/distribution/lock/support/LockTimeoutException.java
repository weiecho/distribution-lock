package cn.echo.distribution.lock.support;

import java.util.concurrent.TimeoutException;

/**
 * 锁超时异常类
 * @author lonyee
 */
public class LockTimeoutException extends TimeoutException {

    public LockTimeoutException() {
        super();
    }

    public LockTimeoutException(String message) {
        super(message);
    }
}
