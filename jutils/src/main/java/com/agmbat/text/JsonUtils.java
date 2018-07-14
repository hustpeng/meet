/**
 * Copyright (C) 2017 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 * <p>
 * jutils
 *
 * @author mayimchen
 * @since 2017-07-23
 */
package com.agmbat.text;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Json处理工具类
 */
public class JsonUtils {

    /**
     * 在字符串在查找Json字符串
     *
     * @param result
     * @return
     */
    public static String findJsonString(String result) {
        int start = result.indexOf('{');
        int end = result.lastIndexOf('}') + 1;
        return result.substring(start, end);
    }

    /**
     * 获取指定key内容
     *
     * @param object
     * @param key
     * @return
     */
    public static String optString(JSONObject object, String key) {
        final Object o = object.opt(key);
        return !JSONObject.NULL.equals(o) ? o.toString() : null;
    }

    /**
     * 将字符串转为JSONObject
     *
     * @param text
     * @return
     */
    public static JSONObject asJsonObject(String text) {
        if (!StringUtils.isEmpty(text)) {
            try {
                return new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将字符串转为JSONArray
     *
     * @param text
     * @return
     */
    public static JSONArray asJSONArray(String text) {
        if (!StringUtils.isEmpty(text)) {
            try {
                return new JSONArray(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}