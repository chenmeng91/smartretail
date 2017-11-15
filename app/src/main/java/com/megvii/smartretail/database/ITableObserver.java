package com.megvii.smartretail.database;

/**
 * 数据库表的监听回调接口
 * 
 * @title TableObserver
 * @author luojianding (luojianding@baidu.com)
 * @date 2014-10-27
 * 
 */
public interface ITableObserver {

    public static final int OPRETION_INSERT = 1;
    public static final int OPRETION_DELETE = 2;
    public static final int OPRETION_UPDATE = 3;

    /**
     * 当表中数据发生变化时，所调用的回调接口
     * 
     * @param tableName
     * @param opretion
     */
    public void onTableChanged(String tableName, int opretion);

}
