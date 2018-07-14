package com.agmbat.server;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseText {

    public static final int SUCCESS = 0;

    private static final String STATUS = "status";
    private static final String STATUS_INFO = "statusInfo";
    private static final String DATA = "data";

    public static String retMsg(int status, String statusInfo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(STATUS, status);
            jsonObject.put(STATUS_INFO, statusInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String text = jsonObject.toString();
        return text;
    }

    public static String retData(int code, String msg, JSONObject data) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(STATUS, code);
            jsonObject.put(STATUS_INFO, msg);
            jsonObject.put(DATA, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String text = jsonObject.toString();
        return text;
    }
}