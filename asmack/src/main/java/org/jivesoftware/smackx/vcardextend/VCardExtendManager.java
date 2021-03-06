package org.jivesoftware.smackx.vcardextend;

import android.text.TextUtils;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.db.CacheStoreBase;
import org.jivesoftware.smackx.xepmodule.XepQueryInfo;
import org.jivesoftware.smackx.xepmodule.Xepmodule;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class VCardExtendManager extends Xepmodule {
    /**
     * vcard过期时间in millisecond,超过这个时间获取时会刷新vcard
     */
    private final static int VCARDEXTEND_REFRESH_TIME = 180000;

    private final static int FETCH_VCARDEXTEND = 0;
    private final static int SET_MY_VCARDEXTEND = 1;
    private final List<VCardExtendListener> listeners;
    private CacheStoreBase<VCardExtendObject> cacheStorage;
    private ConnectionListener myConnectionListener = new ConnectionListener() {
        @Override
        public void loginSuccessful() {
            cacheStorage.cleanAllEntryForOwner();
//            fetchMyVCardExtend();
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            abortAllQuery();
        }

        @Override
        public void connectionClosed() {
            abortAllQuery();
        }
    };

    public VCardExtendManager(final Connection connection) {
        this.xmppConnection = connection;
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(myConnectionListener);
            }
        });
        listeners = new CopyOnWriteArrayList<VCardExtendListener>();
        cacheStorage = new CacheStoreBase<VCardExtendObject>();
    }

    public void addListener(VCardExtendListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(VCardExtendListener listener) {
        listeners.remove(listener);
    }

    private void notifyFetchVCardExtendResult(String jid, VCardExtendObject vcardExtend) {
        for (VCardExtendListener listener : listeners) {
            listener.notifyFetchVCardExtendResult(jid, vcardExtend);
        }
    }

    private void notifySetMyVCardExtendResult(boolean success) {
        for (VCardExtendListener listener : listeners) {
            listener.notifySetMyVCardExtendResult(success);
        }
    }

    @Override
    public void processQueryWithFailureCode(XepQueryInfo queryInfo, String error) {
        switch (queryInfo.getQueryType()) {
            case FETCH_VCARDEXTEND:
                notifyFetchVCardExtendResult(queryInfo.getParam1(), null);
                break;
            case SET_MY_VCARDEXTEND:
                notifySetMyVCardExtendResult(false);
                break;
            default:
                break;
        }
    }

    private void processQueryResponse(Packet packet, XepQueryInfo queryInfo) {
        switch (queryInfo.getQueryType()) {
            case FETCH_VCARDEXTEND: {
                if (packet.getError() == null) {
                    VCardExtendObject object = ((VCardExtendPacket) packet).getObject();
                    if (object != null) {
                        object.setJid(packet.getFrom());
                        object.setUpdate_date(new Date());
                        cacheStorage.insertOrUpdate(object);
                    }
                    notifyFetchVCardExtendResult(queryInfo.getParam1(), object);
                } else {
                    notifyFetchVCardExtendResult(queryInfo.getParam1(), null);
                }
            }
            break;
            case SET_MY_VCARDEXTEND: {
                if (packet.getError() == null) {
                    VCardExtendObject object = (VCardExtendObject) queryInfo.getParam3();
                    if (object != null) {
                        object.setUpdate_date(new Date());
                        cacheStorage.insertOrUpdate(object);
                    }
                    notifySetMyVCardExtendResult(true);
                } else {
                    notifySetMyVCardExtendResult(false);
                }
            }
            break;
            default:
                break;
        }
    }


    /**
     * @return 返回vcard; 如果返回null, 则会异步去服务器获取,然后通过notifyFetchVCardResult返回结果
     * 如果当前没有登录，notifyFetchVCardResult(jid, null)会在函数返回前被调用。
     */
//    public VCardExtendObject fetchMyVCardExtend() {
//        return fetchVCardExtend(xmppConnection.getBareJid());
//    }

    /**
     * @param jid 必须传bare jid,不能带resource
     * @return 返回vcard; 如果返回null, 则会异步去服务器获取,然后通过notifyFetchVCardResult返回结果
     * 如果输入jid为空，或者当前没有登录，notifyFetchVCardResult(jid, null)会在函数返回前被调用。
     */
    public VCardExtendObject fetchVCardExtend(String jid) {
        if (TextUtils.isEmpty(jid)) {
            return null;
        }

        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifyFetchVCardExtendResult(jid, null);
            return null;
        }

        VCardExtendObject vCardExtend = (VCardExtendObject) cacheStorage.getEntryWithKey(jid.toLowerCase());
        if (vCardExtend != null) {
            if (jid.equalsIgnoreCase(xmppConnection.getBareJid())) {
                return vCardExtend;
            }

            //vcard还没有过期
            Date lastUpdateDate = vCardExtend.getUpdate_date();
            if (lastUpdateDate != null) {
                if (lastUpdateDate.after(new Date(System.currentTimeMillis() - VCARDEXTEND_REFRESH_TIME))) {
                    return vCardExtend;
                }
            }
        }

        if (isQueryExist(FETCH_VCARDEXTEND, jid, null)) {
            return null;
        }

        FetchVCardExtendPacket packet = new FetchVCardExtendPacket(jid);

        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        VcardExtendResultListener packetListener = new VcardExtendResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        XepQueryInfo queryInfo = new XepQueryInfo(FETCH_VCARDEXTEND, jid);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
        return null;
    }

    /**
     * 异步接口，结果通过notifySetMyVCardResult返回
     *
     * @param newVCardExtend
     * @return 如果输入newVCard为空，或者当前没有登录，notifySetMyVCardResult(false)会在函数返回前被调用。
     */
    public void setMyVCardExtend(VCardExtendObject newVCardExtend) {
        if (newVCardExtend == null) {
            notifySetMyVCardExtendResult(false);
            return;
        }

        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifySetMyVCardExtendResult(false);
            return;
        }

        SetVCardExtendPacket packet = new SetVCardExtendPacket(newVCardExtend);

        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        VcardExtendResultListener packetListener = new VcardExtendResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        XepQueryInfo queryInfo = new XepQueryInfo(SET_MY_VCARDEXTEND, null, null, newVCardExtend);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    private class VcardExtendResultListener implements PacketListener {

        @Override
        public void processPacket(Packet packet) {
            String packetIdString = packet.getPacketID();
            XepQueryInfo queryInfo = getQueryInfo(packetIdString);
            if (queryInfo != null) {
                removeQueryInfo(queryInfo, packetIdString);
                processQueryResponse(packet, queryInfo);
            }
        }
    }
}
