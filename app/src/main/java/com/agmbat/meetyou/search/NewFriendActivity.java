package com.agmbat.meetyou.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.imevent.PresenceSubscribeEvent;
import com.agmbat.imsdk.user.UserManager;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.tab.contacts.ContactsView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 新的朋友申请列表
 */
public class NewFriendActivity extends Activity implements AdapterView.OnItemClickListener {

    @BindView(android.R.id.list)
    ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_new_friend);
        ButterKnife.bind(this);
        mListView.setAdapter(new FriendAdapter(this, UserManager.getInstance().getFriendRequestList()));
        mListView.setOnItemClickListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ContactInfo info = (ContactInfo) parent.getItemAtPosition(position);
        Intent intent = new Intent(this, UserInfoVerifyActivity.class);
        intent.putExtra("userInfo", info.getBareJid());
        startActivity(intent);
    }

    /**
     * 收到申请添加自己为好友的消息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PresenceSubscribeEvent event) {
        FriendAdapter adapter = (FriendAdapter) mListView.getAdapter();
        adapter.notifyDataSetChanged();
    }

    private static class FriendAdapter extends ArrayAdapter<ContactInfo> {

        public FriendAdapter(@NonNull Context context, List<ContactInfo> list) {
            super(context, 0, list);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = new ContactsView(getContext());
            }
            ContactsView view = (ContactsView) convertView;
            ContactInfo contactInfo = getItem(position);
            view.update(contactInfo);
            return convertView;
        }

    }
}