package org.jivesoftware.smack.roster;

/**
 * 好友关系， both 表示双方互为好友
 */
public enum RosterPacketItemType {

    /**
     * The user and subscriber have no interest in each other's presence.
     * 是用户和自己roster中的好友彼此不关心，既不想把自己的presence状态告诉对方，也不愿意收到对方presence更新消息
     */
    none,

    /**
     * The user is interested in receiving presence updates from the subscriber.
     * 是关心roster中好友的presence状态消息，而不将自己的消息告诉对方
     */
    to,

    /**
     * The subscriber is interested in receiving presence updates from the user.
     * 是只关心，接受对方的状态消息，而不将自己的消息告诉对方
     */
    from,

    /**
     * The user and subscriber have a mutual interest in each other's presence.
     * 即收取对方状态更新，又将自己的更新告知对方
     */
    both,

    /**
     * The user wishes to stop receiving presence updates from the subscriber.
     * 将对方干掉，不再关心他的任何信息
     */
    remove
}