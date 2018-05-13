package com.agmbat.imsdk.view;

import android.content.Context;
import android.graphics.Point;
import android.media.MediaRecorder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.agmbat.android.media.AmrHelper;
import com.agmbat.android.media.Recorder;
import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.android.utils.PhoneUtils;
import com.agmbat.android.utils.ViewUtils;
import com.agmbat.app.AppFileManager;
import com.agmbat.file.FileUtils;
import com.agmbat.http.HttpUtils;
import com.agmbat.imsdk.R;
import com.agmbat.imsdk.emoji.Emotion;
import com.agmbat.imsdk.emoji.EmotionDialog;
import com.agmbat.imsdk.emoji.EmotionDialog.OnSelectedEmojiListener;
import com.agmbat.imsdk.mgr.FileUploader;
import com.agmbat.imsdk.view.RecordButton.OnRecordButtonListener;
//import com.agmbat.meet.app.ConnectionHelper;
import com.agmbat.imsdk.data.body.AudioBody;
import com.agmbat.imsdk.data.body.Body;
import com.agmbat.imsdk.data.body.TextBody;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class InputView extends LinearLayout {

    private ImageButton mInputSwitcher;

    private View mTextPanel;
    private EditText mChatInput;
    private ImageButton mEmojiButton;
    private RecordButton mRecordButton;

    private ImageButton mAttachBtn;
    private Button mSendBtn;

    private boolean mInputTextMode;

    private OnSendMessageListener mOnSendMessageListener;

    private Recorder mRecorder;

    private VoiceRcdHintWindow mWindow;

    private Point mScreenSize;

    private final TextWatcher mChatInputTextWatcher = new TextWatcher() {

        private String mOriginText;
        private int mOriginSelection;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mOriginText = s.toString();
            mOriginSelection = mChatInput.getSelectionStart();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!mOriginText.equals(s.toString())) {
                CharSequence emotionText = Emotion.getEmojiText(s.toString());
                mChatInput.setText(emotionText);
                mChatInput.setSelection(mOriginSelection + (s.length() - mOriginText.length()));
            }
            updateChattingBtn();
        }
    };

    private OnClickListener mClickSendListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnSendMessageListener != null) {
                String message = mChatInput.getText().toString();
                Body body = new TextBody(message);
                mOnSendMessageListener.onSendMessage(body);
                mChatInput.setText("");
            }
        }
    };

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_input_emoji) {
                EmotionDialog dialog = new EmotionDialog(getContext());
                dialog.setOnSelectedEmojiListener(new OnSelectedEmojiListener() {

                    @Override
                    public void onSelectedEmoji(String emotion) {
                        int selection = mChatInput.getSelectionStart();
                        StringBuilder originalText = new StringBuilder(mChatInput.getText().toString());
                        originalText.insert(selection, emotion);
                        mChatInput.setText(originalText);
                        mChatInput.setSelection(selection + emotion.length());
                    }
                });
                dialog.show();
            } else if (id == R.id.btn_input_switcher) {
                mInputTextMode = !mInputTextMode;
                updateChatMode();
            }
        }
    };

    private OnRecordButtonListener mOnRecordButtonListener = new OnRecordButtonListener() {

        @Override
        public void onStart() {
            mRecorder.startRecording(MediaRecorder.OutputFormat.AMR_NB, ".amr");
            if (mWindow == null) {
                mWindow = new VoiceRcdHintWindow(getContext(), mRecordButton);
            }
            mWindow.setRecorder(mRecorder);
            mWindow.show();
        }

        @Override
        public void onEnd(MotionEvent event) {
            mWindow.dismiss();
            mRecorder.stopRecording();
            if (mRecorder.sampleLength() == 0) {
                return;
            }
            mRecorder.clearSampleLength();
            final String path = mRecorder.sampleFile().getAbsolutePath();
            if (isMoveToCancel(event)) {
                FileUtils.deleteFileIfExist(path);
                return;
            }
            AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... params) {
                    return FileUploader.uploadFile(path);
                }

                @Override
                protected void onPostExecute(String fileUrl) {
                    if (!TextUtils.isEmpty(fileUrl)) {
                        File oldFile = new File(path);
                        File newFile = new File(AppFileManager.getRecordDir(), HttpUtils.getFileNameFromUrl(fileUrl));
                        oldFile.renameTo(newFile);
                        long duration = AmrHelper.getAmrDuration(newFile);
                        Body body = new AudioBody(fileUrl, duration);
                        if (mOnSendMessageListener != null) {
                            mOnSendMessageListener.onSendMessage(body);
                        }
                    }
                }

            });
        }
    };

    public InputView(Context context) {
        super(context);
        init(context);
    }

    public InputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_input, this);
        ButterKnife.bind(this);

        mInputTextMode = true;
        mRecorder = new Recorder();
        mRecorder.setOnStateChangedListener(mOnStateChangedListener);
        mTextPanel = findViewById(R.id.text_panel_ll);
        mChatInput = (EditText) findViewById(R.id.chatting_content_et);
        mChatInput.addTextChangedListener(mChatInputTextWatcher);
        mEmojiButton = (ImageButton) findViewById(R.id.btn_input_emoji);
        mEmojiButton.setOnClickListener(mOnClickListener);
        mInputSwitcher = (ImageButton) findViewById(R.id.btn_input_switcher);
        mInputSwitcher.setOnClickListener(mOnClickListener);
        mRecordButton = (RecordButton) findViewById(R.id.btn_input_audio);
        mRecordButton.setOnRcordButtonListener(mOnRecordButtonListener);
        mRecordButton.setOnTouchListener(mRecordTouchListener);
        mAttachBtn = (ImageButton) findViewById(R.id.chatting_attach_btn);
        mSendBtn = (Button) findViewById(R.id.chatting_send_btn);
        mSendBtn.setOnClickListener(mClickSendListener);
        mScreenSize = PhoneUtils.getScreenSize();
        updateChatMode();
    }


    public void setOnSendMessageListener(OnSendMessageListener l) {
        mOnSendMessageListener = l;
    }

    public void setOnAttachClickListener(OnClickListener l) {
        mAttachBtn.setOnClickListener(l);
    }

    private void updateChatMode() {
        if (mInputTextMode) {
            mTextPanel.setVisibility(View.VISIBLE);
            mChatInput.requestFocus();
            mRecordButton.setVisibility(View.GONE);
            mInputSwitcher.setImageResource(R.drawable.chat_btn_input_audio);
            updateChattingBtn();
        } else {
            ViewUtils.hideInputMethod(mChatInput);
            mTextPanel.setVisibility(View.GONE);
            mRecordButton.setVisibility(View.VISIBLE);
            mInputSwitcher.setImageResource(R.drawable.chat_btn_input_keyboard);
            updateChattingBtn(true);
        }
    }

    private void updateChattingBtn() {
        String text = mChatInput.getText().toString();
        updateChattingBtn(text.length() == 0);
    }

    private void updateChattingBtn(boolean showChatingBtn) {
        if (showChatingBtn) {
            mAttachBtn.setVisibility(View.VISIBLE);
            mSendBtn.setVisibility(View.GONE);
        } else {
            mAttachBtn.setVisibility(View.GONE);
            mSendBtn.setVisibility(View.VISIBLE);
        }
    }

    private Recorder.OnStateChangedListener mOnStateChangedListener = new Recorder.OnStateChangedListener() {

        @Override
        public void onStateChanged(int state) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onError(int error) {
            // TODO Auto-generated method stub
        }
    };

    private OnTouchListener mRecordTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (mWindow != null && mWindow.isShowing()) {
                    if (isMoveToCancel(event)) {
                        mWindow.updateUiToCancel();
                    } else {
                        mWindow.updateUiToRecord();
                    }
                }
            }
            return false;
        }
    };

    private boolean isMoveToCancel(MotionEvent event) {
        float y = event.getRawY();
        return y < mScreenSize.y * 2 / 3.0;
    }
}
