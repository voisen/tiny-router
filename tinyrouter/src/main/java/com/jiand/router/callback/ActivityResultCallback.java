package com.jiand.router.callback;

import android.content.Intent;
import android.os.Bundle;

/**
 * @author jiand
 */
public interface ActivityResultCallback {
    /**
     * 回调
     * @param resultCode 结果码
     * @param data 结果数据
     */
    void onActivityResult(int resultCode, Intent data);
}
