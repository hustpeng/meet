package com.agmbat.meetyou.tab.discovery2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.agmbat.debugger.R;
import com.agmbat.imsdk.asmack.roster.ContactInfo;

import java.util.List;


public class DiscoveryView extends FrameLayout {

    TextView mTitleView;

    DiscoveryUserItemView mUser1View;

    DiscoveryUserItemView mUser2View;

    DiscoveryUserItemView mUser3View;

    DiscoveryUserItemView mUser4View;


    public DiscoveryView(@NonNull Context context) {
        super(context);
        View.inflate(context, R.layout.found_item, this);
        mTitleView = findViewById(R.id.title);
        mUser1View = findViewById(R.id.user1);
        mUser2View = findViewById(R.id.user2);
        mUser3View = findViewById(R.id.user3);
        mUser4View = findViewById(R.id.user4);
    }

    public void update(DiscoveryGroup group) {
        mTitleView.setText(group.getTitle());
        List<ContactInfo> userList = group.getUserList();
        mUser1View.bindUser(userList.get(0));
        mUser2View.bindUser(userList.get(1));
        mUser3View.bindUser(userList.get(2));
        mUser4View.bindUser(userList.get(3));
    }
}
