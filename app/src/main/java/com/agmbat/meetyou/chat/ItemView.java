package com.agmbat.meetyou.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agmbat.android.SysResources;
import com.agmbat.android.image.ImageManager;
import com.agmbat.android.media.AudioPlayer;
import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.android.utils.AppUtils;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.ViewUtils;
import com.agmbat.app.AppFileManager;
import com.agmbat.baidumap.MapConfig;
import com.agmbat.emoji.display.EmojiDisplay;
import com.agmbat.imsdk.asmack.MessageManager;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.imsdk.chat.body.AudioBody;
import com.agmbat.imsdk.chat.body.Body;
import com.agmbat.imsdk.chat.body.BodyParser;
import com.agmbat.imsdk.chat.body.FileBody;
import com.agmbat.imsdk.chat.body.FireBody;
import com.agmbat.imsdk.chat.body.FriendBody;
import com.agmbat.imsdk.chat.body.ImageBody;
import com.agmbat.imsdk.chat.body.LocationBody;
import com.agmbat.imsdk.chat.body.TextBody;
import com.agmbat.imsdk.chat.body.UrlBody;
import com.agmbat.imsdk.mgr.XmppFileManager;
import com.agmbat.log.Debug;
import com.agmbat.map.LocationObject;
import com.agmbat.map.Maps;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.component.ViewImageActivity;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.agmbat.net.HttpUtils;
import com.agmbat.time.DurationFormat;
import com.agmbat.time.TimeUtils;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.jivesoftware.smackx.message.MessageObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class ItemView extends LinearLayout {

    private static final int BASE_WIDTH = (int) SysResources.dipToPixel(80);
    private static final int AUDIO_MAX_WIDTH = (int) SysResources.dipToPixel(200);

    /**
     * 用户头像
     */
    protected ImageView mAvatarView;

    /**
     * 文本内容
     */
    protected TextView mChatContentView;

    /**
     * 图片
     */
    protected ImageView mBodyImage;

    /**
     * 时间
     */
    protected TextView mTimeView;

    /**
     * 进度条
     */
    protected ProgressBar mLoadingProgress;

    public ItemView(Context context) {
        super(context);
        View.inflate(context, getLayoutId(), this);
        mAvatarView = (ImageView) findViewById(R.id.avatar);
        mChatContentView = (TextView) findViewById(R.id.chat_content);
        mTimeView = (TextView) findViewById(R.id.time);
        mBodyImage = (ImageView) findViewById(R.id.body_image);
        mLoadingProgress = (ProgressBar) findViewById(R.id.loading_progress);
    }

    protected abstract int getLayoutId();

    protected abstract void setupViews();

    /**
     * 更新消息显示
     *
     * @param msg
     * @param showTime
     */
    public void update(MessageObject msg, boolean showTime, boolean showLoading) {
        setupViews();
        setAvatar(msg);
        setMessageBody(msg);
        if (showTime) {
            mTimeView.setVisibility(View.VISIBLE);
            mTimeView.setText(TimeUtils.formatDateTime(msg.getDate()));
        } else {
            mTimeView.setVisibility(View.GONE);
        }
    }


    /**
     * 设置头像
     *
     * @param msg
     */
    private void setAvatar(MessageObject msg) {
        String senderJid = msg.getSenderJid();
        if (!TextUtils.isEmpty(msg.getSenderAvatar())) {
            ImageManager.displayImage(msg.getSenderAvatar(), mAvatarView, AvatarHelper.getOptions());
        } else {
            ContactInfo contactInfo = XMPPManager.getInstance().getRosterManager().getContactFromMemCache(senderJid);
            if (contactInfo != null) {
                ImageManager.displayImage(contactInfo.getAvatar(), mAvatarView, AvatarHelper.getOptions());
            }
        }
    }

    private void setMessageBody(MessageObject msg) {
        Body body = BodyParser.parse(msg.getBody());
        if (body instanceof TextBody) {
            TextBody textBody = (TextBody) body;
            setTextBody(textBody);
        } else if (body instanceof UrlBody) {
            UrlBody urlBody = (UrlBody) body;
            setUrlBody(urlBody);
        } else if (body instanceof AudioBody) {
            AudioBody audioBody = (AudioBody) body;
            setAudioBody(mChatContentView, audioBody);
        } else if (body instanceof FireBody) {
            FireBody fireBody = (FireBody) body;
            if (MessageManager.isToOthers(msg)) {
                setImageBody(msg, fireBody, false);
            } else {
                setImageBody(msg, fireBody, true);
            }
        } else if (body instanceof ImageBody) {
            ImageBody imageBody = (ImageBody) body;
            setImageBody(msg, imageBody, false);
        } else if (body instanceof LocationBody) {
            LocationBody locationBody = (LocationBody) body;
            setLocationBody(msg, locationBody);
        } else if (body instanceof FriendBody) {
            FriendBody friendBody = (FriendBody) body;
            setFriendBody(friendBody);
        } else if (body instanceof FileBody) {
            FileBody fileBody = (FileBody) body;
            setFileBody(fileBody);
        }
    }

    private void setFileBody(final FileBody fileBody) {
        mBodyImage.setVisibility(View.GONE);
        ViewGroup.LayoutParams params = mChatContentView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mChatContentView.setVisibility(View.VISIBLE);
        mChatContentView.setText("文件:" + fileBody.getFileName());
        mChatContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = fileBody.getFile();
                if (file == null || !file.exists()) {
                    downloadFile(fileBody);
                } else {
                    AppUtils.openFile(getContext(), file);
                }
            }
        });
        mChatContentView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    /**
     * 下载文件
     *
     * @param fileBody
     */
    private void downloadFile(final FileBody fileBody) {
        final File downloadFile = new File(XmppFileManager.getChatFileDir(), fileBody.getFileName());
        // 下载文件
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return HttpUtils.downloadFile(fileBody.getUrl(), downloadFile);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result) {
                    fileBody.setFile(downloadFile);
                    AppUtils.openFile(getContext(), downloadFile);
                } else {
                    ToastUtil.showToast("文件下载失败!");
                }
            }
        });

    }

    private void setFriendBody(final FriendBody friendBody) {
        final ContactInfo profile = friendBody.getContactInfo();
        mBodyImage.setVisibility(View.GONE);
        ViewGroup.LayoutParams params = mChatContentView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mChatContentView.setVisibility(View.VISIBLE);
        mChatContentView.setText("推荐好友:" + profile.getBareJid());
        mChatContentView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                ProfileActivity.viewProfile(getContext(), profile);
            }
        });
        mChatContentView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    /**
     * 设置位置显示
     *
     * @param locationBody
     */
    public void setLocationBody(final MessageObject msg, final LocationBody locationBody) {
        mBodyImage.setVisibility(View.VISIBLE);
        mChatContentView.setVisibility(View.GONE);
        mBodyImage.setImageResource(R.drawable.ic_chat_def_location);
        mBodyImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                viewMap(msg, locationBody);
            }
        });
    }

    /**
     * 创建marker
     *
     * @param lat
     * @param lon
     * @param iconPath
     * @return
     */
    private MarkerOptions createMarkerOptions(double lat, double lon, String iconPath) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.map_pin);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lat, lon));
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.icon(icon);
        markerOptions.zIndex(9);
        return markerOptions;
    }

    /**
     * 查看地图
     *
     * @param msg
     * @param locationBody
     */
    private void viewMap(MessageObject msg, LocationBody locationBody) {
        MapConfig config = new MapConfig();
        config.setIsShowCurrent(true);
        List<MarkerOptions> list = new ArrayList<>();

        LocationObject location = locationBody.getLocation();
        config.setMapCenter(new LatLng(location.mLatitude, location.mLongitude));

        String senderJid = msg.getSenderJid();
        ContactInfo user = XMPPManager.getInstance().getRosterManager().getContactFromMemCache(senderJid);
        File file = ImageManager.getDiskCacheFile(user.getAvatar());
        list.add(createMarkerOptions(location.mLatitude, location.mLongitude, file.getAbsolutePath()));

        config.setMarkerList(list);
        Maps.viewMap(getContext(), config);
    }

    /**
     * 设置图片
     *
     * @param msg
     * @param imageBody
     * @param isFire
     */
    private void setImageBody(final MessageObject msg, final ImageBody imageBody, final boolean isFire) {
        mBodyImage.setVisibility(View.VISIBLE);
        mChatContentView.setVisibility(View.GONE);
        if(TextUtils.isEmpty(imageBody.getFileUrl())){
            mLoadingProgress.setVisibility(VISIBLE);
        }else {
            ImageManager.displayImage(imageBody.getFileUrl(), mBodyImage, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    mLoadingProgress.setVisibility(VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    mLoadingProgress.setVisibility(GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mLoadingProgress.setVisibility(GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    mLoadingProgress.setVisibility(GONE);
                }
            });
            mBodyImage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ViewImageActivity.viewImage(getContext(), imageBody.getFileUrl());
                }
            });
        }
    }

    /**
     * 设置文本消息view
     *
     * @param body
     */
    private void setTextBody(TextBody body) {
        mBodyImage.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.GONE);
        ViewGroup.LayoutParams params = mChatContentView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mChatContentView.setVisibility(View.VISIBLE);

        int fontSize = ViewUtils.getFontHeight(mChatContentView);
        Spannable spannable = EmojiDisplay.update(body.getContent(), fontSize);
        mChatContentView.setText(spannable);

        mChatContentView.setOnClickListener(null);
        mChatContentView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    /**
     * 设置url body
     *
     * @param body
     */
    private void setUrlBody(final UrlBody body) {
        mBodyImage.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.GONE);
        ViewGroup.LayoutParams params = mChatContentView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mChatContentView.setVisibility(View.VISIBLE);
        mChatContentView.setText(body.getContent());
        mChatContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openBrowser(getContext(), body.getContent());
            }
        });
        mChatContentView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    protected void setAudioDrawable(AudioBody body) {
    }

    private void setAudioBody(final TextView tv, AudioBody audioBody) {
        mBodyImage.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(GONE);
        mChatContentView.setVisibility(View.VISIBLE);
        tv.setText(DurationFormat.formatDurationShort(audioBody.getDuration()));
        ViewGroup.LayoutParams params = tv.getLayoutParams();
        float percent = (audioBody.getDuration() / 60000.0f);
        if (percent > 1) {
            percent = 1;
        }
        params.width = (int) (AUDIO_MAX_WIDTH * percent) + BASE_WIDTH;
        setAudioDrawable(audioBody);
        final String url = audioBody.getFileUrl();
        if (url != null && url.startsWith("http")) {
            final File file = XmppFileManager.getRecordFile(url);
            if (file.exists()) {
                setAudioPlayTextView(tv, file.getAbsolutePath());
            } else {
                AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, Boolean>() {

                    @Override
                    protected void onPreExecute() {
                        mLoadingProgress.setVisibility(VISIBLE);
                    }

                    @Override
                    protected Boolean doInBackground(Void... arg0) {
                        return HttpUtils.downloadFile(url, file);
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        mLoadingProgress.setVisibility(GONE);
                        setAudioPlayTextView(tv, file.getAbsolutePath());
                    }
                });
            }
        } else {
            mLoadingProgress.setVisibility(VISIBLE);
            setAudioPlayTextView(tv, url);
        }
    }

    private void setAudioPlayTextView(final TextView tv, final String url) {

        tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AudioPlayer.getDefault().playOrPause(url);
            }
        });
    }


    public boolean isPlaying(AudioBody audioBody) {
        if (AudioPlayer.getDefault().isPlaying()) {
            File file = new File(AppFileManager.getRecordDir(), HttpUtils.getFileNameFromUrl(audioBody.getFileUrl()));
            String url = file.getAbsolutePath();
            return TextUtils.equals(AudioPlayer.getDefault().getDataSource(), url);
        }
        return false;
    }

}
