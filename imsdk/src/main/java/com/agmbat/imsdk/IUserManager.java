package com.agmbat.imsdk;

import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.imsdk.user.OnLoadContactGroupListener;

import java.util.List;

/**
 * 提供给UI层接口
 */
public interface IUserManager {

    /**
     * 获取好友申请列表
     *
     * @return
     */
    public List<ContactInfo> getFriendRequestList();

    /**
     * 获取申请人信息
     *
     * @param jid
     * @return
     */
    public ContactInfo getFriendRequest(String jid);

    /**
     * 同意添加自己为好友
     *
     * @param contactInfo
     */
    public void acceptFriend(ContactInfo contactInfo);

    /**
     * 申请添加对方为好友
     *
     * @param contactInfo
     * @return
     */
    boolean requestAddContactToFriend(ContactInfo contactInfo);

    /**
     * 删除好友申请消息
     *
     * @param contactInfo
     */
    void removeFriendRequest(ContactInfo contactInfo);

    /**
     * 加载所有好友分组
     *
     * @param l
     */
    void loadContactGroup(OnLoadContactGroupListener l);

    /**
     * 获取登陆用户信息
     *
     * @return
     */
    public LoginUser getLoginUser();

    /**
     * 保存登陆用户信息
     *
     * @param user
     */
    void saveLoginUser(LoginUser user);
}
