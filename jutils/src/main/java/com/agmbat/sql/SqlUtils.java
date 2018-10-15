/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * sql
 *
 * @author mayimchen
 * @since 2016-12-17
 */
package com.agmbat.sql;

/**
 * 数据库常用语句
 */
public class SqlUtils {

    /**
     * Add a column to a table using ALTER TABLE.
     *
     * @param table            name of the table
     * @param columnName       name of the column to add
     * @param columnDefinition SQL for the column definition
     */
    public static String addColumnSql(String table, String columnName, String columnDefinition) {
        return "ALTER TABLE " + table + " ADD COLUMN " + columnName + " " + columnDefinition;
    }

    public static String dropTableSql(String table) {
        return "DROP TABLE IF EXISTS " + table;
    }

    public static String deleteFromTableSql(String table) {
        return "DELETE FROM " + table;
    }

    public static String selectMaxSql(String table, String columnName) {
        return String.format("SELECT MAX(%s) FROM %s", columnName, table);
    }

    public static String fullColumns(String table, String columnName) {
        return String.format("%s.%s", table, columnName);
    }

}
