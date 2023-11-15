package com.jiand.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jiand
 */
@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface Interceptor {
    /*
     * 名称
     */
    String name() default "";

    /*
     * 优先级，执行顺序为由1 ~ N
     */
    int priority() default  Integer.MAX_VALUE;

    /**
     * 当路由中的 extras 的值跟此值一样时，跳过此拦截器
     * @return int
     */
    int skipWhenExtras() default Integer.MAX_VALUE;
}
