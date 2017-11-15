package com.megvii.smartretail.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.megvii.smartretail.app.SmartretailApplication;

/**
 * SharePreference管理工具,负责全局的SharePreference的管理
 */
public class PreferenceManager {
    private static PreferenceManager INSTANCE = new PreferenceManager();

    private SharedPreferences sharedPreferences;

    private PreferenceManager() {
        sharedPreferences = SmartretailApplication.getInstance().getSharedPreferences("smartretail", Context.MODE_PRIVATE);
    }

    public static PreferenceManager getInstance() {
        return INSTANCE;
    }

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).commit();
    }

    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }
}
