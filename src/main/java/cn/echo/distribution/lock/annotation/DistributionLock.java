package cn.echo.distribution.lock.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.annotation.*;

/**
 * 分布式锁
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DistributionLock {

	/**
	 * 锁名称
	 */
	String key() default "";

	/**
	 * 持锁时间，持锁超过此时间自动丢弃锁
	 * 单位毫秒，默认30秒
	 */
	long keepMills() default 30 * 1000;

	/**
	 * 最大取锁等待时间
	 * 没有获取到锁的情况下继续等待，如果超时抛出TimeoutException
	 * 单位毫秒，默认1秒
	 */
	long maxWaitMills() default 1 * 1000;

}
