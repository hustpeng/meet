package com.agmbat.meetyou.tab.msg;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agmbat.android.SysResources;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.imsdk.asmack.MessageManager;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.imsdk.group.CircleInfo;
import com.agmbat.imsdk.group.GroupManager;
import com.agmbat.imsdk.imevent.ContactDeleteEvent;
import com.agmbat.imsdk.imevent.ContactGroupLoadEvent;
import com.agmbat.imsdk.imevent.ReceiveMessageEvent;
import com.agmbat.imsdk.imevent.SendMessageEvent;
import com.agmbat.imsdk.search.SearchManager;
import com.agmbat.imsdk.search.group.GroupInfo;
import com.agmbat.imsdk.search.group.OnSearchGroupListener;
import com.agmbat.imsdk.search.group.SearchGroupResult;
import com.agmbat.imsdk.search.user.OnSearchUserListener;
import com.agmbat.imsdk.search.user.SearchUserResult;
import com.agmbat.isdialog.ISLoadingDialog;
import com.agmbat.log.Log;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.chat.ChatActivity;
import com.agmbat.meetyou.event.UnreadMessageEvent;
import com.agmbat.meetyou.group.GroupInfoActivity;
import com.agmbat.meetyou.search.SearchUserActivity;
import com.agmbat.meetyou.search.ViewUserHelper;
import com.agmbat.menu.MenuInfo;
import com.agmbat.menu.OnClickMenuListener;
import com.agmbat.menu.PopupMenu;
import com.agmbat.swipemenulist.SwipeMenu;
import com.agmbat.swipemenulist.SwipeMenuCreator;
import com.agmbat.swipemenulist.SwipeMenuItem;
import com.agmbat.swipemenulist.SwipeMenuListView;
import com.agmbat.zxing.OnScanListener;
import com.agmbat.zxing.ScannerHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.message.MessageObject;
import org.jivesoftware.smackx.message.MessageObjectStatus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Tab聊天
 */
public class MsgFragment extends Fragment {

    private static final String TAG = MsgFragment.class.getSimpleName();

    private static final int STATE_LOADING = 0;
    private static final int STATE_SUCCESS = 1;
    private static final int STATE_NO_DATA = 2;

    @BindView(R.id.recent_chat_list)
    SwipeMenuListView mListView;

    @BindView(R.id.result)
    TextView mResultView;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    /**
     * 聊天记录的adapter
     */
    private RecentChatAdapter mAdapter;

    /**
     * loading对话框
     */
    private ISLoadingDialog mISLoadingDialog;

    private List<MessageObject> mRecentMessages = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.tab_fragment_msg, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        mAdapter = new RecentChatAdapter(getActivity(), mRecentMessages);
        mListView.setAdapter(mAdapter);

