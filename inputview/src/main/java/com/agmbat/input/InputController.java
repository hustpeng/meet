package com.agmbat.input;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.agmbat.android.utils.KeyboardUtils;
import com.agmbat.android.utils.UiUtils;

/**
 * 处理输入View的逻辑
 */
public class InputController {

    /**
     * 输入View
     */
    private InputView mInputView;

    /**
     * 输入框上部的内容View, 配置其高度参数固定, 防止其跳动
     */
    private View mContentView;

    /**
     * 文本输入完成回调
     */
    private OnInputListener mOnInputListener;

    /**
     * 扩展面板控制器
     */
    private ExpandPanelController mExpandPanelController;

    /**
     * 软键盘工具
     */
    private SoftKeyBoard mSoftKeyBoard;

    public InputController(InputView view) {
        mInputView = view;
        mSoftKeyBoard = new SoftKeyBoard(view);
        mSoftKeyBoard.setOnSoftKeyBoardHeightChangeListener(new SoftKeyBoard.OnSoftKeyBoardHeightChangeListener() {
            @Override
            public void onSoftKeyBoardHeightChange(int height) {
                mExpandPanelController.setExpandPanelHeight(height);
            }
        });
        mExpandPanelController = new ExpandPanelController(mInputView.getExpandPanel());
        mExpandPanelController.setExpandPanelHeight(mSoftKeyBoard.getSoftKeyBoardHeight());

        mInputView.setOnClickSendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendText();
            }
        });
        mInputView.setOnClickEmojiListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEmojiPanel();
            }
        });

        mInputView.setOnTouchEditListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showKeyboard();
                }
                return false;
            }
        });
        mInputView.setOnClickAddListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleOtherPanel();
            }
        });
        mInputView.setOnClickVoiceOrTextListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVoicePanel();
            }
        });
    }

    /**
     * 设置ContentView
     *
     * @param view
     */
    public void setContentView(View view) {
        mContentView = view;
    }

    /**
     * 调转文本输入回调
     *
     * @param l
     */
    public void setOnInputListener(OnInputListener l) {
        mOnInputListener = l;
    }

    /**
     * 发送聊天信息
     */
    private void sendText() {
        String text = mInputView.getText().toString();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(mInputView.getContext(), "不能发送空消息", Toast.LENGTH_LONG).show();
        }
        mInputView.setText("");
        if (mOnInputListener != null) {
            mOnInputListener.onInput(OnInputListener.TYPE_TEXT, text);
        }
    }

    /**
     * 锁定内容View以防止跳闪
     */
    private void lockContentViewHeight() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        layoutParams.height = mContentView.getHeight();
        layoutParams.weight = 0;
    }

    /**
     * 释放锁定的内容View
     */
    private void unlockContentViewHeight() {
        // 延迟200ms, 让键盘显示出来后再设回原参数
        UiUtils.runOnUiThreadDelay(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
                layoutParams.height = 0;
                layoutParams.weight = 1;
            }
        }, 200);
    }


    /**
     * 添加表情面板
     *
     * @param panel
     */
    public void addEmojiPanel(View panel) {
        mExpandPanelController.addEmojiPanel(panel);
    }

    /**
     * 添加其他面板
     *
     * @param panel
     */
    public void addOtherPanel(View panel) {
        mExpandPanelController.addOtherPanel(panel);
    }

    /**
     * 重置到正常显示状态，用于选中其他面板后还原位置
     */
    public void reset() {
        mInputView.showText();
        mExpandPanelController.hideExpandPanel();
    }

    /**
     * 显示表情面板
     */
    private void showEmojiPanel() {
        // 如果软键在显示中, 需要固定高度后再切换
        if (mSoftKeyBoard.isSoftKeyBoardShown()) {
            mInputView.getEditText().clearFocus();
            KeyboardUtils.hideInputMethod(mInputView);
            lockContentViewHeight();
            mExpandPanelController.showEmojiPanel();
            unlockContentViewHeight();
        } else {
            mExpandPanelController.showEmojiPanel();
        }
    }

    /**
     * 切换emoji是否显示
     */
    private void toggleEmojiPanel() {
        mInputView.showText();
        if (mExpandPanelController.isEmojiPanelShow()) {
            showKeyboard();
            mInputView.setEmojiButtonShown(true);
        } else {
            showEmojiPanel();
            mInputView.setEmojiButtonShown(false);
        }
    }

    /**
     * 切换other panel是否显示
     */
    private void toggleOtherPanel() {
        mInputView.showText();
        if (mExpandPanelController.isOtherPanelShown()) {
            showKeyboard();
        } else {
            showOtherPanel();
        }
    }

    /**
     * 显示其他内容面板
     */
    private void showOtherPanel() {
        // 如果软键在显示中, 需要固定高度后再切换
        if (mSoftKeyBoard.isSoftKeyBoardShown()) {
            mInputView.getEditText().clearFocus();
            KeyboardUtils.hideInputMethod(mInputView);
            lockContentViewHeight();
            mExpandPanelController.showOtherPanel();
            unlockContentViewHeight();
        } else {
            mExpandPanelController.showOtherPanel();
        }
    }

    /**
     * 显示键盘
     */
    private void showKeyboard() {
        // 如果扩展面板在显示中, 需要固定高度后再切换为软键盘
        View v = mInputView.getEditText();
        if (mExpandPanelController.isExpandPanelShown()) {
            lockContentViewHeight();
            mExpandPanelController.hideExpandPanel();
            // 令编辑框获取焦点
            v.requestFocus();
            KeyboardUtils.showInputMethod(v);
            unlockContentViewHeight();
        } else {
            v.requestFocus();
            KeyboardUtils.showInputMethod(v);
        }
    }

    /**
     * 切换语音与文本输入
     */
    private void toggleVoicePanel() {
        if (mInputView.isVoiceShown()) {
            mInputView.showText();
            mInputView.setVoiceOrTextShown(true);
            KeyboardUtils.openSoftKeyboard(mInputView.getEditText());
        } else {
            mInputView.setVoiceOrTextShown(false);
            mInputView.showVoice();
        }
    }
}
