package cn.echo.distribution.lock.boot.autoconfigure;

import cn.echo.distribution.lock.interceptor.DistributionLockAdvisor;
import cn.echo.distribution.lock.interceptor.DistributionLockInterceptor;
import cn.echo.distribution.lock.support.RedisLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
  * 分布式锁自动配置器
 * @author lonyee
 */
@Configuration
public class DistributionLockAutoConfiguration {
	
	@Bean
    @ConditionalOnMissingBean
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public RedisLock redisLock(RedisTemplate redisTemplate) {
		RedisLock redisLock = new RedisLock();
		redisLock.setRedisTemplate(redisTemplate);
        return redisLock;
    }
	
	@Bean
    @ConditionalOnMissingBean
    public DistributionLockInterceptor distributionLockInterceptor(RedisLock redisLock) {
		DistributionLockInterceptor lockInterceptor = new DistributionLockInterceptor();
        lockInterceptor.setRedisLock(redisLock);
        return lockInterceptor;
    }
	
	@Bean
    @ConditionalOnMissingBean
    public DistributionLockAdvisor distributionLockAdvisor(DistributionLockInterceptor lockInterceptor) {
        return new DistributionLockAdvisor(lockInterceptor);
    }
}
