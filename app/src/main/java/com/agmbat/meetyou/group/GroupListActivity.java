package com.agmbat.meetyou.group;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.group.GroupBean;
import com.agmbat.imsdk.group.QueryGroupResultIQ;
import com.agmbat.log.Log;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.tab.contacts.ContactsFragment;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.circle.CircleListPacket;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 群列表
 */
public class GroupListActivity extends Activity implements ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener {

    private static final String TAG = ContactsFragment.class.getSimpleName();

    private static final int STATE_LOADING = 0;
    private static final int STATE_LOAD_FINISH = 1;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.friend_list)
    ExpandableListView mListView;

    private GroupAdapter mFriendsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);

        mListView.setOnChildClickListener(this);
        mListView.setOnGroupClickListener(this);
        mListView.setOnCreateContextMenuListener(this);
        mFriendsAdapter = new GroupAdapter(this, new ArrayList<CircleGroup>());
        mListView.setAdapter(mFriendsAdapter);

        // 加载数据
        //new InitContactListTask().execute();
        XMPPManager.getInstance().getXmppConnection().addPacketListener(mQueryGroupListener, new PacketTypeFilter(QueryGroupResultIQ.class));
        queryGroupList();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    private void queryGroupList() {
        setState(STATE_LOADING);
        CircleListPacket circleListPacket = new CircleListPacket(XMPPManager.GROUP_CHAT_SERVER);
        XMPPManager.getInstance().getXmppConnection().sendPacket(circleListPacket);
    }


    private PacketListener mQueryGroupListener = new PacketListener() {

        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof QueryGroupResultIQ) {
                QueryGroupResultIQ queryGroupResultIQ = (QueryGroupResultIQ) packet;
                Log.d("Group size: " + queryGroupResultIQ.getGroups().size());

                FillDataTask fillDataTask = new FillDataTask();
                fillDataTask.execute(queryGroupResultIQ.getGroups());
            }
        }
    };


    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        int groupCount = mFriendsAdapter.getGroupCount();
        for (int position = 0; position < groupCount; position++) {
            if (position == groupPosition) {
                if (mListView.isGroupExpanded(groupPosition)) {
                    mListView.collapseGroup(groupPosition);
                } else {
                    mListView.expandGroup(groupPosition);
                }
            } else {
                mListView.collapseGroup(position);
            }
        }
        return true;
    }


    private void fillData(List<CircleGroup> groups) {
        mFriendsAdapter.clear();
        mFriendsAdapter.addAll(groups);
        mFriendsAdapter.notifyDataSetChanged();
        if (groups.size() > 0) {
            mListView.expandGroup(0);
        }
    }

    private class FillDataTask extends AsyncTask<List<GroupBean>, Void, List<CircleGroup>>{

        @Override
        protected List<CircleGroup> doInBackground(List<GroupBean>... lists) {
            return initGroupList(lists[0]);
        }

        @Override
        protected void onPostExecute(List<CircleGroup> circleGroups) {
            fillData(circleGroups);
        }

    }

    private void setState(int state) {
        if (state == STATE_LOADING) {
            mProgressBar.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else if (state == STATE_LOAD_FINISH) {
            mProgressBar.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    private List<CircleGroup> initGroupList(List<GroupBean> groupBeans) {
        List<CircleGroup> groups = new ArrayList<CircleGroup>();

        CircleGroup myCircleGroup = new CircleGroup();
        List<CircleInfo> myCircles = new ArrayList<>();
        myCircleGroup.setGroupName("我创建的群");
        myCircleGroup.setContactList(myCircles);

        CircleGroup otherCircleGroup = new CircleGroup();
        List<CircleInfo> joinCircles = new ArrayList<>();
        otherCircleGroup.setGroupName("我加入的群");
        otherCircleGroup.setContactList(joinCircles);

        String myJid = XMPPManager.getInstance().getXmppConnection().getBareJid();

        for (int i = 0; i < groupBeans.size(); i++) {
            GroupBean groupBean = groupBeans.get(i);
            CircleInfo circleInfo = new CircleInfo();
            circleInfo.setName(groupBean.getName());
            circleInfo.setAvatar(groupBean.getAvatar());
            circleInfo.setGroupJid(groupBean.getGroupJid());
            circleInfo.setMembers(groupBean.getMembers());
            circleInfo.setOwnerJid(groupBean.getOwnerJid());

            if (myJid.equals(groupBean.getOwnerJid())) {
                myCircles.add(circleInfo);
            } else {
                joinCircles.add(circleInfo);
            }
        }
        groups.add(myCircleGroup);
        groups.add(otherCircleGroup);
        return groups;
    }


}
