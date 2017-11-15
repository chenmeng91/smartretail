package com.megvii.smartretail.app;

import android.app.Application;

/**
 * Created by luojianding on 16/9/21.
 */
public class SmartretailApplication extends Application {
    private static SmartretailApplication INSTANCE = new SmartretailApplication();

    public static SmartretailApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
