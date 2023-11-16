package com.jiand.demo;


import android.util.Log;

import com.jiand.tinyrouter.annotation.Interceptor;
import com.jiand.tinyrouter.annotation.meta.route.TinyRouteMetaInfo;
import com.jiand.tinyrouter.callback.TinyRouterInterceptorCallback;
import com.jiand.tinyrouter.interfaces.ITinyRouterInterceptor;

/**
 * @author jiand
 */
@Interceptor(priority = 3)
public class Test3Interceptor implements ITinyRouterInterceptor {
    @Override
    public void intercept(TinyRouteMetaInfo routerMetaInfo, TinyRouterInterceptorCallback callback) {
        Log.i("Test2Interceptor", "intercept: 经过拦截器3");
        callback.doContinue();
    }
}
