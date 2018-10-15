package com.agmbat.db;

public class DbConfig {

    protected String mDbName;
    protected boolean mAllowTransaction;

    public boolean isAllowTransaction() {
        return mAllowTransaction;
    }

    public String getDbName() {
        return mDbName;
    }

    public void setDbName(String name) {
        mDbName = name;
    }

}