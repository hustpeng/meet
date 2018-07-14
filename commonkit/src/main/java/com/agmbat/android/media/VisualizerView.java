package com.agmbat.android.media;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.View;

/**
 * A simple class that draws waveform data received from a
 * {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture }
 */
public class VisualizerView extends View implements Runnable {
    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();

    private Paint mForePaint = new Paint();

    private Recorder mRecorder;

    private boolean mStarted;

    private int mCurrentMaxAmplitude = 1;

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setRecorder(Recorder recorder) {
        mRecorder = recorder;
        invalidate();
    }

    private void init() {
        mBytes = null;
        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(0, 128, 255));
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }

    private byte[] getBytes() {
        byte[] bytes = new byte[1024];
        for (int i = 0; i < 1024; i++) {
            int maxAmplitude = mRecorder.getMaxAmplitude();
            if (maxAmplitude > mCurrentMaxAmplitude) {
                mCurrentMaxAmplitude = maxAmplitude;
            } else {
                mCurrentMaxAmplitude = Math.max(maxAmplitude, mCurrentMaxAmplitude - 32767 / 1024);
            }
            bytes[i] = (byte) (mCurrentMaxAmplitude * 127 * 2 / 32768 - 127);
        }
        return bytes;
    }

    @Override
    public void run() {
        if (mRecorder != null && mRecorder.state() == Recorder.RECORDING_STATE) {
            mBytes = getBytes();
            invalidate();
            postDelayed(this, 100);
            mStarted = true;
        } else {
            mStarted = false;
        }
    }

    public boolean isStarted() {
        return mStarted;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBytes == null) {
            return;
        }
        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }
        mRect.set(0, 0, getWidth(), getHeight());
        for (int i = 0; i < mBytes.length - 1; i++) {
            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
            mPoints[i * 4 + 1] = mRect.height() / 2
                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
            mPoints[i * 4 + 3] = mRect.height() / 2
                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
        }
        canvas.drawLines(mPoints, mForePaint);
    }
}
