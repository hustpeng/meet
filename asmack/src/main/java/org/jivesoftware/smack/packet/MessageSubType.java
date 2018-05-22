package org.jivesoftware.smack.packet;

/**
 * 聊天消息类型
 */
public enum MessageSubType {

    text,
    image,
    geoloc,
    voice;

    public static MessageSubType fromString(String name) {
        try {
            return MessageSubType.valueOf(name);
        } catch (Exception e) {
            return text;
        }
    }

    public static MessageSubType of(int subType) {
        switch (subType) {
            case 0:
                return text;
            case 1:
                return image;
            case 2:
                return geoloc;
            case 3:
                return voice;
            default:
                return text;
        }
    }
}