package com.agmbat.db;

public class DbConfig {

    protected String mDbName;
    protected boolean mAllowTransaction;

    public boolean isAllowTransaction() {
        return mAllowTransaction;
    }

    public void setDbName(String name) {
        mDbName = name;
    }

    public String getDbName() {
        return mDbName;
    }

}