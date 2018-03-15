package com.agmbat.meetyou.tab.msg;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agmbat.log.Log;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.data.ChatMessage;
import com.agmbat.meetyou.data.ContactInfo;
import com.agmbat.meetyou.data.RecentChat;
import com.agmbat.meetyou.tab.MeetDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Tab聊天
 */
public class MsgFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = MsgFragment.class.getSimpleName();

    private static final int STATE_LOADING = 0;
    private static final int STATE_SUCCESS = 1;
    private static final int STATE_NO_DATA = 2;

    private ListView mRecentChatList;
    private TextView mResultView;
    private RecentChatAdapter mRecentChatAdapter;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.tab_fragment_msg, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mRecentChatList = (ListView) view.findViewById(R.id.recent_chat_list);
        mRecentChatList.setOnItemClickListener(this);
        mResultView = (TextView) view.findViewById(R.id.result);
        new InitRecentChatTask().execute();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RecentChat recentChat = mRecentChatAdapter.getItem(position);
//        Intent intent = new Intent(getActivity(), ChatActivity.class);
//        intent.putExtra(Constants.EXTRA_PARTICIPANT, recentChat.getContact());
//        startActivityForResult(intent, REQUEST_CODE_CHAT);
    }

    private RecentChat queryRecentChatFor(ContactInfo contactInfo) {
        RecentChat recentChat = new RecentChat();
        recentChat.setContact(contactInfo);
        ChatMessage lastChatMessage = new ChatMessage();
        recentChat.setUnreadCount(5);
        recentChat.setLastChatMessage(lastChatMessage);
        return recentChat;
    }

    private void setState(int state) {
        if (state == STATE_LOADING) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRecentChatList.setVisibility(View.GONE);
            mResultView.setVisibility(View.GONE);
        } else if (state == STATE_SUCCESS) {
            mProgressBar.setVisibility(View.GONE);
            mRecentChatList.setVisibility(View.VISIBLE);
            mResultView.setVisibility(View.GONE);
        } else if (state == STATE_NO_DATA) {
            mProgressBar.setVisibility(View.GONE);
            mRecentChatList.setVisibility(View.GONE);
            mResultView.setText("没有聊天");
            mResultView.setVisibility(View.VISIBLE);
        }
    }


    private void fillListView(List<RecentChat> recentChatList) {
        mRecentChatAdapter = new RecentChatAdapter(getActivity(), recentChatList);
        mRecentChatList.setAdapter(mRecentChatAdapter);
        mRecentChatAdapter.sort();
    }

    private class InitRecentChatTask extends AsyncTask<Void, Void, List<RecentChat>> {

        @Override
        protected void onPreExecute() {
            if (null == mRecentChatAdapter || mRecentChatAdapter.getCount() == 0) {
                setState(STATE_LOADING);
            }
        }

        @Override
        protected List<RecentChat> doInBackground(Void... params) {
            return MeetDatabase.getInstance().getRecentChatList();
        }

        @Override
        protected void onPostExecute(List<RecentChat> recentChatList) {
            fillListView(recentChatList);
            if (recentChatList.size() > 0) {
                setState(STATE_SUCCESS);
            } else {
                setState(STATE_NO_DATA);
            }
        }

    }

}
