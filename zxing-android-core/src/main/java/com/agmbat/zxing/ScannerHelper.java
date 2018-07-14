package com.agmbat.zxing;

import android.content.Context;
import android.content.Intent;

import com.agmbat.android.transit.ActionHelper;
import com.agmbat.android.transit.PendingAction;

/**
 * 二维码扫描辅助
 */
public class ScannerHelper {

    /**
     * 打开扫描界面
     *
     * @param context
     * @param listener
     */
    public static void scan(Context context, OnScanListener listener) {
        ActionHelper.request(context, new ScanAction(listener));
    }

    private static class ScanAction implements PendingAction {

        private final OnScanListener mOnScanListener;

        public ScanAction(OnScanListener l) {
            mOnScanListener = l;
        }

        @Override
        public void onActivityResult(int resultCode, Intent data) {
            // 扫描成功并返回数据
            if (resultCode == ScannerActivity.QR_SUCCESS) {
                String scanResult = data.getStringExtra(ScannerActivity.QR_CONTENT);
                if (mOnScanListener != null) {
                    mOnScanListener.onScan(scanResult);
                }
            }
        }

        @Override
        public Intent getActionIntent(Context context) {
            return new Intent(context, ScannerActivity.class);
        }
    }
}
