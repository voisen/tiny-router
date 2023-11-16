package com.jiand.demo;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jiand.tinyrouter.TinyRouter;
import com.jiand.tinyrouter.annotation.Autowired;
import com.jiand.tinyrouter.annotation.Route;
import com.jiand.tinyrouter.callback.ActivityResultCallback;

import java.util.Date;

/**
 * @author jiand
 */
@Route(path = "/main")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Autowired(name = "time")
    private String val;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TinyRouter.getRouter().inject(this);
        textView = findViewById(R.id.tv_display);
        textView.setText(val);
    }

    public void testClick(View view) {
        TinyRouter.getRouter().build("/main")
                .withString("time", new Date().toString())
                .navigation(this, (resultCode, data) -> {
                    textView.setText("回调来了: " + data.getExtras().getString("data"));
                });
        System.out.println(TinyRouter.getRouter().build("/test")
                .navigation());

        TinyRouter.getRouter().build("/test/to-activity")
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .withAction(Intent.ACTION_VIEW)
                .withString("time", new Date().toString())
                .greenChannel()//绿色通道， 跳过拦截器
                .navigation(this, new ActivityResultCallback() {
                    @Override
                    public void onActivityResult(int i, Intent intent) {
                        //ToActivity通过setResult返回数据
                        //Intent intent = new Intent();
                        //intent.putExtra("data", "结束的回调: " + System.currentTimeMillis());
                        //setResult(RESULT_OK, intent);
                        Log.i(TAG, "onActivityResult: 返回的数据: " + intent);
                    }
                });

    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("data", "结束的回调: " + System.currentTimeMillis());
        setResult(RESULT_OK, intent);
        super.finish();
    }

    public void jumpToTestClick(View view) {
        TinyRouter.getRouter().build("/demo/test")
                .navigation();
    }
}