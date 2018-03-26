package com.agmbat.imsdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.agmbat.imsdk.R;
import com.agmbat.log.Log;

public class RecordButton extends Button {

    public interface OnRecordButtonListener {
        public void onStart();

        public void onEnd(MotionEvent event);
    }

    private OnRecordButtonListener mListener;
    private boolean mLongTouchFlag;
    private OnTouchListener mOnTouchListener;

    private OnLongClickListener mLongClickListener = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            mLongTouchFlag = true;
            if (mListener != null) {
                mListener.onStart();
            }
            setBackgroundResource(R.drawable.im_voice_rcd_btn_pressed);
            return false;
        }
    };

    private OnTouchListener mInternalTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d("RecordButton", "action:" + event.getAction());
            switch (event.getAction()) {
                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_UP:
                    if (mLongTouchFlag) {
                        if (mListener != null) {
                            mListener.onEnd(event);
                        }
                        mLongTouchFlag = false;
                    }
                    setBackgroundResource(R.drawable.im_voice_rcd_btn);
                    break;
                default:
                    break;
            }
            if (mOnTouchListener != null) {
                mOnTouchListener.onTouch(v, event);
            }
            return false;
        }
    };

    public void setOnRcordButtonListener(OnRecordButtonListener l) {
        mListener = l;
    }

    public RecordButton(Context context) {
        super(context);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOnLongClickListener(mLongClickListener);
        super.setOnTouchListener(mInternalTouchListener);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        mOnTouchListener = l;
    }

}
