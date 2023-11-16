package com.jiand.router.core;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiand
 */
public class TinyRouterActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {

    private static TinyRouterActivityLifecycleCallback instance = new TinyRouterActivityLifecycleCallback();
    public static TinyRouterActivityLifecycleCallback getInstance(){
        return instance;
    }

    private List<Activity> activities = new ArrayList<>();

    public Activity getTopActivity(){
        if (activities.size() > 0){
            return activities.get(activities.size() - 1);
        }else {
            return null;
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activities.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activities.remove(activity);
    }

    public void clear() {
        activities.clear();
    }
}
