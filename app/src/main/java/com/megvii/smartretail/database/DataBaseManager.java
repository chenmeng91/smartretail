package com.megvii.smartretail.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 数据库操作管理类
 *
 * @author luojianding (luojianding@baidu.com)
 * @title DataBaseManager
 * @date 2014-10-23
 */
public class DataBaseManager {

    public static final Object dbLock = new Object();

    private static DataBaseManager instance = null;

    private DataBaseOpenHelper dbHelper;

    private HashMap<String, Set<ITableObserver>> observerMap = new HashMap<String, Set<ITableObserver>>();

    private Handler mainHandler;

    private DBManagerConfiguration config;

    private DataBaseManager() {
        mainHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                String tableName = (String) msg.obj;
                Set<ITableObserver> set = observerMap.get(tableName);
                if (set != null) {
                    Iterator<ITableObserver> iterator = set.iterator();
                    while (iterator.hasNext()) {
                        iterator.next().onTableChanged(tableName, msg.what);
                    }
                }
            }

        };
    }

    public static synchronized DataBaseManager getInstance() {
        if (instance == null) {
            instance = new DataBaseManager();
        }
        return instance;
    }

    public void init(DBManagerConfiguration iconfig) {
        config = iconfig;
        dbHelper = new DataBaseOpenHelper(config.context, config.dbName, null, config.dbVersion);
        dbHelper.setListenerList(config.dbChangeListenerList);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.fengchao.dao.DataBaseOperations#close() close方法尽量在退出程序时调用，当数据库没有引用时会自动关闭
     */
    public void close() {

        synchronized (dbLock) {
            if (dbHelper != null) {
                dbHelper.close();
            }

            if (instance != null) {
                instance = null;
            }
        }
    }

    public void registeTableObserver(String tableName, ITableObserver observer) {
        if (observerMap.containsKey(tableName)) {
            observerMap.get(tableName).add(observer);
        } else {
            HashSet<ITableObserver> observerSet = new HashSet<ITableObserver>();
            observerSet.add(observer);
            observerMap.put(tableName, observerSet);
        }
    }

    public void unregisteTableObserver(ITableObserver observer) {
        Iterator<Entry<String, Set<ITableObserver>>> iterator = observerMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, Set<ITableObserver>> entry = iterator.next();
            Set<ITableObserver> set = entry.getValue();
            set.remove(observer);
        }
    }

    public void notifyDataChanged(String table) {
        notifyDataChanged(table, 0);
    }

    public void notifyDataChanged(String table, int operation) {
        Message msg = mainHandler.obtainMessage(operation, table);
        msg.sendToTarget();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.fengchao.dao.DataBaseOperations#insertData(java.lang.String, android.content.ContentValues)
     * 插入一条数据，返回值是新插入行的行数, 返回行号=-1表示错误
     */
    public Long insertData(String table, String nullColumnHack, ContentValues values) {
        synchronized (dbLock) {
            Long i = null;
            if (dbHelper == null) {
                return -1L;
            }
            try {
                i = dbHelper.open().insert(table, nullColumnHack, values);

                if (config.autoNotifyChanged) {
                    Message msg = mainHandler.obtainMessage(ITableObserver.OPRETION_INSERT, table);
                    msg.sendToTarget();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return i;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.fengchao.dao.DataBaseOperations#insertData(java.lang.String, android.content.ContentValues)
     * 插入多条数据，插入成功返回true,否则返回false
     */
    public boolean insertData(String table, String nullColumnHack, List<ContentValues> values) {

        synchronized (dbLock) {
            if (dbHelper == null) {
                return false;
            }
            SQLiteDatabase db = dbHelper.open();
            db.beginTransaction();
            try {
                for (ContentValues v : values) {
                    db.insert(table, nullColumnHack, v);
                }
                db.setTransactionSuccessful();

                if (config.autoNotifyChanged) {
                    Message msg = mainHandler.obtainMessage(ITableObserver.OPRETION_INSERT, table);
                    msg.sendToTarget();
                }
                return true;
            } catch (Exception e) {
                return false;
            } finally {
                db.endTransaction();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.fengchao.dao.DataBaseOperations#insertDataBySql(java.lang.String, java.lang.String[])
     * args:sql语句中的参数,参数的顺序对应占位符顺序
     */
    public Long insertDataBySql(String sql, String[] args) throws Exception {
        synchronized (dbLock) {
            long rowNum = 0;
            if (dbHelper == null) {
                return rowNum;
            }
            SQLiteDatabase db = dbHelper.open();
            SQLiteStatement statement = db.compileStatement(sql);
            if (args != null) {
                int size = args.length;
                for (int i = 0; i < size; i++) {
                    statement.bindString(i + 1, args[i]);
                }
                rowNum = statement.executeInsert();
                statement.close();
            }
            return rowNum;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.fengchao.dao.DataBaseOperations#queryData(java.lang.String, java.lang.String[])
     */
    public Cursor queryData(String sql, String[] selectionArgs) {
        Cursor cursor = null;
        if (dbHelper == null) {
            return cursor;
        }
        synchronized (dbLock) {
            try {
                cursor = dbHelper.open().rawQuery(sql, selectionArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cursor;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.fengchao.dao.DataBaseOperations#queryData(java.lang.String, java.lang.String[])
     */
    public Cursor queryData(DaoQueryStructure query) {
        synchronized (dbLock) {
            Cursor cursor = null;
            if (dbHelper == null) {
                return cursor;
            }
            try {
                cursor =
                        dbHelper.open().query(query.tableName, query.columns, query.selection, query.selectionArgs,
                                query.groupBy, query.having, query.orderBy);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cursor;
        }
    }

    public Cursor queryAll(String tableName, String orderBy) {

        synchronized (dbLock) {
            Cursor cursor = null;
            if (dbHelper == null) {
                return cursor;
            }
            try {
                cursor = dbHelper.open().query(tableName, null, null, null, null, null, orderBy);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cursor;
        }
    }

    public int getCount(String tableName) {
        Cursor cursor = queryAll(tableName, null);
        try {
            return cursor.getCount();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int querySum(String tableName, String column, String selection, String[] selectionArgs) {
        synchronized (dbLock) {
            int unreadCount = 0;
            String raw = "select sum(" + column + ") from " + tableName;
            if (selection != null) {
                raw = raw + " where " + selection;
            }
            Cursor cursor = null;
            if (dbHelper == null) {
                return unreadCount;
            }
            try {
                cursor = dbHelper.open().rawQuery(raw, selectionArgs);
                if (cursor != null && cursor.moveToFirst()) {
                    unreadCount = cursor.getInt(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return unreadCount;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.fengchao.dao.DataBaseOperations#updateData(java.lang.String, android.content.ContentValues,
     * java.lang.String, java.lang.String[]) 返回值是影响的行数
     */
    public int updateData(String table, ContentValues values, String whereClause, String[] whereArgs) {
        synchronized (dbLock) {
            if (dbHelper == null) {
                return 0;
            }
            int rows = dbHelper.open().update(table, values, whereClause, whereArgs);

            if (config.autoNotifyChanged) {
                Message msg = mainHandler.obtainMessage(ITableObserver.OPRETION_UPDATE, table);
                msg.sendToTarget();
            }
            return rows;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.fengchao.dao.IDataBaseManager#updateData(java.lang.String, java.util.List, java.util.List,
     * java.util.List)
     */
    public boolean updateData(String table, List<ContentValues> values, List<String> whereClause,
                              List<String[]> whereArgs) {

        if (TextUtils.isEmpty(table) || values == null || whereClause == null || whereArgs == null
                || values.size() != whereClause.size() || whereClause.size() != whereArgs.size()) {
            return false;
        }

        synchronized (dbLock) {
            if (dbHelper == null) {
                return false;
            }
            SQLiteDatabase db = dbHelper.open();
            db.beginTransaction();
            try {
                for (int i = 0; i < values.size(); i++) {
                    db.update(table, values.get(i), whereClause.get(i), whereArgs.get(i));
                }
                db.setTransactionSuccessful();

                if (config.autoNotifyChanged) {
                    Message msg = mainHandler.obtainMessage(ITableObserver.OPRETION_UPDATE, table);
                    msg.sendToTarget();
                }
                return true;
            } catch (Exception e) {
                return false;
            } finally {
                db.endTransaction();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * 
     * @see com.baidu.fengchao.dao.DataBaseOperations#updateDataBySql(java.lang.String, java.lang.String[])
     */
    public void updateDataBySql(String sql, String[] args) throws Exception {
        synchronized (dbLock) {
            if (dbHelper == null) {
                return;
            }
            SQLiteStatement statement = dbHelper.open().compileStatement(sql);
            if (args != null && statement != null) {
                int size = args.length;
                for (int i = 0; i < size; i++) {
                    statement.bindString(i + 1, args[i]);
                }
                statement.execute();
                statement.close();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.fengchao.dao.DataBaseOperations#deleteData(java.lang.String, java.lang.String, java.lang.String[])
     * Returns: the number of rows affected if a whereClause is passed in, 0 otherwise. To remove all rows and get a
     * count pass "1" as the whereClause.
     */
    public int deleteData(String table, String whereClause, String[] whereArgs) {
        synchronized (dbLock) {
            int i = 0;
            try {
                if (dbHelper == null) {
                    return i;
                }
                i = dbHelper.open().delete(table, whereClause, whereArgs);

                if (config.autoNotifyChanged) {
                    Message msg = mainHandler.obtainMessage(ITableObserver.OPRETION_DELETE, table);
                    msg.sendToTarget();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return i;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.fengchao.dao.DataBaseOperations#updateData(java.lang.String, android.content.ContentValues,
     * java.lang.String, java.lang.String[]) 删除表中全部数据
     */
    public void deleteAll(String table) {
        synchronized (dbLock) {
            if (dbHelper == null) {
                return;
            }
            try {
                SQLiteDatabase db = dbHelper.open();
                db.beginTransaction();
                db.delete(table, null, null);
                db.setTransactionSuccessful();
                db.endTransaction();

                if (config.autoNotifyChanged) {
                    Message msg = mainHandler.obtainMessage(ITableObserver.OPRETION_DELETE, table);
                    msg.sendToTarget();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.fengchao.dao.IDataBaseManager#replace(java.lang.String, java.lang.String,
     * android.content.ContentValues) 如果存在则更新，不存在则插入
     */
    public long replace(String table, String nullColumnHack, ContentValues initialValues) {

        synchronized (dbLock) {
            if (dbHelper == null) {
                return 0;
            }
            long id = dbHelper.open().replace(table, nullColumnHack, initialValues);

            if (config.autoNotifyChanged) {
                Message msg = mainHandler.obtainMessage(0, table);
                msg.sendToTarget();
            }
            return id;
        }
    }

    public long replace(String table, String nullColumnHack, List<ContentValues> values) {
        synchronized (dbLock) {
            long result = -1;
            if (dbHelper == null) {
                return result;
            }
            SQLiteDatabase db = dbHelper.open();
            db.beginTransaction();
            try {
                for (ContentValues v : values) {
                    result = db.replace(table, nullColumnHack, v);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                return -1;
            } finally {
                db.endTransaction();
            }
            return result;
        }
    }

    public boolean deleteDataThenInsertData(String table, String whereClause, String[] whereArgs,
                                            String nullColumnHack, List<ContentValues> values) {

        synchronized (dbLock) {
            int lineDeleted = deleteData(table, whereClause, whereArgs);
            boolean insertSuccess = insertData(table, nullColumnHack, values);
            return insertSuccess;
        }
    }

}
