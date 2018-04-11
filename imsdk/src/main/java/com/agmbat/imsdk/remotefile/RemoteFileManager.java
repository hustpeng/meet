package com.agmbat.imsdk.remotefile;

import android.graphics.Bitmap;

import com.agmbat.android.image.BitmapUtils;
import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.file.FileUtils;
import com.agmbat.imsdk.api.ApiResult;
import com.agmbat.imsdk.asmack.XMPPManager;

import java.io.File;

/**
 * 远程文件管理
 */
public class RemoteFileManager {

    public interface OnFileUploadListener {

        public void onUpload(ApiResult<String> apiResult);

    }

    /**
     * 上传头像文件
     *
     * @param bitmap
     * @param l
     */
    public static void uploadAvatarFile(final Bitmap bitmap, final OnFileUploadListener l) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, ApiResult<String>>() {
            @Override
            protected ApiResult<String> doInBackground(Void... voids) {
                return requestUploadAvatar(bitmap);
            }

            @Override
            protected void onPostExecute(ApiResult<String> result) {
                super.onPostExecute(result);
                if (l != null) {
                    l.onUpload(result);
                }
            }
        });
    }

    /**
     * 上传头像文件
     *
     * @param path
     * @param l
     */
    public static void uploadAvatarFile(final String path, final OnFileUploadListener l) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, ApiResult<String>>() {
            @Override
            protected ApiResult<String> doInBackground(Void... voids) {
                return requestUploadAvatar(path);
            }

            @Override
            protected void onPostExecute(ApiResult<String> result) {
                super.onPostExecute(result);
                if (l != null) {
                    l.onUpload(result);
                }
            }
        });
    }

    /**
     * 上传图像文件
     *
     * @return
     */
    private static ApiResult<String> requestUploadAvatar(Bitmap bitmap) {
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String ticket = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        String format = "jpg";
        byte[] imageData = BitmapUtils.compressToBytes(bitmap);
        ApiResult<String> result = FileApi.uploadAvatar(phone, ticket, format, imageData);
        if (result == null) {
            result = new ApiResult<String>();
            result.mResult = false;
            result.mErrorMsg = "网络请求失败";
        }
        return result;
    }

    /**
     * 上传图像文件
     *
     * @return
     */
    private static ApiResult<String> requestUploadAvatar(String path) {
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String ticket = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        String format = FileUtils.getExtension(path);
        byte[] imageData = FileUtils.readFileBytes(new File(path));
        ApiResult<String> result = FileApi.uploadAvatar(phone, ticket, format, imageData);
        if (result == null) {
            result = new ApiResult<String>();
            result.mResult = false;
            result.mErrorMsg = "网络请求失败";
        }
        return result;
    }
}
