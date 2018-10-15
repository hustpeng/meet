/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * sql
 *
 * @author mayimchen
 * @since 2016-12-17
 */
package com.agmbat.sql;

import com.agmbat.text.StringUtils;

/**
 * 创建数据库表
 */
public class TableSqlBuilder {

    /**
     * 创建表sql语句
     */
    private static final String CREATE_TABLE_FORMAT = "CREATE TABLE IF NOT EXISTS %s (%s)";

    private final String mTableName;
    private final StringBuilder mBuilder;

    public TableSqlBuilder(String tableName) {
        mTableName = tableName;
        mBuilder = new StringBuilder();
    }

    public TableSqlBuilder addColumn(String columnName, String dataType, String... params) {
        if (mBuilder.length() > 0) {
            mBuilder.append(',');
        }
        mBuilder.append(columnName);
        mBuilder.append(' ');
        mBuilder.append(dataType);
        if (params.length > 0) {
            mBuilder.append(' ');
            for (String param : params) {
                mBuilder.append(param);
                mBuilder.append(' ');
            }
        }
        return this;
    }

    public TableSqlBuilder primaryKey(String... columns) {
        StringBuilder builder = new StringBuilder();
        for (String column : columns) {
            if (builder.length() > 0) {
                builder.append(", ");
            } else {
                builder.append(", PRIMARY KEY (");
            }
            builder.append(column);
        }
        builder.append(")");
        mBuilder.append(builder.toString());
        return this;
    }

    public String buildSql() {
        String text = mBuilder.toString();
        if (StringUtils.isEmpty(text)) {
            throw new IllegalArgumentException("can you add column?");
        }
        return String.format(CREATE_TABLE_FORMAT, mTableName, text);
    }
}
