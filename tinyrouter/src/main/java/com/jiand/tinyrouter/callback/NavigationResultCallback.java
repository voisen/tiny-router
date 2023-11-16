package com.jiand.tinyrouter.callback;

/**
 * @author jiand
 */
public interface NavigationResultCallback {

    /**
     * 找到目标
     */
    default void onFound(){}

    /**
     * 未找到目标
     */
    default void onUnFound() {}

    /**
     * 执行完成
     * @param result r
     */
    default void onCompleted(Object result){}

    /**
     * 取消执行
     * @param throwable throwable
     */
    void onError(Throwable throwable);
}
