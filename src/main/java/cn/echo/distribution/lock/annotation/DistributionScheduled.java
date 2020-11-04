package cn.echo.distribution.lock.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.annotation.*;

/**
 * An annotation that marks a method to be scheduled. <br>
 * 分布式定时任务
 * @author lonyee
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Scheduled
@DistributionLock
@Inherited
public @interface DistributionScheduled {
	
	/**
	 * 锁名称
	 * Alias for {@link DistributionLock#key()}.
	 */
	@AliasFor(annotation = DistributionLock.class)
	String key() default "";

	/**
	 * 持锁时间，持锁超过此时间自动丢弃锁
	 * 单位毫秒，默认30秒
	 * Alias for {@link DistributionLock#keepMills}.
	 */
	@AliasFor(annotation = DistributionLock.class)
	long keepMills() default 30 * 1000;

	/**
	 * 最大取锁等待时间
	 * Alias for {@link DistributionLock#maxWaitMills}.
	 */
	@AliasFor(annotation = DistributionLock.class)
	long maxWaitMills() default -1;

	/**
	 * Alias for {@link Scheduled#cron}.
	 */
	@AliasFor(annotation = Scheduled.class)
	String cron() default "";

	/**
	 * Alias for {@link Scheduled#zone}.
	 */
	@AliasFor(annotation = Scheduled.class)
	String zone() default "";

	/**
	 * Alias for {@link Scheduled#fixedDelay}.
	 */
	@AliasFor(annotation = Scheduled.class)
	long fixedDelay() default -1;

	/**
	 * Alias for {@link Scheduled#fixedDelayString}.
	 */
	@AliasFor(annotation = Scheduled.class)
	String fixedDelayString() default "";

	/**
	 * Alias for {@link Scheduled#fixedRate}.
	 */
	@AliasFor(annotation = Scheduled.class)
	long fixedRate() default -1;

	/**
	 * Alias for {@link Scheduled#fixedRateString}.
	 */
	@AliasFor(annotation = Scheduled.class)
	String fixedRateString() default "";

	/**
	 * Alias for {@link Scheduled#initialDelay}.
	 */
	@AliasFor(annotation = Scheduled.class)
	long initialDelay() default -1;

	/**
	 * Alias for {@link Scheduled#initialDelayString}.
	 */
	@AliasFor(annotation = Scheduled.class)
	String initialDelayString() default "";
}