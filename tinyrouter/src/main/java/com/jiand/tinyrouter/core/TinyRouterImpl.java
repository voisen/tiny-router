package com.jiand.tinyrouter.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.jiand.tinyrouter.TinyRouter;
import com.jiand.tinyrouter.annotation.Autowired;
import com.jiand.tinyrouter.annotation.meta.route.AutowiredInfo;
import com.jiand.tinyrouter.annotation.meta.route.RouteType;
import com.jiand.tinyrouter.annotation.meta.route.TinyRouteMetaInfo;
import com.jiand.tinyrouter.callback.ActivityResultCallback;
import com.jiand.tinyrouter.callback.NavigationResultCallback;
import com.jiand.tinyrouter.callback.TinyRouterInterceptorCallback;
import com.jiand.tinyrouter.interfaces.ITinyRouterInterceptor;
import com.jiand.tinyrouter.utils.StringUtils;
import com.jiand.tinyrouter.utils.TinyRouterActivityUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author jiand
 */
public class TinyRouterImpl implements TinyRouter {
    private static String TAG = "TinyRouterImpl";

    private static TinyRouterImpl instance;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private Application mApplication;

    public static TinyRouter getInstance() {
        if (instance != null){
            return instance;
        }
        synchronized (TinyRouterImpl.class){
            if (instance == null){
                instance = new TinyRouterImpl();
            }
        }
        return instance;
    }

    @Override
    public TinyRouterTicket build(String path) {
        if (mApplication == null){
            throw new RuntimeException("你需要在Application内初始化TinyRouter");
        }
        TinyRouteMetaInfo routerMetaInfo = TinyRouterProvider.getInstance().getRouterMetaInfo(path);
        if (routerMetaInfo == null){
            Log.e(TAG, "build: 未发现路由信息: " + path);
        }
        return new TinyRouterTicket(routerMetaInfo);
    }

    @Override
    public void init(Application context) {
        mApplication = context;
        context.registerActivityLifecycleCallbacks(TinyRouterActivityLifecycleCallback.getInstance());
    }

    @Override
    public void deinit() {
        mApplication.unregisterActivityLifecycleCallbacks(TinyRouterActivityLifecycleCallback.getInstance());
        TinyRouterActivityLifecycleCallback.getInstance().clear();
        TinyRouterProvider.getInstance().clear();
        mApplication = null;
    }

    @Override
    public Object navigation(TinyRouterTicket ticket, ActivityResultCallback activityResultCallback) {
        return navigation(ticket, activityResultCallback, null);
    }

    @Override
    public Object navigation(final TinyRouterTicket ticket, final ActivityResultCallback activityResultCallback, NavigationResultCallback navigationResultCallback) {
        TinyRouteMetaInfo routerMetaInfo = ticket.getRouterMetaInfo();
        if (routerMetaInfo == null){
            if (navigationResultCallback != null){
                navigationResultCallback.onUnFound();
            }
            return null;
        }
        if (navigationResultCallback != null) {
            navigationResultCallback.onFound();
        }
        if (routerMetaInfo.getRouteType() == RouteType.ACTIVITY && !ticket.isGreenChannel()){
            //超时处理
            AtomicBoolean isDone = new AtomicBoolean(false);
            Runnable timeoutRunnable = () -> {
                if (!isDone.getAndSet(true)){
                    if (navigationResultCallback != null){
                        navigationResultCallback.onError(new RuntimeException("拦截器处理超时。"));
                    }
                    Log.e(TAG, "navigation: 拦截器处理超时....");
                }
            };
            doInterceptor(ticket, null, new TinyRouterInterceptorCallback(){
                @Override
                public void doContinue() {
                    if (!isDone.getAndSet(true)){
                        Object o = doCompleteNavigation(ticket, activityResultCallback);
                        if (navigationResultCallback != null){
                            navigationResultCallback.onCompleted(o);
                        }
                    }
                }

                @Override
                public void doInterrupt(Throwable throwable) {
                    if (!isDone.getAndSet(true)) {
                        if (navigationResultCallback != null){
                            navigationResultCallback.onError(throwable);
                        }else{
                            throwable.printStackTrace();
                        }
                    }
                }
            });
            mHandler.postDelayed(timeoutRunnable, ticket.getTimeout());
        }else{
            return doCompleteNavigation(ticket, activityResultCallback);
        }
        return null;
    }


