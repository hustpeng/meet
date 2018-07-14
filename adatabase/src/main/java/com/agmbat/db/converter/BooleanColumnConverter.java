package com.agmbat.db.converter;

import android.database.Cursor;

import com.agmbat.db.sql.ColumnDbType;

public class BooleanColumnConverter implements ColumnConverter<Boolean> {

    @Override
    public Boolean getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getInt(index) == 1;
    }

    @Override
    public Object fieldValueToDbValue(Boolean fieldValue) {
        if (fieldValue == null) {
            return null;
        }
        return fieldValue ? 1 : 0;
    }

    @Override
    public ColumnDbType getColumnDbType() {
        return ColumnDbType.INTEGER;
    }

}
