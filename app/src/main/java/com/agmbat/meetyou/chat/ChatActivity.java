package com.agmbat.meetyou.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.agmbat.imsdk.chat.body.FileBody;
import com.agmbat.imsdk.chat.body.ImageBody;
import com.agmbat.imsdk.chat.body.LocationBody;
import com.agmbat.imsdk.chat.body.TextBody;
import com.agmbat.imsdk.chat.body.UrlBody;
import com.agmbat.imsdk.imevent.ReceiveMessageEvent;
import com.agmbat.imsdk.imevent.SendMessageEvent;
import com.agmbat.imsdk.remotefile.FileApiResult;
import com.agmbat.imsdk.remotefile.OnFileUploadListener;
import com.agmbat.imsdk.remotefile.RemoteFileManager;
import com.agmbat.imsdk.splash.SplashStore;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.input.InputController;
import com.agmbat.input.InputView;
import com.agmbat.input.OnInputListener;
import com.agmbat.input.VoiceInputController;
import com.agmbat.map.LocationCallback;
import com.agmbat.map.LocationObject;
import com.agmbat.map.Maps;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.group.CircleInfo;
import com.agmbat.meetyou.group.GroupInfoActivity;
import com.agmbat.menu.MenuInfo;
import com.agmbat.menu.OnClickMenuListener;
import com.agmbat.net.HttpUtils;
import com.agmbat.pulltorefresh.view.PullToRefreshListView;
import com.agmbat.text.TagText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.message.MessageObject;

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        AudioPlayer.getDefault().removeListener(mOnPlayListener);
        AudioPlayer.getDefault().pause();
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
        if(mChatType == TYPE_SINGLE_CHAT){
            participantJid =  mParticipant.getBareJid();
        }else if(mChatType == TYPE_GROUP_CHAT){
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
        mPtrView.setAdapter(mAdapter);
    }

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
        beans.add(createMenuInfo(R.mipmap.icon_contact, "联系人", new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {

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
        Maps.getLocation(this, new LocationCallback() {
            @Override
            public void callback(LocationObject location) {
                if (location == null) {
                    ToastUtil.showToast("获取位置失败");
                    return;
                }
                mInputController.reset();
                LocationBody body = new LocationBody(location);
                sendMessage(body);
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
                sendMessage(body);
                return;
            }
            // else  当前为文本消息
            if (hasSensitiveWords(content)) {
                ToastUtil.showToast("请勿发送违法或敏感内容，否则你将负上法律责任!");
                return;
            }
            Body body = new TextBody(content);
            sendMessage(body);
        } else if (type == OnInputListener.TYPE_VOICE) {
            final String path = content;
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
                        sendMessage(body);
                    } else {
                        ToastUtil.showToast("发送语音失败!");
                    }
                }
            });
        }
    }

    /**
     * 发送消息
     *
     * @param body
     */
    private void sendMessage(Body body) {
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
                .sendTextMessage(chatType, toJid, mLoginUser.getNickname(), mLoginUser.getAvatar(), body.toXml());
        mAdapter.notifyDataSetChanged();
        if (messageObject != null) {
            EventBus.getDefault().post(new SendMessageEvent(messageObject));
        }
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
        RemoteFileManager.uploadImageFile(new File(path), new OnFileUploadListener() {
            @Override
            public void onUpload(FileApiResult apiResult) {
                if (apiResult.mResult) {
                    String url = apiResult.url;
                    Body body = new ImageBody(url, new ImageBody.Image());

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
                            .sendTextMessage(chatType, toJid, mLoginUser.getNickname(), mLoginUser.getAvatar(), body.toXml());
                    mAdapter.notifyDataSetChanged();
                    if (messageObject != null) {
                        EventBus.getDefault().post(new SendMessageEvent(messageObject));
                    }
                } else {
                    ToastUtil.showToast("发送图片失败!");
                }
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
        RemoteFileManager.uploadTempFile(file, new OnFileUploadListener() {
            @Override
            public void onUpload(FileApiResult apiResult) {
                if (apiResult.mResult) {
                    String url = apiResult.url;
                    Body body = new FileBody(url, file.getName(), file);
                    sendMessage(body);
                } else {
                    ToastUtil.showToast("发送文件失败!");
                }
            }
        });
    }

    @OnClick(R.id.btn_profile)
    void onClickProfile() {
        if(mChatType == TYPE_GROUP_CHAT) {
            GroupInfoActivity.launch(this, mCircleInfo.getGroupJid());
        }
    }
}
