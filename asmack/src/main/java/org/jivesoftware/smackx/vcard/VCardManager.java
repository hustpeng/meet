
package org.jivesoftware.smackx.vcard;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.db.CacheStoreBase;
import org.jivesoftware.smackx.xepmodule.xepmodule;

import android.text.TextUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class VCardManager extends xepmodule {
    /**
     * vcard过期时间in millisecond,超过这个时间获取时会刷新vcard
     */
    private final static int VCARD_REFRESH_TIME = 180000;

    private final static int fetchVCard = 0;
    private final static int setMyVCard = 1;

    private CacheStoreBase<VCardObject> cacheStorage;

    private final List<VCardListener> listeners;

    public VCardManager(final Connection connection) {
        this.xmppConnection = connection;
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(myConnectionListener);
            }
        });
        listeners = new CopyOnWriteArrayList<VCardListener>();
        cacheStorage = new CacheStoreBase<VCardObject>();
    }

    private ConnectionListener myConnectionListener = new ConnectionListener() {
        @Override
        public void loginSuccessful() {
            cacheStorage.cleanAllEntryForOwner();
            fetchMyVCard();
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

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void addListener(VCardListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(VCardListener listener) {
        listeners.remove(listener);
    }

    private void notifyFetchVCardResult(String jid, VCardObject vcard) {
        for (VCardListener listener : listeners) {
            listener.notifyFetchVCardResult(jid, vcard);
        }
    }

    private void notifySetMyVCardResult(boolean success) {
        for (VCardListener listener : listeners) {
            listener.notifySetMyVCardResult(success);
        }
    }

    @Override
    public void processQueryWithFailureCode(xepQueryInfo queryInfo, String error) {
        switch (queryInfo.getQueryType()) {
            case fetchVCard:
                notifyFetchVCardResult(queryInfo.getParam1(), null);
                break;

            case setMyVCard:
                notifySetMyVCardResult(false);
                break;

            default:
                break;
        }
    }

    private void processQueryResponse(Packet packet, xepQueryInfo queryInfo) {
        switch (queryInfo.getQueryType()) {
            case fetchVCard: {
                if (packet.getError() == null) {
                    VCardObject object = ((VCardPacket) packet).getObject();
                    if (object != null) {
                        object.setJid(packet.getFrom());
                        object.setUpdate_date(new Date());
                        cacheStorage.insertOrUpdate(object);
                    }

                    notifyFetchVCardResult(queryInfo.getParam1(), object);
                } else {
                    notifyFetchVCardResult(queryInfo.getParam1(), null);
                }
            }
            break;

            case setMyVCard: {
                if (packet.getError() == null) {
                    VCardObject object = (VCardObject) queryInfo.getParam3();
                    if (object != null) {
                        object.setUpdate_date(new Date());
                        cacheStorage.insertOrUpdate(object);
                    }

                    notifySetMyVCardResult(true);
                } else {
                    notifySetMyVCardResult(false);
                }
            }
            break;

            default:
                break;
        }
    }

    public class FetchVCardPacket extends IQ {

        public FetchVCardPacket(String aJid) {
            setTo(aJid);
        }

        public String getChildElementXML() {
            return new StringBuffer()
                    .append("<")
                    .append(VCardProvider.elementName())
                    .append(" xmlns=\"")
                    .append(VCardProvider.namespace())
                    .append("\"/>")
                    .toString();
        }
    }

    /**
     * @return 返回vcard; 如果返回null, 则会异步去服务器获取,然后通过notifyFetchVCardResult返回结果
     * 如果当前没有登录，notifyFetchVCardResult(jid, null)会在函数返回前被调用。
     */
    public VCardObject fetchMyVCard() {
        return fetchVCard(xmppConnection.getBareJid());
    }

    /**
     * @param jid 必须传bare jid,不能带resource
     * @return 返回vcard; 如果返回null, 则会异步去服务器获取,然后通过notifyFetchVCardResult返回结果
     * 如果输入jid为空，或者当前没有登录，notifyFetchVCardResult(jid, null)会在函数返回前被调用。
     */
    public VCardObject fetchVCard(String jid) {
        if (TextUtils.isEmpty(jid)) {
            return null;
        }

        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifyFetchVCardResult(jid, null);
            return null;
        }

        VCardObject vCard = (VCardObject) cacheStorage.getEntryWithKey(jid.toLowerCase());
        if (vCard != null) {
            if (jid.equalsIgnoreCase(xmppConnection.getBareJid())) {
                return vCard;
            }

            //vcard还没有过期
            Date lastUpdateDate = vCard.getUpdate_date();
            if (lastUpdateDate != null) {
                if (lastUpdateDate.after(new Date(System.currentTimeMillis() - VCARD_REFRESH_TIME))) {
                    return vCard;
                }
            }
        }

        if (isQueryExist(fetchVCard, jid, null)) {
            return null;
        }

        FetchVCardPacket packet = new FetchVCardPacket(jid);

        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        VcardResultListener packetListener = new VcardResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        xepQueryInfo queryInfo = new xepQueryInfo(fetchVCard, jid);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
        return null;
    }

    public class SetVCardPacket extends IQ {

        private final VCardObject vcard;

        public SetVCardPacket(VCardObject newVCard) {
            vcard = newVCard;
            setType(Type.SET);
        }

        public String getChildElementXML() {
            return VCardObject.getXmlNode(vcard);
        }
    }

    /**
     * 异步接口，结果通过notifySetMyVCardResult返回
     *
     * @param newVCard
     * @return 如果输入newVCard为空，或者当前没有登录，notifySetMyVCardResult(false)会在函数返回前被调用。
     */
    public void setMyVCard(VCardObject newVCard) {
        if (newVCard == null) {
            notifySetMyVCardResult(false);
            return;
        }

        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifySetMyVCardResult(false);
            return;
        }

        SetVCardPacket packet = new SetVCardPacket(newVCard);

        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        VcardResultListener packetListener = new VcardResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        xepQueryInfo queryInfo = new xepQueryInfo(setMyVCard, null, null, newVCard);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    private class VcardResultListener implements PacketListener {
        public void processPacket(Packet packet) {
            String packetIdString = packet.getPacketID();
            xepQueryInfo queryInfo = getQueryInfo(packetIdString);
            if (queryInfo != null) {
                removeQueryInfo(queryInfo, packetIdString);
                processQueryResponse(packet, queryInfo);
            }
        }
    }
}
