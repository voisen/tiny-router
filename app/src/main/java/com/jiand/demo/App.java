package com.jiand.demo;

import android.app.Application;

import com.jiand.tinyrouter.TinyRouter;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TinyRouter.getRouter().init(this);
    }
}
