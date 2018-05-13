package org.jivesoftware.smackx.message;

/**
 * 消息状态
 */
public enum MessageObjectStatus {

    READ,
    UNREAD,
    SENDING,
    SEND,
    FAILED,
    LOCATING,
    UPLOADING,
    MSG_HIDDEN;

    public static MessageObjectStatus fromString(String name) {
        try {
            return MessageObjectStatus.valueOf(name);
        } catch (Exception e) {
            return MSG_HIDDEN;
        }
    }
}