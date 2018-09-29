package com.agmbat.imsdk.chat.body;

import android.text.TextUtils;

import com.agmbat.android.utils.XmlUtils;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.map.LocationObject;
import com.agmbat.text.StringParser;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
            List<TextBody.AtUser> atUsers = parseAtUsers(bodyText);
            return new TextBody(content, atUsers);
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
            LocationObject locationObject = new LocationObject();
            locationObject.mLatitude = StringParser.parseDouble(latText);
            locationObject.mLongitude = StringParser.parseDouble(lonText);
            locationObject.mAddress = address;
            return new LocationBody(locationObject);
        }

        // 好友
        if (bodyType == BodyType.FRIEND) {
            String jid = XmlUtils.getNodeValue(bodyText, "friend_jid");
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setBareJid(jid);
            return new FriendBody(contactInfo);
        }

        if (bodyType == BodyType.FILE) {
            String url = XmlUtils.getNodeValue(bodyText, "url");
            String fileName = XmlUtils.getNodeValue(bodyText, "fileName");
            return new FileBody(url, fileName);
        }

        if(bodyType == BodyType.EVENTS){
            return new EventsBody();
        }
        return new TextBody(bodyText);
    }

    public static List<TextBody.AtUser> parseAtUsers(String xmlString) {
        List<TextBody.AtUser> result = null;
        TextBody.AtUser atUser = null;
        try {
            Document document = DocumentHelper.parseText(xmlString);
            Element ele = document.getRootElement();
            Iterator<Element> it = ele.elementIterator();
            while (it.hasNext()){
                Element node = it.next();
                if ("at".equals(node.getName())) {
                    //result = node.getText();
                    result = new ArrayList<>();
                    Iterator<Element> userNodes = node.elementIterator();
                    while (userNodes.hasNext()){
                        atUser = new TextBody.AtUser();
                        result.add(atUser);
                        Element userNode = userNodes.next();
                        Iterator<Element> userInfoNodes = userNode.elementIterator();
                        while (userInfoNodes.hasNext()){
                            Element userInfoNode = userInfoNodes.next();
                            if("jid".equals(userInfoNode.getName())){
                                atUser.setJid(userInfoNode.getText());
                            }else if("nickname".equals(userInfoNode.getName())){
                                atUser.setNickName(userInfoNode.getText());
                            }
                        }
                    }

                }
            }
        } catch (DocumentException e) {
        }
        return result;
    }

}
