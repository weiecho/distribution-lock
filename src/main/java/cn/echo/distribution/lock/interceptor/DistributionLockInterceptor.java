package cn.echo.distribution.lock.interceptor;

import cn.echo.distribution.lock.annotation.DistributionLock;
import cn.echo.distribution.lock.annotation.LockKey;
import cn.echo.distribution.lock.support.RedisLock;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.TreeMap;

public class DistributionLockInterceptor implements MethodInterceptor {
	
    RedisLock redisLock;

	private final static String PERIOD = ".";

	public void setRedisLock(RedisLock redisLock) {
		this.redisLock = redisLock;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		DistributionLock lockInfo = invocation.getMethod().getAnnotation(DistributionLock.class);
		if (lockInfo == null) {
			throw new IllegalArgumentException("DistributionLock argument illegal");
		}
		double idx = 0.1; //利用double特性实现key排序
		Object[] args = invocation.getArguments();
		Annotation[][] paramAnnotationArrays = invocation.getMethod().getParameterAnnotations();
		Map<Double, String> keys = new TreeMap<>();
		for (int ix = 0; ix < paramAnnotationArrays.length; ix++) {
			for (Annotation paramAnnotation : paramAnnotationArrays[ix]) {
				if (LockKey.class.equals(paramAnnotation.annotationType())) {
					LockKey lockKey = (LockKey) paramAnnotation;
					keys.put(keys.containsKey(0.0D + lockKey.index())? idx+lockKey.index(): lockKey.index(), ":" + args[ix]);
					idx += 0.1D;
					break;
				}
			}
		}
		String methodName = invocation.getMethod().getName();
		String lockName = StringUtils.isEmpty(lockInfo.value())? methodName: lockInfo.value();
		if (!keys.isEmpty()) {
			lockName += PERIOD + StringUtils.collectionToDelimitedString(keys.values(), ":");
		}
		long lockId = redisLock.lock(lockName, lockInfo.keepMills(), lockInfo.maxWaitMills(), methodName);
		try {
			return invocation.proceed();
		} finally {
			redisLock.unlock(lockName, lockId, methodName);
		}
	}

}
