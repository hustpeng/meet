package org.jivesoftware.smackx.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheStoreBase<T extends ICacheStoreObject> {

    private final Map<String, T> objects;

    public CacheStoreBase() {
        objects = new ConcurrentHashMap<String, T>();
    }

    public boolean isMemoryCached() {
        return true;
    }

    public int bulkInsert(List<T> ts) {
        if (ts == null) return 0;

        for (T obj : ts) {
            objects.put(obj.getKey(), obj);
        }
        return ts.size();
    }

    public boolean delete(T t) {
        if (t != null && objects.remove(t.getKey()) != null) {
            return true;
        }
        return false;
    }

    public void insertOrUpdate(T t) {
        if (t == null) {
            return;
        }

        objects.put(t.getKey(), t);
    }

    public void cleanAllEntryForOwner() {
        objects.clear();
    }

    public int getEntriesCount() {
        return objects.size();
    }

    public ArrayList<T> getAllEntires() {
        ArrayList<T> objs = new ArrayList<T>();
        Object[] keys = objects.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            objs.add(objects.get(keys[i]));
        }
        return objs;
    }

    public boolean contain(String bareJid) {
        if (bareJid != null) {
            return objects.containsKey(bareJid);
        }
        return false;
    }

    public T getEntryWithKey(String key) {
        if (key == null) {
            return null;
        }
        return objects.get(key);
    }
}
