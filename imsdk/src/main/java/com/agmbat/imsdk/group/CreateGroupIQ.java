package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class CreateGroupIQ extends IQ {

    private String groupName;

    private String notice;

    private String description;

    private String coverUrl;

    private boolean needVerify;

    private int category;

    public CreateGroupIQ(String groupName, boolean needVerify, int category) {
        this.groupName = groupName;
        this.needVerify = needVerify;
        this.category = category;
        description = "";
        notice = "";
        coverUrl = "";
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }


    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<query xmlns=\"" + CreateGroupIQProvider.namespace() + "\">");
        builder.append("<x xmlns=\"jabber:x:data\" type=\"submit\">");
        builder.append("<field type=\"text-single\" var=\"muc#circleprofile_circle_name\" label=\"群名\"><value>" + groupName + "</value></field>");
        builder.append("<field type=\"text-single\"var=\"muc#circleprofile_headline\"label=\"群公告\"><value>" + notice + "</value></field>");
        builder.append("<field type=\"text-single\"var=\"muc#circleprofile_description\"label=\"群描述\"><value>" + description + "</value></field>");
        builder.append("<field type=\"text-single\"var=\"muc#circleprofile_cover\"label=\"群头像\"><value>" + coverUrl + "</value></field>");
        int needVerifyValue = needVerify ? 1 : 0;
        builder.append("<field type=\"boolean\"var=\"muc#circleprofile_is_verify\"label=\"加入群是否需要管理员审核\"><value>" + needVerifyValue + "</value></field>");
        builder.append("<field type=\"list-single\"var=\"muc#circleprofile_category_id\"label=\"群分类\"><value>" + category + "</value></field>");
        builder.append("</x>");
        builder.append("</query>");
        return builder.toString();
    }

    @Override
    public String toXML() {
        return super.toXML();
    }
}
