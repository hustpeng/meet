package com.agmbat.android.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;

import com.agmbat.android.SystemManager;
import com.agmbat.app.AppFileManager;

import java.io.File;
import java.io.IOException;

public class Recorder {

    /**
     * State indicating this item is IDLE.
     */
    public static final int IDLE_STATE = 0;

    /**
     * State indicating this item is recording.
     */
    public static final int RECORDING_STATE = 1;

    /**
     * Error code is no error
     */
    public static final int NO_ERROR = 0;

    /**
     * Error code is sdcard acess error
     */
    public static final int SDCARD_ACCESS_ERROR = 1;

    /**
     * Error code is internal error
     */
    public static final int INTERNAL_ERROR = 2;

    /**
     * Error code is in call precord error
     */
    public static final int IN_CALL_RECORD_ERROR = 3;

    /**
     * the ample prefix
     */
    private static final String SAMPLE_PREFIX = "recording";

    /**
     * the current state
     */
    private int mState = IDLE_STATE;

    /**
     * the listener of the state changed
     */
    private OnStateChangedListener mOnStateChangedListener = null;

    /**
     * time at which latest record or play operation started
     */
    private long mSampleStart = 0;

    /**
     * length of current sample
     */
    private long mSampleLength = 0;
    /**
     * file of current sample
     */
    private File mSampleFile = null;

    /**
     * the MediaRecorder
     */
    private MediaRecorder mRecorder = null;

    /**
     * Creates an Recorder.
     */
    public Recorder() {
    }

    /**
     * Get the max amplitude
     *
     * @return the max amplitude
     */
    public int getMaxAmplitude() {
        if (mState != RECORDING_STATE) {
            return 0;
        }
        return mRecorder.getMaxAmplitude();
    }

    /**
     * Set the state changed listener
     *
     * @param listener
     */
    public void setOnStateChangedListener(OnStateChangedListener listener) {
        mOnStateChangedListener = listener;
    }

    /**
     * Get the state
     *
     * @return the state
     */
    public int state() {
        return mState;
    }

    /**
     * Get the progress
     *
     * @return the progress
     */
    public int progress() {
        if (mState == RECORDING_STATE) {
            return (int) ((System.currentTimeMillis() - mSampleStart) / 1000);
        }
        return 0;
    }

    /**
     * Get the sample length
     *
     * @return the sample length
     */
    public long sampleLength() {
        return mSampleLength;
    }

    /**
     * Get the sample file
     *
     * @return the sample file
     */
    public File sampleFile() {
        return mSampleFile;
    }

    /**
     * Resets the recorder state. If a sample was recorded, the file is deleted.
     */
    public void delete() {
        stopRecording();
        if (mSampleFile != null) {
            mSampleFile.delete();
        }
        mSampleFile = null;
        mSampleLength = 0;
        signalStateChanged(IDLE_STATE);
    }

    /**
     * Clear sample length
     */
    public void clearSampleLength() {
        mSampleLength = 0;
    }

    /**
     * Resets the recorder state. If a sample was recorded, the file is left on disk and will be reused for a new
     * recording.
     */
    public void clear() {
        stopRecording();
        mSampleLength = 0;
        signalStateChanged(IDLE_STATE);
    }

    /**
     * Start recording
     *
     * @param outputfileformat the output file format
     * @param extension        the file extension
     */
    public void startRecording(int outputfileformat, String extension) {
        stopRecording();

        File sampleDir = AppFileManager.getRecordDir();
        try {
            mSampleFile = File.createTempFile(SAMPLE_PREFIX, extension, sampleDir);
        } catch (IOException e) {
            setError(SDCARD_ACCESS_ERROR);
            return;
        }
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(outputfileformat);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mSampleFile.getAbsolutePath());

        // Handle IOException
        try {
            mRecorder.prepare();
        } catch (IOException exception) {
            setError(INTERNAL_ERROR);
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            return;
        }
        // Handle RuntimeException if the recording couldn't start
        try {
            mRecorder.start();
        } catch (RuntimeException exception) {
            Context context = SystemManager.getContext();
            AudioManager audioMngr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int mode = audioMngr.getMode();
            boolean isInCall = (mode == AudioManager.MODE_IN_CALL) || (mode == AudioManager.MODE_IN_COMMUNICATION);
            if (isInCall) {
                setError(IN_CALL_RECORD_ERROR);
            } else {
                setError(INTERNAL_ERROR);
            }
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            return;
        }
        mSampleStart = System.currentTimeMillis();
        setState(RECORDING_STATE);
    }

    /**
     * Stop recording
     */
    public void stopRecording() {
        if (mRecorder == null) {
            return;
        }
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        mSampleLength = System.currentTimeMillis() - mSampleStart;
        setState(IDLE_STATE);
    }

    /**
     * Set state
     *
     * @param state
     */
    private void setState(int state) {
        if (state == mState) {
            return;
        }
        mState = state;
        signalStateChanged(mState);
    }

    /**
     * Callback the state on changed
     *
     * @param state
     */
    private void signalStateChanged(int state) {
        if (mOnStateChangedListener != null) {
            mOnStateChangedListener.onStateChanged(state);
        }
    }

    /**
     * Set error
     *
     * @param error the error
     */
    private void setError(int error) {
        if (mOnStateChangedListener != null) {
            mOnStateChangedListener.onError(error);
        }
    }

    /**
     * Interface definition for a callback to be invoked when recorder state is changed.
     */
    public interface OnStateChangedListener {

        /**
         * Called when the state of the recorder has changed.
         *
         * @param state The recorder whose state has changed.
         */
        public void onStateChanged(int state);

        /**
         * Called when the state of the recorder has error.
         *
         * @param error The recorder whose state has error.
         */
        public void onError(int error);
    }
}
