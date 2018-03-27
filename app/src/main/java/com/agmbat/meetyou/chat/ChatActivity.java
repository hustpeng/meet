package com.agmbat.meetyou.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.agmbat.imsdk.data.ChatMessage;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.db.MeetDatabase;
import com.agmbat.pulltorefresh.view.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;


/***
 * 消息聊天界面
 */
public class ChatActivity extends Activity {

    /**
     * key, 用于标识联系人
     */
    private static final String KEY_CONTACT = "contact";

    /**
     * 显示名称的控件
     */
    private TextView mNameView;

    /**
     * 消息列表
     */
    private PullToRefreshListView mListView;

    /**
     * 填充消息列表的adapter
     */
    private MessageListAdapter mAdapter;

    /**
     * 参与聊天的联系人
     */
    private ContactInfo mParticipant;

    public static void openChat(Context context, ContactInfo contactInfo) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(KEY_CONTACT, contactInfo.getBareJid());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String jid = getIntent().getStringExtra(KEY_CONTACT);
        mParticipant = MeetDatabase.getInstance().findParticipant(jid);
        setupViews();
    }

    private void setupViews() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mNameView = (TextView) findViewById(R.id.name);
        mNameView.setText(mParticipant.getNickName());

        mListView = (PullToRefreshListView) findViewById(R.id.message_list);
        List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
        chatMessages = MeetDatabase.getInstance().getMessage(mParticipant);
        mAdapter = new MessageListAdapter(this, chatMessages);
        mListView.setAdapter(mAdapter);
    }
}
