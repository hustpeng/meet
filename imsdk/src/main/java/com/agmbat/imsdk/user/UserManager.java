package com.agmbat.imsdk.user;

import com.agmbat.imsdk.IUserManager;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.asmack.api.OnFetchLoginUserListener;
import com.agmbat.imsdk.asmack.api.OnSaveUserInfoListener;
import com.agmbat.imsdk.asmack.api.XMPPApi;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.db.MeetDatabase;
import com.agmbat.imsdk.imevent.LoginUserUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提供给UI层, 用于用户信息管理
 * 用户管理
 */
public class UserManager implements IUserManager {

    /**
     * 当前登陆用户
     */
    private LoginUser mLoginUser;

    /**
     * 好友申请列表
     */
    private List<ContactInfo> mFriendRequestList;


    private static final UserManager INSTANCE = new UserManager();

    public static UserManager getInstance() {
        return INSTANCE;
    }

    private static final String TAG = "xx";


    private UserManager() {
        XMPPManager.getInstance().getRosterManager();


        Connection.addConnectionCreationListener(new ConnectionCreationListener() {

            @Override
            public void connectionCreated(Connection connection) {
                connection.addConnectionListener(new ConnectionListener() {
                    @Override
                    public void loginSuccessful() {
                        // 登陆成功后刷新登陆用户信息
                        refreshLoginUserInfo();
                    }

                    @Override
                    public void connectionClosed() {

                    }

                    @Override
                    public void connectionClosedOnError(Exception e) {

                    }
                });
            }
        });
    }

    /**
     * 刷新登陆用户信息
     */
    private void refreshLoginUserInfo() {
        String loginUserJid = XMPPManager.getInstance().getXmppConnection().getBareJid();
        XMPPApi.fetchLoginUser(loginUserJid, new OnFetchLoginUserListener() {
            @Override
            public void onFetchLoginUser(LoginUser user) {
                mLoginUser = user;
                EventBus.getDefault().post(new LoginUserUpdateEvent(mLoginUser));
            }

        });
    }

    /**
     * 添加申请用户列表, 此方法不对外暴露
     *
     * @param willToAdd
     */
    public void addFriendRequest(ContactInfo willToAdd) {
        MeetDatabase.getInstance().saveFriendRequest(willToAdd);
        ContactInfo exist = getFriendRequest(willToAdd.getBareJid());
        if (exist == null) {
            mFriendRequestList.add(willToAdd);
        }
    }

    public void removeFriendRequest(ContactInfo contactInfo) {
        MeetDatabase.getInstance().deleteFriendRequest(contactInfo);
        ContactInfo exist = getFriendRequest(contactInfo.getBareJid());
        if (exist != null) {
            mFriendRequestList.remove(exist);
        }
    }

    /**
     * 对UI层
     *
     * @return
     */
    public List<ContactInfo> getFriendRequestList() {
        if (mFriendRequestList == null) {
            mFriendRequestList = MeetDatabase.getInstance().getFriendRequestList();
        }
        return mFriendRequestList;
    }

    /**
     * 申请人信息
     *
     * @param jid
     * @return
     */
    @Override
    public ContactInfo getFriendRequest(String jid) {
        List<ContactInfo> list = getFriendRequestList();
        for (ContactInfo contactInfo : list) {
            if (contactInfo.getBareJid().equals(jid)) {
                return contactInfo;
            }
        }
        return null;
    }


    @Override
    public LoginUser getLoginUser() {
        return mLoginUser;
    }


    @Override
    public void saveLoginUser(LoginUser user) {
        // 先通知UI变化
        EventBus.getDefault().post(new LoginUserUpdateEvent(user));
        XMPPApi.saveUserInfo(user, new OnSaveUserInfoListener() {
            @Override
            public void onSaveUserInfo(LoginUser user) {
                EventBus.getDefault().post(new LoginUserUpdateEvent(user));
            }
        });
    }


    /**
     * 联系人缓存
     */
    private Map<String, ContactInfo> mContactCache = new HashMap<>();

    /**
     * 获取联系人
     *
     * @param jid
     * @return
     */
    public ContactInfo getContactFromCache(String jid) {
        return mContactCache.get(jid);
    }

    /**
     * 添加到缓存中, 只于Activity共同访问数据
     *
     * @param contactInfo
     */
    public void addContactToCache(ContactInfo contactInfo) {
        mContactCache.put(contactInfo.getBareJid(), contactInfo);
    }
}
