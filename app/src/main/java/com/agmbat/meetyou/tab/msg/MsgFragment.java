package com.agmbat.meetyou.tab.msg;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.log.Log;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.chat.ChatActivity;
import com.agmbat.meetyou.data.RecentChat;
import com.agmbat.meetyou.db.MeetDatabase;
import com.agmbat.swipemenulist.SwipeMenu;
import com.agmbat.swipemenulist.SwipeMenuCreator;
import com.agmbat.swipemenulist.SwipeMenuItem;
import com.agmbat.swipemenulist.SwipeMenuListView;

import java.util.List;

/**
 * Tab聊天
 */
public class MsgFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = MsgFragment.class.getSimpleName();

    private static final int STATE_LOADING = 0;
    private static final int STATE_SUCCESS = 1;
    private static final int STATE_NO_DATA = 2;

    private SwipeMenuListView mListView;
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
        mListView = (SwipeMenuListView) view.findViewById(R.id.recent_chat_list);
        mListView.setOnItemClickListener(this);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth((int) AppResources.dipToPixel(90));
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth((int) AppResources.dipToPixel(90));
                // set a icon
//                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);

        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                ApplicationInfo item = mAppList.get(position);
                switch (index) {
                    case 0:
                        // open
//                        open(item);
                        break;
                    case 1:
                        // delete
//					delete(item);
//                        mAppList.remove(position);
//                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });

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
        ContactInfo contactInfo = recentChat.getContact();
        ChatActivity.openChat(getActivity(), contactInfo);
    }

    private void setState(int state) {
        if (state == STATE_LOADING) {
            mProgressBar.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            mResultView.setVisibility(View.GONE);
        } else if (state == STATE_SUCCESS) {
            mProgressBar.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mResultView.setVisibility(View.GONE);
        } else if (state == STATE_NO_DATA) {
            mProgressBar.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            mResultView.setText("没有聊天");
            mResultView.setVisibility(View.VISIBLE);
        }
    }


    private void fillListView(List<RecentChat> recentChatList) {
        mRecentChatAdapter = new RecentChatAdapter(getActivity(), recentChatList);
        mListView.setAdapter(mRecentChatAdapter);
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
