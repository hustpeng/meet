package com.agmbat.crashreport;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.agmbat.android.AppResources;
import com.agmbat.android.utils.ApkUtils;
import com.agmbat.android.utils.AppUtils;
import com.agmbat.file.FileUtils;
import com.agmbat.wxshare.WXShare;

import java.io.File;

/**
 * This is the dialog Activity used by ACRA to get authorization from the user to send reports. Requires
 * android:theme="@android:style/Theme.Dialog" and android:launchMode="singleInstance" in your AndroidManifest to work
 * properly.
 */
public class CrashReportDialog extends Activity {

    private static final String PACKAGE_NAME = "Package name : ";
    private static final String VERSION_NAME = "Version name : ";
    private static final String VERSION_CODE = "Version code : ";

    private String mReportFileName = null;
    private String mReportEmail = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mReportFileName = intent.getStringExtra(CrashReporter.EXTRA_REPORT_FILE_NAME);
        mReportEmail = intent.getStringExtra(CrashReporter.EXTRA_REPORT_EMAIL);
        if (TextUtils.isEmpty(mReportFileName) || TextUtils.isEmpty(mReportEmail)) {
            finish();
        }
        requestWindowFeature(Window.FEATURE_LEFT_ICON);

        final LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(10, 10, 10, 10);
        root.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        final ScrollView scroll = new ScrollView(this);
        root.addView(scroll, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));

        final TextView text = new TextView(this);
        text.setText(getString(R.string.crash_report_dialog_text));
        scroll.addView(text, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        final LinearLayout buttons = new LinearLayout(this);
        buttons.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        buttons.setPadding(buttons.getPaddingLeft(), 10, buttons.getPaddingRight(), buttons.getPaddingBottom());

        addButton(buttons, getString(R.string.crash_report_btn_report), new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                // Start email to send report
                if (CrashReporter.getInstance().isUserWinXinSendFile()) {
                    WXShare.shareFileToFriend(CrashReportDialog.this, new File(mReportFileName));
                    finish();
                } else {
                    sendEmail();
                }
            }

        });

        addButton(buttons, getString(R.string.crash_report_btn_exit), new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                FileUtils.delete(mReportFileName);
                finish();
            }

        });

        root.addView(buttons, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setContentView(root);
        setTitle(getString(R.string.crash_report_dialog_title));
        int resLeftIcon = AppResources.getDrawableId("ic_launcher");
        if (resLeftIcon == 0) {
            resLeftIcon = android.R.drawable.ic_dialog_alert;
        }
        getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, resLeftIcon);
    }

    private void addButton(LinearLayout buttons, String text, View.OnClickListener l) {
        if (!TextUtils.isEmpty(text)) {
            final Button button = new Button(this);
            button.setText(text);
            button.setOnClickListener(l);
            buttons.addView(button, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
                    1.0f));
        }
    }

    /**
     * 发送邮件
     */
    private void sendEmail() {
        try {
            final Intent intent = new Intent(Intent.ACTION_SEND);
            final String[] tos = new String[]{mReportEmail};
            intent.putExtra(Intent.EXTRA_EMAIL, tos);
            String subject = getString(R.string.crash_report_email_subject, ApkUtils.getAppName(), "v"
                    + ApkUtils.getVersionName());
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, getEmailText());
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + mReportFileName));
            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getEmailText() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getAppInfo());
        builder.append("\n\n");
        final String crashInfo = FileUtils.readFileText(new File(mReportFileName));
        builder.append(crashInfo);
        builder.append("\n\n");
        builder.append(getString(R.string.crash_report_email_text));
        builder.append("\n\n");
        return builder.toString();
    }

    private String getAppInfo() {
        PackageInfo pi = ApkUtils.getPackageInfo(getPackageName());
        final StringBuilder sb = new StringBuilder();
        sb.append(PACKAGE_NAME).append(getPackageName()).append("\n");
        if (pi != null) {
            sb.append(VERSION_NAME).append(pi.versionName).append("\n");
            sb.append(VERSION_CODE).append(pi.versionCode).append("\n");
        }
        return sb.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.killMyProcess();
    }
}
