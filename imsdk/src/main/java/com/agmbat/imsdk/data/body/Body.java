package com.agmbat.imsdk.data.body;

import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;

import com.agmbat.android.utils.XmlUtils;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.text.StringParser;

public abstract class Body {

    public static final String EXTRA_ADDRESS = "address";

    public abstract String toXml();

    public abstract BodyType getBodyType();

    public static Body parse(String bodyText) {
        String type = XmlUtils.getNodeValue(bodyText, "type");
        BodyType bodyType = BodyType.TEXT;
        if (!TextUtils.isEmpty(type)) {
            bodyType = BodyType.valueOf(type);
        }
        if (bodyType == BodyType.TEXT) {
            String content = XmlUtils.getNodeValue(bodyText, "content");
            Body body = new TextBody(content);
            return body;
        } else if (bodyType == BodyType.AUDIO) {
            String fileUrl = XmlUtils.getNodeValue(bodyText, "file_url");
            long duration = StringParser.parseLong(XmlUtils.getNodeValue(bodyText, "duration"));
            Body body = new AudioBody(fileUrl, duration);
            return body;
        } else if (bodyType == BodyType.IMAGE) {
            String fileUrl = XmlUtils.getNodeValue(bodyText, "file_url");
            ImageBody.Image image = new ImageBody.Image(bodyText);
            Body body = new ImageBody(fileUrl, image);
            return body;
        } else if (bodyType == BodyType.FIRE) {
            String fileUrl = XmlUtils.getNodeValue(bodyText, "file_url");
            ImageBody.Image image = new ImageBody.Image(bodyText);
            Body body = new FireBody(fileUrl, image);
            return body;
        } else if (bodyType == BodyType.GEOLOC) {
            String latText = XmlUtils.getNodeValue(bodyText, "lat");
            String lonText = XmlUtils.getNodeValue(bodyText, "lon");
            String address = XmlUtils.getNodeValue(bodyText, "address");
            Location location = new Location("?");
            location.setLatitude(StringParser.parseDouble(latText));
            location.setLongitude(StringParser.parseDouble(lonText));
            if (!TextUtils.isEmpty(address)) {
                Bundle extras = new Bundle();
                extras.putString(EXTRA_ADDRESS, address);
                location.setExtras(extras);
            }
            Body body = new LocationBody(location);
            return body;
        } else if (bodyType == BodyType.FRIEND) {
            String jid = XmlUtils.getNodeValue(bodyText, "friend_jid");
            ContactInfo contactInfo = new ContactInfo(jid);
            return new FriendBody(contactInfo);
        }
        return new TextBody(bodyText);
    }


}
