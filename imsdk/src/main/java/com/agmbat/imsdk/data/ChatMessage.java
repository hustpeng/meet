package com.agmbat.imsdk.data;

import com.agmbat.imsdk.data.body.Body;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 聊天消息
 */
public class ChatMessage {

    private static final SimpleDateFormat SHOW_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * this message is sent from others
     **/
    public static final int DIRECTION_FROM_OTHERS = 0;

    /**
     * this message is sent from me
     **/
    public static final int DIRECTION_TO_OTHERS = 1;


    private String mContent;

    /**
     * 消息内容
     */
    private Body mBody;

    /**
     * 消息时间
     */
    private long mTimestamp;

    /**
     * Indicate whether this message is send from me or others
     */
    private int mMsgDirection = DIRECTION_FROM_OTHERS;

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

    public int getMsgDirection() {
        return mMsgDirection;
    }

    public void setMsgDirection(int direction) {
        mMsgDirection = direction;
    }

    /**
     * 获取显示的日期
     *
     * @return
     */
    public String getShowTimeText() {
        return SHOW_TIME_FORMAT.format(new Date(mTimestamp));
    }

    /**
     * 获取图像地址
     *
     * @return
     */
    public String getAvatarBareAddress() {
        String key = null;
        if (mMsgDirection == DIRECTION_TO_OTHERS) {
        } else if (mMsgDirection == DIRECTION_FROM_OTHERS) {
        }
        return key;
    }

    /**
     * 获取消息内容
     *
     * @return
     */
    public Body getBody() {
        return mBody;
    }
}
