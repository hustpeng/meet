package com.agmbat.meetyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.meetyou.tab.contacts.ContactsFragment;
import com.agmbat.meetyou.tab.discovery.Discovery2Fragment;
import com.agmbat.meetyou.tab.msg.MsgFragment;
import com.agmbat.meetyou.tab.profile.ProfileFragment;
import com.agmbat.tab.TabManager;

/**
 * 主Tab界面
 */
public class MainTabActivity extends FragmentActivity {

    public static final int TAB_INDEX_MSG = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_maintab);
        setupViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        tabManager.addTab(tabFound, "tabFound", new Discovery2Fragment());
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

}
