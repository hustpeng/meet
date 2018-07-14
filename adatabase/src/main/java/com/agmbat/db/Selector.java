package com.agmbat.db;

import android.database.Cursor;

import com.agmbat.db.sql.OrderBy;
import com.agmbat.db.sql.SQLQueryBuilder;
import com.agmbat.db.sql.WhereBuilder;
import com.agmbat.db.table.DbModel;
import com.agmbat.db.table.TableEntity;
import com.agmbat.db.utils.CursorUtils;

import java.util.LinkedList;
import java.util.List;

public final class Selector<T> {

    private final TableEntity<T> table;

    private WhereBuilder whereBuilder;
    private List<OrderBy> orderByList;
    private int limit = 0;
    private int offset = 0;

    /* package */
    public static <T> Selector<T> from(TableEntity<T> table) {
        return new Selector<T>(table);
    }

    private Selector(TableEntity<T> table) {
        this.table = table;
    }

    public Selector<T> where(WhereBuilder whereBuilder) {
        this.whereBuilder = whereBuilder;
        return this;
    }

    public Selector<T> where(String columnName, String op, Object value) {
        this.whereBuilder = WhereBuilder.b(columnName, op, value);
        return this;
    }

    public Selector<T> and(String columnName, String op, Object value) {
        this.whereBuilder.and(columnName, op, value);
        return this;
    }

    public Selector<T> and(WhereBuilder where) {
        this.whereBuilder.and(where);
        return this;
    }

    public Selector<T> or(String columnName, String op, Object value) {
        this.whereBuilder.or(columnName, op, value);
        return this;
    }

    public Selector<T> or(WhereBuilder where) {
        this.whereBuilder.or(where);
        return this;
    }

    public Selector<T> expr(String expr) {
        if (this.whereBuilder == null) {
            this.whereBuilder = WhereBuilder.b();
        }
        this.whereBuilder.expr(expr);
        return this;
    }

    public DbModelSelector groupBy(String columnName) {
        return new DbModelSelector(this, columnName);
    }

    public DbModelSelector select(String...columnExpressions) {
        return new DbModelSelector(this, columnExpressions);
    }

    public Selector<T> orderBy(String columnName) {
        if (orderByList == null) {
            orderByList = new LinkedList<OrderBy>();
        }
        orderByList.add(new OrderBy(columnName));
        return this;
    }

    public Selector<T> orderBy(String columnName, boolean desc) {
        if (orderByList == null) {
            orderByList = new LinkedList<OrderBy>();
        }
        orderByList.add(new OrderBy(columnName, desc));
        return this;
    }

    public Selector<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public Selector<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    public TableEntity<T> getTable() {
        return table;
    }

    public WhereBuilder getWhereBuilder() {
        return whereBuilder;
    }

    public List<OrderBy> getOrderByList() {
        return orderByList;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public T findFirst() throws DbException {
        if (!table.tableIsExist()) {
            return null;
        }
        this.limit(1);
        Cursor cursor = table.getDb().execQuery(buildQuerySql());
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

    public List<T> findAll() throws DbException {
        if (!table.tableIsExist()) {
            return null;
        }
        List<T> result = null;
        Cursor cursor = table.getDb().execQuery(buildQuerySql());
        if (cursor != null) {
            try {
                result = new LinkedList<T>();
                while (cursor.moveToNext()) {
                    T entity = CursorUtils.getEntity(table, cursor);
                    result.add(entity);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    public long count() throws DbException {
        if (!table.tableIsExist()) {
            return 0;
        }
        DbModelSelector dmSelector = select("count(" + table.getId().getColumnName() + ") as count");
        DbModel firstModel = dmSelector.findFirst();
        if (firstModel != null) {
            return firstModel.getLong("count");
        }
        return 0;
    }

    /**
     * Mysql 中 sql语句表名不需要用引号
     * 
     * @return
     */
    public String buildQuerySql() {
        String tables = table.getTableName();
        String where = null;
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            where = whereBuilder.build();
        }
        String orderBy = getOrderBy();
        String limitText = getLimitText();
        return SQLQueryBuilder.buildQueryString(tables, null, where, null, null, orderBy, limitText);
    }

    /**
     * 获取limit文本 LIMIT 0,10
     * 
     * @return
     */
    private String getLimitText() {
        if (limit > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(offset).append(",").append(limit);
            return builder.toString();
        }
        return null;
    }

    private String getLimitOffset() {
        if (limit > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(" LIMIT ").append(limit);
            builder.append(" OFFSET ").append(offset);
            return builder.toString();
        }
        return null;
    }

    /**
     * 获取orderBy字符串
     * 
     * @return
     */
    private String getOrderBy() {
        String orderBy = null;
        if (orderByList != null && orderByList.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (OrderBy order : orderByList) {
                builder.append(order.build()).append(',');
            }
            builder.deleteCharAt(builder.length() - 1);
            orderBy = builder.toString();
        }
        return orderBy;
    }
}
