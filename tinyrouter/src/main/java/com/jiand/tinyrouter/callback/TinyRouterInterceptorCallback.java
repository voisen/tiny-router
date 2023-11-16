package com.jiand.tinyrouter.callback;

/**
 * @author jiand
 */
public interface TinyRouterInterceptorCallback {
    /**
     * 继续执行
     */
    void doContinue();

    /**
     * 取消执行
     * @param throwable throwable
     */
    void doInterrupt(Throwable throwable);
}
