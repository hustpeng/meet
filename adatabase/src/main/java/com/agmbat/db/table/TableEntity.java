package com.agmbat.db.table;

import com.agmbat.db.DbException;
import com.agmbat.db.DbManager;
import com.agmbat.db.annotation.Column;
import com.agmbat.db.annotation.Table;
import com.agmbat.db.converter.ColumnConverterFactory;
import com.agmbat.db.sql.KeyValue;
import com.agmbat.db.sql.SqlInfo;
import com.agmbat.db.sql.WhereBuilder;
import com.agmbat.log.Log;
import com.agmbat.text.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述表的结构, 必需的有表名，字段(数据名称,数据类型,数据相关参数)
 *
 * @param <T>
 */
public final class TableEntity<T> {

    public static final ConcurrentHashMap<TableEntity<?>, String> REPLACE_SQL_CACHE =
            new ConcurrentHashMap<TableEntity<?>, String>();
    private final DbManager db;
    /**
     * 表名
     */
    private final String tableName;
    /**
     * key: columnName
     */
    private final LinkedHashMap<String, ColumnEntity> columnMap;
    private final String onCreated;
    private ColumnEntity id;
    private Class<T> entityType;
    private Constructor<T> constructor;
    private boolean checkedDatabase;

    TableEntity(DbManager db, Class<T> entityType) throws Throwable {
        this.db = db;
        this.entityType = entityType;
        this.constructor = entityType.getConstructor();
        this.constructor.setAccessible(true);
        Table table = entityType.getAnnotation(Table.class);
        this.tableName = table.name();
        this.onCreated = table.onCreated();
        this.columnMap = findColumnMap(entityType);
        for (ColumnEntity column : columnMap.values()) {
            if (column.isId()) {
                this.id = column;
                break;
            }
        }
    }

    /**
     * 从Class中找到所有表中的字段
     *
     * @param entityType 表示entity的Class
     * @return
     */
    static synchronized LinkedHashMap<String, ColumnEntity> findColumnMap(Class<?> entityType) {
        LinkedHashMap<String, ColumnEntity> columnMap = new LinkedHashMap<String, ColumnEntity>();
        addColumnsToMap(entityType, columnMap);
        return columnMap;
    }

    private static void addColumnsToMap(Class<?> entityType, HashMap<String, ColumnEntity> columnMap) {
        if (Object.class.equals(entityType)) {
            return;
        }
        try {
            Field[] fields = entityType.getDeclaredFields();
            for (Field field : fields) {
                int modify = field.getModifiers();
                if (Modifier.isStatic(modify) || Modifier.isTransient(modify)) {
                    continue;
                }
                Column columnAnn = field.getAnnotation(Column.class);
                if (columnAnn != null) {
                    if (ColumnConverterFactory.isSupportColumnConverter(field.getType())) {
                        ColumnEntity column = new ColumnEntity(entityType, field, columnAnn);
                        if (!columnMap.containsKey(column.getColumnName())) {
                            columnMap.put(column.getColumnName(), column);
                        }
                    }
                }
            }
            addColumnsToMap(entityType.getSuperclass(), columnMap);
        } catch (Throwable e) {
            Log.e(e.getMessage(), e);
        }
    }

    public T createEntity() throws Throwable {
        return this.constructor.newInstance();
    }

    /**
     * 判断表是否存在
     *
     * @return
     * @throws DbException
     */
    public boolean tableIsExist() throws DbException {
        if (isCheckedDatabase()) {
            return true;
        }
        boolean exist = db.existTable(this);
        setCheckedDatabase(exist);
        return exist;
    }

    public DbManager getDb() {
        return db;
    }

