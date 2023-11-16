package com.jiand.tinyrouter.core;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;

import androidx.annotation.Nullable;

import com.jiand.tinyrouter.TinyRouter;
import com.jiand.tinyrouter.annotation.meta.route.TinyRouteMetaInfo;
import com.jiand.tinyrouter.callback.ActivityResultCallback;
import com.jiand.tinyrouter.callback.NavigationResultCallback;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author jiand
 */
public class TinyRouterTicket {

    private TinyRouteMetaInfo routerMetaInfo;

    private int mFlags = 0;

    private long timeout = 180000;

    private Bundle mBundle = new Bundle();

    private boolean mGreenChannel = false;

    private Context mContext;

    private String mAction;

    //// 动画
    private Bundle mOptionsCompat;
    private int mEnterAnim = -1;
    private int mExitAnim = -1;

    public TinyRouterTicket(TinyRouteMetaInfo routerMetaInfo) {
        this.routerMetaInfo = routerMetaInfo;
    }

    public Object navigation(){
        return navigation(null, null, null);
    }

    public Object navigation(Context context){
        return navigation(context, null, null);
    }

    public Object navigation(Context context, ActivityResultCallback activityResultCallback){
        return navigation(context, activityResultCallback, null);
    }

    public Object navigation(NavigationResultCallback navigationResultCallback){
        return navigation(null, null, navigationResultCallback);
    }
    public Object navigation(Context context, NavigationResultCallback navigationResultCallback){
        return navigation(context, null, navigationResultCallback);
    }

    public Object navigation(Context context, ActivityResultCallback activityResultCallback, NavigationResultCallback navigationResultCallback){
        this.mContext = context;
        return TinyRouter.getRouter().navigation(this, activityResultCallback, navigationResultCallback);
    }

    public TinyRouterTicket withOptionsCompat(Bundle optionsCompat){
        this.mOptionsCompat = optionsCompat;
        return this;
    }

    public TinyRouterTicket withAnimation(int enterAnim, int exitAnim){
        mEnterAnim = enterAnim;
        mExitAnim = exitAnim;
        return this;
    }

    public TinyRouterTicket addFlags(int flags){
        mFlags |= flags;
        return this;
    }

    public TinyRouterTicket withFlags(int flags){
        mFlags = flags;
        return this;
    }

    public TinyRouterTicket withAction(String action){
        this.mAction = action;
        return this;
    }

    public TinyRouterTicket withTimeout(long timeout){
        this.timeout = timeout;
        return this;
    }

    public TinyRouterTicket greenChannel(){
        mGreenChannel = true;
        return this;
    }

    public TinyRouterTicket withAll(Bundle bundle){
        if (bundle!= null) {
            mBundle.putAll(bundle);
        }
        return this;
    }

    public TinyRouterTicket withString(@Nullable String key, String value){
        mBundle.putString(key, value);
        return this;
    }

    public TinyRouterTicket withBundle(@Nullable Bundle value) {
        this.mBundle = value;
        return this;
    }


    public TinyRouterTicket withByte(@Nullable String key, byte value) {
        mBundle.putByte(key, value);
        return this;
    }

    public TinyRouterTicket withChar(@Nullable String key, char value) {
        mBundle.putChar(key, value);
        return this;
    }

    public TinyRouterTicket withShort(@Nullable String key, short value) {
        mBundle.putShort(key, value);
        return this;
    }

    public TinyRouterTicket withFloat(@Nullable String key, float value) {
        mBundle.putFloat(key, value);
        return this;
    }
    public TinyRouterTicket withCharSequence(@Nullable String key, @Nullable CharSequence value) {
        mBundle.putCharSequence(key, value);
        return this;
    }
    public TinyRouterTicket withParcelable(@Nullable String key, @Nullable Parcelable value) {
        mBundle.putParcelable(key, value);
        return this;
    }
    
    public TinyRouterTicket withSize(@Nullable String key, @Nullable Size value) {
        mBundle.putSize(key, value);
        return this;
    }

    public TinyRouterTicket withSizeF(@Nullable String key, @Nullable SizeF value) {
        mBundle.putSizeF(key, value);
        return this;
    }

    public TinyRouterTicket withParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        mBundle.putParcelableArray(key, value);
        return this;
    }

    public TinyRouterTicket withParcelableArrayList(@Nullable String key,
                                                    @Nullable ArrayList<? extends Parcelable> value) {
        mBundle.putParcelableArrayList(key, value);
        return this;
    }

    public TinyRouterTicket withSparseParcelableArray(@Nullable String key,
                                                      @Nullable SparseArray<? extends Parcelable> value) {
        mBundle.putSparseParcelableArray(key, value);
        return this;
    }

    public TinyRouterTicket withIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        mBundle.putIntegerArrayList(key, value);
        return this;
    }

    
    public TinyRouterTicket withStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        mBundle.putStringArrayList(key, value);
        return this;
    }

    public TinyRouterTicket withCharSequenceArrayList(@Nullable String key,
                                                      @Nullable ArrayList<CharSequence> value) {
        mBundle.putCharSequenceArrayList(key, value);
        return this;
    }

    public TinyRouterTicket withSerializable(@Nullable String key, @Nullable Serializable value) {
        mBundle.putSerializable(key, value);
        return this;
    }

    public TinyRouterTicket withByteArray(@Nullable String key, @Nullable byte[] value) {
        mBundle.putByteArray(key, value);
        return this;
    }

    
    public TinyRouterTicket withShortArray(@Nullable String key, @Nullable short[] value) {
        mBundle.putShortArray(key, value);
        return this;
    }

    
    public TinyRouterTicket withCharArray(@Nullable String key, @Nullable char[] value) {
        mBundle.putCharArray(key, value);
        return this;
    }

    public TinyRouterTicket withFloatArray(@Nullable String key, @Nullable float[] value) {
        mBundle.putFloatArray(key, value);
        return this;
    }
    
    public TinyRouterTicket withCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value) {
        mBundle.putCharSequenceArray(key, value);
        return this;
    }

    public TinyRouterTicket withBundle(@Nullable String key, @Nullable Bundle value) {
        mBundle.putBundle(key, value);
        return this;
    }
    public TinyRouterTicket withBinder(@Nullable String key, @Nullable IBinder value) {
        mBundle.putBinder(key, value);
        return this;
    }

    public TinyRouteMetaInfo getRouterMetaInfo() {
        return routerMetaInfo;
    }

    public int getFlags() {
        return mFlags;
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public boolean isGreenChannel() {
        return mGreenChannel;
    }

    public Context getContext() {
        return mContext;
    }

    public String getAction() {
        return mAction;
    }

    public Bundle getOptionsCompat() {
        return mOptionsCompat;
    }

    public int getEnterAnim() {
        return mEnterAnim;
    }

    public int getExitAnim() {
        return mExitAnim;
    }

    public long getTimeout() {
        return timeout;
    }
}
