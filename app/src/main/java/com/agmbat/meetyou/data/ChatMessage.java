package com.agmbat.meetyou.data;

/**
 * 聊天消息
 */
public class ChatMessage {

    private String mContent;
    private long mTimestamp;

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setContent(String content) {
        mContent = content;
    }


    public String getContent() {
        return mContent;
    }
}
