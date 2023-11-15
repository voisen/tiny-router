package com.jiand.demo;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jiand.annotation.*;
import com.jiand.router.TinyRouter;
import com.jiand.router.callback.ActivityResultCallback;
import com.jiand.router.callback.NavigationResultCallback;

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