        // step 1. create a MenuCreator
        final SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = createDeleteMenuItem();
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);

        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        MessageObject item = mAdapter.getItem(position);
                        mRecentMessages.remove(item);
                        mAdapter.notifyDataSetChanged();
                        refreshState();
                        XMPPManager.getInstance().getMessageManager().deleteMessage(item);
                        break;
                }
                return false;
            }
        });
        isInitialed = true;
    }

    private boolean isInitialed;

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        refreshRecentChat();
    }

    private InitRecentChatTask mInitRecentChatTask;

    public void refreshRecentChat() {
        if (!isInitialed) {
            return;
        }
        if (null == mInitRecentChatTask) {
            mInitRecentChatTask = new InitRecentChatTask();
            mInitRecentChatTask.execute();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnItemClick(R.id.recent_chat_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MessageObject recentChat = mAdapter.getItem(position);
        if (recentChat.getChatType() == Message.Type.chat) {
            ContactInfo contactInfo = MessageManager.getTalkContactInfo(recentChat);
            if (contactInfo != null) {
                ChatActivity.openChat(getActivity(), contactInfo);
            }
        } else if (recentChat.getChatType() == Message.Type.groupchat) {
            CircleInfo groupBean = GroupManager.getInstance().getMemCacheGroup(MessageManager.getTalkJid(recentChat));
            if (groupBean != null) {
                ChatActivity.openGroupChat(getActivity(), groupBean);
            }
        }
    }

    @OnClick(R.id.title_btn_add)
    void onClickAdd(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext());

        MenuInfo groupChat = new MenuInfo();
        groupChat.setTitle(getString(R.string.menu_groupchat));
        groupChat.setIcon(getResources().getDrawable(R.drawable.im_ic_menu_group));
        groupChat.setOnClickMenuListener(new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {

            }
        });
        popupMenu.addItem(groupChat);

        MenuInfo addFriend = new MenuInfo();
        addFriend.setTitle(getString(R.string.menu_addfriend));
        addFriend.setIcon(getResources().getDrawable(R.drawable.im_ic_menu_addfriend));
        addFriend.setOnClickMenuListener(new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {
                Intent intent = new Intent(getActivity(), SearchUserActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        popupMenu.addItem(addFriend);

        MenuInfo scan = new MenuInfo();
        scan.setTitle(getString(R.string.menu_qrcode));
        scan.setIcon(getResources().getDrawable(R.drawable.im_ic_menu_sao));
        scan.setOnClickMenuListener(new OnClickMenuListener() {
            @Override
            public void onClick(MenuInfo menu, int index) {
                scanBarCode();
            }
        });
        popupMenu.addItem(scan);

        View v = (View) view.getParent();
        popupMenu.show(v);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ReceiveMessageEvent event) {
        MessageObject messageObject = event.getMessageObject();
        updateRecentMsgList(messageObject);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SendMessageEvent event) {
        MessageObject messageObject = event.getMessageObject();
        updateRecentMsgList(messageObject);
    }

    /**
     * 删除了联系人事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ContactDeleteEvent event) {
        String jid = event.getJid();
        MessageObject existTalkMessage = findTalkMessage(jid);
        if (existTalkMessage != null) {
            mRecentMessages.remove(existTalkMessage);
            mAdapter.notifyDataSetChanged();
            refreshState();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ContactGroupLoadEvent event) {
        refreshRecentChat();
    }


    /**
     * 打开相机开始扫描二维码
     */
    private void scanBarCode() {
        ScannerHelper.scan(getActivity(), new OnScanListener() {
            @Override
            public void onScan(String text) {
                if (!TextUtils.isEmpty(text)) {
                    if(text.startsWith(SearchManager.PREFIX_USER)) {
                        searchUser(text.replace(SearchManager.PREFIX_USER, ""));
                    }else if(text.startsWith(SearchManager.PREFIX_GROUP)){
                        searchGroup(text.replace(SearchManager.PREFIX_GROUP, ""));
                    }else{
                        ToastUtil.showToast("二维码格式有误");
                    }
                }else{
                    ToastUtil.showToast("二维码内容为空");
                }
            }
        });
    }

    /**
     * 搜索用户
     *
     * @param uid
     */
    private void searchUser(String uid) {
        showLoadingDialog();
        SearchManager.searchUser(uid, new OnSearchUserListener() {
            @Override
            public void onSearchUser(SearchUserResult result) {
                hideLoadingDialog();
                if (result.mResult) {
                    ContactInfo contactInfo = result.mData;
                    if (contactInfo == null) {
                        ToastUtil.showToast("未搜索到用户");
                    } else {
                        ViewUserHelper.openStrangerDetail(getActivity(), contactInfo);
                    }
                } else {
                    ToastUtil.showToast("搜索用户失败!");
                }
            }

        });
    }

    /**
     * 搜索群组
     *
     * @param uid
     */
    private void searchGroup(String uid) {
        showLoadingDialog();
        SearchManager.searchGroup(uid, new OnSearchGroupListener() {
            @Override
            public void onSearchGroup(SearchGroupResult result) {
                hideLoadingDialog();
                if (result.mResult) {
                    List<GroupInfo> groupInfos = result.mData;
                    if (groupInfos == null || groupInfos.size() == 0) {
                        ToastUtil.showToast("未搜索到群组");
                    } else {
                        GroupInfo groupInfo = groupInfos.get(0);
                        GroupInfoActivity.launch(getContext(), groupInfo);
                    }
                } else {
                    ToastUtil.showToast("搜索群组失败!");
                }
            }

        });
    }

    /**
     * 更新聊天列表
     *
     * @param messageObject
     */
    private void updateRecentMsgList(MessageObject messageObject) {
        MessageObject existTalkMessage = findTalkMessage(MessageManager.getTalkJid(messageObject));
        if (existTalkMessage != null) {
            mRecentMessages.remove(existTalkMessage);
        }
        mRecentMessages.add(0, messageObject);
        MessageObject.sortByDate(mRecentMessages, false);
        mAdapter.notifyDataSetChanged();
        refreshState();
        if (messageObject.getMsgStatus() == MessageObjectStatus.UNREAD) {
            EventBus.getDefault().post(new UnreadMessageEvent(true));
        }
    }


    /**
     * 查找已存的最近对话
     *
     * @param jid
     * @return
     */
    private MessageObject findTalkMessage(String jid) {
        for (int i = 0; i < mRecentMessages.size(); i++) {
            MessageObject exist = mRecentMessages.get(i);
            if (jid.equals(MessageManager.getTalkJid(exist))) {
                return exist;
            }
        }
        return null;
    }

    /**
     * 设置状态
     *
     * @param state
     */
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
            mListView.setVisibility(View.VISIBLE);
            mResultView.setText("没有聊天");
            mResultView.setVisibility(View.VISIBLE);
        }
    }

    private class InitRecentChatTask extends AsyncTask<Void, Void, List<MessageObject>> {

        @Override
        protected void onPreExecute() {
            if (mRecentMessages.size() == 0) {
                setState(STATE_LOADING);
            }
        }

        @Override
        protected List<MessageObject> doInBackground(Void... params) {
            XMPPManager.getInstance().getRosterManager().loadContactGroupFromDBSync();
            String user = XMPPManager.getInstance().getXmppConnection().getBareJid();
            return XMPPManager.getInstance().getMessageManager().getRecentMessage(user);
        }

        @Override
        protected void onPostExecute(List<MessageObject> recentChatList) {
            mRecentMessages.clear();
            mRecentMessages.addAll(recentChatList);
            mAdapter.notifyDataSetChanged();
            refreshState();
            boolean hasUnread = hasUnreadMessage(recentChatList);
            EventBus.getDefault().post(new UnreadMessageEvent(hasUnread));
            mInitRecentChatTask = null;
        }
    }

    private boolean hasUnreadMessage(List<MessageObject> recentChatList) {
        boolean hasUnread = false;
        for (int i = 0; i < recentChatList.size(); i++) {
            MessageObject messageObject = recentChatList.get(i);
            if (messageObject.getMsgStatus() == MessageObjectStatus.UNREAD) {
                hasUnread = true;
                break;
            }
        }
        return hasUnread;
    }

    /**
     * 刷新状态显示
     */
    private void refreshState() {
        if (mRecentMessages.size() > 0) {
            setState(STATE_SUCCESS);
        } else {
            setState(STATE_NO_DATA);
        }
    }

    private SwipeMenuItem createOpenMenuItem() {
        // create "open" item
        SwipeMenuItem openItem = new SwipeMenuItem(getActivity().getApplicationContext());
        // set item background
        openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                0xCE)));
        // set item width
        openItem.setWidth((int) SysResources.dipToPixel(90));
        // set item title
        openItem.setTitle("Open");
        // set item title fontsize
        openItem.setTitleSize(18);
        // set item title font color
        openItem.setTitleColor(Color.WHITE);
        return openItem;
    }

    /**
     * 创建删除的menu item
     *
     * @return
     */
    private SwipeMenuItem createDeleteMenuItem() {
        // create "delete" item
        SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
        // set item background
        deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
        // set item width
        deleteItem.setWidth((int) SysResources.dipToPixel(90));
        deleteItem.setTitle("删除");
        deleteItem.setTitleColor(Color.WHITE);
        deleteItem.setTitleSize(18);
        // set a icon
        return deleteItem;
    }

    /**
     * 显示loading框
     */
    private void showLoadingDialog() {
        if (mISLoadingDialog == null) {
            mISLoadingDialog = new ISLoadingDialog(getActivity());
            mISLoadingDialog.setMessage("正在搜索...");
            mISLoadingDialog.setCancelable(false);
        }
        mISLoadingDialog.show();
    }

    /**
     * 隐藏loading框
     */
    private void hideLoadingDialog() {
        if (mISLoadingDialog != null) {
            mISLoadingDialog.dismiss();
        }
    }
}
