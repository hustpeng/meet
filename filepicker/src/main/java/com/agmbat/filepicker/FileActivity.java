package com.agmbat.filepicker;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 显示文件的Activity
 */
public class FileActivity extends ListActivity {

    /**
     * 文件路径显示
     */
    private TextView mPathView;

    private FileAdapter mAdapter;
    private ProgressDialog mProgressDialog;

    private File mCurrentFile;

    private static List<File> readChildrenFile(String path) {
        List<File> fileArrayList = new ArrayList<File>();
        File parentFile = new File(path);
        File[] files = parentFile.listFiles();
        if (null != files) {
            for (File file : files) {
                if (file != null) {
                    fileArrayList.add(file);
                }
            }
        }
        return fileArrayList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filepicker_activity_file);
        findViewById(R.id.title_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPathView = (TextView) findViewById(R.id.path);
        mAdapter = new FileAdapter(this, new ArrayList<File>());
        getListView().setAdapter(mAdapter);
        showDir(Environment.getExternalStorageDirectory());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File file = mAdapter.getItem(position);
        if (file.isFile()) {
            Intent data = new Intent();
            data.setData(Uri.fromFile(file));
            setResult(Activity.RESULT_OK, data);
            finish();
        } else {
            mProgressDialog = new ProgressDialog(FileActivity.this);
            mProgressDialog.setMessage("Loading...");
            showDir(file);
        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrentFile.equals(Environment.getExternalStorageDirectory())) {
            super.onBackPressed();
            return;
        }
        mCurrentFile = mCurrentFile.getParentFile();
        showDir(mCurrentFile);
    }

    private void showDir(final File file) {
        mCurrentFile = file;
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String display = file.getAbsolutePath().replace(rootPath, "");
        if (display.startsWith("/")) {
            display = display.substring(1);
        }
        if (TextUtils.isEmpty(display)) {
            mPathView.setVisibility(View.GONE);
        } else {
            mPathView.setVisibility(View.VISIBLE);
            mPathView.setText(display);
        }
        AsyncTaskUtils.executeAsyncTask(new AsyncTask<Void, Void, List<File>>() {
            @Override
            protected List<File> doInBackground(Void... voids) {
                final List<File> list = readChildrenFile(file.getAbsolutePath());
                return list;
            }

            @Override
            protected void onPostExecute(List<File> list) {
                super.onPostExecute(list);
                mAdapter.updateData(list);
                if (null != mProgressDialog) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        });
    }
}
