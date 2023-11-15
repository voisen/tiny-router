package com.jiand.demo;


import com.jiand.annotation.Interceptor;
import com.jiand.annotation.meta.route.TinyRouteMetaInfo;
import com.jiand.router.callback.TinyRouterInterceptorCallback;
import com.jiand.router.interfaces.ITinyRouterInterceptor;

/**
 * @author jiand
 */
@Interceptor
public class TestInterceptor implements ITinyRouterInterceptor {
    @Override
    public void intercept(TinyRouteMetaInfo routerMetaInfo, TinyRouterInterceptorCallback callback) {
        callback.doContinue();
    }
}
