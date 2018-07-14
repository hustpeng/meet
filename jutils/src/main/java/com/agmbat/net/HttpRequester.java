/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jhttp
 *
 * @author mayimchen
 * @since 2016-10-16
 */
package com.agmbat.net;

import com.agmbat.io.IoUtils;
import com.agmbat.log.Log;
import com.agmbat.text.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class HttpRequester {

    /**
     * GZip compression format.
     */
    private static final String COMPRESS_FORMAT_GZIP = "gzip";

    /**
     * Deflate compression format.
     */
    private static final String COMPRESS_FORMAT_DEFLATE = "deflate";


    private static final String LINE_FEED = "\r\n";

    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private static final int SOCKET_BUUFER_SIZE = 8192; // 8k
    private static final int SOCKET_TIMEOUT = 60 * 1000; // 60 seconds
    private static final int CONNECTION_TIMEOUT = 20 * 1000; // 20 seconds
    private static final int CONNECTION_MAX_IDLE = 15; // 15 seconds
    private static final int IDLE_CHECK_INTERVAL = 5 * 1000; // 5 seconds
    private static final int DEFAULT_ALIVE_DURATION = 15 * 1000; // 15 seconds


    private static final String DEFAULT_USER_AGENT =
            "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.8) Gecko/20100202 Firefox/3.5.8 GTB6";


//    private static HttpParams createDefaultHttpParams() {
//        final HttpParams params = new BasicHttpParams();
//
//        // Increase the connection count for the connection manager.
//        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(4));
//
//        // Turn off stale checking. Our connections break all the time anyway,
//        // and it's not worth it to pay the penalty of checking every time.
//        HttpConnectionParams.setStaleCheckingEnabled(params, false);
//
//        // Default connection and socket timeout of 20 seconds. Tweak to taste.
//        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
//        HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
//        HttpConnectionParams.setSocketBufferSize(params, SOCKET_BUUFER_SIZE);
//        // android-changed from AndroidHttpClient
//        HttpProtocolParams.setUseExpectContinue(params, false);
//        // Don't handle redirects -- return them to the caller. Our code
//        // often wants to re-POST after a redirect, which we must do ourselves.
//        HttpClientParams.setRedirecting(params, false);
//        return params;
//    }


    public static String getDefaultUserAgent() {
        return DEFAULT_USER_AGENT;
    }

    private final HttpRequesterData mData;

    private HttpRequester(HttpRequesterData data) {
        mData = data;
    }

    /**
     * 请求连接
     *
     * @return
     * @throws IOException
     */
    public HttpURLConnection request() throws IOException {
        String url = mData.getUrl();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(mData.mMethod);
        if (mData.mHeaders.size() > 0) {
            Set<String> keys = mData.mHeaders.keySet();
            for (String field : keys) {
                connection.setRequestProperty(field, mData.mHeaders.get(field));
            }
        }

        if (mData.mConnectionTimeout > 0) {
            connection.setConnectTimeout(mData.mConnectionTimeout);
        }
        if (mData.mReadTimeout > 0) {
            connection.setReadTimeout(mData.mReadTimeout);
        }
        // 添加参数
        if ("POST".equalsIgnoreCase(mData.mMethod)) {
            byte[] entity = mData.getEntity();
            if (entity != null) {
                connection.setDoOutput(true);
                OutputStream out = connection.getOutputStream();
                out.write(entity);
                // out.flush();
                // out.close();
            }
        }

        // 上传文件
        if (mData.mMultipartFilePart.size() > 0) {
            connection.setRequestMethod("POST");

            final String charset = mData.mMultipartCharset;
            // creates a unique boundary based on time stamp
            final String boundary = "===" + System.currentTimeMillis() + "===";

            connection.setUseCaches(false);
            connection.setDoOutput(true); // indicates POST method
            connection.setDoInput(true);

            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            OutputStream out = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, charset), true);

            // add formfield
            Set<String> keys = mData.mMultipartFormField.keySet();
            for (String field : keys) {
                addFormField(writer, boundary, field, mData.mMultipartFormField.get(field));
            }

            // add file
            Set<String> fileKeys = mData.mMultipartFilePart.keySet();
            for (String field : fileKeys) {
                addFilePart(writer, out, boundary, field, mData.mMultipartFilePart.get(field));
            }

            // finish
            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();
        }

        int status = connection.getResponseCode();
        Log.d("Server returned status: " + status);
        return connection;
    }

    /**
     * 发送请求获取数据, 请求结果为字符串
     *
     * @return
     */
    public String requestAsString() {
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            connection = request();
            is = getInputStream(connection);
            return IoUtils.loadContent(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(is);
            HttpUtils.disconnect(connection);
        }
        return null;
    }

    /**
     * 请求结果为Byte数组
     *
     * @return
     */
    public byte[] requestAsByte() {
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            connection = request();
            is = getInputStream(connection);
            return IoUtils.loadBytes(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(is);
            HttpUtils.disconnect(connection);
        }
        return null;
    }

    /**
     * 请求结果存为文件
     *
     * @param outputFile
     */
    public void requestAsFile(File outputFile) {
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            connection = request();
            is = getInputStream(connection);
            IoUtils.copyStream(is, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(is);
            HttpUtils.disconnect(connection);
        }
    }

    public static class Builder {

        private final HttpRequesterData mData;

        /**
         * Initiate a new instance of {@link Builder}.
         */
        public Builder() {
            mData = new HttpRequesterData();
        }

        /**
         * 配置url
         *
         * @param url
         * @return
         */
        public Builder url(String url) {
            mData.mUrl = url;
            return this;
        }

        /**
         * 配置base url,不带参数
         *
         * @param baseUrl
         * @return
         */
        public Builder baseUrl(String baseUrl) {
            mData.mBaseUrl = baseUrl;
            return this;
        }

        /**
         * 配置url参数
         *
         * @param key
         * @param value
         * @return
         */
        public Builder urlParam(String key, String value) {
            mData.mUrlParams.put(key, value);
            return this;
        }

        /**
         * 配置url编码
         *
         * @param encoding
         * @return
         */
        public Builder urlEncoding(String encoding) {
            mData.mUrlEncoding = encoding;
            return this;
        }

        /**
         * 添加请求头
         *
         * @param key
         * @param value
         * @return
         */
        public Builder addHeader(String key, String value) {
            mData.mHeaders.put(key, value);
            return this;
        }

        /**
         * 添加请求头
         *
         * @param headers
         * @return
         */
        public Builder addHeader(Map<String, String> headers) {
            if (headers != null) {
                mData.mHeaders.putAll(headers);
            }
            return this;
        }

        public Builder method(String method) {
            mData.mMethod = method;
            return this;
        }

        public Builder entity(byte[] data) {
            mData.mEntity = data;
            return this;
        }

        /**
         * post请数参数
         *
         * @param key
         * @param value
         * @return
         */
        public Builder postParam(String key, String value) {
            mData.mPostParams.put(key, value);
            return this;
        }

        public Builder keepAlive(boolean keepAlive) {
            mData.mKeepAlive = keepAlive;
            return this;
        }

        public Builder userAgent(String userAgent) {
            mData.mUserAgent = userAgent;
            return this;
        }

        public Builder soTimeout(int time) {
            mData.mSocketTimeout = time;
            return this;
        }

        public Builder connectionTimeout(int time) {
            mData.mConnectionTimeout = time;
            return this;
        }

        public Builder readTimeout(int timeout) {
            mData.mReadTimeout = timeout;
            return this;
        }

        /**
         * Adds a form field to the request
         *
         * @param name  field name
         * @param value field value
         */
        public Builder addFormField(String name, String value) {
            mData.mMultipartFormField.put(name, value);
            return this;

        }

        /**
         * Adds a upload file section to the request
         *
         * @param fieldName  name attribute in <input type="file" name="..." />
         * @param uploadFile a File to be uploaded
         */
        public Builder addFilePart(String fieldName, File uploadFile) {
            mData.mMultipartFilePart.put(fieldName, uploadFile);
            return this;
        }

        public HttpRequester build() {
            return new HttpRequester(mData);
        }

    }

    /**
     * 获取InputStream
     *
     * @param connection
     * @return
     * @throws IOException
     */
    private static InputStream getInputStream(HttpURLConnection connection) throws IOException {
        InputStream is = connection.getInputStream();
        String encoding = connection.getContentEncoding();
        if (COMPRESS_FORMAT_GZIP.equalsIgnoreCase(encoding)) {
            is = new GZIPInputStream(is);
        } else if (COMPRESS_FORMAT_DEFLATE.equalsIgnoreCase(encoding)) {
            is = new InflaterInputStream(is);
        }
        return is;
    }


    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(PrintWriter writer, String boundary, String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + mData.mMultipartCharset).append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(PrintWriter writer, OutputStream outputStream, String boundary, String fieldName, File uploadFile) throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        IoUtils.copyStream(inputStream, outputStream);
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();
    }

}
