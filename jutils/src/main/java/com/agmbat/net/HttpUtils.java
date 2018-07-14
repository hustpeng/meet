/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jhttp
 *
 * @author mayimchen
 * @since 2016-10-16
 */
package com.agmbat.net;

import com.agmbat.file.FileUtils;
import com.agmbat.io.IoUtils;
import com.agmbat.io.Operation;
import com.agmbat.text.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Http工具类
 */
public class HttpUtils {

    private static final String TAG = HttpUtils.class.getSimpleName();

    /**
     * 超时时长
     */
    private static final int TIMEOUT = 15000;

    /**
     * Retrieve the content of the URL as string.
     *
     * @param url The URL to retrieve.
     * @return The content of the given URL, or null if error occurred.
     */
    public static String getUrlAsString(String url) {
        return getUrlAsString(url, null);
    }

    public static String getUrlAsString(String url, Map<String, String> header) {
        return new HttpRequester.Builder().url(url).addHeader(header).build().requestAsString();
    }


    /**
     * Download specified url directly and save it to local path.
     *
     * @param url        the url of the file.
     * @param saveToFile the file to save to.
     * @return true if success, false otherise.
     */
    public static boolean downloadFile(String url, File saveToFile) {
        return downloadFile(url, saveToFile, null);
    }

    /**
     * Download specified url directly and save it to local path.
     *
     * @param url        the url of the file.
     * @param saveToFile the file to save to.
     * @return true if success, false otherise.
     */
    public static boolean downloadFile(String url, File saveToFile, Operation operation) {
        InputStream is = null;
        HttpURLConnection connection = null;
        File tempFile = genDownloadTempFile(saveToFile);
        try {
            URL urlpath = new URL(url);
            connection = (HttpURLConnection) urlpath.openConnection();
            is = connection.getInputStream();
            int total = connection.getContentLength();
            IoUtils.copyStream(is, saveToFile, total, operation);
            if (total == tempFile.length()) {
                FileUtils.delete(saveToFile);
                return tempFile.renameTo(saveToFile);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            FileUtils.delete(tempFile);
        } finally {
            IoUtils.closeQuietly(is);
            disconnect(connection);
        }
        return false;
    }

    public static byte[] httpPost(String url, byte[] data) {
        return new HttpRequester.Builder()
                .url(url)
                .method("POST")
                .connectionTimeout(TIMEOUT)
                .readTimeout(TIMEOUT)
                .entity(data)
                .build().requestAsByte();
    }

    public static String httpPost(String url, String content) {
        return new HttpRequester.Builder()
                .url(url)
                .method("POST")
                .connectionTimeout(TIMEOUT)
                .readTimeout(TIMEOUT)
                .entity(content.getBytes())
                .build().requestAsString();
    }

    /**
     * Retrieve the content of the URL as byte array.
     *
     * @param url
     * @return
     */
    public static byte[] getUrlAsByteArray(String url) {
        return new HttpRequester.Builder().url(url).build().requestAsByte();
    }

    /**
     * 上传文件
     *
     * @param url
     * @param fieldName
     * @param file
     * @return
     */
    public static String uploadFile(String url, String fieldName, File file) {
        return new HttpRequester.Builder().url(url).method("POST").addFilePart(fieldName, file).build().requestAsString();
    }

    /**
     * 断开连接
     *
     * @param connection
     */
    public static void disconnect(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }

    /**
     * 生成下载临时文件
     *
     * @param file
     * @return
     */
    private static File genDownloadTempFile(File file) {
        return new File(file.getAbsolutePath() + ".tmp" + System.currentTimeMillis());
    }

    /**
     * 获取待下载的文件大小
     *
     * @param url 下载地址
     * @return 待下载文件长度
     */
    public static int getLength(String url) {
        int length = 0;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(10000);
            length = connection.getContentLength();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            disconnect(connection);
        }
        return length;
    }

    /**
     * 根据url获取文件名
     *
     * @param url
     * @return
     */
    public static String getFileNameFromUrl(String url) {
        if (!StringUtils.isEmpty(url)) {
            String path = url.replaceFirst("http:/", "");
            return new File(path).getName();
        }
        return null;
    }
}