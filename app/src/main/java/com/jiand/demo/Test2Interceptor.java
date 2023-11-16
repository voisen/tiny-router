package com.jiand.demo;


import android.util.Log;

import com.jiand.tinyrouter.annotation.Interceptor;
import com.jiand.tinyrouter.annotation.meta.route.TinyRouteMetaInfo;
import com.jiand.tinyrouter.callback.TinyRouterInterceptorCallback;
import com.jiand.tinyrouter.interfaces.ITinyRouterInterceptor;

/**
 * @author jiand
 */
@Interceptor(priority = 2)
public class Test2Interceptor implements ITinyRouterInterceptor {
    @Override
    public void intercept(TinyRouteMetaInfo routerMetaInfo, TinyRouterInterceptorCallback callback) {
        Log.i("Test2Interceptor", "intercept: 经过拦截器2");
        callback.doContinue();
    }
}
