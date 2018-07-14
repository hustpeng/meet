package com.agmbat.sqlite;

import android.database.sqlite.SQLiteDatabase;

import com.agmbat.db.BaseManager;
import com.agmbat.db.DbConfig;
import com.agmbat.db.DbException;
import com.agmbat.db.sql.KeyValue;
import com.agmbat.db.sql.SqlInfo;
import com.agmbat.db.sql.WhereBuilder;
import com.agmbat.db.table.ColumnEntity;
import com.agmbat.db.table.TableEntity;
import com.agmbat.sql.DataType;
import com.agmbat.sql.Param;
import com.agmbat.sql.TableSqlBuilder;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class SqliteDbManager extends BaseManager {

    public SqliteDbManager(SqliteDbConfig config) {
        super(config);
    }

    @Override
    protected SQLiteDatabase createDatabase(DbConfig c) {
        SqliteDbConfig config = (SqliteDbConfig) c;
        SQLiteDatabase db = null;
        File dbDir = config.getDbDir();
        if (dbDir != null && (dbDir.exists() || dbDir.mkdirs())) {
            File dbFile = new File(dbDir, config.getDbName());
            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        } else {
            db = SQLiteDatabase.openOrCreateDatabase(config.getDbName(), null);
        }
        return db;
    }

    @Override
    public void execNonQuery(SqlInfo sqlInfo) throws DbException {
        try {
            if (sqlInfo.getBindArgs() != null) {
                mSQLDatabase.execSQL(sqlInfo.getSql(), sqlInfo.getBindArgsAsStrArray());
            } else {
                mSQLDatabase.execSQL(sqlInfo.getSql());
            }
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }

    @Override
    public <T> String buildCreateTableSql(TableEntity<T> tTableEntity) {
        ColumnEntity id = tTableEntity.getId();
        TableSqlBuilder builder = new TableSqlBuilder(tTableEntity.getTableName());
        if (id.isAutoId()) {
            builder.addColumn(id.getColumnName(), DataType.INTEGER, Param.PRIMARY_KEY, Param.AUTOINCREMENT);
        } else {
            builder.addColumn(id.getColumnName(), String.valueOf(id.getColumnDbType()), Param.PRIMARY_KEY);
        }
        Collection<ColumnEntity> columns = tTableEntity.getColumnMap().values();
        for (ColumnEntity column : columns) {
            if (column.isId()) {
                continue;
            }
            builder.addColumn(column.getColumnName(), String.valueOf(column.getColumnDbType()),
                    String.valueOf(column.getProperty()));
        }
        return builder.buildSql();
    }

    @Override
    protected String buildInsertSql(TableEntity<?> table, Object entity, List<KeyValue> keyValueList) {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ");
        builder.append("\"").append(table.getTableName()).append("\"");
        builder.append(" (");
        for (KeyValue kv : keyValueList) {
            builder.append("\"").append(kv.key).append("\"").append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(") VALUES (");
        int length = keyValueList.size();
        for (int i = 0; i < length; i++) {
            builder.append("?,");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(")");
        return builder.toString();
    }

    @Override
    protected String buildDeleteSqlById(TableEntity<?> table, ColumnEntity id, Object idValue) {
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        builder.append("\"").append(table.getTableName()).append("\"");
        builder.append(" WHERE ").append(WhereBuilder.b(id.getColumnName(), "=", idValue).build());
        String sql = builder.toString();
        return sql;
    }

    @Override
    protected String getCheckTableSql(TableEntity<?> entity) {
        String sql =
                "SELECT COUNT(*) AS c FROM sqlite_master WHERE type='table' AND name='" + entity.getTableName() + "'";
        return sql;
    }

    @Override
    public String getIdKey() {
        return mSQLDatabase.getPath();
    }
}
