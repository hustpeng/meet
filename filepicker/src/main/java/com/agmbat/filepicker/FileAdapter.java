package com.agmbat.filepicker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * 文件列表
 */
public class FileAdapter extends ArrayAdapter<File> {

    private List<File> mDataList;

    public FileAdapter(Context context, List<File> fileDirs) {
        super(context, 0, fileDirs);
        mDataList = fileDirs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.filepicker_file_item, null);
        }
        File fileDirInfo = getItem(position);
        TextView nameView = (TextView) convertView.findViewById(R.id.name);
        nameView.setText(fileDirInfo.getName());

        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        if (fileDirInfo.isFile()) {
            imageView.setBackgroundResource(R.drawable.filepicker_file);
        } else {
            imageView.setBackgroundResource(R.drawable.filepicker_folder);
        }
        return convertView;
    }

    public void updateData(List<File> list) {
        clear();
        addAll(list);
        notifyDataSetChanged();
    }

    public List<File> getDataList() {
        return mDataList;
    }
}
