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
import com.agmbat.imsdk.asmack.roster.RosterManager;
import com.agmbat.imsdk.chat.body.Body;
import com.agmbat.imsdk.chat.body.BodyParser;
import com.agmbat.imsdk.chat.body.ImageBody;
import com.agmbat.imsdk.chat.body.TextBody;
import com.agmbat.imsdk.chat.body.UrlBody;
import com.agmbat.imsdk.imevent.ReceiveSysMessageEvent;
import com.agmbat.imsdk.search.SearchManager;
import com.agmbat.imsdk.search.group.GroupCategory;
import com.agmbat.imsdk.search.group.GroupCategoryResult;
import com.agmbat.imsdk.search.group.OnGetGroupCategoryListener;
import com.agmbat.isdialog.ISAlertDialog;
import com.agmbat.meetyou.group.GroupDBCache;
import com.agmbat.meetyou.tab.contacts.ContactsFragment;
import com.agmbat.meetyou.tab.discovery.DiscoveryFragment;
import com.agmbat.meetyou.tab.msg.MsgFragment;
import com.agmbat.meetyou.tab.profile.ProfileFragment;
import com.agmbat.tab.TabManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.message.MessageObject;

import java.util.List;

/**
 * 主Tab界面
 */
public class MainTabActivity extends FragmentActivity {

    public static final int TAB_INDEX_MSG = 0;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_maintab);
        setupViews();
        EventBus.getDefault().register(this);
        mHandler.postDelayed(mInitRunnable, 1500);
    }

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

    private void setupViews() {
        TabManager tabManager = new TabManager(getSupportFragmentManager(), findViewById(android.R.id.content));
        View tabMsg = createTabItemView(R.string.tab_msg, R.drawable.tab_msg);
        tabManager.addTab(tabMsg, "tabMsg", new MsgFragment());
        View tabContacts = createTabItemView(R.string.tab_contacts, R.drawable.tab_contacts);
        tabManager.addTab(tabContacts, "tabContacts", new ContactsFragment());
        View tabFound = createTabItemView(R.string.tab_found, R.drawable.tab_found);
        tabManager.addTab(tabFound, "tabFound", new DiscoveryFragment());
        View tabMe = createTabItemView(R.string.tab_profile, R.drawable.tab_profile);
        tabManager.addTab(tabMe, "tabMe", new ProfileFragment());
        tabManager.setCurrentTab(TAB_INDEX_MSG);
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
        }
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
        }
    };

}
