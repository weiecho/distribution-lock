package cn.echo.distribution.lock.interceptor;

import cn.echo.distribution.lock.annotation.DistributionLock;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.lang.NonNull;

/**
 * 分布式锁aop注入
 * @author lonyee
 */
public class DistributionLockAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

	private static final long serialVersionUID = 1L;

	private Advice advice;

    private Pointcut pointcut;

    public DistributionLockAdvisor(@NonNull DistributionLockInterceptor lockInterceptor) {
        this.advice = lockInterceptor;
        this.pointcut = AnnotationMatchingPointcut.forMethodAnnotation(DistributionLock.class);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }
}
