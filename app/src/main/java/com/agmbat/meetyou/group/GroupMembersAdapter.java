package com.agmbat.meetyou.group;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.imsdk.group.GroupMember;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.agmbat.meetyou.widget.BaseRecyclerAdapter;

public class GroupMembersAdapter extends BaseRecyclerAdapter<GroupMember, GroupMembersAdapter.GroupMemberHolder> {

    private Context mContext;

    public GroupMembersAdapter(Context context) {
        mContext = context;
    }

    @Override
    public GroupMemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_group_member_item, null, false);
        return new GroupMemberHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupMemberHolder holder, int position) {
        holder.bindData(getItem(position));
        super.onBindViewHolder(holder, position);
    }

    public class GroupMemberHolder extends RecyclerView.ViewHolder {

        private ImageView mAvatarView;
        private TextView mNameView;

        public GroupMemberHolder(View itemView) {
            super(itemView);
            mAvatarView = (ImageView) itemView.findViewById(R.id.avatar);
            mNameView = (TextView) itemView.findViewById(R.id.name);
        }

        public void bindData(GroupMember groupMember) {
            ImageManager.displayImage(groupMember.getAvatar(), mAvatarView, AvatarHelper.getGroupOptions());
            mNameView.setText(groupMember.getNickName());
        }
    }

    public void removeMember(String jid) {
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            if (getItem(i).getJid().equals(jid)) {
                remove(i);
                break;
            }
        }
    }

}
