package com.agmbat.db.converter;

import android.database.Cursor;

import com.agmbat.db.sql.ColumnDbType;

public interface ColumnConverter<T> {

    T getFieldValue(final Cursor cursor, int index);

    Object fieldValueToDbValue(T fieldValue);

    ColumnDbType getColumnDbType();
}
