package com.agmbat.meetyou.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.emoji.panel.p2.EmojiPanel;
import com.agmbat.emoji.panel.p2.EmojiPanelConfig;
import com.agmbat.emoji.res.DefEmoticons;
import com.agmbat.emoji.res.DefXhsEmoticons;
import com.agmbat.imsdk.asmack.ContactManager;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.data.body.Body;
import com.agmbat.imsdk.data.body.TextBody;
import com.agmbat.imsdk.imevent.ReceiveMessageEvent;
import com.agmbat.imsdk.imevent.SendMessageEvent;
import com.agmbat.imsdk.remotefile.OnFileUploadListener2;
import com.agmbat.imsdk.remotefile.RemoteFileManager;
import com.agmbat.imsdk.remotefile.TempFileApiResult;
import com.agmbat.input.InputController;
import com.agmbat.input.InputView;
import com.agmbat.input.OnInputListener;
import com.agmbat.input.VoiceInputController;
import com.agmbat.meetyou.R;
import com.agmbat.pulltorefresh.view.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.message.MessageObject;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * 消息聊天界面
 */
public class ChatActivity extends Activity implements OnInputListener {

    /**
     * key, 用于标识联系人
     */
    private static final String KEY_CONTACT = "contact";

    /**
     * 显示名称的控件
     */
    @BindView(R.id.nickname)
    TextView mNicknameView;


    @BindView(R.id.input)
    InputView mInputView;

    /**
     * 消息列表
     */
    @BindView(R.id.message_list)
    PullToRefreshListView mPtrView;

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
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        String jid = getIntent().getStringExtra(KEY_CONTACT);
        mParticipant = ContactManager.getContactInfo(jid);
        setupViews();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ReceiveMessageEvent event) {
        if (event.getMessageObject().getSenderJid().equals(mParticipant.getBareJid())) {
            mAdapter.notifyDataSetChanged();
//            autoScrollToLast();
        }
    }

    private InputController mInputController;
    private VoiceInputController mVoiceInputController;

    private void setupViews() {
        mNicknameView.setText(mParticipant.getNickName());

        mInputController = new InputController(mInputView);
        mInputController.setOnInputListener(this);
        mVoiceInputController = new VoiceInputController(mInputView.getVoiceButton());
        mVoiceInputController.setOnInputListener(this);

        // 配置 emoji 面板
        EmojiPanelConfig config = new EmojiPanelConfig();
        config.setEditText(mInputView.getEditText());
        config.addEmojiResProvider(new DefEmoticons());
        config.addEmojiResProvider(new DefXhsEmoticons());

        EmojiPanel panel = new EmojiPanel(this);
        panel.setConfig(config);
        mInputController.addEmojiPanel(panel);

        mInputController.setContentView(mPtrView);


//        mInputView.setOnSendMessageListener(new OnSendMessageListener() {
//            @Override
//            public void onSendMessage(Body message) {
//                sendTextMsg(message);
//            }
//        });
        List<MessageObject> chatMessages = XMPPManager.getInstance().getMessageManager().getMessageList(mParticipant.getBareJid());
        mAdapter = new MessageListAdapter(this, chatMessages);
        mPtrView.setAdapter(mAdapter);
//        autoScrollToLast();
    }

    private void sendTextMsg(Body message) {
        TextBody textBody = (TextBody) message;
        String text = textBody.getContent();
        if (!TextUtils.isEmpty(text)) {
            MessageObject messageObject = XMPPManager.getInstance().getMessageManager()
                    .sendTextMessage(mParticipant.getBareJid(), mParticipant.getNickName(), text);
            mAdapter.notifyDataSetChanged();
//            autoScrollToLast();
            if (messageObject != null) {
                EventBus.getDefault().post(new SendMessageEvent(messageObject));
            }
        }
    }

//    private void autoScrollToLast() {
//        mPtrView.post(new Runnable() {
//            @Override
//            public void run() {
//                ListView lv = mPtrView.getRefreshableView();
//                lv.setSelection(lv.getAdapter().getCount() - 1);
//            }
//        });
//    }

    @Override
    public void onInput(int type, String content) {
        if (type == OnInputListener.TYPE_TEXT) {
            MessageObject messageObject = XMPPManager.getInstance().getMessageManager()
                    .sendTextMessage(mParticipant.getBareJid(), mParticipant.getNickName(), content);
            mAdapter.notifyDataSetChanged();
            if (messageObject != null) {
                EventBus.getDefault().post(new SendMessageEvent(messageObject));
            }
        } else if (type == OnInputListener.TYPE_VOICE) {
            String path = content;
            RemoteFileManager.uploadVoiceFile(new File(path), new OnFileUploadListener2() {
                @Override
                public void onUpload(TempFileApiResult apiResult) {
                    if (apiResult.mResult) {
                        String url = apiResult.url;
                        MessageObject messageObject = XMPPManager.getInstance().getMessageManager()
                                .sendVoiceMessage(mParticipant.getBareJid(), mParticipant.getNickName(), url);
                        mAdapter.notifyDataSetChanged();
                        if (messageObject != null) {
                            EventBus.getDefault().post(new SendMessageEvent(messageObject));
                        }
                    }
                }
            });
        }
    }
}
