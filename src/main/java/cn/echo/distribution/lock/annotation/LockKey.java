package cn.echo.distribution.lock.annotation;

import java.lang.annotation.*;

/**
 * 分布式锁 key
 * 加在方法的参数上，指定的参数会作为锁的key的一部分
 * 
 * @author lonyee
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LockKey {
	/**
	 * key的拼接排序 0-9
	 */
	int index() default 0;
}
