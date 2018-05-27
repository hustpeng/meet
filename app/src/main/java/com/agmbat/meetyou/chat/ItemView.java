package com.agmbat.meetyou.chat;

import android.content.Context;
import android.location.Location;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agmbat.android.AppResources;
import com.agmbat.android.image.ImageManager;
import com.agmbat.android.media.AudioPlayer;
import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.android.utils.AppUtils;
import com.agmbat.android.utils.ViewUtils;
import com.agmbat.app.AppFileManager;
import com.agmbat.emoji.display.EmojiDisplay;
import com.agmbat.http.HttpUtils;
import com.agmbat.imsdk.asmack.MessageManager;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.chat.body.AudioBody;
import com.agmbat.imsdk.chat.body.Body;
import com.agmbat.imsdk.chat.body.BodyParser;
import com.agmbat.imsdk.chat.body.FireBody;
import com.agmbat.imsdk.chat.body.FriendBody;
import com.agmbat.imsdk.chat.body.ImageBody;
import com.agmbat.imsdk.chat.body.LocationBody;
import com.agmbat.imsdk.chat.body.TextBody;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.imsdk.chat.body.UrlBody;
import com.agmbat.imsdk.mgr.XmppFileManager;
import com.agmbat.log.Debug;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.component.ViewImageActivity;
import com.agmbat.time.DurationFormat;
import com.agmbat.time.TimeUtils;

import org.jivesoftware.smackx.message.MessageObject;

import java.io.File;

public abstract class ItemView extends LinearLayout {

    private static final int BASE_WIDTH = (int) AppResources.dipToPixel(80);
    private static final int AUDIO_MAX_WIDTH = (int) AppResources.dipToPixel(200);

    public ImageView mAvatarView;
    public TextView mChatContentView;
    public ImageView mBodyImage;
    public TextView mTimeView;

    public ItemView(Context context) {
        super(context);
        View.inflate(context, getLayoutId(), this);
        mAvatarView = (ImageView) findViewById(R.id.avatar);
        mChatContentView = (TextView) findViewById(R.id.chat_content);
        mTimeView = (TextView) findViewById(R.id.time);
        mBodyImage = (ImageView) findViewById(R.id.body_image);
    }

    protected abstract int getLayoutId();

    protected abstract void setupViews();

    public void update(MessageObject msg) {
        setupViews();
        setAvatar(msg);
        setMessageBody(msg);
        mTimeView.setText(TimeUtils.formatDateTime(msg.getDate()));
    }

    /**
     * 设置头像
     *
     * @param msg
     */
    private void setAvatar(MessageObject msg) {
        String senderJid = msg.getSenderJid();
        ContactInfo contactInfo = XMPPManager.getInstance().getRosterManager().getContactInfo(senderJid);
        if (contactInfo != null) {
            ImageManager.displayImage(contactInfo.getAvatar(), mAvatarView, ImageManager.getCircleOptions());
        } else {
            Debug.printStackTrace();
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
            setLocationBody(locationBody);
        } else if (body instanceof FriendBody) {
            FriendBody friendBody = (FriendBody) body;
            setFriendBody(friendBody);
        }
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

    public void setLocationBody(final LocationBody locationBody) {
        mBodyImage.setVisibility(View.VISIBLE);
        mChatContentView.setVisibility(View.GONE);
        mBodyImage.setImageResource(R.drawable.ic_chat_def_location);
        mBodyImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Location location = locationBody.getLocation();
//                ContactInfo info = new ContactInfo(ChatRes.getParticipant());
//                info.setLatitude(location.getLatitude());
//                info.setLongitude(location.getLongitude());
//                final Context context = getContext();
//                Intent i = new Intent(context, MapActivity.class);
//                i.putExtra(Constants.EXTRA_CONTACT, info);
//                context.startActivity(i);
            }
        });
    }

    private void setImageBody(final MessageObject msg, final ImageBody imageBody, final boolean isFire) {
        mBodyImage.setVisibility(View.VISIBLE);
        mChatContentView.setVisibility(View.GONE);
        ImageManager.displayImage(imageBody.getFileUrl(), mBodyImage);
        mBodyImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ViewImageActivity.viewImage(getContext(), imageBody.getFileUrl());
            }
        });
    }

    /**
     * 设置文本消息view
     *
     * @param body
     */
    private void setTextBody(TextBody body) {
        mBodyImage.setVisibility(View.GONE);
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
                    protected Boolean doInBackground(Void... arg0) {
                        return HttpUtils.downloadFile(url, file);
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        setAudioPlayTextView(tv, file.getAbsolutePath());
                    }
                });
            }
        } else {
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
