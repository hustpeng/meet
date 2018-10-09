package com.agmbat.meetyou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.agmbat.android.utils.NetworkUtil;
import com.agmbat.imsdk.settings.MeetNotificationManager;
import com.agmbat.meetyou.splash.SplashActivity;
import com.agmbat.meetyou.tab.msg.SysMsgActivity;

import org.jivesoftware.smackx.message.MessageObject;

public class MessageReceiver extends BroadcastReceiver {

    private static MessageReceiver sReceiver = new MessageReceiver();

    public static void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MeetNotificationManager.ACTION_RCV_NEW_MESSAGE);
        context.registerReceiver(sReceiver, filter);
    }

    public static void unregister(Context context) {
        context.unregisterReceiver(sReceiver);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(MeetNotificationManager.ACTION_RCV_NEW_MESSAGE.equals(intent.getAction())){
            MessageObject messageObject = (MessageObject) intent.getSerializableExtra(MeetNotificationManager.EXTRA_NEW_MESSAGE);

            Intent launchIntent = new Intent(context, SplashActivity.class);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(messageObject.getFromJid().equals("support@yuan520.com")){ //系统消息
                launchIntent.putExtra(MainTabActivity.EXTRA_MAIN_TAB_INDEX, MainTabActivity.TAB_INDEX_PROFILE);
                launchIntent.putExtra(MainTabActivity.EXTRA_LAUNCH_ACTIVITY, SysMsgActivity.class.getName());
            }else{
                launchIntent.putExtra(MainTabActivity.EXTRA_MAIN_TAB_INDEX, MainTabActivity.TAB_INDEX_MSG);
            }
            context.startActivity(launchIntent);
        }
    }
}
