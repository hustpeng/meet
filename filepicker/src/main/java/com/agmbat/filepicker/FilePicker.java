package com.agmbat.filepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.agmbat.android.transit.ActionHelper;
import com.agmbat.android.transit.PendingAction;

import java.io.File;

/**
 * 选择文件
 */
public class FilePicker {

    /**
     * 获取文件
     *
     * @param context
     */
    public static void pickFile(Context context, OnPickFileListener listener) {
        ActionHelper.request(context, new PickFileAction(listener));
    }


    private static class PickFileAction implements PendingAction {

        private final OnPickFileListener mOnPickFileListener;

        public PickFileAction(OnPickFileListener callback) {
            mOnPickFileListener = callback;
        }

        @Override
        public void onActivityResult(int resultCode, Intent data) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    String path = uri.getPath();
                    if (!TextUtils.isEmpty(path)) {
                        File file = new File(path);
                        if (mOnPickFileListener != null) {
                            mOnPickFileListener.onPick(file);
                        }
                    }
                }
            }
        }

        @Override
        public Intent getActionIntent(Context context) {
            return new Intent(context, FileActivity.class);
        }
    }

}
