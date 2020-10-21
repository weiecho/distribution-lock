package cn.echo.distribution.lock.interceptor;

import cn.echo.distribution.lock.annotation.DistributionLock;
import cn.echo.distribution.lock.support.RedisLock;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StringUtils;

public class DistributionLockInterceptor implements MethodInterceptor {
	
    RedisLock redisLock;

	public void setRedisLock(RedisLock redisLock) {
		this.redisLock = redisLock;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		DistributionLock lockInfo = invocation.getMethod().getAnnotation(DistributionLock.class);
		if (lockInfo == null || StringUtils.isEmpty(lockInfo.value())) {
			throw new IllegalArgumentException("DistributionLock argument illegal");
		}
		String methodName = invocation.getMethod().getName();
		long lockId = redisLock.lock(lockInfo.value(), lockInfo.keepMills(), lockInfo.maxWaitMills(), methodName);
		try {
			return invocation.proceed();
		} finally {
			redisLock.unlock(lockInfo.value(), lockId, methodName);
		}
	}

}
