package com.agmbat.meetyou.tab.msg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.meetyou.R;

import org.jivesoftware.smackx.message.MessageObject;
import org.jivesoftware.smackx.message.MessageStorage;

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
        mSysMsgAdapter = new SysMsgAdapter(getBaseContext());
        mMsgListView.setAdapter(mSysMsgAdapter);
        mMsgListView.addItemDecoration(new DividerDecoration(Color.parseColor("#e5e5e5"), 1, DividerDecoration.VERTICAL_LIST));
        mSysMsgAdapter.addAll(sysMessages);
    }

    @OnClick(R.id.title_btn_back)
    void onClickBack(){
        finish();
    }


}

