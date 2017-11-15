package com.megvii.smartretail.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库管理类，管理数据库实例的创建、更新和开关
 * 
 * @title DataBaseOpenHelper
 * @author luojianding (luojianding@baidu.com)
 * @date 2014-10-23
 * 
 */
public class DataBaseOpenHelper extends SQLiteOpenHelper {

    private List<IDataBaseChangeListener> dbChangeListenerList = new ArrayList<>();
    private int dbVersion;

    public DataBaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        dbVersion = version;
    }

    public void setListenerList(List<IDataBaseChangeListener> dbChangeListenerList) {
        this.dbChangeListenerList = dbChangeListenerList;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final int size = dbChangeListenerList.size();
        for (int i = 0; i < size; i++) {
            IDataBaseChangeListener listener = dbChangeListenerList.get(i);
            if (listener.minVersion() > dbVersion) {
                continue;
            }
            listener.onCreate(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final int size = dbChangeListenerList.size();
        for (int i = oldVersion; i < newVersion; i++) {
            int changeVersion = i + 1;
            for (int j = 0; j < size; j++) {
                IDataBaseChangeListener listener = dbChangeListenerList.get(j);
                int minVersion = listener.minVersion();
                if (minVersion > changeVersion) {
                    continue;
                }

                listener.onUpgrade(db, oldVersion, changeVersion);
            }
        }

    }

    private SQLiteDatabase db;

    /**
     * open database
     * 
     * @return
     * @throws SQLException
     */
    public SQLiteDatabase open() throws SQLException {
        if (null == db || !db.isOpen()) {
            db = getWritableDatabase();
        }
        return db;
    }

    public void close() throws SQLException {
        if (null != db && db.isOpen()) {
            db.close();
        }
    }

}
