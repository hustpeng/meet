package com.agmbat.input;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.agmbat.android.utils.KeyboardUtils;
import com.agmbat.inputview.R;

/**
 * 用户显示输入的View, 可包含文字, 表情, 语音, 文件等, 解决输入面板切换的问题
 */
public class InputView extends LinearLayout {

    private static final String TAG = "InputView";

    /**
     * 文本输入框, 控制键显示需要在对应的Activity加上 android:windowSoftInputMode="stateUnchanged|adjustResize" />
     */
    private EditText mEditText;

    /**
     * 输入完成确认Button
     */
    private Button mSendButton;

    /**
     * 添加媒体信息
     */
    private ImageView mAddButton;

    /**
     * 表情Button
     */
    private ImageView mEmojiButton;

    /**
     * 语音和文本切换Button
     */
    private ImageView mVoiceOrTextButton;

    /**
     * 文本输入框和表情Button的Layout
     */
    private RelativeLayout mTextInputLayout;

    /**
     * 语音输入Button, 用于长按输入语音
     */
    private Button mVoiceButton;

    /**
     * 底部扩展面板, 用户显示emoji, 拍照等
     */
    private FrameLayout mExpandPanel;

    public InputView(Context context) {
        super(context);
        init();
    }

    public InputView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown" + event.getAction());
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEventPreIme" + event.getAction());
        return super.dispatchKeyEventPreIme(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEvent" + event.getAction());
        // 拦截返回事件, 如果扩展面板显示, 则需要将其收起
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mExpandPanel.isShown()) {
                mExpandPanel.setVisibility(View.GONE);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void init() {
        View.inflate(getContext(), R.layout.inputview_view_input, this);
        mVoiceOrTextButton = (ImageView) findViewById(R.id.btn_voice_or_text);
        mTextInputLayout = ((RelativeLayout) findViewById(R.id.rl_input));
        mVoiceButton = ((Button) findViewById(R.id.btn_voice));

        mEditText = (EditText) findViewById(R.id.input_text);
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);

        mSendButton = (Button) findViewById(R.id.btn_send);
        mEmojiButton = (ImageView) findViewById(R.id.btn_face);
        mAddButton = (ImageView) findViewById(R.id.btn_multimedia);
        mExpandPanel = (FrameLayout) findViewById(R.id.expand_panel);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 当输入文本后, 显示发送Button
                if (!TextUtils.isEmpty(s)) {
                    mSendButton.setVisibility(VISIBLE);
                    mSendButton.setBackgroundResource(R.drawable.btn_send_bg);
                    mAddButton.setVisibility(GONE);
                } else {
                    mAddButton.setVisibility(VISIBLE);
                    mSendButton.setVisibility(GONE);
                }
            }
        });

    }


    /**
     * 显示语音Layout
     */
    public void showVoice() {
        mTextInputLayout.setVisibility(GONE);
        mVoiceButton.setVisibility(VISIBLE);
        mSendButton.setVisibility(GONE);
        mAddButton.setVisibility(VISIBLE);
        KeyboardUtils.closeSoftKeyboard(this);
        mExpandPanel.setVisibility(View.GONE);
    }

    /**
     * 显示文本输入
     */
    public void showText() {
        mTextInputLayout.setVisibility(VISIBLE);
        mVoiceButton.setVisibility(GONE);
    }

    /**
     * 显示表情是否显示
     *
     * @param isShown
     */
    public void setEmojiButtonShown(boolean isShown) {
        if (isShown) {
            mEmojiButton.setImageResource(R.mipmap.chatting_emoticons);
        } else {
            mEmojiButton.setImageResource(R.mipmap.chatting_softkeyboard);
        }
    }

    public void setOnClickSendListener(OnClickListener l) {
        mSendButton.setOnClickListener(l);
    }

    public void setOnClickEmojiListener(OnClickListener l) {
        mEmojiButton.setOnClickListener(l);
    }

    /**
     * 设置点击+的事件
     *
     * @param l
     */
    public void setOnClickAddListener(OnClickListener l) {
        mAddButton.setOnClickListener(l);
    }

    /**
     * 设置语音切换点击事件
     *
     * @param l
     */
    public void setOnClickVoiceOrTextListener(OnClickListener l) {
        mVoiceOrTextButton.setOnClickListener(l);
    }

    /**
     * 设置EditText的Touch事件, 处理收起面板, 弹出键盘的操作
     *
     * @param l
     */
    public void setOnTouchEditListener(OnTouchListener l) {
        mEditText.setOnTouchListener(l);
    }

    /**
     * 获取文本内容
     *
     * @return
     */
    public String getText() {
        return mEditText.getText().toString();
    }

    /**
     * 设置文本内容
     *
     * @param text
     */
    public void setText(String text) {
        mEditText.setText(text);
    }

    /**
     * 获取扩展面板
     *
     * @return
     */
    public FrameLayout getExpandPanel() {
        return mExpandPanel;
    }

    /**
     * 获取EditTextView
     *
     * @return
     */
    public EditText getEditText() {
        return mEditText;
    }

    public View getVoiceButton() {
        return mVoiceButton;
    }


    public void setSelection(int start, int stop) {
        mEditText.setSelection(start, stop);
    }

    public void setSelection(int index) {
        mEditText.setSelection(index);
    }

    /**
     * 判断语音面板是否显示
     *
     * @return
     */
    public boolean isVoiceShown() {
        return !mTextInputLayout.isShown();
    }

    public void setVoiceOrTextShown(boolean isShown) {
        if (isShown) {
            mVoiceOrTextButton.setImageResource(R.drawable.btn_voice_or_text);
        } else {
            mVoiceOrTextButton.setImageResource(R.drawable.btn_voice_or_text_keyboard);
        }
    }
}
