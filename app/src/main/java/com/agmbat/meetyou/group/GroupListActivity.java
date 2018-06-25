package com.agmbat.meetyou.group;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.tab.contacts.ContactsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 群列表
 */
public class GroupListActivity extends Activity implements ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener {

    private static final String TAG = ContactsFragment.class.getSimpleName();

    private static final int STATE_LOADING = 0;
    private static final int STATE_LOAD_FINISH = 1;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.friend_list)
    ExpandableListView mListView;

    private GroupAdapter mFriendsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);

        mListView.setOnChildClickListener(this);
        mListView.setOnGroupClickListener(this);
        mListView.setOnCreateContextMenuListener(this);
        mFriendsAdapter = new GroupAdapter(this, new ArrayList<CircleGroup>());
        mListView.setAdapter(mFriendsAdapter);

        setState(STATE_LOADING);
        // 加载数据
        new InitContactListTask().execute();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        int groupCount = mFriendsAdapter.getGroupCount();
        for (int position = 0; position < groupCount; position++) {
            if (position == groupPosition) {
                if (mListView.isGroupExpanded(groupPosition)) {
                    mListView.collapseGroup(groupPosition);
                } else {
                    mListView.expandGroup(groupPosition);
                }
            } else {
                mListView.collapseGroup(position);
            }
        }
        return true;
    }


    private void fillData(List<CircleGroup> groups) {
        mFriendsAdapter.clear();
        mFriendsAdapter.addAll(groups);
        mFriendsAdapter.notifyDataSetChanged();
        if (groups.size() > 0) {
            mListView.expandGroup(0);
        }
    }

    private void setState(int state) {
        if (state == STATE_LOADING) {
            mProgressBar.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else if (state == STATE_LOAD_FINISH) {
            mProgressBar.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    private class InitContactListTask extends AsyncTask<Void, Void, List<CircleGroup>> {

        @Override
        protected void onPreExecute() {
            setState(STATE_LOADING);
        }

        @Override
        protected List<CircleGroup> doInBackground(Void... params) {
            return initGroupList();
        }

        @Override
        protected void onPostExecute(List<CircleGroup> groups) {
            fillData(groups);
            setState(STATE_LOAD_FINISH);
        }

    }

    private List<CircleGroup> initGroupList() {
        List<CircleGroup> groups = new ArrayList<CircleGroup>();

        CircleGroup friendGroup = new CircleGroup();
        List<CircleInfo> friends = new ArrayList<>();
        CircleInfo c1 = new CircleInfo();
        c1.setNickName("群1");
        friends.add(c1);

        CircleInfo c2 = new CircleInfo();
        c2.setNickName("群2");
        friends.add(c2);

        friendGroup.setGroupName("我创建的群");
        friendGroup.setContactList(friends);

        CircleGroup joinGroup = new CircleGroup();
        List<CircleInfo> joinList = new ArrayList<>();
        CircleInfo c3 = new CircleInfo();
        c3.setNickName("群3");
        joinList.add(c3);

        CircleInfo c4 = new CircleInfo();
        c4.setNickName("群4");
        joinList.add(c4);

        joinGroup.setContactList(joinList);
        joinGroup.setGroupName("我加入的群");
        groups.add(friendGroup);
        groups.add(joinGroup);
        return groups;
    }

}
