package com.agmbat.meetyou.tab.msg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.chat.body.Body;
import com.agmbat.imsdk.chat.body.BodyParser;
import com.agmbat.imsdk.chat.body.EventsBody;
import com.agmbat.imsdk.chat.body.ImageBody;
import com.agmbat.imsdk.chat.body.TextBody;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.component.ViewImageActivity;
import com.agmbat.meetyou.component.WebViewActivity;
import com.agmbat.meetyou.discovery.meeting.MeetingActivity;
import com.agmbat.meetyou.util.ImageUtil;
import com.agmbat.meetyou.widget.BaseRecyclerAdapter;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.jivesoftware.smackx.message.MessageObject;
import org.jivesoftware.smackx.message.MessageStorage;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SysMsgActivity extends Activity {

    @BindView(R.id.msg_list)
    RecyclerView mMsgListView;
    @BindView(R.id.result)
    TextView mResultTv;

    private MessageStorage mMessageStorage;
    private SysMsgAdapter mSysMsgAdapter;

    public static void launch(Context context){
        Intent intent = new Intent(context, SysMsgActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_msg);
        ButterKnife.bind(this);
        mMessageStorage = new MessageStorage();

        String myJid = XMPPManager.getInstance().getXmppConnection().getBareJid();
        List<MessageObject> sysMessages = mMessageStorage.getMessages(myJid, "support@yuan520.com");
        mMsgListView.setLayoutManager(new LinearLayoutManager(getApplication()));
        mSysMsgAdapter = new SysMsgAdapter();
        mMsgListView.setAdapter(mSysMsgAdapter);
        mMsgListView.addItemDecoration(new DividerDecoration(Color.parseColor("#e5e5e5"), 1, DividerDecoration.VERTICAL_LIST));
        mSysMsgAdapter.addAll(sysMessages);
    }

    @OnClick(R.id.title_btn_back)
    void onClickBack(){
        finish();
    }

    private class SysMsgAdapter extends BaseRecyclerAdapter<MessageObject, SysMsgAdapter.SysMsgViewHolder>{


        @Override
        public SysMsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_sys_msg_item, parent, false);
            return new SysMsgViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SysMsgViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            holder.bindData(getItem(position));
        }

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
                Body body = BodyParser.parse(messageObject.getBody());
                if(body instanceof TextBody){
                    TextBody textBody = (TextBody) body;
                    mContentTv.setVisibility(View.VISIBLE);
                    mContentImage.setVisibility(View.GONE);
                    mContentTv.setText(textBody.getContent());
                }else if(body instanceof ImageBody){
                    final ImageBody imageBody = (ImageBody) body;
                    mContentTv.setVisibility(View.GONE);
                    mContentImage.setVisibility(View.VISIBLE);
                    ImageUtil.loadImage(getBaseContext(), mContentImage, imageBody.getFileUrl(), R.drawable.ic_default_image);
                    mContentImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ViewImageActivity.viewImage(getBaseContext(), imageBody.getFileUrl());
                        }
                    });
                }else if(body instanceof EventsBody){
                    final EventsBody eventsBody = (EventsBody) body;
                    mContentTv.setVisibility(View.VISIBLE);
                    mContentImage.setVisibility(View.GONE);
                    mContentTv.setText("活动通知，点击查看详情");
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(TextUtils.isEmpty(eventsBody.getContent())){
                                ToastUtil.showToast("链接为空，无法继续查看详情");
                                return;
                            }
                            String phone = XMPPManager.getInstance().getConnectionUserName();
                            String token = XMPPManager.getInstance().getTokenManager().getTokenRetry();
                            String url = eventsBody.getContent() + "&uid=" + phone + "&ticket=" + token;

                            String title = getString(R.string.discovery_meeting);
                            WebViewActivity.openBrowser(getBaseContext(), url, title);
                        }
                    });
                }else{
                    mContentTv.setVisibility(View.VISIBLE);
                    mContentImage.setVisibility(View.GONE);
                    mContentTv.setText(messageObject.getBody());
                }
            }
        }
    }
}

