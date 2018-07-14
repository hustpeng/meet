package com.agmbat.android.transit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

/**
 * 中转的Activity, 并且还需要处理onActivityResult
 */
public class TransitActivity extends Activity {

    /**
     * 请求码的key值
     */
    private static final String KEY_REQUEST_CODE = "requestCode";

    /**
     * 启动中转的Activity
     *
     * @param context
     * @param requestCode
     */
    public static void request(Context context, int requestCode) {
        Intent intent = new Intent(context, TransitActivity.class);
        intent.putExtra(KEY_REQUEST_CODE, requestCode);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            // 去掉切换动画关键
            activity.overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PendingAction action = ActionHelper.getPendingAction(requestCode);
        action.onActivityResult(resultCode, data);
        ActionHelper.clearAction();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        // 去掉切换动画关键
        overridePendingTransition(0, 0);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void handleIntent(Intent intent) {
        int requestCode = intent.getIntExtra(KEY_REQUEST_CODE, -1);
        PendingAction action = ActionHelper.getPendingAction(requestCode);
        Intent actionIntent = action.getActionIntent(this);
        // 执行时启动对应的Activity
        startActivityForResult(actionIntent, requestCode);
    }
}
