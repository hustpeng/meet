package com.agmbat.meetyou.tab.discovery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.chat.body.Body;
import com.agmbat.imsdk.chat.body.BodyParser;
import com.agmbat.imsdk.chat.body.EventsBody;
import com.agmbat.imsdk.imevent.ReceiveSysMessageEvent;
import com.agmbat.imsdk.user.LoginUser;
import com.agmbat.imsdk.util.AppConfigUtils;
import com.agmbat.isdialog.ISAlertDialog;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.discovery.meeting.MeetingActivity;
import com.agmbat.meetyou.discovery.search.DiscoveryHelper;
import com.agmbat.meetyou.group.GroupSearchActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.message.MessageObject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 发现tab
 */
public class DiscoveryFragment extends Fragment {

    private TextView mUnreadDotTv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_discovery, null);
        mUnreadDotTv = view.findViewById(R.id.unread_dot);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppConfigUtils.hasEventNews(getContext())) {
            mUnreadDotTv.setVisibility(View.VISIBLE);
        } else {
            mUnreadDotTv.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ReceiveSysMessageEvent event) {
        MessageObject msg = event.getMessageObject();
        final Body body = BodyParser.parse(msg.getBody());
        if (body instanceof EventsBody) {
            AppConfigUtils.setHasEventNews(getContext(), true);
            mUnreadDotTv.setVisibility(View.VISIBLE);
        }
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

    /**
     * 点击找玩伴
     */
    @OnClick(R.id.btn_discovery_hobby)
    void onClickHobby() {
        LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        String hobby = user.getHobby();
        if (TextUtils.isEmpty(hobby)) {
            ISAlertDialog dialog = new ISAlertDialog(getActivity());
            dialog.setMessage("请先在个人信息中填写兴趣爱好!");
            dialog.setPositiveButton("确定", null);
            dialog.show();
            return;
        }
        DiscoveryHelper.openHobby(getActivity());
    }

    @OnClick(R.id.btn_discovery_birthplace)
    void onClickBirthplace() {
        LoginUser user = XMPPManager.getInstance().getRosterManager().getLoginUser();
        String birthplace = user.getBirthplace();
        if (TextUtils.isEmpty(birthplace)) {
            ISAlertDialog dialog = new ISAlertDialog(getActivity());
            dialog.setMessage("请先在个人信息中填写籍贯!");
            dialog.setPositiveButton("确定", null);
            dialog.show();
            return;
        }
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

    /**
     * 点击找群组
     */
    @OnClick(R.id.btn_search_group)
    void onClickSearchGroup() {
        getActivity().startActivity(new Intent(getActivity(), GroupSearchActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
