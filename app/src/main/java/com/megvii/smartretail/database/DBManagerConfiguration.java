package com.megvii.smartretail.database;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库管理的配置类
 * 
 * @title DBManagerConfiguration
 * @author luojianding (luojianding@baidu.com)
 * @date 2014-12-4
 * 
 */
public class DBManagerConfiguration {
    Context context;
    int dbVersion;
    String dbName;
    boolean autoNotifyChanged;
    List<IDataBaseChangeListener> dbChangeListenerList = new ArrayList<IDataBaseChangeListener>();

    private DBManagerConfiguration(Builder builder) {
        context = builder.context;
        dbVersion = builder.dbVersion;
        dbName = builder.dbName;
        autoNotifyChanged = builder.autoNotifyChanged;
        dbChangeListenerList = builder.dbChangeListenerList;
    }

    public static class Builder {
        private Context context;
        private int dbVersion;
        private String dbName;
        private boolean autoNotifyChanged;
        private List<IDataBaseChangeListener> dbChangeListenerList = new ArrayList<IDataBaseChangeListener>();

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder setDbVersion(int dbVersion) {
            this.dbVersion = dbVersion;
            return this;
        }

        public Builder setDbName(String dbName) {
            this.dbName = dbName;
            return this;
        }

        public Builder setAutoNotifyChanged(boolean autoNotifyChanged) {
            this.autoNotifyChanged = autoNotifyChanged;
            return this;
        }

        public Builder addDataBaseChangeListener(IDataBaseChangeListener listener) {
            dbChangeListenerList.add(listener);
            return this;
        }

        public DBManagerConfiguration build() {
            initEmptyFieldsWithDefaultValues();
            return new DBManagerConfiguration(this);
        }

        private void initEmptyFieldsWithDefaultValues() {

        }
    }
}
