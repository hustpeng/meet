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

import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.imsdk.db.MeetDatabase;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.tab.contacts.ContactsView;

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
        setContentView(R.layout.activity_new_friend);
        ButterKnife.bind(this);
        mListView.setAdapter(new FriendAdapter(this, MeetDatabase.getInstance().getFriendRequestList()));
        mListView.setOnItemClickListener(this);
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