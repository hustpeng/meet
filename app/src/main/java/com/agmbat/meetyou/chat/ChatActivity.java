package com.agmbat.meetyou.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.agmbat.meetyou.data.ContactInfo;


/***
 * 消息聊天界面
 */
public class ChatActivity extends Activity {

    /**
     * key, 用于标识联系人
     */
    private static final String KEY_CONTACT = "contact";

    public static void openChat(Context context, ContactInfo contactInfo) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(KEY_CONTACT, contactInfo.getBareJid());
        context.startActivity(intent);
    }
}
