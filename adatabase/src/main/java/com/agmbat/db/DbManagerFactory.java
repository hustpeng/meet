package com.agmbat.db;

import android.database.sqlite.SQLiteDatabase;

import com.agmbat.db.DbManager.DbUpgradeListener;
import com.agmbat.log.Log;
import com.agmbat.sqlite.SqliteDbConfig;
import com.agmbat.sqlite.SqliteDbManager;

import java.util.HashMap;

public class DbManagerFactory {

    private static HashMap<DbConfig, DbManager> daoMap = new HashMap<DbConfig, DbManager>();

    public synchronized static DbManager getInstance(DbConfig config) {
        SqliteDbManager dao = (SqliteDbManager) daoMap.get(config);
        if (dao == null) {
            dao = new SqliteDbManager((SqliteDbConfig) config);
            daoMap.put(config, dao);
        }
        // update the database if needed
        SQLiteDatabase database = dao.mSQLDatabase;
        int oldVersion = database.getVersion();
        int newVersion = ((SqliteDbConfig) config).getDbVersion();
        if (oldVersion != newVersion) {
            if (oldVersion != 0) {
                DbUpgradeListener upgradeListener = ((SqliteDbConfig) config).getDbUpgradeListener();
                if (upgradeListener != null) {
                    upgradeListener.onUpgrade(dao, oldVersion, newVersion);
                } else {
                    try {
                        dao.dropDb();
                    } catch (DbException e) {
                        Log.e(e.getMessage(), e);
                    }
                }
            }
            database.setVersion(newVersion);
        }
        return dao;
    }

    public static void remove(DbConfig config) {
        daoMap.remove(config);
    }

}