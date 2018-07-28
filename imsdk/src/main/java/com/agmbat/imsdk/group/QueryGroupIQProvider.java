package com.agmbat.imsdk.group;

import android.text.TextUtils;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.XmppStringUtils;
import org.jivesoftware.smackx.findserver.FindServerObject;
import org.jivesoftware.smackx.findserver.FindServerPacket;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 由于查找群组和发现子服务器的命名空间一样，所以只能在这里统一解析服务器返回的数据，返回各自类型的IQ
 */
public class QueryGroupIQProvider implements IQProvider {

    private static final int MSG_TYPE_FIND_SERVER = 0; //发现服务

    private static final int MSG_TYPE_QUERY_GROUP_LIST = 1; //查找群列表

    private static final int MSG_TYPE_QUERY_GROUP_INFO = 2; //查询群详细信息


    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "http://jabber.org/protocol/disco#items";
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        List<GroupBean> groupBeans = new ArrayList<>();
        FindServerObject item = new FindServerObject();
        int messageType = -1;
        QueryGroupInfoResultIQ queryGroupInfoResultIQ = new QueryGroupInfoResultIQ();
        List<String> memberJids = new ArrayList<>();
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("item")) {
                    String circleStatus = parser.getAttributeValue("", "circle_status");
                    if (TextUtils.isEmpty(circleStatus)) {
                        messageType = MSG_TYPE_FIND_SERVER;
                        String name = parser.getAttributeValue("", "name");
                        if ("ticket".equals(name)) {
                            String value = parser.getAttributeValue("", "jid");
                            item.setTokenServer(value);
                        } else if ("circle".equals(name)) {
                            String value = parser.getAttributeValue("", "jid");
                            item.setCircleServer(value);
                        } else if ("vip".equals(name)) {
                            String value = parser.getAttributeValue("", "jid");
                            item.setPaidServer(value);
                        }
                    } else {
                        messageType = MSG_TYPE_QUERY_GROUP_LIST;
                        String groupJid = parser.getAttributeValue("", "jid");
                        String members = parser.getAttributeValue("", "members");
                        String groupName = parser.getAttributeValue("", "name");
                        String cover = parser.getAttributeValue("", "cover");
                        String owner = parser.getAttributeValue("", "owner");

                        GroupBean groupBean = new GroupBean(groupJid, groupName);
                        groupBean.setAvatar(cover);
                        if (!TextUtils.isEmpty(members) && TextUtils.isDigitsOnly(members)) {
                            groupBean.setMembers(Integer.parseInt(members));
                        }
                        groupBean.setOwnerJid(owner);
                        groupBeans.add(groupBean);
                    }
                } else if (parser.getName().equals("profile")) {
                    messageType = MSG_TYPE_QUERY_GROUP_INFO;
                    String cover = parser.getAttributeValue("", "cover");
                    String membersText = parser.getAttributeValue("", "members");
                    String owner = parser.getAttributeValue("", "owner");
                    String name = parser.getAttributeValue("", "name");
                    String category = parser.getAttributeValue("", "category");
                    String jid = parser.getAttributeValue("", "jid");
                    String verify = parser.getAttributeValue("", "verify");

                    queryGroupInfoResultIQ.setAvatar(cover);
                    if (!TextUtils.isEmpty(membersText) && TextUtils.isDigitsOnly(membersText)) {
                        queryGroupInfoResultIQ.setMembers(Integer.parseInt(membersText));
                    }
                    queryGroupInfoResultIQ.setOwner(owner);
                    queryGroupInfoResultIQ.setName(name);
                    queryGroupInfoResultIQ.setCategory(category);
                    queryGroupInfoResultIQ.setGroupJid(jid);
                    queryGroupInfoResultIQ.setNeedVerify("1".endsWith(verify));

                } else if (parser.getName().equals("headline")) {
                    String headline = geTagText(parser, "headline");
                    queryGroupInfoResultIQ.setHeadLine(headline);
                } else if (parser.getName().equals("description")) {
                    String description = geTagText(parser, "description");
                    queryGroupInfoResultIQ.setDescription(description);
                } else if(parser.getName().equals("current_user")){
                    parser.next();
                    parser.next();
                    String isMemberText = parser.getText();
                    if("true".equals(isMemberText)){
                        queryGroupInfoResultIQ.setGroupMember(true);
                    }else{
                        queryGroupInfoResultIQ.setGroupMember(false);
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("query")) {
                    done = true;
                }
            }
        }
        if (messageType == MSG_TYPE_FIND_SERVER) {
            FindServerPacket findServerPacket = new FindServerPacket();
            findServerPacket.setFindServerObject(item);
            return findServerPacket;
        } else if (messageType == MSG_TYPE_QUERY_GROUP_LIST) {
            QueryGroupResultIQ queryGroupResultIQ = new QueryGroupResultIQ();
            queryGroupResultIQ.setGroups(groupBeans);
            return queryGroupResultIQ;
        } else if (messageType == MSG_TYPE_QUERY_GROUP_INFO) {
            return queryGroupInfoResultIQ;
        }
        return null;
    }


    private String geTagText(XmlPullParser xmlPullParser, String tagName)
            throws XmlPullParserException, IOException {
        String mCity = "";
        xmlPullParser.require(XmlPullParser.START_TAG, null, tagName);
        if (xmlPullParser.next() == XmlPullParser.TEXT) {
            mCity = xmlPullParser.getText();
            xmlPullParser.nextTag();
        }
        xmlPullParser.require(XmlPullParser.END_TAG, null, tagName);
        return mCity;
    }

}
