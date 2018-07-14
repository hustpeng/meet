package com.agmbat.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.agmbat.db.sql.KeyValue;
import com.agmbat.db.sql.SqlInfo;
import com.agmbat.db.sql.WhereBuilder;
import com.agmbat.db.table.ColumnEntity;
import com.agmbat.db.table.DbModel;
import com.agmbat.db.table.TableEntity;
import com.agmbat.db.table.TableEntityCache;
import com.agmbat.db.utils.CursorUtils;
import com.agmbat.log.Log;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public abstract class BaseManager implements DbManager {

    protected SQLiteDatabase mSQLDatabase;
    protected DbConfig mConfig;

    public BaseManager(DbConfig config) {
        mConfig = config;
        mSQLDatabase = createDatabase(config);
    }

    protected abstract SQLiteDatabase createDatabase(DbConfig config);

    @Override
    public final void saveOrUpdate(Object entity) throws DbException {
        try {
            beginTransaction();
            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                TableEntity<?> table = TableEntityCache.get(this, entities.get(0).getClass());
                table.createTableIfNotExist();
                for (Object item : entities) {
                    saveOrUpdateWithoutTransaction(table, item);
                }
            } else {
                TableEntity<?> table = TableEntityCache.get(this, entity.getClass());
                table.createTableIfNotExist();
                saveOrUpdateWithoutTransaction(table, entity);
            }
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public void save(Object entity) throws DbException {
        try {
            beginTransaction();
            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                TableEntity<?> table = TableEntityCache.get(this, entities.get(0).getClass());
                table.createTableIfNotExist();
                for (Object item : entities) {
                    SqlInfo sqlInfo = buildInsertSqlInfo(table, item);
                    execNonQuery(sqlInfo);
                }
            } else {
                TableEntity<?> table = TableEntityCache.get(this, entity.getClass());
                table.createTableIfNotExist();
                SqlInfo sqlInfo = buildInsertSqlInfo(table, entity);
                execNonQuery(sqlInfo);
            }
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public final void replace(Object entity) throws DbException {
        try {
            beginTransaction();
            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                TableEntity<?> table = TableEntityCache.get(this, entities.get(0).getClass());
                table.createTableIfNotExist();
                for (Object item : entities) {
                    execNonQuery(table.buildReplaceSqlInfo(item));
                }
            } else {
                TableEntity<?> table = TableEntityCache.get(this, entity.getClass());
                table.createTableIfNotExist();
                execNonQuery(table.buildReplaceSqlInfo(entity));
            }
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public final boolean saveBindingId(Object entity) throws DbException {
        boolean result = false;
        try {
            beginTransaction();
            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                TableEntity<?> table = TableEntityCache.get(this, entities.get(0).getClass());
                table.createTableIfNotExist();
                for (Object item : entities) {
                    if (!saveBindingIdWithoutTransaction(table, item)) {
                        throw new DbException("saveBindingId error, transaction will not commit!");
                    }
                }
            } else {
                TableEntity<?> table = TableEntityCache.get(this, entity.getClass());
                table.createTableIfNotExist();
                result = saveBindingIdWithoutTransaction(table, entity);
            }
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
        return result;
    }

    @Override
    public final void deleteById(Class<?> entityType, Object idValue) throws DbException {
        TableEntity<?> table = TableEntityCache.get(this, entityType);
        if (!table.tableIsExist())
            return;
        try {
            beginTransaction();
            execNonQuery(buildDeleteSqlInfoById(table, idValue));
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public final void delete(Object entity) throws DbException {
        try {
            beginTransaction();
            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                TableEntity<?> table = TableEntityCache.get(this, entities.get(0).getClass());
                if (!table.tableIsExist())
                    return;
                for (Object item : entities) {
                    execNonQuery(table.buildDeleteSqlInfo(item));
                }
            } else {
                TableEntity<?> table = TableEntityCache.get(this, entity.getClass());
                if (!table.tableIsExist()) {
                    return;
                }
                execNonQuery(table.buildDeleteSqlInfo(entity));
            }
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public final void delete(Class<?> entityType) throws DbException {
        delete(entityType, null);
    }

    @Override
    public final void delete(Class<?> entityType, WhereBuilder whereBuilder) throws DbException {
        TableEntity<?> table = TableEntityCache.get(this, entityType);
        if (!table.tableIsExist())
            return;
        try {
            beginTransaction();
            execNonQuery(table.buildDeleteSqlInfo(whereBuilder));
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public final void update(Object entity, String... updateColumnNames) throws DbException {
        try {
            beginTransaction();
            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                TableEntity<?> table = TableEntityCache.get(this, entities.get(0).getClass());
                if (!table.tableIsExist())
                    return;
                for (Object item : entities) {
                    execNonQuery(table.buildUpdateSqlInfo(item, updateColumnNames));
                }
            } else {
                TableEntity<?> table = TableEntityCache.get(this, entity.getClass());
                if (!table.tableIsExist()) {
                    return;
                }
                execNonQuery(table.buildUpdateSqlInfo(entity, updateColumnNames));
            }
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public final void update(Object entity, WhereBuilder whereBuilder, String... updateColumnNames) throws DbException {
        try {
            beginTransaction();
            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                TableEntity<?> table = TableEntityCache.get(this, entities.get(0).getClass());
                if (!table.tableIsExist()) {
                    return;
                }
                for (Object item : entities) {
                    execNonQuery(table.buildUpdateSqlInfo(item, whereBuilder, updateColumnNames));
                }
            } else {
                TableEntity<?> table = TableEntityCache.get(this, entity.getClass());
                if (!table.tableIsExist()) {
                    return;
                }
                execNonQuery(table.buildUpdateSqlInfo(entity, whereBuilder, updateColumnNames));
            }
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T findById(Class<T> entityType, Object idValue) throws DbException {
        TableEntity<T> table = TableEntityCache.get(this, entityType);
        if (!table.tableIsExist()) {
            return null;
        }
        Selector selector = Selector.from(table).where(table.getId().getColumnName(), "=", idValue);
        String sql = selector.limit(1).toString();
        Cursor cursor = execQuery(sql);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    return CursorUtils.getEntity(table, cursor);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    @Override
    public final <T> T findFirst(Class<T> entityType) throws DbException {
        return this.selector(entityType).findFirst();
    }

    @Override
    public final <T> List<T> findAll(Class<T> entityType) throws DbException {
        return this.selector(entityType).findAll();
    }

    @Override
    public final <T> Selector<T> selector(Class<T> entityType) throws DbException {
        return Selector.from(TableEntityCache.get(this, entityType));
    }

    @Override
    public final DbModel findDbModelFirst(SqlInfo sqlInfo) throws DbException {
        Cursor cursor = execQuery(sqlInfo);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    return CursorUtils.getDbModel(cursor);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    @Override
    public final List<DbModel> findDbModelAll(SqlInfo sqlInfo) throws DbException {
        List<DbModel> dbModelList = new LinkedList<DbModel>();
        Cursor cursor = execQuery(sqlInfo);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    dbModelList.add(CursorUtils.getDbModel(cursor));
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                cursor.close();
            }
        }
        return dbModelList;
    }

    @Override
    public final void dropTable(Class<?> entityType) throws DbException {
        TableEntity<?> table = TableEntityCache.get(this, entityType);
        if (!table.tableIsExist()) {
            return;
        }
        execNonQuery("DROP TABLE \"" + table.getTableName() + "\"");
        TableEntityCache.remove(this, entityType);
    }

    @Override
    public final void addColumn(Class<?> entityType, String column) throws DbException {
        try {
            beginTransaction();
            TableEntityCache.remove(this, entityType);
            TableEntity<?> table = TableEntityCache.get(this, entityType);
            ColumnEntity col = table.getColumnMap().get(column);
            if (col != null) {
                StringBuilder builder = new StringBuilder();
                builder.append("ALTER TABLE ").append("\"").append(table.getTableName()).append("\"")
                        .append(" ADD COLUMN ").append("\"").append(col.getColumnName()).append("\"").append(" ")
                        .append(col.getColumnDbType()).append(" ").append(col.getProperty());
                execNonQuery(builder.toString());
            }
            table.setCheckedDatabase(true);
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @Override
    public final void dropDb() throws DbException {
        Cursor cursor = execQuery("SELECT name FROM sqlite_master WHERE type='table' AND name<>'sqlite_sequence'");
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    try {
                        String tableName = cursor.getString(0);
                        execNonQuery("DROP TABLE " + tableName);
                        TableEntityCache.remove(this, tableName);
                    } catch (Throwable e) {
                        Log.e(e.getMessage(), e);
                    }
                }

            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                cursor.close();
            }
        }
    }

    @Override
    public void close() throws IOException {
        DbManagerFactory.remove(mConfig);
        mSQLDatabase.close();
    }

    @Override
    public void execNonQuery(SqlInfo sqlInfo) throws DbException {
        // 子类实现
    }

    @Override
    public final void execNonQuery(String sql) throws DbException {
        try {
            mSQLDatabase.execSQL(sql);
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }

    @Override
    public final Cursor execQuery(SqlInfo sqlInfo) throws DbException {
        try {
            return mSQLDatabase.rawQuery(sqlInfo.getSql(), sqlInfo.getBindArgsAsStrArray());
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }

    @Override
    public final Cursor execQuery(String sql) throws DbException {
        try {
            return mSQLDatabase.rawQuery(sql, null);
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }

    @Override
    public String getDbName() {
        return mConfig.getDbName();
    }

    @Override
    public boolean existTable(TableEntity<?> entity) throws DbException {
        String sql = getCheckTableSql(entity);
        Cursor cursor = execQuery(sql);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    int columnIndex = cursor.getColumnIndex("c");
                    int count = cursor.getInt(columnIndex);
                    if (count > 0) {
                        return true;
                    }
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                cursor.close();
            }
        }
        return false;
    }

    /**
     * 获取检测表存在的sql查询语句
     *
     * @param entity
     * @return
     */
    protected abstract String getCheckTableSql(TableEntity<?> entity);

    private void saveOrUpdateWithoutTransaction(TableEntity<?> table, Object entity) throws DbException {
        ColumnEntity id = table.getId();
        if (id.isAutoId()) {
            if (id.getColumnValue(entity) != null) {
                execNonQuery(table.buildUpdateSqlInfo(entity));
            } else {
                saveBindingIdWithoutTransaction(table, entity);
            }
        } else {
            execNonQuery(table.buildReplaceSqlInfo(entity));
        }
    }

    private boolean saveBindingIdWithoutTransaction(TableEntity<?> table, Object entity) throws DbException {
        ColumnEntity id = table.getId();
        if (id.isAutoId()) {
            execNonQuery(buildInsertSqlInfo(table, entity));
            long idValue = getLastAutoIncrementId(table.getTableName());
            if (idValue == -1) {
                return false;
            }
            id.setAutoIdValue(entity, idValue);
            return true;
        } else {
            execNonQuery(buildInsertSqlInfo(table, entity));
            return true;
        }
    }

    private long getLastAutoIncrementId(String tableName) throws DbException {
        long id = -1;
        Cursor cursor = execQuery("SELECT seq FROM sqlite_sequence WHERE name='" + tableName + "' LIMIT 1");
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    id = cursor.getLong(0);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                cursor.close();
            }
        }
        return id;
    }

    protected void beginTransaction() {
        if (mConfig.isAllowTransaction()) {
            mSQLDatabase.beginTransaction();
        }
    }

    protected void setTransactionSuccessful() {
        if (mConfig.isAllowTransaction()) {
            mSQLDatabase.setTransactionSuccessful();
        }
    }

    protected void endTransaction() {
        if (mConfig.isAllowTransaction()) {
            mSQLDatabase.endTransaction();
        }
    }

    /**
     * insert sql
     *
     * @param entity
     * @return
     * @throws DbException
     */
    protected SqlInfo buildInsertSqlInfo(TableEntity<?> table, Object entity) {
        List<KeyValue> keyValueList = table.entityToKeyValueList(entity);
        if (keyValueList.size() == 0) {
            return null;
        }
        SqlInfo result = new SqlInfo();
        String sql = TableEntityCache.INSERT_SQL_CACHE.get(table);
        if (sql == null) {
            sql = buildInsertSql(table, entity, keyValueList);
            TableEntityCache.INSERT_SQL_CACHE.put(table, sql);
        }
        result.setSql(sql);
        result.addBindArgs(keyValueList);
        return result;
    }

    protected String buildInsertSql(TableEntity<?> table, Object entity, List<KeyValue> keyValueList) {
        return null;
    }

    protected SqlInfo buildDeleteSqlInfoById(TableEntity<?> table, Object idValue) throws DbException {
        SqlInfo result = new SqlInfo();
        ColumnEntity id = table.getId();
        if (idValue == null) {
            throw new DbException("this entity[" + table.getEntityType() + "]'s id value is null");
        }
        String sql = buildDeleteSqlById(table, id, idValue);
        result.setSql(sql);
        return result;
    }

    protected abstract String buildDeleteSqlById(TableEntity<?> table, ColumnEntity id, Object idValue);


}