package com.agmbat.db.sql;

import java.util.LinkedList;
import java.util.List;

public final class SqlInfo {

    public String sql;
    public List<KeyValue> bindArgs;

    public SqlInfo() {
    }

    public SqlInfo(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void addBindArg(KeyValue kv) {
        if (bindArgs == null) {
            bindArgs = new LinkedList<KeyValue>();
        }
        bindArgs.add(kv);
    }

    public void addBindArgs(List<KeyValue> bindArgs) {
        if (this.bindArgs == null) {
            this.bindArgs = bindArgs;
        } else {
            this.bindArgs.addAll(bindArgs);
        }
    }

    public Object[] getBindArgs() {
        Object[] result = null;
        if (bindArgs != null) {
            result = new Object[bindArgs.size()];
            for (int i = 0; i < bindArgs.size(); i++) {
                result[i] = bindArgs.get(i).value;
            }
        }
        return result;
    }

    public String[] getBindArgsAsStrArray() {
        String[] result = null;
        if (bindArgs != null) {
            result = new String[bindArgs.size()];
            for (int i = 0; i < bindArgs.size(); i++) {
                Object value = bindArgs.get(i).value;
                result[i] = value == null ? null : value.toString();
            }
        }
        return result;
    }
}
