package com.agmbat.meetyou.group;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.agmbat.android.utils.ToastUtil;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.util.VLog;
import com.agmbat.isdialog.ISLoadingDialog;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.chat.MessageListAdapter;

import org.jivesoftware.smackx.message.MessageObject;
import org.jivesoftware.smackx.message.MessageStorage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchChatActivity extends Activity {

    private static final String KEY_CHAT_JID = "chat_jid";

    @BindView(R.id.message_list)
    ListView mMessageListView;
    @BindView(R.id.txt_search)
    EditText mSearchText;
    @BindView(R.id.result)
    TextView mResultTv;

    private MessageStorage mMessageStorage;
    private String mChatJid;
    private MessageListAdapter mAdapter;
    private List<MessageObject> mMessages = new ArrayList<>();
    private SearchTask mSearchTask;

    public static final void launch(Context context, String chatJid) {
        Intent intent = new Intent(context, SearchChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_CHAT_JID, chatJid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChatJid = getIntent().getStringExtra(KEY_CHAT_JID);
        setContentView(R.layout.activity_search_chat);
        ButterKnife.bind(this);
        mMessageStorage = new MessageStorage();
        mAdapter = new MessageListAdapter(this, mMessages);
        mMessageListView.setAdapter(mAdapter);
    }

    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    @OnClick(R.id.btn_search)
    void onClickSearch() {
        if (TextUtils.isEmpty(mSearchText.getText().toString())) {
            ToastUtil.showToast("搜索关键字不能为空");
            return;
        }
        if (null == mSearchTask) {
            mSearchTask = new SearchTask();
            String myJid = XMPPManager.getInstance().getXmppConnection().getBareJid();
            mSearchTask.execute(myJid, mChatJid, mSearchText.getText().toString());
        }
    }

    private class SearchTask extends AsyncTask<String, Void, List<MessageObject>> {

        private ISLoadingDialog mLoadingDialog;

        @Override
        protected void onPreExecute() {
            mLoadingDialog = new ISLoadingDialog(SearchChatActivity.this);
            mLoadingDialog.setMessage("查找中");
            mLoadingDialog.show();
            mResultTv.setVisibility(View.GONE);
        }

        @Override
        protected List<MessageObject> doInBackground(String... strings) {
            String myJid = strings[0];
            String chatJid = strings[1];
            String keyword = strings[2];
            return mMessageStorage.search(myJid, chatJid, keyword);
        }

        @Override
        protected void onPostExecute(List<MessageObject> messageObjects) {
            mLoadingDialog.dismiss();
            mSearchTask = null;

            mMessages.clear();
            mMessages.addAll(messageObjects);
            mAdapter.notifyDataSetChanged();

            VLog.d("Search result size:" + messageObjects.size());
            if (messageObjects.size() == 0) {
                mResultTv.setText("无法查找到相关聊天记录");
                mResultTv.setVisibility(View.VISIBLE);
                mMessageListView.setVisibility(View.GONE);
            } else {
                mResultTv.setVisibility(View.GONE);
                mMessageListView.setVisibility(View.VISIBLE);
            }
        }
    }

}
