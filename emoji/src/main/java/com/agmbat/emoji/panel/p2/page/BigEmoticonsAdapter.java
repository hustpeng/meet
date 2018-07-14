package com.agmbat.emoji.panel.p2.page;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.emoji.EmojiBean;
import com.agmbat.emoji.R;
import com.agmbat.emoji.panel.EmoticonClickListener;
import com.agmbat.emoji.panel.p2.EmoticonPageEntity;

public class BigEmoticonsAdapter extends EmoticonsAdapter {

    protected final double DEF_HEIGHTMAXTATIO = 1.6;

    public BigEmoticonsAdapter(Context context, EmoticonPageEntity emoticonPageEntity, EmoticonClickListener onEmoticonClickListener) {
        super(context, emoticonPageEntity, onEmoticonClickListener);
        mItemHeight = (int) context.getResources().getDimension(R.dimen.item_emoticon_size_big);
        mItemHeightMaxRatio = DEF_HEIGHTMAXTATIO;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_emoticon_big, null);
            viewHolder.rootView = convertView;
            viewHolder.ly_root = (LinearLayout) convertView.findViewById(R.id.ly_root);
            viewHolder.iv_emoticon = (ImageView) convertView.findViewById(R.id.iv_emoticon);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        bindView(position, viewHolder);
        updateUI(viewHolder, parent);
        return convertView;
    }

    protected void updateUI(ViewHolder viewHolder, ViewGroup parent) {
        if (mDefaultItemHeight != mItemHeight) {
            viewHolder.iv_emoticon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mItemHeight));
        }
        mItemHeightMax = this.mItemHeightMax != 0 ? this.mItemHeightMax : (int) (mItemHeight * mItemHeightMaxRatio);
        mItemHeightMin = this.mItemHeightMin != 0 ? this.mItemHeightMin : mItemHeight;
        int realItemHeight = ((View) parent.getParent()).getMeasuredHeight() / mEmoticonPageEntity.getLine();
        realItemHeight = Math.min(realItemHeight, mItemHeightMax);
        realItemHeight = Math.max(realItemHeight, mItemHeightMin);
        viewHolder.ly_root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, realItemHeight));
    }

    protected void bindView(int position, ViewHolder viewHolder) {
        final boolean isDelBtn = position == mDelbtnPosition;
        final EmojiBean emoticonEntity = mData.get(position);
        if (isDelBtn) {
            viewHolder.iv_emoticon.setImageResource(R.mipmap.icon_del);
            viewHolder.iv_emoticon.setBackgroundResource(R.drawable.bg_emoticon);
        } else {
            if (emoticonEntity != null) {
                ImageManager.displayImage(emoticonEntity.getIconUri(), viewHolder.iv_emoticon);
                viewHolder.iv_emoticon.setBackgroundResource(R.drawable.bg_emoticon);
            }
        }
        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEmoji(emoticonEntity, isDelBtn);
            }
        });
    }

    public static class ViewHolder {
        public View rootView;
        public LinearLayout ly_root;
        public ImageView iv_emoticon;
        public TextView tv_content;
    }
}