package com.agmbat.sql;

public class Param {

    /**
     * sqlite自动增长类型
     */
    public static final String AUTOINCREMENT = "AUTOINCREMENT";

    /**
     * mysql 自动增长类型
     */
    public static final String AUTO_INCREMENT = "AUTO_INCREMENT";

    public static final String NOT_NULL = "NOT NULL";
    public static final String DEFAULT = "DEFAULT";
    public static final String PRIMARY_KEY = "PRIMARY KEY";

    private static final String DEFAULT_FORMAT = "DEFAULT %s";

    public static final String defaultValue(String defaultValue) {
        return String.format(DEFAULT_FORMAT, defaultValue);
    }

}