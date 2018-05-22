package com.agmbat.meetyou.chat;

import android.text.TextUtils;

import com.agmbat.android.media.AudioPlayer;
import com.agmbat.app.AppFileManager;
import com.agmbat.http.HttpUtils;

import java.io.File;

public class AudioBody {

    private final String mFileUrl;
    private final long mDuration;

    public AudioBody(String fileUrl, long duration) {
        mFileUrl = fileUrl;
        mDuration = duration;
    }

    public String getFileUrl() {
        return mFileUrl;
    }

    public long getDuration() {
        return mDuration;
    }


    public boolean isPlaying() {
        if (AudioPlayer.getDefault().isPlaying()) {
            String url = getAudioFile().getAbsolutePath();
            return TextUtils.equals(AudioPlayer.getDefault().getDataSource(), url);
        }
        return false;
    }

    public File getAudioFile() {
        return new File(AppFileManager.getRecordDir(), HttpUtils.getFileNameFromUrl(mFileUrl));
    }

}