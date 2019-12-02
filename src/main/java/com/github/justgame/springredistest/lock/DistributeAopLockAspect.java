package com.github.justgame.springredistest.lock;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * @author xcl
 * @date 2019/11/29
 */
@Configuration
@Aspect
@Slf4j
public class DistributeAopLockAspect {
    @Autowired
    private DistributeLock lock;

    @Pointcut("@annotation(com.github.justgame.springredistest.lock.DistributeAopLock)")
    private void lockPoint() {

    }

    @Around("lockPoint()")
    public Object around(ProceedingJoinPoint pjp) {
        Method method = ((MethodSignature) (pjp.getSignature())).getMethod();
        DistributeAopLock aopLock = method.getAnnotation(DistributeAopLock.class);
        String key = aopLock.value();
        lock.lock(key, aopLock.waitMillis(), aopLock.retryTimes(), aopLock.expireMillis());
        try {
            return pjp.proceed();
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
        } finally {
            lock.releaseLock(key);
        }
        return null;
    }
}
