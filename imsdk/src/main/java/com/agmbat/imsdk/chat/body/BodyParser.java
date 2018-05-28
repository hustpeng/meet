package com.agmbat.imsdk.chat.body;

import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;

import com.agmbat.android.utils.XmlUtils;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.text.StringParser;
import com.baidu.location.BDLocation;

public class BodyParser {

    /**
     * 解析body
     *
     * @param bodyText
     * @return
     */
    public static Body parse(String bodyText) {
        String type = XmlUtils.getNodeValue(bodyText, "type");
        BodyType bodyType = BodyType.TEXT;
        if (!TextUtils.isEmpty(type)) {
            bodyType = BodyType.valueOf(type);
        } else {
            return new TextBody(bodyText);
        }
        // 文本
        if (bodyType == BodyType.TEXT) {
            String content = XmlUtils.getNodeValue(bodyText, "content");
            return new TextBody(content);
        }
        // url
        if (bodyType == BodyType.URL) {
            String content = XmlUtils.getNodeValue(bodyText, "content");
            return new UrlBody(content);
        }

        // 音频
        if (bodyType == BodyType.AUDIO) {
            String fileUrl = XmlUtils.getNodeValue(bodyText, "file_url");
            long duration = StringParser.parseLong(XmlUtils.getNodeValue(bodyText, "duration"));
            return new AudioBody(fileUrl, duration);
        }

        // 图片
        if (bodyType == BodyType.IMAGE) {
            String fileUrl = XmlUtils.getNodeValue(bodyText, "file_url");
            ImageBody.Image image = new ImageBody.Image(bodyText);
            return new ImageBody(fileUrl, image);
        }

        // 阅后即焚图片
        if (bodyType == BodyType.FIRE) {
            String fileUrl = XmlUtils.getNodeValue(bodyText, "file_url");
            ImageBody.Image image = new ImageBody.Image(bodyText);
            return new FireBody(fileUrl, image);
        }

        // 位置
        if (bodyType == BodyType.GEOLOC) {
            String latText = XmlUtils.getNodeValue(bodyText, "lat");
            String lonText = XmlUtils.getNodeValue(bodyText, "lon");
            String address = XmlUtils.getNodeValue(bodyText, "address");
            BDLocation location = new BDLocation();
            location.setLatitude(StringParser.parseDouble(latText));
            location.setLongitude(StringParser.parseDouble(lonText));
//            if (!TextUtils.isEmpty(address)) {
//                Bundle extras = new Bundle();
//                extras.putString(LocationBody.EXTRA_ADDRESS, address);
//                location.setExtras(extras);
//            }
            return new LocationBody(location);
        }

        // 好友
        if (bodyType == BodyType.FRIEND) {
            String jid = XmlUtils.getNodeValue(bodyText, "friend_jid");
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setBareJid(jid);
            return new FriendBody(contactInfo);
        }
        return new TextBody(bodyText);
    }

}
