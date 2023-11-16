package com.jiand.libdemo;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jiand.tinyrouter.annotation.Route;


@Route(path = "/demo/test")
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.libdemo_activity_test);
    }
}