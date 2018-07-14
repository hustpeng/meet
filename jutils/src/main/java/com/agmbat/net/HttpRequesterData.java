package com.agmbat.net;

import com.agmbat.text.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 此类封装请求的相关数据, 不对外开放此类
 */
public class HttpRequesterData {

    /**
     * 默认编码
     */
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * url全地址
     */
    public String mUrl;

    /**
     * base url地址, 用于拼接参数
     */
    public String mBaseUrl;


    /**
     * 请求头
     */
    public Map<String, String> mHeaders;

    /**
     * We use this because we have to keep the order of the query keys.
     */
    public Map<String, String> mUrlParams;

    /**
     * 将参数放在post body中
     */
    public Map<String, String> mPostParams;

    /**
     * 请求方法
     */
    public String mMethod;
    public String mUserAgent;
    public boolean mKeepAlive = false; // by default disable it

    public int mSocketTimeout;
    public int mConnectionTimeout;
    public int mReadTimeout;
    public String mUrlEncoding;


    /**
     * post 数据
     */
    public byte[] mEntity;


    /**
     * 以下数据用于文件上传
     */
    public String mMultipartCharset;

    /**
     * 上传文件的Field
     */
    public Map<String, String> mMultipartFormField;
    public Map<String, File> mMultipartFilePart;


    HttpRequesterData() {
        mHeaders = new LinkedHashMap<>();
        mUrlParams = new LinkedHashMap<>();
        mPostParams = new LinkedHashMap<>();

        // 默认为GET方法
        mMethod = "GET";
        // 默认为utf8
        mUrlEncoding = DEFAULT_CHARSET;

        mMultipartCharset = DEFAULT_CHARSET;
        mMultipartFormField = new LinkedHashMap<>();
        mMultipartFilePart = new LinkedHashMap<>();
    }

    /**
     * 获取Url
     *
     * @return
     */
    public String getUrl() {
        if (StringUtils.isEmpty(mBaseUrl)) {
            return mUrl;
        }
        String query = encodeParams(mUrlParams, mUrlEncoding);
        if (StringUtils.isEmpty(query)) {
            return mBaseUrl;
        }
        return mBaseUrl + "?" + query;
    }

    /**
     * 获取post entity
     *
     * @return
     */
    public byte[] getEntity() {
        if (mEntity != null) {
            return mEntity;
        }
        if (mPostParams.size() > 0) {
            String params = encodeParams(mPostParams, DEFAULT_CHARSET);
            return StringUtils.getUtf8Bytes(params);
        }
        return null;
    }

    /**
     * Url编码参数
     *
     * @param params 参数建议使用LinkedHashMap, 保证拼接的顺序
     * @return
     */
    private static String encodeParams(Map<String, String> params, String enc) {
        try {
            StringBuilder builder = new StringBuilder();
            Set<Map.Entry<String, String>> queries = params.entrySet();
            for (Map.Entry<String, String> query : queries) {
                builder.append(query.getKey());
                builder.append('=');
                String value = query.getValue();
                if (value != null) {
                    value = URLEncoder.encode(value, enc);
                } else {
                    value = "";
                }
                builder.append(value);
                builder.append('&');
            }
            // Remove to tailing &
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
            return builder.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
