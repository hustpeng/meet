package com.agmbat.imsdk.group;

import org.jivesoftware.smack.packet.IQ;

public class UpdateGroupIQ extends IQ {

    private String groupName;

    private String headline;

    private String description;

    private boolean needVerify;

    private int categoryId;

    public UpdateGroupIQ(String groupJid){
        setTo(groupJid);
        setType(Type.SET);
    }


    @Override
    public String getChildElementXML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<query xmlns=\"http://jabber.org/protocol/muc#owner\">");
        builder.append("<x xmlns=\"jabber:x:data\" type=\"submit\">");
        builder.append("<field type=\"text-single\" var=\"muc#circleprofile_circle_name\" label=\"群名\"><value>"+groupName+"</value></field>");
        builder.append("<field type=\"text-single\" var=\"muc#circleprofile_headline\" label=\"群公告\"><value>"+headline+"</value></field>");
        builder.append("<field type=\"text-single\" var=\"muc#circleprofile_description\" label=\"群描述\"><value>"+description+"</value></field>");
        //builder.append("<field type=\"text-single\" var=\"muc#circleprofile_cover\" label=\"群头像\"><value>" + avatar +"</value></field>");
        builder.append("<field type=\"boolean\" var=\"muc#circleprofile_is_verify\" label=\"加入群是否需要管理员审核\"><value>"+ (needVerify ? 1:0)+"</value></field>");
        builder.append("<field type=\"list-single\" var=\"muc#circleprofile_category_id\" label=\"群分类\"><value>"+categoryId+"</value></field>");
        builder.append("</x>");
        builder.append("</query>");
        return builder.toString();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isNeedVerify() {
        return needVerify;
    }

    public void setNeedVerify(boolean needVerify) {
        this.needVerify = needVerify;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
