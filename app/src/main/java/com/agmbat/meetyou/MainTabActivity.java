package com.agmbat.meetyou;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.agmbat.meetyou.tab.contacts.ContactsFragment;
import com.agmbat.meetyou.tab.found.FoundFragment;
import com.agmbat.meetyou.tab.msg.MsgFragment;
import com.agmbat.meetyou.tab.me.MeFragment;
import com.agmbat.tab.TabManager;

/**
 * 主Tab界面
 */
public class MainTabActivity extends FragmentActivity {

    public static final int TAB_INDEX_MSG = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintab);
        setupViews();
//        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void setupViews() {
        TabManager tabManager = new TabManager(getSupportFragmentManager(), findViewById(android.R.id.content));
        View tabMsg = createTabItemView(R.string.tab_msg, R.drawable.tab_msg);
        tabManager.addTab(tabMsg, "tabMsg", new MsgFragment());
        View tabContacts = createTabItemView(R.string.tab_friend, R.drawable.tab_contacts);
        tabManager.addTab(tabContacts, "tabContacts", new ContactsFragment());
        View tabFound = createTabItemView(R.string.tab_found, R.drawable.tab_found);
        tabManager.addTab(tabFound, "tabFound", new FoundFragment());
        View tabMe = createTabItemView(R.string.tab_me, R.drawable.tab_me);
        tabManager.addTab(tabMe, "tabMe", new MeFragment());
        tabManager.setCurrentTab(TAB_INDEX_MSG);
    }

    private View createTabItemView(int textId, int imageId) {
        View v = View.inflate(this, R.layout.view_tab_item, null);
        TextView tv = (TextView) v.findViewById(android.R.id.text1);
        tv.setText(getText(textId));
        tv.setCompoundDrawablesWithIntrinsicBounds(0, imageId, 0, 0);
        return v;
    }

}
