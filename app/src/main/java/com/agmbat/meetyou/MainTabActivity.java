package com.agmbat.meetyou;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.android.utils.AppUtils;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.chat.body.Body;
import com.agmbat.imsdk.chat.body.BodyParser;
import com.agmbat.imsdk.chat.body.EventsBody;
import com.agmbat.imsdk.chat.body.ImageBody;
import com.agmbat.imsdk.chat.body.TextBody;
import com.agmbat.imsdk.chat.body.UrlBody;
import com.agmbat.imsdk.group.GroupManager;
import com.agmbat.imsdk.group.QueryGroupResultIQ;
import com.agmbat.imsdk.imevent.ReceiveSysMessageEvent;
import com.agmbat.imsdk.search.SearchManager;
import com.agmbat.imsdk.search.group.GroupCategory;
import com.agmbat.imsdk.search.group.GroupCategoryResult;
import com.agmbat.imsdk.search.group.OnGetGroupCategoryListener;
import com.agmbat.imsdk.util.AppConfigUtils;
import com.agmbat.isdialog.ISAlertDialog;
import com.agmbat.meetyou.chat.ChangeTabEvent;
import com.agmbat.meetyou.event.UnreadMessageEvent;
import com.agmbat.meetyou.group.GroupDBCache;
import com.agmbat.meetyou.splash.SplashManager;
import com.agmbat.meetyou.tab.contacts.ContactsFragment;
import com.agmbat.meetyou.tab.discovery.DiscoveryFragment;
import com.agmbat.meetyou.tab.msg.MsgFragment;
import com.agmbat.meetyou.tab.profile.ProfileFragment;
import com.agmbat.tab.TabManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.circle.CircleListPacket;
import org.jivesoftware.smackx.message.MessageObject;

import java.util.List;

/**
 * 主Tab界面
 */
public class MainTabActivity extends FragmentActivity {

