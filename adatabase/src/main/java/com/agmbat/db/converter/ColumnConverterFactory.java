package com.agmbat.db.converter;

import com.agmbat.db.sql.ColumnDbType;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public final class ColumnConverterFactory {

    private static final ConcurrentHashMap<String, ColumnConverter<?>> COLUMNTYPE_COLUMNCONVERTER_MAP;

    static {
        COLUMNTYPE_COLUMNCONVERTER_MAP = new ConcurrentHashMap<String, ColumnConverter<?>>();

        BooleanColumnConverter booleanColumnConverter = new BooleanColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(boolean.class.getName(), booleanColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Boolean.class.getName(), booleanColumnConverter);

        ByteArrayColumnConverter byteArrayColumnConverter = new ByteArrayColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(byte[].class.getName(), byteArrayColumnConverter);

        ByteColumnConverter byteColumnConverter = new ByteColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(byte.class.getName(), byteColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Byte.class.getName(), byteColumnConverter);

        CharColumnConverter charColumnConverter = new CharColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(char.class.getName(), charColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Character.class.getName(), charColumnConverter);

        DateColumnConverter dateColumnConverter = new DateColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Date.class.getName(), dateColumnConverter);

        DoubleColumnConverter doubleColumnConverter = new DoubleColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(double.class.getName(), doubleColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Double.class.getName(), doubleColumnConverter);

        FloatColumnConverter floatColumnConverter = new FloatColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(float.class.getName(), floatColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Float.class.getName(), floatColumnConverter);

        IntegerColumnConverter integerColumnConverter = new IntegerColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(int.class.getName(), integerColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Integer.class.getName(), integerColumnConverter);

        LongColumnConverter longColumnConverter = new LongColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(long.class.getName(), longColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Long.class.getName(), longColumnConverter);

        ShortColumnConverter shortColumnConverter = new ShortColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(short.class.getName(), shortColumnConverter);
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(Short.class.getName(), shortColumnConverter);

        SqlDateColumnConverter sqlDateColumnConverter = new SqlDateColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(java.sql.Date.class.getName(), sqlDateColumnConverter);

        StringColumnConverter stringColumnConverter = new StringColumnConverter();
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(String.class.getName(), stringColumnConverter);
    }

    private ColumnConverterFactory() {
    }

    /**
     * 获取字段转为的实例
     *
     * @param columnType
     * @return
     */
    public static ColumnConverter<?> getColumnConverter(Class<?> columnType) {
        ColumnConverter<?> result = null;
        if (COLUMNTYPE_COLUMNCONVERTER_MAP.containsKey(columnType.getName())) {
            result = COLUMNTYPE_COLUMNCONVERTER_MAP.get(columnType.getName());
        } else {
            result = addColumnConverter(columnType);
        }
        if (result == null) {
            throw new RuntimeException("Database Column Not Support: " + columnType.getName()
                    + ", please impl ColumnConverter or use ColumnConverterFactory#registerColumnConverter(...)");
        }
        return result;
    }

    public static ColumnDbType getDbColumnType(Class<?> columnType) {
        ColumnConverter<?> converter = getColumnConverter(columnType);
        return converter.getColumnDbType();
    }

    /**
     * 添加自定义转换
     *
     * @param columnType
     * @param columnConverter
     */
    public static void registerColumnConverter(Class<?> columnType, ColumnConverter<?> columnConverter) {
        COLUMNTYPE_COLUMNCONVERTER_MAP.put(columnType.getName(), columnConverter);
    }

    /**
     * 判断是否支持字段转换
     *
     * @param columnType
     * @return
     */
    public static boolean isSupportColumnConverter(Class<?> columnType) {
        if (COLUMNTYPE_COLUMNCONVERTER_MAP.containsKey(columnType.getName())) {
            return true;
        }
        ColumnConverter<?> columnConverter = addColumnConverter(columnType);
        return columnConverter != null;
    }

    /**
     * 添加自定义的ColumnConverter
     *
     * @param columnType
     * @return 如果添加成功，返回ColumnConverter，否则返回null
     */
    private static ColumnConverter<?> addColumnConverter(Class<?> columnType) {
        if (ColumnConverter.class.isAssignableFrom(columnType)) {
            try {
                ColumnConverter<?> columnConverter = (ColumnConverter<?>) columnType.newInstance();
                if (columnConverter != null) {
                    COLUMNTYPE_COLUMNCONVERTER_MAP.put(columnType.getName(), columnConverter);
                }
                return columnConverter;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
