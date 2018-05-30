package com.agmbat.imsdk.remotefile;

import com.agmbat.android.image.ImageUtils;
import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.file.FileUtils;
import com.agmbat.imsdk.asmack.XMPPManager;
import com.agmbat.imsdk.mgr.UserFileManager;
import com.agmbat.text.StringUtils;

import java.io.File;

/**
 * 远程文件管理
 */
public class RemoteFileManager {

    /**
     * 上传图片文件, 如果文件过大, 需要将文件进行裁剪
     *
     * @param file
     * @param l
     */
    public static void uploadImageFile(final File file, final OnFileUploadListener l) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, FileApiResult>() {
            @Override
            protected FileApiResult doInBackground(Void... voids) {
                // 先对图片进行裁剪
                String name = file.getName();
                File outFile = new File(UserFileManager.getCurImageDir(), name);
                ImageUtils.resizeImage(file.getAbsolutePath(), outFile.getAbsolutePath(), 1080, 1920);
                return requestUploadTempFile(outFile.getAbsoluteFile());
            }

            @Override
            protected void onPostExecute(FileApiResult result) {
                super.onPostExecute(result);
                if (l != null) {
                    l.onUpload(result);
                }
            }
        });
    }


    /**
     * 上传音频文件
     *
     * @param file
     * @param l
     */
    public static void uploadTempFile(final File file, final OnFileUploadListener l) {
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, FileApiResult>() {
            @Override
            protected FileApiResult doInBackground(Void... voids) {
                return requestUploadTempFile(file);
            }

            @Override
            protected void onPostExecute(FileApiResult result) {
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
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, FileApiResult>() {
            @Override
            protected FileApiResult doInBackground(Void... voids) {
                // 先对图片进行裁剪
                File file = new File(path);
                String name = file.getName();
                File outFile = new File(UserFileManager.getCurImageDir(), name);
                // 将头像大小调整为400x400
                ImageUtils.resizeImage(file.getAbsolutePath(), outFile.getAbsolutePath(), 400, 400);
                return requestUploadAvatar(outFile.getAbsolutePath());
            }

            @Override
            protected void onPostExecute(FileApiResult result) {
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
    private static FileApiResult requestUploadAvatar(String path) {
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String ticket = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        String format = FileUtils.getExtension(path);
        FileApiResult result = FileApi.uploadAvatar(phone, ticket, format, new File(path));
        if (result == null) {
            result = new FileApiResult();
            result.mResult = false;
            result.mErrorMsg = "网络请求失败";
        }
        if (result.mResult) {
            String imageUrl = result.url;
            if (!StringUtils.isEmpty(imageUrl)) {
                result.mErrorMsg = "上传头像成功";
            }
        }
        return result;
    }

    /**
     * 上传临时文件
     *
     * @param file
     * @return
     */
    private static FileApiResult requestUploadTempFile(File file) {
        String phone = XMPPManager.getInstance().getConnectionUserName();
        String ticket = XMPPManager.getInstance().getTokenManager().getTokenRetry();
        String format = FileUtils.getExtension(file.getPath());
        FileApiResult result = FileApi.uploadTempFile(phone, ticket, format, file);
        if (result == null) {
            result = new FileApiResult();
            result.mResult = false;
            result.mErrorMsg = "网络请求失败";
        }
        return result;
    }

}
