package com.agmbat.db.converter;

import android.database.Cursor;

import com.agmbat.db.sql.ColumnDbType;

public class CharColumnConverter implements ColumnConverter<Character> {
    @Override
    public Character getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : (char) cursor.getInt(index);
    }

    @Override
    public Object fieldValueToDbValue(Character fieldValue) {
        if (fieldValue == null)
            return null;
        return (int) fieldValue;
    }

    @Override
    public ColumnDbType getColumnDbType() {
        return ColumnDbType.INTEGER;
    }
}
