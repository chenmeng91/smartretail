package com.megvii.smartretail.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activity 入栈
        ActivityManager.getInstance().pushActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Activity 出栈
        ActivityManager.getInstance().popActivity(this);
    }
}
