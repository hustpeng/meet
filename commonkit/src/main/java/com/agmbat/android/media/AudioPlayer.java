package com.agmbat.android.media;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayer implements OnCompletionListener, OnErrorListener {

    /**
     * the msg to update playing state
     */
    private static final int MSG_UPDATE = 1;

    /**
     * the delay time
     */
    private static final long DELAY_TIME = 500;

    /**
     * the default player
     */
    private static DefaultPlayer sDefault;

    /**
     * the media player
     */
    private MediaPlayer mPlayer;

    /**
     * current playing state
     */
    private boolean mIsPlaying;

    /**
     * the path of the audio file
     */
    private String mPath;

    /**
     * the handler to update playing state
     */
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case MSG_UPDATE:
                    updatePlaying();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * the listener of the play state is changed
     */
    private OnPlayListener mOnPlayListener;

    /**
     * Get the default player
     *
     * @return the default player
     */
    public static DefaultPlayer getDefault() {
        if (sDefault == null) {
            sDefault = new DefaultPlayer();
        }
        return sDefault;
    }

    /**
     * Create an AudioPlayer
     */
    public AudioPlayer() {
    }

    /**
     * Register a callback to be invoked when this player state is changed
     *
     * @param l The callback that will run
     */
    public void setOnPlayListener(OnPlayListener l) {
        mOnPlayListener = l;
    }

    /**
     * Sets the data source (file-path or http/rtsp URL) to use.
     *
     * @param path the path of the file, or the http/rtsp URL of the stream you want to play
     */
    public void setDataSource(String path) {
        if (!TextUtils.equals(mPath, path)) {
            stop();
            mPath = path;
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(path);
                mPlayer.setOnCompletionListener(this);
                mPlayer.setOnErrorListener(this);
                mPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Starts or pause playback. If playback had previously been paused, playback will continue from where it was
     * paused. If playback had been started, playback will paused.
     */
    public void playOrPause(String url) {
        setDataSource(url);
        if (mIsPlaying) {
            pause();
        } else {
            play();
        }
    }

    /**
     * Starts or resumes playback. If playback had previously been paused, playback will continue from where it was
     * paused. If playback had been stopped, or never started before, playback will start at the beginning.
     */
    public void play() {
        if (!mIsPlaying) {
            mPlayer.start();
            mIsPlaying = true;
            mHandler.sendEmptyMessage(MSG_UPDATE);
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlay();
            }
        }
    }

    /**
     * Pauses playback. Call start() to resume.
     */
    public void pause() {
        if (mIsPlaying) {
            mPlayer.pause();
            mIsPlaying = false;
            mHandler.removeMessages(MSG_UPDATE);
            if (mOnPlayListener != null) {
                mOnPlayListener.onPause();
            }
        }
    }

    /**
     * Stops playback.
     */
    public void stop() {
        mIsPlaying = false;
        release();
    }

    /**
     * Releases resources associated with this MediaPlayer object.
     */
    public void release() {
        if (mPlayer != null) {
            try {
                mPlayer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * Get the playing state
     *
     * @return the playing
     */
    public boolean isPlaying() {
        return mIsPlaying;
    }

    /**
     * Get the playing state
     *
     * @param url the playing url
     * @return the playing
     */
    public boolean isPlaying(String url) {
        if (isPlaying()) {
            return TextUtils.equals(getDataSource(), url);
        }
        return false;
    }

    /**
     * Called to indicate an error.
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mIsPlaying = false;
        mHandler.removeMessages(MSG_UPDATE);
        if (mOnPlayListener != null) {
            mOnPlayListener.onPlaying(0, getDuration());
        }
        return false;
    }

    /**
     * Called when the end of a media source is reached during playback.
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        mIsPlaying = false;
        mHandler.removeMessages(MSG_UPDATE);
        if (mOnPlayListener != null) {
            mOnPlayListener.onCompletion(mp);
        }
    }

    /**
     * Get the current path of playback
     *
     * @return the current path of playback
     */
    public String getDataSource() {
        return mPath;
    }

    /**
     * Gets the duration of the file.
     *
     * @return the duration in milliseconds, if no duration is available (for example, if streaming live content), -1 is
     * returned.
     */
    public int getDuration() {
        return mPlayer.getDuration();
    }

    /**
     * Gets the current playback position.
     *
     * @return the current position in milliseconds
     */
    public int getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    /**
     * Update the status of playing
     */
    private void updatePlaying() {
        if (mPlayer != null) {
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlaying(getCurrentPosition(), getDuration());
                if (mIsPlaying) {
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE, DELAY_TIME);
                    return;
                }
            }
        }
        mHandler.removeMessages(MSG_UPDATE);
    }

    /**
     * Default Player
     */
    public static class DefaultPlayer extends AudioPlayer {

        /**
         * the list of playing listeners
         */
        private List<OnPlayListener> mListeners = new ArrayList<OnPlayListener>();

        /**
         * Create a default player
         */
        public DefaultPlayer() {
            setOnPlayListener(new DefaultListener(mListeners));
        }

        /**
         * Add a listener
         *
         * @param l the listener
         */
        public void addListener(OnPlayListener l) {
            if (!mListeners.contains(l)) {
                mListeners.add(l);
            }
        }

        /**
         * Remove a listener
         *
         * @param l the listener
         */
        public void removeListener(OnPlayListener l) {
            mListeners.remove(l);
        }

        /**
         * Default listener
         */
        private static class DefaultListener implements OnPlayListener {
            /**
             * the list of playing listeners
             */
            private final List<OnPlayListener> mListeners;

            /**
             * Create a default listener
             *
             * @param listeners
             */
            public DefaultListener(List<OnPlayListener> listeners) {
                mListeners = listeners;
            }

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                for (OnPlayListener l : mListeners) {
                    l.onError(mp, what, extra);
                }
                return false;
            }

            @Override
            public void onCompletion(MediaPlayer mp) {
                for (OnPlayListener l : mListeners) {
                    l.onCompletion(mp);
                }
            }

            @Override
            public void onPlaying(int position, int duration) {
                for (OnPlayListener l : mListeners) {
                    l.onPlaying(position, duration);
                }
            }

            @Override
            public void onPlay() {
                for (OnPlayListener l : mListeners) {
                    l.onPlay();
                }
            }

            @Override
            public void onPause() {
                for (OnPlayListener l : mListeners) {
                    l.onPause();
                }
            }
        }
    }

    /**
     * Interface definition for a callback to be invoked when state of the player has changed.
     */
    public interface OnPlayListener extends OnCompletionListener, OnErrorListener {
        /**
         * Called when player start
         */
        public void onPlay();

        /**
         * Called when player pause
         */
        public void onPause();

        /**
         * Called to update status playing
         *
         * @param position the position
         * @param duration the duration
         */
        public void onPlaying(int position, int duration);
    }
}
