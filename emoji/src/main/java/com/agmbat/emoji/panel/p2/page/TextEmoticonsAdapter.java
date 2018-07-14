package com.agmbat.emoji.panel.p2.page;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agmbat.emoji.EmojiBean;
import com.agmbat.emoji.R;
import com.agmbat.emoji.panel.EmoticonClickListener;
import com.agmbat.emoji.panel.p2.EmoticonPageEntity;

public class TextEmoticonsAdapter extends EmoticonsAdapter {

    public TextEmoticonsAdapter(Context context, EmoticonPageEntity emoticonPageEntity, EmoticonClickListener onEmoticonClickListener) {
        super(context, emoticonPageEntity, onEmoticonClickListener);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_emoticon_text, null);
            viewHolder.rootView = convertView;
            viewHolder.ly_root = (LinearLayout) convertView.findViewById(R.id.ly_root);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final boolean isDelBtn = isDelBtn(position);
        final EmojiBean emoticonEntity = mData.get(position);
        if (isDelBtn) {
            viewHolder.ly_root.setBackgroundResource(R.drawable.bg_emoticon);
        } else {
            viewHolder.tv_content.setVisibility(View.VISIBLE);
            if (emoticonEntity != null) {
                viewHolder.tv_content.setText(emoticonEntity.getContent());
                viewHolder.ly_root.setBackgroundResource(R.drawable.bg_emoticon);
            }
        }

        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEmoji(emoticonEntity, isDelBtn);
            }
        });

        updateUI(viewHolder, parent);
        return convertView;
    }

    private void updateUI(ViewHolder viewHolder, ViewGroup parent) {
        if (mDefaultItemHeight != mItemHeight) {
            viewHolder.tv_content.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mItemHeight));
        }
        mItemHeightMax = this.mItemHeightMax != 0 ? this.mItemHeightMax : (int) (mItemHeight * mItemHeightMaxRatio);
        mItemHeightMin = this.mItemHeightMin != 0 ? this.mItemHeightMin : mItemHeight;
        int realItemHeight = ((View) parent.getParent()).getMeasuredHeight() / mEmoticonPageEntity.getLine();
        realItemHeight = Math.min(realItemHeight, mItemHeightMax);
        realItemHeight = Math.max(realItemHeight, mItemHeightMin);
        viewHolder.ly_root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, realItemHeight));
    }

    public static class ViewHolder {
        public View rootView;
        public LinearLayout ly_root;
        public TextView tv_content;
    }
}