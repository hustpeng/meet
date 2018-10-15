package com.agmbat.sqlite;

import com.agmbat.db.DbConfig;
import com.agmbat.db.DbManager.DbUpgradeListener;

import java.io.File;

public class SqliteDbConfig extends DbConfig {

    private int dbVersion = 1;
    private DbUpgradeListener dbUpgradeListener;
    private File dbDir;

    public SqliteDbConfig() {
        mDbName = "sqlite3"; // default db name
        mAllowTransaction = true;
    }

    public int getDbVersion() {
        return dbVersion;
    }

    public SqliteDbConfig setDbVersion(int dbVersion) {
        this.dbVersion = dbVersion;
        return this;
    }

    public boolean isAllowTransaction() {
        return mAllowTransaction;
    }

    public SqliteDbConfig setAllowTransaction(boolean allowTransaction) {
        this.mAllowTransaction = allowTransaction;
        return this;
    }

    public String getDbName() {
        return mDbName;
    }

    public DbUpgradeListener getDbUpgradeListener() {
        return dbUpgradeListener;
    }

    public SqliteDbConfig setDbUpgradeListener(DbUpgradeListener dbUpgradeListener) {
        this.dbUpgradeListener = dbUpgradeListener;
        return this;
    }

    public File getDbDir() {
        return dbDir;
    }

    public SqliteDbConfig setDbDir(File dbDir) {
        this.dbDir = dbDir;
        return this;
    }

}