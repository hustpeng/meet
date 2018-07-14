package com.agmbat.emoji.panel.p2.page;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.agmbat.android.image.ImageManager;
import com.agmbat.emoji.DelBtnStatus;
import com.agmbat.emoji.EmojiBean;
import com.agmbat.emoji.R;
import com.agmbat.emoji.panel.EmoticonClickListener;
import com.agmbat.emoji.panel.p2.EmoticonPageEntity;

import java.util.ArrayList;

/**
 * 用于表示一页表情的GridAdapter
 */
public class EmoticonsAdapter extends BaseAdapter {

    private static final int DEF_HEIGHTMAXTATIO = 2;

    /**
     * 默认item高度
     */
    protected final int mDefaultItemHeight;

    protected Context mContext;
    protected ArrayList<EmojiBean> mData = new ArrayList<>();
    protected EmoticonPageEntity mEmoticonPageEntity;

    protected int mItemHeightMax;
    protected int mItemHeightMin;

    /**
     * item高度
     */
    protected int mItemHeight;

    /**
     * 高度比
     */
    protected double mItemHeightMaxRatio;

    protected int mDelbtnPosition;
    private EmoticonClickListener mOnEmoticonClickListener;

    public EmoticonsAdapter(Context context, EmoticonPageEntity emoticonPageEntity, EmoticonClickListener onEmoticonClickListener) {
        mContext = context;
        mEmoticonPageEntity = emoticonPageEntity;
        mOnEmoticonClickListener = onEmoticonClickListener;
        mItemHeightMaxRatio = DEF_HEIGHTMAXTATIO;
        mDelbtnPosition = -1;
        mItemHeight = (int) context.getResources().getDimension(R.dimen.item_emoticon_size_default);
        mDefaultItemHeight = mItemHeight;
        mData.addAll(emoticonPageEntity.getEmoticonList());
        checkDelBtn(emoticonPageEntity);
    }

    private void checkDelBtn(EmoticonPageEntity entity) {
        DelBtnStatus delBtnStatus = entity.getDelBtnStatus();
        if (DelBtnStatus.GONE.equals(delBtnStatus)) {
            return;
        }
        if (DelBtnStatus.FOLLOW.equals(delBtnStatus)) {
            mDelbtnPosition = getCount();
            mData.add(null);
        } else if (DelBtnStatus.LAST.equals(delBtnStatus)) {
            int max = entity.getLine() * entity.getRow();
            while (getCount() < max) {
                mData.add(null);
            }
            mDelbtnPosition = getCount() - 1;
        }
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_emoticon, null);
            viewHolder.rootView = convertView;
            viewHolder.ly_root = (LinearLayout) convertView.findViewById(R.id.ly_root);
            viewHolder.iv_emoticon = (ImageView) convertView.findViewById(R.id.iv_emoticon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        bindView(position, parent, viewHolder);
        updateUI(viewHolder, parent);
        return convertView;
    }

    /**
     * 是否为删除Button
     *
     * @param position
     * @return
     */
    protected boolean isDelBtn(int position) {
        return position == mDelbtnPosition;
    }

    private void bindView(int position, ViewGroup parent, ViewHolder viewHolder) {
        final EmojiBean emojiBean = mData.get(position);
        final boolean isDelBtn = isDelBtn(position);
        if (emojiBean == null && !isDelBtn) {
            return;
        }
        viewHolder.ly_root.setBackgroundResource(R.drawable.bg_emoticon);
        if (isDelBtn) {
            viewHolder.iv_emoticon.setImageResource(R.mipmap.icon_del);
        } else {
            ImageManager.displayImage(emojiBean.getIconUri(), viewHolder.iv_emoticon);
        }
        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEmoji(emojiBean, isDelBtn);
            }
        });
    }

    /**
     * 点击emoji
     */
    protected void clickEmoji(EmojiBean emojiBean, boolean isDelBtn) {
        if (mOnEmoticonClickListener != null) {
            mOnEmoticonClickListener.onEmoticonClick(emojiBean, isDelBtn);
        }
    }

    private void updateUI(ViewHolder viewHolder, ViewGroup parent) {
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

    public static class ViewHolder {
        public View rootView;
        public LinearLayout ly_root;
        public ImageView iv_emoticon;
    }
}