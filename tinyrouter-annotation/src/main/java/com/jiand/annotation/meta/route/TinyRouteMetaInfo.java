package com.jiand.annotation.meta.route;

import java.util.List;

/**
 * @author jiand
 */
public class TinyRouteMetaInfo {
    private final RouteType routeType;
    private final Class<?> targetClass;
    private final String path;
    private final int extras;
    private final List<AutowiredInfo> autowiredInfoList;

    public TinyRouteMetaInfo(RouteType routeType, Class<?> targetClass, String path, int extras, List<AutowiredInfo> autowiredInfoList) {
        this.routeType = routeType;
        this.targetClass = targetClass;
        this.path = path;
        this.extras = extras;
        this.autowiredInfoList = autowiredInfoList;
    }

    public RouteType getRouteType() {
        return routeType;
    }
    public Class<?> getTargetClass() {
        return targetClass;
    }

    public String getPath() {
        return path;
    }
    public int getExtras() {
        return extras;
    }
    public List<AutowiredInfo> getAutowiredInfoList() {
        return autowiredInfoList;
    }

}
