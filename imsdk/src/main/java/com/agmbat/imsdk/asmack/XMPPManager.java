package com.agmbat.imsdk.asmack;

import android.content.Context;
import android.text.TextUtils;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.login.RegisterInfo;
import com.agmbat.imsdk.util.AppConfigUtils;
import com.agmbat.imsdk.util.LocationAutoSync;
import com.agmbat.imsdk.util.LocationHelper;
import com.agmbat.log.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.block.BlockManager;
import org.jivesoftware.smackx.block.BlockProvider;
import org.jivesoftware.smackx.favoritedme.FavoritedMeManager;
import org.jivesoftware.smackx.favoritedme.FavoritedMeProvider;
import org.jivesoftware.smackx.favorites.FavoritesManager;
import org.jivesoftware.smackx.favorites.FavoritesProvider;
import org.jivesoftware.smackx.findserver.FindServerListener;
import org.jivesoftware.smackx.findserver.FindServerManager;
import org.jivesoftware.smackx.findserver.FindServerObject;
import org.jivesoftware.smackx.findserver.FindServerProvider;
import org.jivesoftware.smackx.location.LocateManager;
import org.jivesoftware.smackx.location.LocateProvider;
import org.jivesoftware.smackx.message.MessageHtmlProvider;
import org.jivesoftware.smackx.message.MessageManager;
import org.jivesoftware.smackx.message.MessageRelationProvider;
import org.jivesoftware.smackx.paid.PaidAccountProvider;
import org.jivesoftware.smackx.paid.PaidManager;
import org.jivesoftware.smackx.paid.PaidPageInfoProvider;
import org.jivesoftware.smackx.permit.PermitManager;
import org.jivesoftware.smackx.permit.PermitProvider;
import org.jivesoftware.smackx.token.TokenManager;
import org.jivesoftware.smackx.token.TokenProvider;
import org.jivesoftware.smackx.vcard.VCardManager;
import org.jivesoftware.smackx.vcard.VCardProvider;
import org.jivesoftware.smackx.vcardextend.VCardExtendManager;
import org.jivesoftware.smackx.vcardextend.VCardExtendProvider;
import org.jivesoftware.smackx.visitor.VisitorManager;
import org.jivesoftware.smackx.visitor.VisitorMeProvider;

import java.util.HashMap;
import java.util.Map;

public class XMPPManager {

    private static XMPPManager sInstance;

    private XMPPConnection xmppConnection;

    private BlockManager blockManager;
    private PermitManager permitManager;
    private FavoritesManager favoritesManager;
    private FavoritedMeManager favoritedMeManager;
    private VCardManager vCardManager;
    private VCardExtendManager vCardExtendManager;
    private PaidManager paidManager;
    private FindServerManager findServerManager;
    private TokenManager tokenManager;
    private LocateManager locateManager;
    private VisitorManager visitorManager;
    private MessageManager messageManager;
    private LocationHelper locationHelper;
    private LocationAutoSync locationAutoSync;
    private ReconnectionManager reconnectionManager;

    // 47.106.77.125
    private static final String URL = "http://yuan520.com";

    public static synchronized XMPPManager getInstance() {
        if (sInstance == null) {
            sInstance = new XMPPManager();
        }
        return sInstance;
    }

    private XMPPManager() {
        configureProviderManager();
        String tigaseServerAddress = "yuan520.com";
        ConnectionConfiguration configuration = initConnectionConfig(tigaseServerAddress, 5222);
        xmppConnection = new XMPPConnection(configuration);

        reconnectionManager = new ReconnectionManager(xmppConnection);
        favoritesManager = new FavoritesManager(xmppConnection);
        permitManager = new PermitManager(xmppConnection);
        blockManager = new BlockManager(xmppConnection);
        favoritedMeManager = new FavoritedMeManager(xmppConnection);
        vCardManager = new VCardManager(xmppConnection);
        vCardExtendManager = new VCardExtendManager(xmppConnection);
        findServerManager = new FindServerManager(xmppConnection);
        tokenManager = new TokenManager(xmppConnection);
        locateManager = new LocateManager(xmppConnection);
        visitorManager = new VisitorManager(xmppConnection);
        messageManager = new MessageManager(xmppConnection);
        paidManager = new PaidManager(xmppConnection);

        locationHelper = new LocationHelper();
        locationAutoSync = new LocationAutoSync();

        findServerManager.addListener(new FindServerListener() {
            @Override
            public void notifyFindServersResult(FindServerObject serverObject) {
                Log.i("XMPPManager", "notifyFindServersResult = " + serverObject);
                findServerManager.removeListener(this);
                if (serverObject != null) {
                    getTokenManager().setTokenServer(serverObject.getTokenServer());
                    getPaidManager().setPaidServer(serverObject.getPaidServer());
                }
            }
        });
        reconnectionManager.autoLogin();
    }

