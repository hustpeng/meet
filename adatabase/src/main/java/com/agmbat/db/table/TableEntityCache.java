package com.agmbat.db.table;

import com.agmbat.db.DbException;
import com.agmbat.db.DbManager;
import com.agmbat.text.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableEntityCache {

    /**
     * 数据库缓存
     */
    private static final HashMap<String, TableEntity<?>> TABLE_MAP = new HashMap<String, TableEntity<?>>();

    public static final ConcurrentHashMap<TableEntity<?>, String> INSERT_SQL_CACHE =
            new ConcurrentHashMap<TableEntity<?>, String>();


    @SuppressWarnings("unchecked")
    public static synchronized <T> TableEntity<T> get(DbManager db, Class<T> entityType) throws DbException {
        String tableKey = getTableKey(db, entityType);
        TableEntity<T> table = (TableEntity<T>) TABLE_MAP.get(tableKey);
        if (table == null) {
            try {
                table = new TableEntity<T>(db, entityType);
            } catch (Throwable ex) {
                throw new DbException(ex);
            }
            TABLE_MAP.put(tableKey, table);
        }
        return table;
    }

    public static synchronized void remove(DbManager db, Class<?> entityType) {
        String tableKey = getTableKey(db, entityType);
        TABLE_MAP.remove(tableKey);
    }

    public static synchronized void remove(DbManager db, String tableName) {
        if (TABLE_MAP.size() > 0) {
            String key = null;
            for (Map.Entry<String, TableEntity<?>> entry : TABLE_MAP.entrySet()) {
                TableEntity table = entry.getValue();
                if (table != null) {
                    if (table.getTableName().equals(tableName) && table.getDb() == db) {
                        key = entry.getKey();
                        break;
                    }
                }
            }
            if (!StringUtils.isEmpty(key)) {
                TABLE_MAP.remove(key);
            }
        }
    }

    /**
     * 获取table key
     *
     * @param db
     * @param entityType
     * @return
     */
    private static String getTableKey(DbManager db, Class entityType) {
        // key: dbName#className, 由于使用同一个数据库和表名, 导致不同目录下的同一种数据库串数据
        return db.getIdKey() + "#" + entityType.getName();
    }
}