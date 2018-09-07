package com.agmbat.meetyou.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.media.AmrHelper;
import com.agmbat.android.media.AudioPlayer;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.UrlStringUtils;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.app.AppFileManager;
import com.agmbat.emoji.panel.p2.EmojiPanel;
import com.agmbat.emoji.panel.p2.EmojiPanelConfig;
import com.agmbat.emoji.res.DefEmoticons;
import com.agmbat.emoji.res.DefXhsEmoticons;
import com.agmbat.file.FileUtils;
import com.agmbat.filepicker.FilePicker;
import com.agmbat.filepicker.OnPickFileListener;
import com.agmbat.imagepicker.ImagePickerHelper;
import com.agmbat.imagepicker.OnPickImageListener;
import com.agmbat.imagepicker.OnPickMultiImageListener;
import com.agmbat.imagepicker.bean.ImageItem;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.imsdk.chat.body.AudioBody;
import com.agmbat.imsdk.chat.body.Body;
import com.agmbat.imsdk.chat.body.BodyParser;
import com.agmbat.imsdk.chat.body.FileBody;
import com.agmbat.imsdk.chat.body.ImageBody;
import com.agmbat.imsdk.chat.body.LocationBody;
import com.agmbat.imsdk.chat.body.TextBody;
import com.agmbat.imsdk.chat.body.UrlBody;
import com.agmbat.imsdk.group.CircleInfo;
import com.agmbat.imsdk.group.GroupChatReply;
import com.agmbat.imsdk.imevent.ReceiveMessageEvent;
import com.agmbat.imsdk.imevent.SendMessageEvent;
import com.agmbat.imsdk.remotefile.FileApiResult;
import com.agmbat.imsdk.remotefile.OnFileUploadListener;
import com.agmbat.imsdk.remotefile.RemoteFileManager;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.input.InputController;
import com.agmbat.input.InputView;
import com.agmbat.input.OnInputListener;
import com.agmbat.input.VoiceInputController;
import com.agmbat.isdialog.ISLoadingDialog;
import com.agmbat.map.LocationCallback;
import com.agmbat.map.LocationObject;
import com.agmbat.map.Maps;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.group.EditGroupEvent;
import com.agmbat.meetyou.group.GroupInfoActivity;
import com.agmbat.meetyou.group.RemoveGroupEvent;
import com.agmbat.meetyou.splash.SplashStore;
import com.agmbat.meetyou.util.SystemUtil;
import com.agmbat.menu.MenuInfo;
import com.agmbat.menu.OnClickMenuListener;
import com.agmbat.net.HttpUtils;
import com.agmbat.pulltorefresh.view.PullToRefreshListView;
import com.agmbat.text.TagText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.message.MessageObject;
import org.jivesoftware.smackx.message.MessageObjectStatus;
import org.jivesoftware.smackx.message.MessageStorage;

import java.io.File;
import java.util.ArrayList;
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
    private static final String KEY_GROUP = "group";

    public static final String KEY_CHAT_TYPE = "chat_type";

    public static final int TYPE_GROUP_CHAT = 1;
    public static final int TYPE_SINGLE_CHAT = 0;

    /**
     * 显示名称的控件
     */
    @BindView(R.id.nickname)
    TextView mNicknameView;


    /**
     * 输入控件
     */
    @BindView(R.id.input)
    InputView mInputView;

    /**
     * 消息列表
     */
    @BindView(R.id.message_list)
    PullToRefreshListView mPtrView;

    @BindView(R.id.btn_profile)
    ImageView mBtnProfile;

    /**
     * 填充消息列表的adapter
     */
    private MessageListAdapter mAdapter;

    /**
     * 参与聊天的联系人
     */
    private ContactInfo mParticipant;

    /**
     * 聊天群
     */
    private CircleInfo mCircleInfo;

    private int mChatType;

    private LoginUser mLoginUser;

    private InputController mInputController;
    private VoiceInputController mVoiceInputController;
    private MessageStorage messageStorage = new MessageStorage();

    private AudioPlayer.OnPlayListener mOnPlayListener = new AudioPlayer.OnPlayListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onPlaying(int position, int duration) {
        }

        @Override
        public void onPlay() {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onPause() {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    };


    public static void openChat(Context context, ContactInfo contactInfo) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(KEY_CONTACT, contactInfo);
        intent.putExtra(KEY_CHAT_TYPE, TYPE_SINGLE_CHAT);
        context.startActivity(intent);
    }

    public static void openGroupChat(Context context, CircleInfo circleInfo) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(KEY_GROUP, circleInfo);
        intent.putExtra(KEY_CHAT_TYPE, TYPE_GROUP_CHAT);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        mChatType = getIntent().getIntExtra(KEY_CHAT_TYPE, TYPE_SINGLE_CHAT);
        if (mChatType == TYPE_SINGLE_CHAT) {
            ContactInfo contactInfo = (ContactInfo) getIntent().getSerializableExtra(KEY_CONTACT);
            mParticipant = XMPPManager.getInstance().getRosterManager().getContactFromMemCache(contactInfo.getBareJid());
        } else if (mChatType == TYPE_GROUP_CHAT) {
            mCircleInfo = (CircleInfo) getIntent().getSerializableExtra(KEY_GROUP);
        }

        mLoginUser = XMPPManager.getInstance().getRosterManager().getLoginUser();
        setupViews();
        EventBus.getDefault().register(this);
        AudioPlayer.getDefault().addListener(mOnPlayListener);

