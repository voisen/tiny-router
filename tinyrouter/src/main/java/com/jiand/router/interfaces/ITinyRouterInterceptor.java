package com.jiand.router.interfaces;

import com.jiand.annotation.meta.route.TinyRouteMetaInfo;
import com.jiand.router.callback.TinyRouterInterceptorCallback;

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
