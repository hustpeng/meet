package com.agmbat.meetyou.tab.msg;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.SysResources;
import com.agmbat.android.image.ImageManager;
import com.agmbat.android.media.AudioPlayer;
import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.android.utils.AppUtils;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.baidumap.MapConfig;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.imsdk.chat.body.AudioBody;
import com.agmbat.imsdk.chat.body.Body;
import com.agmbat.imsdk.chat.body.BodyParser;
import com.agmbat.imsdk.chat.body.EventsBody;
import com.agmbat.imsdk.chat.body.FileBody;
import com.agmbat.imsdk.chat.body.FireBody;
import com.agmbat.imsdk.chat.body.FriendBody;
import com.agmbat.imsdk.chat.body.ImageBody;
import com.agmbat.imsdk.chat.body.LocationBody;
import com.agmbat.imsdk.chat.body.TextBody;
import com.agmbat.imsdk.chat.body.UrlBody;
import com.agmbat.imsdk.mgr.XmppFileManager;
import com.agmbat.map.LocationObject;
import com.agmbat.map.Maps;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.component.ViewImageActivity;
import com.agmbat.meetyou.component.WebViewActivity;
import com.agmbat.meetyou.util.ImageUtil;
import com.agmbat.meetyou.widget.BaseRecyclerAdapter;
import com.agmbat.net.HttpUtils;
import com.agmbat.time.DurationFormat;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import org.jivesoftware.smackx.message.MessageObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SysMsgAdapter extends BaseRecyclerAdapter<MessageObject, SysMsgAdapter.SysMsgViewHolder> {

    private Context mContext;

    public SysMsgAdapter(Context context){
        mContext = context;
    }


    @Override
    public SysMsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sys_msg_item, parent, false);
        return new SysMsgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SysMsgViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.bindData(getItem(position));
    }


    private static final int BASE_WIDTH = (int) SysResources.dipToPixel(80);
    private static final int AUDIO_MAX_WIDTH = (int) SysResources.dipToPixel(200);

    public class SysMsgViewHolder extends RecyclerView.ViewHolder{

        private ImageView mContentImage;
        private TextView mContentTv;
        private TextView mTimeTv;

        public SysMsgViewHolder(View itemView) {
            super(itemView);
            mContentImage = (ImageView) itemView.findViewById(R.id.image_conent);
            mContentTv = (TextView) itemView.findViewById(R.id.text_content);
            mTimeTv = (TextView) itemView.findViewById(R.id.time);
        }

        public void bindData(MessageObject messageObject){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            mTimeTv.setText(dateFormat.format(messageObject.getDate()));
            final Body body = BodyParser.parse(messageObject.getBody());
            if(body instanceof TextBody){
                TextBody textBody = (TextBody) body;
                setTextBody(textBody);
            }else if(body instanceof ImageBody){
                final ImageBody imageBody = (ImageBody) body;
                setImageBody(imageBody);
            }else if (body instanceof UrlBody) {
                final UrlBody urlBody = (UrlBody) body;
                setUrlBody(urlBody);
            } else if (body instanceof AudioBody) {
                AudioBody audioBody = (AudioBody) body;
                setAudioBody(audioBody);
            } else if (body instanceof FireBody) {
                FireBody fireBody = (FireBody) body;
                setImageBody(fireBody);
            } else if (body instanceof LocationBody) {
                LocationBody locationBody = (LocationBody) body;
                setLocationBody(messageObject, locationBody);
            } else if (body instanceof FriendBody) {
                FriendBody friendBody = (FriendBody) body;
                setFriendBody(friendBody);
            } else if (body instanceof FileBody) {
                FileBody fileBody = (FileBody) body;
                setFileBody(fileBody);
            } else if(body instanceof EventsBody){
                final EventsBody eventsBody = (EventsBody) body;
                setEventsBody(eventsBody);
            }else{
                mContentTv.setVisibility(View.VISIBLE);
                mContentImage.setVisibility(View.GONE);
                mContentTv.setText(messageObject.getBody());
            }
        }

        private void setTextBody(TextBody textBody){
            mContentTv.setVisibility(View.VISIBLE);
            mContentImage.setVisibility(View.GONE);
            mContentTv.setText(textBody.getContent());
        }

        private void setAudioPlayTextView(final View tv, final String url) {

            tv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AudioPlayer.getDefault().playOrPause(url);
                }
            });
        }

        private void setUrlBody(final UrlBody urlBody){
            mContentTv.setVisibility(View.VISIBLE);
            mContentImage.setVisibility(View.GONE);
            mContentTv.setText(urlBody.getContent());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.openBrowser(mContext, urlBody.getContent());
                }
            });
            mContentTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        private void setAudioBody(AudioBody audioBody){
            mContentTv.setVisibility(View.VISIBLE);
            mContentImage.setVisibility(View.GONE);
            mContentTv.setText(DurationFormat.formatDurationShort(audioBody.getDuration()));
            ViewGroup.LayoutParams params = mContentTv.getLayoutParams();
            float percent = (audioBody.getDuration() / 60000.0f);
            if (percent > 1) {
                percent = 1;
            }
            params.width = (int) (AUDIO_MAX_WIDTH * percent) + BASE_WIDTH;
            final String url = audioBody.getFileUrl();
            if (url != null && url.startsWith("http")) {
                final File file = XmppFileManager.getRecordFile(url);
                if (file.exists()) {
                    setAudioPlayTextView(itemView, file.getAbsolutePath());
                } else {
                    AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, Boolean>() {

                        @Override
                        protected void onPreExecute() {
                        }

                        @Override
                        protected Boolean doInBackground(Void... arg0) {
                            return HttpUtils.downloadFile(url, file);
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            setAudioPlayTextView(itemView, file.getAbsolutePath());
                        }
                    });
                }
            } else {
                setAudioPlayTextView(itemView, url);
            }
        }

        /**
         * 设置图片
         *
         * @param imageBody
         */
        private void setImageBody(final ImageBody imageBody) {
            mContentTv.setVisibility(View.GONE);
            mContentImage.setVisibility(View.VISIBLE);
            mContentImage.setImageResource(0);
            ImageUtil.loadImage(mContext, mContentImage, imageBody.getFileUrl(), R.drawable.ic_default_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewImageActivity.viewImage(mContext, imageBody.getFileUrl());
                }
            });
        }

        /**
         * 设置位置显示
         *
         * @param locationBody
         */
        public void setLocationBody(final MessageObject msg, final LocationBody locationBody) {
            mContentImage.setVisibility(View.VISIBLE);
            mContentTv.setVisibility(View.GONE);
            mContentImage.setImageResource(R.drawable.ic_chat_def_location);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    viewMap(msg, locationBody);
                }
            });
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
            Maps.viewMap(mContext, config);
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

        private void setFriendBody(final FriendBody friendBody) {
            final ContactInfo profile = friendBody.getContactInfo();
            mContentTv.setVisibility(View.VISIBLE);
            mContentImage.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = mContentTv.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            mContentTv.setText("推荐好友:" + profile.getBareJid());
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                ProfileActivity.viewProfile(mContext, profile);
                }
            });
            mContentTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }


        private void setFileBody(final FileBody fileBody) {
            mContentImage.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = mContentTv.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            mContentTv.setVisibility(View.VISIBLE);
            mContentTv.setText("文件:" + fileBody.getFileName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = fileBody.getFile();
                    if (file == null || !file.exists()) {
                        downloadFile(fileBody);
                    } else {
                        AppUtils.openFile(mContext, file);
                    }
                }
            });
            mContentTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
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
                        AppUtils.openFile(mContext, downloadFile);
                    } else {
                        ToastUtil.showToast("文件下载失败!");
                    }
                }
            });

        }

        private void setEventsBody(final EventsBody eventsBody){
            mContentTv.setVisibility(View.VISIBLE);
            mContentImage.setVisibility(View.GONE);
            mContentTv.setText("新活动通知，点击查看详情");
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(eventsBody.getContent())){
                        ToastUtil.showToast("链接为空，无法继续查看详情");
                        return;
                    }
                    if(!eventsBody.getContent().startsWith("http://") && !eventsBody.getContent().startsWith("https://")){
                        ToastUtil.showToast("非法的链接地址，无法继续查看详情");
                        return;
                    }
                    String phone = XMPPManager.getInstance().getConnectionUserName();
                    String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
                    String url = eventsBody.getContent() + "&uid=" + phone + "&ticket=" + token;

                    String title = mContext.getString(R.string.discovery_meeting);
                    WebViewActivity.openBrowser(mContext, url, title);
                }
            });
        }

    }
}
