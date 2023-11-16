package com.jiand.tinyrouter.annotation;

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
public @interface Route {
    String path();
    String name() default "";
    int extras() default Integer.MIN_VALUE;
}
