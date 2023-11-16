package com.jiand.router.core;

import android.util.Log;

import com.jiand.annotation.meta.interceptor.RouteInterceptorInfo;
import com.jiand.annotation.meta.route.TinyRouteMetaInfo;
import com.jiand.router.core.loader._IInterceptorLoader;
import com.jiand.router.core.loader._ITinyRouteLoader;
import com.jiand.router.interfaces.ITinyRouterInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jiand
 */
public class TinyRouterProvider {
    private static String TAG = "TinyRouterProvider";
    private static Map<String, TinyRouteMetaInfo> routeMap = new HashMap<>();
    private static Map<Class<?>, TinyRouteMetaInfo> classRouteMap = new HashMap<>();
    private static List<RouteInterceptorInfo> interceptorInfoList = new ArrayList<>();

    private static Set<String> classNames = new HashSet<>();

    private static TinyRouterProvider tinyRouterProvider;

    public static TinyRouterProvider getInstance(){
        if (tinyRouterProvider != null) {
            return tinyRouterProvider;
        }
        synchronized (TinyRouterProvider.class){
            if (tinyRouterProvider == null){
                tinyRouterProvider = new TinyRouterProvider();
                tinyRouterProvider.init();
                Log.i(TAG, "getInstance: 路由个数: " + routeMap.size() + " ,拦截器个数: " + interceptorInfoList.size());
                tinyRouterProvider.preprocessing();
            }
        }
        return tinyRouterProvider;
    }

    private void init(){
        //TODO 初始化路由
    }

    private void preprocessing(){
        Log.i(TAG, "preprocessing: 预处理: " + interceptorInfoList.size());
        Collections.sort(interceptorInfoList, (o1, o2) -> {
            if (o1.getPriority() == o2.getPriority()){
                return 0;
            }else if (o1.getPriority() > o2.getPriority()){
                return 1;
            }else {
                return -1;
            }
        });

        for (TinyRouteMetaInfo info : routeMap.values()) {
            classRouteMap.put(info.getTargetClass(), info);
        }
    }

    private void register(String className){
        try {
            classNames.add(className);
            Class<?> aClass = Class.forName(className);
            if (_ITinyRouteLoader.class.isAssignableFrom(aClass)){
                Method loadMeta = aClass.getMethod("loadMeta", Map.class);
                loadMeta.invoke(null, routeMap);
            }else if (_IInterceptorLoader.class.isAssignableFrom(aClass)){
                Method loadInterceptor = aClass.getMethod("loadInterceptor", List.class);
                loadInterceptor.invoke(null, interceptorInfoList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public TinyRouteMetaInfo getRouterMetaInfo(String path){
        return routeMap.get(path);
    }


    public ITinyRouterInterceptor getNextInterceptor(ITinyRouterInterceptor currentInterceptor, TinyRouteMetaInfo routerMetaInfo){
        if (interceptorInfoList.isEmpty()){
            return null;
        }
        RouteInterceptorInfo nextInterceptor = null;
        if (currentInterceptor == null) {
            nextInterceptor = interceptorInfoList.get(0);
        }else{
            int idx = -1;
            for (int i = 0; i < interceptorInfoList.size(); i++) {
                if (currentInterceptor == interceptorInfoList.get(i).getInterceptor()){
                    idx = i;
                    break;
                }
            }
            if (idx == -1){
                return null;
            }
            if (interceptorInfoList.size() > idx + 1){
                nextInterceptor = interceptorInfoList.get(idx + 1);
            }else{
                return null;
            }
        }
        if (nextInterceptor.getInterceptor() == null){
            synchronized (this){
                if (nextInterceptor.getInterceptor() == null){
                    ITinyRouterInterceptor interceptor = null;
                    try {
                        interceptor = (ITinyRouterInterceptor) nextInterceptor.getInterceptorClass().newInstance();
                        interceptor.onInit();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    nextInterceptor.setInterceptor(interceptor);
                }
            }
        }
        if (nextInterceptor.getSkipWhenExtras() == routerMetaInfo.getExtras()){
            return getNextInterceptor((ITinyRouterInterceptor) nextInterceptor.getInterceptor(), routerMetaInfo);
        }
        return (ITinyRouterInterceptor) nextInterceptor.getInterceptor();
    }

    public TinyRouteMetaInfo getRouterMetaInfo(Class<?> aClass) {
        return classRouteMap.get(aClass);
    }

    public void clear() {
        classRouteMap.clear();
        routeMap.clear();
        classNames.clear();
        interceptorInfoList.clear();
    }
}
