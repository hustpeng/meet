package org.jivesoftware.smack.roster;

import android.text.TextUtils;

import org.jivesoftware.smack.util.XmppStringUtils;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A roster item, which consists of a JID, their name, the type of subscription, and
 * the groups the roster item belongs to.
 */
public class RosterPacketItem {

    /**
     * jid
     */
    private final String user;

    /**
     * 昵称
     */
    private String name;

    /**
     * 好友关系
     */
    private RosterPacketItemType itemType;

    /**
     * 好友关系请求
     */
    private RosterPacketItemStatus itemStatus;

    private final Set<String> groupNames;
    private String avatarId;
    private String personalMsg;
    private double latitude;
    private double longitude;

    /**
     * 好友备注
     */
    private String nickName;
    private boolean isRobot;

    /**
     * Creates a new roster item.
     *
     * @param user the user.
     * @param name the user's name.
     */
    public RosterPacketItem(String user, String name) {
        this.user = user.toLowerCase();
        this.name = name;
        itemType = null;
        itemStatus = null;
        groupNames = new CopyOnWriteArraySet<String>();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * Returns the user.
     *
     * @return the user.
     */
    public String getUser() {
        return user;
    }

    /**
     * Returns the user's name.
     *
     * @return the user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name the user's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the roster item type.
     *
     * @return the roster item type.
     */
    public RosterPacketItemType getItemType() {
        return itemType;
    }

    /**
     * Sets the roster item type.
     *
     * @param itemType the roster item type.
     */
    public void setItemType(RosterPacketItemType itemType) {
        this.itemType = itemType;
    }

    /**
     * Returns the roster item status.
     *
     * @return the roster item status.
     */
    public RosterPacketItemStatus getItemStatus() {
        return itemStatus;
    }

    /**
     * Sets the roster item status.
     *
     * @param itemStatus the roster item status.
     */
    public void setItemStatus(RosterPacketItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }

    /**
     * Returns an unmodifiable set of the group names that the roster item
     * belongs to.
     *
     * @return an unmodifiable set of the group names.
     */
    public Set<String> getGroupNames() {
        return Collections.unmodifiableSet(groupNames);
    }

    /**
     * Adds a group name.
     *
     * @param groupName the group name.
     */
    public void addGroupName(String groupName) {
        groupNames.add(groupName);
    }

    /**
     * Removes a group name.
     *
     * @param groupName the group name.
     */
    public void removeGroupName(String groupName) {
        groupNames.remove(groupName);
    }

    /**
     * @return the avatarId store on server
     */
    public String getAvatarId() {
        return avatarId;
    }

    /**
     * set the avatarId
     *
     * @param avatarId
     */
    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public String getPersonalMsg() {
        return personalMsg;
    }

    public void setPersonalMsg(String personalMsg) {
        this.personalMsg = personalMsg;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double lagitude) {
        this.latitude = lagitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isRobot() {
        return isRobot;
    }

    public void setRobot(boolean isRobot) {
        this.isRobot = isRobot;
    }

    public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<item jid=\"").append(user).append("\"");
        if (name != null) {
            buf.append(" name=\"").append(XmppStringUtils.escapeForXML(name)).append("\"");
        }

        if (!TextUtils.isEmpty(nickName)) {
            buf.append(" nickname=\"").append(nickName).append("\"");
        }

        if (!TextUtils.isEmpty(avatarId)) {
            buf.append(" avatarId=\"").append(avatarId).append("\"");
        }

        if (!TextUtils.isEmpty(personalMsg)) {
            buf.append(" personalMsg=\"").append(personalMsg).append("\"");
        }

        buf.append(" robot=\"").append(isRobot).append("\"");

        if (itemType != null) {
            buf.append(" subscription=\"").append(itemType).append("\"");
        }
        if (itemStatus != null) {
            buf.append(" ask=\"").append(itemStatus).append("\"");
        }

        buf.append(" lat=\"").append(latitude).append("\"");
        buf.append(" lon=\"").append(longitude).append("\"");

        buf.append(">");
        for (String groupName : groupNames) {
            buf.append("<group>").append(XmppStringUtils.escapeForXML(groupName)).append("</group>");
        }
        buf.append("</item>");
        return buf.toString();
    }
}
