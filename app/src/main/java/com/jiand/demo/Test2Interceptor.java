package com.jiand.demo;


import com.jiand.annotation.Interceptor;
import com.jiand.annotation.meta.route.TinyRouteMetaInfo;
import com.jiand.router.callback.TinyRouterInterceptorCallback;
import com.jiand.router.interfaces.ITinyRouterInterceptor;

/**
 * @author jiand
 */
@Interceptor(priority = 2)
public class Test2Interceptor implements ITinyRouterInterceptor {
    @Override
    public void intercept(TinyRouteMetaInfo routerMetaInfo, TinyRouterInterceptorCallback callback) {
        callback.doContinue();
    }
}