    private Object doCompleteNavigation(TinyRouterTicket ticket, ActivityResultCallback activityResultCallback){
        TinyRouteMetaInfo routerMetaInfo = ticket.getRouterMetaInfo();
        Context currentContext = ticket.getContext();
        if (currentContext == null){
            currentContext = TinyRouterActivityLifecycleCallback.getInstance().getTopActivity();
        }
        if (currentContext == null){
            currentContext = mApplication;
        }
        switch (routerMetaInfo.getRouteType()){
            case ACTIVITY:
                return startActivity(currentContext, ticket, activityResultCallback);
            case FRAGMENT:
            case FRAGMENT_X:
            {
                try {
                    Class<?> targetClass = routerMetaInfo.getTargetClass();
                    Constructor<?> constructor = targetClass.getConstructor();
                    Object newInstance = constructor.newInstance();
                    Method setArguments = targetClass.getMethod("setArguments", Bundle.class);
                    setArguments.setAccessible(true);
                    setArguments.invoke(newInstance, ticket.getBundle());
                    return newInstance;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            case SERVICE:
            case BROADCAST:
            case CONTENT_PROVIDER:
            case UNKNOWN:
            default:
                return ticket.getRouterMetaInfo().getTargetClass();
        }
    }

    @SuppressLint("WrongConstant")
    private Object startActivity(Context currentContext, TinyRouterTicket ticket, ActivityResultCallback activityResultCallback){
        TinyRouteMetaInfo routerMetaInfo = ticket.getRouterMetaInfo();
        Intent intent = new Intent(currentContext, routerMetaInfo.getTargetClass());
        if (ticket.getFlags() != 0){
            intent.setFlags(ticket.getFlags());
        }
        if (!(currentContext instanceof Activity)){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (StringUtils.isNotEmpty(ticket.getAction())){
            intent.setAction(ticket.getAction());
        }
        if (ticket.getBundle()!= null){
            intent.putExtras(ticket.getBundle());
        }
        int enterAnim = ticket.getEnterAnim();
        int exitAnim = ticket.getExitAnim();
        mHandler.post(() -> {
            if (activityResultCallback == null){
                ActivityCompat.startActivity(currentContext, intent, ticket.getOptionsCompat());
            }else{
                TinyRouterActivityUtils.startActivity(currentContext, intent, ticket.getOptionsCompat(), activityResultCallback);
            }
            if (enterAnim != -1 && exitAnim != -1 && currentContext instanceof Activity){
                ((Activity) currentContext).overridePendingTransition(enterAnim, exitAnim);
            }
        });
        return null;
    }

    private void doInterceptor(TinyRouterTicket ticket, ITinyRouterInterceptor nextInterceptor, TinyRouterInterceptorCallback interceptorCallback){
        nextInterceptor = TinyRouterProvider.getInstance().getNextInterceptor(nextInterceptor, ticket.getRouterMetaInfo());
        if (nextInterceptor != null){
            ITinyRouterInterceptor finalNextInterceptor = nextInterceptor;
            nextInterceptor.intercept(ticket.getRouterMetaInfo(), new TinyRouterInterceptorCallback() {
                @Override
                public void doContinue() {
                    doInterceptor(ticket, finalNextInterceptor, interceptorCallback);
                }

                @Override
                public void doInterrupt(Throwable throwable) {
                    interceptorCallback.doInterrupt(throwable);
                }
            });
        }else{
            interceptorCallback.doContinue();
        }
    }

    @Override
    public void inject(Object target) {
        TinyRouteMetaInfo routerMetaInfo = TinyRouterProvider.getInstance().getRouterMetaInfo(target.getClass());
        List<AutowiredInfo> autowiredInfoList = null;
        if (routerMetaInfo != null){
            autowiredInfoList = routerMetaInfo.getAutowiredInfoList();
        }
        Bundle extras = null;
        if (target instanceof Activity){
            extras = ((Activity) target).getIntent().getExtras();
        }else if (target instanceof Fragment){
            extras = ((Fragment) target).getArguments();
        }else if (target instanceof android.app.Fragment){
            extras = ((android.app.Fragment) target).getArguments();
        }else{
            Log.e(TAG, "inject: 不支持的参数注入类:" + target.getClass().getCanonicalName());
            return;
        }
        if (autowiredInfoList != null){
            injectValue(target, autowiredInfoList, extras);
        }else {
            injectValue(target, extras);
        }
    }

    private void injectValue(Object target, Bundle extras) {
        try {
            if (extras == null){
                return;
            }
            Class<?> aClass = target.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                Autowired annotation = declaredField.getAnnotation(Autowired.class);
                if (annotation == null) {
                    continue;
                }
                String name = annotation.name();
                if (name.isEmpty()){
                    name = declaredField.getName();
                }
                Object o = extras.get(name);
                if (o == null){
                    continue;
                }
                declaredField.setAccessible(true);
                declaredField.set(target, o);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void injectValue(Object target, List<AutowiredInfo> autowiredInfoList, Bundle extras) {
        try {
            if (extras == null){
                return;
            }
            Class<?> targetClass = target.getClass();
            for (AutowiredInfo autowiredInfo : autowiredInfoList) {
                Object o = extras.get(autowiredInfo.getName());
                if (o == null){
                    continue;
                }
                Field declaredField = targetClass.getDeclaredField(autowiredInfo.getFieldName());
                declaredField.setAccessible(true);
                declaredField.set(target, o);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