    public String getTableName() {
        return tableName;
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public String getOnCreated() {
        return onCreated;
    }

    public ColumnEntity getId() {
        return id;
    }

    public LinkedHashMap<String, ColumnEntity> getColumnMap() {
        return columnMap;
    }

    public boolean isCheckedDatabase() {
        return checkedDatabase;
    }

    public void setCheckedDatabase(boolean checkedDatabase) {
        this.checkedDatabase = checkedDatabase;
    }

    @Override
    public String toString() {
        return tableName;
    }

    /**
     * 创建表，当表不存在时
     *
     * @throws DbException
     */
    public void createTableIfNotExist() throws DbException {
        if (!tableIsExist()) {
            String sql = buildCreateTableSql();
            db.execNonQuery(sql);
            String execAfterTableCreated = getOnCreated();
            if (!StringUtils.isEmpty(execAfterTableCreated)) {
                db.execNonQuery(execAfterTableCreated);
            }
            setCheckedDatabase(true);
        }
    }

    /**
     * Build "insert", "replace",，"update", "delete" and "create" sql.
     */

    /**
     * 构建创建表的sql语句
     *
     * @return
     * @throws DbException
     */
    private String buildCreateTableSql() throws DbException {
        return db.buildCreateTableSql(this);
    }

    /**
     * 将entity转为List<KeyValue>
     *
     * @param entity
     * @return
     */
    public List<KeyValue> entityToKeyValueList(Object entity) {
        List<KeyValue> keyValueList = new LinkedList<KeyValue>();
        Collection<ColumnEntity> columns = getColumnMap().values();
        for (ColumnEntity column : columns) {
            KeyValue kv = column.columnToKeyValue(entity);
            if (kv != null) {
                keyValueList.add(kv);
            }
        }
        return keyValueList;
    }

    /**
     * replace sql
     *
     * @param entity
     * @return
     * @throws DbException
     */
    public SqlInfo buildReplaceSqlInfo(Object entity) throws DbException {
        List<KeyValue> keyValueList = entityToKeyValueList(entity);
        if (keyValueList.size() == 0) {
            return null;
        }
        SqlInfo result = new SqlInfo();
        String sql = REPLACE_SQL_CACHE.get(this);
        if (sql == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("REPLACE INTO ");
            builder.append("\"").append(getTableName()).append("\"");
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
            sql = builder.toString();
            result.setSql(sql);
            result.addBindArgs(keyValueList);
            REPLACE_SQL_CACHE.put(this, sql);
        } else {
            result.setSql(sql);
            result.addBindArgs(keyValueList);
        }
        return result;
    }

    /**
     * delete sql
     *
     * @return
     * @throws DbException
     */
    public SqlInfo buildDeleteSqlInfo(Object entity) throws DbException {
        SqlInfo result = new SqlInfo();
        ColumnEntity id = getId();
        Object idValue = id.getColumnValue(entity);
        if (idValue == null) {
            throw new DbException("this entity[" + getEntityType() + "]'s id value is null");
        }
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        builder.append("\"").append(getTableName()).append("\"");
        builder.append(" WHERE ").append(WhereBuilder.b(id.getColumnName(), "=", idValue));
        result.setSql(builder.toString());
        return result;
    }

    public SqlInfo buildDeleteSqlInfo(WhereBuilder whereBuilder) throws DbException {
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        builder.append("\"").append(getTableName()).append("\"");
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            builder.append(" WHERE ").append(whereBuilder.toString());
        }
        return new SqlInfo(builder.toString());
    }

    /**
     * update sql
     *
     * @param table
     * @param entity
     * @param updateColumnNames
     * @return
     * @throws DbException
     */
    public SqlInfo buildUpdateSqlInfo(Object entity, String... updateColumnNames) throws DbException {
        List<KeyValue> keyValueList = entityToKeyValueList(entity);
        if (keyValueList.size() == 0) {
            return null;
        }
        HashSet<String> updateColumnNameSet = null;
        if (updateColumnNames != null && updateColumnNames.length > 0) {
            updateColumnNameSet = new HashSet<String>(updateColumnNames.length);
            Collections.addAll(updateColumnNameSet, updateColumnNames);
        }
        ColumnEntity id = getId();
        Object idValue = id.getColumnValue(entity);
        if (idValue == null) {
            throw new DbException("this entity[" + getEntityType() + "]'s id value is null");
        }
        SqlInfo result = new SqlInfo();
        StringBuilder builder = new StringBuilder("UPDATE ");
        builder.append("\"").append(getTableName()).append("\"");
        builder.append(" SET ");
        for (KeyValue kv : keyValueList) {
            if (updateColumnNameSet == null || updateColumnNameSet.contains(kv.key)) {
                builder.append("\"").append(kv.key).append("\"").append("=?,");
                result.addBindArg(kv);
            }
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(" WHERE ").append(WhereBuilder.b(id.getColumnName(), "=", idValue));
        result.setSql(builder.toString());
        return result;
    }

    public SqlInfo buildUpdateSqlInfo(Object entity, WhereBuilder whereBuilder, String... updateColumnNames)
            throws DbException {
        List<KeyValue> keyValueList = entityToKeyValueList(entity);
        if (keyValueList.size() == 0) {
            return null;
        }
        HashSet<String> updateColumnNameSet = null;
        if (updateColumnNames != null && updateColumnNames.length > 0) {
            updateColumnNameSet = new HashSet<String>(updateColumnNames.length);
            Collections.addAll(updateColumnNameSet, updateColumnNames);
        }
        SqlInfo result = new SqlInfo();
        StringBuilder builder = new StringBuilder("UPDATE ");
        builder.append("\"").append(getTableName()).append("\"");
        builder.append(" SET ");
        for (KeyValue kv : keyValueList) {
            if (updateColumnNameSet == null || updateColumnNameSet.contains(kv.key)) {
                builder.append("\"").append(kv.key).append("\"").append("=?,");
                result.addBindArg(kv);
            }
        }
        builder.deleteCharAt(builder.length() - 1);
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            builder.append(" WHERE ").append(whereBuilder.toString());
        }
        result.setSql(builder.toString());
        return result;
    }
}
