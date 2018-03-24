package com.agmbat.meetyou.data.body;

import android.text.TextUtils;

import com.agmbat.android.media.AudioPlayer;
import com.agmbat.app.AppFileManager;
import com.agmbat.http.HttpUtils;

import java.io.File;

public class AudioBody extends Body {

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

    @Override
    public String toXml() {
        StringBuilder builder = new StringBuilder();
        builder.append("<wrap>");
        builder.append("<type>").append(getBodyType()).append("</type>");
        builder.append("<file_url>").append(mFileUrl).append("</file_url>");
        builder.append("<duration>").append(mDuration).append("</duration>");
        builder.append("</wrap>");
        return builder.toString();
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

    @Override
    public BodyType getBodyType() {
        return BodyType.AUDIO;
    }
}