    public static final int TAB_INDEX_MSG = 0;
    public static final int TAB_INDEX_CONTACT = 1;
    public static final int TAB_INDEX_DISCOVERY = 2;
    public static final int TAB_INDEX_PROFILE = 3;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryGroupList();
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_maintab);
        setupViews();
        EventBus.getDefault().register(this);
        mHandler.postDelayed(mInitRunnable, 1000);

        View unreadView = mTabManager.getTabWidget().getChildTabViewAt(2).findViewById(R.id.unread_count);
        if (AppConfigUtils.hasEventNews(getBaseContext())) {
            unreadView.setVisibility(View.VISIBLE);
        } else {
            unreadView.setVisibility(View.GONE);
        }
    }

    private void queryGroupList() {
        XMPPManager.getInstance().getXmppConnection().addPacketListener(mQueryGroupListener, new PacketTypeFilter(QueryGroupResultIQ.class));
        CircleListPacket circleListPacket = new CircleListPacket(XMPPManager.GROUP_CHAT_SERVER);
        XMPPConnection xmppConnection = XMPPManager.getInstance().getXmppConnection();
        if (xmppConnection.isConnected()) {
            xmppConnection.sendPacket(circleListPacket);
        }
    }

    private PacketListener mQueryGroupListener = new PacketListener() {

        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof QueryGroupResultIQ) {
                final QueryGroupResultIQ queryGroupResultIQ = (QueryGroupResultIQ) packet;
                GroupManager.getInstance().setMemCacheGroups(queryGroupResultIQ.getGroups());
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private TabManager mTabManager;

    private MsgFragment mMsgFragment;

    private void setupViews() {
        mTabManager = new TabManager(getSupportFragmentManager(), findViewById(android.R.id.content));
        View tabMsg = createTabItemView(R.string.tab_msg, R.drawable.tab_msg);
        mMsgFragment = new MsgFragment();
        mTabManager.addTab(tabMsg, "tabMsg", mMsgFragment);
        View tabContacts = createTabItemView(R.string.tab_contacts, R.drawable.tab_contacts);
        mTabManager.addTab(tabContacts, "tabContacts", new ContactsFragment());
        View tabFound = createTabItemView(R.string.tab_found, R.drawable.tab_found);
        mTabManager.addTab(tabFound, "tabFound", new DiscoveryFragment());
        View tabMe = createTabItemView(R.string.tab_profile, R.drawable.tab_profile);
        mTabManager.addTab(tabMe, "tabMe", new ProfileFragment());
        mTabManager.setOnTabChangedListener(new TabManager.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if ("tabContacts".equals(tabId)) {
                    if (XMPPManager.getInstance().isLogin()) {
                        XMPPManager.getInstance().getRosterManager().reloadRoster();
                    }
                } else if ("tabMsg".equals(tabId)) {
                    mMsgFragment.refreshRecentChat();
                }
            }
        });
        mTabManager.setCurrentTab(TAB_INDEX_MSG);
    }

    private View createTabItemView(int textId, int imageId) {
        View v = View.inflate(this, R.layout.view_tab_item, null);
        TextView tv = (TextView) v.findViewById(android.R.id.text1);
        tv.setText(getText(textId));
        tv.setCompoundDrawablesWithIntrinsicBounds(0, imageId, 0, 0);
        return v;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ReceiveSysMessageEvent event) {
        MessageObject msg = event.getMessageObject();
        final Body body = BodyParser.parse(msg.getBody());
        if (body instanceof TextBody) {
            TextBody textBody = (TextBody) body;
            // 如果是文本消息, 则显示文本
            ISAlertDialog dialog = new ISAlertDialog(this);
            dialog.setTitle("系统消息");
            dialog.setMessage(textBody.getContent());
            dialog.setPositiveButton("确定", null);
            dialog.show();
        } else if (body instanceof UrlBody) {
            final UrlBody urlBody = (UrlBody) body;
            ISAlertDialog dialog = new ISAlertDialog(this);
            dialog.setTitle("系统消息");
            dialog.setMessage(urlBody.getContent());
            dialog.setPositiveButton("打开", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppUtils.openBrowser(MainTabActivity.this, urlBody.getContent());
                }
            });
            dialog.setNegativeButton("取消", null);
            dialog.show();
        } else if (body instanceof ImageBody) {
            ImageBody imageBody = (ImageBody) body;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("系统消息");
            ImageView view = new ImageView(this);
            ImageManager.displayImage(imageBody.getFileUrl(), view);
            builder.setView(view);
            builder.setPositiveButton("确定", null);
            builder.create().show();
        } else if (body instanceof EventsBody) {
            AppConfigUtils.setHasEventNews(getBaseContext(), true);
            View unreadView = mTabManager.getTabWidget().getChildTabViewAt(2).findViewById(R.id.unread_count);
            unreadView.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChangeTabEvent event) {
        mTabManager.setCurrentTab(event.getTabIndex());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UnreadMessageEvent event) {
        View unreadView = mTabManager.getTabWidget().getChildTabViewAt(0).findViewById(R.id.unread_count);
        unreadView.setVisibility(event.hasUnread() ? View.VISIBLE : View.GONE);
    }

    private Runnable mInitRunnable = new Runnable() {
        @Override
        public void run() {
            //预先下载群分类
            List<GroupCategory> cachedGroupCategories = GroupDBCache.getGroupCategories();
            if ((null == cachedGroupCategories || cachedGroupCategories.size() == 0)) {
                SearchManager.getGroupCategory(new OnGetGroupCategoryListener() {
                    @Override
                    public void onGetGroupCategory(GroupCategoryResult result) {
                        if (result.mResult && null != result.mData) {
                            GroupDBCache.saveGroupCategories(result.mData);
                        }
                    }

                });
            }
            mMsgFragment.refreshRecentChat();
            SplashManager.update();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (XMPPManager.getInstance().isLogin()) {
            XMPPManager.getInstance().getRosterManager().reloadRoster();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
