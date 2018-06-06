package org.jivesoftware.smack.roster;

/**
 * 好友关系， both 表示双方互为好友
 */
public enum RosterPacketItemType {

    /**
     * The user and subscriber have no interest in each other's presence.
     */
    none,

    /**
     * The user is interested in receiving presence updates from the subscriber.
     */
    to,

    /**
     * The subscriber is interested in receiving presence updates from the user.
     */
    from,

    /**
     * The user and subscriber have a mutual interest in each other's presence.
     */
    both,

    /**
     * The user wishes to stop receiving presence updates from the subscriber.
     */
    remove
}