package com.jiand.router;

import android.app.Application;

import com.jiand.router.callback.ActivityResultCallback;
import com.jiand.router.callback.NavigationResultCallback;
import com.jiand.router.core.TinyRouterTicket;
import com.jiand.router.core.TinyRouterImpl;

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
