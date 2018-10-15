package org.jivesoftware.smackx.circle;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.XmppStringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 提交修改群表单：
 * SEND:
 * <iq xmlns="jabber:client" type="set" to="2@circle.yuan520.com" id="sd19">
 * <query xmlns="http://jabber.org/protocol/muc#owner">
 * <x xmlns="jabber:x:data" type="submit">
 * <field type="text-single" var="muc#circleprofile_circle_name" label="群名"><value>android P</ value ></field>
 * <field type="text-single" var="muc#circleprofile_headline" label="群公告"><value>true love2</ value ></field>
 * <field type="text-single" var="muc#circleprofile_description" label="群描述"><value>talk about love2</ value ></field>
 * <field type="text-single" var="muc#circleprofile_cover" label="群头像">
 * <value >http://p6bt95t1n.bkt.clouddn.com/08ff1dd7247f4fbe5955b78db92ab88535173.jpg</ value ></field>
 * <field type="boolean" var="muc#circleprofile_is_verify" label="加入群是否需要管理员审核"><value>1</value></field>
 * <field type="list-single" var="muc#circleprofile_category_id" label="群分类"><value>2</ value >
 * </field>
 * </x>
 * </query>
 * </iq>
 */
public class CommitCircleInfoPacket extends Packet {

    private List<FieldItem> mFieldList = new ArrayList<>();

    public CommitCircleInfoPacket(String to) {
        setTo(to);
    }

    @Override
    public String getXmlns() {
        return "jabber:client";
    }

    @Override
    public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<iq ");
        if (getTo() != null) {
            buf.append("to=\"").append(XmppStringUtils.escapeForXML(getTo())).append("\" ");
        }

        if (getXmlns() != null) {
            buf.append(" xmlns=\"").append(getXmlns()).append("\"");
        }

        if (getPacketID() != null) {
            buf.append("id=\"" + getPacketID() + "\" ");
        }

        buf.append("type=\"set\">");

        // Add the query section if there is one.
        String queryXML = getChildElementXML();
        if (queryXML != null) {
            buf.append(queryXML);
        }
        buf.append("</iq>");
        return buf.toString();
    }

    /**
     * * <query xmlns="http://jabber.org/protocol/muc#owner">
     * <x xmlns="jabber:x:data" type="submit">
     * <field type="text-single" var="muc#circleprofile_circle_name" label="群名"><value>android P</ value ></field>
     * <field type="text-single" var="muc#circleprofile_headline" label="群公告"><value>true love2</ value ></field>
     * <field type="text-single" var="muc#circleprofile_description" label="群描述"><value>talk about love2</ value ></field>
     * <field type="text-single" var="muc#circleprofile_cover" label="群头像">
     * <value >http://p6bt95t1n.bkt.clouddn.com/08ff1dd7247f4fbe5955b78db92ab88535173.jpg</ value ></field>
     * <field type="boolean" var="muc#circleprofile_is_verify" label="加入群是否需要管理员审核"><value>1</value></field>
     * <field type="list-single" var="muc#circleprofile_category_id" label="群分类"><value>2</ value >
     * </field>
     * </x>
     * </query>
     *
     * @return
     */
    private String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append(" <query xmlns=\"http://jabber.org/protocol/muc#owner\">");
        buf.append("<x xmlns=\"jabber:x:data\" type=\"submit\">");
        if (mFieldList != null && mFieldList.size() > 0) {
            for (FieldItem item : mFieldList) {
                buf.append(item.toXML());
            }
        }
        buf.append("</x>");
        buf.append("</query>");
        return buf.toString();
    }
}
