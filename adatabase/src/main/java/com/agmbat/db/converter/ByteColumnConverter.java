package com.agmbat.db.converter;

import android.database.Cursor;

import com.agmbat.db.sql.ColumnDbType;

public class ByteColumnConverter implements ColumnConverter<Byte> {

    @Override
    public Byte getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : (byte) cursor.getInt(index);
    }

    @Override
    public Object fieldValueToDbValue(Byte fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnDbType getColumnDbType() {
        return ColumnDbType.INTEGER;
    }

}
