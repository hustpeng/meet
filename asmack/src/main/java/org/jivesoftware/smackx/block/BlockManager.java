package org.jivesoftware.smackx.block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.XmppStringUtils;
import org.jivesoftware.smackx.db.CacheStoreBase;
import org.jivesoftware.smackx.xepmodule.XepQueryInfo;
import org.jivesoftware.smackx.xepmodule.Xepmodule;

import android.text.TextUtils;

public class BlockManager extends Xepmodule {
    private static final int fetchBlockListName = 111;
    private static final int fetchBlockList = 222;
    private static final int addBlock = 333;
    private static final int removeBlock = 444;
    private static final int setDefaultName = 555;
    private static final int setActiveName = 666;

    private CacheStoreBase<BlockObject> cacheStorage;
    private String mListName = "block-message-list";

    private String mDefaultListName = "";
    private String mActiviteListName;
    boolean hasBlock = false;
    private final List<BlockListener> listeners;

    public BlockManager(final Connection connection) {
        this.xmppConnection = connection;
        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(myConnectionListener);
            }
        });

        listeners = new CopyOnWriteArrayList<BlockListener>();

        cacheStorage = new CacheStoreBase<BlockObject>();
    }

    private ConnectionListener myConnectionListener = new ConnectionListener() {
        @Override
        public void loginSuccessful() {
            cacheStorage.cleanAllEntryForOwner();
            fetchBlockListName();
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
    public void clearResource() {
        super.clearResource();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void addListener(BlockListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(BlockListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void processQueryWithFailureCode(XepQueryInfo aQuery, String error) {
        switch (aQuery.getQueryType()) {
            case fetchBlockListName:
                notifyFetchBlockListNameResult(false);
                break;
            case fetchBlockList:
                notifyFetchBlockResult(false);
                break;
            case addBlock:
                notifyAddBlockResult(aQuery.getParam1(), false);
                break;
            case removeBlock:
                notifyRemoveBlockResult(aQuery.getParam1(), false);
                break;
            case setDefaultName:

                break;
            default:
                break;
        }
    }

    private void processQueryResponse(Packet packet, XepQueryInfo queryInfo) {
        switch (queryInfo.getQueryType()) {
            case fetchBlockListName: {
                BlockPacket blockPacket = (BlockPacket) packet;
                setListName(blockPacket.getListName());
                setDefaultListName(blockPacket.getDefaultName());
                setActiveListName(blockPacket.getActiveName());
                fetchBlockList();
                notifyFetchBlockListNameResult(true);
            }
            break;
            case fetchBlockList:
                BlockPacket blockPacket = (BlockPacket) packet;
                for (BlockObject item : blockPacket.getBlockItems()) {
                    cacheStorage.insertOrUpdate(item);
                }
                notifyFetchBlockResult(true);
                break;
            case addBlock:
                BlockObject object = new BlockObject();
                object.setJid(queryInfo.getParam1());
                cacheStorage.insertOrUpdate(object);
                if (TextUtils.isEmpty(getDefaultListName())) {
                    setDefaultName(getListName());
                } else {
                    setActiveName(getListName());
                }
                notifyAddBlockResult(queryInfo.getParam1(), true);
                break;
            case removeBlock:
                BlockObject obj = new BlockObject();
                obj.setJid(queryInfo.getParam1());
                cacheStorage.delete(obj);
                if (0 == cacheStorage.getAllEntires().size()) {
                    setActiveName("");
                } else {
                    setActiveName(getListName());
                }
                notifyRemoveBlockResult(queryInfo.getParam1(), true);
                break;
            case setDefaultName:
                setDefaultListName(queryInfo.getParam1());
                if (!TextUtils.isEmpty(getDefaultListName())) {
                    setActiveName(getDefaultListName());
                } else {

                }
                break;
            case setActiveName:
                setActiveListName(queryInfo.getParam1());
                if (TextUtils.isEmpty(getActiveListName())) {
                    setDefaultName(getActiveListName());
                }
                break;
            default:
                break;
        }
    }

    public ArrayList<BlockObject> getAllBlockObjects() {
        return cacheStorage.getAllEntires();
    }

    public void fetchBlockListName() {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifyFetchBlockListNameResult(false);
            return;
        }

        if (isQueryExist(fetchBlockListName, null, null)) {
            return;
        }
        BlockPacket packet = new BlockPacket();
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        BlockResultListener packetListener = new BlockResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        XepQueryInfo queryInfo = new XepQueryInfo(fetchBlockListName);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    public void fetchBlockList() {
        if (TextUtils.isEmpty(getActiveListName())) {
            notifyFetchBlockResult(false);
            return;
        }
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifyFetchBlockResult(false);
            return;
        }

        if (isQueryExist(fetchBlockList, null, null)) {
            return;
        }

        BlockPacket packet = new BlockPacket();
        packet.setListName(getListName());
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        BlockResultListener packetListener = new BlockResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        XepQueryInfo queryInfo = new XepQueryInfo(fetchBlockList);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    public class AddBlockPacket extends IQ {
        private String mJid;

        private ArrayList<BlockObject> mList;

        public AddBlockPacket(String jid) {
            setType(Type.SET);
            this.mJid = jid;
            BlockObject object = new BlockObject();
            object.setJid(mJid);
            mList = cacheStorage.getAllEntires();
            if (null == mList) {
                mList = new ArrayList<BlockObject>();
            }
            mList.add(object);
        }

        @Override
        public String getChildElementXML() {
            if (null == mList) {
                return null;
            }
            if (0 == mList.size()) {
                return null;
            }
            String blocker = getXmlNode(mList);
            StringBuilder buffer = new StringBuilder();

            buffer.append("<");
            buffer.append(BlockProvider.elementName());
            buffer.append(" xmlns=\"");
            buffer.append(BlockProvider.namespace());
            buffer.append("\">");
            buffer.append("<list name=\"");
            buffer.append(getListName());
            buffer.append("\">");

            buffer.append(blocker);

            buffer.append("</list></");
            buffer.append(BlockProvider.elementName());
            buffer.append(">");

            return buffer.toString();
        }

        private String getXmlNode(ArrayList<BlockObject> list) {
            if (null == list) {
                return null;
            }
            if (list.size() == 0) {
                return null;
            }
            StringBuilder buffer = new StringBuilder();
            for (BlockObject object : list) {
                buffer.append("<item");
                // buffer.append(" xmlns=\"");
                // buffer.append(BlockProvider.namespace());
                buffer.append(" action=\"deny\" type=\"jid\" value=\"");
                buffer.append(XmppStringUtils.escapeForXML(object.getJid()));
                buffer.append("\" order=\"0\">");
                buffer.append("<message/></item>");
            }
            return buffer.toString();
        }
    }

    public void addBlock(String jid) {

        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifyAddBlockResult(jid, false);
            return;
        }

        if (isQueryExist(addBlock, jid, null)) {
            return;
        }

        AddBlockPacket packet = new AddBlockPacket(XmppStringUtils.parseBareAddress(jid));
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        BlockResultListener packetListener = new BlockResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        XepQueryInfo queryInfo = new XepQueryInfo(addBlock, jid);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    public class RemoveBlockPacket extends IQ {
        private String mJid;

        private ArrayList<BlockObject> mList = new ArrayList<BlockObject>();

        public RemoveBlockPacket(String jid) {
            setType(Type.SET);
            this.mJid = jid;
            for (BlockObject obj : cacheStorage.getAllEntires()) {
                if (!obj.getJid().equals(mJid)) {
                    mList.add(obj);
                }
            }
        }

        @Override
        public String getChildElementXML() {
            if (null == mList) {
                return null;
            }
            if (0 == mList.size()) {
                return null;
            }
            StringBuilder buffer = new StringBuilder();
            String blocker = getXmlNode(mList);
            buffer.append("<");
            buffer.append(BlockProvider.elementName());
            buffer.append(" xmlns=\"");
            buffer.append(BlockProvider.namespace());
            buffer.append("\">");
            if (!TextUtils.isEmpty(blocker)) {
                buffer.append("<list name=\"");
                buffer.append(getListName());
                buffer.append("\">");

                buffer.append(blocker);

                buffer.append("</list>");

            } else {
                buffer.append("<active/>");
            }
            buffer.append("</");
            buffer.append(BlockProvider.elementName());
            buffer.append(">");
            return buffer.toString();
        }

        private String getXmlNode(ArrayList<BlockObject> list) {
            if (null == list) {
                return null;
            }
            if (list.size() == 0) {
                return null;
            }
            StringBuilder buffer = new StringBuilder();
            for (BlockObject object : list) {
                buffer.append("<item");
                buffer.append(" action=\"deny\" type=\"jid\" value=\"");
                buffer.append(XmppStringUtils.escapeForXML(object.getJid()));
                buffer.append("\" order=\"0\">");
                buffer.append("<message/></item>");
            }
            return buffer.toString();
        }
    }

    public void removeBlock(String jid) {
        if (cacheStorage.getAllEntires().size() == 1) {
            BlockObject obj = new BlockObject();
            obj.setJid(jid);
            cacheStorage.delete(obj);
            setActiveName("");
            notifyRemoveBlockResult(jid, true);
            return;
        }
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            notifyRemoveBlockResult(jid, false);
            return;
        }

        if (isQueryExist(removeBlock, jid, null)) {
            return;
        }

        RemoveBlockPacket packet = new RemoveBlockPacket(XmppStringUtils.parseBareAddress(jid));
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        BlockResultListener packetListener = new BlockResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        XepQueryInfo queryInfo = new XepQueryInfo(removeBlock, jid);
        addQueryInfo(queryInfo, packetId, packetListener);

        xmppConnection.sendPacket(packet);
    }

    public class ActiveNamePacket extends IQ {

        private String activeName;

        public ActiveNamePacket(String name) {
            setType(Type.SET);
            setActiveName(name);
        }

        @Override
        public String getChildElementXML() {
            StringBuilder buffer = new StringBuilder();
            buffer.append("<");
            buffer.append(BlockProvider.elementName());
            buffer.append(" xmlns=\"");
            buffer.append(BlockProvider.namespace());
            buffer.append("\">");
            if (TextUtils.isEmpty(getActiveName())) {
                buffer.append("<active/>");
            } else {
                buffer.append("<active");
                buffer.append(" name=\"block-message-list\"/>");
            }
            buffer.append("</");
            buffer.append(BlockProvider.elementName());
            buffer.append(">");
            return buffer.toString();
        }

        public String getActiveName() {
            return activeName;
        }

        public void setActiveName(String activeName) {
            this.activeName = activeName;
        }

    }

    public void setActiveName(String activeName) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            return;
        }

        if (isQueryExist(setActiveName, null, null)) {
            return;
        }

        ActiveNamePacket packet = new ActiveNamePacket(activeName);
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        BlockResultListener packetListener = new BlockResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        XepQueryInfo queryInfo = new XepQueryInfo(setActiveName, activeName);
        addQueryInfo(queryInfo, packetId, packetListener);
        xmppConnection.sendPacket(packet);
    }

    public class DefaultNamePacket extends IQ {
        private String defaultName;

        public DefaultNamePacket(String name) {
            setType(Type.SET);
            setDefaultName(name);
        }

        @Override
        public String getChildElementXML() {
            StringBuilder buffer = new StringBuilder();
            buffer.append("<");
            buffer.append(BlockProvider.elementName());
            buffer.append(" xmlns=\"");
            buffer.append(BlockProvider.namespace());
            buffer.append("\">");
            if (TextUtils.isEmpty(getDefaultName())) {
                buffer.append("<default/>");
            } else {
                buffer.append("<default");
                buffer.append(" name=\"block-message-list\"/>");
            }
            buffer.append("</");
            buffer.append(BlockProvider.elementName());
            buffer.append(">");
            return buffer.toString();
        }

        public String getDefaultName() {
            return defaultName;
        }

        public void setDefaultName(String defaultName) {
            this.defaultName = defaultName;
        }
    }

    public void setDefaultName(String defaultName) {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            return;
        }

        if (isQueryExist(setDefaultName, null, null)) {
            return;
        }

        DefaultNamePacket packet = new DefaultNamePacket(defaultName);
        String packetId = packet.getPacketID();
        PacketFilter idFilter = new PacketIDFilter(packetId);
        BlockResultListener packetListener = new BlockResultListener();
        xmppConnection.addPacketListener(packetListener, idFilter);

        XepQueryInfo queryInfo = new XepQueryInfo(setDefaultName, defaultName);
        addQueryInfo(queryInfo, packetId, packetListener);
        xmppConnection.sendPacket(packet);
    }

    public boolean isBlock(String jid) {
        boolean result = false;
        if (cacheStorage.getAllEntires().size() <= 0) {
            return result;
        }
        for (BlockObject obj : cacheStorage.getAllEntires()) {
            if (obj.getJid().equals(jid)) {
                result = true;
                return result;
            }
        }
        return result;
    }

    private class BlockResultListener implements PacketListener {
        @Override
        public void processPacket(Packet packet) {
            String packetId = packet.getPacketID();
            XepQueryInfo queryInfo = getQueryInfo(packetId);
            if (queryInfo != null) {
                removeQueryInfo(queryInfo, packetId);
                processQueryResponse(packet, queryInfo);
            }
        }
    }

    private void notifyFetchBlockListNameResult(Boolean success) {
        for (BlockListener listener : listeners) {
            listener.notifyFetchBlockListNameResult(success);

        }
    }

    private void notifyFetchBlockResult(Boolean success) {
        for (BlockListener listener : listeners) {
            listener.notifyFetchBlockResult(success);

        }
    }

    private void notifyAddBlockResult(String jid, boolean success) {
        for (BlockListener listener : listeners) {
            listener.notifyAddBlockResult(jid, success);
        }
    }

    private void notifyRemoveBlockResult(String jid, boolean success) {
        for (BlockListener listener : listeners) {
            listener.notifyRemoveBlockResult(jid, success);
        }
    }

    public String getListName() {
        return mListName;
    }

    public void setListName(String mListName) {
        this.mListName = mListName;
    }

    public String getDefaultListName() {
        return mDefaultListName;
    }

    public void setDefaultListName(String mDefaultListName) {
        this.mDefaultListName = mDefaultListName;
    }

    public String getActiveListName() {
        return mActiviteListName;
    }

    public void setActiveListName(String mActiviteListName) {
        this.mActiviteListName = mActiviteListName;
    }
}
