package com.jiand.demo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.jiand.annotation.Route;

/**
 * @author jiand
 */
@Route(path = "/test/service")
public class TestService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
