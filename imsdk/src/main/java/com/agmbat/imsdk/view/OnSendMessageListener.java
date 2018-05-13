package com.agmbat.imsdk.view;

import com.agmbat.imsdk.data.body.Body;

public interface OnSendMessageListener {

    /**
     * 发送消息
     *
     * @param message
     */
    public void onSendMessage(Body message);
}