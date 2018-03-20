package com.agmbat.meetyou.tab.found;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.agmbat.meetyou.R;
import com.agmbat.meetyou.data.ContactInfo;

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
        List<HeaderCard.HeaderItemInfo> list = new ArrayList<>();
        list.add(new HeaderCard.HeaderItemInfo("在线会员"));
        list.add(new HeaderCard.HeaderItemInfo("附近的人"));
        list.add(new HeaderCard.HeaderItemInfo("找恋人"));
        list.add(new HeaderCard.HeaderItemInfo("找玩伴"));

        list.add(new HeaderCard.HeaderItemInfo("找同行"));
        list.add(new HeaderCard.HeaderItemInfo("找老乡"));
        list.add(new HeaderCard.HeaderItemInfo("找群组"));
        list.add(new HeaderCard.HeaderItemInfo("创建群组"));
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
        group.setTitle("附近的人");
        group.setUserList(userList);
        dataList.add(group);

        group = new FoundGroup();
        group.setTitle("找恋人");
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
}
