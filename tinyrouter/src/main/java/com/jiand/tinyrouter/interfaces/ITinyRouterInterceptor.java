package com.jiand.tinyrouter.interfaces;

import com.jiand.tinyrouter.annotation.meta.route.TinyRouteMetaInfo;
import com.jiand.tinyrouter.callback.TinyRouterInterceptorCallback;

/**
 * @author jiand
 */
public interface ITinyRouterInterceptor {

    /**
     * 初始化
     */
    default void onInit(){}

    /**
     * 拦截器
     * @param routerMetaInfo info
     * @param callback callback
     */
    void intercept(TinyRouteMetaInfo routerMetaInfo, TinyRouterInterceptorCallback callback);

}
