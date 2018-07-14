package com.agmbat.db.converter;

import android.database.Cursor;

import com.agmbat.db.sql.ColumnDbType;

import java.util.Date;

public class DateColumnConverter implements ColumnConverter<Date> {
    @Override
    public Date getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : new Date(cursor.getLong(index));
    }

    @Override
    public Object fieldValueToDbValue(Date fieldValue) {
        if (fieldValue == null) return null;
        return fieldValue.getTime();
    }

    @Override
    public ColumnDbType getColumnDbType() {
        return ColumnDbType.INTEGER;
    }
}
