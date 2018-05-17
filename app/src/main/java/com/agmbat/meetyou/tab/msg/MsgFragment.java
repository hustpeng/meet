package com.agmbat.meetyou.tab.msg;

import android.content.Intent;
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
import com.agmbat.imsdk.asmack.MessageManager;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.imevent.ReceiveMessageEvent;
import com.agmbat.imsdk.imevent.SendMessageEvent;
import com.agmbat.log.Log;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.chat.ChatActivity;
import com.agmbat.meetyou.search.SearchUserActivity;
import com.agmbat.menu.MenuInfo;
import com.agmbat.menu.OnClickMenuListener;
import com.agmbat.menu.PopupMenu;
import com.agmbat.swipemenulist.SwipeMenu;
import com.agmbat.swipemenulist.SwipeMenuCreator;
import com.agmbat.swipemenulist.SwipeMenuItem;
import com.agmbat.swipemenulist.SwipeMenuListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.message.MessageObject;

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

    private RecentChatAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.tab_fragment_msg, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
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
        EventBus.getDefault().unregister(this);
    }

    @OnItemClick(R.id.recent_chat_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MessageObject recentChat = mAdapter.getItem(position);
        ContactInfo contactInfo = MessageManager.getTalkContactInfo(recentChat);
        if (contactInfo != null) {
            ChatActivity.openChat(getActivity(), contactInfo);
        }
    }

    @OnClick(R.id.title_btn_add)
    void onClickAdd() {
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

            }
        });
        popupMenu.addItem(scan);

        View view = (View) getView().findViewById(R.id.title_btn_add).getParent();
        popupMenu.show(view);
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

    private void updateRecentMsgList(MessageObject messageObject) {
        MessageObject existTalkMessage = findTalkMessage(MessageManager.getTalkJid(messageObject));
        if (existTalkMessage != null) {
            mAdapter.remove(existTalkMessage);
        }
        mAdapter.insert(messageObject, 0);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 查找已存的最近对话
     *
     * @param jid
     * @return
     */
    private MessageObject findTalkMessage(String jid) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            MessageObject exist = mAdapter.getItem(i);
            if (jid.equals(MessageManager.getTalkJid(exist))) {
                return exist;
            }
        }
        return null;
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

    private void fillListView(List<MessageObject> recentChatList) {
        mAdapter = new RecentChatAdapter(getActivity(), recentChatList);
        mListView.setAdapter(mAdapter);
//        mAdapter.sort();
    }

    private class InitRecentChatTask extends AsyncTask<Void, Void, List<MessageObject>> {

        @Override
        protected void onPreExecute() {
            if (null == mAdapter || mAdapter.getCount() == 0) {
                setState(STATE_LOADING);
            }
        }

        @Override
        protected List<MessageObject> doInBackground(Void... params) {
            String user = XMPPManager.getInstance().getXmppConnection().getBareJid();
            return XMPPManager.getInstance().getMessageManager().getAllMessage(user);
        }

        @Override
        protected void onPostExecute(List<MessageObject> recentChatList) {
            fillListView(recentChatList);
            if (recentChatList.size() > 0) {
                setState(STATE_SUCCESS);
            } else {
                setState(STATE_NO_DATA);
            }
        }
    }

}
