package com.agmbat.db.sql;

import android.database.sqlite.SQLiteDatabase;

import com.agmbat.text.StringUtils;

import java.util.regex.Pattern;

/**
 * This is a convience class that helps build SQL queries to be sent to {@link SQLiteDatabase} objects.
 */
public class SQLQueryBuilder {

    private static final Pattern sLimitPattern = Pattern.compile("\\s*\\d+\\s*(,\\s*\\d+\\s*)?");

    public static String buildQueryString(String tables, String[] columns, String where, String groupBy, String having,
            String orderBy, String limit) {
        return buildQueryString(false, tables, columns, where, groupBy, having, orderBy, limit);
    }

    /**
     * Build an SQL query string from the given clauses.
     * 
     * @param distinct true if you want each row to be unique, false otherwise.
     * @param tables The table names to compile the query against.
     * @param columns A list of which columns to return. Passing null will return all columns, which is discouraged to
     *            prevent reading data from storage that isn't going to be used.
     * @param where A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE
     *            itself). Passing null will return all rows for the given URL.
     * @param groupBy A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY
     *            itself). Passing null will cause the rows to not be grouped.
     * @param having A filter declare which row groups to include in the cursor, if row grouping is being used,
     *            formatted as an SQL HAVING clause (excluding the HAVING itself). Passing null will cause all row
     *            groups to be included, and is required when row grouping is not being used.
     * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself).
     *            Passing null will use the default sort order, which may be unordered.
     * @param limit Limits the number of rows returned by the query, formatted as LIMIT clause. Passing null denotes no
     *            LIMIT clause.
     * @return the SQL query string
     */
    public static String buildQueryString(boolean distinct, String tables, String[] columns, String where,
            String groupBy, String having, String orderBy, String limit) {
        if (StringUtils.isEmpty(groupBy) && !StringUtils.isEmpty(having)) {
            throw new IllegalArgumentException("HAVING clauses are only permitted when using a groupBy clause");
        }
        if (!StringUtils.isEmpty(limit) && !sLimitPattern.matcher(limit).matches()) {
            throw new IllegalArgumentException("invalid LIMIT clauses:" + limit);
        }

        StringBuilder query = new StringBuilder(120);

        query.append("SELECT ");
        if (distinct) {
            query.append("DISTINCT ");
        }
        if (columns != null && columns.length != 0) {
            appendColumns(query, columns);
        } else {
            query.append("* ");
        }
        query.append("FROM ");
        query.append(tables);
        appendClause(query, " WHERE ", where);
        appendClause(query, " GROUP BY ", groupBy);
        appendClause(query, " HAVING ", having);
        appendClause(query, " ORDER BY ", orderBy);
        appendClause(query, " LIMIT ", limit);
        return query.toString();
    }

    private static void appendClause(StringBuilder s, String name, String clause) {
        if (!StringUtils.isEmpty(clause)) {
            s.append(name);
            s.append(clause);
        }
    }

    /**
     * Add the names that are non-null in columns to s, separating them with commas.
     */
    public static void appendColumns(StringBuilder s, String[] columns) {
        int n = columns.length;
        for (int i = 0; i < n; i++) {
            String column = columns[i];
            if (column != null) {
                if (i > 0) {
                    s.append(", ");
                }
                s.append(column);
            }
        }
        s.append(' ');
    }

}
