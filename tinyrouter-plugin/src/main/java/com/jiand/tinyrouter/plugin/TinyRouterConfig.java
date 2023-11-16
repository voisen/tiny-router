package com.jiand.tinyrouter.plugin;

/**
 * @author jiand
 */
public interface TinyRouterConfig {

    String TINY_ROUTER_PACKAGE = "com.jiand.tinyrouter";

    String TINY_ROUTER_ROUTER_MAP_FILE = "com/jiand/tinyrouter/core/TinyRouterProvider.class";
    String TINY_ROUTER_ROUTER_PROVIDER_NAME = "com/jiand/tinyrouter/core/TinyRouterProvider";
    String TINY_ROUTER_ROUTER_PROVIDER_REGISTER_METHOD_NAME = "register";

    String CLASS_EXT = ".class";
    String TINY_ROUTER_INTERCEPTOR_LOADER = "com/jiand/tinyrouter/core/loader/_IInterceptorLoader";
    String TINY_ROUTER_ROUTE_META_LOADER = "com/jiand/tinyrouter/core/loader/_ITinyRouteLoader";

    String TINY_ROUTER_LOADER_PREFIX = "com/jiand/tinyrouter";
}
