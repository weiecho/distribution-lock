package cn.echo.distribution.lock.interceptor;

import cn.echo.distribution.lock.annotation.DistributionLock;
import cn.echo.distribution.lock.support.RedisLock;
import cn.echo.distribution.lock.support.SpelUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * 分布式锁拦截调用
 */
public class DistributionLockInterceptor implements MethodInterceptor {

	Logger log = LoggerFactory.getLogger(this.getClass());

    RedisLock redisLock;

	public void setRedisLock(RedisLock redisLock) {
		this.redisLock = redisLock;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		DistributionLock lockInfo = invocation.getMethod().getAnnotation(DistributionLock.class);
		if (lockInfo == null || StringUtils.isEmpty(lockInfo.key())) {
			throw new IllegalArgumentException("DistributionLock argument illegal");
		}
		String methodName = invocation.getMethod().getName();
        String key = SpelUtil.generateKeyBySpEL(lockInfo.key(), invocation);

        long lockId = redisLock.lock(key, lockInfo.keepMills(), lockInfo.maxWaitMills(), methodName);
		try {
			return invocation.proceed();
		} finally {
			redisLock.unlock(key, lockId, methodName);
		}
	}

}
