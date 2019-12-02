package com.github.justgame.springredistest.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xcl
 * @date 2019/11/29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributeAopLock {
    String value() default "default";

    int retryTimes() default Integer.MAX_VALUE;

    int waitMillis() default 100;

    int expireMillis() default 30000;
}
