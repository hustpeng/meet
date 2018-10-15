package com.agmbat.imsdk.chat.body;

import com.agmbat.android.sysprovider.LocalImage;
import com.agmbat.android.utils.XmlUtils;
import com.agmbat.imsdk.mgr.XmppFileManager;
import com.agmbat.net.HttpUtils;
import com.agmbat.text.StringParser;

import java.io.File;

public class ImageBody extends Body {

    private final Image mImage;
    private final String mFileUrl;
    public ImageBody(String fileUrl, LocalImage image) {
        this(fileUrl, new Image(image));
    }

    public ImageBody(String fileUrl, Image image) {
        mFileUrl = fileUrl;
        mImage = image;
    }

    public String getFileUrl() {
        return mFileUrl;
    }

    public Image getImage() {
        return mImage;
    }

    public File getLocalFile() {
        return new File(XmppFileManager.getImageDir(), HttpUtils.getFileNameFromUrl(mFileUrl));
    }

    @Override
    public String toXml() {
        StringBuilder builder = new StringBuilder();
        builder.append("<wrap>");
        builder.append("<type>").append(getBodyType()).append("</type>");
        builder.append("<file_url>").append(mFileUrl).append("</file_url>");
        builder.append("<width>").append(mImage.width).append("</width>");
        builder.append("<height>").append(mImage.height).append("</height>");
        builder.append("<latitude>").append(mImage.latitude).append("</latitude>");
        builder.append("<longitude>").append(mImage.longitude).append("</longitude>");
        builder.append("<rotation>").append(mImage.rotation).append("</rotation>");
        builder.append("</wrap>");
        return builder.toString();
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.IMAGE;
    }

    public static class Image {

        public int rotation;
        public double latitude;
        public double longitude;
        public int width;
        public int height;
        public Image(String xmlText) {
            rotation = StringParser.parseInt(XmlUtils.getNodeValue(xmlText, "rotation"));
            latitude = StringParser.parseDouble(XmlUtils.getNodeValue(xmlText, "latitude"));
            longitude = StringParser.parseDouble(XmlUtils.getNodeValue(xmlText, "longitude"));
            width = StringParser.parseInt(XmlUtils.getNodeValue(xmlText, "width"));
            height = StringParser.parseInt(XmlUtils.getNodeValue(xmlText, "height"));
        }
        public Image(LocalImage image) {
            rotation = image.rotation;
            latitude = image.latitude;
            longitude = image.longitude;
            width = image.width;
            height = image.height;
        }
        public Image() {
        }
    }
}