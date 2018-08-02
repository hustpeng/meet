package com.agmbat.meetyou.tab.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ProgressBar;

import com.agmbat.android.utils.ToastUtil;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.asmack.roster.ContactGroup;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.imsdk.imevent.ContactGroupLoadEvent;
import com.agmbat.imsdk.imevent.ContactListUpdateEvent;
import com.agmbat.imsdk.imevent.PresenceSubscribeEvent;
import com.agmbat.imsdk.util.VLog;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.group.CreateGroupActivity;
import com.agmbat.meetyou.group.GroupListActivity;
import com.agmbat.meetyou.search.NewFriendActivity;
import com.agmbat.meetyou.search.SearchUserActivity;
import com.agmbat.meetyou.search.ViewUserHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 联系人tab界面
 */
public class ContactsFragment extends Fragment implements OnGroupClickListener,
        OnChildClickListener {

    private static final String TAG = ContactsFragment.class.getSimpleName();

    private static final int STATE_LOADING = 0;
    private static final int STATE_LOAD_FINISH = 1;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.friend_list)
    ExpandableListView mListView;

    private ContactsAdapter mFriendsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        VLog.d("onCreate");
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        VLog.d("onCreateView");
        return inflater.inflate(R.layout.tab_fragment_contacts, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        VLog.d("onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        View headerView = View.inflate(getActivity(), R.layout.layout_head_friend, null);
        headerView.findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAdd();
            }
        });
        headerView.findViewById(R.id.btn_new_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewFriendActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        headerView.findViewById(R.id.btn_group_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

            }
        });

        headerView.findViewById(R.id.btn_my_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GroupListActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        mListView.addHeaderView(headerView);
        mListView.setOnChildClickListener(this);
        mListView.setOnGroupClickListener(this);
        mListView.setOnCreateContextMenuListener(this);
        mFriendsAdapter = new ContactsAdapter(getActivity(), new ArrayList<ContactGroup>());
        mListView.setAdapter(mFriendsAdapter);

        setState(STATE_LOADING);
    }

    @Override
    public void onResume() {
        super.onResume();
        VLog.d("onResume");
        // 加载数据
        XMPPManager.getInstance().getRosterManager().loadContactGroupFromDB();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 点击右上角+
     */
    @OnClick(R.id.title_btn_add)
    void onClickAdd() {
        Intent intent = new Intent(getActivity(), SearchUserActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 收到申请添加自己为好友的消息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PresenceSubscribeEvent event) {
        ContactInfo contactInfo = event.getContactInfo();
        ToastUtil.showToastLong("收到添加好友请求" + contactInfo.getBareJid());
    }

    /**
     * 收到list group加载完成
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ContactGroupLoadEvent event) {
        VLog.d("Receive contacts load event: GroupSize=" + event.getDataList().size());
        fillData(event.getDataList());
        setState(STATE_LOAD_FINISH);
    }

    /**
     * 收到更新人列表更新
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ContactListUpdateEvent event) {
        VLog.d("Receive contacts update event: GroupSize=" + event.getList().size());
        mFriendsAdapter.clear();
        mFriendsAdapter.addAll(event.getList());
        mFriendsAdapter.notifyDataSetChanged();
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

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        ContactInfo contactInfo = mFriendsAdapter.getChild(groupPosition, childPosition);
        ViewUserHelper.openContactDetail(getActivity(), contactInfo);
        return false;
    }

    private void fillData(List<ContactGroup> groups) {
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
}
