package com.megvii.smartretail.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库变化监听器,用于各表监听数据库的创建、升级
 * 
 * @title IDataBaseChangeListener
 * @author luojianding (luojianding@baidu.com)
 * @date 2014-12-4
 * 
 */
public interface IDataBaseChangeListener {

    /**
     * 该监听所作用的最小数据库版本号，当数据当前版本小于该版本时，该监听不起任何作用
     * 
     * @return
     */
    int minVersion();

    /**
     * 数据库创建时的回调
     * 
     * @param db
     */
    void onCreate(SQLiteDatabase db);

    /**
     * 数据库升级时的回调。 该调用会从旧数据库的版本（不包含）开始逐步上升，每个版本调用一次该方法，直到新数据库版本（包含）为止
     * 
     * @param db
     * @param oldVersion
     * @param currentVersion
     */
    void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion);
}
