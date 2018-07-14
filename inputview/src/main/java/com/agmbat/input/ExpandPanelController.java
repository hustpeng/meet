package com.agmbat.input;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 扩展面板控制器, 处理面板显示emoji或其他信息的逻辑
 */
public class ExpandPanelController {

    /**
     * 扩展面板
     */
    private FrameLayout mExpandPanel;

    /**
     * 表情面板
     */
    private View mEmojiPanel;

    /**
     * 其他内容面板
     */
    private View mOtherPanel;

    public ExpandPanelController(FrameLayout panel) {
        mExpandPanel = panel;
        hideExpandPanel();
    }

    /**
     * 设置扩展面板的高度
     */
    public void setExpandPanelHeight(int height) {
        ViewGroup.LayoutParams params = mExpandPanel.getLayoutParams();
        params.height = height;
        mExpandPanel.setLayoutParams(params);
    }

    /**
     * 显示emoji
     */
    public void showEmojiPanel() {
        hideOtherPaner();
        showExpandPanel();
        if (mEmojiPanel != null) {
            mEmojiPanel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏Emoji面板
     */
    private void hideEmojiPanel() {
        if (mEmojiPanel != null) {
            mEmojiPanel.setVisibility(View.GONE);
        }
    }

    /**
     * 当ExpandPanel的高度大于0时, 则表示在显示中
     *
     * @return
     */
    public boolean isExpandPanelShown() {
        return mExpandPanel.isShown();
    }

    /**
     * 隐藏扩展面板
     */
    public void hideExpandPanel() {
        mExpandPanel.setVisibility(View.GONE);
    }

    /**
     * 显示扩展面板
     */
    private void showExpandPanel() {
        mExpandPanel.setVisibility(View.VISIBLE);
    }

    /**
     * 添加emoji面板
     *
     * @param panel
     */
    public void addEmojiPanel(View panel) {
        mEmojiPanel = panel;
        mExpandPanel.addView(mEmojiPanel);
    }

    /**
     * 表情面板是否显示
     *
     * @return
     */
    public boolean isEmojiPanelShow() {
        return mEmojiPanel != null && mEmojiPanel.isShown();
    }

    /**
     * 添加其他面板, 显示点击+的内容
     *
     * @param panel
     */
    public void addOtherPanel(View panel) {
        mOtherPanel = panel;
        mExpandPanel.addView(panel);
    }

    /**
     * 其他面板是否已在显示中
     *
     * @return
     */
    public boolean isOtherPanelShown() {
        return mOtherPanel != null && mOtherPanel.isShown();
    }

    /**
     * 显示+号面内容
     */
    public void showOtherPanel() {
        hideEmojiPanel();
        showExpandPanel();
        if (mOtherPanel != null) {
            mOtherPanel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏+内容面板
     */
    private void hideOtherPaner() {
        if (mOtherPanel != null) {
            mOtherPanel.setVisibility(View.GONE);
        }
    }


}
