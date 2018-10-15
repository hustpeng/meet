package com.agmbat.meetyou.util;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;

/**
 * Created by MHL on 2016/6/29.
 */
public class IoUtils {

    private static final int EOF = -1;
    private static final int BUFFER_SIZE = 1024;

    private IoUtils() {
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {

            }
        }
    }

    public static void close(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {

            }
        }
    }

    /**
     * Copy the content of the input stream into the output stream, using a
     * temporary byte array buffer whose size is defined by
     * {@link #IO_BUFFER_SIZE}.
     *
     * @param in  The input stream to copy from.
     * @param out The output stream to copy to.
     * @throws IOException If any error occurs during the copy.
     */
    public static void copyStream(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = in.read(buffer)) != EOF) {
            out.write(buffer, 0, read);
        }
    }

    /**
     * Closes 'closeable', ignoring any checked exceptions. Does nothing if
     * 'closeable' is null.
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeCursor(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String loadContent(final InputStream stream)
            throws IOException {
        return loadContent(stream, null);
    }

    /**
     * Convert an {@link InputStream} to String.
     *
     * @param stream   the stream that contains data.
     * @param encoding the encoding of the data.
     * @return the result string.
     * @throws IOException an I/O error occurred.
     */
    public static String loadContent(final InputStream stream, String encoding)
            throws IOException {
        if (null == stream) {
            throw new IllegalArgumentException("stream may not be null.");
        }
        if (TextUtils.isEmpty(encoding)) {
            encoding = System.getProperty("file.encoding", "utf-8");
        }
        final InputStreamReader reader = new InputStreamReader(stream, encoding);
        final StringWriter writer = new StringWriter();
        final char[] buffer = new char[4 * BUFFER_SIZE];
        int charRead = reader.read(buffer);
        while (charRead > 0) {
            writer.write(buffer, 0, charRead);
            charRead = reader.read(buffer);
        }
        return writer.toString();
    }

    public static String loadFromAssets(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }
        try {
            return loadContent(context.getAssets().open(fileName), "utf-8");
        } catch (Exception e) {
            return "";
        }
    }

    public static String loadContent(File file) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            return loadContent(stream, "utf-8");
        } catch (Exception e) {
        } catch (OutOfMemoryError ee) {
        } finally {
            closeQuietly(stream);
        }
        return null;
    }

    public static byte[] loadBytes(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data = null;
        try {
            copyStream(in, out);
            data = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(out);
        }
        return data;
    }
}
