package com.agmbat.imsdk.group;

import android.text.TextUtils;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.findserver.FindServerObject;
import org.jivesoftware.smackx.findserver.FindServerPacket;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

/**
 * 由于查找群组和发现子服务器的命名空间一样，所以只能在这里统一解析服务器返回的数据，返回各自类型的IQ
 */
public class QueryGroupIQProvider implements IQProvider {


    public static String elementName() {
        return "query";
    }

    public static String namespace() {
        return "http://jabber.org/protocol/disco#items";
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        QueryGroupResultIQ queryGroupResultIQ = new QueryGroupResultIQ();
        List<GroupBean> groupBeans = new ArrayList<>();

        FindServerPacket findServerPacket = new FindServerPacket();
        FindServerObject item = new FindServerObject();

        boolean isFindServer = false;

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("item")) {
                    String circleStatus = parser.getAttributeValue("", "circle_status");
                    isFindServer = TextUtils.isEmpty(circleStatus);
                    if (isFindServer) {
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
                        String groupJid = parser.getAttributeValue("", "jid");
                        String members = parser.getAttributeValue("", "members");
                        String groupName = parser.getAttributeValue("", "name");
                        String cover = parser.getAttributeValue("", "cover");
                        String owner = parser.getAttributeValue("", "owner");

                        GroupBean groupBean = new GroupBean(groupJid, groupName);
                        groupBean.setAvatar(cover);
                        if (!TextUtils.isEmpty(members)) {
                            groupBean.setMembers(Integer.parseInt(members));
                        }
                        groupBean.setOwnerJid(owner);
                        groupBeans.add(groupBean);
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("query")) {
                    done = true;
                }
            }
        }
        if (isFindServer) {
            findServerPacket.setFindServerObject(item);
            return findServerPacket;
        } else {
            queryGroupResultIQ.setGroups(groupBeans);
            return queryGroupResultIQ;
        }
    }

}
