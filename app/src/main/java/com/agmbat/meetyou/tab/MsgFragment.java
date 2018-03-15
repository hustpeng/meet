package com.agmbat.meetyou.tab;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        setupViews();
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

    private RecentChat queryRecentChatFor(ContactInfo contactInfo) {
        RecentChat recentChat = new RecentChat();
        recentChat.setContact(contactInfo);
        ChatMessage lastChatMessage = new ChatMessage();
        recentChat.setUnreadCount(5);
        recentChat.setLastChatMessage(lastChatMessage);
        return recentChat;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RecentChat recentChat = mRecentChatAdapter.getItem(position);
//        Intent intent = new Intent(getActivity(), ChatActivity.class);
//        intent.putExtra(Constants.EXTRA_PARTICIPANT, recentChat.getContact());
//        startActivityForResult(intent, REQUEST_CODE_CHAT);
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

    private void setupViews() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRecentChatList = (ListView) findViewById(R.id.recent_chat_list);
        mRecentChatList.setOnItemClickListener(this);
        mResultView = (TextView) findViewById(R.id.result);
    }

    private View findViewById(int id) {
        return getView().findViewById(id);
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
            List<RecentChat> recentChatList = new ArrayList<RecentChat>();
            return recentChatList;
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

    public static class RecentChatAdapter extends ArrayAdapter<RecentChat> {

        public RecentChatAdapter(Context context, List<RecentChat> contactList) {
            super(context, 0, contactList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = new RecentMsgView(getContext());
            }
            RecentChat recentChat = getItem(position);
            RecentMsgView view = (RecentMsgView) convertView;
            view.update(recentChat);
            return convertView;
        }

        public void updateRecentChat(RecentChat recentChat) {
            int count = getCount();
            for (int i = 0; i < count; i++) {
                RecentChat current = getItem(i);
                if (current.getContact().equals(recentChat.getContact())) {
                    current.setContact(recentChat.getContact());
                    current.setLastChatMessage(recentChat.getLastChatMessage());
                    current.setUnreadCount(recentChat.getUnreadCount());
                    break;
                }
            }
            notifyDataSetChanged();
            notifyDataSetInvalidated();
        }

        public boolean isRecentChatExist(RecentChat recentChat) {
            boolean exist = false;
            int count = getCount();
            for (int i = 0; i < count; i++) {
                RecentChat current = getItem(i);
                if (current.getContact().equals(recentChat.getContact())) {
                    exist = true;
                    break;
                }
            }
            return exist;
        }

        public void addRecentChat(RecentChat recentChat) {
            add(recentChat);
            notifyDataSetChanged();
        }

        public void sort() {
            super.sort(new RecentChat.RecentChatComparator());
            notifyDataSetChanged();
        }


    }
}
