package com.agmbat.imsdk.settings;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.agmbat.imsdk.R;
import com.agmbat.imsdk.util.AppConfigUtils;
import com.agmbat.imsdk.util.SystemUtil;
import com.agmbat.text.uri.Uri;

import org.jivesoftware.smackx.message.MessageObject;

import static com.agmbat.android.AppResources.getResources;

public class MeetNotificationManager {

    private NotificationManager mNotificationManager;
    private static MeetNotificationManager sInstance;
    private String mNewMsgEntranceClass;
    private Context mContext;

    private MeetNotificationManager(Context context){
        mContext = context;
        mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static MeetNotificationManager init(Context context){
       if(null == sInstance){
           sInstance = new MeetNotificationManager(context);
       }
       return sInstance;
    }

    public static MeetNotificationManager getInstance(){
        return sInstance;
    }

    public void configNewMsgEntrance(String className){
        mNewMsgEntranceClass = className;
    }


    public void notifyMessageReceived(MessageObject messageObject) {
        if(!AppConfigUtils.isNotificationEnable(mContext) || !SystemUtil.isAppBackground(mContext)){
            return;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        try {
            Intent intent = new Intent();
            intent.setClass(mContext, Class.forName(mNewMsgEntranceClass));
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext,0,intent,0);
            builder.setContentIntent(pendingIntent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        builder.setAutoCancel(true);
        builder.setContentTitle(messageObject.getBody());
        builder.setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        if(AppConfigUtils.isNotificationSoundEnable(mContext)) {
            notification.sound = RingtoneManager.getActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE);
        }
        mNotificationManager.notify(messageObject.getSenderJid().hashCode(),notification);
    }


}
