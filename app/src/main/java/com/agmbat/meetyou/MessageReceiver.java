package com.agmbat.meetyou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.agmbat.android.utils.NetworkUtil;
import com.agmbat.imsdk.settings.MeetNotificationManager;

import org.jivesoftware.smackx.message.MessageObject;

public class MessageReceiver extends BroadcastReceiver {

    public static final String ACTION_RCV_NEW_MESSAGE = "ACTION_MEET_RCV_NEW_MESSAGE";

    private static MessageReceiver sReceiver = new MessageReceiver();

    public static void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RCV_NEW_MESSAGE);
        context.registerReceiver(sReceiver, filter);
    }

    public static void unregister(Context context) {
        context.unregisterReceiver(sReceiver);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ACTION_RCV_NEW_MESSAGE.equals(intent.getAction())){
            MessageObject messageObject = (MessageObject) intent.getSerializableExtra("new_message");

        }
    }
}
