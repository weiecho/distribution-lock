package cn.echo.distribution.lock.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.LockSupport;

/**
 * redis分布式锁
 * 
 * @author lonyee
 */
public class RedisLock {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private final static String PRE_LOCK_NAME = "DISTRIBUTION:LOCKED:";
	private final static int BOUND = 100;


	private RedisTemplate<String, Long> redisTemplate;

	public void setRedisTemplate(RedisTemplate<String, Long> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public RedisLock() { }

	/**
	 * 获取分布式锁
	 * @param lockName 锁名称
	 * @param keepMills 持锁时间
	 * @param maxWaitMills 最大取锁等待时间
	 * @param methodName 方法名称
	 * @return 锁ID
	 * @throws TimeoutException
	 */
	public long lock(final String lockName, final Long keepMills, final Long maxWaitMills, final String methodName) throws TimeoutException {
		String key = PRE_LOCK_NAME + lockName;
		ValueOperations<String, Long> operations = redisTemplate.opsForValue();
		long waitTime = 0L;
		do {
			// 生成锁ID - 锁的过期时间
			Long lockedId = this.currTimeFromRedis() + keepMills +  ThreadLocalRandom.current().nextInt(BOUND);
			boolean hasLocked = operations.setIfAbsent(key, lockedId);
			if (hasLocked) {
				redisTemplate.expire(key, keepMills, TimeUnit.MILLISECONDS);
				log.info("{}-{} locked.", methodName, key);
				return lockedId;
			}
			Long expLockedId = operations.get(key);
			if (expLockedId != null && expLockedId < this.currTimeFromRedis()) {
				// 获取上一个锁到期时间，并设置现在的锁到期时间
				Long resLockedId = operations.getAndSet(key, lockedId);
				// 如果多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
				if (resLockedId != null && resLockedId.equals(expLockedId)) {
					redisTemplate.expire(key, keepMills, TimeUnit.MILLISECONDS);
					log.info("{}-{} locked (exist).", methodName, key);
					return lockedId;
				}
			}

			int rd = ThreadLocalRandom.current().nextInt(BOUND);
			waitTime += rd;

			log.info("{} waiting for lock {} ms.", methodName, rd);
			LockSupport.parkNanos(rd * 1000000);
		} while (waitTime < maxWaitMills); // 循环获取锁

		String noLock = String.format("%s-%s can not get the lock.", methodName, key);
		log.warn(noLock);
		throw new TimeoutException(noLock);
	}

	/**
	 * 解除分布式锁
	 * @param lockName 锁名称
	 * @param lockId 锁ID
	 * @param methodName 方法名称
	 */
	public void unlock(String lockName, long lockId, String methodName) {
		String key = PRE_LOCK_NAME + lockName;
		ValueOperations<String, Long> operations = redisTemplate.opsForValue();
		Long resLockedId = operations.get(key);
		// 如果是加锁者则删除锁, 如果不是则等待自动过期 重新竞争加锁
		if (resLockedId != null && resLockedId == lockId) {
			redisTemplate.delete(key);
			log.info("{}-{} unlocked.", methodName, key);
		}
		log.info("{}-{} unlocked (wait timeout).", methodName, key);
	}

	/**
	 * 获取当前redis时间
	 */
	private long currTimeFromRedis() {
		return redisTemplate.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.time();
			}
		});
	}
}