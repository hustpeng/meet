package com.agmbat.meetyou.tab.discovery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agmbat.meetyou.R;
import com.agmbat.meetyou.discovery.search.DiscoveryHelper;
import com.agmbat.meetyou.discovery.meeting.MeetingActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 发现tab
 */
public class DiscoveryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_discovery, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    /**
     * 点击附近的人
     */
    @OnClick(R.id.btn_discovery_nearby_users)
    void onClickNearbyUsers() {
        DiscoveryHelper.openNearByUsers(getActivity());
    }

    /**
     * 点击找恋人
     */
    @OnClick(R.id.btn_discovery_lover)
    void onClickLover() {
        DiscoveryHelper.openLover(getActivity());
    }

    @OnClick(R.id.btn_discovery_hobby)
    void onClickHobby() {
        DiscoveryHelper.openHobby(getActivity());
    }

    @OnClick(R.id.btn_discovery_birthplace)
    void onClickBirthplace() {
        DiscoveryHelper.openBirthplace(getActivity());
    }

    @OnClick(R.id.btn_discovery_search_user)
    void onClickSearchUser() {
        DiscoveryHelper.openFilter(getActivity());
    }

    /**
     * 点击聚会活动
     */
    @OnClick(R.id.btn_discovery_meeting)
    void onClickMeeting() {
        getActivity().startActivity(new Intent(getActivity(), MeetingActivity.class));
    }
}
