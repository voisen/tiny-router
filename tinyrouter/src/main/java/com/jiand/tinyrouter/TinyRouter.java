package com.jiand.tinyrouter;

import android.app.Application;

import com.jiand.tinyrouter.callback.ActivityResultCallback;
import com.jiand.tinyrouter.callback.NavigationResultCallback;
import com.jiand.tinyrouter.core.TinyRouterTicket;
import com.jiand.tinyrouter.core.TinyRouterImpl;

/**
 * @author jiand
 */
public interface TinyRouter {

    /**
     * 获取TinyRouter实例
     * @return TinyRouter
     */
    static TinyRouter getRouter(){
        return TinyRouterImpl.getInstance();
    }

    /**
     * 初始化
     * @param context 上下文
     */
    void init(Application context);

    /**
     * 反初始化
     */
    void deinit();

    /**
     * 构建一个Ticket
     * @param path 路径
     * @return Ticket
     */
    TinyRouterTicket build(String path);

    /**
     * 导航
     *
     * @param ticket Ticket
     * @param activityResultCallback 回调
     * @return
     */
    Object navigation(TinyRouterTicket ticket, ActivityResultCallback activityResultCallback);

    /**
     * 导航
     *
     * @param ticket Ticket
     * @param activityResultCallback 回调
     * @param navigationResultCallback 回调
     * @return
     */
    Object navigation(TinyRouterTicket ticket, ActivityResultCallback activityResultCallback, NavigationResultCallback navigationResultCallback);



    /**
     * 注入
     * @param target 目标对象
     */
    void inject(Object target);


}