//        PacketFilter packetFilter = new PacketTypeFilter(GroupChatReply.class);
//        XMPPManager.getInstance().getXmppConnection().addPacketListener(mGroupChatListener, packetFilter);
//        if (mChatType == TYPE_GROUP_CHAT) {
//            List<MessageObject> cacheMessages = messageStorage.getAllMessage(mCircleInfo.getGroupJid());
//            if (cacheMessages.size() == 0) {
//                QueryGroupChatIQ queryGroupChatIQ = new QueryGroupChatIQ();
//                queryGroupChatIQ.setTo(mCircleInfo.getGroupJid());
//                queryGroupChatIQ.setType(IQ.Type.GET);
//                XMPPManager.getInstance().getXmppConnection().sendPacket(queryGroupChatIQ);
//            }
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        AudioPlayer.getDefault().removeListener(mOnPlayListener);
        AudioPlayer.getDefault().pause();
        XMPPManager.getInstance().getXmppConnection().removePacketListener(mGroupChatListener);
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
        String participantJid = "";
        if (mChatType == TYPE_SINGLE_CHAT) {
            participantJid = mParticipant.getBareJid();
        } else if (mChatType == TYPE_GROUP_CHAT) {
            participantJid = mCircleInfo.getGroupJid();
        }
        if (event.getMessageObject().getFromJid().equals(participantJid)) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setupViews() {
        if (mChatType == TYPE_SINGLE_CHAT) {
            mNicknameView.setText(mParticipant.getNickName());
        } else if (mChatType == TYPE_GROUP_CHAT) {
            mNicknameView.setText(mCircleInfo.getName());
        }
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

        initOtherPanel();
        mInputController.setContentView(mPtrView);


//        mInputView.setOnSendMessageListener(new OnSendMessageListener() {
//            @Override
//            public void onSendMessage(Body message) {
//                sendTextMsg(message);
//            }
//        });
        String bareJid = "";
        if (mChatType == TYPE_SINGLE_CHAT) {
            bareJid = mParticipant.getBareJid();
        } else if (mChatType == TYPE_GROUP_CHAT) {
            bareJid = mCircleInfo.getGroupJid();
        }
        List<MessageObject> chatMessages = XMPPManager.getInstance().getMessageManager().getMessageList(bareJid);
        mAdapter = new MessageListAdapter(this, chatMessages);
        mAdapter.setOnContentLongClickListener(mOnItemLongClickListener);
        mPtrView.setOnScrollListener(mOnScrollListener);
        mPtrView.setAdapter(mAdapter);
    }

    private MessageListAdapter.OnContentLongClickListener mOnItemLongClickListener = new MessageListAdapter.OnContentLongClickListener() {
        @Override
        public void onLongClick(final int position, MessageView messageView) {
            final MessageObject messageObject = mAdapter.getItem(position);
            String bodyText = messageObject.getBody();
            String[] operations = null;
            final Body body = BodyParser.parse(bodyText);
            if (body instanceof TextBody) {
                operations = new String[]{"复制", "删除"};
            } else {
                operations = new String[]{"删除"};
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);

            builder.setItems(operations, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        if (body instanceof TextBody) {
                            TextBody textBody = (TextBody) body;
                            SystemUtil.copyToClipBoard(getBaseContext(), "chat", textBody.getContent());
                            ToastUtil.showToast("已复制");
                        } else {
                            mAdapter.remove(messageObject);
                            XMPPManager.getInstance().getMessageManager().deleteMessage(messageObject.getMsgId());
                        }
                    } else if (which == 1) {
                        mAdapter.remove(messageObject);
                        XMPPManager.getInstance().getMessageManager().deleteMessage(messageObject.getMsgId());
                    }
                }
            });
            builder.create().show();
        }
    };

    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {


        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int lastIndex = firstVisibleItem + visibleItemCount;
            for (int i = firstVisibleItem; i < lastIndex; i++) {
                if (i == 0 || i == lastIndex - 1) {
                    continue;
                }
                MessageObject messageObject = mAdapter.getItem(i - 1);
                MessageObjectStatus oldStatus = messageObject.getMsgStatus();
                if (oldStatus != MessageObjectStatus.READ) {
                    XMPPManager.getInstance().getMessageManager().updateMsgStatus(messageObject.getMsgId(), MessageObjectStatus.READ);
                }
            }
        }
    };

    private void initOtherPanel() {
        List<MenuInfo> beans = new ArrayList<>();
        beans.add(createMenuInfo(R.mipmap.icon_photo, "图片", new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {
                mInputController.reset();
                selectPicture();
            }
        }));
        beans.add(createMenuInfo(R.mipmap.icon_camera, "拍照", new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {
                mInputController.reset();
                takePicture();
            }
        }));
        beans.add(createMenuInfo(R.mipmap.icon_file, "文件", new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {
                mInputController.reset();
                selectFileFromLocal();
            }
        }));
        beans.add(createMenuInfo(R.mipmap.icon_loaction, "位置", new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {
                getLocation();
            }
        }));
        beans.add(createMenuInfo(R.mipmap.icon_blank, "", new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {

            }
        }));
        SimpleAppsGridView gridView = new SimpleAppsGridView(this);
        gridView.addMenuList(beans);
        mInputController.addOtherPanel(gridView);
    }

    private MenuInfo createMenuInfo(int icon, String title, OnClickMenuListener l) {
        MenuInfo menuInfo = new MenuInfo(icon, title);
        menuInfo.setOnClickMenuListener(l);
        return menuInfo;
    }

    private void getLocation() {
        //先发送空消息
        //LocationObject locationObject = new LocationObject();
        //final Body emptyBody = new LocationBody(locationObject);
        //final MessageObject emptyMessage = sendMessage(emptyBody, false, "");
        Maps.getLocation(this, new LocationCallback() {
            @Override
            public void callback(LocationObject location) {
                if (location == null) {
                    ToastUtil.showToast("获取位置失败");
                    return;
                }
                mInputController.reset();
                LocationBody body = new LocationBody(location);
                //if (null != emptyMessage) {
                sendMessage(body, true, "");
                //}
            }
        });
    }

    /**
     * 内容是否包含敏感词
     *
     * @param content
     * @return
     */
    private boolean hasSensitiveWords(String content) {
        String worlds = SplashStore.getSensitiveWords();
        List<String> worldList = TagText.parseTagList(worlds);
        for (String world : worldList) {
            if (content.contains(world)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onInput(int type, String content) {
        if (type == OnInputListener.TYPE_TEXT) {
            if (UrlStringUtils.isValidUrl(content)) {
                Body body = new UrlBody(content);
                sendMessage(body, true, "");
                return;
            }
            // else  当前为文本消息
            if (hasSensitiveWords(content)) {
                ToastUtil.showToast("请勿发送违法或敏感内容，否则你将负上法律责任!");
                return;
            }
            Body body = new TextBody(content);
            sendMessage(body, true, "");
        } else if (type == OnInputListener.TYPE_VOICE) {
            final String path = content;
            showUploadingDialog();
            //先发送空的消息
            //final Body emptyBody = new AudioBody("", 0);
            //final MessageObject emptyMessage = sendMessage(emptyBody, false, "");
            RemoteFileManager.uploadTempFile(new File(path), new OnFileUploadListener() {
                @Override
                public void onUpload(FileApiResult apiResult) {
                    if (apiResult.mResult) {
                        String url = apiResult.url;
                        File oldFile = new File(path);
                        File newFile = new File(AppFileManager.getRecordDir(), HttpUtils.getFileNameFromUrl(url));
                        // 将文件移到缓存文件夹下
                        FileUtils.move(oldFile, newFile);

                        long duration = AmrHelper.getAmrDuration(newFile);
                        Body body = new AudioBody(url, duration);
                        //if (null != emptyMessage) {
                        sendMessage(body, true, "");
                        //}
                    } else {
                        ToastUtil.showToast("发送语音失败!");
                    }
                    dismissUploadingDialog();
                }
            });
        }
    }

    private ISLoadingDialog mUploadingDialog;

    private void showUploadingDialog() {
        if (null == mUploadingDialog || !mUploadingDialog.isShowing()) {
            mUploadingDialog = new ISLoadingDialog(this);
            mUploadingDialog.setMessage("发送中");
            mUploadingDialog.show();
        }
    }

    private void dismissUploadingDialog() {
        if (null != mUploadingDialog && mUploadingDialog.isShowing()) {
            mUploadingDialog.dismiss();
            mUploadingDialog = null;
        }
    }

    /**
     * 发送消息
     *
     * @param body
     */
    private MessageObject sendMessage(Body body, boolean shouldSend, String updateMsgId) {
        String toJid = "";
        Message.Type chatType = null;
        if (mChatType == TYPE_SINGLE_CHAT) {
            chatType = Message.Type.chat;
            toJid = mParticipant.getBareJid();
        } else if (mChatType == TYPE_GROUP_CHAT) {
            chatType = Message.Type.groupchat;
            toJid = mCircleInfo.getGroupJid();
        }

        MessageObject messageObject = XMPPManager.getInstance().getMessageManager()
                .sendTextMessage(chatType, toJid, mLoginUser.getNickname(), mLoginUser.getAvatar(), body, shouldSend, updateMsgId);
        mAdapter.notifyDataSetChanged();
        if (messageObject != null) {
            EventBus.getDefault().post(new SendMessageEvent(messageObject));
        }
        return messageObject;
    }

    /**
     * 选择图片
     */
    private void selectPicture() {
        ImagePickerHelper.pickMultiImage(this, new OnPickMultiImageListener() {
            @Override
            public void onPickImage(List<ImageItem> imageList) {
                if (imageList != null) {
                    for (ImageItem imageItem : imageList) {
                        sendImage(imageItem.path);
                    }
                }
            }
        });
    }

    /**
     * 拍照
     */
    private void takePicture() {
        ImagePickerHelper.takePicture(this, new OnPickImageListener() {
            @Override
            public void onPickImage(ImageItem imageItem) {
                sendImage(imageItem.path);
            }
        });
    }


    /**
     * 发送图片
     *
     * @param path
     */
    private void sendImage(final String path) {
        showUploadingDialog();
        //先发送空消息
        //Body emptyBody = new ImageBody("", new ImageBody.Image());
        //final MessageObject emptyMessage = sendMessage(emptyBody, false, "");
        RemoteFileManager.uploadImageFile(new File(path), new OnFileUploadListener() {
            @Override
            public void onUpload(FileApiResult apiResult) {
                if (apiResult.mResult) {
                    Body body = new ImageBody(apiResult.url, new ImageBody.Image());
                    sendMessage(body, true, "");
                } else {
                    ToastUtil.showToast("发送图片失败!");
                }
                dismissUploadingDialog();
            }
        });
    }

    /**
     * 选择文件
     */
    private void selectFileFromLocal() {
        FilePicker.pickFile(this, new OnPickFileListener() {
            @Override
            public void onPick(File file) {
                sendFile(file);
            }
        });
    }

    /**
     * 发送文件
     *
     * @param file
     */
    private void sendFile(final File file) {
        showUploadingDialog();
        //先发送空消息
        //final Body emptyBody = new FileBody("", file.getName(), file);
        //final MessageObject emptyMessage = sendMessage(emptyBody, false, "");
        RemoteFileManager.uploadTempFile(file, new OnFileUploadListener() {
            @Override
            public void onUpload(FileApiResult apiResult) {
                if (apiResult.mResult) {
                    String url = apiResult.url;
                    Body body = new FileBody(url, file.getName(), file);
                    //if (null != emptyMessage) {
                    sendMessage(body, true, "");
                    //}
                } else {
                    ToastUtil.showToast("发送文件失败!");
                }
                dismissUploadingDialog();
            }
        });
    }

    @OnClick(R.id.btn_profile)
    void onClickProfile() {
        if (mChatType == TYPE_GROUP_CHAT) {
            GroupInfoActivity.launch(this, mCircleInfo.getGroupJid());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinishChatEvent finishChatEvent) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RemoveGroupEvent removeGroupEvent) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EditGroupEvent editGroupEvent) {
        //收到群修改成功通知后，重新刷新列表
        if (mChatType == TYPE_GROUP_CHAT) {
            if (editGroupEvent.getGroupJid().equals(mCircleInfo.getGroupJid())) {
                mNicknameView.setText(editGroupEvent.getGroupName());
                mCircleInfo.setName(editGroupEvent.getGroupName());
                mCircleInfo.setAvatar(editGroupEvent.getAvatar());
            }
        }
    }

    private PacketListener mGroupChatListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof GroupChatReply) {
                GroupChatReply groupChatReply = (GroupChatReply) packet;
                List<MessageObject> messageObjects = groupChatReply.getMessages();
                for (int i = 0; i < messageObjects.size(); i++) {
                    MessageObject messageObject = messageObjects.get(i);
                    messageObject.setAccount(XMPPManager.getInstance().getXmppConnection().getBareJid());
                    messageStorage.insertMsg(messageObject);
                }

                List<MessageObject> chatMessages = XMPPManager.getInstance().getMessageManager().getMessageList(mCircleInfo.getGroupJid());
                mAdapter = new MessageListAdapter(ChatActivity.this, chatMessages);
                mPtrView.setAdapter(mAdapter);
            }
        }
    };


}
