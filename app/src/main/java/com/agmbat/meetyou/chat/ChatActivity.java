package com.agmbat.meetyou.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.agmbat.android.media.AmrHelper;
import com.agmbat.android.media.AudioPlayer;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.UiUtils;
import com.agmbat.android.utils.UrlStringUtils;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.app.AppFileManager;
import com.agmbat.baidumap.BDLocationManager;
import com.agmbat.emoji.panel.p2.EmojiPanel;
import com.agmbat.emoji.panel.p2.EmojiPanelConfig;
import com.agmbat.emoji.res.DefEmoticons;
import com.agmbat.emoji.res.DefXhsEmoticons;
import com.agmbat.file.FileUtils;
import com.agmbat.http.HttpUtils;
import com.agmbat.imagepicker.ImagePicker;
import com.agmbat.imagepicker.bean.ImageItem;
import com.agmbat.imagepicker.loader.UILImageLoader;
import com.agmbat.imagepicker.ui.ImageGridActivity;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.imsdk.chat.body.AudioBody;
import com.agmbat.imsdk.chat.body.Body;
import com.agmbat.imsdk.chat.body.ImageBody;
import com.agmbat.imsdk.chat.body.LocationBody;
import com.agmbat.imsdk.chat.body.TextBody;
import com.agmbat.imsdk.chat.body.UrlBody;
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
import com.agmbat.menu.MenuInfo;
import com.agmbat.menu.OnClickMenuListener;
import com.agmbat.pulltorefresh.view.PullToRefreshListView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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

    /**
     * 填充消息列表的adapter
     */
    private MessageListAdapter mAdapter;

    /**
     * 参与聊天的联系人
     */
    private ContactInfo mParticipant;

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

    /**
     * 位置管理
     */
    private BDLocationManager mLocationManager;

    private BDLocationListener mBDLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location != null) {
                final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                DataManager.getInstance().setPlace(latLng);
//                final String address = location.getAddrStr();
//                DataManager.getInstance().setAddress(address);
                UiUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        stopRecordLocation();
//                        LocaltrainHelper.loadHotPoints();
//                        if (TextUtils.isEmpty(address)) {
//                            mSearch = GeoCoder.newInstance();
//                            mSearch.setOnGetGeoCodeResultListener(mOnGetGeoCoderResultListener);
//                            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
//                        }
                    }
                });
            }
        }
    };

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
        mParticipant = XMPPManager.getInstance().getRosterManager().getContactInfo(jid);
        setupViews();
        EventBus.getDefault().register(this);
        AudioPlayer.getDefault().addListener(mOnPlayListener);

        mLocationManager = new BDLocationManager();
        mLocationManager.recordLocation(true);
        mLocationManager.addBDLocationListener(mBDLocationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        AudioPlayer.getDefault().removeListener(mOnPlayListener);
        AudioPlayer.getDefault().pause();

        stopRecordLocation();
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
        }
    }

    private void stopRecordLocation() {
        mLocationManager.removeBDLocationListener(mBDLocationListener);
        mLocationManager.recordLocation(false);
    }

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

        initOtherPanel();
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

            }
        }));
        beans.add(createMenuInfo(R.mipmap.icon_loaction, "位置", new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {
                BDLocation location = mLocationManager.getCurrentLocation();
                if (location == null) {
                    ToastUtil.showToast("获取位置失败");
                    return;
                }
                LocationBody body = new LocationBody(location);
                sendMessage(body);
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

    @Override
    public void onInput(int type, String content) {
        if (type == OnInputListener.TYPE_TEXT) {
            Body body;
            if (UrlStringUtils.isValidUrl(content)) {
                body = new UrlBody(content);
            } else {
                body = new TextBody(content);
            }
            sendMessage(body);
        } else if (type == OnInputListener.TYPE_VOICE) {
            final String path = content;
            RemoteFileManager.uploadTempFile(new File(path), new OnFileUploadListener2() {
                @Override
                public void onUpload(TempFileApiResult apiResult) {
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
        MessageObject messageObject = XMPPManager.getInstance().getMessageManager()
                .sendTextMessage(mParticipant.getBareJid(), mParticipant.getNickName(), body.toXml());
        mAdapter.notifyDataSetChanged();
        if (messageObject != null) {
            EventBus.getDefault().post(new SendMessageEvent(messageObject));
        }
    }

    private static final int REQUEST_CODE_TAKE_PICTURE = 121;

    /**
     * 选择图片
     */
    private void selectPicture() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new UILImageLoader());
        imagePicker.setShowCamera(false); //显示拍照按钮
        imagePicker.setCrop(false); //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(false); //是否按矩形区域保存
        imagePicker.setSelectLimit(9); //选中数量限制
        imagePicker.setMultiMode(true); // 设置为单选
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }

    /**
     * 拍照
     */
    private void takePicture() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new UILImageLoader());
        imagePicker.setCrop(false); //允许裁剪（单选才有效）
        Intent intent = new Intent(this, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
            if (data != null) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    for (ImageItem imageItem : images) {
                        sendImage(imageItem.path);
                    }
                }
            }
        }
    }

    /**
     * 发送图片
     *
     * @param path
     */
    private void sendImage(final String path) {
        RemoteFileManager.uploadImageFile(new File(path), new OnFileUploadListener2() {
            @Override
            public void onUpload(TempFileApiResult apiResult) {
                if (apiResult.mResult) {
                    String url = apiResult.url;
                    Body body = new ImageBody(url, new ImageBody.Image());
                    MessageObject messageObject = XMPPManager.getInstance().getMessageManager()
                            .sendTextMessage(mParticipant.getBareJid(), mParticipant.getNickName(), body.toXml());
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

}
