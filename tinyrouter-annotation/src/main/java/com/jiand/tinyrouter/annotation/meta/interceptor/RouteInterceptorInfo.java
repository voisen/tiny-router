package com.jiand.tinyrouter.annotation.meta.interceptor;

/**
 * @author jiand
 */
public class RouteInterceptorInfo {
    private final String name;
    private final int priority;
    private final int skipWhenExtras;

    private final Class<?> interceptorClass;

    private Object interceptor;

    public RouteInterceptorInfo(String name, int priority, int skipWhenExtras, Class<?> interceptorClass) {
        this.name = name;
        this.priority = priority;
        this.skipWhenExtras = skipWhenExtras;
        this.interceptorClass = interceptorClass;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public int getSkipWhenExtras() {
        return skipWhenExtras;
    }

    public Class<?> getInterceptorClass() {
        return interceptorClass;
    }

    public Object getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(Object interceptor) {
        this.interceptor = interceptor;
    }
}
