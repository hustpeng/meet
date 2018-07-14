package com.agmbat.input;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.view.MotionEvent;
import android.view.View;

import com.agmbat.android.media.Recorder;
import com.agmbat.android.utils.DeviceUtils;
import com.agmbat.file.FileUtils;
import com.agmbat.log.Log;

/**
 * 语音输入控制
 */
public class VoiceInputController {

    /**
     * 长按Button
     */
    private View mVoiceButton;


    /**
     * 录音器
     */
    private Recorder mRecorder;

    /**
     * 是否在长按中
     */
    private boolean mLongTouchFlag;


    /**
     * 显示录音状态的window
     */
    private VoiceRcdHintWindow mWindow;


    /**
     * 当前屏幕大小
     */
    private Point mScreenSize;

    private View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            mLongTouchFlag = true;
            onVoiceStart();
            setVoiceButtonPressed();
            return true;
        }
    };

    private View.OnTouchListener mInternalTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d("RecordButton", "action:" + event.getAction());
            switch (event.getAction()) {
                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_UP:
                    if (mLongTouchFlag) {
                        onVoiceEnd(event);
                        mLongTouchFlag = false;
                    }
                    // 还原背景的状态
                    setVoiceButtonNormal();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mWindow != null && mWindow.isShowing()) {
                        if (isMoveToCancel(event)) {
                            mWindow.updateUiToCancel();
                        } else {
                            mWindow.updateUiToRecord();
                        }
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    /**
     * 录音Button状态drawable
     */
    private Drawable mVoiceButtonBgDrawable;

    /**
     * 输入内容回调
     */
    private OnInputListener mOnInputListener;

    public VoiceInputController(View view) {
        mVoiceButton = view;
        mVoiceButton.setOnLongClickListener(mLongClickListener);
        mVoiceButton.setOnTouchListener(mInternalTouchListener);
        mVoiceButtonBgDrawable = mVoiceButton.getBackground();
        mRecorder = new Recorder();
//        mRecorder.setOnStateChangedListener(mOnStateChangedListener);
        mScreenSize = DeviceUtils.getScreenSize();
    }

    public void setOnInputListener(OnInputListener listener) {
        mOnInputListener = listener;
    }

    /**
     * 设置背景为按下的状态
     */
    private void setVoiceButtonPressed() {
        Drawable drawable = mVoiceButton.getBackground();
        Drawable pressed = drawable.getCurrent();
        mVoiceButton.setBackgroundDrawable(pressed);
    }

    /**
     * 还原voice Button背景
     */
    private void setVoiceButtonNormal() {
        mVoiceButton.setBackgroundDrawable(mVoiceButtonBgDrawable);
    }

    /**
     * 录音开始
     */
    private void onVoiceStart() {
        mRecorder.startRecording(MediaRecorder.OutputFormat.AMR_NB, ".amr");
        if (mWindow == null) {
            mWindow = new VoiceRcdHintWindow(mVoiceButton.getContext(), mVoiceButton);
        }
        mWindow.setRecorder(mRecorder);
        mWindow.show();
    }

    /**
     * 录音结束
     *
     * @param event
     */
    private void onVoiceEnd(MotionEvent event) {
        mWindow.dismiss();
        mRecorder.stopRecording();
        if (mRecorder.sampleLength() == 0) {
            return;
        }
        mRecorder.clearSampleLength();
        final String path = mRecorder.sampleFile().getAbsolutePath();
        if (isMoveToCancel(event)) {
            FileUtils.delete(path);
            return;
        }
        if (mOnInputListener != null) {
            mOnInputListener.onInput(OnInputListener.TYPE_VOICE, path);
        }
    }

    /**
     * 是否移动到取消上
     *
     * @param event
     * @return
     */
    private boolean isMoveToCancel(MotionEvent event) {
        float y = event.getRawY();
        return y < mScreenSize.y * 2 / 3.0;
    }
}
