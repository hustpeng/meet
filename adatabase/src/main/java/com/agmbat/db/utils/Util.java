package com.agmbat.db.utils;

public class Util {

    public static void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String toString(Object object) {
        if (object == null) {
            return null;
        }
        return object.toString();
    }

}
