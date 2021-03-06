package com.agmbat.picker.file;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.agmbat.android.SysResources;
import com.agmbat.android.utils.StorageUtils;
import com.agmbat.picker.popup.ConfirmPopup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 文件目录选择器
 */
public class FilePicker extends ConfirmPopup<LinearLayout> implements AdapterView.OnItemClickListener {
    public static final int DIRECTORY = 0;
    public static final int FILE = 1;

    private String initPath;
    private FileAdapter adapter = new FileAdapter();
    private PathAdapter pathAdapter = new PathAdapter();
    private TextView emptyView;
    private OnFilePickListener onFilePickListener;
    private int mode;
    private CharSequence emptyHint = java.util.Locale.getDefault().getDisplayLanguage().contains("中文") ? "<空>" : "<Empty>";

    /**
     * @see #FILE
     * @see #DIRECTORY
     */
    public FilePicker(Context context, @Mode int mode) {
        super(context);
        setHalfScreen(true);
        try {
            this.initPath = StorageUtils.getDownloadPath();
        } catch (RuntimeException e) {
            this.initPath = StorageUtils.getInternalRootPath(context);
        }
        this.mode = mode;
        adapter.setOnlyListDir(mode == DIRECTORY);
        adapter.setShowHideDir(false);
        adapter.setShowHomeDir(false);
        adapter.setShowUpDir(false);
    }

    @Override
    @NonNull
    protected LinearLayout makeCenterView() {
        LinearLayout rootLayout = new LinearLayout(mContext);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.WHITE);

        ListView listView = new ListView(mContext);
        listView.setBackgroundColor(Color.WHITE);
        listView.setDivider(new ColorDrawable(0xFFDDDDDD));
        listView.setDividerHeight(1);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        rootLayout.addView(listView);

        emptyView = new TextView(mContext);
        LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        txtParams.gravity = Gravity.CENTER;
        emptyView.setLayoutParams(txtParams);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setVisibility(View.GONE);
        emptyView.setTextColor(Color.BLACK);
        rootLayout.addView(emptyView);

        return rootLayout;
    }

    @Nullable
    @Override
    protected View makeFooterView() {
        LinearLayout rootLayout = new LinearLayout(mContext);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.WHITE);

        View lineView = new View(mContext);
        lineView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 1));
        lineView.setBackgroundColor(0xFFDDDDDD);
        rootLayout.addView(lineView);

        HorizontalListView pathView = new HorizontalListView(mContext);
        int height = (int) SysResources.dipToPixel(30);
        pathView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, height));
        pathView.setAdapter(pathAdapter);
        pathView.setBackgroundColor(Color.WHITE);
        pathView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                refreshCurrentDirPath(pathAdapter.getItem(position));
            }
        });
        rootLayout.addView(pathView);

        return rootLayout;
    }

    public void setRootPath(String initPath) {
        this.initPath = initPath;
    }

    public void setAllowExtensions(String[] allowExtensions) {
        adapter.setAllowExtensions(allowExtensions);
    }

    public void setShowUpDir(boolean showUpDir) {
        adapter.setShowUpDir(showUpDir);
    }

    public void setShowHomeDir(boolean showHomeDir) {
        adapter.setShowHomeDir(showHomeDir);
    }

    public void setShowHideDir(boolean showHideDir) {
        adapter.setShowHideDir(showHideDir);
    }

    public void setFileIcon(Drawable fileIcon) {
        adapter.setFileIcon(fileIcon);
    }

    public void setFolderIcon(Drawable folderIcon) {
        adapter.setFolderIcon(folderIcon);
    }

    public void setHomeIcon(Drawable homeIcon) {
        adapter.setHomeIcon(homeIcon);
    }

    public void setUpIcon(Drawable upIcon) {
        adapter.setUpIcon(upIcon);
    }

    public void setArrowIcon(Drawable arrowIcon) {
        pathAdapter.setArrowIcon(arrowIcon);
    }

    public void setItemHeight(int itemHeight) {
        adapter.setItemHeight(itemHeight);
    }

    public void setEmptyHint(CharSequence emptyHint) {
        this.emptyHint = emptyHint;
    }

    @Override
    protected void setContentViewBefore() {
        boolean isPickFile = mode == FILE;
        setCancelVisible(!isPickFile);
        if (isPickFile) {
            setSubmitText(mContext.getString(android.R.string.cancel));
        } else {
            setSubmitText(mContext.getString(android.R.string.ok));
        }
    }

    @Override
    protected void setContentViewAfter(View contentView) {
        refreshCurrentDirPath(initPath);
    }

    @Override
    protected void onSubmit() {
        if (mode == FILE) {
        } else {
            String currentPath = adapter.getCurrentPath();
            if (onFilePickListener != null) {
                onFilePickListener.onFilePicked(currentPath);
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //adapter.recycleData();
        //pathAdapter.recycleData();
    }

    public FileAdapter getAdapter() {
        return adapter;
    }

    public PathAdapter getPathAdapter() {
        return pathAdapter;
    }

    public String getCurrentPath() {
        return adapter.getCurrentPath();
    }

    /**
     * 响应选择器的列表项点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        FileItem fileItem = adapter.getItem(position);
        if (fileItem.isDirectory()) {
            refreshCurrentDirPath(fileItem.getPath());
        } else {
            String clickPath = fileItem.getPath();
            if (mode == DIRECTORY) {
            } else {
                dismiss();
                if (onFilePickListener != null) {
                    onFilePickListener.onFilePicked(clickPath);
                }
            }
        }
    }

    private void refreshCurrentDirPath(String currentPath) {
        if (currentPath.equals("/")) {
            pathAdapter.updatePath("/");
        } else {
            pathAdapter.updatePath(currentPath);
        }
        adapter.loadData(currentPath);
        int adapterCount = adapter.getCount();
        if (adapter.isShowHomeDir()) {
            adapterCount--;
        }
        if (adapter.isShowUpDir()) {
            adapterCount--;
        }
        if (adapterCount < 1) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(emptyHint);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    public void setOnFilePickListener(OnFilePickListener listener) {
        this.onFilePickListener = listener;
    }

    @IntDef(value = {DIRECTORY, FILE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    public interface OnFilePickListener {

        void onFilePicked(String currentPath);

    }

}
