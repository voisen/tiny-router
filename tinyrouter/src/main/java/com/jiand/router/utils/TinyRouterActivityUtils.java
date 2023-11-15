package com.jiand.router.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jiand.router.callback.ActivityResultCallback;
import com.jiand.router.core.TinyRouterActivityLifecycleCallback;

/**
 * @author jiand
 */
public class TinyRouterActivityUtils {

    public static void startActivity(Context context, Intent intent, Bundle options, ActivityResultCallback activityResultCallback){
        FragmentActivity activity = null;
        if (context instanceof Activity){
            activity = (FragmentActivity) context;
        }else{
            Activity topActivity = TinyRouterActivityLifecycleCallback.getInstance().getTopActivity();
            if (topActivity instanceof FragmentActivity){
                activity = (FragmentActivity) topActivity;
            }
        }
        if (activity == null){
            throw new IllegalArgumentException("Context 必须是FragmentActivity的子类, 请给定正确的上下文环境 Context !");
        }
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(_CallbackFragment.TAG_FRAGMENT);
        if (fragment == null){
            fragment = new _CallbackFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(fragment, _CallbackFragment.TAG_FRAGMENT);
            fragmentTransaction.commitNow();
        }
        _CallbackFragment callbackFragment = (_CallbackFragment) fragment;
        callbackFragment.startActivity(intent, options, activityResultCallback);
    }

    @SuppressWarnings("All")
    public static class _CallbackFragment extends Fragment {
        protected static final String TAG_FRAGMENT = "__TINY_ROUTER_CALLBACK_FRAGMENT";
        private static final String TAG = "_CallbackFragment";

        private final SparseArray<ActivityResultCallback> callbackList = new SparseArray<>();
        private int requestCode = 0;

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            ActivityResultCallback activityResultCallback = callbackList.get(requestCode);
            if (activityResultCallback != null){
                callbackList.remove(requestCode);
                activityResultCallback.onActivityResult(resultCode, data);
            }
        }

        protected void startActivity(Intent intent, Bundle options, ActivityResultCallback activityResultCallback){
            callbackList.append(requestCode, activityResultCallback);
            startActivityForResult(intent, requestCode, options);
            requestCode++;
        }

    }

}
