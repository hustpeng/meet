package com.agmbat.meetyou.tab.found;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.discovery.nearbyuser.NearbyUsersActivity;
import com.agmbat.meetyou.tab.found.card.CardInfo;
import com.agmbat.meetyou.tab.found.card.HeaderCard;

import java.util.ArrayList;
import java.util.List;

public class FoundFragment extends Fragment {

    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_found, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.list);
        HeaderCard headerCard = new HeaderCard(getActivity());
        List<CardInfo> list = new ArrayList<>();
        list.add(new CardInfo("在线会员"));
        list.add(nearby());
        list.add(lover());
        list.add(new CardInfo("找玩伴"));

        list.add(new CardInfo("找同行"));
        list.add(new CardInfo("找老乡"));
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

        List<FoundGroup> dataList = new ArrayList<>();
        FoundGroup group = new FoundGroup();
        group.setTitle(AppResources.getString(R.string.nearby_users));
        group.setUserList(userList);
        dataList.add(group);

        group = new FoundGroup();
        group.setTitle(AppResources.getString(R.string.lover));
        group.setUserList(userList);
        dataList.add(group);

        group = new FoundGroup();
        group.setTitle("找玩伴");
        group.setUserList(userList);
        dataList.add(group);

        group = new FoundGroup();
        group.setTitle("找同行");
        group.setUserList(userList);
        dataList.add(group);

        group = new FoundGroup();
        group.setTitle("找老乡");
        group.setUserList(userList);
        dataList.add(group);

        group = new FoundGroup();
        group.setTitle("找群组");
        group.setUserList(userList);
        dataList.add(group);

        FoundAdapter adapter = new FoundAdapter(getActivity(), dataList);
        mListView.setAdapter(adapter);
    }


    private CardInfo nearby() {
        CardInfo info = new CardInfo(AppResources.getString(R.string.nearby_users));
        info.mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NearbyUsersActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        };
        return info;
    }

    private CardInfo lover() {
        CardInfo info = new CardInfo(AppResources.getString(R.string.lover));
        info.mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NearbyUsersActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        };
        return info;
    }
}
