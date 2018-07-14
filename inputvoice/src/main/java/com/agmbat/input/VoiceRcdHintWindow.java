package com.agmbat.input;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.agmbat.android.media.Recorder;
import com.agmbat.android.utils.DeviceUtils;
import com.agmbat.inputvoice.R;

public class VoiceRcdHintWindow extends PopupWindow {

    private final Context mContext;
    private final View mView;

    private View mVoiceLayout;
    private View mCancelLayout;
    private ImageView mVoiceRcdHintAnim;

    private Handler mHandler = new Handler();

    private Recorder mRecorder;
    private Runnable mUpdateRunnable = new Runnable() {

        @Override
        public void run() {
            if (mRecorder != null && mRecorder.state() == Recorder.RECORDING_STATE) {
                int signalEMA = (int) (mRecorder.getMaxAmplitude() / 2700.0);
                updateDisplay(signalEMA);
            }
            mHandler.postDelayed(this, 100);
        }
    };

    public VoiceRcdHintWindow(Context context, View v) {
        mContext = context;
        mView = v;
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        View content = View.inflate(mContext, R.layout.im_voice_rcd_hint_window, null);
        setContentView(content);
        content.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setWidth(content.getMeasuredWidth());
        setHeight(content.getMeasuredHeight());
        mVoiceRcdHintAnim = (ImageView) content.findViewById(R.id.voice_rcd_hint_anim);
        mVoiceLayout = content.findViewById(R.id.voice_rcd_hint_anim_area);
        mCancelLayout = content.findViewById(R.id.voice_rcd_hint_cancel_area);
    }

    public void show() {
        Point size = DeviceUtils.getScreenSize();
        int x = (size.x - getWidth()) / 2;
        int y = (size.y - getHeight()) / 2;
        showAtLocation(mView, Gravity.NO_GRAVITY, x, y);
        mHandler.post(mUpdateRunnable);
    }

    @Override
    public void dismiss() {
        mHandler.removeCallbacks(mUpdateRunnable);
        super.dismiss();
    }

    public void setRecorder(Recorder recorder) {
        mRecorder = recorder;
    }

    private void updateDisplay(int signalEMA) {
        int resid = R.drawable.im_voice_amp7;
        switch (signalEMA) {
            case 0:
            case 1:
                resid = R.drawable.im_voice_amp1;
                break;
            case 2:
            case 3:
                resid = R.drawable.im_voice_amp2;
                break;
            case 4:
            case 5:
                resid = R.drawable.im_voice_amp3;
                break;
            case 6:
            case 7:
                resid = R.drawable.im_voice_amp4;
                break;
            case 8:
            case 9:
                resid = R.drawable.im_voice_amp5;
                break;
            case 10:
            case 11:
                resid = R.drawable.im_voice_amp6;
                break;
            default:
                break;
        }
        mVoiceRcdHintAnim.setBackgroundResource(resid);
    }

    public void updateUiToCancel() {
        mVoiceLayout.setVisibility(View.GONE);
        mCancelLayout.setVisibility(View.VISIBLE);
    }

    public void updateUiToRecord() {
        mVoiceLayout.setVisibility(View.VISIBLE);
        mCancelLayout.setVisibility(View.GONE);
    }
}
