package com.jiand.tinyrouter.annotation.meta.route;

/**
 * @author jiand
 */

public enum RouteType {

    /**
     * 路由类型
     */
    ACTIVITY("android.app.Activity"),

    /**
     * 路由类型
     */
    FRAGMENT("android.app.Fragment"),
    FRAGMENT_X("androidx.fragment.app.Fragment"),

    /**
     * 路由类型
     */
    SERVICE("android.app.Service"),
    SERVICE_X("androidx.core.app.Service"),


    /**
     * 路由类型
     */
    BROADCAST("android.content.BroadcastReceiver"),

    /**
     * 路由类型
     */
    CONTENT_PROVIDER("android.content.ContentProvider"),

    /**
     * 其他
     */
    UNKNOWN("");
    private String base;

    public String getBase() {
        return base;
    }

    RouteType(String base) {
        this.base = base;
    }
}
