/*
 * Copyright (C) 2015 mayimchen <mayimchen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agmbat.android.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库操作工具类
 */
public class DatabaseUtil {

    /**
     * 判断某张表是否存在
     *
     * @param tableName 表名
     * @return
     */
    public static boolean isTableExist(SQLiteDatabase db, String tableName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        try {
            String sql =
                    "select count(1) as c from sqlite_master where type ='table' and name ='" + tableName.trim() + "'";
            Cursor cursor = db.rawQuery(sql, null);
            if (null != cursor) {
                if (cursor.moveToNext()) {
                    int count = cursor.getInt(0);
                    if (count > 0) {
                        result = true;
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断某张表中是否存在某字段(注，该方法无法判断表是否存在，因此应与isTableExist一起使用)
     *
     * @param tableName  表名
     * @param columnName 列名
     * @return
     */
    public boolean isColumnExist(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        try {
            String sql =
                    "select count(1) as c from sqlite_master where type ='table' and name ='" + tableName.trim()
                            + "' and sql like '%" + columnName.trim() + "%'";
            Cursor cursor = db.rawQuery(sql, null);
            if (null != cursor) {
                if (cursor.moveToNext()) {
                    int count = cursor.getInt(0);
                    if (count > 0) {
                        result = true;
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
