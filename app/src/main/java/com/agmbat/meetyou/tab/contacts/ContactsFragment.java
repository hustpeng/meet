package com.agmbat.meetyou.tab.contacts;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ProgressBar;

import com.agmbat.android.utils.ToastUtil;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.imevent.PresenceSubscribeEvent;
import com.agmbat.meetyou.NewFriendActivity;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.db.MeetDatabase;
import com.agmbat.meetyou.search.SearchUserActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactsFragment extends Fragment implements OnGroupClickListener,
        OnChildClickListener, OnCreateContextMenuListener {

    private static final String TAG = ContactsFragment.class.getSimpleName();

    private static final int MENU_INDEX_ADD_BLOCK_USER = 1;
    private static final int MENU_INDEX_REMOVE_BLOCK_USER = 2;
    private static final int MENU_INDEX_REMOVE_FRIENDS = 3;
    private static final int MENU_INDEX_REMOVE_RECENTLY = 4;

    private static final int STATE_LOADING = 0;
    private static final int STATE_LOAD_FINISH = 1;

    private InitContactListTask mInitListTask = null;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.friend_list)
    ExpandableListView mListView;

    private ContactsAdapter mFriendsAdapter;

    private String mLoginUserName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_contacts, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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

            }
        });
        mListView.addHeaderView(headerView);
        mListView.setOnChildClickListener(this);
        mListView.setOnGroupClickListener(this);
        mListView.setOnCreateContextMenuListener(this);
        initContactList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

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

    private void initContactList() {
        if (null == mInitListTask) {
            mInitListTask = new InitContactListTask();
            mInitListTask.execute();
        }
    }

    private void updateRecentList() {
//        MeetDatabase dataManager = MeetDatabase.getInstance();
//        List<ContactInfo> recentContactList = dataManager
//                .queryRecentContacts(mLoginUserName);
//        for (ContactInfo contactInfo : recentContactList) {
//            if (dataManager.isBlockUserExist(mLoginUserName, contactInfo.getUserName())) {
//                dataManager
//                        .deleteRecentContactByUserName(mLoginUserName, contactInfo.getUserName());
//            } else {
//                contactInfo = mRosterManager.ensureContactInformation(mLoginUserName,
//                        contactInfo);
//                contactInfo.setGroups(GroupHolder.GROUP_RECENTLY);
//                dataManager.updateRecentContact(contactInfo, false);
//            }
//        }
    }

    private void updateFriendList() {
    }


    private void fillData(List<GroupHolder> groups) {
        mFriendsAdapter = new ContactsAdapter(getActivity(), groups);
        mListView.setAdapter(mFriendsAdapter);
        mListView.expandGroup(0);
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
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                int childPosition, long id) {
//        ContactInfo contactInfo = mFriendsAdapter.getChild(groupPosition, childPosition);
//        boolean isBlockUser = MeetDatabase.getInstance().isBlockUserExist(mLoginUserName,
//                contactInfo.getUserName());
//        if (isBlockUser) {
//            GlobalToast.showToast(R.string.tips_could_not_char_with_block_user);
//        } else {
//            ChatActivity.openChat(getActivity(), contactInfo);
//        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuInfo;
//        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
//        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
//            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
//            menu.setHeaderTitle(R.string.context_menu_title);
//            if (groupPos == ContactsAdapter.GROUP_INDEX_HOTLIST) {
//                menu.add(0, MENU_INDEX_ADD_BLOCK_USER, 0, R.string.context_menu_add_block_user);
//                menu.add(0, MENU_INDEX_REMOVE_FRIENDS, 0, R.string.context_menu_remove_hotlist);
//            } else if (groupPos == ContactsAdapter.GROUP_INDEX_RECENTLY) {
//                menu.add(0, MENU_INDEX_ADD_BLOCK_USER, 0, R.string.context_menu_add_block_user);
//                menu.add(0, MENU_INDEX_REMOVE_RECENTLY, 0, R.string.context_menu_remove_recently);
//            } else if (groupPos == ContactsAdapter.GROUP_INDEX_BLOCK) {
//                menu.add(0, MENU_INDEX_REMOVE_BLOCK_USER, 0,
//                        R.string.context_menu_remove_block_user);
//            }
//        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
//        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
//        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
//        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
//            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
//            int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
//            ContactInfo contactInfo = mFriendsAdapter.getChild(groupPos, childPos);
//
//            if (item.getItemId() == MENU_INDEX_ADD_BLOCK_USER) {
//                if (ConnectionHelper.checkServerConnection()) {
//                    MeetDatabase dataManager = MeetDatabase.getInstance();
//                    int blockCount = dataManager.queryBlockUsersCount(mLoginUserName);
//                    if (blockCount < RosterManager.MAX_BLOCK_COUNT) {
//                        BlockUserTask blockUserTask = new BlockUserTask(contactInfo);
//                        blockUserTask.execute();
//                    } else {
//                        GlobalToast.showToast(
//                                getString(R.string.tips_block_user_reach_max,
//                                        RosterManager.MAX_BLOCK_COUNT));
//                    }
//                }
//            } else if (item.getItemId() == MENU_INDEX_REMOVE_BLOCK_USER) {
//                if (ConnectionHelper.checkServerConnection()) {
//                    RemoveBlockUserTask removeBlockUserTask = new RemoveBlockUserTask(contactInfo);
//                    removeBlockUserTask.execute();
//                }
//            } else if (item.getItemId() == MENU_INDEX_REMOVE_FRIENDS) {
//                if (ConnectionHelper.checkServerConnection()) {
//                    showRemoveFriendDialog(contactInfo);
//                }
//            } else if (item.getItemId() == MENU_INDEX_REMOVE_RECENTLY) {
//                removeRecentlyContact(contactInfo);
//            }
//        }
        return super.onContextItemSelected(item);
    }

    private void removeRecentlyContact(ContactInfo contactInfo) {
    }

    private class InitContactListTask extends AsyncTask<Void, Void, List<GroupHolder>> {

        @Override
        protected void onPreExecute() {
            setState(STATE_LOADING);
        }

        @Override
        protected List<GroupHolder> doInBackground(Void... params) {
            return MeetDatabase.getInstance().getGroupList(mLoginUserName);
        }

        @Override
        protected void onPostExecute(List<GroupHolder> groups) {
            fillData(groups);
            setState(STATE_LOAD_FINISH);
        }

    }

}
