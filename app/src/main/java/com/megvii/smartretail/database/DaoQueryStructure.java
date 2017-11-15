package com.megvii.smartretail.database;


/**
 * 数据库查询语句的封装类
 *
 * @title DaoQueryStructure
 * @author luojianding (luojianding@baidu.com)
 * @date 2014-10-23
 *
 */
public class DaoQueryStructure {

    String tableName;

    String[] columns;

    String selection;

    String[] selectionArgs;

    String groupBy;

    String having;

    String orderBy;

    String limit;

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        this.selectionArgs = selectionArgs;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public void setHaving(String having) {
        this.having = having;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }
}
