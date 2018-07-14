package com.agmbat.emoji.panel.p2.page;

import android.content.Context;
import android.view.View;

import com.agmbat.android.image.ImageManager;
import com.agmbat.emoji.EmojiBean;
import com.agmbat.emoji.R;
import com.agmbat.emoji.panel.EmoticonClickListener;
import com.agmbat.emoji.panel.p2.EmoticonPageEntity;


public class BigEmoticonsAndTitleAdapter extends BigEmoticonsAdapter {

    protected final double DEF_HEIGHTMAXTATIO = 1.6;

    public BigEmoticonsAndTitleAdapter(Context context, EmoticonPageEntity emoticonPageEntity, EmoticonClickListener onEmoticonClickListener) {
        super(context, emoticonPageEntity, onEmoticonClickListener);
        mItemHeight = (int) context.getResources().getDimension(R.dimen.item_emoticon_size_big);
        mItemHeightMaxRatio = DEF_HEIGHTMAXTATIO;
    }

    protected void bindView(int position, ViewHolder viewHolder) {
        final boolean isDelBtn = isDelBtn(position);
        final EmojiBean emoticonEntity = mData.get(position);
        if (isDelBtn) {
            viewHolder.iv_emoticon.setImageResource(R.mipmap.icon_del);
            viewHolder.iv_emoticon.setBackgroundResource(R.drawable.bg_emoticon);
        } else {
            if (emoticonEntity != null) {
                ImageManager.displayImage(emoticonEntity.getIconUri(), viewHolder.iv_emoticon);
                viewHolder.tv_content.setVisibility(View.VISIBLE);
                viewHolder.tv_content.setText(emoticonEntity.getContent());
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


}