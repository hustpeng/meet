package com.agmbat.meetyou.tab.found;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.meetyou.R;

import java.util.List;

public class FoundView extends FrameLayout {

    private TextView mTitleView;

    private FoundUserItemView mUser1View;
    private FoundUserItemView mUser2View;
    private FoundUserItemView mUser3View;
    private FoundUserItemView mUser4View;


    public FoundView(@NonNull Context context) {
        super(context);
        View.inflate(context, R.layout.found_item, this);
        mTitleView = (TextView) findViewById(R.id.title);
        mUser1View = (FoundUserItemView) findViewById(R.id.user1);
        mUser2View = (FoundUserItemView) findViewById(R.id.user2);
        mUser3View = (FoundUserItemView) findViewById(R.id.user3);
        mUser4View = (FoundUserItemView) findViewById(R.id.user4);
    }

    public void update(FoundGroup group) {
        mTitleView.setText(group.getTitle());
        List<ContactInfo> userList = group.getUserList();
        mUser1View.bindUser(userList.get(0));
        mUser2View.bindUser(userList.get(1));
        mUser3View.bindUser(userList.get(2));
        mUser4View.bindUser(userList.get(3));
    }
}
