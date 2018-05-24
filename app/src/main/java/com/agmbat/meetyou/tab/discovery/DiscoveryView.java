package com.agmbat.meetyou.tab.discovery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.meetyou.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscoveryView extends FrameLayout {

    @BindView(R.id.title)
    TextView mTitleView;

    @BindView(R.id.user1)
    DiscoveryUserItemView mUser1View;

    @BindView(R.id.user2)
    DiscoveryUserItemView mUser2View;

    @BindView(R.id.user3)
    DiscoveryUserItemView mUser3View;

    @BindView(R.id.user4)
    DiscoveryUserItemView mUser4View;


    public DiscoveryView(@NonNull Context context) {
        super(context);
        View.inflate(context, R.layout.found_item, this);
        ButterKnife.bind(this, this);
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
