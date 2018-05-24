package com.agmbat.meetyou.tab.discovery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.discovery.search.DiscoveryHelper;
import com.agmbat.meetyou.tab.discovery.card.CardInfo;
import com.agmbat.meetyou.tab.discovery.card.HeaderCard;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 发现tab
 */
public class DiscoveryFragment extends Fragment {

    @BindView(android.R.id.list)
    ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_found, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        HeaderCard headerCard = new HeaderCard(getActivity());
        List<CardInfo> list = new ArrayList<>();
        list.add(new CardInfo("在线会员"));
        list.add(nearby());
        list.add(lover());
        list.add(hobby());

        list.add(new CardInfo("找同行"));
        list.add(birthplace());
        list.add(new CardInfo("找群组"));
        list.add(new CardInfo("创建群组"));
        headerCard.setHeaderItemList(list);
        mListView.addHeaderView(headerCard);

        List<ContactInfo> userList = new ArrayList<>();
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setNickname("布丁");
        userList.add(contactInfo);
        userList.add(contactInfo);
        userList.add(contactInfo);
        userList.add(contactInfo);

        List<DiscoveryGroup> dataList = new ArrayList<>();
        DiscoveryGroup group = new DiscoveryGroup();
        group.setTitle(AppResources.getString(R.string.discovery_nearby_users));
        group.setUserList(userList);
        dataList.add(group);

        group = new DiscoveryGroup();
        group.setTitle(AppResources.getString(R.string.discovery_lover));
        group.setUserList(userList);
        dataList.add(group);

        group = new DiscoveryGroup();
        group.setTitle(AppResources.getString(R.string.discovery_hobby));
        group.setUserList(userList);
        dataList.add(group);

        group = new DiscoveryGroup();
        group.setTitle("找同行");
        group.setUserList(userList);
        dataList.add(group);

        group = new DiscoveryGroup();
        group.setTitle(AppResources.getString(R.string.discovery_birthplace));
        group.setUserList(userList);
        dataList.add(group);

        group = new DiscoveryGroup();
        group.setTitle("找群组");
        group.setUserList(userList);
        dataList.add(group);

        DiscoveryAdapter adapter = new DiscoveryAdapter(getActivity(), dataList);
        mListView.setAdapter(adapter);
    }

    private CardInfo nearby() {
        CardInfo info = new CardInfo(AppResources.getString(R.string.discovery_nearby_users));
        info.mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscoveryHelper.openNearByUsers(getActivity());
            }
        };
        return info;
    }

    private CardInfo lover() {
        CardInfo info = new CardInfo(AppResources.getString(R.string.discovery_lover));
        info.mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscoveryHelper.openLover(getActivity());
            }
        };
        return info;
    }

    private CardInfo hobby() {
        CardInfo info = new CardInfo(AppResources.getString(R.string.discovery_hobby));
        info.mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscoveryHelper.openHobby(getActivity());
            }
        };
        return info;
    }

    private CardInfo birthplace() {
        CardInfo info = new CardInfo(AppResources.getString(R.string.discovery_birthplace));
        info.mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscoveryHelper.openBirthplace(getActivity());
            }
        };
        return info;
    }
}
