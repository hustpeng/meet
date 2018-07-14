package com.agmbat.io;

import com.agmbat.file.FileUtils;
import com.agmbat.text.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.ServerSocket;

/**
 * IO操作工具类
 */
public class IoUtils {

    /**
     * 流结束标志
     */
    private static final int EOF = -1;

    /**
     * buffer 大小 256K
     */
    private static final int BUFFER_SIZE = 256 * 1024;

    /**
     * utf-8编码
     */
    public static final String UTF_8 = "utf-8";

    /**
     * UTF_BOM,utf-8文件首字符
     */
    private static final String UTF8_BOM = "\uFEFF";

    /**
     * Copy the content of the input stream into the output stream, using a temporary byte array buffer whose size is
     * defined by {@link #BUFFER_SIZE}.
     *
     * @param in  The input stream to copy from.
     * @param out The output stream to copy to.
     * @throws IOException If any error occurs during the copy.
     */
    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        copyStream(in, out, 0, (Operation) null);
    }

    /**
     * 将InputStream转存到文件中
     *
     * @param in      输入流
     * @param outFile 输出文件
     * @throws IOException If any error occurs during the copy.
     */
    public static void copyStream(InputStream in, File outFile) throws IOException {
        copyStream(in, outFile, 0, null);
    }

    /**
     * 将InputStream转存到文件中
     *
     * @param in
     * @param outFile
     * @param total
     * @param l
     * @throws IOException
     */
    public static void copyStream(InputStream in, File outFile, long total, OnProgressListener l) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = FileUtils.openFileOutputStream(outFile);
            copyStream(in, fos, total, l);
        } finally {
            closeQuietly(fos);
        }
    }

    /**
     * 复制流程并回调进度
     *
     * @param in
     * @param out
     * @param total
     * @param l
     * @throws IOException
     */
    public static void copyStream(InputStream in, OutputStream out, long total, final OnProgressListener l)
            throws IOException {
        Operation operation = null;
        if (l != null) {
            operation = new Operation() {

                @Override
                public boolean isCancelled() {
                    return false;
                }

                @Override
                public void onProgress(long current, long total) {
                    l.onProgress(current, total);
                }
            };
        }
        copyStream(in, out, total, operation);
    }

    /**
     * 复制流程，支持取消操作
     *
     * @param in
     * @param out
     * @param cancelable
     * @throws IOException
     */
    public static void copyStream(InputStream in, OutputStream out, final Cancelable cancelable) throws IOException {
        Operation operation = null;
        if (cancelable != null) {
            operation = new Operation() {

                @Override
                public boolean isCancelled() {
                    return cancelable.isCancelled();
                }

                @Override
                public void onProgress(long total, long current) {
                }
            };
        }
        copyStream(in, out, 0, operation);
    }

    /**
     * 复制流
     *
     * @param in
     * @param out
     * @param total
     * @param operation
     * @throws IOException
     */
    public static void copyStream(InputStream in, OutputStream out, long total, Operation operation) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        long current = 0;
        int len = 0;
        while ((len = in.read(buffer)) != EOF) {
            out.write(buffer, 0, len);
            current += len;
            if (operation != null) {
                operation.onProgress(total, current);

                if (operation.isCancelled()) {
                    // 如果已经取消,则停止复制流程
                    return;
                }
            }
        }
    }


    /**
     * Convert an {@link InputStream} to String.
     *
     * @param stream the stream that contains data.
     * @return the result string.
     * @throws IOException an I/O error occurred.
     */
    public static String loadContent(InputStream stream) throws IOException {
        return loadContent(stream, null);
    }

    /**
     * Convert an {@link InputStream} to String.
     *
     * @param stream      the stream that contains data.
     * @param charsetName the encoding of the data.
     * @return the result string.
     * @throws IOException an I/O error occurred.
     */
    public static String loadContent(InputStream stream, String charsetName) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("stream may not be null.");
        }
        String encoding = charsetName;
        if (StringUtils.isEmpty(encoding)) {
            encoding = System.getProperty("file.encoding", UTF_8);
        }
        final InputStreamReader reader = new InputStreamReader(stream, encoding);
        final StringWriter writer = new StringWriter();
        final char[] buffer = new char[BUFFER_SIZE];
        int len = reader.read(buffer);
        while (len > 0) {
            writer.write(buffer, 0, len);
            len = reader.read(buffer);
        }
        String text = writer.toString();
        if (UTF_8.equalsIgnoreCase(encoding) && text.startsWith(UTF8_BOM)) {
            text = text.substring(1);
        }
        return text;
    }


    /**
     * 将Stream转为byte数组
     *
     * @param in the stream that contains data.
     * @return
     */
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

    /**
     * Closes the specified closeable.
     *
     * @param closeable The closeable to close.
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 关闭socket, 此方法定义在这的原因是低版本或其他平台的java中,ServerSocket未实现Closeable
     *
     * @param socket
     */
    public static void closeQuietly(ServerSocket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭closeable
     *
     * @param closeable
     */
    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
