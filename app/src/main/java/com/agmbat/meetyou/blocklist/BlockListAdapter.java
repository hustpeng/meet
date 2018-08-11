package com.agmbat.meetyou.blocklist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.meetyou.R;
import com.agmbat.meetyou.util.ImageUtil;
import com.agmbat.meetyou.widget.BaseRecyclerAdapter;

import org.jivesoftware.smackx.block.BlockObject;

public class BlockListAdapter extends BaseRecyclerAdapter<BlockObject, BlockListAdapter.BlockViewHolder> {

    private Context mContext;

    public BlockListAdapter(Context context) {
        mContext = context;
    }


    @Override
    public BlockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_block_list_item, null, false);
        return new BlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BlockViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.bindData(getItem(position));
    }

    public class BlockViewHolder extends RecyclerView.ViewHolder {
        private ImageView mAvatarView;
        private TextView mNickNameView;

        public BlockViewHolder(View itemView) {
            super(itemView);
            mAvatarView = (ImageView) itemView.findViewById(R.id.avatar);
            mNickNameView = (TextView) itemView.findViewById(R.id.nickname);
        }

        public void bindData(BlockObject blockObject) {
            ImageUtil.loadCircleImage(mContext, mAvatarView, blockObject.getAvatar(), R.drawable.ic_default_avatar);
            if (TextUtils.isEmpty(blockObject.getNickname())) {
                mNickNameView.setText(blockObject.getJid());
            } else {
                mNickNameView.setText(blockObject.getNickname());
            }
        }

    }
}
