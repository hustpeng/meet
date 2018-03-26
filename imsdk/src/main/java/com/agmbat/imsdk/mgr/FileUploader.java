package com.agmbat.imsdk.mgr;

import com.agmbat.net.HttpUtils;
import com.agmbat.text.JsonUtils;

import org.json.JSONObject;

import java.io.File;

public class FileUploader {

    private static final String URL = "https://www.xmpp.org.cn/uploadservice/upload.api?uid=kenchen&ticket=d39bf26b-ca54-4cb8-8f0f-6a74d8f25f23";
//    private static final String URL = "http://" + XmppPrefs.DEFAULT_XMPP_SERVER
//            + ":8080/upload/api?uid=kenchen&ticket=d39bf26b-ca54-4cb8-8f0f-6a74d8f25f23";

    public static String uploadFile(String path) {
        // {"success": true,"file_url": "https://www.xmpp.org.cn/uploadservice/files/2018-03-09_bb43e051ede64897ae5d7461f09f67df.jpg"}
        String result = HttpUtils.uploadFile(URL, "FileData", new File(path));
        JSONObject jsonObject = JsonUtils.asJsonObject(result);
        if (jsonObject != null) {
            boolean success = jsonObject.optBoolean("success");
            if (success) {
                return jsonObject.optString("file_url");
            }
        }
        return null;
    }
}