    private void configureProviderManager() {
        ProviderManager pm = ProviderManager.getInstance();

        pm.addIQProvider(FindServerProvider.elementName(), FindServerProvider.namespace(),
                new FindServerProvider());
        pm.addIQProvider(FavoritesProvider.elementName(), FavoritesProvider.namespace(),
                new FavoritesProvider());
        pm.addIQProvider(FavoritedMeProvider.elementName(), FavoritedMeProvider.namespace(),
                new FavoritedMeProvider());
        pm.addIQProvider(TokenProvider.elementName(), TokenProvider.namespace(),
                new TokenProvider());
        pm.addIQProvider(VisitorMeProvider.elementName(), VisitorMeProvider.namespace(),
                new VisitorMeProvider());
        pm.addIQProvider(VCardProvider.elementName(), VCardProvider.namespace(),
                new VCardProvider());
        pm.addIQProvider(VCardExtendProvider.elementName(), VCardExtendProvider.namespace(),
                new VCardExtendProvider());
        pm.addIQProvider(LocateProvider.elementName(), LocateProvider.namespace(),
                new LocateProvider());
        pm.addIQProvider(PermitProvider.elementName(), PermitProvider.namespace(),
                new PermitProvider());
        pm.addIQProvider(BlockProvider.elementName(), BlockProvider.namespace(),
                new BlockProvider());
        pm.addIQProvider(PaidAccountProvider.elementName(), PaidAccountProvider.namespace(),
                new PaidAccountProvider());
        pm.addIQProvider(PaidPageInfoProvider.elementName(), PaidPageInfoProvider.namespace(),
                new PaidPageInfoProvider());

        pm.addExtensionProvider(MessageHtmlProvider.elementName(), MessageHtmlProvider.namespace(),
                new MessageHtmlProvider());
        pm.addExtensionProvider(MessageRelationProvider.elementName(),
                MessageRelationProvider.namespace(), new MessageRelationProvider());
    }

    private ConnectionConfiguration initConnectionConfig(String host, int port) {
        SmackConfiguration.setPacketReplyTimeout(30000);// 所有跟服务器的延迟请求时间
        SmackConfiguration.setKeepAliveInterval(120000);// 设置心跳时间间隔2分钟
        ConnectionConfiguration config = new ConnectionConfiguration(host, port);
        config.setDebuggerEnabled(true);
        config.setSendPresence(true);
        config.setSecurityMode(SecurityMode.disabled);
        config.setCompressionEnabled(false);
        config.setReconnectionAllowed(false);
        config.setRosterLoadedAtLogin(false);
        return config;
    }

    /**
     * 切换服务器，重连时可以使用
     *
     * @throws XMPPException
     */
    public void connect() throws XMPPException {
        if (xmppConnection.isConnected()) {
            xmppConnection.disconnect();
        }
        xmppConnection.connect();
    }

    /**
     * 自动根据当前的server地址先进行连接。 阻塞调用线程，建议在异步线程中调用。
     *
     * @param userName
     * @param password
     * @throws XMPPException
     */
    public void signIn(String userName, String password) throws XMPPException {
        connect();
        login(userName, password);
    }

    public void login(String userName, String password) throws XMPPException {
        xmppConnection.login(userName, password);
        Context context = AppResources.getAppContext();
        AppConfigUtils.saveNormalLoginInfo(context, userName, password);
        xmppConnection.getConfiguration().setReconnectionAllowed(true);
    }

    public void logout() {
        Context context = AppResources.getAppContext();
        AppConfigUtils.setPassword(context, null);
        xmppConnection.getConfiguration().setReconnectionAllowed(false);
        xmppConnection.disconnect();
    }

    /**
     * 自动根据当前的server地址先进行连接。 阻塞调用线程，建议在异步线程中调用。 signup完成后,自动sign in
     *
     * @param registerInfo
     * @param needSignIn   注册成功后自动登录
     * @throws XMPPException
     */
    public void signUp(RegisterInfo registerInfo, boolean needSignIn) throws XMPPException {
        connect();
        String userName = registerInfo.getUserName();
        String password = registerInfo.getPassword();
        Map<String, String> map = new HashMap<>();
        map.put("username", userName);
        map.put("password", password);
        map.put("nickname", registerInfo.getNickName());
        map.put("gender", String.valueOf(registerInfo.getGender()));
        map.put("birth", registerInfo.getBirthYear());
        map.put("invite", registerInfo.getInviteCode());
        xmppConnection.getAccountManager().createAccount(map);
        if (needSignIn) {
            login(userName, password);
        }
    }

    /**
     * 必须在登录后才能调用。 阻塞调用线程，建议在异步线程中调用。
     *
     * @param oldPassword
     * @param newPassword
     * @throws XMPPException
     */
    public void changePassword(String oldPassword, String newPassword) throws XMPPException {
        if (!xmppConnection.isAuthenticated() || xmppConnection.isAnonymous()) {
            throw new XMPPException("not login in yet");
        }

        if (TextUtils.isEmpty(newPassword)) {
            throw new XMPPException("invaild password");
        }

        if (!oldPassword.equals(xmppConnection.getConfiguration().getPassword())) {
            throw new XMPPException("old password dismatch");
        }

        xmppConnection.getAccountManager().changePassword(newPassword);
    }

    public boolean isLogin() {
        return xmppConnection.isAuthenticated() && !xmppConnection.isAnonymous();
    }

    public XMPPConnection getXmppConnection() {
        return xmppConnection;
    }

    public VCardManager getvCardManager() {
        return vCardManager;
    }

    public FavoritesManager getFavoritesManager() {
        return favoritesManager;
    }

    public PermitManager getPermitManager() {
        return permitManager;
    }

    public BlockManager getBlockManager() {
        return blockManager;
    }

    public VCardExtendManager getvCardExtendManager() {
        return vCardExtendManager;
    }

    public FindServerManager getFindServerManager() {
        return findServerManager;
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    public LocateManager getLocateManager() {
        return locateManager;
    }

    public FavoritedMeManager getFavoritedMeManager() {
        return favoritedMeManager;
    }

    public VisitorManager getVisitorManager() {
        return visitorManager;
    }

    public LocationHelper getLocationHelper() {
        return locationHelper;
    }

    public LocationAutoSync getLocationAutoSync() {
        return locationAutoSync;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ReconnectionManager getReconnectionManager() {
        return reconnectionManager;
    }

    public PaidManager getPaidManager() {
        return paidManager;
    }
}
