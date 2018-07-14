package com.agmbat.db.sql;

public class OrderBy {

    private String columnName;
    private boolean desc;

    public OrderBy(String columnName) {
        this.columnName = columnName;
    }

    public OrderBy(String columnName, boolean desc) {
        this.columnName = columnName;
        this.desc = desc;
    }

    public String build() {
        return "\"" + columnName + "\"" + (desc ? " DESC" : " ASC");
    }

    @Override
    public String toString() {
        return "\"" + columnName + "\"" + (desc ? " DESC" : " ASC");
    }
}