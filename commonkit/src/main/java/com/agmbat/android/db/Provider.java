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
package com.agmbat.android.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public abstract class Provider {


    private final long mId;

    public Provider() {
        this(-1);
    }

    public Provider(long id) {
        mId = id;
    }

    public static final void copyInteger(String key, ContentValues from, ContentValues to) {
        Integer i = from.getAsInteger(key);
        if (i != null) {
            to.put(key, i);
        }
    }

    public static final void copyBoolean(String key, ContentValues from, ContentValues to) {
        Boolean b = from.getAsBoolean(key);
        if (b != null) {
            to.put(key, b);
        }
    }

    public static final void copyString(String key, ContentValues from, ContentValues to) {
        String s = from.getAsString(key);
        if (s != null) {
            to.put(key, s);
        }
    }

    public static final void copyStringWithDefault(String key, ContentValues from,
                                                   ContentValues to,
                                                   String defaultValue) {
        copyString(key, from, to);
        if (!to.containsKey(key)) {
            to.put(key, defaultValue);
        }
    }

    public long getId() {
        return mId;
    }

    private boolean hasId() {
        return getId() != -1;
    }

    private SqlSelection getWhereClause(final String where, final String[] whereArgs) {
        SqlSelection selection = new SqlSelection();
        selection.appendClause(where, whereArgs);
        if (hasId()) {
            selection.appendClause(BaseColumns._ID + " = ?", getId());
        }
        return selection;
    }

    public Cursor query(SQLiteDatabase db, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SqlSelection fullSelection = getWhereClause(selection, selectionArgs);
        return db.query(getTable(), projection, fullSelection.getSelection(),
                fullSelection.getParameters(), null, null, sortOrder);
    }

    public long insert(SQLiteDatabase db, ContentValues values) {
        if (hasId()) {
            throw new RuntimeException("can't insert uri");
        }
        return db.insert(getTable(), null, values);
    }

    public int delete(SQLiteDatabase db, String where, String[] whereArgs) {
        SqlSelection selection = getWhereClause(where, whereArgs);
        return db.delete(getTable(), selection.getSelection(), selection.getParameters());
    }

    public int update(SQLiteDatabase db, ContentValues values, String where,
                      String[] whereArgs) {
        int count;
        if (values.size() > 0) {
            SqlSelection selection = getWhereClause(where, whereArgs);
            count = db.update(getTable(), values, selection.getSelection(),
                    selection.getParameters());
        } else {
            count = 0;
        }
        return count;
    }

    protected abstract String getTable();

}
